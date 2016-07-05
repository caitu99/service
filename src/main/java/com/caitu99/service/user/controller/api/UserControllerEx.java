package com.caitu99.service.user.controller.api;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.sys.sms.SMSNumbeUtil;
import com.caitu99.service.sys.sms.SMSSend;
import com.caitu99.service.transaction.api.IintegralActivator;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.controller.vo.UserCardManualVo;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserMail;
import com.caitu99.service.user.domain.UserThirdInfo;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserMailService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.user.service.UserThirdInfoService;
import com.caitu99.service.utils.Configuration;

@Controller
@RequestMapping("/api/user")
public class UserControllerEx extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UserControllerEx.class);

	@Autowired
	private RedisOperate redis;

	@Autowired
	private UserService userService;

    @Autowired
    private UserMailService userMailService;

    @Autowired
    private UserCardService userCardService;

    @Autowired
    private UserThirdInfoService userThirdInfoService;
    
    @Autowired
    private IintegralActivator activator;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserCardManualService userCardManualService;
    
    @Autowired
    private ConfigService configService;
    
    @RequestMapping(value="/basic/info/1.0")
    @ResponseBody
    public String getUserBasicInfo(Long id) {
        ApiResult<JSONObject> result = new ApiResult<>();

        JSONObject jsonObject = new JSONObject();

        SimplePropertyPreFilter filter_user = new SimplePropertyPreFilter(User.class,
                "id","mobile", "openid", "qq", "weibo", "nick", "wechatNick",
                "qqNick", "weiboNick", "imgurl", "isauth", "isBlank");

        User user = userService.getById(id);
        try{
        	activator.activateUserIntegral(id);
        }catch(Exception ex){
        	logger.error("激活用户财币异常，userId:{},ex{}",id,ex);
        }
        
        //关闭实名认证    mod by chencheng 20160401 暂时关闭实名认证
        user.setIsauth(1);//1,已实名认证
        
        result.setCode(0);
        result.setData(jsonObject);
        String imgUrl = user.getImgurl();
        if(StringUtils.isNotEmpty(imgUrl) && !imgUrl.startsWith("http")) {
            imgUrl = PhotoController.URL + imgUrl;
            user.setImgurl(imgUrl);
        }

        if (StringUtils.isEmpty(user.getImgurl())) {
            UserThirdInfo userThirdInfo = userThirdInfoService.selectByUserId(id);
            userService.setUserImageUrl(user, userThirdInfo);
        }

        List<UserMail> userMails = userMailService.selectByUserId(id);
        Long total = accountService.selectByUserId(id).getAvailableIntegral();

        jsonObject.put("mailsize", userMails.size());
        jsonObject.put("total", total == null ? 0 : total);
        jsonObject.put("paypass", StringUtils.isNotEmpty(user.getPaypass()));
        jsonObject.put("user", user);
        
        //查询用户是否有导入过邮箱或实时查询过
        Integer isImport = 0;
        List<UserCardManualVo> list = userCardManualService.queryIntegralRemoveRepetition(id,1);
        if(null!=list && list.size()>0){
        	isImport = 1;
        }else{
    		list = userCardManualService.queryIntegralRemoveRepetition(id,-1);
    		if(null!=list && list.size()>0){
    			isImport = 1;
    		}else{
    			isImport = 0;
    		}
        }

    	jsonObject.put("isImport", isImport);
    	
    	//判断用户是否是第一次登录,赠送3财币
    	userService.giveNewUserMoney(id);

        logger.debug("get user info: {}, {}", id, JSON.toJSONString(user));
        return JSON.toJSONString(result, new SimplePropertyPreFilter[]{filter_user});
    }

    @RequestMapping(value="/basic/simple/info/1.0")
    @ResponseBody
    public String getUserSimpleInfo(Long id) {
        ApiResult<JSONObject> result = new ApiResult<>();

        JSONObject jsonObject = new JSONObject();

        SimplePropertyPreFilter filter_user = new SimplePropertyPreFilter(User.class,
                "mobile", "nick", "imgurl");

        User user = userService.getById(id);
        result.setCode(0);
        result.setData(jsonObject);
        String imgUrl = user.getImgurl();
        if(StringUtils.isNotEmpty(imgUrl) && !imgUrl.startsWith("http")) {
            imgUrl = PhotoController.URL + imgUrl;
            user.setImgurl(imgUrl);
        }

        if (StringUtils.isEmpty(user.getImgurl())) {
            UserThirdInfo userThirdInfo = userThirdInfoService.selectByUserId(id);
            userService.setUserImageUrl(user, userThirdInfo);
        }

        if (StringUtils.isNotEmpty(user.getMobile())) {
            String mobile = user.getMobile();
            if (mobile.length() > 3) {
                mobile = "****" + mobile.substring(mobile.length() - 4);
                user.setMobile(mobile);
            }
        }

        jsonObject.put("user", user);

        logger.debug("get user info: {}, {}", id, JSON.toJSONString(user));
        return JSON.toJSONString(result, new SimplePropertyPreFilter[]{filter_user});
    }

    /**
     * 重置密码发送验证码
     * @Description: (方法职责详细描述,可空)  
     * @Title: resetPayPasswordSend 
     * @param userid		用户ID
     * @return
     * @date 2016年6月7日 下午3:36:05  
     * @author xiongbin
     */
    @RequestMapping(value="reset/pay/password/send/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String resetPayPasswordSend(Long userid) {
		ApiResult<String> result = new ApiResult<String>();

		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}
		
		User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}
		
		String mobile = user.getMobile();
		if(StringUtils.isBlank(mobile)){
			return result.toJSONString(-1, "您未绑定手机号码,请先绑定手机号码");
		}

		String randomNum = "1234";
		
		if("1".equals(Configuration.getProperty("is.send.sms", "1"))){
			randomNum = SMSNumbeUtil.createRandom(true, 4);
        }
		
		String smsConfig = configService.selectByKey("sms_send_channel").getValue();
		SMSSend.sendSMS(new String[]{mobile},"[财途积分钱包]您正在重置支付密码，验证码为：" + randomNum + "，请注意您的账户安全",smsConfig);
		logger.info("重置密码发送验证码,mobile:{},code:{}",mobile,randomNum);
        redis.set(String.format(RedisKey.SMS_SEND_RESET_PAY_PASSWORD_KEY, mobile), randomNum, 60);
        
		return result.toJSONString(0,"success");
	}
    
    /**
     * 重复密码验证短信验证码
     * @Description: (方法职责详细描述,可空)  
     * @Title: resetPayPasswordVerify 
     * @param userid	用户ID
     * @param code		验证码
     * @return
     * @date 2016年6月7日 下午3:39:48  
     * @author xiongbin
     */
    @RequestMapping(value="reset/pay/password/verify/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String resetPayPasswordVerify(Long userid,String code) {
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();

		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(code)){
			return result.toJSONString(-1, "参数code不能为空");
		}
		
		User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}
		
		String mobile = user.getMobile();
		if(StringUtils.isBlank(mobile)){
			return result.toJSONString(-1, "您未绑定手机号码,请先绑定手机号码");
		}
		
        String redisCode = redis.getStringByKey(String.format(RedisKey.SMS_SEND_RESET_PAY_PASSWORD_KEY, mobile));
        if(StringUtils.isBlank(redisCode)){
			return result.toJSONString(-1, "验证码已过期");
        }else if(!code.equals(redisCode)){
			return result.toJSONString(-1, "验证码错误");
        }
        
		return result.toJSONString(0,"success");
	}
}

