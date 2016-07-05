package com.caitu99.service.free.domain;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class FreeTrade {
	@JSONField(name="freeTradeId")
    private Long id;
	//对应的平台（移动，联通，招商等）
    private Long remoteId;
    //商品编号
    private Long itemId;
    //商品细项编号
    private Long skuId;
    //需要支付的积分（单个）
    private Long payPrice;
    //兑换的价值(财币)
    private Long price;
    //总数量
    private Long totalQuantity;
    //可兑换数量
    private Long availableQuantity;
    //冻结数量
    private Long freezeQuantity;
    //1.普通用户的交易，3.黄牛的交易
    private Integer type;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
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

    public Long getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(Long payPrice) {
        this.payPrice = payPrice;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Long getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Long availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Long getFreezeQuantity() {
        return freezeQuantity;
    }

    public void setFreezeQuantity(Long freezeQuantity) {
        this.freezeQuantity = freezeQuantity;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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