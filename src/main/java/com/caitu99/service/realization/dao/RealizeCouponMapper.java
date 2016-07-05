package com.caitu99.service.realization.dao;

import java.util.Map;
import java.util.List;

import com.caitu99.service.realization.domain.RealizeCoupon;

public interface RealizeCouponMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RealizeCoupon record);

    int insertSelective(RealizeCoupon record);

    RealizeCoupon selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RealizeCoupon record);

    int updateByPrimaryKey(RealizeCoupon record);
    
    int checkExists(Map<String,String> map);
    
    List<RealizeCoupon> selectByRealizeRecordId(Long realizeRecordId);
}