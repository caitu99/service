/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lottery.vo;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LotteryOrderVo 
 * @author fangjunxiao
 * @date 2016年5月11日 上午10:44:27 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class LotteryOrderVo {
	
	//用户id
	private String fuserid;
	//开发者appKey
	private String appKey;
	//准备扣除的金额，以元为单位，保留两位小数
	private String points;
	//1970-01-01开始的时间戳，毫秒为单位。
	private String timestamp;
	//订单描述
	private String description;
	//彩票订单Id
	private String lotteryOrderId;	
	//所兑换的产品名称
	private String product;
	//市场价值，以元为单位
	private String marketPrice;
	//实际价值，以元为单位
	private String realPrice;
	//保留字段，默认传0
	private String ifReview;
	//产品参数保留字段，默认回传彩票Id
	private Long params;
	//MD5签名
	private String sign;
	
	
	//开发者订单号
	private String bizId;
	//兑换是否成功
	private String resultStatus;
	//通知描述
	private String noticeMessage;
	
	
	
	
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	public String getNoticeMessage() {
		return noticeMessage;
	}
	public void setNoticeMessage(String noticeMessage) {
		this.noticeMessage = noticeMessage;
	}
	public String getFuserid() {
		return fuserid;
	}
	public void setFuserid(String fuserid) {
		this.fuserid = fuserid;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getPoints() {
		return points;
	}
	public void setPoints(String points) {
		this.points = points;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLotteryOrderId() {
		return lotteryOrderId;
	}
	public void setLotteryOrderId(String lotteryOrderId) {
		this.lotteryOrderId = lotteryOrderId;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(String marketPrice) {
		this.marketPrice = marketPrice;
	}
	public String getRealPrice() {
		return realPrice;
	}
	public void setRealPrice(String realPrice) {
		this.realPrice = realPrice;
	}
	public String getIfReview() {
		return ifReview;
	}
	public void setIfReview(String ifReview) {
		this.ifReview = ifReview;
	}
	public Long getParams() {
		return params;
	}
	public void setParams(Long params) {
		this.params = params;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	
	

}
