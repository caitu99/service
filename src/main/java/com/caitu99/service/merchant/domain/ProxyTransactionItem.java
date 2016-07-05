package com.caitu99.service.merchant.domain;

import java.util.Date;

public class ProxyTransactionItem {
    private Long id;

    private Long proxyTransactionId;

    private Long platformId;

    private String platformName;

    private Long integral;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProxyTransactionId() {
        return proxyTransactionId;
    }

    public void setProxyTransactionId(Long proxyTransactionId) {
        this.proxyTransactionId = proxyTransactionId;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName == null ? null : platformName.trim();
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}