/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.api;

import com.caitu99.service.transaction.controller.vo.AccountResult;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UnionPayRechargeDirectApi 
 * @author ws
 * @date 2016年1月11日 上午11:56:17 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface UnionPayRechargeDirectApi {
	/**
	 * 银联充值，直冲模式
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: rechargeDirect 
	 * @param userid
	 * @param unionid
	 * @param integral
	 * @param orderNo
	 * @param tNo
	 * @param clientId
	 * @date 2016年1月11日 上午11:57:42  
	 * @author ws
	 */
	public AccountResult rechargeDirect(Long userid, Long unionid,
			Long integral, String orderNo, String tNo, Long clientId);
}
