package com.caitu99.service.sys.dao;

import java.util.List;

import com.caitu99.service.sys.domain.FuncModel;

public interface FuncModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FuncModel record);

    int insertSelective(FuncModel record);

    FuncModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FuncModel record);

    int updateByPrimaryKey(FuncModel record);

	List<FuncModel> selectByModel(Integer modelId);
}