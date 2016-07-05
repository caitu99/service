package com.caitu99.service.integral.controller.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.integral.domain.Consume;
import com.caitu99.service.integral.service.ConsumeService;

@Controller
@RequestMapping("/api/consume")
public class ConsumeController {

	private final static Logger logger = LoggerFactory.getLogger(ConsumeController.class);

	@Autowired
	private ConsumeService consumeService;

	//消费记录查询
	@RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String consumeAll(Long userId) {
		// 初始化
		ApiResult<List<Consume>> result = new ApiResult<>();
		// 业务实现
		List<Consume> consumeList = consumeService.consumeAll(userId);
		// 数据返回
		return result.toJSONString(0,"消费记录查询成功",consumeList);
	}
}
