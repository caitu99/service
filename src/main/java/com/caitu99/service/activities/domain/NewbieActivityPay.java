package com.caitu99.service.activities.domain;

import java.util.Date;

public class NewbieActivityPay {
    private Long id;

    private Long userId;

    private String unionPayNo;

    private Long caibi;

    private Long tubi;

    private Long rmb;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;
    
    private Long rightId;
    
    private String orderNo;

    public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getRightId() {
		return rightId;
	}

	public void setRightId(Long rightId) {
		this.rightId = rightId;
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

    public String getUnionPayNo() {
        return unionPayNo;
    }

    public void setUnionPayNo(String unionPayNo) {
        this.unionPayNo = unionPayNo == null ? null : unionPayNo.trim();
    }

    public Long getCaibi() {
        return caibi;
    }

    public void setCaibi(Long caibi) {
        this.caibi = caibi;
    }

    public Long getTubi() {
        return tubi;
    }

    public void setTubi(Long tubi) {
        this.tubi = tubi;
    }

    public Long getRmb() {
        return rmb;
    }

    public void setRmb(Long rmb) {
        this.rmb = rmb;
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