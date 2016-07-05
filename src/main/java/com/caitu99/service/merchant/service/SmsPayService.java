/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.merchant.service;

import java.util.List;

import com.caitu99.service.merchant.domain.SmspayItem;
import com.caitu99.service.merchant.domain.SmspayPlatform;
import com.caitu99.service.merchant.domain.SmspayRecord;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: SmsPayService 
 * @author fangjunxiao
 * @date 2016年6月21日 下午4:36:23 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface SmsPayService {

	
	
	 List<SmspayPlatform> findAll();
	 
	 List<SmspayItem> findAllByPlatformId(Long platformId);
	 
	 String saveSmsPayResult(SmspayRecord sr);
	
}
