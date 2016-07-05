package com.caitu99.service.user.dao;

import com.caitu99.service.user.domain.UserMail;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;
import java.util.Map;

public interface UserMailMapper extends IEntityDAO<UserMail, UserMail> {

	int deleteByPrimaryKey(Long id);

	int insert(UserMail record);

	int insertSelective(UserMail record);

	UserMail selectByPrimaryKey(Long id);

	UserMail selectByEmail(String email);

	List<UserMail> selectByUserId(Long userId);

	int updateByPrimaryKeySelective(UserMail record);

	int updateByPrimaryKey(UserMail record);

	int updateByLastUpdate(UserMail record);

	int updateByUserIdAndMail(UserMail record);

	UserMail selectByUserIdAndMail(UserMail userMail);

	List<UserMail> list();
	
	int delByUserIdAndMail(UserMail userMail);

	List<UserMail> selectByUserIdForJob(Map paramMap);

}