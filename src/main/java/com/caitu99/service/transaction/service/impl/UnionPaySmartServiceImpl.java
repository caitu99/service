package com.caitu99.service.transaction.service.impl;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.exception.AccountException;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.transaction.constants.AccountDetailConstants;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.dao.SxfRecordMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.SxfRecord;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.transaction.service.UnionPaySmartService;
import com.caitu99.service.transaction.sxf.Sxf;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 银联智慧代充代付实现类
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UnionPaySmartServiceImpl 
 * @author Hongbo Peng
 * @date 2016年5月27日 下午3:42:53 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Service
public class UnionPaySmartServiceImpl implements UnionPaySmartService {

	private final static Logger logger = LoggerFactory
			.getLogger(UnionPaySmartServiceImpl.class);
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private TransactionRecordService transactionRecordService;
	@Autowired
	private AccountDetailService accountDetailService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private UserService userService;
	@Autowired
	private Sxf sxf;
	@Autowired
	private SxfRecordMapper sxfRecordMapper;
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = {"AccountException"})
	public AccountResult rechargeDirect(Long userId, Long unionId, Long point,
			String unionNo, String tNo, Long clientId, String userAccName,
			String userAccNo,Integer model) throws AccountException{
		AccountResult accountResult = new AccountResult();
		accountResult.setSuccess(false);
		
		logger.info("【银联智慧{}】发起代充代付请求,userId:{},unionId:{},point:{},tNo:{},clientId:{},userAccName:{},userAccNo:{},model:{}",
				unionNo,userId,unionId,point,tNo,clientId,userAccName,userAccNo,model);
		// 用户账户验证
		Account userAccount = accountService.selectByUserId(userId);
		if (null == userAccount) {
			accountResult.setCode(3001);
			accountResult.setResult("用户账户不存在");
			return accountResult;
		}
		logger.info("【银联智慧{}】验证用户账户通过",unionNo);
		// 企业账户验证
		Account unionAccount = accountService.selectByUserId(unionId);
		if (null == unionAccount) {
			accountResult.setCode(3003);
			accountResult.setResult("银联账户不存在");
			return accountResult;
		}
		logger.info("【银联智慧{}】验证企业账户通过",unionNo);
		if (unionAccount.getAvailableIntegral().compareTo(point) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("账户财币不足");
			return accountResult;
		}
		logger.info("【银联智慧{}】验证企业账户余额通过",unionNo);
		
		TransactionRecord record = transactionRecordService.getTransactionRecord(unionId, unionNo);
		if(null != record){
			logger.info("【银联智慧{}】订单号重复",unionNo);
			accountResult.setCode(3004);
			accountResult.setResult("订单号重复");
			return accountResult;
		}
		Date date = new Date();
		//业务
		try {
			//计算手续费
			String feeRate = "";
			if(30 == model.intValue()){
				feeRate = Configuration.getProperty("unionpaysmart.fee.30", null);
			}else{
				feeRate = Configuration.getProperty("unionpaysmart.fee.31", null);
			}
			if(StringUtils.isBlank(feeRate)){
				throw new Exception("未配置手续费");
			}
			Long fee = CalculateUtils.getDiscountPriceCeil(point, Integer.valueOf(feeRate));
			logger.info("【银联智慧{}】计算手续费为{},手续费率{}%",unionNo,fee,feeRate);
			//用户累计交易记录,企业交易记录加上手续费
			this.saveTransactionRecord(userId, unionId, point, unionNo, tNo, clientId, date, model, fee, feeRate);
			logger.info("【银联智慧{}】用户交易记录和企业交易记录完成",unionNo);
			//用户账户明细记录,银联账户明细记录
			this.saveAccountDetail(userId, unionNo);
			logger.info("【银联智慧{}】用户账户明细记录和企业账户明细记录完成",unionNo);
			//扣除银联金额,充值到用户账户
			this.updateAccountRecord(userId, unionId, point, fee,model);
			logger.info("【银联智慧{}】扣除企业金额，充值用户完成",unionNo);
			//如果是代付，使用随心付给用户银行卡提现
			if(31 == model.intValue()){
				String flag = sxf.daifu(userAccName, userAccNo, point, unionNo, tNo);
				logger.info("【银联智慧{}】使用随心付给用户提现返回结果：{}",unionNo,flag);
				//处理支付结果
				if(JsonResult.checkResult(flag, 0)){
					User user = userService.getById(userId);
					//生成随心付记录，
					SxfRecord sxfRecord = new SxfRecord();
					sxfRecord.setAccName(userAccName);
					sxfRecord.setAccNo(userAccNo);
					sxfRecord.setMobile(user.getMobile());
					sxfRecord.setAmount(point);
					sxfRecord.setChannel(1);
					sxfRecord.setCreateTime(date);
					sxfRecord.setStatus(2);
					sxfRecord.setTransactionNumber(tNo);
					sxfRecord.setUpdateTime(date);
					sxfRecordMapper.insertSelective(sxfRecord);
					//启动查询JOB 
					Map<String,String> map = new HashMap<String, String>();
					map.put("transactionNumber", tNo);
					map.put("jobType", "SXF_QUERY_PAY_STATUS_JOB");//随心付查询交易状态任务调度
					kafkaProducer.sendMessage(JSON.toJSONString(map),appConfig.jobTopic);
				}else{
					logger.error("【银联智慧{}】随心付返回值：",unionNo,JsonResult.getResult(flag, "msg"));
					//修改交易记录状态
					throw new Exception( JsonResult.getResult(flag, "msg"));
				}
				logger.info("【银联智慧{}】代付交易等待提现结果查询。",unionNo);
			} else {
				logger.info("【银联智慧{}】代充交易完成。",unionNo);
				accountResult.setCode(3101);
				accountResult.setSuccess(true);
				accountResult.setResult("充值成功");
				return accountResult;
			}
		} catch (Exception e) {
			logger.error("银联智慧：",e);
			throw new AccountException(3102, e.getMessage());
		}
		
		// 数据返回
		accountResult.setCode(0);
		accountResult.setSuccess(true);
		accountResult.setResult("处理中");
		return accountResult;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String queryAndHandle(String transactionNumber) {
		logger.info("【随心付结果查询{}】开始",transactionNumber);
		try {
			TransactionRecord unionTransactionRecord = transactionRecordService.selectByTransactionNumberAndType(transactionNumber, 31);
			if(null == unionTransactionRecord){
				logger.info("【随心付结果查询{}】交易记录不存在",transactionNumber);
				return "SUCCESS";
			}
			if(1 != unionTransactionRecord.getStatus().intValue()){
				logger.info("【随心付结果查询{}】该交易记录不是处理中,不执行查询",transactionNumber);
				return "SUCCESS";
			}
			String orderNo = unionTransactionRecord.getOrderNo();
			String flag = sxf.query(orderNo);
			if(StringUtils.isBlank(flag)){
				logger.info("【随心付结果查询{}】返回交易为空",transactionNumber);
				return "SUCCESS";
			}
			String[] orders = flag.split(",");
			if(orders.length != 6){
				logger.error("【随心付结果查询{}】返回订单信息有误：{}",transactionNumber,flag);
			}
			logger.info("【随心付结果查询{}】查询交易结果为:{}",transactionNumber,flag);
			Date date = new Date();
			SxfRecord sxfRecord = sxfRecordMapper.selectByTransactionNumber(unionTransactionRecord.getTransactionNumber());
			if(appConfig.isDevMode || appConfig.isTestMode){
				//开发环境随机一个状态
				Random random = new Random();
				int i = random.nextInt(100)%4;
				orders[1] = i+"";
				orders[4] = "姓名和卡号不符";
			}
			TransactionRecord userTransactionRecord = transactionRecordService.selectByTransactionNumberAndType(transactionNumber, 5);
			if("0".equals(orders[1])){
				logger.info("【随心付结果查询{}】状态为失败",transactionNumber);
				String memo = orders[4];
				//提现失败1.退回银联财币，2.修改交易记录为失败已处理3.提现记录为失败
				this.unionFailedHandle(userTransactionRecord.getUserId(),unionTransactionRecord.getUserId(), userTransactionRecord.getTotal(),unionTransactionRecord.getTotal(), 
						userTransactionRecord.getId(),unionTransactionRecord.getId(), date,memo,sxfRecord.getId());
			}else if("1".equals(orders[1])){
				logger.info("【随心付结果查询{}】状态为成功",transactionNumber);
				//成功1.修改交易记录为成功，提现记录为成功
				logger.info("【随心付结果查询{}】修改用户交易记录成功：{}",transactionNumber,userTransactionRecord.getId());
				this.updateTransactionRecordFailed(userTransactionRecord.getId(), 2, 0, "成功", date);
				logger.info("【随心付结果查询{}】修改企业交易记录成功：{}",transactionNumber,unionTransactionRecord.getId());
				this.updateTransactionRecordFailed(unionTransactionRecord.getId(), 2, 0, "成功", date);
				
				SxfRecord edit = new SxfRecord();
				edit.setId(sxfRecord.getId());
				edit.setStatus(1);
				edit.setUpdateTime(date);
				sxfRecordMapper.updateByPrimaryKeySelective(edit);
			}else if("3".equals(orders[1])){
				logger.info("【随心付结果查询{}】状态为未知",transactionNumber);
				//修改提现记录为未知
				SxfRecord edit = new SxfRecord();
				edit.setId(sxfRecord.getId());
				edit.setStatus(3);
				edit.setUpdateTime(date);
				sxfRecordMapper.updateByPrimaryKeySelective(edit);
				return "AGAIN";//需要继续查询
			}else{
				logger.info("【随心付结果查询{}】状态为处理中",transactionNumber);
				return "AGAIN";//需要继续查询
			}
		} catch (Exception e) {
			return "AGAIN";
		}
		return "SUCCESS";
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void saveTransactionRecord(Long userId,Long unionId, Long point, String unionNo, String tNo, Long clientId,Date nowDate,Integer model,Long fee,String feeRate){
		// 添加用户交易记录
		TransactionRecord userRecord = new TransactionRecord();
		userRecord.setChannel(1);
		userRecord.setComment("");
		userRecord.setCreateTime(nowDate);
		userRecord.setInfo("银联");
		userRecord.setOrderNo(unionNo);
		userRecord.setPayType(1);// 财币
		userRecord.setPicUrl("");
		if(30 == model.intValue()){
			userRecord.setStatus(2);// 成功
		} else {
			userRecord.setStatus(1);//处理中
		}
		userRecord.setSuccessTime(nowDate);
		userRecord.setFreezeTime(nowDate);
		userRecord.setTotal(point);
		userRecord.setTransactionNumber(tNo);
		userRecord.setType(5);//累积
		userRecord.setUpdateTime(nowDate);
		userRecord.setUserId(userId);
		transactionRecordService.insert(userRecord);
		
		// 添加企业交易记录
		TransactionRecord unionRecord = new TransactionRecord();
		unionRecord.setChannel(1);
		unionRecord.setCreateTime(nowDate);
		unionRecord.setInfo("送出给用户");
		unionRecord.setOrderNo(unionNo);
		unionRecord.setPayType(1);// 财币
		unionRecord.setPicUrl("");
		if(30 == model.intValue()){
			unionRecord.setStatus(2);// 成功
			unionRecord.setComment("成功");
		} else {
			unionRecord.setStatus(1);//处理中
			unionRecord.setComment("处理中");
		}
		unionRecord.setSuccessTime(nowDate);
		unionRecord.setFreezeTime(nowDate);
		unionRecord.setTotal(point+fee);
		unionRecord.setTransactionNumber(tNo);
		unionRecord.setType(model);//企业是消费
		unionRecord.setUpdateTime(nowDate);
		unionRecord.setUserId(unionId);
		unionRecord.setCompanyId(clientId);
		unionRecord.setFee(fee);
		unionRecord.setFeeRate(feeRate);
		transactionRecordService.insert(unionRecord);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveAccountDetail(Long userId,String orderNo){
		//用户数据
		TransactionRecord userTranRecord = transactionRecordService.getTransactionRecord(userId,orderNo);
		Date nowDate = new Date();
		AccountDetail userAccountDetail = new AccountDetail();
		userAccountDetail.setRecordId(userTranRecord.getId());
		userAccountDetail.setGmtCreate(nowDate);
		userAccountDetail.setGmtModify(nowDate);
		userAccountDetail.setIntegralChange(userTranRecord.getTotal());
		userAccountDetail.setMemo("银联智慧代充代付");
		userAccountDetail.setType(AccountDetailConstants.TYPE_IN);
		userAccountDetail.setUserId(userTranRecord.getUserId());
		accountDetailService.insertAccountDetail(userAccountDetail );
		//企业数据
		TransactionRecord companyTranRecord = transactionRecordService
				.selectByOrderNoExludeUserId(userId,orderNo);
		AccountDetail companyAccountDetail = new AccountDetail();
		companyAccountDetail.setRecordId(companyTranRecord.getId());
		companyAccountDetail.setGmtCreate(nowDate);
		companyAccountDetail.setGmtModify(nowDate);
		companyAccountDetail.setIntegralChange(-companyTranRecord.getTotal());//出分  负数
		companyAccountDetail.setMemo("银联智慧代充代付");
		companyAccountDetail.setType(AccountDetailConstants.TYPE_OUT);
		companyAccountDetail.setUserId(companyTranRecord.getUserId());
		accountDetailService.insertAccountDetail(companyAccountDetail );
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAccountRecord(Long userid, Long unionid, Long integral, Long fee,Integer model) {
		Date nowDate = new Date();
		Account companyRecord = new Account();
		companyRecord = accountService.selectByUserId(unionid);
		
		//企业财币阈值
		Long threshold = appConfig.companyIntegralThreshold;
		//如果企业可用财币达到某个预警值，短信通知充值财币
		if(companyRecord.getAvailableIntegral().compareTo(threshold) < 0 ){
			User companyUser = userService.selectCompanyUser(companyRecord.getUserId());
			logger.info("企业可用财币预警，企业编号为{},企业名为{}的可用财币不足{}，请尽快补充财币",companyRecord.getUserId(),companyUser.getNick(),threshold);
		}
		//更新用户总财币
		if(30 == model.intValue()){
			Account userRecord = new Account();
			userRecord = accountService.selectByUserId(userid);
			userRecord.setTotalIntegral(userRecord.getTotalIntegral() + integral);
			userRecord.setAvailableIntegral(userRecord.getAvailableIntegral() + integral);
			userRecord.setGmtModify(nowDate);
			accountService.updateAccountByUserId(userRecord );
		}
		//企业总财币
		companyRecord.setTotalIntegral(companyRecord.getTotalIntegral() - integral - fee);
		companyRecord.setAvailableIntegral(companyRecord.getAvailableIntegral() - integral - fee);
		companyRecord.setGmtModify(nowDate);
		accountService.updateAccountByUserId(companyRecord );
	}
	/*
	 * 失败处理
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void unionFailedHandle(Long userId, Long unionId,Long userTotal,Long unionTotal,Long userTransactionRecordId, Long unionTransactionRecordId,Date nowDate,String memo,Long sxfRecordId){
		//增加返还财币账户详细记录
		AccountDetail userAccountDetail = new AccountDetail();
		userAccountDetail.setRecordId(userTransactionRecordId);
		userAccountDetail.setGmtCreate(nowDate);
		userAccountDetail.setGmtModify(nowDate);
		userAccountDetail.setIntegralChange(userTotal);
		userAccountDetail.setMemo("银联智慧代充失败");
		userAccountDetail.setType(AccountDetailConstants.TYPE_OUT);
		userAccountDetail.setUserId(userId);
		accountDetailService.insertAccountDetail(userAccountDetail );
		
		AccountDetail companyAccountDetail = new AccountDetail();
		companyAccountDetail.setRecordId(unionTransactionRecordId);
		companyAccountDetail.setGmtCreate(nowDate);
		companyAccountDetail.setGmtModify(nowDate);
		companyAccountDetail.setIntegralChange(unionTotal);
		companyAccountDetail.setMemo("提现失败返还银联智慧财币");
		companyAccountDetail.setType(AccountDetailConstants.TYPE_IN);
		companyAccountDetail.setUserId(unionId);
		accountDetailService.insertAccountDetail(companyAccountDetail );
		//返还企业财币
		Account companyRecord  = accountService.selectByUserId(unionId);
		Account edit = new Account();
		edit.setTotalIntegral(companyRecord.getTotalIntegral() + unionTotal);
		edit.setAvailableIntegral(companyRecord.getAvailableIntegral() + unionTotal);
		edit.setGmtModify(nowDate);
		edit.setId(companyRecord.getId());
		accountService.updateAccountByUserId(edit );
		//修改失败状态，失败原因
		this.updateTransactionRecordFailed(unionTransactionRecordId,-1,2, memo, nowDate);
		this.updateTransactionRecordFailed(userTransactionRecordId,-1,2, memo, nowDate);
		//修改提现记录为失败
		SxfRecord SxfRecord = new SxfRecord();
		SxfRecord.setId(sxfRecordId);
		SxfRecord.setStatus(0);
		SxfRecord.setUpdateTime(nowDate);
		sxfRecordMapper.updateByPrimaryKeySelective(SxfRecord);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateTransactionRecordFailed(Long transactionRecordId, Integer status, Integer handleStatus,
			String memo, Date date) {
		TransactionRecord record = new TransactionRecord();
		record.setUpdateTime(date);
		record.setStatus(status);
		record.setHandleStatus(handleStatus);
		record.setComment(memo);
		record.setId(transactionRecordId);
		transactionRecordService.updateByPrimaryKeySelective(record);
	}
}
