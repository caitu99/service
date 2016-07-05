package com.caitu99.service.realization.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.realization.domain.RealizePlatform;

public interface RealizePlatformMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(RealizePlatform record);

    int insertSelective(RealizePlatform record);

    RealizePlatform selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RealizePlatform record);

    int updateByPrimaryKey(RealizePlatform record);
    
    List<RealizePlatform> selectPageList(Map<String,String> map);

	List<RealizePlatform> selectBySupport(Map<String, String> map);
}