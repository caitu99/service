/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.provider.rule;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.domain.ExpRecord;
import com.caitu99.service.expedient.dto.ExpRecordDto;
import com.caitu99.service.expedient.provider.abs.AddExpAbstract;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 积分商城送经验
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddExpByIshop 
 * @author fangjunxiao
 * @date 2016年5月31日 下午5:15:13 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class AddExpByIshop extends AddExpAbstract{

	
	public Integer getSource(){
		return ExpRecord.ishop;
	}
	
	
	@Override
	public Long addExp(ExpData data) {
		Long exp = 0L;
		
		if(null == data.getInegral() || 0 == data.getInegral())
			return exp;
		
		exp = CalculateUtils.multiply(data.getInegral(), appConfig.expIshopInegral);
		
		ExpRecordDto expRecordDto = this._getExpRecordBySource(ExpRecord.ishop, data.getUserId());
		//当天当前类型已获得经验
		Long sumExp = expRecordDto.getSumExp();
		//次数
		Integer recordTime = expRecordDto.getCountId();
		
		Long dif = CalculateUtils.multiply(appConfig.expIshopTime, recordTime);
		//实际购买转化的经验
		Long relTotal = CalculateUtils.getDifference(sumExp, dif);
		
		if(relTotal < appConfig.expIshopTop){
			
			Long superpass = CalculateUtils.add(exp, relTotal);
			
			if(superpass > appConfig.expIshopTop){
				exp = CalculateUtils.getDifference(appConfig.expIshopTop, relTotal);
			}
			
		}else{
			exp = 0L;
		}
		
		if(null == recordTime)
			recordTime = 0;
		
		Long topExp = CalculateUtils.multiply(appConfig.expIshopTime, recordTime);
		if(topExp < appConfig.expIshopTimeTop){
			exp = CalculateUtils.add(exp,appConfig.expIshopTime);
		}
		
		
		return  exp;
	}
	
	
	
	private ExpRecordDto _getExpRecordBySource(Integer source,Long userId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("source", source);
		map.put("userId", userId);
		Date endTime = Calendar.getInstance().getTime();
		Date startTime = DateUtil.getZeroPoint(endTime);
		map.put("endTime", endTime);
		map.put("startTime", startTime);
		return expRecordDao.countByuserIdAndSource(map);
	}

}
