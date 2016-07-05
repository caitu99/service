package com.caitu99.service.integral.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.integral.dao.CardTypeMapper;
import com.caitu99.service.integral.dao.ExchangeRuleMapper;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.service.ExchangeRuleService;

@Service
public class ExchangeRuleServiceImpl implements ExchangeRuleService {


	@Autowired
	private RedisOperate redis;

	@Autowired
	private ExchangeRuleMapper exchangeRuleMapper;

	@Override
	public int add(ExchangeRule exchangeRule) {
		return exchangeRuleMapper.insert(exchangeRule);
	}

	@Override
	public int modify(ExchangeRule exchangeRule) {
		return exchangeRuleMapper.updateByPrimaryKey(exchangeRule);
	}

	@Override
	public ExchangeRule findByCardTypeAndWay(ExchangeRule record) {
		return exchangeRuleMapper.findByCardTypeAndWay(record);
	}

	@Override
	public List<ExchangeRule> listAll() {

		String content = redis.getStringByKey(RedisKey.INTEGRAL_EXCHANGE_RULE_LIST_KEY);

		// get from redis
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseArray(content, ExchangeRule.class);
		}

		// get from db
		List<ExchangeRule> list = exchangeRuleMapper.list();
		if (list != null && !list.isEmpty()) {
			redis.set(RedisKey.INTEGRAL_EXCHANGE_RULE_LIST_KEY, JSON.toJSONString(list));
		}
		return list;
	}

	@Override
	public int updateByPrimaryKeySelective(ExchangeRule record) {
		return exchangeRuleMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public ExchangeRule findByCardTypeId(Long cardTypeId) {
		
		return exchangeRuleMapper.findByCardTypeId(cardTypeId);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.service.ExchangeRuleService#findByCardTypeName(java.lang.String)
	 */
	@Override
	public ExchangeRule findByCardTypeName(String cardTypeName) {

		return exchangeRuleMapper.findByCardTypeName(cardTypeName);
	}
}
