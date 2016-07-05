package com.caitu99.service.user.domain;

import java.util.Date;

public class ImportMailError {
    private Long id;

    private String mail;

    private String password;

    private String pwdalone;

    private Date errorDate;

    private String errorMsg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail == null ? null : mail.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPwdalone() {
        return pwdalone;
    }

    public void setPwdalone(String pwdalone) {
        this.pwdalone = pwdalone == null ? null : pwdalone.trim();
    }

    public Date getErrorDate() {
        return errorDate;
    }

    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg == null ? null : errorMsg.trim();
    }
}