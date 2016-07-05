package com.caitu99.service.transaction.domain;

import java.util.Date;

public class FreeTradeOrder {
    private Long id;

    private String moneyOrderNo;

    private String goodOrderNo;

    private String outNo;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMoneyOrderNo() {
        return moneyOrderNo;
    }

    public void setMoneyOrderNo(String moneyOrderNo) {
        this.moneyOrderNo = moneyOrderNo == null ? null : moneyOrderNo.trim();
    }

    public String getGoodOrderNo() {
        return goodOrderNo;
    }

    public void setGoodOrderNo(String goodOrderNo) {
        this.goodOrderNo = goodOrderNo == null ? null : goodOrderNo.trim();
    }

    public String getOutNo() {
        return outNo;
    }

    public void setOutNo(String outNo) {
        this.outNo = outNo == null ? null : outNo.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}