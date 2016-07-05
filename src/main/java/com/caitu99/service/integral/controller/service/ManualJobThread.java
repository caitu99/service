/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ManualJobThread implements Runnable{

	private static final Logger logger = LoggerFactory
			.getLogger(ManualJobThread.class);
	
	ManualUpdateService manualUpdateService;
	
	
	/** 
	 * @Title:  
	 * @Description: 
	 */
	public ManualJobThread() {
		this.manualUpdateService = SpringContext.getBean("manualUpdateService"); 
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		manualUpdateService.autoUpdateManual();
		
	}

	
}
