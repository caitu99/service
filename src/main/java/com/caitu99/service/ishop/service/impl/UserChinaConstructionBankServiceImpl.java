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
import com.caitu99.service.ishop.dao.UserChinaConstructionBankMapper;
import com.caitu99.service.ishop.domain.UserChinaConstructionBank;
import com.caitu99.service.ishop.service.UserChinaConstructionBankService;

@Service
public class UserChinaConstructionBankServiceImpl implements UserChinaConstructionBankService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserChinaConstructionBankServiceImpl.class);
	
	private static final String[] AUTO_ACCOUNT_FILLTER = {"loginAccount","password"};

	@Autowired
	private UserChinaConstructionBankMapper userChinaConstructionBankMapper;
	@Autowired
	private RedisOperate redis;

	@Override
	public void insert(Long userId, String loginAccount, String password,String phone) {
		Date now = new Date();
		UserChinaConstructionBank userChinaConstructionBank = new UserChinaConstructionBank();
		userChinaConstructionBank.setGmtCreate(now);
		userChinaConstructionBank.setGmtModify(now);
		userChinaConstructionBank.setLoginAccount(loginAccount);
		userChinaConstructionBank.setPassword(password);
		userChinaConstructionBank.setPhone(phone);
		userChinaConstructionBank.setStatus(1);
		userChinaConstructionBank.setUserId(userId);
		
		userChinaConstructionBankMapper.insert(userChinaConstructionBank);
		updateRedis(userChinaConstructionBank);
	}

	@Override
	public UserChinaConstructionBank selectByUserIdNewest(Long userId) {
		String key = String.format(RedisKey.CCB_AUTO_ACCOUNT_BY_USERID, userId);
		try {
			String content = redis.getStringByKey(key);
			if(StringUtils.isNotBlank(content)){
				return JSON.parseObject(content, UserChinaConstructionBank.class);
			}
		} catch (Exception e) {
			logger.error("将建行积分商城用户信息从redis取出出错:" + e.getMessage());
		}
		
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("userId", userId);
		map.put("start", 0);
		map.put("pageSize", 1);
		
		List<UserChinaConstructionBank> list = userChinaConstructionBankMapper.selectPageListByUserId(map);
		if(null!=list && list.size()>0){
			UserChinaConstructionBank userChinaConstructionBank = list.get(0);
			updateRedis(userChinaConstructionBank);
			return userChinaConstructionBank;
		}
		
		return null;
	}

	@Override
	public UserChinaConstructionBank selectWhether(Long userId,String loginAccount) {
		UserChinaConstructionBank userChinaConstructionBank = new UserChinaConstructionBank();
		userChinaConstructionBank.setLoginAccount(loginAccount);
		userChinaConstructionBank.setUserId(userId);
		
		return userChinaConstructionBankMapper.selectWhether(userChinaConstructionBank);
	}

	@Override
	public void update(UserChinaConstructionBank userChinaConstructionBank) {
		userChinaConstructionBankMapper.update(userChinaConstructionBank);
		updateRedis(userChinaConstructionBank);
	}
	
	@Override
	public void updateRedis(UserChinaConstructionBank userChinaConstructionBank) {
		try {
			String key = String.format(RedisKey.CCB_AUTO_ACCOUNT_BY_USERID, userChinaConstructionBank.getUserId());
			SimplePropertyPreFilter filter = new SimplePropertyPreFilter(UserChinaConstructionBank.class,AUTO_ACCOUNT_FILLTER);
			redis.set(key, JSON.toJSONString(userChinaConstructionBank,filter));
		} catch (Exception e) {
			logger.error("将建行积分商城用户信息设置进redis出错:" + e.getMessage());
		}
	}
}
