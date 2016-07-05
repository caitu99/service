package com.caitu99.service.backstage.dao;

import com.caitu99.service.backstage.domain.SalesmanPush;

/**
 * Created by liuzs on 2016/3/14.
 */
public interface SalesmanPushMapper {

    //查询登录人员是否为地推业务员
    SalesmanPush selectByPhone(String phone);
}
