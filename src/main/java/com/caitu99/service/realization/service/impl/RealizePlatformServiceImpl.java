package com.caitu99.service.realization.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.realization.dao.RealizePlatformMapper;
import com.caitu99.service.realization.domain.RealizePlatform;
import com.caitu99.service.realization.service.RealizePlatformService;


@Service
public class RealizePlatformServiceImpl implements RealizePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(RealizePlatformServiceImpl.class);
	
	@Autowired
	private RealizePlatformMapper realizationPlatformMapper;
	
	@Autowired
	private RedisOperate redis;

	@Override
	public List<RealizePlatform> selectPageList(String version) {
		String key = String.format(RedisKey.REALIZATION_PLATFORM_LIST_VERSION, version);
		try {
			String content = redis.getStringByKey(key);
			if(StringUtils.isNotBlank(content)){
				return JSON.parseArray(content, RealizePlatform.class);
			}
		} catch (Exception e) {
			logger.error("从redis中查询积分变现列表出错:" + e.getMessage(),e);
		}
		
		Map<String,String> map = new HashMap<String,String>(1);
		map.put("version", version);
		List<RealizePlatform> list = realizationPlatformMapper.selectPageList(map);
		if(null != list){
			try {
				redis.set(key, JSON.toJSONString(list));
			} catch (Exception e) {
				logger.error("将积分变现列表放入redis中出错:" + e.getMessage(),e);
			}
		}
		
		return list;
	}

	@Override
	public RealizePlatform selectByPrimaryKey(Long id) {
		String key = String.format(RedisKey.REALIZATION_PLATFORM_BY_ID, id);
		try {
			String content = redis.getStringByKey(key);
			if(StringUtils.isNotBlank(content)){
				return JSON.parseObject(content, RealizePlatform.class);
			}
		} catch (Exception e) {
			logger.error("从redis中查询积分变现平台出错:" + e.getMessage(),e);
		}
		
		RealizePlatform realizationPlatform = realizationPlatformMapper.selectByPrimaryKey(id);
		if(null != realizationPlatform){
			try {
				redis.set(key, JSON.toJSONString(realizationPlatform));
			} catch (Exception e) {
				logger.error("将积分变现平台放入redis中出错:" + e.getMessage(),e);
			}
		}
		
		return realizationPlatform;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.realization.service.RealizePlatformService#selectBySupport(java.lang.String, java.lang.String)
	 */
	@Override
	public List<RealizePlatform> selectBySupport(String version, String support) {
		Map<String,String> map = new HashMap<String,String>(1);
		map.put("version", version);
		map.put("support", support);
		List<RealizePlatform> list = realizationPlatformMapper.selectBySupport(map);

		return list;
	}
}
