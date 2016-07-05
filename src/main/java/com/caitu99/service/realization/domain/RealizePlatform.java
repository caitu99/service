package com.caitu99.service.realization.domain;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class RealizePlatform {

	@JSONField(name="realizePlatformId")
    private Long id;

    private String name;

    private Integer type;

    private String icon;

    private String sort;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String version;
    
    private String phoneSupport;
    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort == null ? null : sort.trim();
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

	/**
	 * @return the phoneSupport
	 */
	public String getPhoneSupport() {
		return phoneSupport;
	}

	/**
	 * @param phoneSupport the phoneSupport to set
	 */
	public void setPhoneSupport(String phoneSupport) {
		this.phoneSupport = phoneSupport;
	}

}