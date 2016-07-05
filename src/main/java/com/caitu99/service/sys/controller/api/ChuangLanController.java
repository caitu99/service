/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.sys.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.RedisKey;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.sys.sms.SMSSend;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ChuangLanController 
 * @author Hongbo Peng
 * @date 2015年12月16日 下午5:34:47 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/public/cl/")
public class ChuangLanController extends BaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(SendSmsController.class);

    @Autowired
    private RedisOperate redis;

	@RequestMapping(value="/consume/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public void consumeClSMS(HttpServletRequest request){
    	String msgid = request.getParameter("msgid");
    	String mobile = request.getParameter("mobile");
    	String status = request.getParameter("status");
    	if(StringUtils.isBlank(msgid) || StringUtils.isBlank(mobile) || StringUtils.isBlank(status)){
    		return;
    	}
    	String key = String.format(RedisKey.SMS_CL_MSGID, msgid,mobile);
    	//创蓝短信发送成功
    	if("DELIVRD".equals(status)){
    		logger.info("【状态报告】创蓝短信发送成功(msgid:{},mobile:{},status:{})",msgid,mobile,status);
    		redis.del(key);
    		return;
    	}
    	logger.info("创蓝短信发送失败");
    	String msg = redis.getStringByKey(key);
    	if(StringUtils.isNotBlank(msg)){
    		//创蓝短信发送失败，改用亿美发送
    		SMSSend.sendYmSMS(new String[]{mobile}, msg);
    		redis.del(key);
    	}
    }
}
