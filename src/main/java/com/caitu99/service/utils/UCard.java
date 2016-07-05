package com.caitu99.service.utils;

import java.util.Date;
import java.util.List;

import com.caitu99.service.integral.domain.CardIntegral;

public class UCard {
    private Long id;

    private Long userId;

    private String name;

    private Long cardTypeId;

    private String cardNo;

    private String email;

    private String emailPassword;

    private Integer billDay;

    private Date repaymentDay;

    private Integer integralBalance;

    private Long consumeTotal;

    private Integer maxAmount;

    private Integer commonAmount;

    private Integer cashAmount;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer status;
    
    private Integer typeId;
    
    private Float totalbalance;
    
    private Float minamount;
    
    private List<CardIntegral> cardIntegrals;
    
    private Date billMonth;

    public Integer getBillDay() {
		return billDay;
	}

	public void setBillDay(Integer billDay) {
		this.billDay = billDay;
	}

	public List<CardIntegral> getCardIntegrals() {
		return cardIntegrals;
	}

	public void setCardIntegrals(List<CardIntegral> cardIntegrals) {
		this.cardIntegrals = cardIntegrals;
	}

	public Float getTotalbalance() {
		return totalbalance;
	}

	public void setTotalbalance(Float totalbalance) {
		this.totalbalance = totalbalance;
	}

	public Float getMinamount() {
		return minamount;
	}

	public void setMinamount(Float minamount) {
		this.minamount = minamount;
	}

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(Long cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo == null ? null : cardNo.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword == null ? null : emailPassword.trim();
    }

    public Date getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(Date billMonth) {
		this.billMonth = billMonth;
	}

	public Date getRepaymentDay() {
		return repaymentDay;
	}

	public void setRepaymentDay(Date repaymentDay) {
		this.repaymentDay = repaymentDay;
	}

	public Integer getIntegralBalance() {
        return integralBalance;
    }

    public void setIntegralBalance(Integer integralBalance) {
        this.integralBalance = integralBalance;
    }

    public Long getConsumeTotal() {
        return consumeTotal;
    }

    public void setConsumeTotal(Long consumeTotal) {
        this.consumeTotal = consumeTotal;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Integer getCommonAmount() {
        return commonAmount;
    }

    public void setCommonAmount(Integer commonAmount) {
        this.commonAmount = commonAmount;
    }

    public Integer getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Integer cashAmount) {
        this.cashAmount = cashAmount;
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

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
}