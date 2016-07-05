/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AbstractJunit;
import com.caitu99.service.utils.file.FileTypeJudge;
import com.caitu99.service.utils.file.ShowImgApi;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ShowApiTest 
 * @author ws
 * @date 2015年12月15日 下午4:35:36 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ShowApiTest {
	
	@Test
	public void testShowImgApi(){
		String base64EncodedStr = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAZADwDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD2nWrqW3jt1jlaHzZNgkV0X5scKdysMHnnHUDtk1nprU8M32e4kkgkjQgteINrsZCFJZAAAQjAHjqPvVy3jTWdWt/HNhpNpdRPbXSKqx3KKY4pXDRnkDP3WBwSfve4qJta8W+Hddg0W7EF5JfQiO2vJo0iJYZAAbOGAJyA3zHcOhapc0dKw0mk01qrno9tfrLJ5EyGC5AyYnPUeqnow+n44rOfxJDs3xwMy45DttZWzggg9PY9M8EiuKl1XxA2ur4dv9QiLzsgiu7m1WFo2xnKgcZzlQcnJAwea6vULZrS1XzoptQvmQeZ5NqwSU/3jj7rYGMg9+mMAClcipSdOzb3NebV7W2eETiWJZlDRu0ZwT12nHIIHODj+dWbW7tr2LzbWeOZAcFkYHB9K4SfWW0i5htL5bmXRrgA7biEpNat7EAZAOMEZx26c6XhuOXWor25upGQSMPssiFUnVCDhiyAA/jnkMOxoUtbDlRcYc99DP8AFXjDSk1RbaCO6v5rQlZo7aDzEOSCUZs8Ebc8AkED0NVD4h8LhiXu7+zdjuaCawLuhPXLFCT+ZrC8GX2rro1zpen2Wnw3lndtNK1/KVdjjGwoPmLcMD0Hbrmrh+IEOkk2utaHd290pOBZ3LpCy5xlRu4GQenp+FauJTpK/Klf5nUeNNAv9Y8Q+GLi1tBPb2d0XuiWUBU3xnkE88K3AzWf8R9Ivr270KTSolP9nszuI5VRowWjCbVyCSSpAC85GBzXodcz4n/5Cuif9fSf+hrWfKmRGvKPLbpf8f8AhznPFljqOqeMbG8061LfY5II23uow6yl+mckbfm47ZzjBrqLnUpbuys7qFUSeKTe1u+ckhthAYeuWA4Oc8dKtL/yNsn/AF5D/wBDNWX/AOQ3B/17yf8AoaUJJEzqOcVF9DnfEKX+t3FppklnJa228NI7/N5jdNqlT0AJJ6HAPTBqh4aGoeHNV1LTZ4hJBHAZozuHY5UbuuDk9up6DJrv65bxN/yFLf8A69J/5UuVXuUqz5HC2n9a+pg3unavpXi2+vdI0y1vF1NQ8llcsBuK/wAak4AOCcg9CT1FcprfhXXfE2otqJ06105CPLjt4juAVSecqMHnPNeteIP9XYf9fkdWNE/5Baf9dJP/AENq15rahGq4621P/9k=";
        
		String result = ShowImgApi.recognizeImgCodeFromStr(base64EncodedStr);
		System.out.println(result);
		
	}
	
	
	
	@Test
	public void test() throws IOException {

		String BOUNDARY = "----WebKitFormBoundaryA5B1hN295LZwLOMp"; //boundary就是request头和上传文件内容的分隔符    
		//1xz1   "iVBORw0KGgoAAAANSUhEUgAAAFcAAAAoCAIAAAAE3vEvAAAEcUlEQVRoge2Yb0xbVRTAf6+Uf+XPZhgoBWEZ8sHFZcbAdEsMGrNlMSJ2GxqY4OZQF53ZFha3WAxZbJPNmGzRxBCJuLFsRIFUzGKWEXWLZhjwgwsxEhcmgwEDxLCyyp9J64c+HtC+19K+V8oYv7wP59177j33nZx77jtXKGt1cd+jUzO4tbZQq3UsMKO2OLdgK92CSi9sKK1XM7zlmXy/Oobyd9SYUCLB5HALptqLgLC8Iwg0FmJ0ZMXzgpEP11GdS3UuBWmKymnl+4JeVkdZki9brx4KemaJGx+US3JgsWBdR0rMnJbzfTT1ql9SmG2pygtBML4/d4EtyvKbtUCSE98+oNd29tQYKh8jQgD4eYjTXQAJej5aj14H0FTfdr4PUxrPG+Vn+ORP2m9ruygZHjc3SbL9s5OBxYK5nTfaeLNNUaF/nB8GRHnTKoyxAHkpogtGJrl4SzNbQdB8KFm2Xfsd0dTL7UkAnUC+EQHypk3bepl0+hprv0uXQ/MVzbD54yHZdo13BDDhpOEme9YAPPEAW1NZGQXQ7eDK36KOrRfbdJ7b8hCFDwM4XdT8xeh/mq/IPyHJjr8Mc20UQCewLV1srO+R0cw0YJo+/74f4PdQZoQ4wazUFaoz4qvuOa9XR+gY9dSJ1vFWlpgyuh003gzRWmaIE8zux6Nd+x3hJjthzuvAuIxOyWqSYwDGp/i8k6npH5fUw8ldeme0dVh25it5RzddrgxiSQ6XVZIlR7gb5xULpywHx97f71vnwuFsSY7Xk28EcE5/2HMPiueFxMYknkwS5bpuBiZmuvqPDym5AFByQdOLsbLtsjhcVvfjDo15xcKuihN+dbYevybJ29Mx6AEuDZJh4JEEIgRKV3PsD1EhOZriTFFuHZ7Jmmoo+HYMSMlLHrwsfxDI4o6FAP6g169kX7Zib811WoYBMgyY16ITcLqoaCcxkiOPijpnb3BpEAHMa8mMk5+nsYcLt+Zlq25jVFHL5DwX7xtfO6K55gjwtSndh443xZnoBICrIwxN0HmHtn/Erm3prIgkXq/ogoDQygX49sLm148BL9sCyN1PJZEVL8rf9YtCQw8TUwCxEezMlB8YUnRFZb4Vlu8XYOFrSm25U31Uk3mWYwHu9VjQisXihbO1a8JoXcYL9iqFC5BQsrP0+kKaW7E7wy24/x3vr7xw90xxZMk5vOqIYLzQbdJl2HzelvjE+O5LfZ9+E/TwoJldSs6urAjjGfFlYeru+n7/eirwrqA9Pl5iKewIpesTpW/2JlT3C37p3J6U1ahYPssy+mxFwo8W6dVjb6tB6Orwc8trseeotLH4mdkRDa/F7jg95q1Rkfjr7Ncl6ZTA8sJsj1jsOS5rjGCWu0vTgn93HTCcOhmiyT2Ylxfq9m4oqmr1aJQ8Et7o+Mm242lTA3DmYGXJiSCLK7VnhNsX9/o2UVtHWOw5FnuOR+4IO64vVil1Re0VvBu1qaYWWywIexTvcyerXM3vveKpvwT+mtSzWCrr8LLsBYD/AVjsYe3oO5y0AAAAAElFTkSuQmCC"
		//1yt7   "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"
				
        String base64EncodedStr = "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";
        byte[] decodeStr = Base64.getDecoder().decode(base64EncodedStr);

        String contentType = judgeFileType(decodeStr);
        
		String appid = "13486";
		String timestamp = "20151215202145";//DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
		String typeId = "3000";
		String sign = "e7e27e063214461cb5fb5fc1ba4b8a7f";
		
		StringBuffer urlBuf = new StringBuffer("http://route.showapi.com/184-1")
			.append("?showapi_appid=").append(appid)
			.append("&showapi_timestamp=").append(timestamp)
			.append("&typeId=").append(typeId)
			.append("&showapi_sign=").append(sign);
		String res = "";  
		HttpURLConnection conn = null;  
		try{
			URL url=new URL(urlBuf.toString());
			conn = (HttpURLConnection) url.openConnection(); 
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
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(decodeStr)); 
			
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
            System.out.println("发送POST请求出错。" + urlBuf.toString());  
            e.printStackTrace();  
        } finally {  
            if (conn != null) {  
                conn.disconnect();  
                conn = null;  
            }  
        }  
        System.out.println(res);    
		
		
		
		
		
		/*
        InputStream in=u.openStream();
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            byte buf[]=new byte[1024];
            int read = 0;
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
        }  finally {
            if (in != null) {
                in.close();
            }
        }
        byte b[]=out.toByteArray( );
        System.out.println(new String(b,"utf-8"));*/
	}
	
	private static String API_URL = "http://op.juhe.cn/vercode/index";
	private static String KEY = "667beb6bb70b89b24b787426e09dd9b9";
	private static String CODE_TYPE = "8001";
	
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
		byte[] decodeStr = Base64.getDecoder().decode(base64EncodedStr);
		
		Map<String, String> textMap = new HashMap<String, String>();
		textMap.put("key", KEY);
		textMap.put("codeType", CODE_TYPE);
		//获取文件类型
        String contentType = judgeFileType(decodeStr);

        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(decodeStr)); 
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
	 * 图片识别，传入图片地址
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: recognizeImgCodeFromFile 
	 * @param imgPath
	 * @return code if success   or   null if error
	 * @date 2015年12月15日 下午6:30:36  
	 * @author ws
	 */
	public static String recognizeImgCodeFromFile(String imgPath){
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
	 */
	public static String judgeFileType(byte[] decodeResult){
		DataInputStream inputStr = new DataInputStream(new ByteArrayInputStream(decodeResult));
		try {
			return FileTypeJudge.getType(inputStr).name();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
