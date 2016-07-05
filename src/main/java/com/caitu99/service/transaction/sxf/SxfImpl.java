package com.caitu99.service.transaction.sxf;

import java.io.StringReader;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.date.DateStyle;
import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.encryption.RSACoder;
import com.caitu99.service.utils.http.HttpClientUtils;
@Component
public class SxfImpl implements Sxf {
	
	private final static Logger logger = LoggerFactory
			.getLogger(SxfImpl.class);
	
	private final String CUSTID = Configuration.getProperty("sxf.custId", null);

	@Override
	public String daifu(String accName, String accNo, Long amount,
			String orderNo, String tno) throws Exception{
		//验证参数
		if(StringUtils.isBlank(accName) || StringUtils.isBlank(accNo) ||
				amount == null || StringUtils.isBlank(orderNo) || StringUtils.isBlank(tno) ){
			throw new Exception("参数不完整");
		}
		//组装参数
		String dataList = new StringBuffer(orderNo).append(",")
				.append(accName).append(",").append(accNo).append(",,1,")
				.append(amount).toString();
		Map<String,String> params = new HashMap<String, String>();
		params.put("custId", CUSTID);
		params.put("sendTime", DateUtil.DateToString(new Date(), DateStyle.YYMMDDHHMMSS));
		params.put("batchNo", tno);
		params.put("payNum", "1");
		params.put("payAmount", amount.toString());
		params.put("dataList", dataList);
		//获取签名所需参数
		String salt = this.getPaySalt(params);
		//获取签名
		String paramMac = this.getParamMac(salt);
		params.put("paramMac", paramMac);
		//请求参数
		String xmlParams = this.getParamsXML(params);
		
		String url = Configuration.getProperty("sxf.for.pay.url", null);
		if(StringUtils.isBlank(url)){
			logger.error("随心付缺少配置参数");
			throw new Exception("缺少配置参数");
		}
		String flag = HttpClientUtils.getInstances().doPostXmlParams(url, xmlParams, "utf-8");
		if(StringUtils.isBlank(flag)){
			logger.error("随心付请求失败");
			throw new Exception("随心付请求失败");
		}
		return flag;
	}

	@Override
	public String query(String orderNo)  throws Exception{
		if(StringUtils.isBlank(orderNo)){
			throw new Exception("参数不完整");
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("custId", CUSTID);
		params.put("sendTime", DateUtil.DateToString(new Date(), DateStyle.YYMMDDHHMMSS));
		params.put("dataList", orderNo);
		//获取签名所需参数
		String salt = this.getQuerySalt(params);
		//获取签名
		String paramMac = this.getParamMac(salt);
		params.put("paramMac", paramMac);
		//请求参数
		String xmlParams = this.getParamsXML(params);
		
		String url = Configuration.getProperty("sxf.query.for.pay.url", null);
		if(StringUtils.isBlank(url)){
			logger.error("缺少随心付配置参数");
			throw new Exception("缺少配置参数");
		}
		String flag = HttpClientUtils.getInstances().doPostXmlParams(url, xmlParams, "utf-8");
		if(StringUtils.isBlank(flag)){
			logger.error("随心付查询失败");
			throw new Exception("查询失败");
		}
		logger.info("代付查询返回值："+flag);
		//确定返回结果的数据结构
		if(flag.startsWith("<?") && flag.endsWith(">")){
			StringReader sr = new StringReader(flag);
			InputSource is = new InputSource(sr);
			Document doc = (new SAXBuilder()).build(is);
			Element element= doc.getRootElement().getChild("params");
			String custId =  element.getChild("custId").getText();
			String sendTime_back =  element.getChild("sendTime").getText();
			String dataList_back =  element.getChild("dataList").getText();
			String paramMac_back =  element.getChild("paramMac").getText();
			if(!CUSTID.equals(custId)){
				logger.error("随心付返回值商户号与我商户号不匹配");
				throw new Exception("商户号不匹配");
			}
			Map<String,String> checkParam = new HashMap<String, String>();
			checkParam.put("sendTime", sendTime_back);
			checkParam.put("dataList", dataList_back);
			//获取签名所需参数
			String salt_check = this.getQuerySalt(checkParam);
			//验证签名
			if(!this.decrypt(paramMac_back, salt_check)){
				logger.error("随心付查询返回数据验证签名失败");
				throw new Exception("返回数据验证签名失败");
			}
			return dataList_back;
		} else {
			logger.error("随心付查询出错flag：{}",flag);
			throw new Exception("查询出错："+flag);
		}
		
	}
	
	/**
	 * @Description:  (获取签名所需要的参数)  
	 * @Title: getPaySalt 
	 * @param params
	 * @return
	 * @date 2016年5月26日 下午3:57:31  
	 * @author Hongbo Peng
	 */
	private String getPaySalt(Map<String, String> params){
		StringBuffer buffer = new StringBuffer();
		//必须以此顺序
		buffer.append("custId=").append(CUSTID)
			  .append("&sendTime=").append(params.get("sendTime"))
			  .append("&batchNo=").append(params.get("batchNo"))
			  .append("&payNum=").append(params.get("payNum"))
			  .append("&payAmount=").append(params.get("payAmount"))
			  .append("&dataList=").append(params.get("dataList"));
		return buffer.toString();
	}
	/**
	 * @Description: (获取验证签名所需要的参数)  
	 * @Title: getQuerySalt 
	 * @param params
	 * @return
	 * @date 2016年5月26日 下午4:02:19  
	 * @author Hongbo Peng
	 */
	private String getQuerySalt(Map<String, String> params){
		StringBuffer sb = new StringBuffer();
		sb.append("custId=").append(CUSTID)
				.append("&sendTime=").append(params.get("sendTime"))
				.append("&dataList=").append(params.get("dataList"));
		return sb.toString();
	}
	
	/**
	 * @Description: (获取请求参数)  
	 * @Title: getParamsXML 
	 * @param params
	 * @return
	 * @date 2016年5月26日 下午4:04:36  
	 * @author Hongbo Peng
	 */
	private String getParamsXML(Map<String,String> params){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
			  .append("<cfp>")
				  .append("<service>sxf</service>")
				  .append("<function>forPayXml</function>")
				  .append("<params>");
				  	for (String key : params.keySet()) {
				  		buffer.append("<").append(key).append(">")
				  					.append(params.get(key))
				  			  .append("</").append(key).append(">");
					}
			buffer.append("</params>")
			  .append("</cfp>");
		return buffer.toString();
	}

	/**
	 * @Description: (获取签名)  
	 * @Title: getParamMac 
	 * @param salt
	 * @return
	 * @date 2016年5月26日 下午3:53:26  
	 * @author Hongbo Peng
	 */
	private String getParamMac(String salt){
		try {
			String privateKey = Configuration.getProperty("sxf.caitu.private.key", null);
			if(StringUtils.isBlank(privateKey)){
				throw new Exception("缺少配置参数");
			}
			//1.SHA-1计算
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			//salt为上面拼接出的字符串
			byte[] sha1Arr = messageDigest.digest(salt.getBytes("utf-8"));
			//2.RSA加密
			//然后用商户私钥对其加密，然后转base64，得到：paramMac。如下：
			byte[] rsa_array = RSACoder.encryptByPrivateKey(sha1Arr,privateKey);
			//3.BASE64 加密
			byte[] encode = Base64.encodeBase64(rsa_array);
			return new String(encode, "utf-8");
		} catch (Exception e) {
			logger.error("签名加密失败",e);
		}
		return null;
	}
	
	/**
	 * @Description: (验证签名)  
	 * @Title: decrypt 
	 * @param mac
	 * @param salt
	 * @return
	 * @throws Exception
	 * @date 2016年5月26日 下午3:53:41  
	 * @author Hongbo Peng
	 */
	private boolean decrypt(String mac,String salt) throws Exception{
		String publicKey = Configuration.getProperty("sxf.union.public.key", null);
		//1.BASE64解密
		byte[] encode = Base64.decodeBase64(mac.getBytes("utf-8"));
		//2.RSA 解密
		byte[] rsa_array = RSACoder.decryptByPublicKey(encode, publicKey);
		//3.计算参数，将2的结果与计算结果比较
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		byte[] sha1ArrOld = messageDigest.digest(salt.getBytes("utf-8"));
		return Arrays.equals(rsa_array, sha1ArrOld);
	}
}
