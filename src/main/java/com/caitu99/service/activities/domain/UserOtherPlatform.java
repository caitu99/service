package com.caitu99.service.activities.domain;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 第三方注册平台类
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserOtherPlatform 
 * @author xiongbin
 * @date 2016年1月6日 下午2:46:56 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Document(collection = "UserOtherPlatform")
public class UserOtherPlatform implements Serializable{
	
	private static final long serialVersionUID = 4241845508727176007L;
	@Id
	private String id;

	private String phone;
	
	private String source;

	private Date gmtCreate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
}
