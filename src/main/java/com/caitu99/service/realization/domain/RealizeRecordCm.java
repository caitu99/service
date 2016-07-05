package com.caitu99.service.realization.domain;

import java.util.Date;

public class RealizeRecordCm {
    private Long id;

    private Long userId;
    
    private String account;

    private Long realizeDetailId;

    private Long integral;

    private Integer level;

    private String wltAccount;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Long getRealizeDetailId() {
        return realizeDetailId;
    }

    public void setRealizeDetailId(Long realizeDetailId) {
        this.realizeDetailId = realizeDetailId;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getWltAccount() {
        return wltAccount;
    }

    public void setWltAccount(String wltAccount) {
        this.wltAccount = wltAccount == null ? null : wltAccount.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}