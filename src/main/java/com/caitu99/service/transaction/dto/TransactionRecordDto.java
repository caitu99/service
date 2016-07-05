package com.caitu99.service.transaction.dto;

public class TransactionRecordDto {

	private String transactionNumber;

	private String orderNo;

	private Long userId;

	private Long total;

	private String info;

	private String picUrl;

	/**
	 * 1:消费 2：兑入3：充值4：提现5：累积6：赠送 11：支付 12：退款
	 */
	private Integer type;

	private String comment;
	
	private Long tubi;
	
	private Long rmb;

	/**
	 * 1银联
	 * 2聚合数据
	 * 3支付
	 * 4赠送
	 * 8积分变现
	 */
	private Integer channel;

	// 来源
	/**
	 * 1商城
	 * 2活动
	 * 3赠送
	 * 4提现
	 * 10话费
	 * 21自由交易
	 * 22积分变现
	 * 23积分变现分享红包
	 * 24sdk支付
	 */
	private Integer source;

	/**
	 * @return the transactionNumber
	 */
	public String getTransactionNumber() {
		return transactionNumber;
	}

	/**
	 * @param transactionNumber
	 *            the transactionNumber to set
	 */
	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * @param orderNo
	 *            the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @param info
	 *            the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * @return the picUrl
	 */
	public String getPicUrl() {
		return picUrl;
	}

	/**
	 * @param picUrl
	 *            the picUrl to set
	 */
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the channel
	 */
	public Integer getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	/**
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(Long total) {
		this.total = total;
	}

	/**
	 * @return the source
	 */
	public Integer getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(Integer source) {
		this.source = source;
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
		this.tubi = tubi;
	}

	public Long getRmb() {
		return rmb;
	}

	public void setRmb(Long rmb) {
		this.rmb = rmb;
	}
}