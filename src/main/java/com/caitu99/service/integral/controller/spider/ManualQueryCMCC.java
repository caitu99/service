package com.caitu99.service.integral.controller.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.caitu99.service.exception.ApiException;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

@Component
public class ManualQueryCMCC extends ManualQueryAbstract {

    private final static Logger logger = LoggerFactory.getLogger(ManualQueryCMCC.class);

    @Override
    public String getImageCode(Long userid) {
        String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/yidong/img/1.0";
//		String url = "http://127.0.0.1:8092/api/yidong/img/1.0";
        super.setUrl(url);
        super.setName("移动");
        super.setSucceedCode(ApiResultCode.SUCCEED);
        super.setFailureCode(ApiResultCode.CMCC_GET_IMAGECODE_ERROR);

        return super.getImageCode(userid);
    }

    /**
     * 登录
     *
     * @param userid    用户ID
     * @param imageCode 图片验证码
     * @return
     * @Description: (方法职责详细描述, 可空)
     * @Title: checkJingDongImageCode
     * @date 2015年11月16日 下午6:43:14
     * @author xiongbin
     */
    @Override
    public String login(Long userid, String phone, String password, String imageCode) {
        try {
            AssertUtil.notNull(userid, "用户ID不能为空");
            AssertUtil.hasLength(password, "密码不能为空");
            AssertUtil.hasLength(imageCode, "图片验证码不能为空");

            String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/yidong/verify/1.0";
//			String url = "http://127.0.0.1:8092/api/yidong/verify/1.0";

            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("userid", userid.toString());
            paramMap.put("password", password);
            paramMap.put("vcode", imageCode);
            paramMap.put("passwordtype", "service");
            paramMap.put("account", phone);

            String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);

            logger.info("登录移动返回数据:" + jsonString);

            Boolean flag = JsonResult.checkResult(jsonString, ApiResultCode.SUCCEED);
            if (!flag) {
                JSONObject json = JSON.parseObject(jsonString);
                Integer code = json.getInteger("code");

                if (code.equals(1075)) {
                    return ApiResult.outSucceed(ApiResultCode.CMCC_SMS_ERROR, "短信验证码错误，请返回并在一分钟后重新获取验证码");
                } else if (code.equals(1055)) {
                    return ApiResult.outSucceed(ApiResultCode.CMCC_NOT_CMCC_USER, "非移动用户,无法查询");
                } else if (code.equals(1053)) {
                    return ApiResult.outSucceed(ApiResultCode.CMCC_LOGIN_ERROR, "登录移动失败");
                } else if (code.equals(1054)) {
                    return ApiResult.outSucceed(ApiResultCode.CMCC_GET_INTEGRAL_ERROR, "移动积分解析失败");
                }
            }
            return jsonString;
        } catch (Exception e) {
            logger.error("登录移动失败:" + e.getMessage(), e);
            throw new ManualQueryAdaptorException(ApiResultCode.CMCC_LOGIN_ERROR, e.getMessage());
        }
    }


    public String login(Long userId, String loginAccount, String password, String imageCode, String a) {
        String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/yidong/login/1.0";
        super.setName("移动");
        Map<String, String> param = new HashMap<String, String>();
        param.put("userid", userId.toString());
        param.put("account", loginAccount);
        param.put("vcode", imageCode);

        String result = super.login(userId, url, param, ApiResultCode.CMCC_SEND_PHONECODE_SUCCEED);

        //将移动的图片验证码放入redis
        String key = String.format(RedisKey.CMCC_MANUAL_USER, userId);
        redis.set(key, imageCode);

        return result;
    }

    @Override
    public String login(Long userId, String loginAccount, String password) {


        return null;
    }

    @Override
    public String save(Long userId, Long manualId, String loginAccount, String password, String result, String version) {

        try {
            boolean flag = JsonResult.checkResult(result, ApiResultCode.SUCCEED);
            if (!flag) {
                return result;
            }

//			{
//			    "code": 0,
//			    "data": "{\"data\":{\"remark\":null,\"brandCode\":\"02\",\"pointValue\":\"467\",\"pointItems\":[{\"pointName\":\"消费积分\",\"pointValue\":\"59\"},{\"pointName\":\"消费积分\",\"pointValue\":\"59\"},{\"pointName\":\"消费积分\",\"pointValue\":\"59\"},{\"pointName\":\"消费积分\",\"pointValue\":\"59\"},{\"pointName\":\"消费积分\",\"pointValue\":\"99\"},{\"pointName\":\"消费积分\",\"pointValue\":\"97\"},{\"pointName\":\"消费积分\",\"pointValue\":\"35\"}]},\"retCode\":\"000000\",\"retMsg\":\"业务异常\"}",
//			    "message": "抓取成功"
//			}

            JSONObject json = JSON.parseObject(result);
            String jsonString = json.getString("data");

            AssertUtil.hasLength(jsonString, "登录移动失败");

            JSONObject jsonObject = JSON.parseObject(jsonString);

            Integer integral = jsonObject.getInteger("integral");
            String userName = jsonObject.getString("account");

            Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
            /**
             * 记录用户积分数据
             */
            UserCardManual csAirManual = userCardManualService.getUserCardManualSelective(userId, UserCardManual.CMCC_INTEGRAL, null, null, userName);

            if (null == csAirManual) {
            	presentTubiService.presentTubiDo(userId, UserCardManual.CMCC_INTEGRAL, 0, integral, resData, version);
            	
                csAirManual = new UserCardManual();
                csAirManual.setIntegral(integral);
                csAirManual.setUserName(userName);
                csAirManual.setGmtModify(now);
                csAirManual.setGmtCreate(now);
                csAirManual.setUserId(userId);
                csAirManual.setLoginAccount(userName);
                csAirManual.setCardTypeId(UserCardManual.CMCC_INTEGRAL);
            } else {
            	presentTubiService.presentTubiDo(userId, UserCardManual.CMCC_INTEGRAL, csAirManual.getIntegral(), integral, resData, version);
            	
                csAirManual.setIntegral(integral);
                csAirManual.setUserName(userName);
                csAirManual.setLoginAccount(userName);
                csAirManual.setGmtModify(now);
                csAirManual.setStatus(1);
            }

            userCardManualService.insertORupdate(csAirManual);

            /**
             * 记录用户登录数据
             */
            ManualLogin manualLogin = new ManualLogin();
            manualLogin.setLoginAccount(userName);
            manualLogin.setUserId(userId);
            manualLogin.setManualId(manualId);
            manualLogin.setType(ManualLogin.TYPE_PHONE);

            ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
            if (oldManualLogin == null) {
                manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
                manualLogin.setPassword(password);
                manualLoginService.insert(manualLogin);
            } else {
                oldManualLogin.setStatus(1);
                oldManualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
                oldManualLogin.setPassword(password);
                manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
            }

            //删除用户登录记录key
            delUserRecordRedisKey(userId, manualId);

            JSONObject resultJson = new JSONObject();
            resultJson.put("code", ApiResultCode.SUCCEED);
            resultJson.put("message", "登录成功");
            resultJson.put("data", resData);

            return resultJson.toJSONString();
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("登录移动失败:" + e.getMessage(), e);
            throw new ManualQueryAdaptorException(ApiResultCode.CMCC_LOGIN_ERROR, e.getMessage());
        }

    }

    /**
     * 获取短信验证码
     *
     * @param userid 用户ID
     * @param phone  手机号码
     * @Description: (方法职责详细描述, 可空)
     * @Title: getPhoneCode
     * @date 2015年11月20日 下午12:23:04
     * @author xiongbin
     */
    public String getPhoneCode(Long userid, String phone, String imageCode) {
        try {
            AssertUtil.notNull(userid, "用户ID不能为空");
            AssertUtil.hasLength(phone, "手机号码不能为空");

            String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/yidong/login/1.0";
//			String url = "http://127.0.0.1:8092/spider/api/yidong/login/1.0";

            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("userid", userid.toString());
            paramMap.put("account", phone);
            paramMap.put("vcode", imageCode);

            String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);

            logger.info("获取移动短信验证码返回数据:" + jsonString);

            Boolean flag = JsonResult.checkResult(jsonString, ApiResultCode.CMCC_SEND_PHONECODE_SUCCEED);
            if (!flag) {
                JSONObject json = JSON.parseObject(jsonString);
                Integer code = json.getInteger("code");
                if (code.equals(1052)) {
                    String image = getImageCode(userid);
                    image = analysisImageCode(image);
                    JSONObject resultJson = new JSONObject();
                    resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
                    resultJson.put("message", "图片验证码不正确");
                    resultJson.put("imagecode", image);
                    return resultJson.toJSONString();
                } else if (code.equals(1075)) {
                    return ApiResult.outSucceed(ApiResultCode.CMCC_SMS_ERROR, "短信验证码错误，请返回并在一分钟后重新获取验证码");
                } else if (code.equals(1055)) {
                    return ApiResult.outSucceed(ApiResultCode.CMCC_NOT_CMCC_USER, "非移动用户,无法查询");
                } else if (code.equals(1050)) {
                    return ApiResult.outSucceed(ApiResultCode.CMCC_GET_PHONE_CODE_ERROR, "获取短信验证码失败，请稍后再试");
                }else{
                    return ApiResult.outSucceed(ApiResultCode.CMCC_GET_PHONE_CODE_ERROR, "移动系统维护中，请稍后再试");
                }
            }

            return jsonString;
        } catch (Exception e) {
            logger.error("获取移动短信验证码失败:" + e.getMessage(), e);
            throw new ManualQueryAdaptorException(ApiResultCode.CMCC_GET_PHONECODE_ERROR, e.getMessage());
        }
    }


}
