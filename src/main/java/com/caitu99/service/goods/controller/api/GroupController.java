/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.goods.controller.api;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.dto.TemplateDto;
import com.caitu99.service.goods.service.GroupService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: GroupController 
 * @author fangjunxiao
 * @date 2015年12月31日 上午10:44:36 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/goods/group/")
public class GroupController extends BaseController{

	@Autowired
	private GroupService groupService;
	
	
	
	@RequestMapping(value="page/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String page(String version, Pagination<TemplateDto> pagination,String jsonpCallback){
		if(StringUtils.isBlank(version)){
			version = "1.7.0";
		}
		TemplateDto group = new TemplateDto();
		group.setVersionString(version);
		String result = groupService.findPageGroup(group, pagination);
		
		
		return super.retrunResult(result, jsonpCallback);
	}
	
	
	
	
}
