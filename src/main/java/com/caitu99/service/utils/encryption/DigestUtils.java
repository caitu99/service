package com.caitu99.service.utils.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class DigestUtils {
	/**
	 * 根据给定摘要算法名称创建一个消息摘要实例
	 * 推荐使用SHA-512
	 * @param algorithm
	 *            摘要算法名
	 * @return 消息摘要实例
	 * @see MessageDigest#getInstance(String)
	 * @throws RuntimeException
	 *             当 {@link java.security.NoSuchAlgorithmException} 发生时
	 */
	static MessageDigest getDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 获取 MD5 消息摘要实例
	 * 
	 * @return MD5 消息摘要实例
	 * @throws RuntimeException
	 *             当 {@link java.security.NoSuchAlgorithmException} 发生时
	 */
	public static MessageDigest getMd5Digest() {
		return getDigest("MD5");
	}

	/**
	 * 获取 SHA-1 消息摘要实例
	 * 
	 * @return SHA-1 消息摘要实例
	 * @throws RuntimeException
	 *             当 {@link java.security.NoSuchAlgorithmException} 发生时
	 */
	public static MessageDigest getShaDigest() {
		return getDigest("SHA");
	}

	/**
	 * 获取 SHA-256 消息摘要实例
	 * 
	 * @return SHA-256 消息摘要实例
	 * @throws RuntimeException
	 *             当 {@link java.security.NoSuchAlgorithmException} 发生时
	 */
	public static MessageDigest getSha256Digest() {
		return getDigest("SHA-256");
	}

	/**
	 * 获取 SHA-384 消息摘要实例
	 * 
	 * @return SHA-384 消息摘要实例
	 * @throws RuntimeException
	 *             当 {@link java.security.NoSuchAlgorithmException} 发生时
	 */
	public static MessageDigest getSha384Digest() {
		return getDigest("SHA-384");
	}

	/**
	 * 获取 SHA-512 消息摘要实例
	 * 
	 * @return SHA-512 消息摘要实例
	 * @throws RuntimeException
	 *             当 {@link java.security.NoSuchAlgorithmException} 发生时
	 */
	public static MessageDigest getSha512Digest() {
		return getDigest("SHA-512");
	}

	/**
	 * 获取摘要的十六进制字符串
	 * @param data
	 * @return 摘要的十六进制字符串
	 */
	public static String getHexString(byte[] data) {
		return Hex.encodeHexString(data);
	}
	
	/**
	 * 密码加密
	 * @Title: jzShaEncrypt 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param password
	 * @return
	 * @date 2015年3月5日 下午2:57:44  
	 * @author lys
	 */
	public static String jzShaEncrypt(String password){
		if(null == password || "".equals(password)) return null;
		String encryptPassword = DigestUtils.getHexString(DigestUtils
				.getShaDigest().digest(password.getBytes()));
		
		return encryptPassword;
	}
	
}