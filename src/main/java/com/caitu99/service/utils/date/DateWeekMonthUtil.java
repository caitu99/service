/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.date;

import java.util.Calendar;
import java.util.Date;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: DateWeekMonthUtil 
 * @author ws
 * @date 2016年6月21日 上午9:42:43 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class DateWeekMonthUtil {

	
	 /** 
    * get first date of given month and year 
    * @param year 
    * @param month 
    * @return 
    */  
   public static String getFirstDayOfMonth(int year,int month){  
       String monthStr = month < 10 ? "0" + month : String.valueOf(month);  
       return year + "-"+monthStr+"-" +"01";  
   }  
     
   /** 
    * get the last date of given month and year 
    * @param year 
    * @param month 
    * @return 
    */  
   public static String getLastDayOfMonth(int year,int month){  
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
   private static Calendar getCalendarFormYear(int year){  
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
   public static String getStartDayOfWeekNo(int year,int weekNo){  
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
   public static String getEndDayOfWeekNo(int year,int weekNo){  
       Calendar cal = getCalendarFormYear(year);  
       cal.set(Calendar.WEEK_OF_YEAR, weekNo);  
       cal.add(Calendar.DAY_OF_WEEK, 6);  
       return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" +  
              cal.get(Calendar.DAY_OF_MONTH);      
   } 
	
   

   /** 
    * get start date of given week no of a year 
    * @param year 
    * @param weekNo 
    * @return 
    */  
   public static Date getStartDateOfWeekNo(int year,int weekNo){  
       Calendar cal = getCalendarFormYear(year);  
       cal.set(Calendar.WEEK_OF_YEAR, weekNo);  
       return cal.getTime();      
         
   }  
     
   /** 
    * get the end day of given week no of a year. 
    * @param year 
    * @param weekNo 
    * @return 
    */  
   public static Date getEndDateOfWeekNo(int year,int weekNo){  
       Calendar cal = getCalendarFormYear(year);  
       cal.set(Calendar.WEEK_OF_YEAR, weekNo);  
       cal.add(Calendar.DAY_OF_WEEK, 6);  
       return cal.getTime();
   } 
	
}
