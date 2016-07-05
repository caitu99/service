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
 * 变现送经验
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddExpByRealization 
 * @author fangjunxiao
 * @date 2016年5月31日 下午4:38:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class AddExpByRealization extends AddExpAbstract{

	public Integer getSource(){
		return ExpRecord.realization;
	}
	

	@Override
	public Long addExp(ExpData data) {
		
		Long exp = 0L;
		
		if(null == data.getCash() || 0 == data.getCash())
			return exp;
		
		exp = CalculateUtils.multiply(data.getCash(), appConfig.expRealizationCash);
		
		if(0 == exp)
			return exp;
			
		ExpRecordDto expRecordDto = this._getExpRecordBySource(ExpRecord.realization, data.getUserId());
		
		Long sumExp = expRecordDto.getSumExp();
		
		if(sumExp >= appConfig.expRealizationTop)
			return 0L;
		
		Long superpass = CalculateUtils.add(exp, sumExp);
		
		if(superpass > appConfig.expRealizationTop){
			exp = CalculateUtils.getDifference(appConfig.expRealizationTop, sumExp);
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
