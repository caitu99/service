/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.realization.dto;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PhoneDetailDto 
 * @author fangjunxiao
 * @date 2016年6月14日 上午10:28:35 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class PhoneDetailDto{

	private Long amountId;
	
	private Long tubi;
	private Long caibi;
	private Long rmb;
	
	private String name;
	
	
	
	
	public Long getAmountId() {
		return amountId;
	}
	public void setAmountId(Long amountId) {
		this.amountId = amountId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getTubi() {
		return tubi;
	}
	public void setTubi(Long tubi) {
		this.tubi = tubi;
	}
	public Long getCaibi() {
		return caibi;
	}
	public void setCaibi(Long caibi) {
		this.caibi = caibi;
	}
	public Long getRmb() {
		return rmb;
	}
	public void setRmb(Long rmb) {
		this.rmb = rmb;
	}
	
	
	
	
}
