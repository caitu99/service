package com.caitu99.service.transaction.dao;

import java.util.List;

import com.caitu99.service.transaction.domain.UserCoupon;

public interface UserCouponMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserCoupon record);

    int insertSelective(UserCoupon record);

    UserCoupon selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCoupon record);

    int updateByPrimaryKey(UserCoupon record);
    
    List<UserCoupon> selectByUserId(UserCoupon record);
}