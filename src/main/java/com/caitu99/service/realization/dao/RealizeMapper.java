package com.caitu99.service.realization.dao;

import java.util.List;

import com.caitu99.service.realization.domain.Realize;

public interface RealizeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Realize record);

    int insertSelective(Realize record);

    Realize selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Realize record);

    int updateByPrimaryKey(Realize record);
    
    List<Realize> selectByPlatformId(Long platformId);
}