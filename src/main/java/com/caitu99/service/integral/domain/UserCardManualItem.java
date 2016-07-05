package com.caitu99.service.integral.domain;

import java.util.Date;

public class UserCardManualItem {
    private Long id;

    private Long userCardManualId;

    private Integer expirationIntegral;

    private Date expirationTime;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserCardManualId() {
        return userCardManualId;
    }

    public void setUserCardManualId(Long userCardManualId) {
        this.userCardManualId = userCardManualId;
    }

    public Integer getExpirationIntegral() {
        return expirationIntegral;
    }

    public void setExpirationIntegral(Integer expirationIntegral) {
        this.expirationIntegral = expirationIntegral;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }
}