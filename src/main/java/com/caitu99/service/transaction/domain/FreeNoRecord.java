package com.caitu99.service.transaction.domain;

import java.util.Date;

public class FreeNoRecord {
    private Long id;

    private Long userId;

    private String noList;

    private String orderNo;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNoList() {
        return noList;
    }

    public void setNoList(String noList) {
        this.noList = noList == null ? null : noList.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}