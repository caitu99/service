package com.caitu99.service.goods.domain;

import java.util.Date;

public class Stock {
	/**过期*/
    public static final Integer OUTOFDATE = -1;
    /**下架*/
    public static final Integer NOTONSALE = 0;
    /**上架*/
    public static final Integer ONSALE = 1;
    /**出售*/
    public static final Integer SOLDED = 2;
    /**抽奖*/
    public static final Integer LUCKYDRAW = 3;

    private Long stockId;

    private Long itemId;

    private Long skuId;

    private String code;
    
    private String password;

    private Integer status;

    private String version;

    private Date effectiveTime;

    private Date saleTime;

    private Date createTime;



    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(Date saleTime) {
        this.saleTime = saleTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


}
