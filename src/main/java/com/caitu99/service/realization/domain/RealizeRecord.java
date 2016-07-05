package com.caitu99.service.realization.domain;

import java.util.Date;

public class RealizeRecord {
    private Long id;

    private Long userId;

    private Long realizeId;

    private Long platformId;

    private Long realizeDetailId;

    private Long itemId;

    private Long skuId;

    private Integer quantity;

    private Long integral;

    private Long cash;
    
    private Long tubi;

    private Integer status;
    
    private String memo;
    
    private String orderNo;

    private Date createTime;

    private Date updateTime;

    private Date transferTime;
    
    private Long userTermId;
    

    public Long getUserTermId() {
		return userTermId;
	}

	public void setUserTermId(Long userTermId) {
		this.userTermId = userTermId;
	}

	public Long getTubi() {
		return tubi;
	}

	public void setTubi(Long tubi) {
		this.tubi = tubi;
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

    public Long getRealizeId() {
        return realizeId;
    }

    public void setRealizeId(Long realizeId) {
        this.realizeId = realizeId;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getRealizeDetailId() {
        return realizeDetailId;
    }

    public void setRealizeDetailId(Long realizeDetailId) {
        this.realizeDetailId = realizeDetailId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    public Long getCash() {
        return cash;
    }

    public void setCash(Long cash) {
        this.cash = cash;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Date getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(Date transferTime) {
        this.transferTime = transferTime;
    }

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
}