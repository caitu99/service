package com.caitu99.service.goods.service.impl;

import com.caitu99.service.goods.dao.AddrProidMapper;
import com.caitu99.service.goods.dao.OrdernoAddrMapper;
import com.caitu99.service.goods.domain.OrdernoAddr;
import com.caitu99.service.goods.service.OrdernoAddrService;
import com.caitu99.service.transaction.service.OrderAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hy on 16-2-23.
 */
@Service
public class OrdernoAddrServiceImpl implements OrdernoAddrService{
    private final static Logger logger = LoggerFactory.getLogger(OrdernoAddrServiceImpl.class);

    @Autowired
    private OrdernoAddrMapper ordernoAddrMapper;

    @Autowired
    private AddrProidMapper addrProidMapper;

    @Override
    public int insert(OrdernoAddr record) {
        return insert(record);
    }

    @Override
    public List<OrdernoAddr> selectByOrderNo(String orderno) {
        return ordernoAddrMapper.selectByOrderNo(orderno);
    }

    @Override
    public int updateByPrimaryKeySelective(OrdernoAddr record) {
        return ordernoAddrMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public String getProidByItemidAndOrderno(Long itemid, String orderno) {
        List<String> list = addrProidMapper.getProidByOrdernoItemid(itemid, orderno);
        if (list.size() == 1) {
            return list.get(0);
        }
        else {
            logger.error("数据库中数据异常，根据itemid: {}，orderno: {}查询到了{}个交行商品id",
                    itemid,orderno,list.size());
            return null;
        }
    }
}
