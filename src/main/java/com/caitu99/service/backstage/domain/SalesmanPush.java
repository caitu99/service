package com.caitu99.service.backstage.domain;


import java.util.Date;

/**
 * Created by liuzs on 2016/3/14.
 * 地推业务员摊位关联
 */
public class SalesmanPush {

    private Long id;

    private String stall;//摊位号

    private String mobile;//手机号

    private Date gmtCreate;//创建时间

    private Date gmtModify;//修改时间

    private Integer status;//状态

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStall() {
        return stall;
    }

    public void setStall(String stall) {
        this.stall = stall;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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
}
