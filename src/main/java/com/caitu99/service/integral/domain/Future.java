package com.caitu99.service.integral.domain;

import java.util.Date;

public class Future {
	
	/** 图片验证码ID */
	public final static Long IMAGE_CODE_ID = 6L;
	
	/**
	 * 配置类型:登录页面配置
	 */
	public final static Integer TYPE_LOGIN_VIEW = 0;
	
	/**
	 * 配置类型:登录名配置
	 */
	public final static Integer TYPE_LOGIN_ACCOUNT = 1;
	
	/**
	 * 配置类型:密码配置
	 */
	public final static Integer TYPE_PASSWORD_ACCOUNT = 2;
	
    private Long id;

    private String code;

    private String name;

    private Integer type;

	private Date gmtCreate;

    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }
}