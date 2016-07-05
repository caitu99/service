package com.caitu99.service.integral.controller.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenhl on 2016/3/8.
 */
@Component
public class ManualQueryCOMM extends ManualQueryAbstract {

    private final static Logger logger = LoggerFactory.getLogger(ManualQueryCOMM.class);

    @Override
    public String login(Long userId, String loginAccount, String password, String imageCode) {
        return null;
    }

    @Override
    public String login(Long userId, String loginAccount, String password) {
        String login_result = null;
        String login_url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/ishop/com/login/1.0";
        String query_integral_url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/ishop/com/integral/1.0";
//        String login_url = "http://127.0.0.1:8092/api/ishop/com/login/1.0";
//        String query_integral_url = "http://127.0.0.1:8092/api/ishop/com/integral/1.0";
        super.setName("交通银行");
        Map<String,String> param = new HashMap<String,String>();
        param.put("userid", userId.toString());
        param.put("account", loginAccount);
        param.put("password", password);

        try {
            login_result = HttpClientUtils.getInstances().doPost(login_url, "UTF-8", param);
            JSONObject json = JSON.parseObject(login_result);
            Integer code = json.getInteger("code");
            if(code != 0){
                if(code.equals(2113)){
                    return ApiResult.outSucceed(ApiResultCode.COMM_ACCOUNT_PASSWORD_ERROR, "登录账号密码不匹配");
                }else if(code.equals(2110)){
                    return ApiResult.outSucceed(ApiResultCode.COMM_GET_INTEGRAL_ERROR, "解析积分失败");
                }else if(code.equals(2107)){
                    return ApiResult.outSucceed(ApiResultCode.COMM_LOGIN_ERROR, "登录失败");
                }else if( code.equals(2115)){
                    return ApiResult.outSucceed(ApiResultCode.COMM_ACCOUNT_PASSWORD_LOCKED, "卡片查询密码已被锁定");
                }else if( code.equals(2116)){
                    return ApiResult.outSucceed(ApiResultCode.COMM_ACCOUNT_PASSWORD_LIMITTED, "登录密码输入已超过限制，请24小时后再试");
                }
            }
        }catch (Exception e){
            logger.error("登录交通银行平台失败:" + e.getMessage(),e);
        }

        return super.login(userId, query_integral_url, param, ApiResultCode.SUCCEED);
    }

    @Override
    public String save(Long userId, Long manualId, String loginAccount, String password, String result, String version){
        try {
            JSONObject resultJson = new JSONObject();

            boolean flag = JsonResult.checkResult(result, ApiResultCode.COMM_INTEGRAL);
            if(!flag){
                return result;
            }

            JSONObject json = JSON.parseObject(result);
            String jsonString = json.getString("data");

            AssertUtil.hasLength(jsonString, "查询交通银行积分失败");

            JSONObject jsonObject = JSON.parseObject(jsonString);

            Integer integral = jsonObject.getInteger("integral");
            String cardNo = jsonObject.getString("account");
            String name = jsonObject.getString("name");
            cardNo = cardNo.substring(cardNo.length() - 4,cardNo.length());

            Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
            /**
             * 记录用户积分数据
             */
            UserCardManual COMMManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.COMM_INTEGRAL,cardNo,name,loginAccount);
            if(null == COMMManual){
            	presentTubiService.presentTubiDo(userId, UserCardManual.COMM_INTEGRAL, 0, integral, resData, version);
            	
                COMMManual = new UserCardManual();
                COMMManual.setIntegral(integral);
                COMMManual.setUserName(name);
                COMMManual.setCardNo(cardNo);
                COMMManual.setGmtModify(now);
                COMMManual.setGmtCreate(now);
                COMMManual.setUserId(userId);
                COMMManual.setLoginAccount(loginAccount);
                COMMManual.setCardTypeId(UserCardManual.COMM_INTEGRAL);
            }else{
            	presentTubiService.presentTubiDo(userId, UserCardManual.COMM_INTEGRAL, COMMManual.getIntegral(), integral, resData,version);
            	
                COMMManual.setIntegral(integral);
                COMMManual.setUserName(name);
                COMMManual.setCardNo(cardNo);
                COMMManual.setLoginAccount(loginAccount);
                COMMManual.setGmtModify(now);
                COMMManual.setStatus(1);
            }
            userCardManualService.insertORupdate(COMMManual);

            /**
             * 记录用户登录数据
             */
            ManualLogin manualLogin = new ManualLogin();
            manualLogin.setLoginAccount(loginAccount);
            manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			manualLogin.setPassword(password);
            manualLogin.setUserId(userId);
            manualLogin.setManualId(manualId);
            manualLogin.setType(ManualLogin.TYPE_CARD_NO);

            ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
            if(oldManualLogin == null){
                manualLogin.setPassword(password);
                manualLoginService.insert(manualLogin);
            }else{
                oldManualLogin.setPassword(password);
                oldManualLogin.setStatus(1);
                manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
            }

            delUserRecordRedisKey(userId, manualId);

            resultJson.put("code", 0);
            resultJson.put("message", "查询交通银行积分成功");
            resultJson.put("data", resData);

            return resultJson.toJSONString();
        } catch (Exception e) {
            logger.error("查询交通银行积分失败:" + e.getMessage(),e);
            throw new ManualQueryAdaptorException(ApiResultCode.COMM_INTEGRAL_ERROR,e.getMessage());
        }
    }
}
