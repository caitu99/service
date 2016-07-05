package com.caitu99.service.user.controller.vo;

import com.caitu99.service.integral.domain.UserCardManual;

public class UserCardManualVo extends UserCardManual{
	
	private Integer typeId;

	private Float scale;
	
	private String picUrl;

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Float getScale() {
		return scale;
	}

	public void setScale(Float scale) {
		this.scale = scale;
	}
}
