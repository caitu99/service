/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.queue.service;

import java.util.Date;
import java.util.Queue;

import com.caitu99.service.queue.entity.TradeRecord;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: TradeRecordCreater 
 * @author ws
 * @date 2015年11月30日 下午8:50:20 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class TradeRecordCreater implements Runnable{

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		for(int i = 0;i<1000;i++){

			TradeRecord e = new TradeRecord();
			e.setCreateTime(new Date());
			e.setThirdId("third_id_"+i);
			e.setUserId("00"+i);
			
			TradeRecordQueue.getInstance().offer(e);
			System.out.println(TradeRecordQueue.getInstance()+""+TradeRecordQueue.getInstance().size());
			
			try {
				Thread.sleep(10000L);//每隔10秒
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
