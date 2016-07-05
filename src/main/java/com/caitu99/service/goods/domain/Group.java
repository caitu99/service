package com.caitu99.service.goods.domain;

import java.util.Date;

public class Group {
	
	/**热销商品*/
	public final static Integer TYPE_HOT = 1;
	/**商户商品*/
	public final static Integer TYPE_SHOP = 2;
	/**合作商户活动*/
	public final static Integer TYPE_ACTIVITY = 3;
	
    private Long id;
    //业务类型(  1 热销商品 2 商户商品 3合作商户活动)
    private Integer remoteType;
    //业务ID
    private String remoteId;
    
    private String remoteName;
    //图片
    private String imgPath;
    
    private String url;
    //名称
    private String name;
    //标题
    private String title;
    //状态
    private Integer status;
    //排序
    private Long sort;
    
    private Long templateId;
    
    private Date createTime;

    private Date updateTime;
    
    private String discount;
    
    private String reprice;
    

    public String getRemoteName() {
		return remoteName;
	}

	public void setRemoteName(String remoteName) {
		this.remoteName = remoteName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath == null ? null : imgPath.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
	 * @return the reprice
	 */
	public String getReprice() {
		return reprice;
	}

	/**
	 * @param reprice the reprice to set
	 */
	public void setReprice(String reprice) {
		this.reprice = reprice;
	}
    
}