/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.push.model.enums;


/**
 * @Description: (类职责详细描述,可空) 
 * @ClassName: RedSpot 
 * @author Hongbo Peng
 * @date 2015年12月22日 下午4:09:41 
 * @Copyright (c) 2015-2020 by caitu99
 */
public enum RedSpot {

	//信用卡积分
	CREDIT_INTEGRAL(101),
	//商旅卡积分
	BUSINESS_INTEGRAL(102),
	//购物卡积分
	SHOPING_INTEGRAL(103),
	//消息中心
	MESSAGE_CENTER(401),
	//订单
	ORDER(402),
	//我的礼券
	GIFT_CERTIFICATE(403);
	
	private Integer value = 0;

	/**
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Integer value) {
		this.value = value;
	}

	/** 
	 * @Title:  
	 * @Description:
	 * @param value 
	 */
	private RedSpot(Integer value) {
		this.value = value;
	}
}
