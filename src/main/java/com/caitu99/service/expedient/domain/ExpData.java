/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.domain;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ExpData 
 * @author fangjunxiao
 * @date 2016年5月26日 下午5:04:03 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ExpData {
	
	private Long userId;
	
	private String integralResult;
	
	private Long continuousTime;
	
	private Long cash;
	
	private Long inegral;
	
	
	private Long exp;
	
	private Integer source;
	
	


	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Long getExp() {
		return exp;
	}

	public void setExp(Long exp) {
		this.exp = exp;
	}


	public String getIntegralResult() {
		return integralResult;
	}

	public void setIntegralResult(String integralResult) {
		this.integralResult = integralResult;
	}

	public Long getInegral() {
		return inegral;
	}

	public void setInegral(Long inegral) {
		this.inegral = inegral;
	}

	public Long getCash() {
		return cash;
	}

	public void setCash(Long cash) {
		this.cash = cash;
	}

	public Long getContinuousTime() {
		return continuousTime;
	}

	public void setContinuousTime(Long continuousTime) {
		this.continuousTime = continuousTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	

}
