package com.caitu99.service.activities.domain;

import java.util.Date;

public class ActivitiesItem {
	
	/**兑换卷*/
	public final static Integer ACTIVITIES_ITEM_TYPE_ROLL = 1;
	/**财币*/
	public final static Integer ACTIVITIES_ITEM_TYPE_CF = 2;
	
    private Long id;
    /**活动id*/
    private Long activitiesId;
    /**商品id*/
    private Long itemId;
    /**商品sku_id*/
    private Long skuId;
    /**奖品名称*/
    private String name;
    /**奖品数量*/
    private Integer quantity;
    /**奖品类型    1:兑换卷   2:财币*/
    private Integer type;

    private Long integral;
    
    private Integer lev;
    /**中奖机率*/
    private Integer probability;
    /**奖品详情*/
    private String details;
    /**奖品状态  1:可用*/
    private Integer status;

    private Date createTime;

    private Date updateTime;

    

	public Long getIntegral() {
		return integral;
	}

	public void setIntegral(Long integral) {
		this.integral = integral;
	}

	public static Integer getActivitiesItemTypeRoll() {
		return ACTIVITIES_ITEM_TYPE_ROLL;
	}

	public static Integer getActivitiesItemTypeCf() {
		return ACTIVITIES_ITEM_TYPE_CF;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivitiesId() {
        return activitiesId;
    }

    public void setActivitiesId(Long activitiesId) {
        this.activitiesId = activitiesId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getLev() {
        return lev;
    }

    public void setLev(Integer lev) {
        this.lev = lev;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details == null ? null : details.trim();
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
}