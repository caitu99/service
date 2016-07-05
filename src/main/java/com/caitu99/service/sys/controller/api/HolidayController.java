package com.caitu99.service.sys.controller.api;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.sys.domain.Holiday;
import com.caitu99.service.sys.service.HolidayService;
import com.caitu99.service.utils.string.StrUtil;

@Controller
@RequestMapping("/api/holiday")
public class HolidayController  extends BaseController {

	@Autowired
	private HolidayService holidayService;

	// 增加一年的周末时间
	@RequestMapping(value="/add/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String add(Date date) {
		// 初始化
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		result.setCode(0);

		// 业务实现
		for (int i = 1; i < 365; i++) {
			date = StrUtil.adddate(date, 1);
			if (date.getDay() == 6 || date.getDay() == 0) {
				Holiday holiday = new Holiday();
				holiday.setDate(date);
				holiday.setStatus(1);
				holiday.setType(1);
				holidayService.insert(holiday);
			}
		}

		// 数据返回
		result.setData(true);
		return JSON.toJSONString(result);
	}
}