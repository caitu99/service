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
import com.caitu99.service.sys.domain.Suggest;
import com.caitu99.service.sys.service.SuggestService;

@Controller
@RequestMapping("/api/suggest")
public class SuggestController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(ConfigController.class);

	@Autowired
	private SuggestService suggestService;

	@RequestMapping(value = "/suggest/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String signEvery(Suggest suggest, Long userid) {
		// 初始化
		ApiResult<Boolean> result = new ApiResult<Boolean>();


		suggest.setUserId(userid);

		//业务实现
		try {
			suggestService.insert(suggest);
			result.setCode(0);
			result.setData(true);
			result.setMessage("意见反馈成功");
		}
		catch (Exception e)
		{
			result.setCode(2302);
			result.setData(false);
			result.setMessage("意见反馈失败");
			logger.error("保存反馈消息失败：{}", e.getMessage());
		}
		return JSON.toJSONString(result);
	}
}
