package com.caitu99.service.integral.controller.auto.abs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.AutoFindRecord;
import com.caitu99.service.utils.ApiResultCode;

/**
 * 自动发现有图片验证码抽象类
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindImageAbstract 
 * @author xiongbin
 * @date 2015年12月17日 上午9:44:36 
 * @Copyright (c) 2015-2020 by caitu99
 */
public abstract class AutoFindImageAbstract extends AutoFindImageNotAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Override
	public String login(ManualQueryAbstract manualQuery,Long userId,String loginAccount,String password,Integer count,String log){
		logger.info("【手动查询自动发现】:" + "userId：" + userId + "," + log + "自动发现,尝试登陆开始");
		
		JSONObject reslutJSON = new JSONObject();
		
		//获取验证码
		String imageCodeIO = manualQuery.getImageCode(userId);
		imageCodeIO = super.analysisImageCode(imageCodeIO);
		
		//破解验证码
		String imageCode = crackImageCode(imageCodeIO);
		if(null == imageCode){
			logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log + "自动发现,尝试登陆失败." + "图片验证码破解失败");
			
			reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
			reslutJSON.put("message", "图片验证码破解失败");
			reslutJSON.put("error", ApiResultCode.IMAGECODE_ERROR);
			return reslutJSON.toJSONString();
		}
		
		//尝试登陆
		String jsonString = manualQuery.login(userId,loginAccount,password,imageCode);
		
		return verifyLoginReslut(jsonString,manualQuery,userId,loginAccount,password,count,log);
	}
	
	@Override
	public String loginForUpdate(ManualQueryAbstract manualQuery,Long userId,String loginAccount,String password){
		

		JSONObject reslutJSON = new JSONObject();
		
		//获取验证码
		String imageCodeIO = manualQuery.getImageCode(userId);
		imageCodeIO = super.analysisImageCode(imageCodeIO);
		
		//破解验证码
		String imageCode = crackImageCode(imageCodeIO);
		if(null == imageCode){
			//再试一次
			imageCode = crackImageCode(imageCodeIO);
			if(null == imageCode){
				reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
				reslutJSON.put("message", "图片验证码破解失败");
				reslutJSON.put("error", ApiResultCode.IMAGECODE_ERROR);
				return reslutJSON.toJSONString();
			}
		}
		
		//尝试登陆
		String jsonString = manualQuery.login(userId,loginAccount,password,imageCode);
		
		return jsonString;
	}
	
	
	
}
