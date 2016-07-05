package com.caitu99.service.integral.domain;

import java.util.Date;

public class IntegralExchange {
	private Long id;

	private Long userid;

	private Long cardTypeId;

	private Long total;

	private Date time;

	private Integer status;

	private Long tXuserCardTypeId;

	private Integer rate;

	private Float scale;

	private Integer bankRate;

	private Long cattleid;

	public Long getCattleid() {
		return cattleid;
	}

	public void setCattleid(Long cattleid) {
		this.cattleid = cattleid;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

	public Float getScale() {
		return scale;
	}

	public void setScale(Float scale) {
		this.scale = scale;
	}

	public Integer getBankRate() {
		return bankRate;
	}

	public void setBankRate(Integer bankRate) {
		this.bankRate = bankRate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getCardTypeId() {
		return cardTypeId;
	}

	public void setCardTypeId(Long cardTypeId) {
		this.cardTypeId = cardTypeId;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long gettXuserCardTypeId() {
		return tXuserCardTypeId;
	}

	public void settXuserCardTypeId(Long tXuserCardTypeId) {
		this.tXuserCardTypeId = tXuserCardTypeId;
	}
}