/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.sys.controller.api;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.sys.domain.Banner;
import com.caitu99.service.sys.domain.FuncModel;
import com.caitu99.service.sys.service.FuncModelService;

/**
 * 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: FuncModelController 
 * @author ws
 * @date 2016年5月10日 下午6:01:52 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/funcmodel")
public class FuncModelController extends BaseController{

	@Autowired
	private FuncModelService funcModelService;

	@RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String list(Integer modelId) {
		ApiResult<List<FuncModel>> result = new ApiResult<List<FuncModel>>();
		if(null == modelId || 0 == modelId){
			modelId = 1;
		}
		
		List<FuncModel> data = funcModelService.selectByModel(modelId);

		return result.toJSONString(0, "success", data);
	}
	

	
}
