package com.caitu99.service.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class XStringUtil {

	// 获得当天开始时间
	public static Date getBeginOfToday() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		return currentDate.getTime();
	}

	// 将日期转化为字符串
	public static String dateToString(Date date, String... strs) {
		SimpleDateFormat sdf = new SimpleDateFormat(strs == null
				|| strs.length == 0 ? "yyyy-MM-dd" : strs[0]);
		return sdf.format(date);
	}

	// 得到订单编号，如：yyMMddHHmmss
	public static String getOrder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		Random random = new Random(100);
		Date date = new Date();
		int randomNum = random.nextInt(1000);
		return "" + sdf.format(date) + (randomNum < 0 ? -randomNum : randomNum);
	}

	// 获得当天往前50天的日期
	public static Date getLastSeasonDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -50);
		return calendar.getTime();
	}

	/**
	 * 
	 * 
	 * @Description: (创建流水号)
	 * @Title: createSerialNo
	 * @param prefix
	 * @param userid
	 * @return
	 * @date 2015年12月2日 下午12:07:40
	 * @author lhj
	 */
	public static String createSerialNo(String prefix, String id) {
		StringBuffer sb = new StringBuffer(prefix);
		return sb.append(String.valueOf(new Date().getTime())).append("_")
				.append(id).toString();
	}
	/**
	 * @Description: (变现流水号，只能小于16位)  
	 * @Title: createWithdrawSerialNo 
	 * @param prefix
	 * @param id
	 * @return
	 * @date 2016年4月7日 下午5:23:14  
	 * @author Hongbo Peng
	 */
	public static String createWithdrawSerialNo(String prefix, Long id) {
		StringBuffer sb = new StringBuffer(prefix);
		Integer start = new Random().nextInt(999);
		if(start < 100){
			start += 100; 
		}
		Integer end = new Random().nextInt(999);
		if(end < 100){
			end += 100; 
		}
		long i = id.longValue();
		if(i < 10000000){
			i += 10000000;
		}
		return sb.append(start).append(end).append(i).toString();
	}
	
	public static void main(String[] args) {
		System.out.println(XStringUtil.createWithdrawSerialNo("SP", 1L));
	}

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: createSerialNoWithRandom 
	 * @param string
	 * @param valueOf
	 * @return
	 * @date 2016年1月21日 下午12:30:53  
	 * @author ws
	*/
	public static String createSerialNoWithRandom(String prefix, String id) {
		StringBuffer sb = new StringBuffer(prefix);
		Random random = new Random();
		return sb.append(random.nextInt(10))
				.append(System.currentTimeMillis())
				.append("_")
				.append(random.nextInt(10))
				.append(id).toString();
	}

}
