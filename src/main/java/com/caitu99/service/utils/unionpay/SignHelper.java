package com.caitu99.service.utils.unionpay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关签名Demo
 *
 * @author dy
 *
 */
public class SignHelper {

	/**
	 * 编码
	 */
	private static String ENCODE = "utf-8";

	public static void main(String[] args) {

		try {

			Map<String, String> param = new HashMap<String, String>();

			String secretKey = "4f34PekY5RkKDs3K8zk2W5t5XatG6aT5";

			param.put("merId", "1000000078");
			param.put("orderNo", "F1000000001");
			param.put("cardNo", "6222021202012588888");
			param.put("accName", "李明");
			param.put("accId", "33010219811025XXXX");
			param.put("timestamp", System.currentTimeMillis() + "");
			param.put("tranDateTime", "20141219100150");

			String waitSign = SignHelper.sortParamsToSign(param) + secretKey;
			System.out.println("获取待签名串:" + waitSign);

			String sign = SignHelper.MD5LowerCase(waitSign);
			param.put("sign", sign);
			System.out.println("获取签名:" + sign);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * MD5加密处理
	 *
	 * @param src
	 * @return
	 */
	public final static String MD5LowerCase(String src) {

		StringBuffer buf = new StringBuffer("");

		try {
			// 获取MD5摘要算法对象
			MessageDigest digest = MessageDigest.getInstance("MD5");

			// 使用指定的字节更新摘要
			digest.update(src.getBytes(ENCODE));

			// 获取密文
			byte[] b = digest.digest();

			// 将密文转换成16进制的字符串形式
			int i = 0;

			for (int offset = 0; offset < b.length; offset++) {

				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return buf.toString();
	}

	/**
	 * 将所有参数值按升序排序
	 *
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String sortParamsToSign(Map<String, String> params)
			throws UnsupportedEncodingException {

		// 按参数名字典排序
		List<String> valList = Arrays.asList(params.keySet().toArray(
				new String[params.size()]));
		Collections.sort(valList);

		StringBuilder sb = new StringBuilder();

		for (String k : valList) {

			// 跳过 不被签名参数
			if (k.equals("sign")) {
				continue;
			}
			sb.append(k).append("=")
					.append(URLEncoder.encode(params.get(k), ENCODE))
					.append("&");
		}
		if (params.size() > 1)
			sb.delete(sb.length() - 1, sb.length()); // 去掉最后一个字符
		return sb.toString();
	}

}
