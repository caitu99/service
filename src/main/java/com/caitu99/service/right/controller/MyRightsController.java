/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.right.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.right.domain.MyRights;
import com.caitu99.service.right.service.MyRightsService;

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
@RequestMapping("/api/right")
public class MyRightsController extends BaseController{
	
	private static final String[] MY_RIGHTS_FILLTER = {"id","userId", "name","detail","gmtCreate","gmtDisabled","status","rightId","createDateStr","disabledDateStr","identity"};
	
	@Autowired
	private MyRightsService myRightsService;

	/**
	 * 获取我的权益列表
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: myright 
	 * @param userid
	 * @return
	 * @date 2016年5月11日 下午3:42:18  
	 * @author ws
	 */
	@RequestMapping(value="/myright/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String myright(Long userid) {
		ApiResult<List<MyRights>> result = new ApiResult<List<MyRights>>();
		
		List<MyRights> data = myRightsService.selectMyRights(userid);
		//将code过滤掉
		return result.toJSONString(0, "success", data, MyRights.class, MY_RIGHTS_FILLTER);
	}
	
	/**
	 * 获取我的权益明细
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: rightDetail 
	 * @param id
	 * @return
	 * @date 2016年5月11日 下午3:42:29  
	 * @author ws
	 */
	@RequestMapping(value="/detail/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String rightDetail(Long id,Long userid) {
		
		ApiResult<Map<String,String>> result = new ApiResult<Map<String,String>>();
		
		Map<String,String> rightDetail = myRightsService.selectRightDetail(id,userid);
		if(null == rightDetail){
			return result.toJSONString(-1, "您的权益不存在");
		}else{
			return result.toJSONString(0, "success", rightDetail);
		}
	}
	
	/**
	 * 使用我的权益
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: doUser 
	 * @param id
	 * @return
	 * @date 2016年5月11日 下午3:42:41  
	 * @author ws
	 */
	@RequestMapping(value="/douse/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String doUser(Long id,Long userid) {
		ApiResult<MyRights> result = new ApiResult<MyRights>();
		
		MyRights myRights = myRightsService.useMyRights(id,userid);
		if(null == myRights){
			return result.toJSONString(-1, "您的权益不存在或已失效");
		}else{
			return result.toJSONString(0, "成功使用", myRights);
		}
	}
	
	
}
