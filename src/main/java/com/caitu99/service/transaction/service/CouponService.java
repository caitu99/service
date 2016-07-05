/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service;

/** 
 * 
 * @Description: (新客券) 
 * @ClassName: CouponService 
 * @author Hongbo Peng
 * @date 2015年12月7日 下午12:06:38 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface CouponService {

	
	/**
	 * @Description: (领取新客券)  
	 * @Title: receiveCoupon 
	 * @param userId
	 * @param code
	 * @return
	 * @date 2015年12月7日 下午12:08:50  
	 * @author Hongbo Peng
	 */
	String receiveCoupon(Long userId,String code);
	
	/**
	 * @Description: (新客券到期)  
	 * @Title: overdueCoupon 
	 * @param id
	 * @return
	 * @date 2015年12月7日 下午12:09:56  
	 * @author Hongbo Peng
	 */
	String overdueCoupon(Long id);
}
