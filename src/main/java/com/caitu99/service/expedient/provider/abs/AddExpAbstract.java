/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.provider.abs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.caitu99.service.AppConfig;
import com.caitu99.service.expedient.dao.ExpRecordMapper;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.service.ExpedientService;
import com.caitu99.service.integral.dao.UserCardManualMapper;
import com.caitu99.service.user.dao.UserMapper;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddExpAbstract 
 * @author fangjunxiao
 * @date 2016年5月26日 下午12:03:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public abstract class AddExpAbstract{
	
	
	@Autowired
	protected UserCardManualMapper userCardManualDao;
	
	@Autowired
	protected ExpRecordMapper expRecordDao;
	
	@Autowired
	protected AppConfig appConfig;
	
	@Autowired
	protected UserMapper userDao;
	
	@Autowired
	protected ExpedientService expedientService;

	
	
	public abstract Long addExp(ExpData data);
	
	public abstract Integer getSource();
	
	
}
