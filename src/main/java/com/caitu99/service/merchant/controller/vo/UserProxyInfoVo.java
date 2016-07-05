package com.caitu99.service.merchant.controller.vo;

/**
 * todo
 *
 * @author wugaoda
 * @Description: 用户代理信息
 * @ClassName: UserProxyInfoVo
 * @date 2016年06月21日 11:15
 * @Copyright (c) 2015-2020 by caitu99
 */
public class UserProxyInfoVo {
    /**
     * 代理类型 1.代理，2.业务员
     */
    private Long userid;

    private Integer proxyType;

    private String proxyName;

    /**
     * 登录账号
     */
    private String loginAccount;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 联系电话
     */
    private String contact;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public Integer getProxyType() {
        return proxyType;
    }

    public void setProxyType(Integer proxyType) {
        this.proxyType = proxyType;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }
}
