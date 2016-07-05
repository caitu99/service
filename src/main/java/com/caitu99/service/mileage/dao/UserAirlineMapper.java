package com.caitu99.service.mileage.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.mileage.domain.UserAirline;

public interface UserAirlineMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserAirline record);

    int insertSelective(UserAirline record);

    UserAirline selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserAirline record);

    int updateByPrimaryKey(UserAirline record);

	List<UserAirline> selectByUser(Map userid);

	void deleteByUser(Map<String, Long> userAirMap);
}