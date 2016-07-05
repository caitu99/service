package com.caitu99.service.backstage.service;

import com.caitu99.service.backstage.domain.VirtualGoodsInventory;

import java.util.Date;
import java.util.List;

/**
 * Created by liuzs on 2016/2/16.
 */
public interface VirtualGoodsInventoryService {

    List<VirtualGoodsInventory> selectByInventory(Date endTime);
}
