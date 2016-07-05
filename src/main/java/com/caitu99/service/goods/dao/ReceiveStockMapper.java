package com.caitu99.service.goods.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.goods.domain.CouponReceiveStock;
import com.caitu99.service.goods.domain.ReceiveStock;

public interface ReceiveStockMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ReceiveStock record);

    int insertSelective(ReceiveStock record);

    ReceiveStock selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReceiveStock record);

    int updateByPrimaryKey(ReceiveStock record);

    List<CouponReceiveStock> selectReceiveByUserIdAndOrderNo(Map<String, Object> map);

    Integer selectCouponReceiveCountByUserIdAndOrderNo(Map<String, Object> map);

    List<CouponReceiveStock> selectReceiveByUserId(Map<String, Object> map);

    Integer selectCouponReceiveCountByUserId(Map<String, Object> map);

    List<ReceiveStock> selectReceiveStockByOrderNoAndUserId(Map<String, Object> map);

    ReceiveStock getReceiveStockByRecord(ReceiveStock record);

    List<ReceiveStock> selectByInventory(Map map);
}

