package com.caitu99.service.goods.service.impl;

import com.alibaba.druid.filter.AutoLoad;
import com.caitu99.service.goods.dao.AddrProidMapper;
import com.caitu99.service.goods.service.GetAddressByItemIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 16-2-22.
 */
@Service
public class GetAddressByItemIdServiceImpl implements GetAddressByItemIdService{
    private final static Logger logger = LoggerFactory.getLogger(GetAddressByItemIdServiceImpl.class);

    @Autowired
    private AddrProidMapper addrProidMapper;

    @Override
    public List<String> getAddressByItemId(Long itemid) {
        return addrProidMapper.getAddressByItemId(itemid);
    }
}
