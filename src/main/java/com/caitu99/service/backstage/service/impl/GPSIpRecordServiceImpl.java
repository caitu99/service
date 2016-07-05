package com.caitu99.service.backstage.service.impl;

import com.caitu99.service.backstage.dao.GPSIpRecordMapper;
import com.caitu99.service.backstage.domain.GPSIpRecord;
import com.caitu99.service.backstage.service.GPSIpRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by chenhl on 2016/2/19.
 */
@Service
public class GPSIpRecordServiceImpl implements GPSIpRecordService {

    @Autowired
    private GPSIpRecordMapper gpsIpRecordMapper;

    @Override
    public int insert(GPSIpRecord record) {
        return gpsIpRecordMapper.insert(record);
    }

    @Override
    public int selectCount(Long userId) {
        return gpsIpRecordMapper.selectCount(userId);
    }

    @Override
    public List<GPSIpRecord> selectByUserIdAndTime(Map map) {
        return gpsIpRecordMapper.selectByUserIdAndTime(map);
    }
}
