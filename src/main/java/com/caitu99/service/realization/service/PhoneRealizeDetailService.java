/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.realization.service;

import java.util.Map;

import com.caitu99.service.realization.domain.PhoneRealizeDetail;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PhoneRealizeDetailService 
 * @author ws
 * @date 2016年4月13日 下午5:51:13 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface PhoneRealizeDetailService {

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectBy 
	 * @param platformId
	 * @param amountId
	 * @date 2016年4月13日 下午5:54:30  
	 * @author ws
	 * @return 
	*/
	PhoneRealizeDetail selectBy(Long platformId, Long amountId);
	
	
	
	Map<String,String> queryAccountByPrice(Long userid,Long amountId);

}
