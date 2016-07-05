/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.integral.controller.auto.AutoUpdateAdapter;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailJobService 
 * @author ws
 * @date 2015年12月10日 下午5:44:01 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class ManualUpdateService{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	AutoUpdateAdapter autoUpdateAdapter;
	@Autowired
	ManualLoginService manualLoginService;
	
	private String MANUAL_UPDATE_ERR_MSG = "【手动查询自动更新失败】：manualId:{},userId:{},loginAccount:{},password:{},errMsg:{}";
	
	public void autoUpdateManual(){
		String version = "2.2.1";
		logger.info("手动查询自动更新开始==================");
		List<ManualLogin> manualLoginList = manualLoginService.selectAccountForUpdate();
		for (ManualLogin manualLogin : manualLoginList) {
			try {
				autoUpdateAdapter.updateAuto(manualLogin.getUserId(),manualLogin.getManualId()
						, manualLogin.getLoginAccount(), manualLogin.getPassword(), version);
			} catch (Exception e) {
				logger.error(MANUAL_UPDATE_ERR_MSG,manualLogin.getManualId()
						, manualLogin.getUserId()
						, manualLogin.getLoginAccount(), manualLogin.getPassword()
						,e);
			}
			
		}

		logger.info("手动查询自动更新结束====================");
	}

}
