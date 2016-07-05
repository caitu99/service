package com.caitu99.service.free.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.free.domain.FreeTrade;

public interface FreeTradeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FreeTrade record);

    int insertSelective(FreeTrade record);

    FreeTrade selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FreeTrade record);

    int updateByPrimaryKey(FreeTrade record);
    
    int selectCount(FreeTrade record);
    
    List<FreeTrade> selectPage(Map<String,Object> map);
}