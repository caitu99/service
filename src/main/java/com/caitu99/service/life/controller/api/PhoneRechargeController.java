/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life.controller.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.ApiClosedException;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.life.controller.vo.RechargeResult;
import com.caitu99.service.life.service.PhoneRechargeRecordService;
import com.caitu99.service.life.service.PhoneRechargeService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;

/**
 * 话费充值控制层
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PhoneRechargeController 
 * @author lawrence
 * @date 2015年11月2日 下午3:22:38 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/life/recharge")
public class PhoneRechargeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PhoneRechargeController.class);

    @Autowired
    private PhoneRechargeService phoneRechargeService;
    @Autowired
    private PhoneRechargeRecordService phoneRechargeRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private AccountService accountService;
    
    /**
     * 话费充值
     * @Description: (方法职责详细描述,可空)  
     * @Title: phoneRecharge 
     * @param userId 用户ID
     * @param mobile 手机号
     * @param cardNum 充值金额
     * @param payPass 密码
     * @return Json
     * @throws CryptoException
     * @date 2015年11月2日 下午3:24:56  
     * @author lawrence
     */
    @RequestMapping(value = "/phone/1.0")
    @ResponseBody
    @Deprecated
    public String phoneRecharge(String userId,String mobile,
    		String cardNum,String payPass) throws CryptoException {
      	//开关
    	String swith = SpringContext.getBean(AppConfig.class).rechargePhoneSwitch;
    	if (StringUtils.isBlank(swith) || !"1".equals(swith.trim())) {
    		logger.error("话费充值接口已关闭");
    		throw new ApiClosedException(-1,"话费充值接口已关闭");
    	}
    	//获取用户信息
        User user = userService.selectByPrimaryKey(Long.parseLong(userId));
        if(null ==  user){
        	logger.error("用户不存在");
        	throw new UserNotFoundException(2037,"用户不存在");
        }
        //验证密码
        ApiResult<Object> apiResult = new ApiResult<Object>();
        if (!AESCryptoUtil.decrypt(user.getPaypass()).equals(payPass)) {
        	return apiResult.toJSONString(2201, "支付密码错误");
		}
        
        //验证用户财币
        Integer scale = appConfig.phoneRechargeScale;
        BigDecimal needCardNum  = new BigDecimal(cardNum).multiply(new BigDecimal(100))
        						.multiply(new BigDecimal(scale).divide(new BigDecimal(100)));
        
        Account account = accountService.selectByUserId(Long.parseLong(userId));
        if(null == account){
        	return apiResult.toJSONString(2202,"用户财币不足");
        }
        
        Long availableIntegral = account.getAvailableIntegral();
        if (null == availableIntegral || new BigDecimal(availableIntegral).compareTo(needCardNum) == -1) {
        	return apiResult.toJSONString(2202,"用户财币不足");
        }

//      if (null == user.getIntegral() || new BigDecimal(user.getIntegral()).compareTo(needCardNum) == -1) {
//          return apiResult.toJSONString(2202,"用户财币不足");
//      }
        
        //验证并充值
        RechargeResult rechargeResult = phoneRechargeService.checkAndRecharge(userId, mobile, cardNum);
        if (rechargeResult.isSuccess()) {
            apiResult.set(0, "充值成功");
            logger.info("用户 {} ,充值 手机{} {} 成功.", userId, mobile, cardNum);
            return apiResult.toJSONString(0,  "充值成功");
        } 
        logger.warn("用户 ：{} ,充值手机号：{} 金额： {} 失败.", userId, mobile, cardNum);
        
        return apiResult.toJSONString(2203, rechargeResult.getResult());
    }


	/**
     * 获取手机话费充值列表
     * @Description: (方法职责详细描述,可空)  
     * @Title: getMoneyList 
     * @return String 
     * @date 2015年11月2日 下午3:19:05  
     * @author lawrence
     */
    @RequestMapping(value = "/phone/list/1.0")
    @ResponseBody
    public String getMoneyList() {
    	
    	String data =  "10_20_30_50_100_200_300_500";
        return new ApiResult<String>().toJSONString(0, "获取话费充值列表成功！", data);
    }
    
	/**
     * 获取手机话费充值列表
     * @Description: (方法职责详细描述,可空)  
     * @Title: getMoneyList 
     * @return String 
     * @date 2015年11月2日 下午3:19:05  
     * @author lawrence
     */
    @RequestMapping(value = "/phone/list/2.0")
    @ResponseBody
    public String getMoneyList2() {
    	String data =  "10_20_30_50_100_200_300_500";
    	Map<String,String> map = new HashMap<String,String>();
    	map.put("scale",appConfig.phoneRechargeScale.toString());
    	map.put("charge",data);
        return new ApiResult<Map<String,String>>().toJSONString(0, "获取话费充值列表成功！", map);
    }
}
