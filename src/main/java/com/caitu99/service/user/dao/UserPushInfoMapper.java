package com.caitu99.service.user.dao;

import java.util.List;

import com.caitu99.service.user.domain.UserPushInfo;

public interface UserPushInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserPushInfo record);

    int insertSelective(UserPushInfo record);

    UserPushInfo selectByPrimaryKey(Long id);
    
    UserPushInfo selectByUserId(Long userId);

    int updateByPrimaryKeySelective(UserPushInfo record);

    int updateByPrimaryKey(UserPushInfo record);
    
    List<UserPushInfo> selectAll();
}