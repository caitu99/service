package com.caitu99.service.user.service.impl;

import com.caitu99.service.user.dao.UserValueMapper;
import com.caitu99.service.user.domain.UserValue;
import com.caitu99.service.user.service.UserValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserValueServiceImpl implements UserValueService {

	@Autowired
	private UserValueMapper userValueMapper;

	@Override
	public List<UserValue> queryAll() {
		return userValueMapper.selectAllUserValue();
	}

	@Override
	public UserValue listUserValueByLevel(Long level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(UserValue userValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<UserValue> selectUserValueByLevel(Long level) {
		return userValueMapper.selectUserValueByLevel(level);
	}

	public List<UserValue> selectAllUserValue() {
		return userValueMapper.selectAllUserValue();
	}

	@Override
	public void insertuservalue(UserValue userValue) {
		userValueMapper.insertuservalue(userValue);
	}
}
