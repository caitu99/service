/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.dto;

import java.util.List;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserDto 
 * @author fangjunxiao
 * @date 2016年4月27日 上午11:32:36 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class UserDto {

	/**用户数*/
	private Integer userTotal;
	/**有效用户数*/
	private Integer activeUser;
	/**有效用户人均帐户数*/
	private String avgAccount;
	/**查询率*/
	private String queryRate;
	
	
	private List<ManualDto> manualDtoList;
	
	

	public List<ManualDto> getManualDtoList() {
		return manualDtoList;
	}

	public void setManualDtoList(List<ManualDto> manualDtoList) {
		this.manualDtoList = manualDtoList;
	}

	public String getAvgAccount() {
		return avgAccount;
	}

	public void setAvgAccount(String avgAccount) {
		this.avgAccount = avgAccount;
	}

	public String getQueryRate() {
		return queryRate;
	}

	public void setQueryRate(String queryRate) {
		this.queryRate = queryRate;
	}


	public Integer getActiveUser() {
		return activeUser;
	}

	public void setActiveUser(Integer activeUser) {
		this.activeUser = activeUser;
	}

	public Integer getUserTotal() {
		return userTotal;
	}

	public void setUserTotal(Integer userTotal) {
		this.userTotal = userTotal;
	}
	
	
	
}
