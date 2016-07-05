/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.backstage.dto;

import com.caitu99.service.backstage.domain.SalesmanPushRelation;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: salesmanDto 
 * @author fangjunxiao
 * @date 2016年4月26日 下午2:33:57 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class SalesmanDto extends SalesmanPushRelation{
	
	private Integer stalls;
	
	private Integer totalIntegral;
	
	public Integer getStalls() {
		return stalls;
	}
	public void setStalls(Integer stalls) {
		this.stalls = stalls;
	}
	public Integer getTotalIntegral() {
		return totalIntegral;
	}
	public void setTotalIntegral(Integer totalIntegral) {
		this.totalIntegral = totalIntegral;
	}
	
	

}
