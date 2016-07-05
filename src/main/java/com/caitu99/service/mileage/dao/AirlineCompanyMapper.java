package com.caitu99.service.mileage.dao;

import java.util.List;

import com.caitu99.service.mileage.domain.AirlineCompany;

public interface AirlineCompanyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AirlineCompany record);

    int insertSelective(AirlineCompany record);

    AirlineCompany selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AirlineCompany record);

    int updateByPrimaryKey(AirlineCompany record);

	List<AirlineCompany> selectList(Long userid);
}