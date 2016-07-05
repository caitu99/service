package com.caitu99.service.realization.domain;

import java.util.Date;
import java.util.Random;

public class RealizeShareRecord {
	private Long id;

    private Long realizeShareId;

    private Long money;

    private Long userId;

    private Integer platform;

    private Long realizeRecordId;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;
    
    private Integer type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRealizeShareId() {
        return realizeShareId;
    }

    public void setRealizeShareId(Long realizeShareId) {
        this.realizeShareId = realizeShareId;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Long getRealizeRecordId() {
        return realizeRecordId;
    }

    public void setRealizeRecordId(Long realizeRecordId) {
        this.realizeRecordId = realizeRecordId;
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
    
    
    
    public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	/**
     * 生成分享随机金额(财币)
     * @Description: (方法职责详细描述,可空)  
     * @Title: shareAlgorithm 
     * @return
     * @date 2016年4月12日 下午12:00:22  
     * @author xiongbin
     */
    public static Long shareAlgorithm(){
    	Random r = new Random();
    	int i = r.nextInt(100);
    	
    	return i+1L;
    }
}