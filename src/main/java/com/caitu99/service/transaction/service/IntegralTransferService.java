/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service;

import com.caitu99.service.transaction.domain.TransactionRecord;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralTransfer 
 * @author ws
 * @date 2015年12月1日 下午4:39:56 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface IntegralTransferService {

	/**
	 * 	划转财币
	 * <p>将企业财币划到用户
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: transferIntegral 
	 * @param userId
	 * @param orderNo
	 * @date 2015年12月1日 下午8:01:04  
	 * @author ws
	*/
	void transferIntegral(Long userId, String orderNo);
	
	/**
	 * 依据交易记录 划转财币
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: transferSingleRecord 
	 * @param userTranRecord
	 * @date 2015年12月1日 下午10:33:53  
	 * @author ws
	 */
	void transferSingleRecord(TransactionRecord userTranRecord);

    /**
     * 转账
     * @param fUserId
     * @param tUserId
     * @param total
     */
	void transferToOtherUser(Long fUserId, Long tUserId, Long total, String stall,String comment);
	
	/**
	 * 用户最早积分变现财分转让
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: transferFreezeIntegral 
	 * @param fUserId		用户ID
	 * @param tUserId		转让用户ID
	 * @param total			金额
	 * @param stall			摊位号
	 * @date 2016年3月9日 下午5:43:52  
	 * @author xiongbin
	 */
	void transferFreezeIntegral(Long fUserId, Long tUserId, Long total,String stall);
}
