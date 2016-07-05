/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.spider;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualQueryIHG 
 * @author chenhl
 * @date 2015年12月1日 上午10:31:04 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class ManualQueryIHG extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryIHG.class);
			
	public String login(Long userid,String loginAccount,String password) throws ManualQueryAdaptorException {
		
		AssertUtil.notNull(userid, "用户ID不能为空");
		AssertUtil.hasLength(loginAccount, "登录账号不能为空");
		AssertUtil.hasLength(password, "登录密码不能为空");
		
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/ihg/login/1.0";
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("userid", userid.toString());
		paramMap.put("account", loginAccount);
		paramMap.put("password", password);
		
		String jsonString = null;
		try {
			jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			logger.info("登录洲际返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.SUCCEED);
			if(!flag){
				JSONObject json = JSON.parseObject(jsonString);
				Integer code = json.getInteger("code");
				
				if(code.equals(1060)){
					return ApiResult.outSucceed(ApiResultCode.IHG_ACCOUNT_PWD_ERROR,"用户名或密码错误");
//					throw new ManualQueryAdaptorException(ApiResultCode.IHG_ACCOUNT_PWD_ERROR,"用户名或密码错误");
				}else if(code.equals(1006)){
					return ApiResult.outSucceed(ApiResultCode.DATA_LOST,"数据不完整");
//					throw new ManualQueryAdaptorException(ApiResultCode.DATA_LOST,"数据不完整");
				}
			}
			return jsonString;
			
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}catch (Exception e) {
			logger.error("登录洲际失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,e.getMessage());
		}
	}

	@Override
	public String getImageCode(Long userId) {
		return null;
	}

	@Override
	public String login(Long userId, String loginAccount, String password,String imageCode) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String result, String version) {
		return null;
	}
}
