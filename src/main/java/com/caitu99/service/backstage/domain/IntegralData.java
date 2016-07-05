package com.caitu99.service.backstage.domain;

/**
 * Created by zhouxi on 2016/2/15.
 */
public class IntegralData {

    private Long userIncreaseIntegral = 0L; //用户入分

    private Long giftIntegral = 0L; //送分

    private Long testIncreaseIntegral = 0L; //测试入分

    private Long userReduceIntegral = 0L; //用户出分

    private Long testReduceIntegarl = 0L; //测试出分

    private Long increaseIntegralTotal = 0L;; //入分汇总

    private Long reduceIntegralTotal = 0L; //出分汇总

    private Long totalChange = 0L; //财币变动

    private Long totalIntegral = 0L; //平台总财币

    public Long getGiftIntegral() {
        return giftIntegral;
    }

    public void setGiftIntegral(Long giftIntegral) {
        this.giftIntegral = giftIntegral;
    }

    public Long getUserIncreaseIntegral() {
        return userIncreaseIntegral;
    }

    public void setUserIncreaseIntegral(Long userIncreaseIntegral) {
        this.userIncreaseIntegral = userIncreaseIntegral;
    }

    public Long getTestIncreaseIntegral() {
        return testIncreaseIntegral;
    }

    public void setTestIncreaseIntegral(Long testIncreaseIntegral) {
        this.testIncreaseIntegral = testIncreaseIntegral;
    }

    public Long getUserReduceIntegral() {
        return userReduceIntegral;
    }

    public void setUserReduceIntegral(Long userReduceIntegral) {
        this.userReduceIntegral = userReduceIntegral;
    }

    public Long getTestReduceIntegarl() {
        return testReduceIntegarl;
    }

    public void setTestReduceIntegarl(Long testReduceIntegarl) {
        this.testReduceIntegarl = testReduceIntegarl;
    }

    public Long getIncreaseIntegralTotal() {
        return increaseIntegralTotal;
    }

    public void setIncreaseIntegralTotal(Long increaseIntegralTotal) {
        this.increaseIntegralTotal = increaseIntegralTotal;
    }

    public Long getReduceIntegralTotal() {
        return reduceIntegralTotal;
    }

    public void setReduceIntegralTotal(Long reduceIntegralTotal) {
        this.reduceIntegralTotal = reduceIntegralTotal;
    }

    public Long getTotalChange() {
        return totalChange;
    }

    public void setTotalChange(Long totalChange) {
        this.totalChange = totalChange;
    }

    public Long getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(Long totalIntegral) {
        this.totalIntegral = totalIntegral;
    }


}
