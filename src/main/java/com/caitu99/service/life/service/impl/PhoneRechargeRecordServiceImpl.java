package com.caitu99.service.life.service.impl;

import java.util.List;

import com.caitu99.service.life.dao.PhoneRechargeRecordMapper;
import com.caitu99.service.life.domain.PhoneRechargeRecord;
import com.caitu99.service.life.service.PhoneRechargeRecordService;
import com.caitu99.service.sys.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneRechargeRecordServiceImpl implements PhoneRechargeRecordService {
    @Autowired
    private PhoneRechargeRecordMapper phoneRechargeRecordMapper;

    @Override
    public int saveRecord(PhoneRechargeRecord phoneRechargeRecord) {
        return phoneRechargeRecordMapper.insert(phoneRechargeRecord);
    }

    @Override
    public List<PhoneRechargeRecord> listAll(Page page) {
        return phoneRechargeRecordMapper.listAll(page);
    }

    @Override
    public int countNum() {
        return phoneRechargeRecordMapper.countNum();
    }
}
