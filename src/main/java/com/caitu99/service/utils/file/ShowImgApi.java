/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.file;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ShowImgApi 
 * @author ws
 * @date 2015年12月15日 下午8:12:05 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ShowImgApi {

	private static final Logger logger = LoggerFactory
			.getLogger(ShowImgApi.class);
	
	private static String API_URL = "http://route.showapi.com/184-1";
	private static String APP_ID = "13987";
	private static String TYPE_ID = "3000";
	private static String SIGN = "7733ef1a57b549eb89033ac770bdd882";
	
	public static String recognizeImgCodeFromStr(String base64EncodedStr){
		try {
			KeyConfig keyConfig = SpringContext.getBean("keyConfig");
			byte[] decodeStr = Base64.getDecoder().decode(base64EncodedStr);

			String contentType = judgeFileType(decodeStr);
			String timestamp = DateUtil.DateToString(new Date(), "yyyyMMddHHmmss");
			
			StringBuffer urlBuf = new StringBuffer(API_URL)
				.append("?showapi_appid=").append(keyConfig.showapiAppId)
				.append("&showapi_timestamp=").append(timestamp)
				.append("&typeId=").append(TYPE_ID)
				.append("&showapi_sign=").append(keyConfig.showapiSign);
			
			DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(decodeStr)); 
			String result = formUpload(urlBuf.toString(),inputStream,contentType);
			//System.out.println(result);
			
			JSONObject json = JSON.parseObject(result);
			String error_code = json.getString("showapi_res_code");
			if(StringUtils.isNotBlank(error_code) && "0".equals(error_code)){

				JSONObject jsonBody = JSON.parseObject(json.getString("showapi_res_body"));
				String codeText = jsonBody.getString("Result");
				if(StringUtils.isBlank(codeText)){
					logger.info("ShowAPI图形验证码识别失败：{}",codeText);
				}else{
					logger.info("ShowAPI图形验证码识别成功：{}",codeText);
				}
				return codeText;
			}else{
				logger.error("ShowAPI图形验证码失败失败：{}",json.getString("showapi_res_error"));
				return null;
			}
		} catch (Exception e) {
			logger.warn("ShowAPI图形验证码识别调用失败：{}",e);
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
    public static String formUpload(String urlStr
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
            
            // file     
            StringBuffer strBuf = new StringBuffer();  
            strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
            strBuf.append("Content-Disposition: form-data; name=\"" + "image" + "\"; filename=\"" + "caitu99."+ contentType.toLowerCase() + "\"\r\n");  
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
    
	
}
