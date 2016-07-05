/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.file;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.transaction.service.impl.MobileRechargeServiceImpl;
import com.caitu99.service.utils.SpringContext;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: JuHeImgApi 
 * @author ws
 * @date 2015年12月15日 下午2:24:06 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class JuHeImgApi {
	
	private static String API_URL = "http://op.juhe.cn/vercode/index";
	private static String KEY = "667beb6bb70b89b24b787426e09dd9b9";
	private static String CODE_TYPE = "8001";

	private static final Logger logger = LoggerFactory
			.getLogger(JuHeImgApi.class);
	
	/**
	 * 图片识别，传入base64转码后的图片串
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: recognizeImgCodeFromStr 
	 * @param base64EncodedStr
	 * @return code   or   null
	 * @date 2015年12月15日 下午6:29:57  
	 * @author ws
	 */
	public static String recognizeImgCodeFromStr(String base64EncodedStr){
		try {
			KeyConfig keyConfig = SpringContext.getBean("keyConfig");
			byte[] decodeStr = Base64.getDecoder().decode(base64EncodedStr);
			
			Map<String, String> textMap = new HashMap<String, String>();
			textMap.put("key", keyConfig.juheapiKey);
			textMap.put("codeType", CODE_TYPE);
			//获取文件类型
	        String contentType = judgeFileType(decodeStr);

	        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(decodeStr)); 
			String result = formUpload(API_URL,textMap,inputStream,contentType);
			//System.out.println(result);
			
			JSONObject json = JSON.parseObject(result);
			String error_code = json.getString("error_code");
			if("0".equals(error_code)){
				String codeText = json.getString("result");
				if(StringUtils.isBlank(codeText)){
					logger.info("聚合图形验证码识别失败：{}",codeText);
				}else{
					logger.info("聚合图形验证码识别成功：{}",codeText);
				}
				return codeText;
			}else{
				logger.error("聚合图形验证码识别失败：{}",json.getString("reason"));
				return null;
			}
		} catch (Exception e) {
			logger.warn("聚合图形验证码调用失败：{}",e);
			return null;
		}
		
		
	}
	
	/**
	 * 图片识别，传入图片地址
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: recognizeImgCodeFromFile 
	 * @param imgPath
	 * @return code if success   or   null if error
	 * @date 2015年12月15日 下午6:30:36  
	 * @author ws
	 * @throws IOException 
	 */
	public static String recognizeImgCodeFromFile(String imgPath) throws IOException{
		File imgFile = new File(imgPath);
		byte[] imgByte = getBytesFromFile(imgFile);
		Map<String, String> textMap = new HashMap<String, String>();
		textMap.put("key", KEY);
		textMap.put("codeType", CODE_TYPE);
		//获取文件类型
        String contentType = judgeFileType(imgByte);
        
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(imgByte)); 
		String result = formUpload(API_URL,textMap,inputStream,contentType);
		//System.out.println(result);
		
		JSONObject json = JSON.parseObject(result);
		String error_code = json.getString("error_code");
		if("0".equals(error_code)){
			return json.getString("result");
		}else{
			return null;
		}
	}
	
	/**
	 * 获取文件类型
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: judgeFileType 
	 * @param decodeResult
	 * @return
	 * @date 2015年12月15日 下午3:37:47  
	 * @author ws
	 * @throws IOException 
	 */
	public static String judgeFileType(byte[] decodeResult) throws IOException{
		DataInputStream inputStr = new DataInputStream(new ByteArrayInputStream(decodeResult));

		return FileTypeJudge.getType(inputStr).name();

	}
	
	/** 
     * 上传图片 
     * @param urlStr 
     * @param textMap 
     * @param fileMap 
     * @return 
     */  
    public static String formUpload(String urlStr, Map<String, String> textMap
    			, DataInputStream in, String contentType) {  
        String res = "";  
        HttpURLConnection conn = null;  
        String BOUNDARY = "----WebKitFormBoundaryA5B1hN295LZwLOMp"; //boundary就是request头和上传文件内容的分隔符    
        try {  
            URL url = new URL(urlStr);  
            conn = (HttpURLConnection) url.openConnection();  
            conn.setConnectTimeout(5000);  
            conn.setReadTimeout(30000);  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);  
            conn.setRequestMethod("POST");  
            conn.setRequestProperty("Connection", "Keep-Alive");  
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");  
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  
  
            OutputStream out = new DataOutputStream(conn.getOutputStream());  
            // text    
            if (textMap != null) {  
                StringBuffer strBuf = new StringBuffer();  
                Iterator<Map.Entry<String, String>> iter = textMap.entrySet().iterator();  
                while (iter.hasNext()) {  
                    Map.Entry<String, String> entry = iter.next();  
                    String inputName = (String) entry.getKey();  
                    String inputValue = (String) entry.getValue();  
                    if (inputValue == null) {  
                        continue;  
                    }  
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");  
                    strBuf.append(inputValue);  
                }  
                out.write(strBuf.toString().getBytes());  
            }  
            // file     
            
            
            
            StringBuffer strBuf = new StringBuffer();  
            strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
            strBuf.append("Content-Disposition: form-data; name=\"" + "image" + "\"; filename=\"" + "caitu99."+ contentType + "\"\r\n");  
			strBuf.append("Content-Type:image/" + contentType.toLowerCase() + "\r\n\r\n");  

            out.write(strBuf.toString().getBytes());  
             
            int bytes = 0;  
            byte[] bufferOut = new byte[1024];  
            while ((bytes = in.read(bufferOut)) != -1) {  
                out.write(bufferOut, 0, bytes);  
            }  
            in.close(); 
  
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
            out.write(endData);  
            out.flush();  
            out.close();  
  
            // 读取返回数据    
            StringBuffer strBuf2 = new StringBuffer();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
                strBuf2.append(line).append("\n");  
            }  
            res = strBuf2.toString();  
            reader.close();  
            reader = null;  
        } catch (Exception e) {  
            System.out.println("发送POST请求出错。" + urlStr);  
            e.printStackTrace();  
        } finally {  
            if (conn != null) {  
                conn.disconnect();  
                conn = null;  
            }  
        }  
        return res;  
    }  
    
    /**
     * 从文件中读取字节流
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: getBytesFromFile 
     * @param imgFile
     * @return
     * @date 2015年12月15日 下午4:01:07  
     * @author ws
     */
    public static byte[] getBytesFromFile(File imgFile){  
        if (imgFile == null){  
            return null;  
        }  
        try{  
            FileInputStream stream = new FileInputStream(imgFile);  
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = stream.read(b)) != -1)  
                out.write(b, 0, n);  
                stream.close();  
                out.close();  
            return out.toByteArray();  
        } catch (IOException e){  
            e.printStackTrace();  
        }  
        return null;  
    }  
	
}
