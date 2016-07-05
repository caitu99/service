/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.api;

import java.util.HashMap;
import java.util.Map;

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
import com.caitu99.service.transaction.service.IntegralTransferService;
import com.caitu99.service.user.api.IUserServiceApi;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.Configuration;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CompanyToThaw 
 * @author ws
 * @date 2015年12月1日 下午12:22:17 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class CompanyToThaw implements ICompanyToThaw {


	private static final Logger logger = LoggerFactory
			.getLogger(CompanyToThaw.class);
	
	@Autowired
	UserService userService;
	@Autowired
	IUserServiceApi iUserServiceApi;
	@Autowired
	IntegralTransferService integralTransferService;
	@Autowired
	KafkaProducer kafkaProducer;
	@Autowired
	AppConfig appConfig;
	@Autowired
	private PushMessageService pushMessageService;
	/**
	 * 判断用户是否是新用户
	 * 如果是，则加入解冻计划中
	 * 如果不是，则直接划转财币
	 * <P>事务处理
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addCompanyToThaw(String phoneNo, String orderNo, Long integral) {

		//获取用户信息，如果是新用户，则注册用户
		User user = iUserServiceApi.addUser(phoneNo);
		
		int loginCount = user.getLoginCount();
		if(0 == loginCount){//未激活的新用户
			String message = "";
			Map<String,Object> jsonParam = new HashMap<String,Object>();
			jsonParam.put("userId", user.getId());
			jsonParam.put("orderNo", orderNo);
			jsonParam.put("jobType", "INTEGRAL_THAW");
			message = JSON.toJSONString(jsonParam);
			//加入解冻计划中
			//往消息队列加入主题 
			//kafkaProducer.sendMessage(message , "job_message_topic");
			kafkaProducer.sendMessage(message , appConfig.jobTopic);
			logger.info("新增解冻计划：{}",message);
		}else{
			//直接划转财币1、更新交易表状态为正常；2、插入财币明细表；3、更新总财币
			integralTransferService.transferIntegral(user.getId(), orderNo);
			//消息通知
			try {
				String description =  Configuration.getProperty("push.integral.charge.description", null);
				String sms = Configuration.getProperty("push.integral.charge.sms", null);
				String title = Configuration.getProperty("push.integral.charge.title", null);
				Long userId = user.getId();
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(true);
				message.setIsYellow(false);
				message.setTitle(title);
				message.setPushInfo(String.format(description, integral/100.0,integral));
				message.setSmsInfo(String.format(sms, integral/100.0,integral));
				logger.info("新增消息通知：userId:{},description:{}",userId,JSON.toJSONString(message));
				pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, userId, message);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("银联赠送财币推送消息发生异常：{}",e);
			}
		}
		
	}

}
