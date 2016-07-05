/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.push.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PushMessage 
 * @author Hongbo Peng
 * @date 2015年12月22日 下午12:04:33 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Document(collection = "pushMessage")
public class PushMessage implements Serializable{
	
	private static final long serialVersionUID = 1652381450956152170L;
	@Id
	private String id;
	//小红点编号
	private Integer redId;
	//类型1.单推，2.群推
	private Integer type;
	//渠道1.push,2短信
	private Integer channel;
	//是否小米推送
	private Boolean isPush;
	//是否发送短信
	private Boolean isSMS;
	//推送的目的用户
	private Long userId;
	//标题
	private String title;
	//描述
	private String description;
	//内容
	private String payload;
	//短信消息
	private String smsInfo;
	//状态0.未发送 1.未读2.已读 -1.删除
	private Integer status;
	//发送时间
	private Date sendTime;
	//读取时间
	private Date readTime;
	//创建时间
	private Date createTime;
	//修改时间
	private Date updateTime;

	private String sendTimeStr;
	
	/**
	 * @return the sendTimeStr
	 */
	public String getSendTimeStr() {
		return sendTimeStr;
	}

	/**
	 * @param sendTimeStr the sendTimeStr to set
	 */
	public void setSendTimeStr(String sendTimeStr) {
		this.sendTimeStr = sendTimeStr;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the redId
	 */
	public Integer getRedId() {
		return redId;
	}

	/**
	 * @param redId the redId to set
	 */
	public void setRedId(Integer redId) {
		this.redId = redId;
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
	 * @return the channel
	 */
	public Integer getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
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

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the payload
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * @param payload the payload to set
	 */
	public void setPayload(String payload) {
		this.payload = payload;
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
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the sendTime
	 */
	public Date getSendTime() {
		return sendTime;
	}

	/**
	 * @param sendTime the sendTime to set
	 */
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * @return the readTime
	 */
	public Date getReadTime() {
		return readTime;
	}

	/**
	 * @param readTime the readTime to set
	 */
	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
