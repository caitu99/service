package com.caitu99.service.realization.dao;

import java.util.Map;

import com.caitu99.service.realization.domain.UserTerm;

public interface UserTermMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(UserTerm record);

    UserTerm selectByPrimaryKey(Long id);

    int update(UserTerm record);

    UserTerm selectUserTerm(Map<String,String> map);
}