package com.caitu99.service.realization.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.realization.domain.UserAddTerm;

public interface UserAddTermMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserAddTerm record);

    UserAddTerm selectByPrimaryKey(Long id);

    int update(UserAddTerm record);
    
    List<UserAddTerm> selectListByUserId(Long userId);
    
    List<UserAddTerm> selectUserAddTerm(Map<String,Object> map);
}