package com.caitu99.service.integral.domain;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class Manual {
	
	/** 天翼  */
	public final static Long TIANYI_ID = 4L;
	/** 国航 */
	public final static Long AIRCHINA_ID = 5L;
	
	@JSONField(name="manualId")
    private Long id;

    private String name;

    private Integer type;

    private String icon;

	@JSONField(serialize=false)
    private Integer status;

    private String sort;

	@JSONField(serialize=false)
	private String url;

	@JSONField(serialize=false)
	private Date gmtCreate;

	@JSONField(serialize=false)
    private Date gmtModify;

	private Long version;

    /**
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(Long version) {
		this.version = version;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
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
}