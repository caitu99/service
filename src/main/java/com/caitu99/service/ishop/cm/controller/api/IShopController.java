package com.caitu99.service.ishop.cm.controller.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.service.GoodPropService;
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.ishop.domain.UserPwd;
import com.caitu99.service.ishop.service.UserPwdService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.http.HttpClientUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/ishop/cm")
public class IShopController extends BaseController {

    private static final Logger logger = LoggerFactory
            .getLogger(IShopController.class);

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private RedisOperate redis;
    @Autowired
    private UserPwdService userPwdService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodPropService goodPropService;
	@Autowired
	ManualLoginService manualLoginService;

    /**
     * 验证是否可以下单
     *
     * @param userid
     * @param price
     * @return 2611 请求lsp服务器失败
     * 1081 用户尚未登录
     * 1084 积分不足
     * 1085 可以下单
     */
    @RequestMapping(value = "/validate/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String validate(Long userid, String orderno) {
        //初始化
        ApiResult<String> apiResult = new ApiResult<>();
        //业务实现
        Map<String, Object> attrs = orderService.getToPayByThird(orderno);
        String price = attrs.get("price").toString();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("price", price);
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/cm/validate/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            apiResult.set(2611, "请求lsp服务器失败");
            logger.info("删除订单");
            orderService.processOrderByThird(0, userid, orderno, null);
            return apiResult.toString();
        }
        apiResult = JSON.parseObject(postResult, ApiResult.class);
        if (1019 == apiResult.getCode()) {//已经登录
            JSONObject jsonObject = JSON.parseObject(apiResult.getData());
            JSONObject infoObject = jsonObject.getJSONObject("info");
            Long availableIntegral = infoObject.getLong("userCurrentIntegral");
            if (availableIntegral.compareTo(Long.valueOf(price)) < 0) {
                apiResult.set(1084, "您的移动积分小于" + price);
                logger.info("删除订单");
                orderService.processOrderByThird(0, userid, orderno, null);
                return apiResult.toString();
            }
        } else {//尚未登录
            return apiResult.toString();
        }
        apiResult.set(1085, "可以下单");
        return apiResult.toString();
    }

    /**
     * 获取验证码
     *
     * @param userid
     * @param account
     * @return 1001:"成功获取验证码"
     */
    @RequestMapping(value = "/vcode/get/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getVcode(Long userid, String account, String orderno) {
        logger.debug("获取验证码。。。");
        ApiResult<String> apiResult = new ApiResult<>();
        //业务实现
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("account", account);
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/cm/vcode/get/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            apiResult.set(2611, "请求lsp服务器失败");
        }
        try{
            apiResult = JSON.parseObject(postResult, ApiResult.class);
        }catch(com.alibaba.fastjson.JSONException ee){
            ee.printStackTrace();
            return apiResult.toJSONString(-1, "获取验证码失败，移动系统维护中，请稍后再试");
        }
        //数据返回
        return apiResult.toString();
    }

    /**
     * 登录
     *
     * @param userid
     * @param account
     * @param password
     * @param vcode
     * @param price
     * @return
     */
    @RequestMapping(value = "/login/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String login(Long userid, String account, String password, String vcode, String orderno) {
        logger.debug("开始登录。。。");
        ApiResult<String> apiResult = new ApiResult<>();
        //业务实现
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("account", account);
        paramMap.put("password", password);
        paramMap.put("vcode", vcode);
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/cm/login/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            //logger.info("删除订单");
            //orderService.processOrderByThird(0, userid, orderNo, null);
            apiResult.set(2611, "请求lsp服务器失败");
        }
        try{
            apiResult = JSON.parseObject(postResult, ApiResult.class);
        }catch(com.alibaba.fastjson.JSONException ee){
            ee.printStackTrace();
            return apiResult.toJSONString(-1, "登录失败，移动系统维护中，请稍后再试");
        }

        if (1019 == apiResult.getCode()) {//登录成功
            //保存用户移动商城的账号密码信息
            UserPwd userPwd = new UserPwd();
            userPwd.setAccount(account);
            userPwd.setCompany(1);
            userPwd.setGmtCreate(new Date());
            userPwd.setGmtModify(new Date());
            try {
                userPwd.setPassword(AESCryptoUtil.encrypt(password));
            } catch (CryptoException e) {
                logger.error("加密失败", e);
            }
            userPwd.setStatus(1);
            userPwd.setUserId(userid);
            userPwdService.saveUserPwd(userPwd);
            JSONObject jsonObject = JSON.parseObject(apiResult.getData());
            JSONObject infoObject = jsonObject.getJSONObject("info");
            Long availableIntegral = infoObject.getLong("userCurrentIntegral");
            Map<String, Object> attrs = orderService.getToPayByThird(orderno);
            String price = attrs.get("price").toString();
            if (availableIntegral.compareTo(Long.valueOf(price)) < 0) {
                apiResult.set(1084, "您的移动积分小于" + price);
                logger.info("删除订单");
                orderService.processOrderByThird(0, userid, orderno, null);
                return apiResult.toString();
            }
        } else if(1087 == apiResult.getCode()
                || 1088 == apiResult.getCode()
                || 1092 == apiResult.getCode()
                || 1119 == apiResult.getCode()
                || 1081 == apiResult.getCode()
                || 1001 == apiResult.getCode()){ //登录失败
            return apiResult.toString();
        }else{
            return apiResult.toJSONString(-1, "登录失败，移动系统维护中，请稍后再试");
        }
        //数据返回
        apiResult.set(1085, "可以下单");
        return apiResult.toString();
    }

    /**
     * 获取短信验证码
     *
     * @param userid
     * @param account
     * @param wareIds
     * @return
     */
    @RequestMapping(value = "/sms/get/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSms(Long userid, String orderno) {
        logger.debug("获取短信验证码。。。");
        ApiResult<String> apiResult = new ApiResult<>();
        //业务实现
        //获取商品属性
        Map<String, Object> attrs = orderService.getToPayByThird(orderno);
        List<GoodProp> goodPropList = (List<GoodProp>) attrs.get("prop");
        Map<String, Object> goodPropMap = this.getGoodProp(goodPropList);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("wareIds", goodPropMap.get("wareid").toString());
        StringBuffer hisProductv3 = new StringBuffer("[");
        //[{"wareid":"100000000523655","warename":"5元30M数据流量包(次月生效）","warepic":"55/55/346041_1429513576601_240.jpg","base_value":"420","payType":"01"}]
        hisProductv3.append("{'wareid':'").append(goodPropMap.get("wareid")).append("','warename':'")
                .append(goodPropMap.get("warename")).append("','warepic':'").append(goodPropMap.get("warepic"))
                .append("','base_value':'").append(goodPropMap.get("base_value")).append("','payType':'")
                .append(goodPropMap.get("payType")).append("'}]");
        String temp = "";
        try {
            temp = URLEncoder.encode(hisProductv3.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        paramMap.put("hisProductv3", temp);
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/cm/sms/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            //logger.info("删除订单");
            //orderService.processOrderByThird(0, userid, orderNo, null);
            apiResult.set(2611, "请求lsp服务器失败");
        }
        try{
            apiResult = JSON.parseObject(postResult, ApiResult.class);
        }catch(com.alibaba.fastjson.JSONException ee){
            ee.printStackTrace();
            return apiResult.toJSONString(-1, "获取短信验证码失败，移动系统维护中，请稍后再试");
        }

        //数据返回
        return apiResult.toString();
    }

    @RequestMapping(value = "/order/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String order(Long userid, String smscode, String orderno) {
        logger.debug("开始下单。。。");
        ApiResult<String> apiResult = new ApiResult<>();
        //业务实现
        Map<String, Object> attrs = orderService.getToPayByThird(orderno);
        List<GoodProp> goodPropList = (List<GoodProp>) attrs.get("prop");
        Map<String, Object> goodPropMap = this.getGoodProp(goodPropList);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("smsCode", smscode);
        paramMap.put("wareIds", goodPropMap.get("wareid").toString());
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/cm/order/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            //logger.info("删除订单");
            //orderService.processOrderByThird(0, userid, orderNo, null);
            apiResult.set(2611, "请求lsp服务器失败");
        }

        try{
            apiResult = JSON.parseObject(postResult, ApiResult.class);
        }catch(com.alibaba.fastjson.JSONException ee){
            ee.printStackTrace();
            return apiResult.toJSONString(-1, "获取短信验证码失败，移动系统维护中，请稍后再试");
        }

        if (1083 == apiResult.getCode()) {
            //成功
            orderService.processOrderByThird(1, userid, orderno, apiResult.getData());

			logger.info("商城消费，移动自动更新开始，manualID:{}",8);
			try {
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 8L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("商城消费，移动自动更新失败：{}",e);
			}
            
        } else {
            //失败
            logger.info("删除订单");
            orderService.processOrderByThird(0, userid, orderno, null);
        }
        //数据返回
        return apiResult.toString();
    }

    /**
     * 获取商品属性
     *
     * @param itemId
     * @return
     */
    private Map getGoodAttr(Long itemId) {
        Map<String, String> attrs = new HashMap<>();
        List<GoodProp> list = goodPropService.findPropByItemId(itemId, 0);
        for (GoodProp goodProp : list) {
            attrs.put(goodProp.getName(), goodProp.getValue());
        }
        return attrs;
    }

    private Map<String, Object> getGoodProp(List<GoodProp> goodPropList) {
        Map<String, Object> goodPropMap = new HashMap<>();
        for (GoodProp goodProp : goodPropList) {
            goodPropMap.put(goodProp.getName(), goodProp.getValue());
        }
        return goodPropMap;
    }
}
