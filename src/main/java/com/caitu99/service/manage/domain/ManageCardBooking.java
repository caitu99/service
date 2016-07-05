package com.caitu99.service.manage.domain;

import java.util.Date;

public class ManageCardBooking {
    private Long id;

    private String specialManager;

    private String specialTel;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialManager() {
        return specialManager;
    }

    public void setSpecialManager(String specialManager) {
        this.specialManager = specialManager == null ? null : specialManager.trim();
    }

    public String getSpecialTel() {
        return specialTel;
    }

    public void setSpecialTel(String specialTel) {
        this.specialTel = specialTel == null ? null : specialTel.trim();
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}