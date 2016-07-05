package com.caitu99.service.goods.service;

import com.caitu99.service.goods.domain.OrdernoAddr;

import java.util.List;

/**
 * Created by hy on 16-2-23.
 */
public interface OrdernoAddrService {
    int insert(OrdernoAddr record);
    List<OrdernoAddr> selectByOrderNo(String orderno);
    int updateByPrimaryKeySelective(OrdernoAddr record);
    String getProidByItemidAndOrderno(Long itemid, String orderno);
}
