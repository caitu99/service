package com.caitu99.service.integral.domain;

import java.util.Date;

public class ManualResult {
    private Long userId;

    private Long manualId;
    
    private String loginAccount;

    private Long cardTypeId;

    private String cardTypeName;

    private String cardNo;

    private String userName;

    private String picUrl;

    private String result;

    private String remark;
    
    private Date updateTime;
    
    private String addIntegral;
    
    private String addTubi;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getManualId() {
        return manualId;
    }

    public void setManualId(Long manualId) {
        this.manualId = manualId;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName == null ? null : cardTypeName.trim();
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo == null ? null : cardNo.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl == null ? null : picUrl.trim();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

	/**
	 * @return the loginAccount
	 */
	public String getLoginAccount() {
		return loginAccount;
	}

	/**
	 * @param loginAccount the loginAccount to set
	 */
	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * @return the addIntegral
	 */
	public String getAddIntegral() {
		return addIntegral;
	}

	/**
	 * @param addIntegral the addIntegral to set
	 */
	public void setAddIntegral(String addIntegral) {
		this.addIntegral = addIntegral;
	}

	/**
	 * @return the addTubi
	 */
	public String getAddTubi() {
		return addTubi;
	}

	/**
	 * @param addTubi the addTubi to set
	 */
	public void setAddTubi(String addTubi) {
		this.addTubi = addTubi;
	}

	/**
	 * @return the cardTypeId
	 */
	public Long getCardTypeId() {
		return cardTypeId;
	}

	/**
	 * @param cardTypeId the cardTypeId to set
	 */
	public void setCardTypeId(Long cardTypeId) {
		this.cardTypeId = cardTypeId;
	}
	
}