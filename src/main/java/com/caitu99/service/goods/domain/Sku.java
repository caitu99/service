package com.caitu99.service.goods.domain;

import java.util.Date;

public class Sku {
	
    private Long skuId;

    private Long itemId;

    private Long salePrice;
    
    private Long costPrice;

    private String version;

    private Date createTime;

    private Date updateTime;
    //@属性集合
    private String propCode;
    //@属性集合名称
    private String propName;
    //@折后积分
    private Long disPrice;
    //@折后人民币
    private Long rmbPrice;
    
    
    
    
    public Long getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(Long costPrice) {
		this.costPrice = costPrice;
	}

	public String getPropCode() {
		return propCode;
	}

	public void setPropCode(String propCode) {
		this.propCode = propCode;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public Long getDisPrice() {
		return disPrice;
	}

	public void setDisPrice(Long disPrice) {
		this.disPrice = disPrice;
	}

	public Long getRmbPrice() {
		return rmbPrice;
	}

	public void setRmbPrice(Long rmbPrice) {
		this.rmbPrice = rmbPrice;
	}

	public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Long salePrice) {
        this.salePrice = salePrice;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
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