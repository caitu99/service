package com.caitu99.service.user.domain;

import java.util.Date;

public class MailDetail {
    private Long id;

    private String email;

    private Long userId;

    private String mailTitle;

    private String mailBankName;

    private Date mailDate;

    private String mailBody;
    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMailTitle() {
        return mailTitle;
    }

    public void setMailTitle(String mailTitle) {
        this.mailTitle = mailTitle == null ? null : mailTitle.trim();
    }

    public String getMailBankName() {
        return mailBankName;
    }

    public void setMailBankName(String mailBankName) {
        this.mailBankName = mailBankName == null ? null : mailBankName.trim();
    }

    public Date getMailDate() {
        return mailDate;
    }

    public void setMailDate(Date mailDate) {
        this.mailDate = mailDate;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody == null ? null : mailBody.trim();
    }
}