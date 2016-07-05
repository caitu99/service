package com.caitu99.service.utils.encryption.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 32位MD5摘要算法
 * @author shmily
 * @date 2012-6-28 下午02:34:45
 */
public class MD5Util {

	private static MD5Util instance;
	
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	private MD5Util(){
		
	}
	
	public static MD5Util getInstance(){
		if(null == instance)
			return new MD5Util();
		return instance;
	}
	
	/**
	 * 转换字节数组�?6进制字串
	 * @param b 字节数组
	 * @return 16进制字串
	 */
	private String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * 转换字节数组为高位字符串
	 * @param b 字节数组
	 * @return
	 */
	private String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * MD5 摘要计算(byte[]).
	 * @param src byte[]
	 * @throws Exception
	 * @return String
	 */
	public String md5Digest(byte[] src) {
		MessageDigest alg;
		try {
			// MD5 is 32 bit message digest
			alg = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		} 
		return byteArrayToHexString(alg.digest(src));
	}
	
	public String encryptionMD5(String plainText){
		if (null == plainText || "".equals(plainText)) {
			return null;
		}
		try {
			return md5Digest(plainText.getBytes());
		} catch (Exception e) {
			throw  new RuntimeException("MD5加密签名时失败",e);
		}
	}
	/**
	 * 原文加密(参数环绕加密)
	 * @Title: encryptionMD5Around 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @paramparamMapp
	 * @param md5Key
	 * @return
	 * @date 2014年5月6日 下午3:54:49  
	 * @author Administrator
	 */
	public String encryptionMD5Around(Map<String, String> paramMap,String md5Key){
		if (null == paramMap || paramMap.isEmpty()) {
			return null;
		}
		try {
			StringBuffer signbuffer = new StringBuffer();
			for (String key : paramMap.keySet()) {
				signbuffer.append(paramMap.get(key));
			}
			return this.encryptionMD5Around(signbuffer.toString(), md5Key);
		} catch (Exception e) {
			throw  new RuntimeException("MD5加密签名时失败",e);
		}
	}
	/**
	 * 原文加密(参数环绕加密)
	 * @Title: encryptionMD5Around 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param p
	 * @param md5Key
	 * @return
	 * @date 2014年5月6日 下午3:54:49  
	 * @author Administrator
	 */
	public String encryptionMD5Around(String plainText,String md5Key){
		if (null == plainText || "".equals(plainText)) {
			return null;
		}
		try {
			StringBuffer signbuffer = new StringBuffer(md5Key);
			signbuffer.append(plainText).append(md5Key);
			return md5Digest(signbuffer.toString().getBytes("utf-8"));
		} catch (Exception e) {
			throw  new RuntimeException("MD5加密签名时失败",e);
		}
	}
	
	/**
	 * 原文加密(在参数之后加密)
	 * @Title: encryptionMD5 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param p
	 * @param md5Key
	 * @return
	 * @date 2014年5月6日 下午3:54:49  
	 * @author Administrator
	 */
	public String encryptionMD5After(String plainText,String md5Key){
		if (null == plainText || "".equals(plainText)) {
			return null;
		}
		StringBuffer signbuffer = new StringBuffer(plainText);
		signbuffer.append("&key=").append(md5Key);
		try {
			return md5Digest(signbuffer.toString().getBytes("utf-8"));
		} catch (Exception e) {
			throw  new RuntimeException("MD5加密签名时失败",e);
		}
	}
	
	public static void main(String[] args) {
		String plainText="amt_order=1&busi_code=phonevalue_yd&imei_request=1234567890123456789012345678901234567890&mb_cust=15700114735&money_order=100&no_order=10001314050714999086&oid_chn=N&oid_goodsitem=100849&own_goods=浙江移动&pay_ chn=16&sign_type=MD5&transcode=5020&type_goodsbill=0";
		String signString=MD5Util.getInstance().encryptionMD5After(plainText, "jinzheng_0506");
		System.out.println(signString);
	}
	
}
