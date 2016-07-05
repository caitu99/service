/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.controller.api;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.activities.domain.ActivitiesStatistics;
import com.caitu99.service.activities.service.ActivitiesStatisticsService;
import com.caitu99.service.base.BaseController;

/** 
 * 
 * @Description: (活动类统计数据) 
 * @ClassName: ActivitiesStatisticsContriller 
 * @author Hongbo Peng
 * @date 2016年1月11日 上午11:59:43 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/public/activities/statistics/")
public class ActivitiesStatisticsContriller extends BaseController {

	private Logger logger = LoggerFactory.getLogger(ActivitiesStatisticsContriller.class);
	
	@Autowired
	private ActivitiesStatisticsService activitiesStatisticsService;
	
	/**
	 * @Description: (刷卡游戏记录)  
	 * @Title: card 
	 * @param point 记录点
	 * @date 2016年1月11日 下午12:03:08  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value="card/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public void card(Integer point){
		if(null == point){
			logger.error("params \"point\" can not be null");
			return ;
		}
		String pointCode = "";
		switch (point) {
		case 1:
			pointCode = "index_all";
			break;
		case 2:
			pointCode = "sex_boy";
			break;
		case 3:
			pointCode = "sex_girl";
			break;
		case 4:
			pointCode = "again";
			break;
		case 5:
			pointCode = "share";
			break;
		default:
			break;
		}
		ActivitiesStatistics activitiesStatistics = new ActivitiesStatistics();
		activitiesStatistics.setActivitiesCode("activity_card");
		activitiesStatistics.setPointCode(pointCode);
		activitiesStatistics.setCreateTime(new Date());
		activitiesStatisticsService.insert(activitiesStatistics);
	}
	
	/**
	 * @Description: (抽签游戏记录)  
	 * @Title: card 
	 * @param point 记录点
	 * @date 2016年1月11日 下午12:03:08  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value="chouqian/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public void chouqian(){
		ActivitiesStatistics activitiesStatistics = new ActivitiesStatistics();
		activitiesStatistics.setActivitiesCode("chouqian");
		activitiesStatistics.setPointCode("index");
		activitiesStatistics.setCreateTime(new Date());
		activitiesStatisticsService.insert(activitiesStatistics);
	}
}
