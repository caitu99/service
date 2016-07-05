package com.caitu99.service.transaction.dao;

import com.caitu99.service.transaction.domain.OrderAddress;

public interface OrderAddressMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderAddress record);

    int insertSelective(OrderAddress record);

    OrderAddress selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderAddress record);

    int updateByPrimaryKey(OrderAddress record);
    
    OrderAddress getAddressByOrderNo(String orderNo);
}