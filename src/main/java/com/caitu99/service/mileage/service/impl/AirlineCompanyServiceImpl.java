/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mileage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.mileage.dao.AirlineCompanyMapper;
import com.caitu99.service.mileage.domain.AirlineCompany;
import com.caitu99.service.mileage.service.AirlineCompanyService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MileageIntegralServiceImpl 
 * @author ws
 * @date 2016年4月27日 下午5:30:43 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class AirlineCompanyServiceImpl implements AirlineCompanyService {

	@Autowired
	AirlineCompanyMapper airlineCompanyMapper;

	/* (non-Javadoc)
	 * @see com.caitu99.service.mileage.service.AirlineCompanyService#selectList()
	 */
	@Override
	public List<AirlineCompany> selectList(Long userid) {
		List<AirlineCompany> list = airlineCompanyMapper.selectList(userid);
		return list;
	}
	
	

}
