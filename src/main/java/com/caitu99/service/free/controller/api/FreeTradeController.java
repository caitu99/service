/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.free.controller.api;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.free.domain.FreeTrade;
import com.caitu99.service.free.domain.FreeTradePlatform;
import com.caitu99.service.free.service.FreeTradePlatformService;
import com.caitu99.service.free.service.FreeTradeService;
import com.caitu99.service.utils.VersionUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: FreeTradeController 
 * @author Hongbo Peng
 * @date 2016年1月20日 下午4:15:51 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/free/")
public class FreeTradeController extends BaseController {
	
	private final static Logger logger = LoggerFactory.getLogger(FreeTradeController.class);

	@Autowired
	private FreeTradeService freeTradeService;
	
	@Autowired
	private FreeTradePlatformService freeTradePlatformService;
	
	/***
	 * @Description: (分页查询自由交易数据)  
	 * @Title: list 
	 * @param pagination
	 * @param freeTrade
	 * @return
	 * @date 2016年1月20日 下午4:22:27  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value="freetrade/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String freeTradeList(Pagination<FreeTrade> pagination,FreeTrade freeTrade){
		pagination = freeTradeService.selectPage(pagination, freeTrade);
		ApiResult<Pagination<FreeTrade>> result = new ApiResult<Pagination<FreeTrade>>();
		return result.toJSONString(0, "SUCCESS", pagination);
	}
	
	/**
	 * @Description: (自由交易平台集合)  
	 * @Title: freeTradePlatformList 
	 * @return
	 * @date 2016年1月20日 下午4:29:25  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value="freetradeplatform/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String freeTradePlatformList(){
//		List<FreeTradePlatform> list = freeTradePlatformService.selectList();
		List<FreeTradePlatform> list = freeTradePlatformService.listByVersion("2.0.0");
		ApiResult<List<FreeTradePlatform>> result = new ApiResult<List<FreeTradePlatform>>();
		return result.toJSONString(0, "SUCCESS", list);
	}
	
	/**
	 * 自由交易列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: freeTradePlatformList2 
	 * @return
	 * @date 2016年2月18日 下午6:18:20  
	 * @author xiongbin
	 */
	@RequestMapping(value="freetradeplatform/list/2.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String freeTradePlatformList2(HttpServletRequest request){
		ApiResult<List<FreeTradePlatform>> result = new ApiResult<List<FreeTradePlatform>>();
		String version = VersionUtil.getAppVersion(request);
		if(StringUtils.isBlank(version)){
			logger.error("无法获取APP版本号");
			return result.toJSONString(-1, "请传递版本号");
		}else{
			logger.info("获取APP版本号为:" + version);
		}
		List<FreeTradePlatform> list = freeTradePlatformService.listByVersion(version);
		return result.toJSONString(0, "SUCCESS", list);
	}
}
