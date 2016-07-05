/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mail.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.mail.controller.service.MailJobRegisterService;
import com.caitu99.service.mail.controller.service.MailJobThread;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailJobController 
 * @author ws
 * @date 2015年12月10日 下午12:25:01 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/mail/auto")
public class MailUpdateAutoController {

	private static final Logger logger = LoggerFactory
			.getLogger(MailUpdateAutoController.class);
	@Autowired
	MailJobRegisterService mailJobRegisterService;
	private String ERR_MSG = "【邮箱自动更新失败】：{}";
	
	@RequestMapping(value = "/update/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String updateMailAuto(String userId,String cardTypeId){
/*
        String userId = request.getParameter("userId");
        String cardTypeId = request.getParameter("cardTypeId");*/
        
        
		MailJobThread mailJobThread = new MailJobThread(userId,cardTypeId);
		
		Thread updateMailThread = new Thread(mailJobThread);
		
		updateMailThread.start();
		
		return "true";
	}
	
	
	/*@RequestMapping(value = "/register/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String registerMailAuto(){

		mailJobRegisterService.registerMailJobBatch();
		
		return "true";
	}*/

}
