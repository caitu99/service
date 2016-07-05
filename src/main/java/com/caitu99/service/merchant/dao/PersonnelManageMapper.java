package com.caitu99.service.merchant.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.merchant.domain.PersonnelManage;

public interface PersonnelManageMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(PersonnelManage record);

    PersonnelManage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PersonnelManage record);

    Integer selectPageCount(Map<String, Object> map);
    
    List<PersonnelManage> selectPageList(Map<String, Object> map);
}