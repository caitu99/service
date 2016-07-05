/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caitu99.service.integral.controller.vo.ManualBatchVo;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.utils.SpringContext;

/**
 * 手动查询自动更新线程
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualJobThread 
 * @author ws
 * @date 2015年12月18日 下午4:47:24 
 * @Copyright (c) 2015-2020 by caitu99
 */
public class ManualUpdateJobThread implements Runnable{

	private static final Logger logger = LoggerFactory
			.getLogger(ManualUpdateJobThread.class);
	
	ManualUpdateSingleService manualUpdateSingleService;
	List<ManualLogin> manualAccounts;
	
	
	/** 
	 * @Title:  
	 * @Description: 
	 */
	public ManualUpdateJobThread(List<ManualLogin> manualAccounts) {
		this.manualUpdateSingleService = SpringContext.getBean("manualUpdateSingleService"); 
		this.manualAccounts = manualAccounts;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		manualUpdateSingleService.autoUpdateManual(manualAccounts);
		
	}

	
}
