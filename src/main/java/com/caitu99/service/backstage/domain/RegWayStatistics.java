package com.caitu99.service.backstage.domain;

/**
 * Created by liuzs on 2016/2/17.
 * 注册方式统计
 */
public class RegWayStatistics {

    private String registeredWay;//注册方式

    private Long mobile;//手机号注册量

    private Long wechat;//微信注册量

    private Long weibo;//微博注册量

    private Long qq;//qq注册量

    private Long totalRegistrations;//总注册量

    private String proportion;//第三方占比

    public String getRegisteredWay() {
        return registeredWay;
    }

    public void setRegisteredWay(String registeredWay) {
        this.registeredWay = registeredWay;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    public Long getWechat() {
        return wechat;
    }

    public void setWechat(Long wechat) {
        this.wechat = wechat;
    }

    public Long getWeibo() {
        return weibo;
    }

    public void setWeibo(Long weibo) {
        this.weibo = weibo;
    }

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public Long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(Long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }
}
