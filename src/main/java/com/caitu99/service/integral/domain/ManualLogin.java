package com.caitu99.service.integral.domain;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class ManualLogin {
	
	/**
	 * 是否传递密码:是
	 */
	public final static Integer IS_PASSWORD_YES = 0;
	/**
	 * 是否传递密码:否
	 */
	public final static Integer IS_PASSWORD_NO = 1;
	/**
	 * 账户类型:卡号
	 */
	public final static Integer TYPE_CARD_NO = 1;
	/**
	 * 账户类型:身份证
	 */
	public final static Integer TYPE_IDENTITY_CARD = 2;
	/**
	 * 账户类型:用户名
	 */
	public final static Integer TYPE_LOGIN_ACCOUNT = 3;
	/**
	 * 账户类型:手机
	 */
	public final static Integer TYPE_PHONE = 4;
	/**
	 * 账户类型:邮箱
	 */
	public final static Integer TYPE_EMAIL = 8;
	
	public ManualLogin() {
		
	}
	
	public ManualLogin(Long userId, Long manualId, String loginAccount, Integer isPassword, Integer type) {
		this.userId = userId;
		this.manualId = manualId;
		this.loginAccount = loginAccount;
		this.isPassword = isPassword;
		this.type = type;
	}
	
	public ManualLogin(Long userId, Long manualId, String loginAccount,String password, Integer isPassword, Integer type) {
		this.userId = userId;
		this.manualId = manualId;
		this.loginAccount = loginAccount;
		this.password = password;
		this.isPassword = isPassword;
		this.type = type;
	}

	@JSONField(serialize=false)
    private Long id;

	@JSONField(serialize=false)
    private Long userId;

	@JSONField(serialize=false)
    private Long manualId;

    private String loginAccount;

    private String password;

	@JSONField(serialize=false)
    private Integer isPassword;

	@JSONField(serialize=false)
	private Integer type;

	@JSONField(serialize=false)
    private Date gmtCreate;

	@JSONField(serialize=false)
    private Date gmtModify;
	
	@JSONField(serialize=false)
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getManualId() {
        return manualId;
    }

    public void setManualId(Long manualId) {
        this.manualId = manualId;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount == null ? null : loginAccount.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getIsPassword() {
        return isPassword;
    }

    public void setIsPassword(Integer isPassword) {
        this.isPassword = isPassword;
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

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		String loginAccount = "";
		if(obj instanceof ManualLogin){
			ManualLogin manualLogin = (ManualLogin)obj;
			loginAccount = manualLogin.getLoginAccount();
		}else if(obj instanceof JSONObject){
			JSONObject manualLogin = (JSONObject)obj;
			loginAccount = manualLogin.getString("loginAccount");
		}
		
		if(loginAccount.equals(this.loginAccount)){
			return true;
		}
		return false;
	}
}