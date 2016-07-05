package com.caitu99.service.user.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.user.dao.UserAuthMapper;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.service.UserAuthService;

@Service
public class UserAuthServiceImpl implements UserAuthService {

	@Autowired
	private RedisOperate redis;
	@Autowired
	private UserAuthMapper userAuthMapper;
	@Autowired
	private UserMapper userMapper;

	@Override
	@Transactional
	public int insert(UserAuth userAuth) {
		User user = new User();
		user.setId(userAuth.getUserId());
		user.setIsauth(1);
		userMapper.updateByPrimaryKeySelective(user);
		int result = userAuthMapper.insert(userAuth);
		return result;
	}

	@Override
	public UserAuth selectByUserId(Long userId) {
		UserAuth userAuth = userAuthMapper.selectByUserId(userId);
		return userAuth;
	}

	@Override
	public UserAuth findByUserId(Long userId) {
		return userAuthMapper.findByUserId(userId);
	}

	@Override
	public List<UserAuth> selectByAccId(String accId) {
		// TODO Auto-generated method stub
		return userAuthMapper.selectByAccId(accId);
	}

}
