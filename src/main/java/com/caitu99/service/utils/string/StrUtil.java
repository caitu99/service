package com.caitu99.service.utils.string;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtil {

	public static String URL_SEPARATOR = "/";
	public static String PATH_SEPARATOR;

	static {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			PATH_SEPARATOR = "\\";
		} else {
			PATH_SEPARATOR = "/";
		}
	}

	/**
	 * 去掉空格
	 * 
	 * @param object
	 * @return
	 */
	public static String deleteSpace(String str) {
		return str.replaceAll(" ", "").replaceAll(" ", "");
	}

	public static String deleteSpacePlus(String str) {
		return str.replace("\n", "").replace("\t", "").replaceAll(" ", "")
				.replaceAll(" ", "");
	}

	// 判断字符串是否为空
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim()))
			return true;
		return false;
	}

	public static boolean isNull(Object object) {
		if (null == object) {
			return true;
		}
		if ((object instanceof String)) {
			if ("null".equals(object)) {
				return true;
			}
			return "".equals(((String) object).trim());
		}
		return false;
	}

	public static boolean isNotNull(Object object) {
		return !isNull(object);
	}

	// MD5加密
	public static String toMD5(String str) {
		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			re_md5 = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return re_md5;
	}

	// 转化成json对象
	public static String toJsonObject(String[] keys, String[] values) {
		if (keys == null || values == null)
			return "";
		StringBuffer json = new StringBuffer("{");
		for (int i = 0; i < keys.length; i++) {
			if (i > 0)
				json.append(",");
			json.append("\"").append(keys[i]).append("\"").append(":")
					.append("\"").append(values[i]).append("\"");
		}
		return json.append("}").toString();
	}

	// 得到日期时间+随机数
	public static String getDateRandom() {
		Random random = new Random(100);
		Date date = new Date();
		int randomNum = random.nextInt();
		return date.getTime() + "_" + (randomNum < 0 ? -randomNum : randomNum);
	}

	// 得到订单编号，如：yyMMddHHmmss
	public static String getOrderNum(String... strs) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		Random random = new Random(100);
		Date date = new Date();
		if (strs.length > 0)
			return "DC" + sdf.format(date) + strs[0];
		int randomNum = random.nextInt();
		return "ED" + sdf.format(date)
				+ (randomNum < 0 ? -randomNum : randomNum);
	}

	// 得到订单编号，如：yyMMddHHmmss
	public static String getOrder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		Random random = new Random(100);
		Date date = new Date();
		int randomNum = random.nextInt(1000);
		return "" + sdf.format(date) + (randomNum < 0 ? -randomNum : randomNum);
	}

	// 将字符串转化为日期
	public static Date toDate(String str, String... strs) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				strs.length == 0 ? "yyyy-MM-dd" : strs[0]);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获得当天往后一个月的时间
	public static Date getSomeDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 30);
		return calendar.getTime();
	}

	// 获得当天往前一个月的日期
	public static Date getLastMonthDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -31);
		return calendar.getTime();
	}

	// 获得当天往前三个月的日期
	public static Date getLastSeasonDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -50);
		return calendar.getTime();
	}

	// 将日期转化为字符串
	public static String dateToString(Date date, String... strs) {
		SimpleDateFormat sdf = new SimpleDateFormat(strs == null
				|| strs.length == 0 ? "yyyy-MM-dd" : strs[0]);
		return sdf.format(date);
	}

	// 获得某天（相对今天）的日期字符串
	public static String getSomeDay(int num, String... strs) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				strs.length == 0 ? "yyyy-MM-dd" : strs[0]);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, num);
		return sdf.format(calendar.getTime());
	}

	/**
	 * 获取星期一（相对于今天）的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMondayDay(Date date) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return calendar.getTime();
	}

	/**
	 * 获取星期日（相对于今天）的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getSundayDay(Date date) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return calendar.getTime();
	}

	/**
	 * 获取当月第一天（相对于今天）的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDate(Date date) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取当月最后一天（相对于今天）的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndDate(Date date) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	// 获得某天是星期几
	public static String getWeekIndex(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int index = calendar.get(Calendar.DAY_OF_WEEK);
		switch (index) {
		case 1:
			return "周日";
		case 2:
			return "周一";
		case 3:
			return "周二";
		case 4:
			return "周三";
		case 5:
			return "周四";
		case 6:
			return "周五";
		case 7:
			return "周六";
		default:
			return "";
		}
	}

	// 将null、一个或多个空格转化为""
	public static String transfer(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		return str;
	}

	// 验证是否是邮箱
	public static boolean isEmail(String mail) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(mail);
		return matcher.matches();
	}

	// 判断字符串是否包含字母和数字
	public static boolean isContainLAndN(String str) {
		boolean isDigit = false;
		boolean isLetter = false;
		for (int i = 0; i < str.length(); i++) {
			if (isDigit && isLetter)
				return true;
			if (Character.isDigit(str.charAt(i)))
				isDigit = true;
			if (Character.isLetter(str.charAt(i)))
				isLetter = true;
		}
		return isDigit && isLetter;
	}

	public static String toNumber(String str) {
		if (StrUtil.isEmpty(str))
			return null;
		str = str.trim();
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if ((48 <= str.charAt(i) && str.charAt(i) <= 57)
					|| str.charAt(i) == 46) {
				temp.append(str.charAt(i));
			}
		}
		if (StrUtil.isEmpty(temp.toString()))
			return null;
		return temp.toString();
	}

	public static String toNumberPlus(String str) {
		if (StrUtil.isEmpty(str))
			return null;
		str = str.trim();
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if ((48 <= str.charAt(i) && str.charAt(i) <= 57)
					|| str.charAt(i) == 46 || str.charAt(i) == 45) {
				temp.append(str.charAt(i));
			}
		}
		if (StrUtil.isEmpty(temp.toString()))
			return null;
		return temp.toString();
	}

	// 获得本周一0点时间
	public static Date getTimesWeekmorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY),
				cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

	// 获得当天开始时间
	public static Date gettiimeforday() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		return currentDate.getTime();

	}

	// 增加天数
	public static Date adddate(Date date, int time) {
		// 新建一个日期格式化类的对象，该对象可以按照指定模板格式化字符串
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		// 新建一个日期对象，日期的值来源于字符串，由f将这个字符串格式化成为日期
		// 新建一个日历对象。注意：类Calendar是抽象的要使用getInstance()实例化，或者实例化其子类
		Calendar calen = Calendar.getInstance();
		// 日历对象默认的日期为当前日期，调用setTime设置该日历对象的日期为程序中指定的日期
		calen.setTime(date);
		// 将日历的"天"增加5
		calen.add(Calendar.DAY_OF_YEAR, time);
		// 获取日历对象的时间，并赋给日期对象c
		Date c = calen.getTime();
		// 用f格式化c并输出
		return c;
	}

	/**
	 * SHA1摘要
	 * 
	 * @param str
	 * @return
	 */
	public static String digestSHA1(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes("UTF-8"));
			byte[] result = md.digest();

			StringBuffer sb = new StringBuffer();

			for (byte b : result) {
				int i = b & 0xff;
				if (i < 0xf) {
					sb.append(0);
				}
				sb.append(Integer.toHexString(i));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 判断字符串是否是6个数字
	 * @param string
	 * @return
	 */
	public static boolean isSixNums(String string)
	{
		if(string==null) {
			return false;
		}
		char [] chars = string.toCharArray();
		boolean b = true;
		if(chars.length != 6)
		{
			return false;
		}
		else {
			for(char ch:chars)
			{
				if(ch < 0x30 || ch > 0x3A)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 判断是否为电话
	 * @author	熊斌	2015-2-9 下午02:17:45
	 * @param tel
	 * @return	true:是
	 */
	public static boolean isTel(String tel){
		if(tel == null) return false;
		int charcode;
		for(int i=0;i<tel.length();i++){
			charcode = tel.codePointAt(i);
			//System.out.println(charcode);
			if(charcode<48 && charcode!=45 || charcode>57){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断是否为手机号
	 * @author	熊斌	2015-4-2 上午11:01:08
	 * @param phone
	 * @return	true:是
	 */
	public static boolean isPhone(String phone){
		if(phone == null) return false;
		if(phone.length() != 11) return false;
		
		return isTel(phone);
	}
	
	/**
	 * 手机加密(加密中间4位)
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: phoneEncrypt 
	 * @param phone
	 * @return		151****6726
	 * @date 2016年2月23日 下午4:48:27  
	 * @author xiongbin
	 */
	public static String phoneEncrypt(String phone){
		if(!isPhone(phone)){
			return phone;
		}
		
		StringBuffer string = new StringBuffer();
		string.append(phone.substring(0,3))
				.append("****")
				.append(phone.substring(7,phone.length()));
		
		return string.toString();
	}
	
	/**
	 * 邮箱加密
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: emailEncrypt 
	 * @param mail
	 * @return	myx***@qq.com
	 * @date 2016年2月23日 下午5:01:22  
	 * @author xiongbin
	 */
	public static String emailEncrypt(String mail){
		if(!isEmail(mail)){
			return mail;
		}
		
		StringBuffer string = new StringBuffer();
		String[] str = mail.split("\\@");
		string.append(str[0].substring(0,3))
				.append("***")
				.append("@")
				.append(str[1]);
		
		return string.toString();
	}
	

	public static void main(String[] args) throws Exception {
//		System.out.println("URL_SEPARATOR = " + URL_SEPARATOR);
//		System.out.println("PATH_SEPARATOR = " + PATH_SEPARATOR);
		System.out.println(emailEncrypt("myxiongbin@qq.com"));
		
		String loginAccount = "12334567890";
		loginAccount = loginAccount.substring(0, 3) + "*****" + loginAccount.substring(loginAccount.length()-3, loginAccount.length());
		System.out.println(loginAccount);
	}


}
