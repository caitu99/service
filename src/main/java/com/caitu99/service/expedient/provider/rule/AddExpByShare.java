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
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 分享
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddExpByShare 
 * @author fangjunxiao
 * @date 2016年6月2日 上午9:53:48 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class AddExpByShare extends AddExpAbstract{
	
	
	public Integer getSource(){
		return ExpRecord.share;
	}

	@Override
	public Long addExp(ExpData data) {
		
		Long exp = 0L;
		
		ExpRecordDto expRecordDto = this._getExpRecordBySource(ExpRecord.share, data.getUserId());
		
		Long shareExp = expRecordDto.getSumExp();
		
		if(appConfig.expShareTop > shareExp){
			exp = appConfig.expShareExp;
		}
		
		return exp;
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
