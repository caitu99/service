/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import scala.collection.mutable.StringBuilder;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.IntegralRechargeException;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.IntegralRechargeService;
import com.caitu99.service.user.dao.UserAuthMapper;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.unionpay.UnionOpens;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralRechargeServiceImpl 
 * @author Hongbo Peng
 * @date 2015年12月10日 下午4:29:03 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class IntegralRechargeServiceImpl implements IntegralRechargeService {

	private final static Logger logger = LoggerFactory.getLogger(IntegralRechargeServiceImpl.class);
	@Autowired
	private UserAuthMapper userAuthMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private AccountDetailMapper accountDetailMapper;
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private TransactionRecordMapper transactionRecordMapper;
	@Autowired
	private KafkaProducer kafkaProducer;
	@Autowired
	private AppConfig appConfig;
	
	@SuppressWarnings("rawtypes")
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "IntegralRechargeException" })
	@Override
	public String integralReharge(Long userId, String paypass,Long integral) throws IntegralRechargeException{
		ApiResult<String> apiResult = new ApiResult<String>();
		if(null == userId){
			throw new IntegralRechargeException(2008, "用户Id不能为空");
		}
		if(StringUtils.isEmpty(paypass)){
			throw new IntegralRechargeException(2455, "支付密码不能为空");
		}
		if(null == integral){
			throw new IntegralRechargeException(3105, "财币不能为空");
		}
		//1.验证 帐号、支付密码、银行卡
		User user = userMapper.selectByPrimaryKey(userId);
		if(null == user){
			logger.error("找不到userId={}的记录",userId);
			throw new IntegralRechargeException(2411, "账户不存在");
		}
		try {
			paypass = AESCryptoUtil.encrypt(paypass);
		} catch (Exception e) {
			logger.error("加密密码发生异常{}",e);
			throw new IntegralRechargeException(2203,"支付失败");
		}
		
		if (StringUtils.isEmpty(user.getPaypass())){
			//设置支付密码
			User editUser = new User();
			editUser.setId(userId);
			editUser.setPaypass(paypass);
			userMapper.updateByPrimaryKeySelective(editUser);
		} else {
			//校验支付密码
			if (!paypass.equals(user.getPaypass())){
				throw new IntegralRechargeException(3403,"密码有误，请重试");
			}
		}
		// 校验用户绑定信息
		UserAuth userAuth = userAuthMapper.selectByUserIdForCardPay(user.getId());
		if (userAuth == null){
			throw new IntegralRechargeException(2045, "您还没有绑定银行卡");
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			//2.生成交易记录
			TransactionRecord transactionRecord = new TransactionRecord();
			transactionRecord.setTransactionNumber(XStringUtil.getOrder());
			transactionRecord.setOrderNo("");
			transactionRecord.setUserId(userId);
			transactionRecord.setInfo(new StringBuilder(userAuth.getBankname()).append("(")
				.append(userAuth.getCardNo().substring(userAuth.getCardNo().length()-4)).append(")").toString());
			transactionRecord.setType(3);//充值
			transactionRecord.setPayType(2);//银行卡
			transactionRecord.setStatus(1);//处理中
			transactionRecord.setTotal(integral);
			transactionRecord.setChannel(6);//银行卡充值
			transactionRecord.setCreateTime(date);
			transactionRecord.setUpdateTime(date);
			transactionRecordMapper.insertSelective(transactionRecord);
			
			//3.调用支付接口
			//支付金额
			Long amount = CalculateUtils.divide(integral, 100);
			UnionOpens unionOpens = new UnionOpens();
			Map map = unionOpens.singlePay(transactionRecord.getTransactionNumber(),
					userAuth.getBindId(), amount, sdf.format(date));
			//4.处理请求结果
			//支付成功，给账户充积分
			//支付处理中，启动定时任务查询订单状态，成功后给账户充积分
			logger.info("union back params : {}",map);
			if(!"0000".equals(map.get("retCode")) && !"0".equals(map.get("retCode"))){
				logger.error("调用银联接口支付失败:{}", map.get("retDesc"));
				throw new Exception(map.get("retDesc").toString());
			}
			String status = (String) map.get("orderStatus");
			if("2".equals(status)){
				//成功
				updateTransactionRecordStatus(transactionRecord.getId(),2,map.get("queryId").toString());
				rechargeAccount(transactionRecord);
				logger.info("充值积分，支付成功");
				return apiResult.toJSONString(0, "支付成功");
			} else if("3".equals(status)){
				//失败
				logger.info("充值积分，支付失败:{}",map.get("retDesc"));
				throw new Exception(map.get("retDesc").toString());
			} else if("0".equals(status) || "1".equals(status)){
				//已接受，处理中
				Map<String, Object> jobmap = new HashMap<>();
				jobmap.put("id", transactionRecord.getId());
				jobmap.put("jobType","INTEGRAL_RECHARGE_QUERY_JOB"); 
				kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
				return apiResult.toJSONString(3401, "支付受理中");
			} else {
				logger.error("调用银联支付返回未知情况：{}",map);
				throw new Exception("支付失败");
			}
		} catch (Exception e) {
			throw new IntegralRechargeException(3402,e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "IntegralRechargeException" })
	@Override
	public String query(Long id) throws IntegralRechargeException{
		ApiResult<String> apiResult = new ApiResult<String>();
		TransactionRecord transactionRecord = transactionRecordMapper.selectByPrimaryKey(id);
		if(null == transactionRecord){
			logger.error("交易记录id：{}不存在",id);
			throw new IntegralRechargeException(3003,"交易不存在");
		}
		UnionOpens unionOpens = new UnionOpens();
		try {
			Map map = unionOpens.getOrder(transactionRecord.getTransactionNumber());
			//{"retDesc":"","orderNo":"CZ","orderDetail":{"retDesc":"","processDate":"20151210","orderStatus":"2","remark":"","retCode":"0000"},"retCode":"0000"}
			if(!"0000".equals(map.get("retCode")) && !"0".equals(map.get("retCode"))){
				logger.error("调用银联接口查询订单失败:{}", map.get("retDesc"));
				throw new IntegralRechargeException(3102,"查询失败");
			}
			Map orderDetail = (Map)JSON.parse((String)map.get("orderDetail"));
			if("2".equals(orderDetail.get("orderStatus"))){
				//支付成功，给用户充值财币
				updateTransactionRecordStatus(id,2,null);
				rechargeAccount(transactionRecord);
			} else if ("3".equals(orderDetail.get("orderStatus"))){
				//支付失败，将交易改为失败
				updateTransactionRecordStatus(id,-1,null);
			} else {
				//抛出异常，再次查询
				throw new IntegralRechargeException(3102,"订单还在处理，一会儿再查一次吧");
			}
			return apiResult.toJSONString(0, "交易成功");
		} catch (Exception e) {
			throw new IntegralRechargeException(3102,e.getMessage());
		}
	}


	/**
	 * @Description: (修改交易记录状态)  
	 * @Title: updateTransactionRecordStatus 
	 * @param id
	 * @param status
	 * @date 2015年12月10日 下午5:44:17  
	 * @author Hongbo Peng
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "Exception" })
	private void updateTransactionRecordStatus(Long id,Integer status,String thirdPartyNumber){
		Date date = new Date();
		TransactionRecord transactionRecord = new TransactionRecord();
		transactionRecord.setId(id);
		transactionRecord.setStatus(status);
		transactionRecord.setUpdateTime(date);
		if(-1 == status.intValue()){
			transactionRecord.setFaileTime(date);
		} else if(2 == status.intValue()){
			transactionRecord.setSuccessTime(date);
			transactionRecord.setThirdPartyNumber(thirdPartyNumber);
		}
		transactionRecordMapper.updateByPrimaryKeySelective(transactionRecord);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "IntegralRechargeException" })
	private void rechargeAccount(TransactionRecord transactionRecord) throws IntegralRechargeException{
		try {
			Date date = new Date();
			//1.记录财币详细记录
			AccountDetail accountDetail = new AccountDetail();
			accountDetail.setUserId(transactionRecord.getUserId());
			accountDetail.setRecordId(transactionRecord.getId());
			accountDetail.setIntegralChange(transactionRecord.getTotal());
			accountDetail.setType(1);
			accountDetail.setMemo("银行卡充值");
			accountDetail.setGmtCreate(date);
			accountDetail.setGmtModify(date);
			accountDetailMapper.insertSelective(accountDetail);
			
			//2.给财币账户充值
			Account account = accountMapper.selectByUserId(Long.valueOf(transactionRecord.getUserId()));
			Account editAccount = new Account();
			editAccount.setId(account.getId());
			editAccount.setTotalIntegral(account.getTotalIntegral()+transactionRecord.getTotal());
			editAccount.setAvailableIntegral(account.getAvailableIntegral()+transactionRecord.getTotal());
			editAccount.setGmtModify(date);
			accountMapper.updateByPrimaryKeySelective(editAccount);
		} catch (Exception e) {
			throw new IntegralRechargeException(3102,e.getMessage());
		}
	}
}
