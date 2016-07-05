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
import com.caitu99.service.base.Pagination;
import com.caitu99.service.sys.domain.Banner;
import com.caitu99.service.sys.service.BannerService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BannerController 
 * @author fangjunxiao
 * @date 2015年12月2日 下午9:57:03 
 * @Copyright (c) 2015-2020 by caitu99 
 */

@Controller
@RequestMapping("/api/banner")
public class BannerController extends BaseController{

	@Autowired
	private BannerService bannerService;

	@RequestMapping(value="/rotary/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String list(Integer type,String jsonpCallback) {
		ApiResult<List<Banner>> result = new ApiResult<List<Banner>>();
		if(null == type || 0 == type){
			 type = 1;
		}
		List<Banner> data = bannerService.getRotaryImg(type);
		if(StringUtils.isBlank(jsonpCallback)){
			return result.toJSONString(0, "success", data);
		}else{
			return jsonpCallback + "(" + result.toJSONString(0, "success", data) + ")";
		}
	}
	

	@RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String hot(Banner banner, Pagination<Banner> pagination,String jsonpCallback){
		ApiResult<String> err = new ApiResult<String>();
		if(null == banner){
			return err.toJSONString(-1,"参数不能为空");
		}
		String result = bannerService.findPageBanner(banner, pagination);
		return super.retrunResult(result, jsonpCallback);
	}
	
	
}
