package com.caitu99.service.backstage.domain;

import java.util.Date;

/**
 * Created by chenhl on 2016/2/17.
 */
public class IPGPSInfo {

    private Long ID;

    private Long UID;

    private String IP;  //IP地址

    private String IPBelong;  //IP归属地

    private Date time;

    private String IMEI;

    private String GPSAddress;

    private String isp;  //运营商

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getIPBelong() {
        return IPBelong;
    }

    public void setIPBelong(String IPBelong) {
        this.IPBelong = IPBelong;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public Long getUID() {
        return UID;
    }

    public void setUID(Long UID) {
        this.UID = UID;
    }

    public String getGPSAddress() {
        return GPSAddress;
    }

    public void setGPSAddress(String GPSAddress) {
        this.GPSAddress = GPSAddress;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }
}
