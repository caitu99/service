/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.date.DateWeekMonthUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: StringUtilsTest 
 * @author ws
 * @date 2015年12月11日 下午6:21:31 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class StringUtilsTest {

	@Test
	public void testVersion() {
		
		String version = "1.23.2";
		String[] versionArray = version.split("\\.");
		StringBuffer versionStr = new StringBuffer("");
		boolean isFirst = true;
		for (String ver : versionArray) {
			if(isFirst){
				versionStr.append(ver);
				isFirst = false;
				continue;
			}
			
			if(ver.length()==1){
				versionStr.append("00").append(ver);
			}else if(ver.length()==2){
				versionStr.append("0").append(ver);
			}else{
				versionStr.append(ver);
			}
		}
		System.out.println(versionStr.toString());
		Long versionLong = Long.parseLong(versionStr.toString());
		System.out.println(versionLong);
		
	}
	
	@Test
	public void testReplace() {
		StringBuffer cardTypeStr = new StringBuffer("");
		cardTypeStr.append("、").append("中国银行");
		cardTypeStr.append("、").append("招商银行");
		cardTypeStr.append("、").append("建设银行");
		
		System.out.println(cardTypeStr.toString().replaceFirst("、", ""));
		
	}
	
	@Test
	public void testUUID(){

        String uuid = UUID.randomUUID().toString().replace("-", "");
        System.out.println(uuid);

        Random random = new Random();
        System.out.println(random.nextInt(1000));
        String timestamp = "union"+random.nextInt(10)+System.currentTimeMillis()+random.nextInt(10)+100;
        System.out.println(timestamp);
        
        
	}
	
	
	@Test
	public void testDataParse(){
		String expressDate = "2016-12-31";
		try {
			if(StringUtils.isNotBlank(expressDate)){
				if(expressDate.contains("-")){
					expressDate = DateUtil.getDate(new SimpleDateFormat("yyyy-MM-dd").parse(expressDate));
				}else if(expressDate.contains("/")){
					expressDate = DateUtil.getDate(new SimpleDateFormat("yyyy/MM/dd").parse(expressDate));
				}else{
					expressDate = "";
				}
			}
			System.out.println(expressDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDateFormate(){
		
		System.out.println(DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		
	}
	
	@Test
	public void testReplace2(){
		int len = 4;
		String idName = "1233444455555";
		String idName1 = idName.substring(0, idName.length()-len);
		String idName2 = idName.substring(idName.length()-len);
		idName = idName1.replaceAll("^.{1}", "*")+idName2;
		System.out.println(idName);
	}
	
	@Test
	public void testGetWeekDateRange(){
		//System.out.println(getXDate(116,5));//2016,1900
		System.out.println("开始时间: " + DateWeekMonthUtil.getStartDayOfWeekNo(2016,26) );  
        System.out.println("结束时间:" + DateWeekMonthUtil.getEndDayOfWeekNo(2016,26) );
		//printWeekdays();
		
		/*Date mdate = new Date();
		
		List<Date> list = dateToWeek(mdate );
		for (Date date : list) {
			System.out.println(DateUtil.DateToString(date, DateUtil.DATE_FORMAT));
		}*/
	}

	
	public static List<Date> dateToWeek(Date mdate) {
		int b = mdate.getDay();
		Date fdate;
		List<Date> list = new ArrayList<Date>();
		Long fTime = mdate.getTime() - b * 24 * 3600000;
		for (int a = 1; a <= 7; a++) {
			fdate = new Date();
			fdate.setTime(fTime + (a * 24 * 3600000));
			list.add(a-1, fdate);
		}
		return list;
	}
	
	
	
	private static final int FIRST_DAY = Calendar.MONDAY;
	 
    private static void printWeekdays() {
        Calendar calendar = Calendar.getInstance();
        setToFirstDay(calendar);
        for (int i = 0; i < 7; i++) {
            printDay(calendar);
            calendar.add(Calendar.DATE, 1);
        }
    }
 
    private static void setToFirstDay(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != FIRST_DAY) {
            calendar.add(Calendar.DATE, -1);
        }
    }
 
    private static void printDay(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd EE");
        System.out.println(dateFormat.format(calendar.getTime()));
    }
	
	
	
	
	/**
	 *  返回某年某月周次范围
	 *  @param year  年份
	 *  @param weeks 周次
	 *  @return  返回周次的日期范围
	 */
	public String getXDate(int year,int weeks){
	        Date date = new Date(year,0,1); 
	        long time = date.getTime(); 
	        // 获取当前星期几，0：星期一 。。。。
	        int _week = date.getDay();
	        //当这一年的1月1日为周日时则本年有54周，否则没有54周，没有则去除第54周的提示
	        if(_week!=0){//一年53周情况
	                    if(weeks==54){
	                        return "今年没有54周";
	                    }
	                    int cnt=0;// 获取距离周末的天数
	                    if(_week==0){
	                        cnt = 7;
	                    }else if(_week==1){
	                        cnt = 6;
	                    }else if(_week==2){
	                        cnt = 5;
	                    }else if(_week==3){
	                        cnt = 4;
	                    }else if(_week==4){
	                        cnt = 3;
	                    }else if(_week==5){
	                        cnt = 2;
	                    }else if(_week==6){
	                        cnt = 1;
	                    }
	                    cnt += 1;//加1表示以星期一为一周的第一天
	                    // 将这个长整形时间加上第N周的时间偏移
	                    time += cnt*24*3600000; //第2周开始时间
	                     
	                    Date nextYear = new Date(year+1,0,1);
	                    int nextWeek = nextYear.getDay();
	                    int lastcnt = 0;//获取最后一周开始时间到周末的天数
	                    if(nextWeek==0){
	                        lastcnt = 6;
	                    }else if(nextWeek==1){
	                        lastcnt = 0;
	                    }else if(nextWeek==2){
	                        lastcnt = 1;
	                    }else if(nextWeek==3){
	                        lastcnt = 2;
	                    }else if(nextWeek==4){
	                        lastcnt = 3;
	                    }else if(nextWeek==5){
	                        lastcnt = 4;
	                    }else if(nextWeek==6){
	                        lastcnt = 5;
	                    }
	                    if(weeks==1){//第1周特殊处理
	                        // 为日期对象 date 重新设置成时间 time 
	                        String start = DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                        date.setTime(time-24*3600000); 
	                        return start +"--"+ DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                    }else if(weeks==53){//第53周特殊处理
	                    	long start = time+(weeks-2)*7*24*3600000; //第53周开始时间
	                    	long end = time+(weeks-2)*7*24*3600000 + lastcnt*24*3600000 - 24*3600000; //第53周结束时间
	                        // 为日期对象 date 重新设置成时间 time 
	                        date.setTime(start);
	                        String _start = DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                        date.setTime(end);
	                        String _end = DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                        return _start +"--"+ _end; 
	                    }else{
	                        long start = time+(weeks-2)*7*24*3600000; //第n周开始时间
	                        long end = time+(weeks-1)*7*24*3600000 - 24*3600000; //第n周结束时间
	                        // 为日期对象 date 重新设置成时间 time 
	                        date.setTime(start);
	                        String _start = DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                        date.setTime(end);
	                        String _end = DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                        return _start +"--"+ _end;
	                    }
	        }else{//一年54周情况
	                    int cnt=0;// 获取距离周末的天数
	                    if(_week==0 && weeks==1){//第一周
	                        cnt = 0;
	                    }else if(_week==0){
	                        cnt = 7;
	                    }else if(_week==1){
	                        cnt = 6;
	                    }else if(_week==2){
	                        cnt = 5;
	                    }else if(_week==3){
	                        cnt = 4;
	                    }else if(_week==4){
	                        cnt = 3;
	                    }else if(_week==5){
	                        cnt = 2;
	                    }else if(_week==6){
	                        cnt = 1;
	                    }
	                    cnt += 1;//加1表示以星期一为一周的第一天
	                    // 将这个长整形时间加上第N周的时间偏移
	                    time += 24*3600000; //第2周开始时间
	                     
	                    Date nextYear = new Date(year+1,0,1);
	                    int nextWeek = nextYear.getDay();
	                    int lastcnt = 0;//获取最后一周开始时间到周末的天数
	                    if(nextWeek==0){
	                        lastcnt = 6;
	                    }else if(nextWeek==1){
	                        lastcnt = 0;
	                    }else if(nextWeek==2){
	                        lastcnt = 1;
	                    }else if(nextWeek==3){
	                        lastcnt = 2;
	                    }else if(nextWeek==4){
	                        lastcnt = 3;
	                    }else if(nextWeek==5){
	                        lastcnt = 4;
	                    }else if(nextWeek==6){
	                        lastcnt = 5;
	                    }
	                     
	                    if(weeks==1){//第1周特殊处理
	                        // 为日期对象 date 重新设置成时间 time 
	                        String start = DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                        date.setTime(time-24*3600000); 
	                        return start +"--"+ DateUtil.DateToString(date,"yyyy年MM月dd日");  
	                    }else if(weeks==54){//第54周特殊处理
	                        long start = time+(weeks-2)*7*24*3600000; //第54周开始时间
	                        long end = time+(weeks-2)*7*24*3600000 + lastcnt*24*3600000 - 24*3600000; //第53周结束时间
	                        // 为日期对象 date 重新设置成时间 time 
	                        date.setTime(start);
	                        String _start = DateUtil.DateToString(date,"yyyy年MM月dd日");  
	                        date.setTime(end);
	                        String _end = DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                        return _start +"--"+ _end; 
	                    }else{
	                    	long start = time+(weeks-2)*7*24*3600000; //第n周开始时间
	                    	long end = time+(weeks-1)*7*24*3600000 - 24*3600000; //第n周结束时间
	                        // 为日期对象 date 重新设置成时间 time 
	                        date.setTime(start);
	                        String _start = DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                        date.setTime(end);
	                        String _end = DateUtil.DateToString(date,"yyyy年MM月dd日"); 
	                        return _start +"--"+ _end;
	                    }
	        }
	         
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	 /** 
     * get first date of given month and year 
     * @param year 
     * @param month 
     * @return 
     */  
    public String getFirstDayOfMonth(int year,int month){  
        String monthStr = month < 10 ? "0" + month : String.valueOf(month);  
        return year + "-"+monthStr+"-" +"01";  
    }  
      
    /** 
     * get the last date of given month and year 
     * @param year 
     * @param month 
     * @return 
     */  
    public String getLastDayOfMonth(int year,int month){  
        Calendar calendar = Calendar.getInstance();  
        calendar.set(Calendar.YEAR , year);  
        calendar.set(Calendar.MONTH , month - 1);  
        calendar.set(Calendar.DATE , 1);  
        calendar.add(Calendar.MONTH, 1);  
        calendar.add(Calendar.DAY_OF_YEAR , -1);  
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" +  
               calendar.get(Calendar.DAY_OF_MONTH);  
    }  
      
    /** 
     * get Calendar of given year 
     * @param year 
     * @return 
     */  
    private Calendar getCalendarFormYear(int year){  
        Calendar cal = Calendar.getInstance();  
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);        
        cal.set(Calendar.YEAR, year);  
        return cal;  
    }  
      
    /** 
     * get start date of given week no of a year 
     * @param year 
     * @param weekNo 
     * @return 
     */  
    public String getStartDayOfWeekNo(int year,int weekNo){  
        Calendar cal = getCalendarFormYear(year);  
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);  
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" +  
               cal.get(Calendar.DAY_OF_MONTH);      
          
    }  
      
    /** 
     * get the end day of given week no of a year. 
     * @param year 
     * @param weekNo 
     * @return 
     */  
    public String getEndDayOfWeekNo(int year,int weekNo){  
        Calendar cal = getCalendarFormYear(year);  
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);  
        cal.add(Calendar.DAY_OF_WEEK, 6);  
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" +  
               cal.get(Calendar.DAY_OF_MONTH);      
    } 
	
	
	
	
}
