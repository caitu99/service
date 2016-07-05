package com.caitu99.service.backstage.domain;

/**
 * Created by liuzs on 2016/2/17.
 * 产品使用数据
 */
public class ProductUsageData {

    private Long activationNumber;//激活量

    private Long registrations;//注册量

    private Long uninstallNumber;//卸载量

    private String conversionRate;//注册转化率

    public Long getActivationNumber() {
        return activationNumber;
    }

    public void setActivationNumber(Long activationNumber) {
        this.activationNumber = activationNumber;
    }

    public Long getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Long registrations) {
        this.registrations = registrations;
    }

    public Long getUninstallNumber() {
        return uninstallNumber;
    }

    public void setUninstallNumber(Long uninstallNumber) {
        this.uninstallNumber = uninstallNumber;
    }

    public String getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(String conversionRate) {
        this.conversionRate = conversionRate;
    }
}
