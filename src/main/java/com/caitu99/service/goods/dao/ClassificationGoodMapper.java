package com.caitu99.service.goods.dao;

import com.caitu99.service.goods.domain.Item;

import java.util.List;
import java.util.Map;

/**
 * Created by chenhl on 2015/12/31.
 */
public interface ClassificationGoodMapper {

    List<Item> selectPageList(Map<String, Object> map);

    Integer selectPageCount(Map<String, Object> map);

}
