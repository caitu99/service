/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.dto;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualDto 
 * @author fangjunxiao
 * @date 2016年4月28日 下午3:34:19 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ManualDto {
	
	private Long manualId; 
	
	private String name;
	
	private Integer ptAccount;
	
	private Integer ptUser;
	
	
	private String ptUserPercent;
	
	private String ptAccountPercent;
	
	

	public String getPtAccountPercent() {
		return ptAccountPercent;
	}

	public void setPtAccountPercent(String ptAccountPercent) {
		this.ptAccountPercent = ptAccountPercent;
	}

	public String getPtUserPercent() {
		return ptUserPercent;
	}

	public void setPtUserPercent(String ptUserPercent) {
		this.ptUserPercent = ptUserPercent;
	}

	public Long getManualId() {
		return manualId;
	}

	public void setManualId(Long manualId) {
		this.manualId = manualId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPtAccount() {
		return ptAccount;
	}

	public void setPtAccount(Integer ptAccount) {
		this.ptAccount = ptAccount;
	}

	public Integer getPtUser() {
		return ptUser;
	}

	public void setPtUser(Integer ptUser) {
		this.ptUser = ptUser;
	}
	
	

}
