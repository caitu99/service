/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.queue.service;

import java.util.Date;

import com.caitu99.service.queue.entity.TradeRecord;
import com.caitu99.service.utils.date.TimeUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: TradeRecordHandler 
 * @author ws
 * @date 2015年11月30日 下午8:02:28 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class TradeRecordHandler implements Runnable{


	private static long DAY_UNIT = 24*60*60*1000;
	private static long HOUR_UNIT = 60*60*1000;
	private static long MINUTE_UNIT = 60*1000;
	private static long SECOND_UNIT = 1000;
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		while(true){
		
			TradeRecord tradeRecord = TradeRecordQueue.getInstance().poll();
			
			if(null == tradeRecord){
				try {
					Thread.sleep(30000);//停止30秒
					System.out.println("doNothing");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//TODO 
				}
				continue;
			}
			
			Date time = tradeRecord.getCreateTime();
			Date now = new Date();
			
			long thirtyDaysMillis = 30*SECOND_UNIT;
			long subtractMillis = now.getTime() - time.getTime();
			
			long millis = thirtyDaysMillis - subtractMillis;
			try {
				if(millis > 0){
					Thread.sleep(millis);
				}
				doSomething(tradeRecord);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//TODO 
			}
		
		}
	}

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: doSomething 
	 * @param tradeRecord
	 * @date 2015年11月30日 下午8:07:10  
	 * @author ws
	*/
	private void doSomething(TradeRecord tradeRecord) {
		System.out.println("doSometing..."+tradeRecord.getUserId());
		
	}
	

}
