package com.caitu99.service.user.domain;

import java.util.Date;

public class WeixinFenPK {
    private Integer id;

    private Integer userid;

    private Integer inviterid;

    private Long totalIntegral;

    private Long awardImportMail;

    private Long awardLoginApp;

    private Long integral;

    private Date time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getInviterid() {
        return inviterid;
    }

    public void setInviterid(Integer inviterid) {
        this.inviterid = inviterid;
    }

    public Long getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(Long totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public Long getAwardImportMail() {
        return awardImportMail;
    }

    public void setAwardImportMail(Long awardImportMail) {
        this.awardImportMail = awardImportMail;
    }

    public Long getAwardLoginApp() {
        return awardLoginApp;
    }

    public void setAwardLoginApp(Long awardLoginApp) {
        this.awardLoginApp = awardLoginApp;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}