package com.caitu99.service.transaction.dao;

import java.util.List;

import com.caitu99.service.transaction.domain.OrderItem;
import com.caitu99.service.transaction.dto.OrderItemDto;


public interface OrderItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
    
    List<OrderItemDto> findAllItemByOrderNo(String ordrNo);
    
	List<OrderItem> listByOrderNo(String orderNo);
}
