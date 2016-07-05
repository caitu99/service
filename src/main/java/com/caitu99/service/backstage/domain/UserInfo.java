package com.caitu99.service.backstage.domain;

import java.util.Date;

/**
 * Created by chenhl on 2016/2/17.
 */
public class UserInfo {

    private Long userId;

//    private String IMEI;

    private String ipGpsInfoTable;  //关联ipgps信息表（一个子表），存放子表链接地址

    private Date startDate; //激活日期

//    private boolean isH5User; //是否H5注册用户

    private String accountsTable; //关联账户信息表（包含手机号，微信openid，微信昵称，QQ号，微博号）

//    private boolean isGzh; //是否绑定公众号

    private String phoneInfoTable; //关联手机信息表

    private String aliveInfoTable; //关联启动记录表

    private String inIntegralInfoTable; //关联财分入分记录表

    private String outIntegralInfoTable; //关联财分出分记录表

    private String totalIntegralChangeInfoTable; //关联财分总量变更记录表

    private String integrlAaccountInfoTable; //关联积分管理账户记录表

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

//    public String getIMEI() {
//        return IMEI;
//    }
//
//    public void setIMEI(String IMEI) {
//        this.IMEI = IMEI;
//    }

    public String getIpGpsInfoTable() {
        return ipGpsInfoTable;
    }

    public void setIpGpsInfoTable(String ipGpsInfoTable) {
        this.ipGpsInfoTable = ipGpsInfoTable;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

//    public boolean isH5User() {
//        return isH5User;
//    }
//
//    public void setIsH5User(boolean isH5User) {
//        this.isH5User = isH5User;
//    }

    public String getAccountsTable() {
        return accountsTable;
    }

    public void setAccountsTable(String accountsTable) {
        this.accountsTable = accountsTable;
    }

//    public boolean isGzh() {
//        return isGzh;
//    }
//
//    public void setIsGzh(boolean isGzh) {
//        this.isGzh = isGzh;
//    }

    public String getPhoneInfoTable() {
        return phoneInfoTable;
    }

    public void setPhoneInfoTable(String phoneInfoTable) {
        this.phoneInfoTable = phoneInfoTable;
    }

    public String getAliveInfoTable() {
        return aliveInfoTable;
    }

    public void setAliveInfoTable(String aliveInfoTable) {
        this.aliveInfoTable = aliveInfoTable;
    }

    public String getOutIntegralInfoTable() {
        return outIntegralInfoTable;
    }

    public void setOutIntegralInfoTable(String outIntegralInfoTable) {
        this.outIntegralInfoTable = outIntegralInfoTable;
    }

    public String getInIntegralInfoTable() {
        return inIntegralInfoTable;
    }

    public void setInIntegralInfoTable(String inIntegralInfoTable) {
        this.inIntegralInfoTable = inIntegralInfoTable;
    }

    public String getTotalIntegralChangeInfoTable() {
        return totalIntegralChangeInfoTable;
    }

    public void setTotalIntegralChangeInfoTable(String totalIntegralChangeInfoTable) {
        this.totalIntegralChangeInfoTable = totalIntegralChangeInfoTable;
    }

    public String getIntegrlAaccountInfoTable() {
        return integrlAaccountInfoTable;
    }

    public void setIntegrlAaccountInfoTable(String integrlAaccountInfoTable) {
        this.integrlAaccountInfoTable = integrlAaccountInfoTable;
    }
}
