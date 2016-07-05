package com.caitu99.service.user.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.sys.controller.api.ConfigController;
import com.caitu99.service.user.domain.UserThirdInfo;
import com.caitu99.service.user.service.UserThirdInfoService;

@Controller
@RequestMapping("/user/third/info")
public class UserthirdinfoController  extends BaseController  {
	private static final Logger logger = LoggerFactory
			.getLogger(ConfigController.class);
	
	@Autowired
	private UserThirdInfoService userThirdInfoService;

	@RequestMapping(value="/update/1.0", produces="application/json;charset=utf-8")
	public String update(UserThirdInfo userThirdInfo, Long userId) {
		// 初始化
		ApiResult<Boolean> result = new ApiResult<Boolean>();

		try {
			userThirdInfoService.updateByuserid(userThirdInfo, userId);
			result.setCode(0);
			result.setData(true);
			result.setMessage("更新成功!");
		} catch (Exception e) {
			result.setCode(2309);
			result.setMessage("更新第三方信息失败");
			result.setData(false);
//			logger.warn("");
		}
		return JSON.toJSONString(result);
	}

}
