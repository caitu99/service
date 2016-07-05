package com.caitu99.service.sys.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.sys.domain.Banner;

public interface BannerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Banner record);

    int insertSelective(Banner record);

    Banner selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Banner record);

    int updateByPrimaryKey(Banner record);
    
    List<Banner> findRotaryImg(Banner record);
    
    
    Integer selectPageCount(Map<String, Object> map);
    
    List<Banner> selectPageList(Map<String, Object> map);
     
}