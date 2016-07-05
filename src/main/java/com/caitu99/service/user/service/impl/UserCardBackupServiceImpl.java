package com.caitu99.service.user.service.impl;

import com.caitu99.service.user.dao.UserCardBackupMapper;
import com.caitu99.service.user.domain.UserCardBackup;
import com.caitu99.service.user.service.UserCardBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCardBackupServiceImpl implements UserCardBackupService {

	@Autowired
	private UserCardBackupMapper userCardBackupMapper;

	@Override
	public int deleteByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(UserCardBackup record) {
		return userCardBackupMapper.insert(record);
	}

	@Override
	public int insertSelective(UserCardBackup record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UserCardBackup selectByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(UserCardBackup record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(UserCardBackup record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
