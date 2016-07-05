package com.caitu99.service.backstage.dao;

import com.caitu99.service.backstage.domain.VirtualGoodsInventory;

import java.util.List;
import java.util.Map;

/**
 * Created by liuzs on 2016/2/19.
 */
public interface VirtualGoodsInventoryMapper {

    List<VirtualGoodsInventory> selectByInventory(Map map);
}

