/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.dto;

import java.util.Date;

import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: EnterpriseDetailDto 
 * @author fangjunxiao
 * @date 2016年5月30日 上午9:19:50 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class EnterpriseDetailDto {
	
	
	private String orderNo;
	
	private String transactionNumber;
	
	private Date createTime;
	
	private String createTimeString;
	
	private String accNo;
	
	private String accName;
	 
	private Integer channel;

	private String channelString;
	
	private Long total;
	
	private String totalString;
	
	private Long fee;
	
	private String feeString;
	
	private Integer status;
	
	private String statusString;
	
	private Integer handleStatus;
	
	private String handleStatusString;
	
	private String typeString;
	
	private Integer type;
	
	
	
	
	public String getTotalString() {
		return totalString;
	}
	public void setTotalString(String totalString) {
		this.totalString = totalString;
	}
	public String getFeeString() {
		return feeString;
	}
	public void setFeeString(String feeString) {
		this.feeString = feeString;
	}
	public Integer getHandleStatus() {
		return handleStatus;
	}
	public void setHandleStatus(Integer handleStatus) {
		//处理状态：0.无须处理，1.待处理，2.已处理
		if(null != handleStatus){
			switch (handleStatus) {
			case 0:
				this.handleStatusString = "无须处理";
				break;
			case 1:
				this.handleStatusString = "待处理";
				break;
			case 2:
				this.handleStatusString = "已处理";
				break;
			default:
				this.handleStatusString = "待处理";
				break;
			}
		}
		
		this.handleStatus = handleStatus;
	}
	public String getHandleStatusString() {
		return handleStatusString;
	}
	public void setHandleStatusString(String handleStatusString) {
		this.handleStatusString = handleStatusString;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		if(null != type){
			switch (type) {
			case 30:
				this.typeString = "代充";
				break;
			case 31:
				this.typeString = "代付";
				break;
			default:
				break;
			}
		}
		this.type = type;
	}
	public String getTypeString() {
		return typeString;
	}
	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		
		if(null != createTime){
			this.createTimeString = DateUtil.DateToString(createTime);
		}
		
		this.createTime = createTime;
	}
	public String getCreateTimeString() {
		return createTimeString;
	}
	public void setCreateTimeString(String createTimeString) {
		this.createTimeString = createTimeString;
	}
	public String getChannelString() {
		return channelString;
	}
	public void setChannelString(String channelString) {
		this.channelString = channelString;
	}
	public String getStatusString() {
		return statusString;
	}
	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getAccName() {
		return accName;
	}
	public void setAccName(String accName) {
		this.accName = accName;
	}
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		if(null!=channel){
			switch (channel) {
			case 1:
				this.channelString = "银联智慧";
				break;

			default:
				break;
			}
		}
		this.channel = channel;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		if(null != total){
			this.totalString = CalculateUtils.converPennytoYuan(total);
		}
		this.total = total;
	}
	public Long getFee() {
		return fee;
	}
	public void setFee(Long fee) {
		if(null != fee){
			this.feeString = CalculateUtils.converPennytoYuan(fee);
		}
		this.fee = fee;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		if(null!=status){
			//-1：失败 0：冻结 1：处理中 2：成功
			switch (status) {
			case 2:
				this.statusString = "成功";
				break;
			case -1:
				this.statusString = "失败";
				break;
			case 1:
				this.statusString = "处理中";
				break;
			default:
				break;
			}
		}
		this.status = status;
	}
	
	
	
	

}
