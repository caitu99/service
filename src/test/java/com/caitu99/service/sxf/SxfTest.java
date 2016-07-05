package com.caitu99.service.sxf;

import java.io.StringReader;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.caitu99.service.utils.date.DateStyle;
import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.encryption.RSACoder;
/**
 * 随心付
 * @Description: (类职责详细描述,可空) 
 * @ClassName: SxfTest 
 * @author Hongbo Peng
 * @date 2016年5月20日 下午5:22:05 
 * @Copyright (c) 2015-2020 by caitu99
 */
public class SxfTest {

	public Map<String,String> getParams(String no){
		Map<String,String> params = new HashMap<String, String>();
		params.put("custId", "CB0000001956");
		params.put("sendTime", DateUtil.DateToString(new Date(), DateStyle.YYMMDDHHMMSS));
		params.put("batchNo", "ceshisxf"+no);
		params.put("payNum", "1");
		params.put("payAmount", "1");
//		params.put("remark", "测试");
		params.put("dataList", "ceshi"+no+",彭红波,6214835889291983,,1,1");
//		params.put("paramMac", "");
		return params;
	}
	
	
	public String getSalt(Map<String,String> params){
		StringBuffer buffer = new StringBuffer();
		//必须以此顺序
		buffer.append("custId=").append(params.get("custId"))
			  .append("&sendTime=").append(params.get("sendTime"))
			  .append("&batchNo=").append(params.get("batchNo"))
			  .append("&payNum=").append(params.get("payNum"))
			  .append("&payAmount=").append(params.get("payAmount"))
			  .append("&dataList=").append(params.get("dataList"));
		return buffer.toString();
	}
	
	/*public String getSalt(Map<String, String> params) {
		// 根据参数名字典排序
		List<String> keyList = Arrays.asList(params.keySet().toArray(new String[params.size()]));
		Collections.sort(keyList);
		// 拼接签名参数
		StringBuilder sb = new StringBuilder();
		for (String k : keyList) {
			if ("sign".equals(k))
				continue;
			sb.append(k).append("=").append(params.get(k)).append("&");
		}
		if (params.size() > 0)
			sb.delete(sb.length() - "&".length(), sb.length());
		
		return sb.toString();
	}*/
	
	public String getXML(Map<String,String> params){
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
		System.out.println(buffer.toString());
		return buffer.toString();
	}
	
	public String getMac(String salt){
		try {
			String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL4ymxhVgPq9hkxLn/SleIcujJR9D+ryzw6PEpkwRFwq6Iq+UFKM52m04qF2u+ejv0K7FsJY9kStbvMaphRimOmejv9E8v2acQMHaUpiO5eaGoR/Nz66aHmXc6KlJ8nfnrdOR/hDIk9rT9pppfiNpGiIorObIL1+jo4POkaY4SnrAgMBAAECgYAqj1ZnTpLLCOfpYK7NZs2eqkro20dZkrXEkz1dLBDP8wYQLd/5aPBLlh90dAY+IkUlIIpKOO/6lDiUi60IOLvwgDlRArmQ7RIWLbltVE/8Ru0S9A4AvCwzM1h5FuWLvlPY7OOfMuAPR8jWsYy8LQPKrTIJ9iXdj0pov35y6gSFGQJBAN7US9JXZmOgSvBItNE40cjrSBu/JSauWZ5M9kDlslfmTi6GN8ThP28YsCGRlX5Y9Ivn4j1x04FsWdJBNqkPzz8CQQDagsQiqgz/6g/HHQolTG3R/7QwwjlYSehmS4Q8M6lhtQmAdNq4vxiKqJ/PQWmBbRRpt0EDwA+6ItB5zjMIoiZVAkEAqj56u3bpFF7IQnLaKyuFJEOWcRSF9tqoP8i/L/AOZRfhTaxf+Xy6sU+kadFH7SNbm3SLprRLiwtUSM5oS5x3kwJBAMircqhK9ulG8Ppw5tJeIDTM2ZQ1qig0p6LaEzSeVR2P/ovjxMIJbOZZ+XmCnvvnSunTC3gAN/E+66oQ/bkeAIkCQHAjmoyoPmQNs/JCwrtEbduPyUPSa3XUZNCmsZLWT6/BVxoI8oyPnY8ul0m/j5wlHoJocVa/6FiFhaJdkBdDgWw=";
//			salt = "custId=CB0000001956&sendTime=20160523141400&batchNo=ctsxf1000005&payNum=1&payAmount=100&dataList=tx1000005,彭红波,4312123443212314,,1,100";
			//1.SHA-1计算
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			//salt为上面拼接出的字符串
			byte[] sha1Arr = messageDigest.digest(salt.getBytes("utf-8"));
			System.out.println(Arrays.toString(sha1Arr));
			//2.RSA加密
			//然后用商户私钥对其加密，然后转base64，得到：paramMac。如下：
			byte[] rsa_array = RSACoder.encryptByPrivateKey(sha1Arr,privateKey);//(Base64.decodeBase64(privateKey.getBytes("utf-8")), sha1Arr);
			System.out.println(Arrays.toString(rsa_array));
			//3.BASE64 加密
			byte[] encode = Base64.encodeBase64(rsa_array);
			String mac = new String(encode, "utf-8");
			System.out.println("加密："+mac);
			return mac;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String decrypt(String mac,String salt){
		try {
			String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+IMBbuSZVmiilWuGhGm4cgTmw7YBXykebkQkIDJEifj+SZxjMJBsjZ5JqjAFSlPNW+gv9T3UXe5gBQPM8YqB+kwAWtHjzRDlU/kaAq2A+MVCqR44KDNaVK+raiBme1wJ3w0bxDPwxjMPkg2psc0jGuP+lovS3fJwNbkEHRne68wIDAQAB";
			//1.BASE64解密
			byte[] encode = Base64.decodeBase64(mac.getBytes("utf-8"));
			System.out.println(Arrays.toString(encode));
			//2.RSA 解密
			byte[] rsa_array = RSACoder.decryptByPublicKey(encode, publicKey);
			//3.计算参数，将2的结果与计算结果比较
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			byte[] sha1ArrOld = messageDigest.digest(salt.getBytes("utf-8"));
			System.out.println("签名验证:"+Arrays.equals(rsa_array, sha1ArrOld));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		try {
			/*SxfTest sxf = new SxfTest();
			String url = "http://58.42.236.252:8500/gzubcp-svc-facade/sxf/forPayXml.do";
			Map<String, String> params = sxf.getParams("1000011");
			String salt = sxf.getSalt(params);
			System.out.println(salt);*/
			
			SxfTest sxf = new SxfTest();
			String url = "http://58.42.236.252:8501/gzubcp-svc-facade/sxf/queryForPay.do";
			Map<String, String> params = new HashMap<String, String>();
			params.put("custId", "CB0000002914");
			params.put("sendTime", "20160526094600");
			params.put("dataList", "ceshi1000001");
			StringBuffer buffer = new StringBuffer();
			buffer.append("custId=").append(params.get("custId"))
					.append("&sendTime=").append(params.get("sendTime"))
					.append("&dataList=").append(params.get("dataList"));
			String salt = buffer.toString();
			
			String mac = sxf.getMac(salt);
			params.put("paramMac", mac);
			String xmlParams = sxf.getXML(params);
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			StringEntity stringEntity = new StringEntity(xmlParams,"utf-8");
			post.addHeader("Content-Type", "text/xml");
			post.setEntity(stringEntity);
			HttpResponse httpResponse = client.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();
			
			if(status == HttpStatus.SC_OK){
				String returnString = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
				System.out.println(returnString);
				StringReader sr = new StringReader(returnString);
				InputSource is = new InputSource(sr);
				Document doc = (new SAXBuilder()).build(is);
				Element element= doc.getRootElement().getChild("params");
				String custId =  element.getChild("custId").getText();
				System.out.println(custId);
				String sendTime =  element.getChild("sendTime").getText();
				System.out.println(sendTime);
				String dataList =  element.getChild("dataList").getText();
				System.out.println(dataList);
				String paramMac =  element.getChild("paramMac").getText();
				System.out.println(paramMac);
				
				StringBuffer sb = new StringBuffer();
				sb.append("custId=").append(custId)
						.append("&sendTime=").append(sendTime)
						.append("&dataList=").append(dataList);
				sxf.decrypt(paramMac, sb.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
