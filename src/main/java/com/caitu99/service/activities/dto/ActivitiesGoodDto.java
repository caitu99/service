/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.dto;

import com.caitu99.service.activities.domain.ActivitiesGood;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesGoodDto 
 * @author fangjunxiao
 * @date 2016年6月7日 下午12:03:38 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ActivitiesGoodDto extends ActivitiesGood{

	
	
	private String name;
	private Long marketPrice;
	private Long salePrice;
	private String picUrl;
	private String wapUrl;
	
	
	private String startTimeStr;
	private String endTimeStr;
	
	private String hourString;
	
	
	private Integer timeStatus;
	private Long  timeSecond;
	
	
	
	public Integer getTimeStatus() {
		return timeStatus;
	}
	public void setTimeStatus(Integer timeStatus) {
		this.timeStatus = timeStatus;
	}
	public Long getTimeSecond() {
		return timeSecond;
	}
	public void setTimeSecond(Long timeSecond) {
		this.timeSecond = timeSecond;
	}
	public String getHourString() {
		return hourString;
	}
	public void setHourString(String hourString) {
		this.hourString = hourString;
	}
	public String getStartTimeStr() {
		return startTimeStr;
	}
	public void setStartTimeStr() {
		if(null != super.getStartTime()){
			this.startTimeStr = DateUtil.getMonthAndDay(super.getStartTime());
		}
	}
	public String getEndTimeStr() {
		return endTimeStr;
	}
	public void setEndTimeStr() {
		if(null != super.getEndTime()){
			this.endTimeStr = DateUtil.getMonthAndDay(super.getEndTime());
		}
	}
	public String getWapUrl() {
		return wapUrl;
	}
	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(Long marketPrice) {
		this.marketPrice = marketPrice;
	}
	public Long getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(Long salePrice) {
		this.salePrice = salePrice;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	
	
	
}
