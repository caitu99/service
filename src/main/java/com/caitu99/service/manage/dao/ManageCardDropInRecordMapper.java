package com.caitu99.service.manage.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.manage.domain.ManageCardDropInRecord;

public interface ManageCardDropInRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ManageCardDropInRecord record);

    int insertSelective(ManageCardDropInRecord record);

    ManageCardDropInRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ManageCardDropInRecord record);

    int updateByPrimaryKey(ManageCardDropInRecord record);
    
    Integer selectPageCount(Map<String,Object> map);
    
    List<ManageCardDropInRecord> selectPageList(Map<String,Object> map);
}