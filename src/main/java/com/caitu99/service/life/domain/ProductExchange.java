package com.caitu99.service.life.domain;

import java.util.Date;

/**
 * 商品-O2O兑换表
 * Modified by Lion
 */
public class ProductExchange {
    private Long id;

    private Long userid;

    private Long productid;

    //激活码id
    private Long activationid;

    //产品兑换时间
    private Date time;

    //产品状态
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getProductid() {
        return productid;
    }

    public void setProductid(Long productid) {
        this.productid = productid;
    }

    public Long getActivationid() {
        return activationid;
    }

    public void setActivationid(Long activationid) {
        this.activationid = activationid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}