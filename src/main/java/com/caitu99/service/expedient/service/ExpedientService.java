/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.service;

import com.caitu99.service.exception.ExpedientException;
import com.caitu99.service.expedient.domain.Expedient;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ExpedientService 
 * @author fangjunxiao
 * @date 2016年6月1日 上午10:00:23 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface ExpedientService {

	
	Expedient getExpedientByUserId(Long userId) throws ExpedientException;
	
	
	void addExpRecord(Long userId,Long exp,Integer source,String note) throws ExpedientException;
}
