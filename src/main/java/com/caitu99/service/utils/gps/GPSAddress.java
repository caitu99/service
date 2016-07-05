package com.caitu99.service.utils.gps;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by chenhl on 2016/2/18.
 */
public class GPSAddress {

    private static String API_GPSAddress_URL = "http://api.map.baidu.com/geocoder?output=json&key=8cb976834235d8cbcde2dce4835ae191";

    /*
    *用经纬度查地址信息
    *param : "30.76623,120.43213"
     */
    public static String getGPSAdressByLatAndLng(String latlng){
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = API_GPSAddress_URL + "&location=" + latlng;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

}
