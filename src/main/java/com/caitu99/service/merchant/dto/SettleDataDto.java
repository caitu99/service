/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.merchant.dto;

import java.util.Date;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: SettleDataDto 
 * @author ws
 * @date 2016年6月21日 上午10:42:36 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class SettleDataDto {
	
	private Long id;

    private Long userId;

    private Long empUserId;

    private Long integral;

    private String rate;

    private String startTime;

    private String endTime;
    
    private Integer status;
    
    private String empUserName;
    
    private String mobile;
    
    private String province;
    
    private String city;

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
	 * @return the empUserId
	 */
	public Long getEmpUserId() {
		return empUserId;
	}

	/**
	 * @param empUserId the empUserId to set
	 */
	public void setEmpUserId(Long empUserId) {
		this.empUserId = empUserId;
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
	 * @return the rate
	 */
	public String getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(String rate) {
		this.rate = rate;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the empUserName
	 */
	public String getEmpUserName() {
		return empUserName;
	}

	/**
	 * @param empUserName the empUserName to set
	 */
	public void setEmpUserName(String empUserName) {
		this.empUserName = empUserName;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
    
    
}
