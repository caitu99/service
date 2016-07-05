package com.caitu99.service.backstage.dao;

import com.caitu99.service.backstage.domain.PhoneModelRecord;

import java.util.List;
import java.util.Map;

public interface PhoneModelRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PhoneModelRecord record);

    int insertSelective(PhoneModelRecord record);

    PhoneModelRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PhoneModelRecord record);

    int updateByPrimaryKey(PhoneModelRecord record);

    List<PhoneModelRecord> selectByUID(Long userId);

    List<PhoneModelRecord> selectByUIDAndTime(Map map);
}