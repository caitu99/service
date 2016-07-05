package com.caitu99.service.merchant.dao;

import java.util.List;

import com.caitu99.service.merchant.domain.SmspayPlatform;

public interface SmspayPlatformMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmspayPlatform record);

    int insertSelective(SmspayPlatform record);

    SmspayPlatform selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmspayPlatform record);

    int updateByPrimaryKey(SmspayPlatform record);
    
    List<SmspayPlatform> findAll();
}