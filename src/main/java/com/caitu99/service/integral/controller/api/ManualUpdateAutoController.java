/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.integral.controller.service.ManualJobThread;
import com.caitu99.service.mail.controller.api.MailUpdateAutoController;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailUpdateAutoController 
 * @author ws
 * @date 2015年12月16日 下午7:26:06 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/manual/auto")
public class ManualUpdateAutoController {
	
	private static final Logger logger = LoggerFactory
			.getLogger(MailUpdateAutoController.class);

	private String ERR_MSG = "【手动查询自动更新失败】：{}";
	
	@RequestMapping(value = "/update/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String updateManualAuto(HttpServletRequest request){

		ManualJobThread manualJobThread = new ManualJobThread();
		
		Thread manualUpdateThread = new Thread(manualJobThread);
		
		manualUpdateThread.start();
		
		return "true";
	}
	
	
}
