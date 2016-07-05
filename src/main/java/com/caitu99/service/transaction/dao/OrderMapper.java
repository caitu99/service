package com.caitu99.service.transaction.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.transaction.domain.Order;
import com.caitu99.service.transaction.dto.OrderDto;

public interface OrderMapper {
    int deleteByPrimaryKey(String orderNo);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(String orderNo);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
    
    Integer selectPageCount(Map<String, Object> map);
    
    List<OrderDto> selectPageList(Map<String, Object> map);
    
    List<Order> findAllOrderByFreeTrade(Map<String,Object> map);
    
    List<Order> findOrderByOutNo(Map<String,Object> map);
    
    Order selectOrderByOutNo(String outNo);
}
