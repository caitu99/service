package com.caitu99.service.right.domain;

import java.util.Date;

public class MyRights {
    private Long id;

    private Long userId;

    private String name;

    private String detail;

    private String code;

    private Date gmtCreate;

    private Date gmtDisabled;

    private Long status;

    private Long rightId;
    
    private String createDateStr;
    
    private String disabledDateStr;
    
    private String identity;
    
    private String coupon;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtDisabled() {
        return gmtDisabled;
    }

    public void setGmtDisabled(Date gmtDisabled) {
        this.gmtDisabled = gmtDisabled;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

	/**
	 * @return the createDateStr
	 */
	public String getCreateDateStr() {
		return createDateStr;
	}

	/**
	 * @param createDateStr the createDateStr to set
	 */
	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}

	/**
	 * @return the disabledDateStr
	 */
	public String getDisabledDateStr() {
		return disabledDateStr;
	}

	/**
	 * @param disabledDateStr the disabledDateStr to set
	 */
	public void setDisabledDateStr(String disabledDateStr) {
		this.disabledDateStr = disabledDateStr;
	}

	/**
	 * @return the identity
	 */
	public String getIdentity() {
		return identity;
	}

	/**
	 * @param identity the identity to set
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}

	/**
	 * @return the coupon
	 */
	public String getCoupon() {
		return coupon;
	}

	/**
	 * @param coupon the coupon to set
	 */
	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}
    
}