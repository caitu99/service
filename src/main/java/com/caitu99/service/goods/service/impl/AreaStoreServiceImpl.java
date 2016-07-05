package com.caitu99.service.goods.service.impl;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.dao.AreaStoreMapper;
import com.caitu99.service.goods.domain.AreaStore;
import com.caitu99.service.goods.service.AreaStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lion on 2015/12/7 0007.
 */

@Service
public class AreaStoreServiceImpl implements AreaStoreService{
    private final static Logger logger = LoggerFactory.getLogger(AreaStoreServiceImpl.class);
    @Autowired
    private AreaStoreMapper areaStoreMapper;

    @Override
    public List<AreaStore> selectByBrandId(Long brandId) {
        return areaStoreMapper.selectByBrandId(brandId);
    }

    @Override
    public Pagination<AreaStore> selectByBrandIdPageList(Long brandId,  Pagination<AreaStore> pagination) {

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("brandId", brandId);
            map.put("start", pagination.getStart());
            map.put("pageSize", pagination.getPageSize());

            List<AreaStore> list = areaStoreMapper.selectByBrandIdPageList(map);
            Integer cnt = areaStoreMapper.selectCntByBrandIdPageList(map);
            pagination.setDatas(list);
            pagination.setTotalRow(cnt);
            return  pagination;
        } catch (Exception e) {
            logger.error("查询品牌范围出错"+e.getMessage(),e);
            return pagination;
        }

    }
}
