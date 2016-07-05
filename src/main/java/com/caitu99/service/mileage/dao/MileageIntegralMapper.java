package com.caitu99.service.mileage.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.mileage.domain.MileageIntegral;

public interface MileageIntegralMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MileageIntegral record);

    int insertSelective(MileageIntegral record);

    MileageIntegral selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MileageIntegral record);

    int updateByPrimaryKey(MileageIntegral record);

	List<MileageIntegral> selectListBy(Map<String, Long> params);
}