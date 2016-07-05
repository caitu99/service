package com.caitu99.service.backstage.service.impl;

import com.caitu99.service.backstage.dao.PhoneModelRecordMapper;
import com.caitu99.service.backstage.domain.PhoneModelRecord;
import com.caitu99.service.backstage.service.PhoneModelRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by chenhl on 2016/2/18.
 */
@Service
public class PhoneModelRecordServiceImpl implements PhoneModelRecordService{

    @Autowired
    private PhoneModelRecordMapper phoneModelRecordMapper;

    @Override
    public int insert(PhoneModelRecord phoneModelRecord) {
        return phoneModelRecordMapper.insert(phoneModelRecord);
    }

    @Override
    public List<PhoneModelRecord> selectByUID(Long userId) {
        return phoneModelRecordMapper.selectByUID(userId);
    }

    @Override
    public List<PhoneModelRecord> selectByUIDAndTime(Map map) {
        return phoneModelRecordMapper.selectByUIDAndTime(map);
    }


}
