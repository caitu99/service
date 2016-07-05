package com.caitu99.service.backstage.controller;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.backstage.domain.SalesmanPush;
import com.caitu99.service.backstage.service.SalesmanPushService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by liuzs on 2016/3/14.
 */

@Controller
@RequestMapping("/api/backstage/salesmanLogin")
public class SalesmanLoginController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(SalesmanLoginController.class);

    @Autowired
    private SalesmanPushService salesmanPushService;
    
    
    

    @RequestMapping(value = "/login/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String SalesmanLogin(String phone){
        //登录人员手机号
        ApiResult<SalesmanPush> result = new ApiResult<>();
        SalesmanPush salesmanPush = salesmanPushService.selectByPhone(phone);
        
        Integer m = salesmanPushService.countIsManager(phone);
        
        if(null == salesmanPush){//非业务人员登录
            result.setCode(1);
            result.setMessage("您没有参加此项业务");
            return JSON.toJSONString(result);
        }
        
        if(null == m || 0 == m.intValue()){
        	 result.setCode(0);
        }else{
        	 result.setCode(2);
        }

        result.setData(salesmanPush);
        return JSON.toJSONString(result);
    }
}
