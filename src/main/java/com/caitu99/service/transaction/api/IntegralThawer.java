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

import com.caitu99.service.transaction.constants.TransactionRecordConstants;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;

/** 
 * 
 * 用于解冻企业财币
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralThawer 
 * @author ws
 * @date 2015年12月1日 上午10:06:42 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service("integralThawer")
public class IntegralThawer{
	
	private static final Logger logger = LoggerFactory.getLogger(IntegralThawer.class);
	
	@Autowired
	TransactionRecordService transactionService;
	@Autowired
	AccountDetailService accountDetailService;
	@Autowired
	AccountService accountService;
	/**
	 * 用户未激活，解冻企业财币
	 * <P>事务处理
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: thawIntegral 
	 * @param userId
	 * @date 2015年12月1日 下午12:24:37  
	 * @author ws
	 * @param orderNo 
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void thawIntegral(Long userId, String orderNo){
		
		//System.out.println("执行操作...");
		TransactionRecord userTranRecord = transactionService.getTransactionRecord(userId, orderNo);
		if(TransactionRecordConstants.STATUS_FREEZE == userTranRecord.getStatus()){
			
			Date nowDate = new Date();
			//依据用户Id 查询企业 交易表记录
			TransactionRecord companyTranRecord = transactionService.selectByOrderNoExludeUserId(
						userTranRecord.getUserId(),userTranRecord.getOrderNo());

			//更新用户 交易记录为失败
			userTranRecord.setStatus(TransactionRecordConstants.STATUS_FAIL);
			userTranRecord.setUpdateTime(nowDate);
			transactionService.updateByPrimaryKey(userTranRecord);
			
			//更新企业 交易记录为失败
			companyTranRecord.setStatus(TransactionRecordConstants.STATUS_FAIL);
			companyTranRecord.setUpdateTime(nowDate);
			transactionService.updateByPrimaryKey(companyTranRecord);
			return;
		}else{
			logger.info("无需划转，用户Id:{}",userId);
			return;
		}
	}
	
	
}
