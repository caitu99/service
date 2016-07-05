/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mail.controller.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.domain.UserMail;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserMailService;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.date.TimeUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailJobService 
 * @author ws
 * @date 2015年12月10日 下午5:44:01 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class MailJobThread implements Runnable{

	private static final Logger logger = LoggerFactory
			.getLogger(MailJobThread.class);
	
	MailJobService mailJobService;
	
	private String ERR_MSG = "邮箱自动更新失败：userid:{},account:{},err:{}";
	
	private String userId;
	private String cardTypeId;
	
	/** 
	 * @Title:  
	 * @Description: 
	 */
	public MailJobThread(String userid,String cardTypeId) {
		this.userId = userid;
		this.cardTypeId = cardTypeId;
		this.mailJobService = SpringContext.getBean("mailJobService");
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		mailJobService.parseMail(userId, cardTypeId);
		
	}

	
}
