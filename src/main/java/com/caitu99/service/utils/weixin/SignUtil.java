package com.caitu99.service.utils.weixin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求校验工具类
 * 
 */
public class SignUtil {
	// 与接口配置信息中的Token要一致
	//private static String token = "weixinCourse";//放出来让商务人员填写(可以是商铺号)
	private static String token = "caitu99weixin";//ShopNo
	/**
	 * 验证签名
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean checkSignature(String signature, String timestamp, String nonce) {
		String[] arr = new String[] { token, timestamp, nonce };
		// 将token、timestamp、nonce三个参数进行字典序排序
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md = null;
		String tmpStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			// 将三个参数字符串拼接成一个字符串进行sha1加密
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		content = null;
		// 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
		return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}

	/**
	 * 生成JS-SDK权限验证的签名
	 * @Title: getSignatureForWechatJs 
	 * @Description: (生成JS-SDK权限验证的签名) 
	 * @param jsapi_ticket
	 * @return
	 * @date 2015年2月5日 下午1:57:57  
	 * @author ah
	 */
	public static String getSignatureForWechatJs(String noncestr, String jsapiTicket,
			String timestamp, String url) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("noncestr", noncestr);
		params.put("jsapi_ticket", jsapiTicket);
		params.put("timestamp", timestamp);
		params.put("url", url);
		List<String> keyList = Arrays.asList(params.keySet().toArray(new String[params.size()]));
		Collections.sort(keyList);
		// 拼接签名参数
		StringBuilder sb = new StringBuilder();
		for (String k : keyList) {
			sb.append(k).append("=").append(params.get(k)).append("&");
		}
		if (params.size() > 0)
			sb.delete(sb.length() - "&".length(), sb.length());
		
		return sb.toString();
	}
	
	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param byteArray
	 * @return
	 */
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	/**
	 * 将字节转换为十六进制字符串
	 * 
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];

		String s = new String(tempArr);
		return s;
	}
}
