/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.controller.vo;

import java.util.Date;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesVo 
 * @author fangjunxiao
 * @date 2015年12月2日 下午10:55:00 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ActivitiesVo {

	
	private Long userId;
	
	private Long activitiesId;
	/**页面请求来源*/
	private Integer source;
	
	private Date nowTime;
	
	private Long stockId;
	
	private String subTitle;
	
	private Long marketPrice;
	
	private Long salePrice;
	/**中奖记录ID*/
	private Long inRecordId;
	
	private Long addIntegral;
	
	private Boolean award;
	
	private Integer receiveStockStatus;
	
	
	
	

	public Integer getReceiveStockStatus() {
		return receiveStockStatus;
	}
	public void setReceiveStockStatus(Integer receiveStockStatus) {
		this.receiveStockStatus = receiveStockStatus;
	}
	public Boolean getAward() {
		return award;
	}
	public void setAward(Boolean award) {
		this.award = award;
	}
	public Long getInRecordId() {
		return inRecordId;
	}
	public void setInRecordId(Long inRecordId) {
		this.inRecordId = inRecordId;
	}
	public Long getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(Long salePrice) {
		this.salePrice = salePrice;
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
	}
	public Long getAddIntegral() {
		return addIntegral;
	}
	public void setAddIntegral(Long addIntegral) {
		this.addIntegral = addIntegral;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getActivitiesId() {
		return activitiesId;
	}
	public void setActivitiesId(Long activitiesId) {
		this.activitiesId = activitiesId;
	}
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
	public Date getNowTime() {
		return nowTime;
	}
	public void setNowTime(Date nowTime) {
		this.nowTime = nowTime;
	}
	public Long getStockId() {
		return stockId;
	}
	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}
	
	
}
