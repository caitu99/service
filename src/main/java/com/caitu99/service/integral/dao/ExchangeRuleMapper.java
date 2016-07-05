package com.caitu99.service.integral.dao;

import java.util.List;

import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface ExchangeRuleMapper extends IEntityDAO<ExchangeRule, ExchangeRule> {
	int deleteByPrimaryKey(Long id);

	int insert(ExchangeRule record);

	int insertSelective(ExchangeRule record);

	ExchangeRule selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ExchangeRule record);

	int updateByPrimaryKey(ExchangeRule record);

	ExchangeRule findByCardTypeAndWay(ExchangeRule record);

	List<ExchangeRule> list();

	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findByCardTypeId 
	 * @param cardTypeId
	 * @return
	 * @date 2016年1月6日 上午10:28:53  
	 * @author xiongbin
	*/
	ExchangeRule findByCardTypeId(Long cardTypeId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findByCardTypeName 
	 * @param cardTypeName
	 * @return
	 * @date 2016年5月16日 下午5:53:44  
	 * @author ws
	*/
	ExchangeRule findByCardTypeName(String cardTypeName);
}