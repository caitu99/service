/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.domain.Expedient;
import com.caitu99.service.expedient.provider.AddExp;
import com.caitu99.service.expedient.provider.rule.AddExpByShare;
import com.caitu99.service.expedient.service.ExpedientService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ExpedientController 
 * @author fangjunxiao
 * @date 2016年6月1日 上午9:56:07 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/user/expedient/")
public class ExpedientController extends BaseController{
	
	
	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private AddExp addExp;
	
	@Autowired
	private AddExpByShare addExpByShare;
	
	
    private static final String[] Expedient_FILLTER = {"name","userId","exp","lev"};

	
	@RequestMapping(value="query/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String queryExp(Long userid){
		
		ApiResult<Expedient> result = new ApiResult<Expedient>();
		
		if(null == userid){
			return result.toJSONString(-1, "userid is null");
		}
		
		Expedient expedient = expedientService.getExpedientByUserId(userid);
		
		return result.toJSONString(0, "SUCCESS", expedient,Expedient.class,Expedient_FILLTER);
	}
	
	
	
	@RequestMapping(value="share/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String shareExp(Long userid){
		
		ApiResult<Expedient> result = new ApiResult<Expedient>();
		
		if(null == userid){
			return result.toJSONString(-1, "userid is null");
		}
		
		ExpData expdata = new ExpData();
		expdata.setUserId(userid);
		addExp.addExp(expdata, addExpByShare);
		
		return result.toJSONString(0, "SUCCESS");
	}
	
	
	

}
