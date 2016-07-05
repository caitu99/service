/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.controller.vo;

import com.caitu99.service.utils.calculate.CalculateUtils;


/** 
 * 抽奖返回结果
 * @Description: (类职责详细描述,可空) 
 * @ClassName: WinningRsult 
 * @author fangjunxiao
 * @date 2015年12月3日 下午4:52:23 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class WinningVo {

	/**0:没有中奖，1：兑换卷，2：财币*/
	private Integer type;
	
	private String name;
	
	private String subTitle;
	
	private Long marketPrice;
	
	private Long salePrice;
	
	private String marketPriceString;
	
	private Long inRecordId;

	private Long activitiesItemId;
	
	private Integer source;
	
	
	
	
	public String getMarketPriceString() {
		return marketPriceString;
	}

	public void setMarketPriceString(String marketPriceString) {
		this.marketPriceString = marketPriceString;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Long getInRecordId() {
		return inRecordId;
	}

	public void setInRecordId(Long inRecordId) {
		this.inRecordId = inRecordId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public Long getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(Long marketPrice) {
		this.marketPrice = marketPrice;
		if (null != marketPrice) {
			this.setMarketPriceString(CalculateUtils.converPennytoYuan(marketPrice));;
		}
	}

	/**
	 * @return the salePrice
	 */
	public Long getSalePrice() {
		return salePrice;
	}

	/**
	 * @param salePrice the salePrice to set
	 */
	public void setSalePrice(Long salePrice) {
		this.salePrice = salePrice;
	}

	/**
	 * @return the activitiesItemId
	 */
	public Long getActivitiesItemId() {
		return activitiesItemId;
	}

	/**
	 * @param activitiesItemId the activitiesItemId to set
	 */
	public void setActivitiesItemId(Long activitiesItemId) {
		this.activitiesItemId = activitiesItemId;
	}

	
	
	
}
