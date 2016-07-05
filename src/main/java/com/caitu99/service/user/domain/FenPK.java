package com.caitu99.service.user.domain;

import java.util.Date;

public class FenPK implements Comparable<FenPK>{
    private Integer id;

    private Integer userid;

    private Integer inviterid;

    private Long groupIntegral;

    private Long totalIntegral;

    private Long awardImportMail;

    private Long awardLoginApp;

    private Long integral;

    private Date time;

    private Long rank;

    private String nick;

    private String headurl;

    private String inviterNick;

    private String inviterHeadurl;

    private String valueImgurl;

    private String valueDescription;

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

    public Long getGroupIntegral() {
    	if (null == groupIntegral) {
			return 0L;
		}
        return groupIntegral;
    }

    public void setGroupIntegral(Long groupIntegral) {
        this.groupIntegral = groupIntegral;
    }

    public Long getTotalIntegral() {
    	if (null == totalIntegral) {
			return 0L;
		}
        return totalIntegral;
    }

    public void setTotalIntegral(Long totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public Long getAwardImportMail() {
    	if (null == awardImportMail) {
			return 0L;
		}
        return awardImportMail;
    }

    public void setAwardImportMail(Long awardImportMail) {
        this.awardImportMail = awardImportMail;
    }

    public Long getAwardLoginApp() {
    	if (null == awardLoginApp) {
			return 0L;
		}
        return awardLoginApp;
    }

    public void setAwardLoginApp(Long awardLoginApp) {
        this.awardLoginApp = awardLoginApp;
    }

    public Long getIntegral() {
    	if (null == integral) {
			return 0L;
		}
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

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick == null ? null : nick.trim();
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl == null ? null : headurl.trim();
    }

    public String getInviterNick() {
        return inviterNick;
    }

    public void setInviterNick(String inviterNick) {
        this.inviterNick = inviterNick == null ? null : inviterNick.trim();
    }

    public String getInviterHeadurl() {
        return inviterHeadurl;
    }

    public void setInviterHeadurl(String inviterHeadurl) {
        this.inviterHeadurl = inviterHeadurl == null ? null : inviterHeadurl.trim();
    }

    public String getValueImgurl() {
        return valueImgurl;
    }

    public void setValueImgurl(String valueImgurl) {
        this.valueImgurl = valueImgurl == null ? null : valueImgurl.trim();
    }

    public String getValueDescription() {
        return valueDescription;
    }

    public void setValueDescription(String valueDescription) {
        this.valueDescription = valueDescription == null ? null : valueDescription.trim();
    }

    @Override
	public int compareTo(FenPK o) {
		Long other = o.getTotalIntegral();
		return other.compareTo(this.getTotalIntegral());
	}
}