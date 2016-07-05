/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mileage.service;

import java.util.List;

import com.caitu99.service.mileage.domain.MileageIntegral;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MileageIntegralService 
 * @author ws
 * @date 2016年4月27日 下午5:30:23 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface MileageIntegralService {

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectListBy 
	 * @param airCompanyId
	 * @param platformId
	 * @return
	 * @date 2016年4月27日 下午5:34:03  
	 * @author ws
	*/
	List<MileageIntegral> selectListBy(Long airCompanyId, Long platformId);

}
