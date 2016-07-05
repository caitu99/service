/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mileage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.mileage.dao.MileageIntegralMapper;
import com.caitu99.service.mileage.domain.MileageIntegral;
import com.caitu99.service.mileage.service.MileageIntegralService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MileageIntegralServiceImpl 
 * @author ws
 * @date 2016年4月27日 下午5:30:43 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class MileageIntegralServiceImpl implements MileageIntegralService {

	@Autowired
	MileageIntegralMapper mileageIntegralMapper;
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.mileage.service.MileageIntegralService#selectListBy(java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<MileageIntegral> selectListBy(Long airCompanyId, Long platformId) {
		Map<String,Long> params = new HashMap<String,Long>();
		params.put("airCompanyId", airCompanyId);
		params.put("platformId", platformId);
		
		List<MileageIntegral> list = new ArrayList<MileageIntegral>();
		list = mileageIntegralMapper.selectListBy(params);
		return list;
	}

}
