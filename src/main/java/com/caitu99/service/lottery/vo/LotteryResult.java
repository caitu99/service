/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lottery.vo;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.lottery.utils.Tool;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LotteryResult 
 * @author fangjunxiao
 * @date 2016年5月11日 下午3:53:47 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class LotteryResult {
	
		private String status;
		private String errorMessage;
		private String bizId;
		private String credits;
		
		
		public void set(String status,String errorMessage,String bizId,String credits){
			this.setStatus(status);
			this.setErrorMessage(errorMessage);
			this.setBizId(bizId);
			this.setCredits(credits);
		}
		
		
		public String toJSONString(String status,String errorMessage,String bizId,String credits){
			this.set(status, errorMessage,bizId,credits);
			return this.toString();
		}
		
		@Override
		public String toString() {
			System.out.println(JSON.toJSONString(this));
	        return JSON.toJSONString(this);
	    }
		
		
		
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getErrorMessage() {
			return errorMessage;
		}
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = Tool.UTF8String(errorMessage);
		}
		public String getBizId() {
			return bizId;
		}
		public void setBizId(String bizId) {
			this.bizId = bizId;
		}
		public String getCredits() {
			return credits;
		}
		public void setCredits(String credits) {
			this.credits = credits;
		}
		
		
		

}
