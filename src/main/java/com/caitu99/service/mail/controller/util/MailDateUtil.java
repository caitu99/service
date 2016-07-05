/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mail.controller.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailDateUtil 
 * @author ws
 * @date 2015年12月16日 上午9:56:37 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class MailDateUtil {
	
	public static Date getDateFromBillDay(Date nowDate,int billDay){

		Random random = new Random();
		
		Calendar cal=Calendar.getInstance();
		/*
		cal.set(Calendar.YEAR, nowDate.getYear());
	
		cal.set(Calendar.MONTH, nowDate.getMonth());*/
	
		cal.set(Calendar.DAY_OF_MONTH, billDay);
		
		cal.set(Calendar.HOUR_OF_DAY, 2);//2点钟

		cal.set(Calendar.MINUTE, random.nextInt(30));//30分钟内随机
		
		cal.set(Calendar.SECOND, 0);
		
		return cal.getTime();
		
	}
	
	
	public static Date getNextMonthBillDay(Date nowDate,int billDay){
		
		Random random = new Random();
		
		Calendar cal=Calendar.getInstance();
		
		//cal.set(Calendar.YEAR, nowDate.getYear());
	
		cal.add(Calendar.MONTH, 1);
	
		cal.set(Calendar.DAY_OF_MONTH, billDay);
		
		cal.set(Calendar.HOUR_OF_DAY, 2);//2点钟
		
		cal.set(Calendar.MINUTE, random.nextInt(30));//30分钟内随机
		
		cal.set(Calendar.SECOND, 0);
		
		return cal.getTime();
		
	}
	
	public static Date getFirstDayOfMounth(){

		Calendar cal=Calendar.getInstance();
	
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);

		cal.set(Calendar.MINUTE, 0);
		
		cal.set(Calendar.SECOND, 0);
		
		return cal.getTime();
		
	}
	
	/**
	 * 获取日期中的月份
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getMounthFromDate 
	 * @param dateTime
	 * @return
	 * @date 2015年12月19日 下午2:21:05  
	 * @author ws
	 */
	public static int getMounthFromDate(Date dateTime){
		Calendar nowCal = Calendar.getInstance();
		if(null == dateTime){
			return nowCal.get(Calendar.MONTH) + 1;
		}else{

			nowCal.setTime(dateTime);
			return nowCal.get(Calendar.MONTH) + 1;
		}
	}
	
	public static void main(String[] args) {
		Date nowDate = new Date();
		Date billDate = getDateFromBillDay(nowDate,3);
		System.out.println(billDate);
		

		Date nextMonthBillDate = getNextMonthBillDay(nowDate,3);
		System.out.println(nextMonthBillDate);
	}
	
}
