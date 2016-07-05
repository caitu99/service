package com.caitu99.service.user.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4241845508727176007L;

	private Long id;

	private String qq;
	
	private String nick;

	private String weibo;

	private String openid;

	private String imgurl;

	private String zfbName;

	private String mobile;

	private Integer loginCount;

	private String password;

	private String rePassword;

	private String province;

	private String city;

	private Integer integral;

	private Date gmtCreate;

	private Date gmtModify;

	private Integer status;

	private String contacts;

	private String sessionid;
	
	private String paypass;
	
	private String rePaypass;
	
	private String qqNick;

    private String weiboNick;

    private String wechatNick;
	
	private Integer isauth;
	
	private Integer type;
	
	private String openidBinding;
	
	private String invitationCode;
	
	private Integer ishn;
	
	private Integer isGive;

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	public String getOpenidBinding() {
		return openidBinding;
	}

	public void setOpenidBinding(String openidBinding) {
		this.openidBinding = openidBinding;
	}

	public Integer getIsauth() {
		return isauth;
	}

	public void setIsauth(Integer isauth) {
		this.isauth = isauth;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq == null ? null : qq.trim();
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick == null ? null : nick.trim();
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo == null ? null : weibo.trim();
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid == null ? null : openid.trim();
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl == null ? null : imgurl.trim();
	}

	public String getZfbName() {
		return zfbName;
	}

	public void setZfbName(String zfbName) {
		this.zfbName = zfbName == null ? null : zfbName.trim();
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	public Integer getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province == null ? null : province.trim();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city == null ? null : city.trim();
	}

	public Integer getIntegral() {
		return integral;
	}

	public void setIntegral(Integer integral) {
		this.integral = integral;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts == null ? null : contacts.trim();
	}

	public String getRePassword() {
		return rePassword;
	}

	public void setRePassword(String rePassword) {
		this.rePassword = rePassword;
	}

	public String getPaypass() {
		return paypass;
	}

	public void setPaypass(String paypass) {
		this.paypass = paypass == null ? null : paypass.trim();
	}

	public String getRePaypass() {
		return rePaypass;
	}

	public void setRePaypass(String rePaypass) {
		this.rePaypass = rePaypass;
	}

	public String getQqNick() {
		return qqNick;
	}

	public void setQqNick(String qqNick) {
		this.qqNick = qqNick;
	}

	public String getWeiboNick() {
		return weiboNick;
	}

	public void setWeiboNick(String weiboNick) {
		this.weiboNick = weiboNick;
	}

	public String getWechatNick() {
		return wechatNick;
	}

	public void setWechatNick(String wechatNick) {
		this.wechatNick = wechatNick;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	
	/**
	 * 生成邀请码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: generateInvitationCode 
	 * @return
	 * @date 2015年12月30日 下午8:33:25  
	 * @author xiongbin
	 */
	public static String generateInvitationCode(){
		return generateWord(6);
	}
	
	private static String generateWord(int length) {
		final String allChar = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return sb.toString();
    }

	/**
	 * @return the ishn
	 */
	public Integer getIshn() {
		return ishn;
	}

	/**
	 * @param ishn the ishn to set
	 */
	public void setIshn(Integer ishn) {
		this.ishn = ishn;
	}

	public Integer getIsGive() {
		return isGive;
	}

	public void setIsGive(Integer isGive) {
		this.isGive = isGive;
	}
}
