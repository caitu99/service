/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.api;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.caitu99.service.AppConfig;
import com.caitu99.service.exception.AccountException;
import com.caitu99.service.transaction.constants.AccountDetailConstants;
import com.caitu99.service.transaction.constants.TransactionRecordConstants;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UnionPayRechargeDirectService 
 * @author ws
 * @date 2016年1月11日 上午11:53:36 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class UnionPayRechargeDirectService implements UnionPayRechargeDirectApi{
	
	private final static Logger logger = LoggerFactory
			.getLogger(UnionPayRechargeDirectService.class);

	@Autowired
	TransactionRecordService transactionService;
	@Autowired
	AccountDetailService accountDetailService;
	@Autowired
	AccountService accountService;
	@Autowired
	UserService userService;
	@Autowired
	AppConfig appConfig;
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.api.UnionPayRechargeDirectApi#rechargeDirect(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = {"AccountException"})
	@Override
	public AccountResult rechargeDirect(Long userid, Long unionid, Long integral,
			String orderNo, String tNo, Long clientId) {
		
		AccountResult accountResult = new AccountResult();
		accountResult.setSuccess(false);
		// 用户账户验证
		Account userAccount = accountService.selectByUserId(userid);
		if (null == userAccount) {
			accountResult.setCode(3001);
			accountResult.setResult("用户账户不存在");
			return accountResult;
		}
		// 企业账户验证
		Account unionAccount = accountService.selectByUserId(unionid);
		if (null == unionAccount) {
			accountResult.setCode(3001);
			accountResult.setResult("银联账户不存在");
			return accountResult;
		}
		
		if (unionAccount.getAvailableIntegral().compareTo(integral) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("账户财币不足");
			return accountResult;
		}
		
		try {
			//保存交易记录
			saveTransactionRecord(userid, unionid, integral, orderNo, tNo,
					clientId);
			
			//保存财币明细
			saveAccountDetailRecord(userid, orderNo);
			
			//更新财币账户
			updateAccountRecord(userid, unionid, integral);
			
		} catch (Exception e) {
			logger.error("银联智慧");
			throw new AccountException(3102, e.getMessage());
		}
		// 数据返回
		accountResult.setCode(3101);
		accountResult.setSuccess(true);
		accountResult.setResult("交易成功");
		return accountResult;
	}

	/**
	 * 更新财币账户数据
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateAccountRecord 
	 * @param userid
	 * @param unionid
	 * @param integral
	 * @date 2016年1月11日 下午2:06:37  
	 * @author ws
	 */
	private void updateAccountRecord(Long userid, Long unionid, Long integral) {
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
		Account userRecord = new Account();
		userRecord = accountService.selectByUserId(userid);
		userRecord.setTotalIntegral(userRecord.getTotalIntegral() + integral);
		userRecord.setAvailableIntegral(userRecord.getAvailableIntegral() + integral);
		userRecord.setGmtModify(nowDate);
		accountService.updateAccountByUserId(userRecord );
		//企业总财币
		companyRecord.setTotalIntegral(companyRecord.getTotalIntegral() - integral);
		companyRecord.setAvailableIntegral(companyRecord.getAvailableIntegral() - integral);
		companyRecord.setGmtModify(nowDate);
		accountService.updateAccountByUserId(companyRecord );
	}

	/**
	 * 保存财币明细数据
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveAccountDetailRecord 
	 * @param userid
	 * @param orderNo
	 * @date 2016年1月11日 下午2:06:50  
	 * @author ws
	 */
	private void saveAccountDetailRecord(Long userid, String orderNo) {
		//用户数据
		TransactionRecord userTranRecord = transactionService.getTransactionRecord(userid,orderNo);
		Date nowDate = new Date();
		AccountDetail userAccountDetail = new AccountDetail();
		userAccountDetail.setRecordId(userTranRecord.getId());
		userAccountDetail.setGmtCreate(nowDate);
		userAccountDetail.setGmtModify(nowDate);
		userAccountDetail.setIntegralChange(userTranRecord.getTotal());
		userAccountDetail.setMemo(""+userTranRecord.getComment());
		userAccountDetail.setType(AccountDetailConstants.TYPE_IN);
		userAccountDetail.setUserId(userTranRecord.getUserId());
		accountDetailService.insertAccountDetail(userAccountDetail );
		//企业数据
		TransactionRecord companyTranRecord = transactionService
				.selectByOrderNoExludeUserId(userid,orderNo);
		AccountDetail companyAccountDetail = new AccountDetail();
		companyAccountDetail.setRecordId(companyTranRecord.getId());
		companyAccountDetail.setGmtCreate(nowDate);
		companyAccountDetail.setGmtModify(nowDate);
		companyAccountDetail.setIntegralChange(-userTranRecord.getTotal());//出分  负数
		companyAccountDetail.setMemo(""+companyTranRecord.getComment());
		companyAccountDetail.setType(AccountDetailConstants.TYPE_OUT);
		companyAccountDetail.setUserId(companyTranRecord.getUserId());
		accountDetailService.insertAccountDetail(companyAccountDetail );
	}

	/**
	 * 保存交易明细数据
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveTransactionRecord 
	 * @param userid
	 * @param unionid
	 * @param integral
	 * @param orderNo
	 * @param tNo
	 * @param clientId
	 * @date 2016年1月11日 下午2:07:11  
	 * @author ws
	 */
	private void saveTransactionRecord(Long userid, Long unionid,
			Long integral, String orderNo, String tNo, Long clientId) {
		Date nowDate = new Date();
		// 添加用户交易记录
		TransactionRecord userRecord = new TransactionRecord();
		userRecord.setChannel(1);
		userRecord.setComment("");
		userRecord.setCreateTime(nowDate);
		userRecord.setInfo("银联");
		userRecord.setOrderNo(orderNo);
		userRecord.setPayType(1);// 财币
		userRecord.setPicUrl("");
		userRecord.setStatus(2);// 成功
		userRecord.setSuccessTime(nowDate);
		userRecord.setFreezeTime(nowDate);
		userRecord.setTotal(integral);
		userRecord.setTransactionNumber(tNo);
		userRecord.setType(5);//累积
		userRecord.setUpdateTime(nowDate);
		userRecord.setUserId(userid);
		userRecord.setCompanyId(clientId);
		transactionService.insert(userRecord);
		// 添加企业交易记录
		TransactionRecord unionRecord = new TransactionRecord();
		unionRecord.setChannel(1);
		unionRecord.setComment("");
		unionRecord.setCreateTime(nowDate);
		unionRecord.setInfo("送出给用户");
		unionRecord.setOrderNo(orderNo);
		unionRecord.setPayType(1);// 财币
		unionRecord.setPicUrl("");
		unionRecord.setStatus(2);// 成功
		unionRecord.setSuccessTime(nowDate);
		unionRecord.setFreezeTime(nowDate);
		unionRecord.setTotal(integral);//统一为正，通过type标识扣费
		unionRecord.setTransactionNumber(tNo);
		unionRecord.setType(1);//企业是消费
		unionRecord.setUpdateTime(nowDate);
		unionRecord.setUserId(unionid);
		unionRecord.setCompanyId(clientId);
		transactionService.insert(unionRecord);
	}
	
}
