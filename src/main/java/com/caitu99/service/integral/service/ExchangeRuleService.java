package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.integral.domain.ExchangeRule;

public interface ExchangeRuleService {

	// 添加兑换规则
	int add(ExchangeRule exchangeRule);

	// 修改兑换规则
	int modify(ExchangeRule exchangeRule);

	// 根据卡片类型及兑换方式查找兑换规则
	ExchangeRule findByCardTypeAndWay(ExchangeRule record);

	// 获取所有兑换规则
	List<ExchangeRule> listAll();

	// 修改数据
	int updateByPrimaryKeySelective(ExchangeRule record);

	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findByCardTypeId 
	 * @param cardTypeId
	 * @return
	 * @date 2016年1月6日 上午10:28:43  
	 * @author xiongbin
	*/
	ExchangeRule findByCardTypeId(Long cardTypeId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findByCardTypeName 
	 * @param name
	 * @return
	 * @date 2016年5月16日 下午5:52:56  
	 * @author ws
	*/
	ExchangeRule findByCardTypeName(String cardTypeName);

}
