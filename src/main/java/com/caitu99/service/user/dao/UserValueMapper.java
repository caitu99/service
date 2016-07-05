package com.caitu99.service.user.dao;


import com.caitu99.service.user.domain.UserValue;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface UserValueMapper extends IEntityDAO<UserValue, UserValue> {
	int deleteByPrimaryKey(Integer id);

	int insert(UserValue record);

	int insertSelective(UserValue record);

	UserValue selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(UserValue record);

	int updateByPrimaryKey(UserValue record);

	List<UserValue> selectUserValueByLevel(Long level);

	// 查询所有记录
	List<UserValue> queryAll();

	List<UserValue> selectAllUserValue();

	void insertuservalue(UserValue userValue);
}