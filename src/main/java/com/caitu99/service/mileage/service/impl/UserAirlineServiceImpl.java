/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mileage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.mileage.dao.UserAirlineMapper;
import com.caitu99.service.mileage.domain.UserAirline;
import com.caitu99.service.mileage.service.UserAirlineService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MileageIntegralServiceImpl 
 * @author ws
 * @date 2016年4月27日 下午5:30:43 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class UserAirlineServiceImpl implements UserAirlineService {

	@Autowired
	UserAirlineMapper userAirlineMapper;

	/* (non-Javadoc)
	 * @see com.caitu99.service.mileage.service.UserAirlineService#selectByUser(java.lang.Long)
	 */
	@Override
	public List<UserAirline> selectByUser(Long userid,Long airlineCompanyId) {
		Map<String,Long> paramMap = new HashMap<String,Long>();
		paramMap.put("userid", userid);
		paramMap.put("airlineCompanyId", airlineCompanyId);
		return userAirlineMapper.selectByUser(paramMap);
	}

	

}
