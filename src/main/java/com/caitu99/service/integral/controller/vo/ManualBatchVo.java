/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.vo;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualBatchVo 
 * @author ws
 * @date 2016年2月23日 下午8:11:10 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ManualBatchVo {
	private Long userId;
	private Long manualId;
	private String loginAccount;
	private String password;
	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @return the manualId
	 */
	public Long getManualId() {
		return manualId;
	}
	/**
	 * @param manualId the manualId to set
	 */
	public void setManualId(Long manualId) {
		this.manualId = manualId;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
