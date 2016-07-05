package com.caitu99.service.transaction.dao;

import com.caitu99.service.transaction.domain.CouponType;

public interface CouponTypeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CouponType record);

    int insertSelective(CouponType record);

    CouponType selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CouponType record);

    int updateByPrimaryKey(CouponType record);
}