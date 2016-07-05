package com.caitu99.service.transaction.domain;

import java.util.Date;

public class UserCoupon {
    private Long id;

    private Long couponId;

    private String code;

    private Long userId;

    private Long sendIntegral;

    private Long useIntegral;

    private Long availableIntegral;

    private Integer status;

    private Date receiveTime;

    private Date useTime;

    private Date overdueTime;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSendIntegral() {
        return sendIntegral;
    }

    public void setSendIntegral(Long sendIntegral) {
        this.sendIntegral = sendIntegral;
    }

    public Long getUseIntegral() {
        return useIntegral;
    }

    public void setUseIntegral(Long useIntegral) {
        this.useIntegral = useIntegral;
    }

    public Long getAvailableIntegral() {
        return availableIntegral;
    }

    public void setAvailableIntegral(Long availableIntegral) {
        this.availableIntegral = availableIntegral;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public Date getOverdueTime() {
        return overdueTime;
    }

    public void setOverdueTime(Date overdueTime) {
        this.overdueTime = overdueTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}