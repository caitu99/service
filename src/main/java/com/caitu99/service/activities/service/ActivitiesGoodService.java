/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.service;

import java.util.List;
import java.util.Map;

import com.caitu99.service.activities.dto.ActivitiesGoodDto;
import com.caitu99.service.exception.ActivitiesException;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesGoodService 
 * @author fangjunxiao
 * @date 2016年6月7日 下午12:15:46 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface ActivitiesGoodService {
	
	
	List<ActivitiesGoodDto>  findActivitiesGood() throws ActivitiesException;
	
	
	Integer checkIsActivitiesGood(Long itemId);
	
}
