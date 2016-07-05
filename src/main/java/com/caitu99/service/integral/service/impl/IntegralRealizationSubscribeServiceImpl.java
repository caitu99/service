package com.caitu99.service.integral.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.integral.dao.IntegralRealizationSubscribeMapper;
import com.caitu99.service.integral.domain.IntegralRealizationSubscribe;
import com.caitu99.service.integral.service.IntegralRealizationSubscribeService;

@Service
public class IntegralRealizationSubscribeServiceImpl implements IntegralRealizationSubscribeService {
	
	private final static Logger logger = LoggerFactory.getLogger(IntegralRealizationSubscribeServiceImpl.class);

	@Autowired
	private IntegralRealizationSubscribeMapper integralRealizationSubscribeMapper;
	@Autowired
	private RedisOperate redis;

	@Override
	public IntegralRealizationSubscribe selectByUserIdCardTypeId(Long userId,Long cardTypeId) {
		Map<String,Object> map = new HashMap<String,Object>(2);
		map.put("userId", userId);
		map.put("cardTypeId", cardTypeId);
		
		return integralRealizationSubscribeMapper.selectByUserIdCardTypeId(map);
	}

	@Override
	public void subscribe(Long userId,Long cardTypeId) {
		Date now = new Date();
		IntegralRealizationSubscribe integralRealizationSubscribe = new IntegralRealizationSubscribe();
		integralRealizationSubscribe.setGmtCreate(now);
		integralRealizationSubscribe.setGmtModify(now);
		integralRealizationSubscribe.setCardTypeId(cardTypeId);
		integralRealizationSubscribe.setStatus(1);
		integralRealizationSubscribe.setUserId(userId);
		
		String key = String.format(RedisKey.INTEGRAL_REALIZATION_SUBSCRIBE_BY_CARDTYPEID, cardTypeId);
		redis.del(key);
		
		integralRealizationSubscribeMapper.insert(integralRealizationSubscribe);
	}

	@Override
	public Integer selectCount(Long cardTypeId) {
		String key = String.format(RedisKey.INTEGRAL_REALIZATION_SUBSCRIBE_BY_CARDTYPEID, cardTypeId);
		String content = redis.getStringByKey(key);
		
		try {
			if(StringUtils.isNotBlank(content)){
				return Integer.parseInt(content);
			}
		} catch (NumberFormatException e) {
			logger.error("从redis中获取数据转换失败:" + e.getMessage());
		}
		
		Integer count = integralRealizationSubscribeMapper.selectCount(cardTypeId);
		if(null != count){
			redis.set(key, count.toString());
		}
		
		return count;
	}
}
