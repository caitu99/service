
package com.caitu99.service.utils.encryption;

import java.io.UnsupportedEncodingException;

public class Base64 {

	/**
	 * 64位编码
	 * @Title: getBASE64 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param s
	 * @return
	 * @date 2015年3月5日 下午1:53:25  
	 * @author lys
	 */
	public static String getBASE64(String s) {
		if (s == null) {
			return null;
		}
		try {
			return getBASE64(s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 64位编码
	 * @Title: getBASE64 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param b
	 * @return
	 * @date 2015年3月5日 下午1:53:49  
	 * @author lys
	 */
	public static String getBASE64(byte[] b) {
		byte[] rb = org.apache.commons.codec.binary.Base64.encodeBase64(b);
		if (rb == null) {
			return null;
		}
		try {
			return new String(rb, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 64位编码
	 * @Title: getFromBASE64 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param s
	 * @return
	 * @date 2015年3月5日 下午1:53:53  
	 * @author lys
	 */
	public static String getFromBASE64(String s) {
		if (s == null) {
			return null;
		}
		try {
			byte[] b = getBytesBASE64(s);
			if (b == null) {
				return null;
			}
			return new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 64位编码
	 * @Title: getBytesBASE64 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param s
	 * @return
	 * @date 2015年3月5日 下午1:53:57  
	 * @author lys
	 */
	public static byte[] getBytesBASE64(String s) {
		if (s == null) {
			return null;
		}
		try {
			byte[] b = org.apache.commons.codec.binary.Base64.decodeBase64(s
					.getBytes("UTF-8"));
			return b;
		} catch (Exception e) {
			return null;
		}
	}
}
