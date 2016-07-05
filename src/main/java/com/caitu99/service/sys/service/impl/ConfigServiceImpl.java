package com.caitu99.service.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.sys.dao.ConfigMapper;
import com.caitu99.service.sys.domain.Config;
import com.caitu99.service.sys.service.ConfigService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {


	@Autowired
	private RedisOperate redis;

	@Autowired
	private ConfigMapper configMapper;

	@Override
	public List<Config> selectAll() {
		String content = redis.getStringByKey(RedisKey.SYS_CONFIG_LIST_KEY);

		if (StringUtils.isNotEmpty(content)) {
			return JSON.parseArray(content, Config.class);
		}

		List<Config> configList = configMapper.getAll();

		if (configList.size() > 0) {
			content = JSON.toJSONString(configList);
			redis.set(RedisKey.SYS_CONFIG_LIST_KEY, content);
		}

		return configList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.sys.service.ConfigService#selectByKey(java.lang.String)
	 */
	@Override
	public Config selectByKey(String key) {

		String redisKey = String.format(RedisKey.SYS_CONFIG_BY_KEY_KEY, key);
		String content = redis.getStringByKey(redisKey);

		if (StringUtils.isNotEmpty(content)) {
			return JSON.parseObject(content, Config.class);
		}

		Config config = configMapper.selectByKey(key);

		content = JSON.toJSONString(config);
		redis.set(redisKey, content);

		return config;
	}
}
