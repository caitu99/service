/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.push;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.utils.Configuration;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PushMessageTest 
 * @author Hongbo Peng
 * @date 2015年12月24日 下午5:15:59 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class PushMessageTest extends AbstractJunit {

	@Autowired
	private PushMessageService pushMessageService;
	
//	@Test
	public void Test(){
		Message m = new Message();
		m.setIsPush(true);
		m.setIsSMS(true);
		m.setPushInfo("这是要推送的消息内容");
		m.setSmsInfo("这是要发送短信的内容");
		try {
			pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, 1L, m);
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void Test2(){
		System.out.println(Configuration.getProperty("push.auto.find.pwd.error.description", null));
	}
}
