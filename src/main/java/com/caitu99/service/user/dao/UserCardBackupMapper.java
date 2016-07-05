package com.caitu99.service.user.dao;


import com.caitu99.service.user.domain.UserCardBackup;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface UserCardBackupMapper extends IEntityDAO<UserCardBackup, UserCardBackup> {
    int deleteByPrimaryKey(Long id);

    int insert(UserCardBackup record);

    int insertSelective(UserCardBackup record);

    UserCardBackup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCardBackup record);

    int updateByPrimaryKey(UserCardBackup record);
}