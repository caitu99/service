package com.caitu99.service.goods.domain;

import java.util.Date;

public class Item {
	
	/**
	 * 商品状态:上架
	 */
	public final static Integer STATUS_NORMAL = 1;
	/**
	 * 商品状态:下架
	 */
	public final static Integer STATUS_SOLDOUT = 0;
	/**
	 * 商品状态:删除
	 */
	public final static Integer STATUS_DELETE = -1;
	
    private Long itemId;

    private String title;

    private String subTitle;

    private String itemNo;

    private Long brandId;

    private Long salePrice;

    private Long marketPrice;

    private Long saleVolume;

    private String version;

    private Integer status;

    private String picUrl;

    private Date listTime;

    private Date delistTime;

    private String wapUrl;

    private Long sort;

    private Date createTime;

    private Date updateTime;

    private String content;
    
    /***
     * 1:自己的商品，
		2: 为其他商家引流的商品，
		3.移动商城 
		4.联通商城 
		5.中信商城 
		6.中信特卖 
		
		7 建设商城普通商品 
		8 建设特卖 
		
		9 交通商城普通商品 
		10 交通特卖
		
		11 招商商城普通商品
		12 招商特卖
		
		13 天翼商城普通商品 
		14 天翼特卖 
		
		15 移动特卖商品
		
		10001 中信变现
		10002 建设变现
		10003 交通变现
		10004 移动变现
		10005 电信变现
		10006 联通变现
     */
    private Integer source;

    private String sourceToString;

    //@销售方式（1001:财币  1002:人民币   2001:自带积分 2002:积分+rmb )
    private String salesType;

    //@货币单位（财币，建行积分，交行积分）
    private String monetaryUnitToString;
    
    //每次限购数量
    private Integer limitNum;
    
    //1.虚拟商品，2.实物商品
    private Integer itemType;
    //是否支持自由交易(0.不支持1.支持)
    private Integer isFreeTrade;
    //折扣(单位 %  7折为70)
    private String discount;
    //自由交易的价格
    private Long freeTradePrice;
    //积分兑财币时，能兑多少钱（单位财币）
    private Long exchangePrice;
    /**自有商品*/
    public final static Integer SOURCE_MY = 1;//
    /**引流商品*/
    public final static Integer SOURCE_OTHER = 2;//
    /**移动*/
    public final static Integer SOURCE_MOVE = 3; 
    /**联通商城**/
    public final static Integer SOURCE_UNICON = 4;//
    /**中信商城**/
    public final static Integer SOURCE_ZHONGXIN = 5;//
    /**中信商城特卖**/
    public final static Integer SOURCE_ZHONGXIN_EX = 6;//
    /**建设**/
    public final static Integer SOURCE_JS = 7;
    /** 建设特卖**/
    public final static Integer SOURCE_JS_EX = 8;
    /***交通*/
    public final static Integer SOURCE_JT = 9;
    /***交通特卖*/
    public final static Integer SOURCE_JT_EX = 10;
    /**招商*/
    public final static Integer SOURCE_ZS = 11;
    /** 招商特卖*/
    public final static Integer SOURCE_ZS_EX = 12;
    /**天翼**/
    public final static Integer SOURCE_TY = 13;
    /**天翼特卖**/
    public final static Integer SOURCE_TY_EX = 14;
    /**平安**/
    public final static Integer SOURCE_PAB = 16;

    public String getMonetaryUnitToString() {
        return monetaryUnitToString;
    }

    public void setMonetaryUnitToString(String monetaryUnitToString) {
        this.monetaryUnitToString = monetaryUnitToString;
    }

    public Integer getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getSalesType() {
		return salesType;
	}

	public void setSalesType(String salesType) {
		this.salesType = salesType;
	}

	public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle == null ? null : subTitle.trim();
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo == null ? null : itemNo.trim();
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
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

    public Long getSaleVolume() {
        return saleVolume;
    }

    public void setSaleVolume(Long saleVolume) {
        this.saleVolume = saleVolume;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl == null ? null : picUrl.trim();
    }

    public Date getListTime() {
        return listTime;
    }

    public void setListTime(Date listTime) {
        this.listTime = listTime;
    }

    public Date getDelistTime() {
        return delistTime;
    }

    public void setDelistTime(Date delistTime) {
        this.delistTime = delistTime;
    }

    public String getWapUrl() {
        return wapUrl;
    }

    public void setWapUrl(String wapUrl) {
        this.wapUrl = wapUrl == null ? null : wapUrl.trim();
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getSourceToString() {
        return sourceToString;
    }

    public void setSourceToString(String sourceToString) {
        this.sourceToString = sourceToString;
    }

	/**
	 * @return the itemType
	 */
	public Integer getItemType() {
		return itemType;
	}

	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}

	/**
	 * @return the isFreeTrade
	 */
	public Integer getIsFreeTrade() {
		return isFreeTrade;
	}

	/**
	 * @param isFreeTrade the isFreeTrade to set
	 */
	public void setIsFreeTrade(Integer isFreeTrade) {
		this.isFreeTrade = isFreeTrade;
	}
	
	/**
	 * @return the discount
	 */
	public String getDiscount() {
		return discount;
	}

	/**
	 * @param discount the discount to set
	 */
	public void setDiscount(String discount) {
		this.discount = discount;
	}

	/**
	 * @return the freeTradePrice
	 */
	public Long getFreeTradePrice() {
		return freeTradePrice;
	}

	/**
	 * @param freeTradePrice the freeTradePrice to set
	 */
	public void setFreeTradePrice(Long freeTradePrice) {
		this.freeTradePrice = freeTradePrice;
	}

	/**
	 * @return the exchangePrice
	 */
	public Long getExchangePrice() {
		return exchangePrice;
	}

	/**
	 * @param exchangePrice the exchangePrice to set
	 */
	public void setExchangePrice(Long exchangePrice) {
		this.exchangePrice = exchangePrice;
	}

}
