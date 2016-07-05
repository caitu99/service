package com.caitu99.service.utils.encryption.rsa;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caitu99.service.utils.encryption.Base64;

/**
 * RSA签名公共类
 * 
 * @author dzq
 */
public class RSAUtil {

	private final static Logger log = LoggerFactory.getLogger(RSAUtil.class);
	private static RSAUtil instance = new RSAUtil();

	private RSAUtil() {
	}

	public static RSAUtil getInstance() {
		return instance;
	}

	/**
	 * 签名处理
	 * 
	 * @param prikeyvalue
	 *            ：私钥文件
	 * @param sign_str
	 *            ：签名源内容
	 * @return
	 */
	public static String sign(String prikeyvalue, String sign_str) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.getBytesBASE64(prikeyvalue));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey myprikey = keyf.generatePrivate(priPKCS8);
			// 用私钥对信息生成数字签名
			java.security.Signature signet = java.security.Signature
					.getInstance("MD5withRSA");
			signet.initSign(myprikey);
			signet.update(sign_str.getBytes("UTF-8"));
			byte[] signed = signet.sign(); // 对信息的数字签名
			return new String(
					org.apache.commons.codec.binary.Base64.encodeBase64(signed));
		} catch (java.lang.Exception e) {
			log.error("签名失败," + e.getMessage());
		}
		return null;
	}

	/**
	 * 签名验证
	 * 
	 * @param pubkeyvalue
	 *            ：公钥
	 * @param oid_str
	 *            ：源串
	 * @param signed_str
	 *            ：签名结果串
	 * @return
	 */
	public static boolean checksign(String pubkeyvalue, String oid_str,
			String signed_str) {
		try {
			X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
					Base64.getBytesBASE64(pubkeyvalue));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
			byte[] signed = Base64.getBytesBASE64(signed_str);// 这是SignatureData输出的数字签名
			java.security.Signature signetcheck = java.security.Signature
					.getInstance("MD5withRSA");
			signetcheck.initVerify(pubKey);
			signetcheck.update(oid_str.getBytes("UTF-8"));
			return signetcheck.verify(signed);
		} catch (Exception e) {
			log.error("签名验证异常," + e.getMessage());
			log.error("原文：" + oid_str);
			log.error("密文：" + signed_str);
		}
		return false;
	}

	public static void main(String[] args) {
		// 商户（RSA）私钥 TODO 强烈建议将私钥
		String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM7BtISEErbKGNcskmkyB/i25xZKVh+kIeowfeWsbAA5eBJ1xoa682T3Qz4CWuz/t4mBczbvQkLUsi0+OzMGRG8+7aoWmW7E73C+BRy6i3LfvWZ4u7Z+2OH3h4g7ObFIcTeJa54VvPlkisX9Zko2b09BhtZUYgR0dc8mNpCmRqwjAgMBAAECgYEAkO6QD+RVCfUY/Jyt9TexBtOPobxyKrPvYi6j0f/PpUijtq0AgSlDvJ7nb+xOuJt4mNc5YGTPWfGnBLf+34GhLed/8Y2xNTqFfOg9Jg12pdWrmnFs5F2kLOr6eR/CfYFFqMHQ9Z9+JQciiD5BXmY/8Do3j1azNotwpgpWojdx3AECQQDt+qYgOQyQYo62ZOIyKpEBvEHASAiK2Nfzlw8Kl7nIbVX9pGNkfeDkN7zs6wcV7n+4hs2oMTShg5YrU0BHvxmBAkEA3mnK74SiEUXCR39WlMpAMkwJefoT8BL4p/8pckdZ46Ix+JaSzU+QqMLBjclczp6CZmKRrSKKEGG7HUcO78rvowJAc9FTfkUldzNwDxZj+1Q6BCUxvrmP5rsHxlYTDO2wjfmgKvQRJzwX8hmqSYdMiIDtCcoZVqyz15Mpx2YZ15EKgQJAeeNof9MkLmsYia5TeL9OZ0Icf2h5vLvo4ciIokRQEtw0npOGaFYOZS42fMm5vtJHjGzAgS3IlCm7LdRfbzK8GQJBALSXc599v6NDVvbgjpxD6ayI/wst7DTE2ErbEUwv25RGmyPnGodm7YJyemuOdyShNLNJfA8lGDnJNohNREIegmw=";
		// 银通支付（RSA）公钥
		String RSA_YT_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOwbSEhBK2yhjXLJJpMgf4tucWSlYfpCHqMH3lrGwAOXgSdcaGuvNk90M+Alrs/7eJgXM270JC1LItPjszBkRvPu2qFpluxO9wvgUcuoty371meLu2ftjh94eIOzmxSHE3iWueFbz5ZIrF/WZKNm9PQYbWVGIEdHXPJjaQpkasIwIDAQAB";
		// RSAUtil.getInstance().generateKeyPair("D:\\CertFiles\\inpour\\",
		// "ll_yt");
		String sign = RSAUtil
				.sign(RSA_PRIVATE,
						"app_request=3&busi_partner=101001&dt_order=20140423163028&money_order=0.01&no_order=10000000000002115468&notify_url=http://localhost:8080/jzwgj/LianLian/payConsumerAsyn.htm&oid_partner=201404171000001184&sign_type=RSA&url_return=http://localhost:8080/jzwgj/LianLian/payConsumer.htm&user_id=1&version=1.1");

		System.out.println(sign);
		System.out
				.println(RSAUtil
						.checksign(
								RSA_YT_PUBLIC,
								"app_request=3&busi_partner=101001&dt_order=20140423163028&money_order=0.01&no_order=10000000000002115468&notify_url=http://localhost:8080/jzwgj/LianLian/payConsumerAsyn.htm&oid_partner=201404171000001184&sign_type=RSA&url_return=http://localhost:8080/jzwgj/LianLian/payConsumer.htm&user_id=1&version=1.1",
								sign));
	}
}
