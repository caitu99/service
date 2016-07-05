package com.caitu99.service.backstage.domain;

/**
 * Created by lizus on 2016/2/17.
 * 积分变现数据统计
 */
public class IntegRealData {

    private String platform;//平台

    private Long cashNumber;//兑现人数

    private Long cashCount;//兑现次数

    private Long cashAmount;//兑现金额

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Long getCashNumber() {
        return cashNumber;
    }

    public void setCashNumber(Long cashNumber) {
        this.cashNumber = cashNumber;
    }

    public Long getCashCount() {
        return cashCount;
    }

    public void setCashCount(Long cashCount) {
        this.cashCount = cashCount;
    }

    public Long getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Long cashAmount) {
        this.cashAmount = cashAmount;
    }
}
