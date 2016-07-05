package com.caitu99.service.backstage.service;

import com.caitu99.service.backstage.domain.SalesmanPush;

/**
 * Created by liuzs on 2016/3/14.
 */
public interface SalesmanPushService {

    SalesmanPush selectByPhone (String phone);//查询是否为地推业务员
    
    
    Integer countIsManager(String phone);
    
    
    
}
