/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mail.controller.vo;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserCardTypeVo 
 * @author ws
 * @date 2015年12月16日 下午8:27:10 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class UserCardTypeVo {
	
	private Long userId;
	private Long cardTypeId;
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
