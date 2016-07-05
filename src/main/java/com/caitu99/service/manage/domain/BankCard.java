package com.caitu99.service.manage.domain;

import java.util.Date;

public class BankCard {
	
	/** 办理类型:在线办理 */
	public static final Integer TYPE_ON_LINE = 1;
	/** 办理类型:上门办理 */
	public static final Integer TYPE_DROP_IN = 2;
	
    private Long id;

    private String bankName;

    private Integer type;

    private String icon;

    private String url;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String remark;
    
    private Integer cornerMark;

    public Integer getCornerMark() {
		return cornerMark;
	}

	public void setCornerMark(Integer cornerMark) {
		this.cornerMark = cornerMark;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}