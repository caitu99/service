package com.caitu99.service.backstage.dao;

import com.caitu99.service.backstage.domain.GPSIpRecord;

import java.util.List;
import java.util.Map;

public interface GPSIpRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GPSIpRecord record);

    int insertSelective(GPSIpRecord record);

    GPSIpRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GPSIpRecord record);

    int updateByPrimaryKey(GPSIpRecord record);

    int selectCount(Long userId);

    List<GPSIpRecord> selectByUserIdAndTime(Map map);
}