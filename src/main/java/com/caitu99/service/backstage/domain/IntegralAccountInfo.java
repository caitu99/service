package com.caitu99.service.backstage.domain;

/**
 * Created by chenhl on 2016/2/17.
 *
 * 积分管理账户记录表
 */
public class IntegralAccountInfo {

    private Long ID;

    private String account;

    private String belongTo; //所属平台 银行/航空/电商

    private Long UID;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }

    public Long getUID() {
        return UID;
    }

    public void setUID(Long UID) {
        this.UID = UID;
    }
}
