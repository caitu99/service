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
import com.caitu99.service.base.Pagination;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.integral.controller.api.CardIntegralController;
import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.dao.CardIntegralMapper;
import com.caitu99.service.integral.domain.CardIntegral;
import com.caitu99.service.integral.service.CardIntegralService;

@Service
public class CardIntegralServiceImpl implements CardIntegralService {

	private final static Logger logger = LoggerFactory.getLogger(CardIntegralController.class);

	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private CardIntegralMapper cardIntegralMapper;

	@Override
	public int insert(CardIntegral record) {
		return cardIntegralMapper.insert(record);
	}

	@Override
	public List<CardIntegral> list(Long cardId) {
		
		String key = String.format(RedisKey.INTEGRAL_CARD_INTEGRAL_LIST_BY_CARD_ID_KEY, cardId);
		String content = redis.getStringByKey(key);

		// get from redis
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseArray(content, CardIntegral.class);
		}

		// get from db
		List<CardIntegral> list = cardIntegralMapper.list(cardId);
		if (list != null) {
			redis.set(key, JSON.toJSONString(list));
		}
		return list;
	}

	@Override
	public int updateByPrimaryKeySelective(CardIntegral record) {
		return cardIntegralMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int deleteByCardId(Long cardId) {
		return cardIntegralMapper.deleteByCardId(cardId);
	}

	@Override
	public List<CardIntegralLastTime> selectLastTime(Long userId) {
		String key = String.format(RedisKey.INTEGRAL_CARD_INTEGRAL_LAST_TIME_LIST_BY_USER_ID_KEY, userId);
		String content = redis.getStringByKey(key);

		// get from redis
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseArray(content, CardIntegralLastTime.class);
		}

		// get from db
		List<CardIntegralLastTime> list = cardIntegralMapper.selectLastTime(userId);
		if (list != null) {
			redis.set(key, JSON.toJSONString(list));
		}
		return list;
	}

	@Override
	public List<CardIntegralLastTime> selectLastTimeNew(Long userId,Long cardId) {
//		String key = String.format(RedisKey.INTEGRAL_CARD_INTEGRAL_LAST_TIME_LIST_NEW_BY_USER_ID_KEY, userId,cardId);
//		String content = "";
//		
//		try {
//			content = redis.getStringByKey(key);
//		} catch (Exception e) {
//			logger.error("从redis中获取数据失败:" + e.getMessage());
//		}
//
//		if (!StringUtils.isEmpty(content)) {
//			return JSON.parseArray(content, CardIntegralLastTime.class);
//		}

		Map<String,Object> map = new HashMap<String,Object>(2);
		map.put("userId", userId);
		map.put("cardId", cardId);
		
		List<CardIntegralLastTime> list = cardIntegralMapper.selectLastTimeNew(map);
//		if (list != null) {
//			try {
//				redis.set(key, JSON.toJSONString(list));
//			} catch (Exception e) {
//				logger.error("将数据放入redis中失败:" + e.getMessage());
//			}
//		}
		return list;
	}

	@Override
	public List<CardIntegralLastTime> selectOtherTime(Long userId) {
		
		String key = String.format(RedisKey.INTEGRAL_CARD_INTEGRAL_OTHER_TIME_LIST_BY_USER_ID_KEY, userId);
		String content = redis.getStringByKey(key);

		// get from redis
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseArray(content, CardIntegralLastTime.class);
		}

		// get from db
		List<CardIntegralLastTime> list = cardIntegralMapper.selectOtherTime(userId);
		if (list != null) {
			redis.set(key, JSON.toJSONString(list));
		}
		return list;
	}
	
	@Override
	public CardIntegralLastTime selectLastTimeFirst(Long userId) {
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("userId", userId);
		map.put("start", 0);
		map.put("pageSize", 1);
		
		List<CardIntegralLastTime> list = cardIntegralMapper.selectLastTimePageList(map);
		
		if(null!=list && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public Pagination<CardIntegralLastTime> selectLastTimePageList(Long userId,Pagination<CardIntegralLastTime> pagination) {
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("userId", userId);
		map.put("start", pagination.getStart());
		map.put("pageSize", pagination.getPageSize());
		
		Integer count = cardIntegralMapper.selectPageCount(map);
		List<CardIntegralLastTime> list = cardIntegralMapper.selectLastTimePageList(map);
		pagination.setTotalRow(count);
		pagination.setDatas(list);
		
		return pagination;
	}

	@Override
	public CardIntegral selectFirstTimeByCardId(Long cardId) {
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("cardId", cardId);
		map.put("start", 0);
		map.put("pageSize", 1);
		
		return cardIntegralMapper.selectFirstTimeByCardId(map);
	}
}
