package com.caitu99.service.backstage.domain;

/**
 * Created by liuzs on 2016/2/17.
 *注册-使用转化统计
 */
public class UseProportion {

    private Long totalRegistrations;//总注册量

    private Long usage;//功能使用用户

    private String conversionRate;//注册-使用转化率

    public Long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(Long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public Long getUsage() {
        return usage;
    }

    public void setUsage(Long usage) {
        this.usage = usage;
    }

    public String getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(String conversionRate) {
        this.conversionRate = conversionRate;
    }
}
