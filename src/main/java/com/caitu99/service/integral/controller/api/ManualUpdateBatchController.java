/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.integral.controller.auto.AutoUpdateAdapter;
import com.caitu99.service.integral.controller.service.ManualBatchThread;
import com.caitu99.service.integral.controller.vo.ManualBatchVo;
import com.caitu99.service.integral.dao.ManualResultMapper;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.utils.VersionUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailUpdateAutoController 
 * @author ws
 * @date 2015年12月16日 下午7:26:06 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/manual/update")
public class ManualUpdateBatchController {

	private static final Logger logger = LoggerFactory
			.getLogger(ManualUpdateBatchController.class);
	
	@Autowired
	AutoUpdateAdapter autoUpdateAdapter;
	@Autowired
	ManualLoginService manualLoginService;
	@Autowired
	ManualResultMapper manualResultMapper;

	private String MANUAL_UPDATE_BATCH_ERR_MSG = "【批量实时更新失败】：{}";
	
	/**
	 * 批量实时更新
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateManualAuto 
	 * @param userId
	 * @return
	 * @date 2016年2月25日 上午10:31:52  
	 * @author ws
	 */
	@RequestMapping(value = "/batch/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String updateManualAuto(Long userId, HttpServletRequest request){
		

		String version = VersionUtil.getAppVersion(request);
		if(StringUtils.isBlank(version)){
			version = "2.2.1";
			logger.info("APP使用默认版本号为:" + version);
		}else{
			logger.info("APP版本号为:" + version);
		}
		
		
		ApiResult<List<ManualResult>> resp = new ApiResult<List<ManualResult>>();
		//预清理数据
		manualResultMapper.deleteByUserId(userId);
		
		//获取该用户需要更新的所有账户
		List<ManualLogin> manualLoginList = manualLoginService.selectByUserIdForBatch(userId);
		
		if(null == manualLoginList || manualLoginList.size() == 0){
			resp.setCode(0);
			resp.setData(new ArrayList<ManualResult>());
			return JSON.toJSONString(resp);
		}
		
		Map<Long,List<ManualBatchVo>> accountMap = new HashMap<Long, List<ManualBatchVo>>();
		for (ManualLogin manualLogin : manualLoginList) {//按照cardTypeID账户类型进行分类
			ManualBatchVo vo = new ManualBatchVo();
			vo.setLoginAccount(manualLogin.getLoginAccount());
			vo.setManualId(manualLogin.getManualId());
			vo.setPassword(manualLogin.getPassword());
			vo.setUserId(userId);
			if(accountMap.containsKey(manualLogin.getManualId())){
				accountMap.get(manualLogin.getManualId()).add(vo);
			}else{
				List<ManualBatchVo> voList = new ArrayList<ManualBatchVo>();
				voList.add(vo);
				accountMap.put(manualLogin.getManualId(), voList);
			}
		}

		CyclicBarrier barrier = new CyclicBarrier(accountMap.size(), new Runnable() {
            // 在所有线程都到达Barrier时执行
            public void run() {
				//处理返回结果数据
            	List<ManualResult> data = manualResultMapper.selectByUserId(userId);
            	resp.setData(data);
            	resp.setCode(10);
            }
        });
		
        // 启动线程
		for (Long mapKey : accountMap.keySet()) {
			List<ManualBatchVo> manualAccount = accountMap.get(mapKey);
			new Thread(new ManualBatchThread(barrier, manualAccount, version)).start();
		}
		
		int count = 0;
		
		while(true){
			if(resp.getCode() == 10){
				resp.setCode(0);
				return JSON.toJSONString(resp);
			}else{
				try {
					count = count + 1;
					if(count >= 900){//15分钟未完成
						resp.setMessage("实时批量更新超时，请稍后再试");
						resp.setCode(3301);
						return JSON.toJSONString(resp);
					}
					//System.out.println(userId+":"+count);
					Thread.sleep(1000);//sleep 1秒钟
				} catch (InterruptedException e) {
					logger.error("实时批量更新失败：{}",e);
				}
			}
		}
	}
	
	/**
	 * 获取批量实时账单更新结果
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getManualBatchResult 
	 * @param userId
	 * @return
	 * @date 2016年2月25日 上午10:31:37  
	 * @author ws
	 */
	@RequestMapping(value = "/result/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getManualBatchResult(Long userId){
		ApiResult<List<ManualResult>> resp = new ApiResult<List<ManualResult>>();
		//处理返回结果数据
    	List<ManualResult> data = manualResultMapper.selectByUserId(userId);
    	resp.setData(data);
    	resp.setCode(0);
    	return JSON.toJSONString(resp);
    	
	}
	
	/**
	 * 获取批量实时账单更新结果
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getManualBatchResult 
	 * @param userId
	 * @return
	 * @date 2016年2月25日 上午10:31:37  
	 * @author ws
	 */
	@RequestMapping(value = "/time/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getManualBatchTime(Long userId){
		ApiResult<Date> resp = new ApiResult<Date>();
		//处理返回结果数据
    	Date date = manualResultMapper.selectDateTimeByUserId(userId);
    	resp.setData(date);
    	resp.setCode(0);
    	return JSON.toJSONString(resp);
    	
	}
	
}
