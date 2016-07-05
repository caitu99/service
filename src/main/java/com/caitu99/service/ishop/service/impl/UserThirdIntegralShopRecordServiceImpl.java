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
import com.caitu99.service.ishop.dao.UserThirdIntegralShopRecordMapper;
import com.caitu99.service.ishop.domain.UserThirdIntegralShopRecord;
import com.caitu99.service.ishop.service.UserThirdIntegralShopRecordService;

@Service
public class UserThirdIntegralShopRecordServiceImpl implements UserThirdIntegralShopRecordService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserThirdIntegralShopRecordServiceImpl.class);
	
	private static final String[] AUTO_ACCOUNT_FILLTER = {"loginAccount","password"};

	@Autowired
	private UserThirdIntegralShopRecordMapper userThirdIntegralShopRecordMapper;
	@Autowired
	private RedisOperate redis;

	@Override
	public void insert(Long userId, String loginAccount, String password,String phone,Integer type) {
		Date now = new Date();
		UserThirdIntegralShopRecord userThirdIntegralShopRecord = new UserThirdIntegralShopRecord();
		userThirdIntegralShopRecord.setGmtCreate(now);
		userThirdIntegralShopRecord.setGmtModify(now);
		userThirdIntegralShopRecord.setLoginAccount(loginAccount);
		userThirdIntegralShopRecord.setPassword(password);
		userThirdIntegralShopRecord.setPhone(phone);
		userThirdIntegralShopRecord.setStatus(UserThirdIntegralShopRecord.STATUS_NORMAL);
		userThirdIntegralShopRecord.setUserId(userId);
		userThirdIntegralShopRecord.setType(type);
		
		userThirdIntegralShopRecordMapper.insert(userThirdIntegralShopRecord);
		updateRedis(userThirdIntegralShopRecord);
	}

	@Override
	public UserThirdIntegralShopRecord selectByUserIdNewest(Long userId,Integer type) {
		String key = null;
		if(UserThirdIntegralShopRecord.TYPE_CCB.equals(type)){
			key = String.format(RedisKey.CCB_AUTO_ACCOUNT_BY_USERID, userId);
		}else if(UserThirdIntegralShopRecord.TYPE_ESURFING.equals(type)){
			key = String.format(RedisKey.ESURFING_AUTO_ACCOUNT_BY_USERID, userId);
		}else{
			logger.error("获取第三方积分商城用户登录数据出错:key为null");
			return null;
		}
		
		try {
			String content = redis.getStringByKey(key);
			if(StringUtils.isNotBlank(content)){
				return JSON.parseObject(content, UserThirdIntegralShopRecord.class);
			}
		} catch (Exception e) {
			logger.error("将第三方积分商城用户信息从redis取出出错:" + e.getMessage());
		}
		
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("userId", userId);
		map.put("type", type);
		map.put("start", 0);
		map.put("pageSize", 1);
		
		List<UserThirdIntegralShopRecord> list = userThirdIntegralShopRecordMapper.selectPageListByUserId(map);
		if(null!=list && list.size()>0){
			UserThirdIntegralShopRecord userThirdIntegralShopRecord = list.get(0);
			updateRedis(userThirdIntegralShopRecord);
			return userThirdIntegralShopRecord;
		}
		
		return null;
	}

	@Override
	public UserThirdIntegralShopRecord selectWhether(Long userId,String loginAccount,Integer type) {
		UserThirdIntegralShopRecord userThirdIntegralShopRecord = new UserThirdIntegralShopRecord();
		userThirdIntegralShopRecord.setLoginAccount(loginAccount);
		userThirdIntegralShopRecord.setUserId(userId);
		userThirdIntegralShopRecord.setType(type);
		
		return userThirdIntegralShopRecordMapper.selectWhether(userThirdIntegralShopRecord);
	}

	@Override
	public void update(UserThirdIntegralShopRecord userThirdIntegralShopRecord) {
		userThirdIntegralShopRecordMapper.update(userThirdIntegralShopRecord);
		updateRedis(userThirdIntegralShopRecord);
	}
	
	@Override
	public void updateRedis(UserThirdIntegralShopRecord userThirdIntegralShopRecord) {
		Integer type = userThirdIntegralShopRecord.getType();
		Long userId = userThirdIntegralShopRecord.getUserId();
		String key = null;
		
		if(UserThirdIntegralShopRecord.TYPE_CCB.equals(type)){
			key = String.format(RedisKey.CCB_AUTO_ACCOUNT_BY_USERID, userId);
		}else if(UserThirdIntegralShopRecord.TYPE_ESURFING.equals(type)){
			key = String.format(RedisKey.ESURFING_AUTO_ACCOUNT_BY_USERID, userId);
		}else{
			logger.error("获取第三方积分商城用户登录数据出错:key为null");
			return;
		}
		
		try {
			SimplePropertyPreFilter filter = new SimplePropertyPreFilter(UserThirdIntegralShopRecord.class,AUTO_ACCOUNT_FILLTER);
			redis.set(key, JSON.toJSONString(userThirdIntegralShopRecord,filter));
		} catch (Exception e) {
			logger.error("将第三方积分商城用户信息设置进redis出错:" + e.getMessage());
		}
	}
}
