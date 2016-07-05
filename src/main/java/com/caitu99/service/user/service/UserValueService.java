package com.caitu99.service.user.service;


import com.caitu99.service.user.domain.UserValue;

import java.util.List;

public interface UserValueService {

	// 查询所有记录
	List<UserValue> queryAll();

	UserValue listUserValueByLevel(Long level);

	void update(UserValue userValue);

	List<UserValue> selectUserValueByLevel(Long level);

	List<UserValue> selectAllUserValue();

	void insertuservalue(UserValue userValue);
}
