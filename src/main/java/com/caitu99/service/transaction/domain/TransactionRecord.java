package com.caitu99.service.transaction.domain;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class TransactionRecord {
	private Long id;

	private String transactionNumber;

	private String orderNo;

	private Long userId;

	private String info;

	private String picUrl;

	/** 1:消费 2：兑入3：充值4：提现5：累积6：赠送
	 * 	7:券过期 8:转账,转入;9:转账,转出10:变现 
	 * 20:银联支付;30：代充（银联智慧）31：代付（银联智慧）
	 */
	private Integer type;
	 /* 1商城  2活动   3赠送  4提现   10话费   21自由交易
	  * 30 彩票
	 */
	private Integer source;

	private Integer payType;

	private Integer status;

	private Long total;
	
	private Long couponIntegral;

	private String comment;

	private Date freezeTime;

	private Date successTime;

	private Date faileTime;

	private Long companyId;
	
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	private Date updateTime;
	//渠道 1.银联 2.聚合数据 3.支付 4.赠送 5.券充值 6.银行卡充值7.自由交易8.积分变现;9积分变现分享红包 10 彩票
	private Integer channel;
	//第三方流水
	private String thirdPartyNumber;
	
	
	private Long tubi;
	
	private Long rmb;
	//处理状态：0.无须处理，1.待处理，2.已处理
	private Integer handleStatus;
	//手续费
	private Long fee;
	//手续费率
	private String feeRate;
	
	public String getThirdPartyNumber() {
		return thirdPartyNumber;
	}
	
	public void setThirdPartyNumber(String thirdPartyNumber) {
		this.thirdPartyNumber = thirdPartyNumber;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber == null ? null
				: transactionNumber.trim();
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo == null ? null : orderNo.trim();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info == null ? null : info.trim();
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl == null ? null : picUrl.trim();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getCouponIntegral() {
		return couponIntegral;
	}

	public void setCouponIntegral(Long couponIntegral) {
		this.couponIntegral = couponIntegral;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment == null ? null : comment.trim();
	}

	public Date getFreezeTime() {
		return freezeTime;
	}

	public void setFreezeTime(Date freezeTime) {
		this.freezeTime = freezeTime;
	}

	public Date getSuccessTime() {
		return successTime;
	}

	public void setSuccessTime(Date successTime) {
		this.successTime = successTime;
	}

	public Date getFaileTime() {
		return faileTime;
	}

	public void setFaileTime(Date faileTime) {
		this.faileTime = faileTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * @return the companyId
	 */
	public Long getCompanyId() {
		return companyId;
	}

	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	/**
	 * @return the tubi
	 */
	public Long getTubi() {
		return tubi;
	}

	/**
	 * @param tubi the tubi to set
	 */
	public void setTubi(Long tubi) {
		if(null == tubi){
			this.tubi = 0L;
		}else{
			this.tubi = tubi;
		}
	}

	/**
	 * @return the rmb
	 */
	public Long getRmb() {
		return rmb;
	}

	/**
	 * @param rmb the rmb to set
	 */
	public void setRmb(Long rmb) {
		if(null == rmb){
			this.rmb = 0L;
		}else{
			this.rmb = rmb;
		}
	}

	public Integer getHandleStatus() {
		return handleStatus;
	}

	public void setHandleStatus(Integer handleStatus) {
		this.handleStatus = handleStatus;
	}

	public Long getFee() {
		return fee;
	}

	public void setFee(Long fee) {
		this.fee = fee;
	}

	public String getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}
	
	
}
