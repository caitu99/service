/*
  * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.AppConfig;
import com.caitu99.service.activities.service.ActivitiesService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.utils.weixin.WeixinUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesController 
 * @author fangjunxiao
 * @date 2015年12月1日 下午8:58:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/activities/cards/")
public class ScratchCardsController extends BaseController{
	
	@Autowired
	private ActivitiesService activitiesService;
	
	@Autowired
	private AppConfig appconfig;
	
	@Autowired
	private WeixinUtil weixinUtil;
	
	/**
	 * 	微信分享
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: share 
	 * @param url
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月22日 下午3:22:12  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="share/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String share(String url,String jsonpCallback,HttpServletRequest httpRequest) {
	
		ApiResult<String> relust = new ApiResult<String>();
		if(null == url){
			return relust.toJSONString(-1,"参数不能为空");
		}
		String token = weixinUtil.getAccessToken();
		String timestamp = "1422009542";
		String noncestr = "rfOEfBdBznhLFkZW";
		String jsapi_ticket = weixinUtil.getSignature(token, noncestr, timestamp, url);
		String resultString = relust.toJSONString(1, "success",jsapi_ticket);
		return super.retrunResult(resultString, jsonpCallback);
	}
	
	/**
	 * 	验证用户抽奖次数
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkTimes 
	 * @param userId
	 * @param activitiesId
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月5日 下午3:48:22  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="checkTimes/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String checkTimes(Long userid,String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if(null == userid){
			return error.toJSONString(-1,"参数不能为空");
		}
		Long activitiesid = appconfig.activitiesId;
		String result = "";
	    result = activitiesService.checkUserTimes(userid, activitiesid);
	    return super.retrunResult(result, jsonpCallback);
		
	}
	
	
	/**
	 * 	验证用户财币是否足够付费抽奖
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkIntegral 
	 * @param userId
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月5日 下午4:08:15  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="checkAccount/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String checkIntegral(Long userid,String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if(null == userid){
			return error.toJSONString(-1,"参数不能为空");
		}
		 String result = "";
	     result = activitiesService.checkUserIntegral(userid);
		 
	     return super.retrunResult(result, jsonpCallback);
	}
	
	
	/**
	 * 	用户查询中奖信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getInRecord 
	 * @param userId
	 * @param inRecordId
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月8日 上午1:05:19  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="getInRecord/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getInRecord(Long userid,Long inrecordid,String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if(null == userid || null == inrecordid){
			return error.toJSONString(-1,"参数不能为空");
		}
		
		 String result = "";
	     result = activitiesService.getInRecord(userid, inrecordid);
		 
	     return super.retrunResult(result, jsonpCallback);
	}
	
	
	/**
	 * 	未认证用户查询奖品
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getActivitiesItem 
	 * @param activitiesItemId
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月9日 下午2:33:44  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="getActivitiesItem/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getActivitiesItem(Long activitiesItemid,String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if(null == activitiesItemid){
			return error.toJSONString(-1,"参数不能为空");
		}
		
		 String result = "";
	     result = activitiesService.getActivitiesItem(activitiesItemid);
		 
	     return super.retrunResult(result, jsonpCallback);
	}
	
	/**
	 * 	已认证用户抽奖
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: winning 
	 * @param userId
	 * @param activitiesId
	 * @param source
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月5日 下午4:24:34  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="winning/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String winning(Long userid,Integer source,String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if(null == userid  || null == source){
			return error.toJSONString(-1,"参数不能为空");
		}
		Long activitiesid = appconfig.activitiesId;
		String result = "";
		if(1 == source || 2 == source){
			result = activitiesService.getProof(userid,activitiesid,source);
		}else{
			return error.toJSONString(-1,"非法请求");
		}
		
		   return super.retrunResult(result, jsonpCallback);
	}
	
	
	/**
	 * 已认证用户领取奖品
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: awardItm 
	 * @param userId
	 * @param inRecordId
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月4日 上午11:39:39  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="awardItm/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String awardItm(Long userid,Long inrecordid,Integer source,String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if(null == userid || null == inrecordid || null == source){
			return error.toJSONString(-1,"参数不能为空");
		}
		
		String result = "";
		if(1 == source || 2 == source){
			result = activitiesService.awardItm(inrecordid, userid);
		}else {
			return error.toJSONString(-1,"非法请求");
		}
		
		   return super.retrunResult(result, jsonpCallback);
	}
	
	
	/**
	 * 未认证用户抽奖
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: unautherizedWinning 
	 * @param userId
	 * @param activitiesId
	 * @param source
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月4日 上午11:23:09  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="unautherized/winning/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String unautherizedWinning(Integer source,String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if(null == source ){
			return error.toJSONString(-1,"参数不能为空");
		}
		Long activitiesid = appconfig.activitiesId;
		String result = "";
		if(3 == source || 4 == source){
			result = activitiesService.getUnautherizedProof(activitiesid, source);
		}else{
			return error.toJSONString(-1,"非法请求");
		}
		
		   return super.retrunResult(result, jsonpCallback);
	}
	
	/**
	 * 	未认证用户认证后授奖
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveWinning 
	 * @param userId
	 * @param activitiesId
	 * @param source
	 * @param activitiesItemId
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月5日 下午4:28:19  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="unautherized/awardItm/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String saveWinning(Long userid,Integer source
			,Long activitiesItemid,String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if(null == userid || null == source
				|| null == activitiesItemid){
			return error.toJSONString(-1,"参数不能为空");
		}
		Long activitiesid = appconfig.activitiesId;
		String result = "";
		if(1 == source){
			result = activitiesService.saveWinning(userid, activitiesid,source,activitiesItemid);
		}else{
			return error.toJSONString(-1,"非法请求");
		}
	
		   return super.retrunResult(result, jsonpCallback);
	}
	
	/**
	 * 	已认证用户放弃领奖
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: giveup 
	 * @param userId
	 * @param inRecordId
	 * @param source
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月5日 下午7:39:21  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="giveup/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String giveup(Long userid,Long inrecordid,Integer source,String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if(null == userid || null == inrecordid || null == source){
			return error.toJSONString(-1,"参数不能为空");
		}
		
		String result = "";
		if(1 == source || 2 == source){
			result = activitiesService.giveupItem(inrecordid, userid);
		}else{
			return error.toJSONString(-1,"非法请求");
		}
		   return super.retrunResult(result, jsonpCallback);
	}
	
	
	

}
