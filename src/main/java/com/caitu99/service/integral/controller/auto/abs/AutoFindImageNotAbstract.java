package com.caitu99.service.integral.controller.auto.abs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;

/**
 * 自动发现无图片验证码抽象类
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindImageNotAbstract 
 * @author xiongbin
 * @date 2015年12月17日 上午9:44:36 
 * @Copyright (c) 2015-2020 by caitu99
 */
public abstract class AutoFindImageNotAbstract extends AutoFindAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Override
	public String login(ManualQueryAbstract manualQuery,Long userId,String loginAccount,String password,Integer count,String log){
		logger.info("【手动查询自动发现】:" + "userId：" + userId + "," + log + "自动发现,尝试登陆开始");
		
		//尝试登陆
		String jsonString = manualQuery.login(userId,loginAccount,password);
		
		return verifyLoginReslut(jsonString,manualQuery,userId,loginAccount,password,count,log);
	}
	
	@Override
	public String loginForUpdate(ManualQueryAbstract manualQuery,Long userId,String loginAccount,String password){
		//尝试登陆
		String jsonString = manualQuery.login(userId,loginAccount,password);
		
		return jsonString;
	}
}
