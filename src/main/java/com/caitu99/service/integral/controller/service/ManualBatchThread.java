/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.service;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caitu99.service.constants.SysConstants;
import com.caitu99.service.integral.controller.auto.AutoUpdateAdapter;
import com.caitu99.service.integral.controller.vo.ManualBatchVo;
import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.integral.service.ManualResultService;
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
public class ManualBatchThread implements Runnable{

	private static final Logger logger = LoggerFactory
			.getLogger(ManualBatchThread.class);
	
	CyclicBarrier barrier;// 
	ManualUpdateBatchService manualUpdateBatchService;
	String version;
	
	private List<ManualBatchVo> manualAccounts;
	
	/** 
	 * @Title:  
	 * @Description: 
	 */
	public ManualBatchThread(CyclicBarrier barrier,List<ManualBatchVo> manualAccounts,String version) {
		this.manualUpdateBatchService = SpringContext.getBean("manualUpdateBatchService"); 
		this.barrier = barrier;
		this.manualAccounts = manualAccounts;
		this.version = version;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try{
			manualUpdateBatchService.updateManualIntegral(manualAccounts,version);
		}catch (Exception ex){
			logger.error("批量实时更新异常：{}",ex);
		}
		
		try {
			barrier.await();//通知已完成
		} catch (InterruptedException e) {
			
			logger.error("批量实时更新异常：{}",e);
		} catch (BrokenBarrierException e) {

			logger.error("批量实时更新异常：{}",e);
		}
		
	}

	
}
