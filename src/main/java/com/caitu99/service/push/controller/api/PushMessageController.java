/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.push.controller.api;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.push.model.PushMessage;
import com.caitu99.service.push.service.PushMessageService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PushMessageController 
 * @author Hongbo Peng
 * @date 2015年12月23日 下午4:19:43 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/push")
public class PushMessageController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(PushMessageController.class);
	
	@Autowired
	private PushMessageService pushMessageService;
	
	/**
	 * @Description: (提供给JOB工程调用，JOB工程决定什么时间推送，推送的时候调用此方法)  
	 * @Title: push 
	 * @param messageId
	 * @throws Exception
	 * @date 2015年12月23日 下午4:29:04  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value={"/message/push/1.0"}, produces="application/json;charset=utf-8")
	@ResponseBody
	public void push(String messageId) throws Exception {
		pushMessageService.pushMessage(messageId);
	}
	
	/**
	 * @Description: (提供给PUSH工程调用，当PUSH失败时使用短信通知，由消息体决定是否尝试短信通知)  
	 * @Title: sms 
	 * @param messageId
	 * @throws Exception
	 * @date 2015年12月23日 下午4:30:02  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value={"/message/sms/1.0"}, produces="application/json;charset=utf-8")
	@ResponseBody
	public void sms(String messageId) throws Exception {
		pushMessageService.smsPush(messageId);
	}
	
	/**
	 * @Description: (批量读取消息)  
	 * @Title: read 
	 * @param messageIds
	 * @throws Exception
	 * @date 2015年12月24日 上午11:19:55  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value={"/message/read/1.0"}, produces="application/json;charset=utf-8")
	@ResponseBody
	public String read(String ids){
		ApiResult<String> apiResult = new ApiResult<String>();
		try {
			JSONArray arr = JSON.parseArray(ids);
			String[] jsonIds = new String[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				jsonIds[i] = arr.getString(i);
			}
			pushMessageService.editMessage(jsonIds,2);
			return apiResult.toJSONString(0, "SUCCESS");
		} catch (Exception e) {
			logger.error("批量读取消息发生异常:{}",e);
			return apiResult.toJSONString(-1, "FAILED");
		}
	}
	
	/**
	 * @Description: (批量删除消息)  
	 * @Title: del 
	 * @param messageIds
	 * @throws Exception
	 * @date 2015年12月25日 下午4:58:23  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value={"/message/del/1.0"}, produces="application/json;charset=utf-8")
	@ResponseBody
	public String del(String ids){
		ApiResult<String> apiResult = new ApiResult<String>();
		try {
			JSONArray arr = JSON.parseArray(ids);
			String[] jsonIds = new String[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				jsonIds[i] = arr.getString(i);
			}
			pushMessageService.editMessage(jsonIds,-1);
			return apiResult.toJSONString(0, "SUCCESS");
		} catch (Exception e) {
			logger.error("批量删除消息发生异常:{}",e);
			return apiResult.toJSONString(-1, "FAILED");
		}
	}
	
	/**
	 * @Description: (分页查询消息)  
	 * @Title: list 
	 * @param pagination
	 * @param pushMessage
	 * @return
	 * @throws Exception
	 * @date 2015年12月24日 下午3:37:44  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value={"/message/list/1.0"}, produces="application/json;charset=utf-8")
	@ResponseBody
	public String list(Pagination<PushMessage> pagination,PushMessage pushMessage)throws Exception{
		ApiResult<Pagination<PushMessage>> result = new ApiResult<Pagination<PushMessage>>();
		pagination = pushMessageService.findPage(pagination, pushMessage);
		return result.toJSONString(0, "SUCCESS", pagination);
	}
	
	/**
	 * @Description: (用于APP启动时，查询有哪些小红点需要亮起，总共有多少条未读消息)  
	 * @Title: count 
	 * @param pagination
	 * @param pushMessage
	 * @return
	 * @throws Exception
	 * @date 2015年12月29日 下午4:39:00  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value={"/message/count/1.0"}, produces="application/json;charset=utf-8")
	@ResponseBody
	public String count(Long userId)throws Exception{
		ApiResult<List<PushMessage>> result = new ApiResult<List<PushMessage>>();
		List<PushMessage> list = pushMessageService.getRedSpot(userId);
		return result.toJSONString(0, "SUCCESS", list);
	}
	/**
	 * @Description: (消息群发推送通道)  
	 * @Title: topicSend 
	 * @param title
	 * @param message
	 * @return
	 * @throws Exception
	 * @date 2016年1月4日 下午4:39:11  
	 * @author Hongbo Peng
	 */
	
	@RequestMapping(value={"/message/topic/send/1.0"}, produces="application/json;charset=utf-8")
	@ResponseBody
	public String topicSend(String title,String message)throws Exception{
		ApiResult<String> result = new ApiResult<String>();
		if(StringUtils.isBlank(title) || StringUtils.isBlank(message)){
			return result.toJSONString(-1, "参数不能为空");
		}
		pushMessageService.sendBroadcast(title, message);
		return result.toJSONString(0, "SUCCESS");
	}
}
