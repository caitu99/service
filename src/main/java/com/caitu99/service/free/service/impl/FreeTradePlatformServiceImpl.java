/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.free.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.FreeTradeException;
import com.caitu99.service.free.dao.FreeTradePlatformMapper;
import com.caitu99.service.free.domain.FreeTradePlatform;
import com.caitu99.service.free.service.FreeTradePlatformService;
import com.caitu99.service.utils.VersionUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: FreeTradePlatformServiceImpl 
 * @author Hongbo Peng
 * @date 2016年1月20日 上午10:11:13 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class FreeTradePlatformServiceImpl implements FreeTradePlatformService {
	
	private final static Logger logger = LoggerFactory.getLogger(FreeTradePlatformServiceImpl.class);

	@Autowired
	private FreeTradePlatformMapper freeTradePlatformMapper;
	
	@Autowired
	private RedisOperate redis;
	
	@Override
	public List<FreeTradePlatform> selectList() throws FreeTradeException{
		try {
			String key = RedisKey.FREE_TRADE_PLATFORM_LIST;
			String content = redis.getStringByKey(key);
			if(StringUtils.isNotBlank(content)){
				return JSON.parseArray(content,FreeTradePlatform.class);
			}
			List<FreeTradePlatform> list = freeTradePlatformMapper.selectList();
			if(null != list){
				redis.set(key, JSON.toJSONString(list));
			}
			return list;
		} catch (Exception e) {
			throw new FreeTradeException(3701, e.getMessage());
		}
	}

	@Override
	public List<FreeTradePlatform> listByVersion(String version) {
		try {
			String key = String.format(RedisKey.FREE_TRADE_PLATFORM_LIST_VERSION,version);
			String content = redis.getStringByKey(key);
			if(StringUtils.isNotBlank(content)){
				return JSON.parseArray(content,FreeTradePlatform.class);
			}
			
			List<FreeTradePlatform> list = this.selectList();
			List<FreeTradePlatform> listFilter = null;
			
			try {
				listFilter = filterByVersion(list,version);
			} catch (Exception e) {
				logger.error("积分变现列表过滤版本时出错:" + e.getMessage(),e);
			}
			
			if(null == listFilter){
				return list;
			}
			
			redis.set(key, JSON.toJSONString(listFilter));
			return listFilter;
		} catch (Exception e) {
			logger.error("积分变现列表过滤版本时出错:" + e.getMessage(),e);
			return null;
		}
	}

	private List<FreeTradePlatform> filterByVersion(List<FreeTradePlatform> list, String version) {
		List<FreeTradePlatform> newList = new ArrayList<FreeTradePlatform>();
		
		Long versionLong = VersionUtil.getVersionLong(version);
		
		for (FreeTradePlatform freeTradePlatform : list) {
			if(Long.valueOf(freeTradePlatform.getVersion().toString()).compareTo(versionLong) <= 0){
				newList.add(freeTradePlatform);
			}
		}
		
		return newList;
	}
	
	@Override
	public FreeTradePlatform selectByPrimaryKey(Long id) {
		String key = String.format(RedisKey.FREE_TRADE_PLATFORM_BY_ID, id);
		String content = redis.getStringByKey(key);
		if(StringUtils.isNotBlank(content)){
			return JSONObject.parseObject(content, FreeTradePlatform.class);
		}
		FreeTradePlatform freeTradePlatform = freeTradePlatformMapper.selectByPrimaryKey(id);
		if(freeTradePlatform != null){
			redis.set(key, JSON.toJSONString(freeTradePlatform));
		}
		return freeTradePlatform;
	}
}
