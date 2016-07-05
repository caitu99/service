/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.dto;

import com.caitu99.service.activities.domain.ActivitiesItem;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesItemDto 
 * @author fangjunxiao
 * @date 2015年12月2日 下午12:10:53 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ActivitiesItemDto extends ActivitiesItem{

	
	private Integer interval;

	/**
	 * @return the interval
	 */
	public Integer getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}
	
	
}
