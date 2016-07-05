/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service;

/** 
 * 
 * @Description: (银行卡充值积分) 
 * @ClassName: IntegralRehargeService 
 * @author Hongbo Peng
 * @date 2015年12月10日 下午3:23:18 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface IntegralRechargeService {

	/**
	 * @Description: (银行卡充值财币)  
	 * @Title: integralReharge 
	 * @param userId
	 * @param paypass
	 * @param integral
	 * @return
	 * @date 2015年12月10日 下午4:29:26  
	 * @author Hongbo Peng
	 */
	String integralReharge(Long userId,String paypass,Long integral);
	
	/**
	 * @Description: (查询银行卡支付状态)  
	 * @Title: query 
	 * @param id
	 * @return
	 * @date 2015年12月10日 下午6:09:35  
	 * @author Hongbo Peng
	 */
	String query(Long id);
}
