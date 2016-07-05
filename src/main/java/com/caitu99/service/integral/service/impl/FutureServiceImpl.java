package com.caitu99.service.integral.service.impl;

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
import com.caitu99.service.exception.FutureException;
import com.caitu99.service.integral.dao.FutureMapper;
import com.caitu99.service.integral.domain.Future;
import com.caitu99.service.integral.service.FutureService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.exception.AssertUtil;

@Service
public class FutureServiceImpl implements FutureService {
	
	private final static Logger logger = LoggerFactory.getLogger(FutureServiceImpl.class);

	@Autowired
	private FutureMapper futureMapper;
	
	@Autowired
	private RedisOperate redis;

	@Override
	public List<Future> findListByManualIdType(Long manualId,Integer type) throws FutureException{
		try {
			AssertUtil.notNull(manualId, "积分账户ID不能为空");
			AssertUtil.notNull(type, "配置类型不能为空");
			
			String key =  String.format(RedisKey.MANUAL_FUTURE_BY_MANUALID_TYPE,manualId,type);
			String content = redis.getStringByKey(key);
			
			if(!StringUtils.isBlank(content)){
				return JSON.parseArray(content, Future.class);
			}
			
			Map<String,Object> map = new HashMap<String,Object>(2);
			map.put("manualId", manualId);
			map.put("type", type);
			
			List<Future> list = futureMapper.findListByManualIdType(map);
			
			if(list != null){
				redis.set(key, JSON.toJSONString(list));
			}
			
			return list;
		} catch (Exception e) {
			logger.error("查询手动查询登录配置失败:" + e.getMessage(),e);
			throw new FutureException(ApiResultCode.QUERY_FUTURE_ERROR,e.getMessage());
		}
	}
}
