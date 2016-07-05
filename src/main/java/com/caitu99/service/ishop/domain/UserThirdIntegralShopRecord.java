package com.caitu99.service.ishop.domain;

import java.util.Date;

public class UserThirdIntegralShopRecord {
	
	/** 商城类型:建行商城 */
	public final static Integer TYPE_CCB = 1;
	/** 商城类型:天翼商城 */
	public final static Integer TYPE_ESURFING = 2;
	/** 状态:正常 */
	public final static Integer STATUS_NORMAL = 1;
	/** 状态:删除 */
	public final static Integer STATUS_DETELE = -1;
	
    private Long id;

    private Long userId;

    private String loginAccount;

    private String password;

    private String phone;

    private Integer type;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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