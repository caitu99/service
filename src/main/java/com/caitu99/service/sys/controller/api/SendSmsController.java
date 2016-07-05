package com.caitu99.service.sys.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.sys.sms.SMSNumbeUtil;
import com.caitu99.service.sys.sms.SMSSend;
import com.caitu99.service.utils.Configuration;


@Controller
@RequestMapping("/api/sys")
public class SendSmsController  extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SendSmsController.class);

    @Autowired
    private RedisOperate redis;

    @Autowired
    private AppConfig appConfig;
    
    @Autowired
    private ConfigService configService;

    @RequestMapping(value="/sendsms/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String sendSms(HttpServletRequest request) {
        ApiResult<String> result = new ApiResult<>();

        String mobile = request.getParameter("mobile");
        if (StringUtils.isEmpty(mobile)) {
            result.setCode(2006);
            result.setMessage("用户手机不能为空");
            return JSON.toJSONString(result);
        }
        //0是不发送
        if("0".equals(Configuration.getProperty("is.send.sms", "1"))) {//基于配置是否发送短信验证码，验证码1234
            result.setCode(0);
            result.setData("1234");
            redis.set(String.format(RedisKey.SMS_SEND_KEY, mobile), "1234", 10 * 60);
            result.setMessage("验证码发送成功");
        }
        else{
            try {
//                String value = redis.getStringByKey(String.format(RedisKey.SMS_SEND_KEY, mobile));
//                if (StringUtils.isNotEmpty(value)) {
//                    result.setCode(0);
////                    redis.set(String.format(RedisKey.SMS_SEND_KEY, mobile), value, 15 * 60);
//                } else {
                    // 调用短信发送接口
                    String randomNum = SMSNumbeUtil.createRandom(true, 4);
//                    SingletonClient.getClient().sendSMS(new String[]{mobile},
//                            "短信验证码为：" + randomNum + ",此验证码只为本人使用切勿告知他人，请在页面中输入以完成验证", "", 5);// 带扩展码
                    String smsConfig = configService.selectByKey("sms_send_channel").getValue();
                    SMSSend.sendSMS(new String[]{mobile},
                            "短信验证码为：" + randomNum + ",此验证码只为本人使用切勿告知他人，请在页面中输入以完成验证",smsConfig);
                    // store vcode to redis and the vcode will expire in 15 minutes
                    redis.set(String.format(RedisKey.SMS_SEND_KEY, mobile), randomNum, 15 * 60);
//                }

                logger.info("message send success: {}", mobile);
                result.setCode(0);
                result.setMessage("验证码发送成功");
            } catch (Exception e) {
                logger.error("send message error: {}", mobile, e);
                result.setCode(2007);
                result.setMessage("短信发送失败");
            }
        }
        return result.toString();
    }

}
