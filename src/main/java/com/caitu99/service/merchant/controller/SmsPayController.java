/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.merchant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.merchant.domain.SmspayItem;
import com.caitu99.service.merchant.domain.SmspayPlatform;
import com.caitu99.service.merchant.domain.SmspayRecord;
import com.caitu99.service.merchant.service.SmsPayService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: SmsPayController 
 * @author fangjunxiao
 * @date 2016年6月21日 下午4:39:07 
 * @Copyright (c) 2015-2020 by caitu99 
 */

@Controller
@RequestMapping("/api/merchant/sms")
public class SmsPayController extends BaseController{

	
	@Autowired
	private SmsPayService smsPayService;
	
	
	/**
	 * 平台列表查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: platfromList 
	 * @param userid
	 * @return
	 * @date 2016年6月23日 上午11:01:25  
	 * @author fangjunxiao
	 */
    @RequestMapping(value = "/list/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String platfromList(Long userid) {
        ApiResult<List<SmspayPlatform>> result = new ApiResult<List<SmspayPlatform>>();
        List<SmspayPlatform> splist = smsPayService.findAll();
        return result.toJSONString(0, "Success", splist);
    }

    /**
     * 	商品列表查询
     * @Description: (方法职责详细描述,可空)  
     * @Title: platfromItem 
     * @param userid
     * @param platformId
     * @return
     * @date 2016年6月23日 上午11:01:38  
     * @author fangjunxiao
     */
    @RequestMapping(value = "/item/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String platfromItem(Long userid,Long platformId) {
        ApiResult<List<SmspayItem>> result = new ApiResult<List<SmspayItem>>();
        List<SmspayItem> splist = smsPayService.findAllByPlatformId(platformId);
        return result.toJSONString(0, "Success", splist);
    }

    
    /**
     * 短信支付
     * @Description: (方法职责详细描述,可空)  
     * @Title: pay 
     * @param userid
     * @param platformId
     * @param mobile
     * @param integral
     * @param itemId
     * @param name
     * @param code
     * @param password
     * @return
     * @date 2016年6月23日 上午11:01:48  
     * @author fangjunxiao
     */
    @RequestMapping(value = "/pay/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String pay(Long userid,Long platformId,String mobile,Long integral,
    		Long itemId,String name,String code,String password) {
        SmspayRecord sr = new SmspayRecord();
        sr.setCode(code);
        sr.setPlatformId(platformId);
        sr.setUserId(userid);
        sr.setMobile(mobile);
        sr.setIntegral(integral);
        sr.setSmspayItemId(itemId);
        sr.setName(name);
        sr.setPassword(password);
        return smsPayService.saveSmsPayResult(sr);
    }
}
