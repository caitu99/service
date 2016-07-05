/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mq.producer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AbstractJunit;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: KafkaProducerTest 
 * @author Hongbo Peng
 * @date 2015年12月1日 下午10:27:50 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class KafkaProducerTest extends AbstractJunit {

	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Test
	public void test() {
		JSONObject o = new JSONObject();
		o.put("pushType", "PUSH_TOPIC");
//		o.put("pushType", "PUSH_MESSAGE");
		o.put("title", "这里是标题");
		o.put("description", "这里是描述【来自后端服务推送】10.58");
		o.put("payload", "这里是内容【来自后端服务推送】");
		o.put("regId", "d//igwEhgBGCI2TG6lWqlENNjeOFC71NK0WRukmYZknKiX1MFlrdKDWrJpCR6QdWUh1Hss0kRnCpKX8unz3/jAktYd1rY1Aw5eUihqk0Eaw=");
		o.put("type", "2");
		kafkaProducer.sendMessage(JSON.toJSONString(o), "app_message_push_topic");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
