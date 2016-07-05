package com.caitu99.service.sdk.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;

@Controller
@RequestMapping("/api/sdk/")
public class SdkPayController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(SdkPayController.class);

    @Autowired
    private ConfigService configService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperate redis;
    @Autowired
    private AccountService accountService;
    
    /**
     * 获取用户途币
     * @Description: (方法职责详细描述,可空)  
     * @Title: userInfo 
     * @param userid		企业用户ID
     * @param mobile		用户手机号码
     * @return
     * @date 2016年5月31日 下午4:40:54  
     * @author xiongbin
     */
	@RequestMapping(value="user/info/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String userInfo(Long userid,String mobile) {
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		
		if(StringUtils.isBlank(mobile)){
			return result.toJSONString(-1, "参数mobile不能为空");
		}
		
		User user = userService.register(mobile);
		if(null == user){
			return result.toJSONString(-1, "系统暂时出现异常,请稍后再试");
		}
		
		boolean isPay = false;
		if(StringUtils.isNotBlank(user.getPaypass())){
			isPay = true;
		}
		
		Account account = accountService.selectByUserId(user.getId());
		
		JSONObject json = new JSONObject();
		json.put("isPay", isPay);
		json.put("tubi", account.getTubi());
        
		return result.toJSONString(0,"success",json);
	}
	
	/**
	 * 支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userid		企业用户ID
	 * @param mobile		用户手机号码
	 * @param payPassword	支付密码
	 * @param payTubi		途币
	 * @return
	 * @date 2016年5月31日 下午5:57:11  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String pay(Long userid,String mobile,String payPassword,Long payTubi) {
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();

		if(StringUtils.isBlank(mobile)){
			return result.toJSONString(-1, "参数mobile不能为空");
		}else if(StringUtils.isBlank(payPassword)){
			return result.toJSONString(-1, "参数payPassword不能为空");
		}else if(null == payTubi){
			return result.toJSONString(-1, "参数payTubi不能为空");
		}else if(!StringUtils.isNumeric(payPassword)){
			return result.toJSONString(-1, "参数payPassword必须纯数字");
		}
		
		User user = new User();
		user.setMobile(mobile);
		user = userService.isExistMobile(user);
		if(null == user){
			return result.toJSONString(-1, "系统暂时出现异常,请稍后再试");
		}
		
		boolean isPay = false;
		if(StringUtils.isNotBlank(user.getPaypass())){
			isPay = true;
		}

		try {
			payPassword = AESCryptoUtil.encrypt(payPassword);
		} catch (CryptoException e) {
			logger.error("加密支付密码出错,payPassword:" + payPassword,e);
			return result.toJSONString(-1, "系统暂时出现异常,请稍后再试");
		}
		
		if(isPay){
			if(!payPassword.equals(user.getPaypass())){
				return result.toJSONString(-1, "支付密码错误");
			}
		}else{
			user.setPaypass(payPassword);
		}
		
		Account account = accountService.selectByUserId(user.getId());
		Long tubi = account.getTubi();
		
		if(tubi.equals(0L)){
			return result.toJSONString(-2, "您的途币数量为0，无法支付");
		}else if(tubi.intValue() < payTubi.intValue()){
			return result.toJSONString(-3, "您的途币数量不足，无法支付");
		}else if(tubi.intValue() < payTubi.intValue() + 1000){
			return result.toJSONString(-4, "支付后，账户剩余途币不足1000");
		}
		
		accountService.sdkPay(userid, user, account, payTubi, payPassword, isPay);
        
		return result.toJSONString(0,"success");
	}
}
