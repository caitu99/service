package com.caitu99.service.integral.controller.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.service.ExchangeRuleService;

@Controller
@RequestMapping("/api/integral/rule/exchange")
public class ExchangeRuleController {

	private static final Logger logger = LoggerFactory.getLogger(ExchangeRuleController.class);

	@Autowired
	private ExchangeRuleService exchangeRuleService;
	
	@Autowired
    private AppConfig appConfig;


	//积分兑换比例
	@RequestMapping(value={"/list/1.0", "/list/2.0"}, produces="application/json;charset=utf-8")
	@ResponseBody
	public String list() {
		// 初始化
		ApiResult<List<ExchangeRule>> result = new ApiResult<>();
		List<ExchangeRule> resultList = new ArrayList<>();
		// 业务实现
		boolean hasZhaoShang = false;
		List<ExchangeRule> exchangeRuleList = exchangeRuleService.listAll();
		for (ExchangeRule exchangeRule : exchangeRuleList) {
			exchangeRule.setCardTypePic( appConfig.staticUrl + exchangeRule.getCardTypePic() );
			if(null == exchangeRule) continue;
			if (exchangeRule.getCardTypeName().contains("招商")) {
				if (hasZhaoShang) {
					continue;
				}
				exchangeRule.setCardTypeName(exchangeRule.getCardTypeName().substring(0, 4));
				hasZhaoShang = true;
				resultList.add(exchangeRule);
			} else {
				resultList.add(exchangeRule);
			}
		}
		return result.toJSONString(0,"success",resultList);
	}
}
