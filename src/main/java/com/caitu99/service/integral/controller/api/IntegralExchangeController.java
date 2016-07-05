package com.caitu99.service.integral.controller.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.integral.domain.IntegralExchange;
import com.caitu99.service.integral.service.IntegralExchangeService;

@Controller
@RequestMapping("/api/integral/exchange")
public class IntegralExchangeController {

	private static final Logger logger = LoggerFactory.getLogger(IntegralExchangeController.class);

	@Autowired
	private IntegralExchangeService integralExchangeService;

	//查询积分兑换记录
	@RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String list(Long userId) {
		// 初始化
		ApiResult<List<IntegralExchange>> result = new ApiResult<>();
		// 业务实现
		List<IntegralExchange> integralExchangeList = integralExchangeService.selectByUserId(userId);
		// 数据返回
		return result.toJSONString(0,"查询积分兑换记录成功",integralExchangeList);
	}
}
