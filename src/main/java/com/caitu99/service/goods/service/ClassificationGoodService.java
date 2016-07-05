package com.caitu99.service.goods.service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.domain.Type;

import java.util.List;

/**
 * Created by chenhl on 2015/12/31.
 */
public interface ClassificationGoodService {

    List<Type> selectTypeByUseType( int useType );

    Pagination<Item> findByTypeId( Long typeId , Pagination<Item> pagination, String[] typeId2, String[] source);

}
