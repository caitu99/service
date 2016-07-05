package com.caitu99.service.backstage.domain;

import java.util.Date;

/**
 * Created by chenhl on 2016/2/17.
 *
 * 手机型号记录
 */
public class PhoneModelInfo {

    private Long ID;

    private String phoneModel; // 手机型号

    private Date changModelTime; //手机变更时间

    private Long UID;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public Date getChangModelTime() {
        return changModelTime;
    }

    public void setChangModelTime(Date changModelTime) {
        this.changModelTime = changModelTime;
    }

    public Long getUID() {
        return UID;
    }

    public void setUID(Long UID) {
        this.UID = UID;
    }
}
