package com.caitu99.service.activities.domain;

import java.util.Date;

public class InRecord {
    private Long id;
    /**活动id*/
    private Long activitiesId;
    /**用户ID*/
    private Long userId;
    /**微信OPEN_ID*/
    private String openId;
    /**参与入口*/
    private Integer source;
    /**是否中奖*/
    private Boolean winning;
    /**奖品ID*/
    private Long activitiesItemId;
    /**奖品名称*/
    private String itemName;
    

    private Long marketPrice;

    private String subTitle;
    
    /**数量*/
    private Integer num;
    /**是否已领奖*/
    private Boolean award;
    /**name*/
    private String name;
    /**phone*/
    private String phone;

    private Date createTime;

    private Date updateTime;

    
    
    
    public Long getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(Long marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Boolean getWinning() {
        return winning;
    }

    public void setWinning(Boolean winning) {
        this.winning = winning;
    }

    public Long getActivitiesItemId() {
        return activitiesItemId;
    }

    public void setActivitiesItemId(Long activitiesItemId) {
        this.activitiesItemId = activitiesItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName == null ? null : itemName.trim();
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Boolean getAward() {
        return award;
    }

    public void setAward(Boolean award) {
        this.award = award;
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