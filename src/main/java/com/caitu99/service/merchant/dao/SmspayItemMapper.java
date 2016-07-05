package com.caitu99.service.merchant.dao;

import java.util.List;

import com.caitu99.service.merchant.domain.SmspayItem;

public interface SmspayItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmspayItem record);

    int insertSelective(SmspayItem record);

    SmspayItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmspayItem record);

    int updateByPrimaryKey(SmspayItem record);
    
    List<SmspayItem> findAllByPlatformId(Long platformId);
}