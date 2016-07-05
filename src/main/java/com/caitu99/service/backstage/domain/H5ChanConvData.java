package com.caitu99.service.backstage.domain;

/**
 * Created by liuzs on 2016/2/17.
 * H5渠道转化数据
 */
public class H5ChanConvData {

    private String channel;//渠道

    private Long visits;//访问量

    private Long registrations;//注册量

    private String conversionRate;//转化率

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Long getVisits() {
        return visits;
    }

    public void setVisits(Long visits) {
        this.visits = visits;
    }

    public Long getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Long registrations) {
        this.registrations = registrations;
    }

    public String getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(String conversionRate) {
        this.conversionRate = conversionRate;
    }
}
