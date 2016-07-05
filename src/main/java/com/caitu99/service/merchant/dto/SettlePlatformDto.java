/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.merchant.dto;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: SettleDataDto 
 * @author ws
 * @date 2016年6月21日 上午10:42:36 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class SettlePlatformDto {
	
	private Long id;

    private Long proxyTransactionId;

    private Long platformId;

    private String platformName;

    private Long integral;
    
    private String icon;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the proxyTransactionId
	 */
	public Long getProxyTransactionId() {
		return proxyTransactionId;
	}

	/**
	 * @param proxyTransactionId the proxyTransactionId to set
	 */
	public void setProxyTransactionId(Long proxyTransactionId) {
		this.proxyTransactionId = proxyTransactionId;
	}

	/**
	 * @return the platformId
	 */
	public Long getPlatformId() {
		return platformId;
	}

	/**
	 * @param platformId the platformId to set
	 */
	public void setPlatformId(Long platformId) {
		this.platformId = platformId;
	}

	/**
	 * @return the platformName
	 */
	public String getPlatformName() {
		return platformName;
	}

	/**
	 * @param platformName the platformName to set
	 */
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	/**
	 * @return the integral
	 */
	public Long getIntegral() {
		return integral;
	}

	/**
	 * @param integral the integral to set
	 */
	public void setIntegral(Long integral) {
		this.integral = integral;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
    
    
}
