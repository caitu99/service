package com.caitu99.service.goods.domain;

import java.util.Date;

public class ReceiveStock {
	
	/**回退*/
    public static final Integer GIVEBACK = -1;      
    /**预领取*/
    public static final Integer PRERECEIVE = 1;     
    /**领取*/
    public static final Integer RECEIVE = 2;        
    
    
    
    //1 购买 2 赠送 
    /**抽奖*/
    public static final Integer LUCKYDRAW = 3;
    

    private Long id;

    private Long userId;

    private Long stockId;

    private Integer remoteType;

    private String remoteId;

    private String name;

    private String phone;

    private Long salePrice;

    private Long marketPrice;

    private Integer status;

    private Date receiveTime;

    private Date createTime;

    private Date updateTime;

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

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Integer getRemoteType() {
        return remoteType;
    }

    public void setRemoteType(Integer remoteType) {
        this.remoteType = remoteType;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId == null ? null : remoteId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Long getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Long salePrice) {
        this.salePrice = salePrice;
    }

    public Long getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Long marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
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