/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mileage.service;

import java.util.List;

import com.caitu99.service.mileage.domain.AirlineCompany;
import com.caitu99.service.mileage.domain.UserAirline;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MileageIntegralService 
 * @author ws
 * @date 2016年4月27日 下午5:30:23 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface UserAirlineService {

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUser 
	 * @param userid
	 * @return
	 * @date 2016年4月28日 上午9:32:20  
	 * @author ws
	*/
	List<UserAirline> selectByUser(Long userid,Long airlineCompanyId);

	
}
