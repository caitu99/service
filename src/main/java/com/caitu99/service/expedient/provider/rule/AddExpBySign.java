/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.provider.rule;

import org.springframework.stereotype.Component;

import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.domain.ExpRecord;
import com.caitu99.service.expedient.provider.abs.AddExpAbstract;

/** 
 * 签到送经验
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddExpBySign 
 * @author fangjunxiao
 * @date 2016年5月26日 下午5:45:01 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class AddExpBySign extends AddExpAbstract{

	
	
	public Integer getSource(){
		return ExpRecord.sig;
	}
	
	@Override
	public Long addExp(ExpData data) {
		
		Long time = 1L;
		Long exp = appConfig.expSigEvenyday;
		if(null != data.getContinuousTime()){
			time = data.getContinuousTime(); 
		}
		//连续登陆5天,5经验
		if(time >= 5){
			exp = appConfig.expSigGoon;
		}
		
		return exp;
	}




}
