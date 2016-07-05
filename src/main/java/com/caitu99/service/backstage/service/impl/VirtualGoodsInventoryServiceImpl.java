package com.caitu99.service.backstage.service.impl;

import com.caitu99.service.backstage.dao.VirtualGoodsInventoryMapper;
import com.caitu99.service.backstage.domain.VirtualGoodsInventory;
import com.caitu99.service.backstage.service.VirtualGoodsInventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuzs on 2016/2/16.
 */
@Service
public class VirtualGoodsInventoryServiceImpl implements VirtualGoodsInventoryService {

    private final static Logger logger = LoggerFactory.getLogger(VirtualGoodsInventoryServiceImpl.class);

    @Autowired
    private VirtualGoodsInventoryMapper virtualGoodsInventoryMapper;


    @Override
    public List<VirtualGoodsInventory> selectByInventory(Date endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap<>();
        map.put("end", sdf.format(endTime));

        List<VirtualGoodsInventory> list = virtualGoodsInventoryMapper.selectByInventory(map);

        return list;
    }
}
