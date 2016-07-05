/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.provider.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.domain.ExpRecord;
import com.caitu99.service.expedient.provider.abs.AddExpAbstract;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.UserCardManualService;


/** 
 * 积分查询送经验
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddExpByIntegral 
 * @author fangjunxiao
 * @date 2016年5月26日 下午12:09:16 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class AddExpByIntegral extends AddExpAbstract{
	
	
	@Autowired
	private UserCardManualService userCardManualService;
	
	
	public Integer getSource(){
		return ExpRecord.integeral;
	}
	

	@Override
	public Long addExp(ExpData data) {
		
		JSONObject resultData = JSON.parseObject(data.getIntegralResult());
		Integer code = resultData.getInteger("code");
		
		Long exp = 0L;
		
		if(code == 0){
			JSONObject jsondata = resultData.getJSONObject("data");
			
			//处理返回结果
			Long userId = Long.parseLong(jsondata.getString("userId"));
			Long manualId = Long.parseLong(jsondata.getString("manualId"));
			String loginAccount = jsondata.getString("loginAccount");
			
			UserCardManual puFaBankManual = userCardManualService.getUserCardManualSelective(userId,manualId,null,null,loginAccount);
			
			if(null == puFaBankManual)
				return exp;
			
			if(puFaBankManual.getGmtCreate().equals(puFaBankManual.getGmtModify())){
				exp = appConfig.expRecordExp;
				return exp;
			}
			
//			Date endTime = Calendar.getInstance().getTime();
//			Date startTime = DateUtil.getZeroPoint(endTime);
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("endTime", endTime);
//			map.put("startTime", startTime);
//			map.put("userId", userid);
//			//int queryTime = userCardManualDao.countCardByGmtCreate(map);
//			//积分查询帐户   经验 1
//			int bankCards = userCardManualDao.countAccountByBank(map);
//			//积分查询平台    经验1
//			int ptUser = userCardManualDao.countPlatformByUser(map);
//			
//			Long recordExp = appConfig.expRecordExp;
//			exp = CalculateUtils.add(bankCards,ptUser);
//			exp = CalculateUtils.multiply(exp,recordExp);
			
		}

		return exp;
	}





}
