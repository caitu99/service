package com.caitu99.service.ishop.dao;

import com.caitu99.service.ishop.domain.UserPwd;

public interface UserPwdMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserPwd record);

    int insertSelective(UserPwd record);

    UserPwd selectByPrimaryKey(Long id);

    UserPwd selectByUserAndAccount(UserPwd userPwd);

    int updateByPrimaryKeySelective(UserPwd record);

    int updateByPrimaryKey(UserPwd record);
}