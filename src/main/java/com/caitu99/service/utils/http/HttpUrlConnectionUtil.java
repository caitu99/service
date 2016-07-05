package com.caitu99.service.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author lawrence
 * @Description: (类职责详细描述, 可空)
 * @ClassName: HttpUrlConnectionUtil
 * @date 2015年11月2日 下午4:39:04
 * @Copyright (c) 2015-2020 by caitu99
 */
public class HttpUrlConnectionUtil {

    /**
     * 通过HttpURLConnection发起请求
     *
     * @param _url
     * @param charset
     * @return
     * @throws IOException
     * @Description: (方法职责详细描述, 可空)
     * @Title: httpURLGet
     * @date 2015年11月2日 下午4:34:57
     * @author lawrence
     */
    public static String httpURLGet(String _url, String charset) throws IOException {
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            StringBuffer buffer = new StringBuffer();
            String userAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.3; WOW64; Trident/7.0; LCJB)";// 模拟浏览器
            URL url = new URL(_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("User-agent", userAgent);
            connection.connect();
            inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                buffer.append(strRead);
                buffer.append("\r\n");
            }
            return buffer.toString();

        } finally {
            if (null != reader) {
                reader.close();
            }
            if (null != connection) {
                connection.disconnect();
            }
            if (null != inputStream) {
                inputStream.close();
            }
        }
    }
}
