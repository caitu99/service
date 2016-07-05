package com.caitu99.service.realization.domain;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class Realize {
	
	@JSONField(name="realizeId")
    private Long id;

    private Long platformId;

    private Long integral;

    private Long cash;

    private String propotion;

    private Integer sort;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    public Long getCash() {
        return cash;
    }

    public void setCash(Long cash) {
        this.cash = cash;
    }

    public String getPropotion() {
        return propotion;
    }

    public void setPropotion(String propotion) {
        this.propotion = propotion == null ? null : propotion.trim();
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}