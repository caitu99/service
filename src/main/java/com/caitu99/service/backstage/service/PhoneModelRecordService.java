package com.caitu99.service.backstage.service;

import com.caitu99.service.backstage.domain.PhoneModelRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by chenhl on 2016/2/18.
 */
public interface PhoneModelRecordService {

    int insert(PhoneModelRecord phoneModelRecord);

    List<PhoneModelRecord> selectByUID(Long userId);

    List<PhoneModelRecord> selectByUIDAndTime(Map map);
}
