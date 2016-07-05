package com.caitu99.service.integral.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.domain.UserCardManualItem;

public interface UserCardManualItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserCardManualItem record);

    int insertSelective(UserCardManualItem record);

    UserCardManualItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCardManualItem record);

    int updateByPrimaryKey(UserCardManualItem record);
    
    Integer selectPageCount(Map<String,Object> map);
    
    List<CardIntegralLastTime> selectLastTimePageList(Map<String,Object> map);
    
    UserCardManualItem selectByUserCardManualId(Long userCardManualId);
    
    void deleteByUserCardManualId(Long userCardManualId);
	
    UserCardManualItem selectFirstTimeByCardId(Map<String,Object> map);
}