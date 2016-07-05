package com.caitu99.service.manage.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.manage.dao.ManageCardUserAuthMapper;
import com.caitu99.service.manage.domain.ManageCardUserAuth;
import com.caitu99.service.manage.service.ManageCardUserAuthService;


@Service
public class ManageCardUserAuthServiceImpl implements ManageCardUserAuthService {

	private final static Logger logger = LoggerFactory.getLogger(ManageCardUserAuthServiceImpl.class);
	
	@Autowired
	private ManageCardUserAuthMapper manageCardUserAuthMapper;
	
	@Autowired
	private RedisOperate redis;

	@Override
	public ManageCardUserAuth selectByUserId(Long userId) {
		String key = String.format(RedisKey.MANAGE_CARD_USER_AUTH_BY_USERID,userId);
		try {
			String content = redis.getStringByKey(key);
			if(StringUtils.isNotBlank(content)){
				return JSON.parseObject(content, ManageCardUserAuth.class);
			}
		} catch (Exception e) {
			logger.error("从redis取在线办卡实名认证信息出错:" + e.getMessage(),e);
		}
		
		List<ManageCardUserAuth> list = manageCardUserAuthMapper.selectByUserId(userId);
		ManageCardUserAuth manageCardUserAuth = null;
		if(null!=list && list.size()>0){
			manageCardUserAuth = list.get(0);
		}
		
		try {
			if(null != manageCardUserAuth){
				redis.set(key, JSON.toJSONString(manageCardUserAuth));
			}
		} catch (Exception e) {
			logger.error("将在线办卡实名认证信息放入redis时出错:" + e.getMessage(),e);
		}
		
		return manageCardUserAuth;
	}

	@Override
	public boolean isAuth(Long userId) {
		ManageCardUserAuth manageCardUserAuth = this.selectByUserId(userId);
		if(null == manageCardUserAuth){
			return false;
		}
		return true;
	}

	@Override
	public void insert(ManageCardUserAuth manageCardUserAuth) {
		Date now = new Date();
		manageCardUserAuth.setGmtCreate(now);
		manageCardUserAuth.setGmtModify(now);
		manageCardUserAuth.setStatus(1);
		manageCardUserAuthMapper.insert(manageCardUserAuth);
	}

}
