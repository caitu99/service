package com.caitu99.service.sys.controller.api;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.sys.domain.VersionStatistics;
import com.caitu99.service.sys.service.VersionStatisticsService;

@Controller
@RequestMapping("/api/version")
public class VersionStatisticsController  extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(ConfigController.class);

	@Autowired
	private VersionStatisticsService versionStatisticsService;

	@RequestMapping(value="/add/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String add(VersionStatistics record,Long userid) throws ParseException {
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		record.setUserid(userid);
		versionStatisticsService.insert(record);
		result.setCode(0);
		result.setData(true);
		result.setMessage("信息添加成功");
		return JSON.toJSONString(result);
	}
}
