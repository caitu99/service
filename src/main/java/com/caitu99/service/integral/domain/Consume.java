package com.caitu99.service.integral.domain;

import java.util.Date;

public class Consume {
	private Long id;

	private Long userid;

	private Integer integral;

	private Long usetype;

	private Date gmtCreate;

	private Integer regulation;

	private Integer status;

	private String useid;

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

	public Integer getIntegral() {
		return integral;
	}

	public void setIntegral(Integer integral) {
		this.integral = integral;
	}

	public Long getUsetype() {
		return usetype;
	}

	public void setUsetype(Long usetype) {
		this.usetype = usetype;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Integer getRegulation() {
		return regulation;
	}

	public void setRegulation(Integer regulation) {
		this.regulation = regulation;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUseid() {
		return useid;
	}

	public void setUseid(String useid) {
		this.useid = useid == null ? null : useid.trim();
	}
}