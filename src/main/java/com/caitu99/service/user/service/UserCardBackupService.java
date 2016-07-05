package com.caitu99.service.user.service;


import com.caitu99.service.user.domain.UserCardBackup;

public interface UserCardBackupService {
	int deleteByPrimaryKey(Long id);

	int insert(UserCardBackup record);

	int insertSelective(UserCardBackup record);

	UserCardBackup selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(UserCardBackup record);

	int updateByPrimaryKey(UserCardBackup record);
}
