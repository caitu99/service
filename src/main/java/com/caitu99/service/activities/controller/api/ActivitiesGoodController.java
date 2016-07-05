/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.activities.dto.ActivitiesGoodDto;
import com.caitu99.service.activities.service.ActivitiesGoodService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesGoodController 
 * @author fangjunxiao
 * @date 2016年6月7日 上午11:58:14 
 * @Copyright (c) 2015-2020 by caitu99 
 */

@Controller
@RequestMapping("/api/activities/good/")
public class ActivitiesGoodController extends BaseController {
	
	@Autowired
	private ActivitiesGoodService activitiesGoodService;
	
	@RequestMapping(value="home/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String home(Long userid) {
		ApiResult<List<ActivitiesGoodDto>> result = new ApiResult<List<ActivitiesGoodDto>>();
		List<ActivitiesGoodDto> activitiesGoodList = activitiesGoodService.findActivitiesGood();
	    return result.toJSONString(0, "success", activitiesGoodList);
		
	}
	
	
}
