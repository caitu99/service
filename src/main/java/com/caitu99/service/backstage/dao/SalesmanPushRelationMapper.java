package com.caitu99.service.backstage.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.backstage.domain.SalesmanPushRelation;
import com.caitu99.service.backstage.dto.SalesmanDto;

public interface SalesmanPushRelationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SalesmanPushRelation record);

    int insertSelective(SalesmanPushRelation record);

    SalesmanPushRelation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SalesmanPushRelation record);

    int updateByPrimaryKey(SalesmanPushRelation record);
    
    SalesmanDto selectByStall(Map<String,String> map);//地推业务员数据
    
	List<SalesmanDto> findByStall(Map<String,String> map);
	
	int countIsManager(String mobile);
}
