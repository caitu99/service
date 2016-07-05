package com.caitu99.service.utils.crypto;

import org.apache.commons.lang.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public class AESCryptoUtil {

	public static final String DEFAULT_CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding"; // AES/ECB/PKCS5Padding

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private static String seed = "caitu99.com.key.888";

	private static String parameterSpec;

	private static SecretKey secretKey;

	private static SecretKeySpec secretKeySpec;

	private static SecureRandom secureRandom;

	private static String charsetName = "UTF-8";

	private static Charset charset;

	private static String transformation;

	private static AlgorithmParameterSpec algorithmParameterSpec;

	public static void init() throws CryptoException {
		if (null == charsetName) {
			charset = DEFAULT_CHARSET;
		} else {
			charset = Charset.forName(charsetName);
		}
		if (null == transformation) {
			transformation = DEFAULT_CIPHER_TRANSFORMATION;
		}

		KeyGenerator keyGenerator;
		try {
			// 为指定算法生成一个密钥生成器对象。
			keyGenerator = KeyGenerator.getInstance("AES");
			// 使用用户提供的随机源初始化此密钥生成器，使其具有确定的密钥长度。
			secureRandom = SecureRandom.getInstance("SHA1PRNG");// new
																// SecureRandom("");

			secureRandom.setSeed(seed.getBytes(charset));
			keyGenerator.init(128, secureRandom);
			// 使用KeyGenerator生成（对称）密钥。
			secretKey = keyGenerator.generateKey();
			// byte[] keyBytes = secretKey.getEncoded();
			// secretKeySpec = new SecretKeySpec(keyBytes, "AES");
			// 生成一个实现指定转换的 Cipher 对象
			// algorithmParameterSpec = new
			// IvParameterSpec(getParameterSpec().getBytes(getCharset()));
			// ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoException(e);
		}
	}

	public static String encrypt(String msg) throws CryptoException {
		if(!StringUtils.isEmpty(msg)) {
			try {
				init();
				// 用密钥和一组算法参数初始化此 cipher
				Cipher ecipher;
				ecipher = Cipher.getInstance(transformation);// 创建密码器
	
				byte[] enCodeFormat = secretKey.getEncoded();
				SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");
	
				// ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
				ecipher.init(Cipher.ENCRYPT_MODE, keySpec);
	
				byte[] bs = ecipher.doFinal(msg.getBytes(charset));// 加密
				sun.misc.BASE64Encoder base64Encoder = new sun.misc.BASE64Encoder();
				return base64Encoder.encode(bs);
			} catch (BadPaddingException e) {
				throw new CryptoException(e);
			} catch (InvalidKeyException e) {
				throw new CryptoException(e);
			} catch (IllegalBlockSizeException e) {
				throw new CryptoException(e);
			} catch (NoSuchAlgorithmException e) {
				throw new CryptoException(e);
			} catch (NoSuchPaddingException e) {
				throw new CryptoException(e);
			}
		} else {
			return "";
		}
	}

	public static String decrypt(String value) throws CryptoException {
		if(!StringUtils.isEmpty(value)) {
			try {
				init();
				sun.misc.BASE64Decoder base64Decoder = new sun.misc.BASE64Decoder();
				byte[] bs = base64Decoder.decodeBuffer(value);
				Cipher ecipher;
				ecipher = Cipher.getInstance(transformation);// 创建密码器
	
				byte[] enCodeFormat = secretKey.getEncoded();
				SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");
	
				// ecipher.init(Cipher.DECRYPT_MODE, secretKey);
				ecipher.init(Cipher.DECRYPT_MODE, keySpec);
				bs = ecipher.doFinal(bs);
				return new String(bs, charset);
			} catch (BadPaddingException e) {
				throw new CryptoException(e);
			} catch (InvalidKeyException e) {
				throw new CryptoException(e);
			} catch (IllegalBlockSizeException e) {
				throw new CryptoException(e);
			} catch (NoSuchAlgorithmException e) {
				throw new CryptoException(e);
			} catch (NoSuchPaddingException e) {
				throw new CryptoException(e);
			} catch (IOException e) {
				throw new CryptoException(e);
			}
		} else {
			return "";
		}
	}

	public static void main(String[] args) throws CryptoException {
		System.out.println(AESCryptoUtil.decrypt("g/rqP4zSSYUL+X2Au2qcKg=="));
		System.out.println(AESCryptoUtil.decrypt("07m9z3ozhIwd4SfOVVri2w=="));
	}
}
