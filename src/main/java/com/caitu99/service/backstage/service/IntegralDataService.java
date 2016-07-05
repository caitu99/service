package com.caitu99.service.backstage.service;

import com.caitu99.service.backstage.domain.*;

import java.util.Date;
import java.util.List;

/**
 * Created by chenhl on 2016/2/15.
 */
public interface IntegralDataService {

    IntegralData selectByTime(Date startTime , Date endTime);

    List<IntegralIncreaseInfo> selectByInIntegral(Long userId,Date startTime ,Date endTime);//用户入分记录查询

    List<IntegralReduceInfo> selectByOutIntegral(Long userId,Date startTime ,Date endTime);//用户出分记录查询

    List<IntegralChangeInfo> selectByTotalChange(Long userId,Date startTime ,Date endTime);//用户财分总量变更记录

    List<IntegralAccountInfo> selectByIntegralAccount(Long userId,Date startTime ,Date endTime);//用户积分管理账户记录
}
