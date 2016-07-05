/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.activities.dao.ActivitiesStatisticsMapper;
import com.caitu99.service.activities.domain.ActivitiesStatistics;
import com.caitu99.service.activities.service.ActivitiesStatisticsService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesStatisticsServiceImpl 
 * @author Hongbo Peng
 * @date 2016年1月11日 上午11:50:01 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class ActivitiesStatisticsServiceImpl implements
		ActivitiesStatisticsService {

	@Autowired
	private ActivitiesStatisticsMapper activitiesStatisticsMapper;
	
	@Override
	public void insert(ActivitiesStatistics activitiesStatistics) {
		activitiesStatisticsMapper.insert(activitiesStatistics);
	}

}
