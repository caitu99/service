/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.queue.service;

import java.util.Date;

import com.caitu99.service.queue.entity.TradeRecord;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: TestHandler 
 * @author ws
 * @date 2015年11月30日 下午8:44:11 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class TestHandler {

	public static void main(String[] args) {
		TradeRecordHandler handler = new TradeRecordHandler();
		TradeRecordCreater creater = new TradeRecordCreater();
		
		Thread handlerThread = new Thread(handler);
		Thread createrThread = new Thread(creater);
		
		handlerThread.start();
		createrThread.start();
		
	}
}
