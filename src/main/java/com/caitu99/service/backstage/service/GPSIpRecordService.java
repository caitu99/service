package com.caitu99.service.backstage.service;

import com.caitu99.service.backstage.domain.GPSIpRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by chenhl on 2016/2/19.
 */
public interface GPSIpRecordService {

    int insert(GPSIpRecord record);

    int selectCount(Long userId);  //查看今天有几条记录

    List<GPSIpRecord> selectByUserIdAndTime(Map map);

}
