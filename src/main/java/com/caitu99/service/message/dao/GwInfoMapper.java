package com.caitu99.service.message.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.message.domain.GwInfo;

public interface GwInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GwInfo record);

    int insertSelective(GwInfo record);

    GwInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GwInfo record);

    int updateByPrimaryKey(GwInfo record);

	Integer selectPageCount(Map<String, Object> map);

	List<GwInfo> selectPageList(Map<String, Object> map);
}