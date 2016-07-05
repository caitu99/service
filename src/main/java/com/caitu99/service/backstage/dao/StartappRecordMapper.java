package com.caitu99.service.backstage.dao;

import com.caitu99.service.backstage.domain.StartappRecord;

import java.util.List;
import java.util.Map;

public interface StartappRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StartappRecord record);

    int insertSelective(StartappRecord record);

    StartappRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StartappRecord record);

    int updateByPrimaryKey(StartappRecord record);

    List<StartappRecord> selectByUserIdAndTime(Map map);
}