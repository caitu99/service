package com.caitu99.service.manage.dao;

import java.util.List;

import com.caitu99.service.manage.domain.ManageCardUserAuth;

public interface ManageCardUserAuthMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(ManageCardUserAuth record);

    ManageCardUserAuth selectByPrimaryKey(Long id);

    int update(ManageCardUserAuth record);

    List<ManageCardUserAuth> selectByUserId(Long userId);
}