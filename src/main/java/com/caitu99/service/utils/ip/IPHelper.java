package com.caitu99.service.utils.ip;

/**
 * Created by chenhl on 2016/2/17.
 */
public class IPHelper {

    /*
    查ip归属地信息
     */
    public static String IPAttribution(String ip){
        return IPAttribution.getIPAttribution(ip);
    }
}
