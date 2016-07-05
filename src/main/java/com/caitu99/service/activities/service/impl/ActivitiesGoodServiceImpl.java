/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.activities.dao.ActivitiesGoodMapper;
import com.caitu99.service.activities.dao.ActivitiesTimeMapper;
import com.caitu99.service.activities.domain.ActivitiesGood;
import com.caitu99.service.activities.domain.ActivitiesTime;
import com.caitu99.service.activities.dto.ActivitiesGoodDto;
import com.caitu99.service.activities.service.ActivitiesGoodService;
import com.caitu99.service.exception.ActivitiesException;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesGoodServiceImpl 
 * @author fangjunxiao
 * @date 2016年6月7日 下午12:16:04 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class ActivitiesGoodServiceImpl implements ActivitiesGoodService{
	
	
	private final static Logger log = LoggerFactory
			.getLogger(ActivitiesGoodService.class);
	
	
	@Autowired
	private ActivitiesGoodMapper activitiesGoodDao;
	
	@Autowired
	private ActivitiesTimeMapper activitiesTimeDao;

	@Override
	public List<ActivitiesGoodDto> findActivitiesGood()
			throws ActivitiesException {

		String url = SpringContext.getBean(AppConfig.class).fileUrl;
		String imageBig = SpringContext.getBean(AppConfig.class).imagePicBig;
		String caituUrl = SpringContext.getBean(AppConfig.class).caituUrl;
		
		Date date = new Date();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("endTime", date);
		List<ActivitiesGoodDto> dlist = activitiesGoodDao.findBySort(map);
		
		if(null == dlist || dlist.size() == 0){
			return null;
		}
		
		for (ActivitiesGoodDto agd : dlist) {
			String picUrl = agd.getPicUrl();
			String wapUrl = agd.getWapUrl();
			if(StringUtils.isNotBlank(picUrl)){
				agd.setPicUrl(url + picUrl + imageBig);
				agd.setWapUrl(caituUrl + wapUrl);
			}
			agd.setStartTimeStr();
			agd.setEndTimeStr();
			
		}
		
		StringBuilder hourString = new StringBuilder();
		
		List<ActivitiesTime> activitiesTimeList = activitiesTimeDao.findByItemId(dlist.get(0).getId());
		
		if(null != activitiesTimeList){
			
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        int datehour = cal.get(Calendar.HOUR_OF_DAY);
	        int minute = cal.get(Calendar.MINUTE);
	        int second = cal.get(Calendar.SECOND);
	        
	        int timeStatus = 2;
	        Long timeSecond = 0L;
	        
	        
	        Long mins = 0L;
	        
			for (ActivitiesTime activitiesTime : activitiesTimeList) {
				
				Date st = activitiesTime.getStartTime();
				Date ed = activitiesTime.getEndTime();
				
				String ststr = DateUtil.DateToString(st, DateUtil.DATE_HOUR);
				String edstr = DateUtil.DateToString(ed, DateUtil.DATE_HOUR);
				hourString.append(ststr).append("-").append(edstr).append("  ");
				
			    int sthour = DateUtil.getHour(st);
			    int edhour = DateUtil.getHour(ed);
			    
			  if(timeStatus != 1){
				    if(datehour >= sthour && datehour < edhour){
				    	timeStatus = 1;
				    	timeSecond = (long) ((edhour - datehour - 1)*60*60 + (60-minute-1)*60 + (60-second)) * 1000;
				    }else if(datehour < sthour){
				    	timeStatus = 0;
				    	timeSecond = (long) ((sthour - datehour - 1)*60*60 + (60-minute-1)*60 + (60-second)) * 1000;
				    }else if(datehour >= edhour){
				    	timeStatus = 2;
				    }
			  }
			  
			  if(timeStatus == 0){
				  if(mins == 0){
					  mins = timeSecond;
				  }
				  if(mins > timeSecond){
					  mins = timeSecond;
				  }
			  }
			  
			}
			
			
			dlist.get(0).setTimeStatus(timeStatus);
			//     0 未开始   1 已开始   2 已结束
			switch (timeStatus) {
			case 0:
				dlist.get(0).setTimeSecond(mins);
				break;
			case 1:
				dlist.get(0).setTimeSecond(timeSecond);
				break;
			case 2:
				dlist.get(0).setTimeSecond(0L);
				break;
			default:
				dlist.get(0).setTimeSecond(0L);
				dlist.get(0).setTimeStatus(2);
				break;
			}
			
		}
		
		dlist.get(0).setHourString(hourString.toString());
		
		return dlist;
	}
	

	@Override
	public Integer checkIsActivitiesGood(Long itemId) {
		
		Date date = new Date();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("newTime", date);
		map.put("itemId", itemId);
		List<ActivitiesGood> activitiesGoodList = activitiesGoodDao.findAllByItemId(map);
		
		if(null ==activitiesGoodList || activitiesGoodList.size() == 0){
			return 0;
		}
		
		if(2 <= activitiesGoodList.size()){
			log.debug("活动商品配置有误：activities_good, itemId = " + itemId );
			return 0;
		}
		
		Long goodId = activitiesGoodList.get(0).getId();
		
		List<ActivitiesTime> activitiesTimeList = activitiesTimeDao.findByItemId(goodId);
		
		if(null == activitiesTimeList || activitiesTimeList.size() == 0){
			return 1;
		}
		
		int h = DateUtil.getHour(date);
		
		for (ActivitiesTime activitiesTime : activitiesTimeList) {
			
			Date startTime = activitiesTime.getStartTime();
			Date endTime = activitiesTime.getEndTime();
			
			if(null == startTime || null == endTime){
				log.debug("活动时间没配置：activities_time");
				return 1;
			}
			
			int h2 = DateUtil.getHour(startTime);
			int h3 = DateUtil.getHour(endTime);
			
			if(h >= h2 && h < h3){
				return 2;
			}
		}
		
		return 1;
	}


}
