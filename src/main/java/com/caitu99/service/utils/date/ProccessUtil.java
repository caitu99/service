package com.caitu99.service.utils.date;


import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class ProccessUtil {

	public static Date getTime(String configTimer) throws Exception {
		try {
			if (StringUtils.isBlank(configTimer)) {
				throw new IllegalArgumentException("The  configuration \"configTimer\" must be  not null,"
						+ "please check config files[job.properties]!");
			}
			Date date = new Date();
			String timer=configTimer.substring(0, configTimer.length()-1);
			if (StringUtils.isBlank(timer)) {
				throw new IllegalArgumentException("The  configuration \"timer\" must be not null,"
						+ "please check config files[job.properties]!");
			}
			if (-1 != configTimer.toUpperCase().indexOf("Y")) {//年
				return  DateUtil.addYear(date, Integer.valueOf(timer));
			}else if(-1 != configTimer.toUpperCase().indexOf("D")){//日
				return  DateUtil.addDay(date, Integer.valueOf(timer));
			}else if(-1 != configTimer.toUpperCase().indexOf("H")){//时
				return  DateUtil.addHour(date, Integer.valueOf(timer));
			}else if(-1 != configTimer.toUpperCase().indexOf("MIN")){//分
				return  DateUtil.addMinute(date, Integer.valueOf(configTimer.substring(0, configTimer.length()-3)));
			}else if(-1 != configTimer.toUpperCase().indexOf("S")){//秒
				return  DateUtil.addSecond(date, Integer.valueOf(timer));
			}else if(-1 != configTimer.toUpperCase().indexOf("M")){//月
				return DateUtil.addMonth(date, Integer.valueOf(timer));
			}else{
				return null;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	
}
