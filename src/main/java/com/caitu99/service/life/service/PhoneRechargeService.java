/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life.service;

import com.caitu99.service.life.controller.vo.RechargeResult;
/**
 * 手机话费充值接口
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PhoneRechargeService 
 * @author lawrence
 * @date 2015年11月2日 下午5:26:26 
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface PhoneRechargeService {
	
    /**
     * 检测并充值手机话费
     * @Description: (方法职责详细描述,可空)  
     * @Title: checkAndRecharge 
     * @param userId
     * @param mobile 
     * @param cardNum
     * @return
     * @date 2015年11月2日 下午4:22:51  
     * @author lawrence
     */
	RechargeResult checkAndRecharge(String userId, String mobile,String cardNum);

}
