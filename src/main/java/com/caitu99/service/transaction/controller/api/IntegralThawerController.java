/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.controller.api;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.transaction.api.ICompanyToThaw;
import com.caitu99.service.transaction.constants.TransactionRecordConstants;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralThawerController 
 * @author ws
 * @date 2015年12月2日 下午3:51:24 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
public class IntegralThawerController {
	
	private static final Logger logger = LoggerFactory.getLogger(IntegralThawerController.class);

	@Autowired
	TransactionRecordService transactionService;
	@Autowired
	AccountDetailService accountDetailService;
	@Autowired
	AccountService accountService;
	
	
	@RequestMapping(value = "/api/transaction/thaw/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String thawIntegral(HttpServletRequest request) {
    	
        String userId = request.getParameter("userId");
        String orderNo = request.getParameter("orderNo");
        System.out.println("执行操作...");
		TransactionRecord userTranRecord = transactionService.getTransactionRecord(Long.parseLong(userId), orderNo);
		if(null == userTranRecord){
			logger.warn("用户交易记录不存在:userId:{},orderNo:{}",userId,orderNo);
			return "";
		}
		if(TransactionRecordConstants.STATUS_FREEZE == userTranRecord.getStatus()){
			
			Date nowDate = new Date();
			//依据用户Id 查询企业 交易表记录
			TransactionRecord companyTranRecord = transactionService.selectByOrderNoExludeUserId(
						userTranRecord.getUserId(),userTranRecord.getOrderNo());
			if(null == companyTranRecord){
				logger.warn("企业交易记录不存在:userId:{},orderNo:{}",userTranRecord.getUserId(),userTranRecord.getOrderNo());
				return "";
			}
			
			//更新用户 交易记录为失败
			userTranRecord.setStatus(TransactionRecordConstants.STATUS_FAIL);
			userTranRecord.setUpdateTime(nowDate);
			transactionService.updateByPrimaryKey(userTranRecord);
			
			//更新企业 交易记录为失败
			companyTranRecord.setStatus(TransactionRecordConstants.STATUS_FAIL);
			companyTranRecord.setUpdateTime(nowDate);
			transactionService.updateByPrimaryKey(companyTranRecord);
			return "";
		}else{
			logger.info("无需划转，用户Id:{}",userId);
			return "";
		}
    }
	
	
	@Autowired
	ICompanyToThaw companyToThaw;
	
	
	@RequestMapping(value = "/api/transaction/test/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String test(HttpServletRequest request) {
		/*String phoneNo = "15858284090";
		String orderNo = "999999999";
		Long integral = 100L;*/
		

        String phoneNo = request.getParameter("phoneNo");
        String orderNo = request.getParameter("orderNo");
        Long integral = Long.parseLong(request.getParameter("integral"));
		
		companyToThaw.addCompanyToThaw(phoneNo, orderNo, integral);
        return null;
    }
}
