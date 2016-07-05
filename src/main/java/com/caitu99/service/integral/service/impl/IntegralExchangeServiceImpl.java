package com.caitu99.service.integral.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.integral.dao.IntegralExchangeMapper;
import com.caitu99.service.integral.domain.IntegralExchange;
import com.caitu99.service.integral.service.IntegralExchangeService;

@Service
public class IntegralExchangeServiceImpl implements IntegralExchangeService {
	
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private IntegralExchangeMapper integralexchangeMapper;

	@Override
	public int deleteByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return integralexchangeMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(IntegralExchange record) {
		// TODO Auto-generated method stub
		return integralexchangeMapper.insert(record);
	}

	@Override
	public int insertSelective(IntegralExchange record) {
		// TODO Auto-generated method stub
		return integralexchangeMapper.insertSelective(record);
	}

	@Override
	public IntegralExchange selectByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return integralexchangeMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(IntegralExchange record) {
		// TODO Auto-generated method stub
		return integralexchangeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(IntegralExchange record) {
		// TODO Auto-generated method stub
		return integralexchangeMapper.updateByPrimaryKey(record);
	}

	public List<IntegralExchange> selectAll() {
		try {
			return integralexchangeMapper.selectAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<IntegralExchange> selectByUserId(Long userId) {
		
		String key = String.format(RedisKey.INTEGRAL_INTEGRAL_EXCHANGE_LIST_BY_USER_ID_KEY, userId);
		String content = redis.getStringByKey(key);

		// get from redis
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseArray(content, IntegralExchange.class);
		}

		// get from db
		List<IntegralExchange> list = integralexchangeMapper.selectByUserId(userId);
		if (list != null) {
			redis.set(key, JSON.toJSONString(list));
		}
		return list;
	}
}
