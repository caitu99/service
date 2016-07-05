package com.caitu99.service.goods.dao;

import com.caitu99.service.goods.domain.OrdernoAddr;

import java.util.List;

public interface OrdernoAddrMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrdernoAddr record);

    int insertSelective(OrdernoAddr record);

    OrdernoAddr selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrdernoAddr record);

    int updateByPrimaryKey(OrdernoAddr record);
    List<OrdernoAddr> selectByOrderNo(String orderno);

}