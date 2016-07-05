/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.provider;

import com.caitu99.service.exception.ExpedientException;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.provider.abs.AddExpAbstract;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddExp 
 * @author fangjunxiao
 * @param <T>
 * @param <T>
 * @date 2016年5月26日 下午12:05:29 
 * @Copyright (c) 2015-2020 by caitu99 
 */

public interface AddExp {

	 void addExp(ExpData data,AddExpAbstract addExp) throws ExpedientException;
	
}
