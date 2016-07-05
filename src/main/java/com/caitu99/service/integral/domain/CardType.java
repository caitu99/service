package com.caitu99.service.integral.domain;

import java.math.BigDecimal;
import java.util.Date;

public class CardType {
    private Long id;

    private String name;
    
    private String company;

    private String content;

    private BigDecimal scale;

    private Integer typeId;

    private Long companyId;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer status;
    
    private Long manualId;
    
    private Long remoteId;

	private String urlThird;
    
    private String url;
    
    private Integer defaultSubscribe;
    
    private Long realizePlatformId;
    
    private String belongTo;
    
    private String sort;

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(String belongTo) {
		this.belongTo = belongTo;
	}

	public Long getRealizePlatformId() {
		return realizePlatformId;
	}

	public void setRealizePlatformId(Long realizePlatformId) {
		this.realizePlatformId = realizePlatformId;
	}

	public Long getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(Long remoteId) {
		this.remoteId = remoteId;
	}
	
    public Integer getDefaultSubscribe() {
		return defaultSubscribe;
	}

	public void setDefaultSubscribe(Integer defaultSubscribe) {
		this.defaultSubscribe = defaultSubscribe;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlThird() {
		return urlThird;
	}

	public void setUrlThird(String urlThird) {
		this.urlThird = urlThird;
	}

	public Long getManualId() {
		return manualId;
	}

	public void setManualId(Long manualId) {
		this.manualId = manualId;
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
   
    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }
    
    public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}