/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.vo;

import java.util.Date;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UnionPayOrder 
 * @author ws
 * @date 2016年1月6日 上午11:02:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class UnionPayOrder {
	
	private String mobile;
	private String orderNo;//本方订单号
	private String thirdNo;//第三方流水  对应 transaction_number
	private Integer status;
	private Long integral;//积分
	private Date createTime;
	private Integer type;
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
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}
	/**
	 * @param orderNo the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * @return the thirdNo
	 */
	public String getThirdNo() {
		return thirdNo;
	}
	/**
	 * @param thirdNo the thirdNo to set
	 */
	public void setThirdNo(String thirdNo) {
		this.thirdNo = thirdNo;
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
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	
	
	
}
