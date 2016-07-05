package com.caitu99.service.integral.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.integral.dao.ConsumeMapper;
import com.caitu99.service.integral.domain.Consume;
import com.caitu99.service.integral.service.ConsumeService;

@Service
public class ConsumeServieImpl implements ConsumeService {

	
	@Autowired
	private ConsumeMapper consumeMapper;
	
	@Autowired
	private RedisOperate redis;

	@Override
	public List<Consume> consumeAll(Long userId) {
		
		String key = String.format(RedisKey.INTEGRAL_CONSUME_LIST_BY_USER_ID_KEY, userId);
		String content = redis.getStringByKey(key);

		// get from redis
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseArray(content, Consume.class);
		}

		// get from db
		List<Consume> list = consumeMapper.consumeAll(userId);
		if (list != null) {
			redis.set(key, JSON.toJSONString(list));
		}
		return list;

	}

	@Override
	public int insert(Consume record) {
		int result = consumeMapper.insert(record);
		String key = String.format(RedisKey.INTEGRAL_CONSUME_LIST_BY_USER_ID_KEY, record.getUserid());
		redis.del(key);
		return result;
	}

}
