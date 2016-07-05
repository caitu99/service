/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28      MPI工具类
 * =============================================================================
 */
package com.caitu99.service.utils.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class SDKUtil {
	
	//默认配置的是UTF-8
	public static String encoding_UTF8 = "UTF-8";
	
	public static String encoding_GBK = "GBK";
	//全渠道固定值
	public static String version = "5.0.0";
	
	/**
	 * 功能：对请求报文进行签名
	 * @param contentData 请求报文map
	 * @param encoding 上送请求报文域encoding字段的值
	 * @return　签名后的map对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> signData(Map<String, ?> contentData,String encoding) {
		Entry<String, String> obj = null;
		Map<String, String> submitFromData = new HashMap<String, String>();
		for (Iterator<?> it = contentData.entrySet().iterator(); it.hasNext();) {
			obj = (Entry<String, String>) it.next();
			String value = obj.getValue();
			if (StringUtils.isNotBlank(value)) {
				// 对value值进行去除前后空处理
				submitFromData.put(obj.getKey(), value.trim());
				System.out
						.println(obj.getKey() + "-->" + String.valueOf(value));
			}
		}
		
		SDKUtil.sign(submitFromData, encoding);
		return submitFromData;
	}
	
	
	/**
	 * 多证书签名方法
	 * 如果有多个商户号接入银联,每个商户号对应不同的证书可以使用此方法:传入私钥证书和密码(并且在acp_sdk.properties中 配置 acpsdk.singleMode=false) 
	 * @param contentData 请求报文map
	 * @param certPath 签名私钥文件（带路径）
	 * @param certPwd 签名私钥密码
	 * @param encoding 上送请求报文域encoding字段的值
	 * @return　签名后的map对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> signData(Map<String, ?> contentData,String certPath, 
			String certPwd,String encoding) {
		Entry<String, String> obj = null;
		Map<String, String> submitFromData = new HashMap<String, String>();
		System.out.println("打印请求报文域 :");
		for (Iterator<?> it = contentData.entrySet().iterator(); it.hasNext();) {
			obj = (Entry<String, String>) it.next();
			String value = obj.getValue();
			if (StringUtils.isNotBlank(value)) {
				// 对value值进行去除前后空处理
				submitFromData.put(obj.getKey(), value.trim());
				System.out
						.println(obj.getKey() + "-->" + String.valueOf(value));
			}
		}
		/**
		 * 签名
		 */
		SDKUtil.signByCertInfo(submitFromData, encoding,certPath, certPwd);

		return submitFromData;
	}
	

	/**
	 * 功能：后台交易给银联地址发请求
	 * @param contentData 请求报文map
	 * @param encoding 上送请求报文域encoding字段的值
	 * @return 返回报文 map
	 */
	public static Map<String, String> submitUrl(
			Map<String, String> submitFromData,String requestUrl,String encoding) {
		String resultString = "";
		LogUtil.writeLog("请求银联地址:" + requestUrl);
		/**
		 * 发送后台请求数据
		 */
		HttpClient hc = new HttpClient(requestUrl, 30000, 30000);
		try {
			int status = hc.send(submitFromData, encoding);
			if (200 == status) {
				resultString = hc.getResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, String> resData = new HashMap<String, String>();
		/**
		 * 验证签名
		 */
		if (null != resultString && !"".equals(resultString)) {
			// 将返回结果转换为map
			resData = SDKUtil.convertResultStringToMap(resultString);
			if (SDKUtil.validate(resData, encoding)) {
				LogUtil.writeLog("验证签名成功,可以继续后边的逻辑处理");
			} else {
				LogUtil.writeLog("验证签名失败,必须验签签名通过才能继续后边的逻辑...");
			}
		}
		return resData;
	}

	/**
	 * 功能：后台交易给银联地址发请求
	 * @param contentData
	 * @return 返回报文 map
	 */
	public static Map<String, String> submitDate(Map<String, ?> contentData,String requestUrl,String encoding) {
		Map<String, String> submitFromData = (Map<String, String>)signData(contentData,encoding);
		return submitUrl(submitFromData,requestUrl,encoding);
	}
	
	
	/**
	 * 验证签名(SHA-1摘要算法)<br>
	 * @param resData 返回报文数据<br>
	 * @param encoding 上送请求报文域encoding字段的值<br>
	 * @return
	 */
	public static boolean validate(Map<String, String> resData, String encoding) {
		LogUtil.writeLog("验签处理开始");
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String stringSign = resData.get(SDKConstants.param_signature);

		// 从返回报文中获取certId ，然后去证书静态Map中查询对应验签证书对象
		String certId = resData.get(SDKConstants.param_certId);
		
		LogUtil.writeLog("对返回报文串验签使用的验签公钥序列号：["+certId+"]");
		
		// 将Map信息转换成key1=value1&key2=value2的形式
		String stringData = coverMap2String(resData);

		LogUtil.writeLog("待验签返回报文串：["+stringData+"]");
		
		try {
			// 验证签名需要用银联发给商户的公钥证书.
			return SecureUtil.validateSignBySoft(CertUtil
					.getValidateKey(certId), SecureUtil.base64Decode(stringSign
					.getBytes(encoding)), SecureUtil.sha1X16(stringData,
					encoding));
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * 功能：前台交易构造HTTP POST交易表单的方法示例<br>
	 * @param action 表单提交地址<br>
	 * @param hiddens 以MAP形式存储的表单键值<br>
	 * @param encoding 上送请求报文域encoding字段的值<br>
	 * @return 构造好的HTTP POST交易表单<br>
	 */
	public static String createAutoFormHtml(String action, Map<String, String> hiddens,String encoding) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + action
				+ "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, String>> set = hiddens.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}

	/**
	 * 功能：将批量文件内容并返回使用DEFLATE压缩算法压缩，Base64编码生成字符串
	 * 适用到的交易：批量代付，批量代收，批量退货
	 * @param filePath 批量文件路径
	 * @return
	 */
	public static String  enCodeFileContent(String filePath){
		String baseFileContent = "";
		
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			int fl = in.available();
			if (null != in) {
				byte[] s = new byte[fl];
				in.read(s, 0, fl);
				// 压缩编码.
				baseFileContent = new String(SecureUtil.base64Encode(SecureUtil.deflater(s)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return baseFileContent;
	}
	
	/**
	 * 功能：解析交易返回的fileContent字符串并落地 （ 解base64，解DEFLATE压缩并落地）<br>
	 * 适用到的交易：对账文件下载，批量交易状态查询<br>
	 * @param resData<br>
	 * @param encoding 上送请求报文域encoding字段的值<br>	
	 */
	public static void deCodeFileContent(Map<String, String> resData,String filePathRoot,String encoding) {
		// 解析返回文件
		String fileContent = resData.get(SDKConstants.param_fileContent);
		if (null != fileContent && !"".equals(fileContent)) {
			try {
				byte[] fileArray = SecureUtil.inflater(SecureUtil
						.base64Decode(fileContent.getBytes(encoding)));
				String filePath = null;
				if (SDKUtil.isEmpty(resData.get("fileName"))) {
					filePath = filePathRoot + File.separator + resData.get("merId")
							+ "_" + resData.get("batchNo") + "_"
							+ resData.get("txnTime") + ".txt";
				} else {
					filePath = filePathRoot + File.separator + resData.get("fileName");
				}
				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file);
				out.write(fileArray, 0, fileArray.length);
				out.flush();
				out.close();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将结果文件内容 转换成字符串：解base64,解压缩
	 * @param fileContent
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String getFileContent(String fileContent,String encoding) throws UnsupportedEncodingException, IOException{
		return new String(SecureUtil.inflater(SecureUtil.base64Decode(fileContent.getBytes())),encoding);
	}
	
	
	/**
	 * 持卡人信息域customerInfo构造<br>
	 * 说明：不勾选对敏感信息加密权限 使用旧的构造customerInfo域方式，不对敏感信息进行加密（对cvn2，有效期不加密），但如果送pin的话需加密<br>
	 * @param customerInfoMap 信息域请求参数 key送域名value送值,必送<br>
	 *        例如：customerInfoMap.put("certifTp", "01");					//证件类型<br>
				  customerInfoMap.put("certifId", "341126197709218366");	//证件号码<br>
				  customerInfoMap.put("customerNm", "互联网");				//姓名<br>
				  customerInfoMap.put("phoneNo", "13552535506");			//手机号<br>
				  customerInfoMap.put("smsCode", "123456");					//短信验证码<br>
				  customerInfoMap.put("pin", "111111");						//密码（加密）<br>
				  customerInfoMap.put("cvn2", "123");           			//卡背面的cvn2三位数字（不加密）<br>
				  customerInfoMap.put("expired", "1711");  				    //有效期 年在前月在后（不加密)<br>
	 * @param accNo  customerInfoMap送了密码那么卡号必送,如果customerInfoMap未送密码pin，此字段可以不送<br>
	 * @param encoding 上送请求报文域encoding字段的值<br>				  
	 * @return base64后的持卡人信息域字段<br>
	 */
	public static String getCustomerInfo(Map<String,String> customerInfoMap,String accNo,String encoding) {
		
		StringBuffer sf = new StringBuffer("{");
		
		for(Iterator<String> it = customerInfoMap.keySet().iterator(); it.hasNext();){
			String key = it.next();
			String value = customerInfoMap.get(key);
			if(key.equals("pin")){
				if(null == accNo || "".equals(accNo.trim())){
					LogUtil.writeLog("送了密码（PIN），必须在getCustomerInfoWithEncrypt参数中上传卡号");
					return "{}";
				}else{
					value = SDKUtil.encryptPin(accNo,value,encoding);
				}
			}
			if(it.hasNext())
				sf.append(key + SDKConstants.EQUAL+ value + SDKConstants.AMPERSAND);
			else
				sf.append(key + SDKConstants.EQUAL + value);
		}
		sf.append("}");
		
		String customerInfo = sf.toString();
		LogUtil.writeLog("组装的customerInfo明文："+customerInfo);
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(
					encoding)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return customerInfo;
	}
	
	/**
	 * 持卡人信息域customerInfo构造，勾选对敏感信息加密权限 适用新加密规范，对pin和phoneNo，cvn2，expired加密 <br>
	 * 适用到的交易： <br>
	 * @param customerInfoMap 信息域请求参数 key送域名value送值,必送 <br>
	 *        例如：customerInfoMap.put("certifTp", "01");					//证件类型 <br>
				  customerInfoMap.put("certifId", "341126197709218366");	//证件号码 <br>
				  customerInfoMap.put("customerNm", "互联网");				//姓名 <br>
				  customerInfoMap.put("smsCode", "123456");					//短信验证码 <br>
				  customerInfoMap.put("pin", "111111");						//密码（加密） <br>
				  customerInfoMap.put("phoneNo", "13552535506");			//手机号（加密） <br>
				  customerInfoMap.put("cvn2", "123");           			//卡背面的cvn2三位数字（加密） <br>
				  customerInfoMap.put("expired", "1711");  				    //有效期 年在前月在后（加密） <br>
	 * @param accNo  customerInfoMap送了密码那么卡号必送,如果customerInfoMap未送密码PIN，此字段可以不送<br>
	 * @param encoding 上送请求报文域encoding字段的值
	 * @return base64后的持卡人信息域字段 <br>
	 */
	public static String getCustomerInfoWithEncrypt(Map<String,String> customerInfoMap,String accNo,String encoding) {
		
		StringBuffer sf = new StringBuffer("{");
		//敏感信息加密域
		StringBuffer encryptedInfoSb = new StringBuffer("");
		
		for(Iterator<String> it = customerInfoMap.keySet().iterator(); it.hasNext();){
			String key = it.next();
			String value = customerInfoMap.get(key);
			if(key.equals("phoneNo") || key.equals("cvn2") || key.equals("expired")){
				encryptedInfoSb.append(key + SDKConstants.EQUAL+ value + SDKConstants.AMPERSAND);
			}else{
				if(key.equals("pin")){
					if(null == accNo || "".equals(accNo.trim())){
						LogUtil.writeLog("送了密码（PIN），必须在getCustomerInfoWithEncrypt参数中上传卡号");
						return "{}";
					}else{
						value = SDKUtil.encryptPin(accNo,value,encoding);
					}
				}
				if(it.hasNext())
					sf.append(key + SDKConstants.EQUAL+ value + SDKConstants.AMPERSAND);
				else
					sf.append(key + SDKConstants.EQUAL + value);
			}
		}
		
		if(!encryptedInfoSb.toString().equals("")){
			//去掉最后一个&符号
			encryptedInfoSb.setLength(encryptedInfoSb.length()-1);
			
			LogUtil.writeLog("组装的customerInfo encryptedInfo明文："+ encryptedInfoSb.toString());
			if(sf.toString().equals("{")) //如果没有除phoneNo，cvn2，expired之外的元素，不加&
				sf.append("encryptedInfo"+ SDKConstants.EQUAL);
			else
				sf.append(SDKConstants.AMPERSAND + "encryptedInfo"+ SDKConstants.EQUAL);
			
			sf.append(SDKUtil.encryptEpInfo(encryptedInfoSb.toString(), encoding));
		}
		sf.append("}");
		
		String customerInfo = sf.toString();
		LogUtil.writeLog("组装的customerInfo明文："+customerInfo);
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(
					encoding)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return customerInfo;
	}
	
	/**
	 * 有卡交易信息域(cardTransData)构造示例<br>
	 * 所有子域需用“{}”包含，子域间以“&”符号链接。格式如下：{子域名1=值&子域名2=值&子域名3=值}<br>
	 * 说明：本示例仅供参考，开发时请根据接口文档中的报文要素组装
	 * 
	 * @param contentData
	 * @param encoding
	 * @return
	 */
	public static String getCardTransData(Map<String, ?> contentData,
			String encoding) {

		StringBuffer cardTransDataBuffer = new StringBuffer();
		
		// 以下测试数据只是用来说明组装cardTransData域的基本步骤,真实数据请以实际业务为准
		String ICCardData = "uduiadniodaiooxnnxnnada";// IC卡数据
		String ICCardSeqNumber = "123";// IC卡的序列号
		String track2Data = "testtrack2Datauidanidnaidiadiada231";// 第二磁道数据
		String track3Data = "testtrack3Datadadaiiuiduiauiduia312117831";// 第三磁道数据
		String transSendMode = "b";// 交易发起方式

		// 第二磁道数据 加密格式如下：merId|orderId|txnTime|txnAmt|track2Data
		StringBuffer track2Buffer = new StringBuffer();
		track2Buffer.append(contentData.get("merId"))
				.append(SDKConstants.COLON).append(contentData.get("orderId"))
				.append(SDKConstants.COLON).append(contentData.get("txnTime"))
				.append(SDKConstants.COLON).append(contentData.get("txnAmt"))
				.append(SDKConstants.COLON).append(track2Data);

		String encryptedTrack2 = SDKUtil.encryptTrack(track2Buffer.toString(),
				encoding);

		// 第三磁道数据 加密格式如下：merId|orderId|txnTime|txnAmt|track3Data
		StringBuffer track3Buffer = new StringBuffer();
		track3Buffer.append(contentData.get("merId"))
				.append(SDKConstants.COLON).append(contentData.get("orderId"))
				.append(SDKConstants.COLON).append(contentData.get("txnTime"))
				.append(SDKConstants.COLON).append(contentData.get("txnAmt"))
				.append(SDKConstants.COLON).append(track3Data);

		String encryptedTrack3 = SDKUtil.encryptTrack(track3Buffer.toString(),
				encoding);

		// 将待组装的数据装入MAP,进行格式处理
		Map<String, String> cardTransDataMap = new HashMap<String, String>();
		cardTransDataMap.put("ICCardData", ICCardData);
		cardTransDataMap.put("ICCardSeqNumber", ICCardSeqNumber);
		cardTransDataMap.put("track2Data", encryptedTrack2);
		cardTransDataMap.put("track3Data", encryptedTrack3);
		cardTransDataMap.put("transSendMode", transSendMode);

		return cardTransDataBuffer.append(SDKConstants.LEFT_BRACE)
				.append(SDKUtil.coverMap2String(cardTransDataMap))
				.append(SDKConstants.RIGHT_BRACE).toString();
	}
	
	/**
	 * 生成签名值(SHA1摘要算法)
	 * 
	 * @param data
	 *            待签名数据Map键值对形式
	 * @param encoding
	 *            编码
	 * @return 签名是否成功
	 */
	public static boolean sign(Map<String, String> data, String encoding) {
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		// 设置签名证书序列号
		data.put(SDKConstants.param_certId, CertUtil.getSignCertId());
		// 将Map信息转换成key1=value1&key2=value2的形式
		String stringData = coverMap2String(data);
		LogUtil.writeLog("待签名请求报文串:[" + stringData + "]");
		/**
		 * 签名\base64编码
		 */
		byte[] byteSign = null;
		String stringSign = null;
		try {
			// 通过SHA1进行摘要并转16进制
			byte[] signDigest = SecureUtil.sha1X16(stringData, encoding);
			byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
					CertUtil.getSignCertPrivateKey(), signDigest));
			stringSign = new String(byteSign);
			// 设置签名域值
			data.put(SDKConstants.param_signature, stringSign);
			return true;
		} catch (Exception e) {
			LogUtil.writeErrorLog("签名异常", e);
			return false;
		}
	}

	/**
	 * 通过传入的证书绝对路径和证书密码读取签名证书进行签名并返回签名值<br>
	 * 
	 * @param data
	 *            待签名数据Map键值对形式
	 * @param encoding
	 *            编码
	 * @param certPath
	 *            证书绝对路径
	 * @param certPwd
	 *            证书密码
	 * @return 签名值
	 */
	public static boolean signByCertInfo(Map<String, String> data,
			String encoding, String certPath, String certPwd) {
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		if (isEmpty(certPath) || isEmpty(certPwd)) {
			LogUtil.writeLog("Invalid Parameter:CertPath=[" + certPath
					+ "],CertPwd=[" + certPwd + "]");
			return false;
		}
		// 设置签名证书序列号
		data.put(SDKConstants.param_certId,
				CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
		// 将Map信息转换成key1=value1&key2=value2的形式
		String stringData = coverMap2String(data);
		/**
		 * 签名\base64编码
		 */
		byte[] byteSign = null;
		String stringSign = null;
		try {
			byte[] signDigest = SecureUtil.sha1X16(stringData, encoding);
			byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
					CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd),
					signDigest));
			stringSign = new String(byteSign);
			// 设置签名域值
			data.put(SDKConstants.param_signature, stringSign);
			return true;
		} catch (Exception e) {
			LogUtil.writeErrorLog("签名异常", e);
			return false;
		}
	}

	/**
	 * 密码加密.
	 * 
	 * @param card
	 *            卡号
	 * @param pwd
	 *            密码
	 * @param encoding
	 *            字符集编码
	 * @return 加密后的字符串
	 */
	public static String encryptPin(String card, String pwd, String encoding) {
		return SecureUtil.EncryptPin(pwd, card, encoding, CertUtil
				.getEncryptCertPublicKey());
	}
	
	/**
	 * CVN2加密.
	 * 
	 * @param cvn2
	 *            CVN2值
	 * @param encoding
	 *            字符编码
	 * @return 加密后的数据
	 */
	public static String encryptCvn2(String cvn2, String encoding) {
		return SecureUtil.EncryptData(cvn2, encoding, CertUtil
				.getEncryptCertPublicKey());
	}

	/**
	 * CVN2解密
	 * 
	 * @param base64cvn2
	 *            加密后的CVN2值
	 * @param encoding
	 *            字符编码
	 * @return 解密后的数据
	 */
	public static String decryptCvn2(String base64cvn2, String encoding) {
		return SecureUtil.DecryptedData(base64cvn2, encoding, CertUtil
				.getSignCertPrivateKey());
	}

	/**
	 * 有效期加密.
	 * 
	 * @param date
	 *            有效期
	 * @param encoding
	 *            字符编码
	 * @return 加密后的数据
	 */
	public static String encryptAvailable(String date, String encoding) {
		return SecureUtil.EncryptData(date, encoding, CertUtil
				.getEncryptCertPublicKey());
	}

	/**
	 * 有效期解密.
	 * 
	 * @param base64Date
	 *            有效期值
	 * @param encoding
	 *            字符编码
	 * @return 加密后的数据
	 */
	public static String decryptAvailable(String base64Date, String encoding) {
		return SecureUtil.DecryptedData(base64Date, encoding, CertUtil
				.getSignCertPrivateKey());
	}

	/**
	 * 卡号加密.
	 * 
	 * @param pan
	 *            卡号
	 * @param encoding
	 *            字符编码
	 * @return 加密后的卡号值
	 */
	public static String encryptPan(String pan, String encoding) {
		return SecureUtil.EncryptData(pan, encoding, CertUtil
				.getEncryptCertPublicKey());
	}

	/**
	 * 卡号解密.
	 * 
	 * @param base64Pan
	 *            如果卡号加密，传入返回报文中的卡号字段
	 * @param encoding
	 * @return
	 */
	public static String decryptPan(String base64Pan, String encoding) {
		return SecureUtil.DecryptedData(base64Pan, encoding, CertUtil
				.getSignCertPrivateKey());
	}
	
	
	/**
	 * 敏感信息encryptedInfo加密
	 * 
	 * @param date
	 *            有效期
	 * @param encoding
	 *            字符编码
	 * @return 加密后的数据
	 */
	public static String encryptEpInfo(String encryptedInfo, String encoding) {
		return SecureUtil.EncryptData(encryptedInfo, encoding, CertUtil
				.getEncryptCertPublicKey());
	}

	/**
	 * 敏感信息encryptedInfo解密.
	 * 
	 * @param base64Date
	 *            有效期值
	 * @param encoding
	 *            字符编码
	 * @return 加密后的数据
	 */
	public static String decryptEpInfo(String base64EncryptedInfo, String encoding) {
		return SecureUtil.DecryptedData(base64EncryptedInfo, encoding, CertUtil
				.getSignCertPrivateKey());
	}
	
	
	/**
	 * 加密磁道信息，使用公钥文件
	 * 
	 * @param trackData
	 *            待加密磁道数据
	 * @param encoding
	 *            编码格式
	 * @return String
	 */
	public static String encryptTrack(String trackData, String encoding) {
		return SecureUtil.EncryptData(trackData, encoding,
				CertUtil.getEncryptTrackCertPublicKey());
	}
	
	/**
	 * 加密磁道信息，使用模和指数
	 * 
	 * @param trackData
	 *            待加密磁道数据
	 * @param encoding
	 *            编码格式
	 * @param modulus
	 *            模
	 * @param exponent
	 *            指数
	 * @return
	 */
	public static String encryptTrack(String trackData, String encoding,
			String modulus, String exponent) {
		return SecureUtil.EncryptData(trackData, encoding,
				CertUtil.getEncryptTrackCertPublicKey(modulus, exponent));
	}
	/**
	 * 获取敏感信息加密证书的物理序列号
	 * @return
	 */
	public static String getEncryptCertId(){
		return CertUtil.getEncryptCertId();
	}
	
	
	/**
	 * 将Map中的数据转换成按照Key的ascii码排序后的key1=value1&key2=value2的形式 不包含签名域signature
	 * 
	 * @param data
	 *            待拼接的Map数据
	 * @return 拼接好后的字符串
	 */
	public static String coverMap2String(Map<String, String> data) {
		TreeMap<String, String> tree = new TreeMap<String, String>();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			if (SDKConstants.param_signature.equals(en.getKey().trim())) {
				continue;
			}
			tree.put(en.getKey(), en.getValue());
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			sf.append(en.getKey() + SDKConstants.EQUAL + en.getValue()
					+ SDKConstants.AMPERSAND);
		}
		return sf.substring(0, sf.length() - 1);
	}


	/**
	 * 兼容老方法 将形如key=value&key=value的字符串转换为相应的Map对象
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> coverResultString2Map(String result) {
		return convertResultStringToMap(result);
	}

	/**
	 * 将形如key=value&key=value的字符串转换为相应的Map对象
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> convertResultStringToMap(String result) {
		Map<String, String> map =null;
		try {
			
			if(StringUtils.isNotBlank(result)){
				if(result.startsWith("{") && result.endsWith("}")){
					System.out.println(result.length());
					result = result.substring(1, result.length()-1);
				}
				 map = parseQString(result);
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return map;
	}

	
	/**
	 * 解析应答字符串，生成应答要素
	 * 
	 * @param str
	 *            需要解析的字符串
	 * @return 解析的结果map
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> parseQString(String str)
			throws UnsupportedEncodingException {

		Map<String, String> map = new HashMap<String, String>();
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		char curChar;
		String key = null;
		boolean isKey = true;
		boolean isOpen = false;//值里有嵌套
		char openName = 0;
		if(len>0){
			for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
				curChar = str.charAt(i);// 取当前字符
				if (isKey) {// 如果当前生成的是key
					
					if (curChar == '=') {// 如果读取到=分隔符 
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else  {// 如果当前生成的是value
					if(isOpen){
						if(curChar == openName){
							isOpen = false;
						}
						
					}else{//如果没开启嵌套
						if(curChar == '{'){//如果碰到，就开启嵌套
							isOpen = true;
							openName ='}';
						}
						if(curChar == '['){
							isOpen = true;
							openName =']';
						}
					}
					if (curChar == '&' && !isOpen) {// 如果读取到&分割符,同时这个分割符不是值域，这时将map里添加
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					}else{
						temp.append(curChar);
					}
				}
				
			}
			putKeyValueToMap(temp, isKey, key, map);
		}
		return map;
	}

	private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
			String key, Map<String, String> map)
			throws UnsupportedEncodingException {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}

	/**
	 * 判断字符串是否为NULL或空
	 * 
	 * @param s
	 *            待判断的字符串数据
	 * @return 判断结果 true-是 false-否
	 */
	public static boolean isEmpty(String s) {
		return null == s || "".equals(s.trim());
	}


	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				//在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				//System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
				if (null == res.get(en) || "".equals(res.get(en))) {
					res.remove(en);
				}
			}
		}
		return res;
	}
	
	
	
}
