package com.caitu99.service.backstage.service;

import com.caitu99.service.backstage.domain.StartappRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by chenhl on 2016/2/18.
 */
public interface StartappRecordService {

    int insert(StartappRecord startappRecord);

    List<StartappRecord> selectByUserIdAndTime(Map map);

}
