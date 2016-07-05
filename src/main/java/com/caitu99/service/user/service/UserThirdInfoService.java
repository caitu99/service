package com.caitu99.service.user.service;


import com.caitu99.service.user.domain.UserThirdInfo;

public interface UserThirdInfoService {
	int deleteByPrimaryKey(Long id);

	int insert(UserThirdInfo record);

	int insertSelective(UserThirdInfo record);

	UserThirdInfo selectByUserId(Long userId);

	UserThirdInfo selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(UserThirdInfo record);

	int updateByPrimaryKey(UserThirdInfo record);

	UserThirdInfo insertOrUpdate(UserThirdInfo userThirdInfo, Long userId);

	void updateByuserid(UserThirdInfo userThirdInfo, Long userId);
}
