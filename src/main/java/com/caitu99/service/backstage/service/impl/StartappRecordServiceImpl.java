package com.caitu99.service.backstage.service.impl;

import com.caitu99.service.backstage.dao.StartappRecordMapper;
import com.caitu99.service.backstage.domain.StartappRecord;
import com.caitu99.service.backstage.service.StartappRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by chenhl on 2016/2/18.
 */
@Service
public class StartappRecordServiceImpl implements StartappRecordService{

    @Autowired
    private StartappRecordMapper startappRecordMapper;

    @Override
    public int insert(StartappRecord startappRecord) {
        return startappRecordMapper.insert(startappRecord);
    }

    @Override
    public List<StartappRecord> selectByUserIdAndTime(Map map) {
        return startappRecordMapper.selectByUserIdAndTime(map);
    }
}
