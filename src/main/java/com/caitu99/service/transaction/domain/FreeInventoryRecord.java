package com.caitu99.service.transaction.domain;

import java.util.Date;

public class FreeInventoryRecord {
    private Long id;

    private Long userId;
    
    private String orderNo;

    private Long freeTradeId;

    private Integer num;

    private Date endTime;

    private Date createTime;

    private Date updateTime;
    
    

    public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

    public Long getFreeTradeId() {
        return freeTradeId;
    }

    public void setFreeTradeId(Long freeTradeId) {
        this.freeTradeId = freeTradeId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}