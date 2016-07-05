package com.caitu99.service.user.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.user.domain.UserInvitation;

public interface UserInvitationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserInvitation record);

    int insertSelective(UserInvitation record);

    UserInvitation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserInvitation record);

    int updateByPrimaryKey(UserInvitation record);
    
    List<UserInvitation> selectBySelective(Map<String,Object> map);
}