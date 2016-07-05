package com.caitu99.service.manage.domain;

import java.util.Date;

public class ManageCardDropInRecord {
	
	/** 状态:受理失败 */
	public final static Integer STATUS_ACCEPT_FAIL = -1;
	/** 状态:受理中 */
	public final static Integer STATUS_ACCEPT_THE = 0;
	/** 状态:受理成功 */
	public final static Integer STATUS_ACCEPT_SUCCEED = 1;
	
    private Long id;

    private Long userId;

    private Long bankCardId;

    private String bankCardName;

    private String bankCardIcon;

    private String phone;

    private String identityCard;

    private String name;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String companyAddress;

    private String area;

    private String education;

    private String housing;

    private String creditCardInfo;

    private String car;

    private String jobInfo;

    private String companyName;

    private String companyNature;

    private String socialSecurity;

    private String jobCertify;

    private String specialManager;

    private String specialTel;

    private String remark;

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

    public Long getBankCardId() {
        return bankCardId;
    }

    public void setBankCardId(Long bankCardId) {
        this.bankCardId = bankCardId;
    }

    public String getBankCardName() {
        return bankCardName;
    }

    public void setBankCardName(String bankCardName) {
        this.bankCardName = bankCardName == null ? null : bankCardName.trim();
    }

    public String getBankCardIcon() {
        return bankCardIcon;
    }

    public void setBankCardIcon(String bankCardIcon) {
        this.bankCardIcon = bankCardIcon == null ? null : bankCardIcon.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard == null ? null : identityCard.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress == null ? null : companyAddress.trim();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education == null ? null : education.trim();
    }

    public String getHousing() {
        return housing;
    }

    public void setHousing(String housing) {
        this.housing = housing == null ? null : housing.trim();
    }

    public String getCreditCardInfo() {
        return creditCardInfo;
    }

    public void setCreditCardInfo(String creditCardInfo) {
        this.creditCardInfo = creditCardInfo == null ? null : creditCardInfo.trim();
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car == null ? null : car.trim();
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo == null ? null : jobInfo.trim();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName == null ? null : companyName.trim();
    }

    public String getCompanyNature() {
        return companyNature;
    }

    public void setCompanyNature(String companyNature) {
        this.companyNature = companyNature == null ? null : companyNature.trim();
    }

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity == null ? null : socialSecurity.trim();
    }

    public String getJobCertify() {
        return jobCertify;
    }

    public void setJobCertify(String jobCertify) {
        this.jobCertify = jobCertify == null ? null : jobCertify.trim();
    }

    public String getSpecialManager() {
        return specialManager;
    }

    public void setSpecialManager(String specialManager) {
        this.specialManager = specialManager == null ? null : specialManager.trim();
    }

    public String getSpecialTel() {
        return specialTel;
    }

    public void setSpecialTel(String specialTel) {
        this.specialTel = specialTel == null ? null : specialTel.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}