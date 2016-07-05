/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.encryption;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.AppConfig;
import com.caitu99.service.utils.encryption.DESUtil;
import com.caitu99.service.utils.encryption.RSACoder;
import com.caitu99.service.utils.weixin.WeixinUtil;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: WeixinUtilTest 
 * @author xiongbin
 * @date 2015年12月7日 上午11:48:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class EncryptionTest extends AbstractJunit {

	@Autowired
	private AppConfig appConfig;
	
//	@Test
//	public void test(){
//		String publicKey = appConfig.publicKey;
//		
//		String no = "1";
//		String content = "userid:336,no:" + no;
//		
//		try {
//			byte[] encodedData = RSACoder.encryptByPublicKey(content.getBytes(), publicKey);  
//			String encodedString = new String(encodedData,"iso-8859-1");
//			QRCodeUtil.encode(encodedString , "C:\\Users\\Lenovo\\Desktop\\lALOCST_Dc0EAM0EAA_1024_1024.png","D:\\", true,no);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void test2(){
//		String privateKey = appConfig.privateKey;
//		
//		try {
//			 byte[] decodedData = RSACoder.decryptByPrivateKey("&ÿ=·UÉÖÂÝb".getBytes("iso-8859-1"),privateKey);  
//			 String outputStr = new String(decodedData);  
//			 System.out.println(outputStr);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//
//	@Test
//	public void test3(){
//		try {
//			String key = appConfig.encryptionKey;
//			String encryptData = DESUtil.encrypt("abc", key);
//			String no = "1";
//			
//			QRCodeUtil.encode(encryptData, "C:\\Users\\Lenovo\\Desktop\\lALOCST_Dc0EAM0EAA_1024_1024.png","D:\\", true,no);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
