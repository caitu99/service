package com.caitu99.service.right.dao;

import java.util.List;

import com.caitu99.service.right.domain.MyRights;

public interface MyRightsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MyRights record);

    int insertSelective(MyRights record);

    MyRights selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MyRights record);

    int updateByPrimaryKey(MyRights record);

	List<MyRights> selectMyRights(Long userid);
}