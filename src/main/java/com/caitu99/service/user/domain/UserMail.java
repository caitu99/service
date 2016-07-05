package com.caitu99.service.user.domain;

import java.util.Date;

public class UserMail {
    private Long id;

    private Long userId;

    private String email;

    private String emailPassword;

    private Integer flag;

    private Integer isforward;

    private String forwardMail;

    private String forwardPassword;

    private Date gmtLastUpdate;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer status;
    
    private Integer isNeedRevoke;
    
    private String emailPasswordAlone;

    public String getEmailPasswordAlone() {
		return emailPasswordAlone;
	}

	public void setEmailPasswordAlone(String emailPasswordAlone) {
		this.emailPasswordAlone = emailPasswordAlone;
	}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword == null ? null : emailPassword.trim();
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getIsforward() {
        return isforward;
    }

    public void setIsforward(Integer isforward) {
        this.isforward = isforward;
    }

    public String getForwardMail() {
        return forwardMail;
    }

    public void setForwardMail(String forwardMail) {
        this.forwardMail = forwardMail == null ? null : forwardMail.trim();
    }

    public String getForwardPassword() {
        return forwardPassword;
    }

    public void setForwardPassword(String forwardPassword) {
        this.forwardPassword = forwardPassword == null ? null : forwardPassword.trim();
    }

    public Date getGmtLastUpdate() {
        return gmtLastUpdate;
    }

    public void setGmtLastUpdate(Date gmtLastUpdate) {
        this.gmtLastUpdate = gmtLastUpdate;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public Integer getIsNeedRevoke() {
		return isNeedRevoke;
	}

	public void setIsNeedRevoke(Integer isNeedRevoke) {
		this.isNeedRevoke = isNeedRevoke;
	}
    
}