package com.caitu99.service.integral.domain;

import java.util.Date;

public class ExchangeRule {
    private Long id;

    private Long cardTypeId;

    private Integer way;

    private Float scale;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer status;

    private String scaleStr;

    private Long exchangeRateId;
    
    private String cardTypeName;
    
    //图标的相对路径
    private String cardTypePic;

    public String getScaleStr() {
		return scaleStr;
	}

	public void setScaleStr(String scaleStr) {
		this.scaleStr = scaleStr;
	}

	public String getCardTypeName() {
		return cardTypeName;
	}

	public void setCardTypeName(String cardTypeName) {
		this.cardTypeName = cardTypeName;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(Long cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public Integer getWay() {
        return way;
    }

    public void setWay(Integer way) {
        this.way = way;
    }

    public Float getScale() {
        return scale;
    }

    public void setScale(Float scale) {
        this.scale = scale;
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

    public Long getExchangeRateId() {
        return exchangeRateId;
    }

    public void setExchangeRateId(Long exchangeRateId) {
        this.exchangeRateId = exchangeRateId;
    }

	public String getCardTypePic() {
		return cardTypePic;
	}

	public void setCardTypePic(String cardTypePic) {
		this.cardTypePic = cardTypePic;
	}
}