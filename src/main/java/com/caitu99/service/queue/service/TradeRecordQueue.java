/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.queue.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import com.caitu99.service.queue.entity.TradeRecord;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CompanyIntegralFreezer 
 * @author ws
 * @date 2015年11月30日 下午7:28:08 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class TradeRecordQueue {

	private static Queue<TradeRecord> tradeRecordQueue = null;

	
	public static Queue<TradeRecord> getInstance() {
		if (tradeRecordQueue == null) {
			synchronized (TradeRecordQueue.class) {
				if (tradeRecordQueue == null) {
					tradeRecordQueue = new LinkedList<TradeRecord>();
				}
			}
		}
		return tradeRecordQueue;
	}
	
	
}
