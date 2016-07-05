package com.caitu99.service.merchant.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import com.caitu99.service.merchant.domain.ProxyRelation;

public interface ProxyRelationMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(ProxyRelation record);

    ProxyRelation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProxyRelation record);

    ProxyRelation selectByEmpUserId(Long empUserId);

    ProxyRelation selectByUserIDAndEmpUserId(@Param("userId") Long userId, @Param("empUserId")Long empUserId);
    

	ProxyRelation selectRelationBy(Map<String, Object> queryParam);

	ProxyRelation selectMyLoad(Long empUserId);

	List<ProxyRelation> getMyUnderling(Long userId);
}