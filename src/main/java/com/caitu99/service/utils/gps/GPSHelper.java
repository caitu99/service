package com.caitu99.service.utils.gps;

/**
 * Created by chenhl on 2016/2/18.
 */
public class GPSHelper {

    /*
    用经纬度查询地址
     */
    public static String GPSAddress(String latlng){
        return GPSAddress.getGPSAdressByLatAndLng(latlng);
    }
}
