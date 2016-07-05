package com.caitu99.service.ishop.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.ishop.dao.UserChinaConstructionBankCardMapper;
import com.caitu99.service.ishop.domain.UserChinaConstructionBankCard;
import com.caitu99.service.ishop.service.UserChinaConstructionBankCardService;

@Deprecated
@Service
public class UserChinaConstructionBankCardServiceImpl implements UserChinaConstructionBankCardService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserChinaConstructionBankCardServiceImpl.class);
	
	private static final String[] AUTO_BANKCARD_FILLTER = {"cardNo","reservedPhone"};

	@Autowired
	private UserChinaConstructionBankCardMapper userChinaConstructionBankCardMapper;
	@Autowired
	private RedisOperate redis;

	@Override
	public void insert(Long userId,String cardNo,String reservedPhone) {
		Date now = new Date();
		UserChinaConstructionBankCard userChinaConstructionBankCard = new UserChinaConstructionBankCard();
		userChinaConstructionBankCard.setGmtCreate(now);
		userChinaConstructionBankCard.setGmtModify(now);
		userChinaConstructionBankCard.setCardNo(cardNo);
		userChinaConstructionBankCard.setReservedPhone(reservedPhone);
		userChinaConstructionBankCard.setStatus(1);
		userChinaConstructionBankCard.setUserId(userId);
		
		userChinaConstructionBankCardMapper.insert(userChinaConstructionBankCard);
		updateRedis(userChinaConstructionBankCard);
	}

	@Override
	public UserChinaConstructionBankCard selectByUserIdNewest(Long userId) {
		String key = String.format(RedisKey.CCB_AUTO_BANKCARD_BY_USERID, userId);
		try {
			String content = redis.getStringByKey(key);
			if(StringUtils.isNotBlank(content)){
				return JSON.parseObject(content, UserChinaConstructionBankCard.class);
			}
		} catch (Exception e) {
			logger.error("将建行积分商城银行卡信息从redis取出出错:" + e.getMessage());
		}
		
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("userId", userId);
		map.put("start", 0);
		map.put("pageSize", 1);
		
		List<UserChinaConstructionBankCard> list = userChinaConstructionBankCardMapper.selectPageListByUserId(map);
		if(null!=list && list.size()>0){
			UserChinaConstructionBankCard userChinaConstructionBankCard = list.get(0);
			updateRedis(userChinaConstructionBankCard);
			return userChinaConstructionBankCard;
		}
		
		return null;
	}

	@Override
	public UserChinaConstructionBankCard selectWhether(Long userId,String cardNo) {
		UserChinaConstructionBankCard userChinaConstructionBankCard = new UserChinaConstructionBankCard();
		userChinaConstructionBankCard.setCardNo(cardNo);
		userChinaConstructionBankCard.setUserId(userId);
		
		return userChinaConstructionBankCardMapper.selectWhether(userChinaConstructionBankCard);
	}

	@Override
	public void update(UserChinaConstructionBankCard userChinaConstructionBankCard) {
		userChinaConstructionBankCardMapper.update(userChinaConstructionBankCard);
		updateRedis(userChinaConstructionBankCard);
	}
	
	@Override
	public void updateRedis(UserChinaConstructionBankCard userChinaConstructionBankCard) {
		try {
			String key = String.format(RedisKey.CCB_AUTO_BANKCARD_BY_USERID, userChinaConstructionBankCard.getUserId());
			SimplePropertyPreFilter filter = new SimplePropertyPreFilter(UserChinaConstructionBankCard.class,AUTO_BANKCARD_FILLTER);
			redis.set(key, JSON.toJSONString(userChinaConstructionBankCard,filter));
		} catch (Exception e) {
			logger.error("将建行积分商城银行卡信息设置进redis出错:" + e.getMessage());
		}
	}
}
