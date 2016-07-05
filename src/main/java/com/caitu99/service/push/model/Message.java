/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.push.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: Message 
 * @author Hongbo Peng
 * @date 2015年12月22日 下午4:19:05 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2374926621663773765L;
	//需要尝试推送
	private Boolean isPush = false;
	//需要尝试短信
	private Boolean isSMS = false;
	//需要展示小黄条
	private Boolean isYellow = false;
	
	//消息中心标题，跟推送的安卓标题不一样
	private String title;
	//推送信息
	private String pushInfo;
	//短信信息
	private String smsInfo;
	//小黄条信息
	private String yellowInfo;

	/**
	 * @return the pushInfo
	 */
	public String getPushInfo() {
		return pushInfo;
	}

	/**
	 * @param pushInfo the pushInfo to set
	 */
	public void setPushInfo(String pushInfo) {
		this.pushInfo = pushInfo;
	}

	/**
	 * @return the smsInfo
	 */
	public String getSmsInfo() {
		return smsInfo;
	}

	/**
	 * @param smsInfo the smsInfo to set
	 */
	public void setSmsInfo(String smsInfo) {
		this.smsInfo = smsInfo;
	}

	/**
	 * @return the isPush
	 */
	public Boolean getIsPush() {
		return isPush;
	}

	/**
	 * @param isPush the isPush to set
	 */
	public void setIsPush(Boolean isPush) {
		this.isPush = isPush;
	}

	/**
	 * @return the isSMS
	 */
	public Boolean getIsSMS() {
		return isSMS;
	}

	/**
	 * @param isSMS the isSMS to set
	 */
	public void setIsSMS(Boolean isSMS) {
		this.isSMS = isSMS;
	}

	/**
	 * @return the isYellow
	 */
	public Boolean getIsYellow() {
		return isYellow;
	}

	/**
	 * @param isYellow the isYellow to set
	 */
	public void setIsYellow(Boolean isYellow) {
		this.isYellow = isYellow;
	}

	/**
	 * @return the yellowInfo
	 */
	public String getYellowInfo() {
		return yellowInfo;
	}

	/**
	 * @param yellowInfo the yellowInfo to set
	 */
	public void setYellowInfo(String yellowInfo) {
		this.yellowInfo = yellowInfo;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public boolean validate(){
		if(isPush && StringUtils.isBlank(pushInfo)){
			return false;
		}
		if(isSMS && StringUtils.isBlank(smsInfo)){
			return false;
		}
		if(isYellow && StringUtils.isBlank(yellowInfo)){
			return false;
		}
		if(StringUtils.isBlank(title)){
			return false;
		}
		return true;
	}
}
