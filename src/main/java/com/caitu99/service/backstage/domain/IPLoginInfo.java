package com.caitu99.service.backstage.domain;

import java.util.Date;

/**
 * Created by chenhl on 2016/2/17.
 *
 * IP登录地点记录
 */
public class IPLoginInfo {

    private Long ID;

    private String IPLoginAddress; //登录地点

    private Date loginTime; //登录时间

    private Long UID;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getLoginAddress() {
        return IPLoginAddress;
    }

    public void setLoginAddress(String loginAddress) {
        this.IPLoginAddress = loginAddress;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Long getUID() {
        return UID;
    }

    public void setUID(Long UID) {
        this.UID = UID;
    }
}
