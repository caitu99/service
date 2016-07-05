/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lianlianpay.service;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LianlianQueryService 
 * @author ws
 * @date 2016年6月14日 下午2:54:21 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface LianlianQueryService {

	String getCardBin(String cardNo);

	/**
	 * 退款请求处理
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: refund 
	 * @param noRefund
	 * @param dtRefund
	 * @param moneyRefund
	 * @param notifyUrl
	 * @return	code为0表示处理成功，code为-1表示处理失败
	 * @date 2016年6月16日 上午9:19:32  
	 * @author ws
	 */
	String refund(String noRefund, String dtRefund, Double moneyRefund, String oidPaybill, String notifyUrl);
}
