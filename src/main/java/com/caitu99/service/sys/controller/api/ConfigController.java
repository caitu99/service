package com.caitu99.service.sys.controller.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.sys.domain.Config;
import com.caitu99.service.sys.service.ConfigService;

@Controller
@RequestMapping("/api/config")
public class ConfigController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

	@Autowired
	private ConfigService configService;

	@RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String list() {
		ApiResult<List<Config>> result = new ApiResult<List<Config>>();
		result.setCode(0);
		List<Config> configList = configService.selectAll();
		result.setData(configList);
		logger.debug("get all configure", JSON.toJSONString(configList));
		return JSON.toJSONString(result);
	}
	
	@RequestMapping(value="/bykey/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String byKey(String key) {
		ApiResult<Config> result = new ApiResult<Config>();
		result.setCode(0);
		Config config = configService.selectByKey(key);
		result.setData(config);
		logger.debug("get configure", JSON.toJSONString(config));
		return JSON.toJSONString(result);
	}
	
}
