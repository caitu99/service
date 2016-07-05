package com.caitu99.service.realization.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.realization.domain.RealizeDetail;

public interface RealizeDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RealizeDetail record);

    int insertSelective(RealizeDetail record);

    RealizeDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RealizeDetail record);

    int updateByPrimaryKey(RealizeDetail record);
    
    List<RealizeDetail> selectByRealizeId(Long realizeId);
    
    RealizeDetail selectByLevel(Map<String,String> map);
}