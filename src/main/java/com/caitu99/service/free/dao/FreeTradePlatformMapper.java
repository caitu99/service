package com.caitu99.service.free.dao;

import java.util.List;

import com.caitu99.service.free.domain.FreeTradePlatform;

public interface FreeTradePlatformMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FreeTradePlatform record);

    int insertSelective(FreeTradePlatform record);

    FreeTradePlatform selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FreeTradePlatform record);

    int updateByPrimaryKey(FreeTradePlatform record);
    
    List<FreeTradePlatform> selectList();
}