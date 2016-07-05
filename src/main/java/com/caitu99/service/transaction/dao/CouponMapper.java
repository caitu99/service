package com.caitu99.service.transaction.dao;

import com.caitu99.service.transaction.domain.Coupon;

public interface CouponMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Coupon record);

    int insertSelective(Coupon record);

    Coupon selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Coupon record);

    int updateByPrimaryKey(Coupon record);
    
    Coupon selectByCode(String code);
}