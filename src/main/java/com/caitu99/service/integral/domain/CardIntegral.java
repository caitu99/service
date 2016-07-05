package com.caitu99.service.integral.domain;

import java.util.Date;

public class CardIntegral {
    private Long id;

    private Long cardId;

    private Integer balance;

    private Date gmtEffective;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Date getGmtEffective() {
        return gmtEffective;
    }

    public void setGmtEffective(Date gmtEffective) {
        this.gmtEffective = gmtEffective;
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