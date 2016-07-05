package com.caitu99.service.transaction.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.WithdrawException;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.WithdrawService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserBankCard;
import com.caitu99.service.user.service.UserBankCardService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.unionpay.UnionOpens;


@Service
public class WithdrawServiceImpl implements WithdrawService {
	
	private Logger _log = LoggerFactory.getLogger(WithdrawServiceImpl.class);

	//每天限制的提现次数
	private final String counts = Configuration.getProperty("withdraw.count", null);
	//最小提现金额
	private final String amounts = Configuration.getProperty("withdraw.amount", null);
	
	private final String MAX_AMOUNT = Configuration.getProperty("withdraw.max.amount", "2000");

	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserBankCardService userBankCardService;
	
	@Autowired
	private TransactionRecordMapper transactionRecordMapper;
	
	@Autowired
	private AccountDetailMapper accountDetailMapper;
	
	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private AppConfig appConfig;

	@SuppressWarnings("rawtypes")
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String withdraw(Long userId, Long userBankCardId, Long amount,
			String payPass) throws WithdrawException {
		String uuid =UUID.randomUUID().toString();
		_log.info("{}:用户:{},提现:{}财币到银行卡:{},开始……",uuid,userId,amount,userBankCardId);

		ApiResult<String> result = new ApiResult<String>();
		//判断是否可提现
		if(amount < Integer.parseInt(amounts)){
			_log.info("{}:提现财币",amount);
			return result.toJSONString(4102, "最小提现金额为"+Integer.parseInt(amounts)/100+"元。");
		}

		int count = transactionRecordMapper.isWithdraw(userId);
		if(count >= Integer.parseInt(counts)){
			_log.info("用户{}:今天提现次数:{}",userId,count);
			return result.toJSONString(4103, "一天只能提现一次，明天再来试试吧。");
		}
		Long sum = transactionRecordMapper.sameDayWithrawSUM(userId);
		if(amount.longValue() > Long.valueOf(MAX_AMOUNT).longValue()){
			return result.toJSONString(4103, "单日最多提现"+Long.valueOf(MAX_AMOUNT)/100+"元");
		}
		if(null != sum && sum.longValue()+amount.longValue() >= Long.valueOf(MAX_AMOUNT).longValue()){
			_log.info("用户{}:今天提现总额{}",userId,sum);
			return result.toJSONString(4103, "单日最多提现"+Long.valueOf(MAX_AMOUNT)/100+"元");
		}
		//用户
		User user = userService.getById(userId);
		if(null == user){
			_log.info("{}:用户不存在",uuid);
			return result.toJSONString(4104, "用户不存在");
		}
		if(StringUtils.isBlank(user.getPaypass())){
			_log.info("{}:未设置支付密码",uuid);
			return result.toJSONString(4105, "未设置支付密码");
		}
		
		try {
			payPass = AESCryptoUtil.encrypt(payPass);
		} catch (CryptoException e1) {
			return result.toJSONString(4106, "密码加密失败");
		}
		
		if(!payPass.equals(user.getPaypass())){
			_log.info("{}:密码错误",uuid);
			return result.toJSONString(4106, "密码错误");
		}
		//账户
		Account account = accountService.selectByUserId(userId);
		
		if(null == account){
			_log.info("{}:账户不存在",uuid);
			return result.toJSONString(4107, "账户不存在");
		}
		if(account.getAvailableIntegral().compareTo(amount) < 0){
			_log.info("{}:账户余额不足",uuid);
			return result.toJSONString(4108, "账户余额不足");
		}
		//银行卡
		UserBankCard userBankCard = userBankCardService.selectById(userBankCardId);
		if(null == userBankCard){
			_log.info("{}:用户银行卡记录不存在",uuid);
			return result.toJSONString(4109, "用户银行卡记录不存在");
		}
		
		String transactionNumber = XStringUtil.createWithdrawSerialNo("TD", userId);
		Date date = new Date();
		Integer status = 0;
		
		//锁定帐号
		accountService.selectByPrimaryKeyForUpdate(account.getId());
		//扣减财币
		Long total = CalculateUtils.getDifference(account.getTotalIntegral(),amount);
		Long available = CalculateUtils.getDifference(account.getAvailableIntegral(),amount);
		try {
			Account editAccount = new Account();
			editAccount.setId(account.getId());
			editAccount.setTotalIntegral(total);
			editAccount.setAvailableIntegral(available);
			editAccount.setGmtModify(date);
			accountMapper.updateByPrimaryKeySelective(editAccount);
		} catch (Exception e) {
			_log.error("{}:扣减财币失败",uuid);
			throw new WithdrawException(4111,"扣减财币失败");
		}
		
		//调用外部提现接口
		UnionOpens unionOpens = new UnionOpens();
		Map map = null;
		try {
			map = unionOpens.paymentNoBind(amount, userBankCard.getCardNo(), transactionNumber, 
					userBankCard.getAccName(), "", userBankCard.getAccId());
		} catch (Exception e) {
			_log.error("{}:调用提现接口发送异常:{}",uuid,e);
			throw new WithdrawException(4110, "提现服务暂时无法使用，请稍后再试……");
		}
		if(null == map || !"0000".equals(map.get("retCode"))){
			_log.error("{}:提现请求失败:{}",uuid,map);
			throw new WithdrawException(4110, "提现服务暂时无法使用，请稍后再试……");
		}
		
		if("0".equals(map.get("orderStatus")) || "1".equals(map.get("orderStatus"))){
			//处理中
			status = 1;
			_log.info("{}:提现请求处理中",uuid);
		} else if("2".equals(map.get("orderStatus"))){
			//处理成功
			status = 2;
			_log.info("{}:提现请求出来成功",uuid);
		}else if("3".equals(map.get("orderStatus"))){
			//处理失败
			_log.info("{}:提现请求处理失败",uuid);
			throw new WithdrawException(4112,"提现处理失败");
		} else{
			//未知状态
			_log.info("{}:提现请求返回未知参数:{}",uuid,map);
			throw new WithdrawException(4113,"提现返回未知结果");
		}
		
		//生成交易记录
		TransactionRecord transactionRecord =  new TransactionRecord();;
		try {
			String info = userBankCard.getBankName()+"("+userBankCard.getCardNo().substring(userBankCard.getCardNo().length()-4)+")";
			transactionRecord.setTransactionNumber(transactionNumber);
			transactionRecord.setOrderNo("");
			transactionRecord.setUserId(userId);
			transactionRecord.setInfo(info);
			transactionRecord.setType(4);
			transactionRecord.setSource(4);
			transactionRecord.setPayType(1);
			transactionRecord.setStatus(status);
			transactionRecord.setTotal(amount);
			transactionRecord.setChannel(5);
			transactionRecord.setComment("提现");
			transactionRecord.setCreateTime(date);
			transactionRecord.setUpdateTime(date);
			transactionRecordMapper.insertSelective(transactionRecord);
			
			//账户详细记录
			AccountDetail accountDetail = new AccountDetail();
			accountDetail.setUserId(userId);
			accountDetail.setRecordId(transactionRecord.getId());
			accountDetail.setIntegralChange(amount);
			accountDetail.setType(2);
			accountDetail.setMemo("提现");
			accountDetail.setGmtCreate(date);
			accountDetail.setGmtModify(date);
			accountDetailMapper.insertSelective(accountDetail);

			userBankCardService.updateByUseIdAndCardNo(userBankCard);
		
			//处理中JOB,定时查看订单状态 TODO
			if(1 == status.intValue()){
				_log.info("{}:启动定时任务定时查看提现请求状态",uuid);
				Map<String, Object> jobmap = new HashMap<>();
				jobmap.put("id", transactionRecord.getId());
				jobmap.put("jobType","WITHDRAW_QUERY_JOB"); 
				kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
			}
		} catch (Exception e) {
			_log.info("{}:生成提现记录发生异常：{}",uuid,e);
			throw new WithdrawException(4114,"生成记录失败");
		}
		String data = new StringBuffer("您已成功提现")
						.append(amount/100)
						.append("元至")
						.append(userBankCard.getBankName())
						.append("(尾号")
						.append(userBankCard.getCardNo().substring(userBankCard.getCardNo().length()-4))
						.append("),预计T+1个工作日到账")
						.toString();
		return result.toJSONString(0, "提现已受理",data);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String queryWithdraw(Long id) throws WithdrawException {
		String uuid = UUID.randomUUID().toString();
		_log.info("{}:查询交易记录id:{}的提现状态",uuid,id);
		ApiResult<String> result = new ApiResult<String>();
		TransactionRecord record = transactionRecordMapper.selectByPrimaryKey(id);
		Date date = new Date();
		if(null == record){
			_log.info("{}:没有找到id:{}的提现记录",uuid,id);
			return result.toJSONString(0, "没有该提现记录");
		}
		if(1 != record.getStatus().intValue()){
			_log.info("{}:交易记录状态为:{},状态不为1,不用查询",uuid,record.getStatus());
			return result.toJSONString(0, "该记录状态不对");
		}
		UnionOpens unionOpens = new UnionOpens();
		Map map = null;
		try {
			map = unionOpens.getOrder(record.getTransactionNumber());
		} catch (Exception e) {
			_log.info("{}:查询提现订单状态发生异常：{}",uuid,e);
			return result.toJSONString(-1, "查询提现失败");
		}
		if(null == map || !"0000".equals(map.get("retCode"))){
			_log.info("{}:查询提现订单失败：{}",uuid,map);
			return result.toJSONString(-1, "查询提现失败");
		}
		JSONObject json = JSONObject.parseObject(map.get("orderDetail").toString());
		if(null == json){
			_log.info("{}:查询提现订单失败：{}",uuid,map);
			return result.toJSONString(-1, "查询提现失败");
		}
		Integer status = null;
		if("0".equals(json.get("orderStatus")) || "1".equals(json.get("orderStatus"))){
			//处理中
			_log.info("{}:提现订单还在处理中,等下次再来查询……",uuid);
			return result.toJSONString(-1, "提现处理中");
		} else if("2".equals(json.get("orderStatus"))){
			//处理成功，生成成功交易记录
			status = 2;
			_log.info("{}:提现订单处理成功",uuid);
		}else if("3".equals(json.get("orderStatus"))){
			//处理失败，返还财币
			_log.info("{}:提现订单处理失败，将返还财币",uuid);
			status = -1;
			//返还财币
			Account account = accountService.selectByUserId(record.getUserId());
			Long total = CalculateUtils.getAdd(account.getTotalIntegral(),record.getTotal());
			Long available = CalculateUtils.getAdd(account.getAvailableIntegral(),record.getTotal());
			try {
				Account editAccount = new Account();
				editAccount.setId(account.getId());
				editAccount.setTotalIntegral(total);
				editAccount.setAvailableIntegral(available);
				editAccount.setGmtModify(date);
				accountMapper.updateByPrimaryKeySelective(editAccount);
				//账户详细记录
				AccountDetail accountDetail = new AccountDetail();
				accountDetail.setUserId(record.getUserId());
				accountDetail.setRecordId(record.getId());
				accountDetail.setIntegralChange(record.getTotal());
				accountDetail.setType(1);
				accountDetail.setMemo("提现失败返还财币");
				accountDetail.setGmtCreate(date);
				accountDetail.setGmtModify(date);
				accountDetailMapper.insertSelective(accountDetail);
			} catch (Exception e) {
				_log.error("{}:返还提现财币发生异常:{}",uuid,e);
				throw new WithdrawException(-1,"返还财币失败");
			}
		} else{
			_log.error("{}:提现订单查询返回未知参数",uuid);
			//未知状态
			throw new WithdrawException(-2,"提现返回未知结果");
		}
		
		TransactionRecord transactionRecord = new TransactionRecord();
		transactionRecord.setTransactionNumber(record.getTransactionNumber());
		transactionRecord.setOrderNo("");
		transactionRecord.setUserId(record.getUserId());
		transactionRecord.setInfo(record.getInfo());
		transactionRecord.setType(4);
		transactionRecord.setSource(4);
		transactionRecord.setPayType(1);
		transactionRecord.setStatus(status);
		transactionRecord.setTotal(record.getTotal());
		transactionRecord.setChannel(5);
		transactionRecord.setComment("提现");
		transactionRecord.setCreateTime(date);
		transactionRecord.setUpdateTime(date);
		transactionRecordMapper.insertSelective(transactionRecord);
		_log.info("{}:提现订单查询完成");
		return result.toJSONString(0, "提现到账处理成功");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String isWithdraw(Long userId) {
		ApiResult<String> result = new ApiResult<String>();
		int count = transactionRecordMapper.isWithdraw(userId);
		if(count >= Integer.parseInt(counts)){
			_log.info("{}:今天提现次数",count);
			return result.toJSONString(4103, "一天只能提现一次，明天再来试试吧。");
		}
		return result.toJSONString(0, "可以提现。");
	}


}
