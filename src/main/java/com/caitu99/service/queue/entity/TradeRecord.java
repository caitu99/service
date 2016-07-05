/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.queue.entity;

import java.util.Date;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: TradeRecod 
 * @author ws
 * @date 2015年11月30日 下午7:24:51 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class TradeRecord {
	
	private String userId;
	private Date createTime;
	private String thirdId;
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * @return the thirdId
	 */
	public String getThirdId() {
		return thirdId;
	}
	/**
	 * @param thirdId the thirdId to set
	 */
	public void setThirdId(String thirdId) {
		this.thirdId = thirdId;
	}
	
	
}
