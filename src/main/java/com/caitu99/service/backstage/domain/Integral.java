package com.caitu99.service.backstage.domain;


import java.util.Date;

public class Integral {
    private Date date;
    //用户入分
    private Long userAddIntegral;
    //用户出分
    private Long userReduceIntegral;
    //送分
    private Long deliveryIntegral;
    //测试入分
    private Long testAddIntegral;
    //测试出分
    private Long testReduceIntegral;
    //入分总汇
    private Long totalAddIntegral;
    //出分总汇
    private Long totalReduceIntegral;
    //积分变动
    private Long changeIntegral;
    //平台总财币
    private Long totalIntegral;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getUserAddIntegral() {
        return userAddIntegral;
    }

    public void setUserAddIntegral(Long userAddIntegral) {
        this.userAddIntegral = userAddIntegral;
    }

    public Long getUserReduceIntegral() {
        return userReduceIntegral;
    }

    public void setUserReduceIntegral(Long userReduceIntegral) {
        this.userReduceIntegral = userReduceIntegral;
    }

    public Long getDeliveryIntegral() {
        return deliveryIntegral;
    }

    public void setDeliveryIntegral(Long deliveryIntegral) {
        this.deliveryIntegral = deliveryIntegral;
    }

    public Long getTestAddIntegral() {
        return testAddIntegral;
    }

    public void setTestAddIntegral(Long testAddIntegral) {
        this.testAddIntegral = testAddIntegral;
    }

    public Long getTestReduceIntegral() {
        return testReduceIntegral;
    }

    public void setTestReduceIntegral(Long testReduceIntegral) {
        this.testReduceIntegral = testReduceIntegral;
    }

    public Long getTotalAddIntegral() {
        return totalAddIntegral;
    }

    public void setTotalAddIntegral(Long totalAddIntegral) {
        this.totalAddIntegral = totalAddIntegral;
    }

    public Long getTotalReduceIntegral() {
        return totalReduceIntegral;
    }

    public void setTotalReduceIntegral(Long totalReduceIntegral) {
        this.totalReduceIntegral = totalReduceIntegral;
    }

    public Long getChangeIntegral() {
        return changeIntegral;
    }

    public void setChangeIntegral(Long changeIntegral) {
        this.changeIntegral = changeIntegral;
    }

    public Long getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(Long totalIntegral) {
        this.totalIntegral = totalIntegral;
    }
}
