package com.caitu99.service.goods.service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.domain.AreaStore;

import java.util.List;

/**
 * Created by Lion on 2015/12/7 0007.
 */
public interface AreaStoreService {

    List<AreaStore> selectByBrandId(Long brandId);

    Pagination<AreaStore> selectByBrandIdPageList(Long brandId,  Pagination<AreaStore> pagination);
}
