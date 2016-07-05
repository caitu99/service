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

import com.caitu99.service.transaction.constants.AccountDetailConstants;
import com.caitu99.service.transaction.constants.TransactionRecordConstants;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralRechargeHandler 
 * @author ws
 * @date 2015年12月30日 下午5:00:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class IntegralRechargeHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(IntegralRechargeHandler.class);
	
	@Autowired
	TransactionRecordService transactionRecordService;
	@Autowired
	AccountDetailService accountDetailService;
	@Autowired
	AccountService accountService;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void paySuccessDo(Long userId,String orderNo, String queryId){
		
		TransactionRecord tranRecord = transactionRecordService.getTransactionRecord(userId, orderNo);
		if(null == tranRecord){
			logger.warn("未查询到相关交易，userId:{},订单编号：{}",userId,orderNo);
			return;
		}
		saveRecordDetail(tranRecord, queryId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void paySuccessDo(String orderNo,String queryId){
		
		TransactionRecord tranRecord = transactionRecordService.getTransactionRecordByOrderNo(orderNo);
		if(null == tranRecord){
			logger.warn("未查询到相关交易，orderNo:{}",orderNo);
			return;
		}
		saveRecordDetail(tranRecord,queryId);
	}

	private void saveRecordDetail(TransactionRecord tranRecord, String queryId) {
		Date nowDate = new Date();
		if(tranRecord.getStatus().equals(TransactionRecordConstants.STATUS_DOING)){
			//1、更新交易表状态为成功；
			tranRecord.setStatus(TransactionRecordConstants.STATUS_SUCCESS);
			tranRecord.setSuccessTime(nowDate);
			tranRecord.setThirdPartyNumber(queryId);
			transactionRecordService.updateByPrimaryKey(tranRecord);
			
			//2、插入财币明细表；
			AccountDetail companyAccountDetail = new AccountDetail();
			companyAccountDetail.setRecordId(tranRecord.getId());
			companyAccountDetail.setGmtCreate(nowDate);
			companyAccountDetail.setGmtModify(nowDate);
			companyAccountDetail.setIntegralChange(tranRecord.getTotal());
			companyAccountDetail.setMemo("财币充值"+tranRecord.getComment());
			companyAccountDetail.setType(AccountDetailConstants.TYPE_IN);
			companyAccountDetail.setUserId(tranRecord.getUserId());
			accountDetailService.insertAccountDetail(companyAccountDetail );
			
			//3、更新总财币
			Account companyRecord = new Account();
			companyRecord = accountService.selectByUserId(tranRecord.getUserId());
			companyRecord.setTotalIntegral(companyRecord.getTotalIntegral() + tranRecord.getTotal());
			companyRecord.setAvailableIntegral(companyRecord.getAvailableIntegral() + tranRecord.getTotal());
			companyRecord.setGmtModify(nowDate);
			accountService.updateAccountByUserId(companyRecord );
			
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void payFailDo(Long userId,String orderNo, String queryId){
		Date nowDate = new Date();
		
		TransactionRecord tranRecord = transactionRecordService.getTransactionRecord(userId, orderNo);
		if(null == tranRecord){
			logger.warn("未查询到相关交易，userId:{},订单编号：{}",userId,orderNo);
			return;
		}
		if(tranRecord.getStatus().equals(TransactionRecordConstants.STATUS_DOING)){
			//1、更新交易表状态为成功；
			tranRecord.setStatus(TransactionRecordConstants.STATUS_FAIL);
			tranRecord.setSuccessTime(nowDate);
			tranRecord.setThirdPartyNumber(queryId);
			transactionRecordService.updateByPrimaryKey(tranRecord);
		}
		return;
	}
}
