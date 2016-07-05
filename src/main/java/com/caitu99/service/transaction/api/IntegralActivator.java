/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.IntegralTransferService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.Configuration;

/** 
 * 
 * 用于用户首次登陆时激活财币
 * 依据用户表中的登录次数进行判断
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralThawer 
 * @author ws
 * @date 2015年12月1日 上午10:06:42 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class IntegralActivator implements IintegralActivator{

	private final static Logger logger = LoggerFactory
			.getLogger(IntegralActivator.class);
	
	@Autowired
    private UserService userService;
	@Autowired
	TransactionRecordService transactionService;
	@Autowired
	AccountDetailService accountDetailService;
	@Autowired
	AccountService accountService;
	@Autowired
	IntegralTransferService transferService;
	@Autowired
	KafkaProducer kafkaProducer;
	@Autowired
	AppConfig appConfig;
	@Autowired
	private PushMessageService pushMessageService;

	/* (non-Javadoc)
	 * @see com.caitu99.service.company.api.IintegralActivator#activateUserIntegral(java.lang.Long)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void activateUserIntegral(Long userId) {
		//查询用户表，获取登录次数，满足条件继续
		//直冲模式不需要激活
		/*User user = userService.getById(userId);
		int loginCount = user.getLoginCount();
		if(loginCount == 1){//首次登录
			//激活用户财币，修改交易表状态，财币表新增一条财币入分记录，重新计算总财币
			List<TransactionRecord> TranRecords = transactionService.selectByUserIdFreeze(userId);
			if(null == TranRecords || TranRecords.size() == 0){
				return ;
			}
			Long totalIntegral = 0L;//总共激活的财币
			for (TransactionRecord transactionRecord : TranRecords) {
				
				//扣除企业财币，修改交易表状态，财币表新增一条出分记录，重新计算总财币
				transferService.transferSingleRecord(transactionRecord);
				totalIntegral = totalIntegral + transactionRecord.getTotal();
			}
			//消息通知 
			try {
				String description =  Configuration.getProperty("push.integral.charge.description", null);
				String sms = Configuration.getProperty("push.integral.charge.sms", null);
				String title = Configuration.getProperty("push.integral.charge.title", null);
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(true);
				message.setIsYellow(false);
				message.setTitle(title);
				message.setPushInfo(String.format(description, totalIntegral/100.0,totalIntegral));
				message.setSmsInfo(String.format(sms, totalIntegral/100.0,totalIntegral));
				logger.info("新增消息通知：userId:{},description:{}",userId,JSON.toJSONString(message));
				pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, userId, message);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("银联赠送财币推送消息发生异常：{}",e);
			}
		}*/
		/*try{
			
		}catch(Exception ex){//所有异常内吞，记录异常日志，不影响外部功能
			logger.error("激活用户财币异常，userId:{}",userId);
			logger.error("激活用户财币异常：{}",ex);
		}*/
	}
	
	
	
	
}
