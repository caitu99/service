package com.caitu99.service.utils.encryption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import sun.misc.BASE64Decoder;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.ParsingException;

/**
 * 基于证书的加密解密
 * @ClassName: SignUtil 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2014年8月30日 上午11:28:31
 */
@SuppressWarnings("all")
public class SignUtil {

	/**
	 * 证书签名
	 * 
	 * @Title: getSign
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param verifyIp
	 * @param verifyPort
	 * @param plantString
	 * @return
	 * @date 2014年8月30日 上午9:56:24
	 * @author Administrator
	 */
	public static String getSign(String verifyIp, int verifyPort, String plaintext)
			throws Exception {
		Socket socket = null;
		BufferedWriter wr = null;
		BufferedReader rd = null;
		try {
			socket = new Socket(InetAddress.getByName(verifyIp), verifyPort);
			wr = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			wr.write(plaintext);
			wr.flush();
			rd = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != wr) {
				wr.close();
			}
			if (null != rd) {
				rd.close();
			}
			if (null != socket) {
				socket.close();
			}
		}
	}
	
	/**
	 * 返回密文的解密
	 * @Title: jieMi 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param miwen
	 * @return
	 * @throws Exception
	 * @date 2014年8月30日 上午11:24:25  
	 * @author Administrator
	 */
	public static String jieMi(String miwen) throws Exception {
        try {
        	PKCS7 p7 = new PKCS7(new BASE64Decoder().decodeBuffer(miwen));
			return new String(p7.getContentInfo().getContentBytes(),"utf-8");
		} catch (Exception e) {
			throw e;
		}
	}
	
}
