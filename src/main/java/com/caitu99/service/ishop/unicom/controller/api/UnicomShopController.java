package com.caitu99.service.ishop.unicom.controller.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.service.GoodPropService;
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.ishop.domain.UserPwd;
import com.caitu99.service.ishop.service.UserPwdService;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.domain.OrderItem;
import com.caitu99.service.transaction.service.OrderItemService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

/**
 * Created by Administrator on 2016/1/14.
 */
@Controller
@RequestMapping("/api/ishop/unicom")
public class UnicomShopController {
    private static final Logger logger = LoggerFactory
            .getLogger(UnicomShopController.class);

    @Autowired
    private GoodPropService goodPropService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private UserPwdService userPwdService;

    @Autowired
    private RedisOperate redis;

    @Autowired
    private AppConfig appConfig;
	@Autowired
	ManualLoginService manualLoginService;
	@Autowired
	private RealizeService realizeService;

    private Integer time = 7200;        //订单存储时间

    /**
     * @param userid  用户id
     * @param orderno 订单号
     * @return
     */
//    @RequestMapping(value = "/init/1.0", produces = "application/json;charset=utf-8")
//    @ResponseBody
//    public String init(Long userid, String orderno) {
//
//        ApiResult<String> apiResult = new ApiResult<>();
//
//        //数据检查
//        if (userid == null || StringUtils.isEmpty(orderno)) {
//            apiResult.set(2001, "数据不完整", "userid: " + userid + ",orderNo:" + orderno);//todo
//            return apiResult.toString();
//        }
//
//        Map<String, Object> attrs = orderService.getToPayByThird(orderno);
//
//        //String price = attrs.get("price").toString();
//        //获取商品在联通商城的id
//        List<GoodProp> goodPropList = (List<GoodProp>) attrs.get("prop");
//        Map<String, Object> goodPropMap = this.getGoodProp(goodPropList);
//        String giftid = (String) goodPropMap.get("giftid");
//        if (StringUtils.isEmpty(giftid)) {
//            logger.error("缺少giftid属性值");
//            apiResult.set(3801, "未能获取到属性giftid");//todo
//            return apiResult.toString();
//        }
//
//        List<OrderItem> orderItems = orderItemService.listByOrderNo(orderno);
//        if (orderItems.size() != 1) {
//            logger.error("订单号：" + orderno + "，对应的orderItem记录不为1：条数：{}", orderItems.size());
//            apiResult.set(3802, "订单号：" + orderno + "，对应的记录不为1", String.valueOf(orderItems.size()));//todo
//            return apiResult.toString();
//        }
//        OrderItem orderItem = orderItems.get(0);
//        //数量
//        Integer quantity = orderItem.getQuantity();
//        //获取商品价格
//        Long price = orderItem.getPrice();
//
//        //发送请求
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("userid", String.valueOf(userid));
//        paramMap.put("price", price.toString());
//        paramMap.put("giftid", giftid);
//        paramMap.put("nums", quantity.toString());
//        paramMap.put("local_orderno", orderno);
////        Map<String, String> paramMap = new HashMap<>();
////        paramMap.put("userid", String.valueOf(userid));
////        paramMap.put("price", "500");
////        paramMap.put("giftid", "E22430");
////        paramMap.put("nums", "1");
//        String postResult = null;
//        try {
//            postResult = HttpClientUtils
//                    .getInstances()
//                    .doPost(appConfig.spiderUrl + "/api/shop/unicom/init/1.0", "utf-8", paramMap);
//        } catch (Exception e) {
//            logger.error("请求lsp服务器失败", e);
//            apiResult.set(2611, "请求lsp服务器失败");
//            return apiResult.toString();
//        }
//        ApiResult apiResultLsp = null;
//        try {
//            apiResultLsp = JSON.parseObject(postResult, ApiResult.class);
//        } catch (Exception e) {
//            logger.error("解析lsp返回的数据为JSON发生异常,{}", postResult, e);
//            apiResult.set(3803, "lsp服务器返回的数据无法解析为json", postResult);
//            return apiResult.toString();
//        }
//        if (apiResultLsp == null) {
//            apiResult.set(3803, "lsp服务器返回的数据无法解析为json", postResult);
//            return apiResult.toString();
//        }
//
//        if (apiResultLsp.getCode() == 1207) {       //正常情况
//            String key = String.format(RedisKey.UNICOM_ORDERNO_USERID_KEY, userid);
//            redis.set(key, orderno, time);
//            apiResult.set(3804, " 联通商城初始化完成", postResult);
//            return apiResult.toString();
//        } else if (apiResultLsp.getCode() == 1006 ||
//                apiResultLsp.getCode() == 1200 ||
//                apiResultLsp.getCode() == 1201 ||
//                apiResultLsp.getCode() == 1202
//                ) {
//            logger.error("联通商城登录初始化参数错误");
//            apiResult.set(3805, "LSP接收到的参数错误", postResult);
//            return apiResult.toString();
//        } else if (apiResultLsp.getCode() == 1205) {
//            logger.error("联通商城登录初始化无法获取到登录页面URL");
//            apiResult.set(3806, "无法获取到登录页面URL");
//            return apiResult.toString();
//        } else if (apiResultLsp.getCode() == 1208) {
//            apiResult.set(3807, "获取登录页面失败");
//            return apiResult.toString();
//        }
//        //对于已经登录到联通商城的情况：
//        if(apiResultLsp.getCode() == 1214)
//        {
//            apiResult.set(3821, "请获取短信验证码");
//        }
//        else if(apiResultLsp.getCode() == 1006)
//        {
//            apiResult.set(3805, "LSP接收到的参数错误", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1005)
//        {
//            apiResult.set(3825, "LSP：账号验证已过期", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1003)
//        {
//            apiResult.set(3826, "LSP：状态值不对", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1213)
//        {
//            apiResult.set(3818, "返回的数据有误", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1212)
//        {
//            apiResult.set(3819, "积分数据异常，无法转换", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1211)
//        {
//            apiResult.set(3820, "积分不足以兑换该商品", apiResultLsp.getData().toString());
//        }
//        else {   //其他未知状态
//            logger.info("其他未知错误："+postResult);
//            apiResult.set(3811, "其他未知错误", postResult);
//        }
//        return apiResult.toString();
//    }
    
    @RequestMapping(value = "/init/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String init(Long userid, String orderno) {

        ApiResult<String> apiResult = new ApiResult<>();

//        //数据检查
//        if (userid == null || StringUtils.isEmpty(orderno)) {
//            apiResult.set(2001, "数据不完整", "userid: " + userid + ",orderNo:" + orderno);//todo
//            return apiResult.toString();
//        }
//
//
//        //发送请求
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("userid", String.valueOf(userid));
        String postResult = null;
//        try {
//            postResult = HttpClientUtils
//                    .getInstances()
//                    .doPost(appConfig.spiderUrl + "/api/shop/unicom/init/1.0", "utf-8", paramMap);
//        } catch (Exception e) {
//            logger.error("请求lsp服务器失败", e);
//            apiResult.set(2611, "请求lsp服务器失败");
//            return apiResult.toString();
//        }
//        ApiResult apiResultLsp = null;
//        try {
//            apiResultLsp = JSON.parseObject(postResult, ApiResult.class);
//        } catch (Exception e) {
//            logger.error("解析lsp返回的数据为JSON发生异常,{}", postResult, e);
//            apiResult.set(3803, "lsp服务器返回的数据无法解析为json", postResult);
//            return apiResult.toString();
//        }
//        if (apiResultLsp == null) {
//            apiResult.set(3803, "lsp服务器返回的数据无法解析为json", postResult);
//            return apiResult.toString();
//        }
//
//        if (apiResultLsp.getCode() == 1207) {       //正常情况
//            String key = String.format(RedisKey.UNICOM_ORDERNO_USERID_KEY, userid);
//            redis.set(key, orderno, time);
            apiResult.set(3804, " 联通商城初始化完成", postResult);
            return apiResult.toString();
//        } else if (apiResultLsp.getCode() == 1006 ||
//                apiResultLsp.getCode() == 1200 ||
//                apiResultLsp.getCode() == 1201 ||
//                apiResultLsp.getCode() == 1202
//                ) {
//            logger.error("联通商城登录初始化参数错误");
//            apiResult.set(3805, "LSP接收到的参数错误", postResult);
//            return apiResult.toString();
//        } else if (apiResultLsp.getCode() == 1205) {
//            logger.error("联通商城登录初始化无法获取到登录页面URL");
//            apiResult.set(3806, "无法获取到登录页面URL");
//            return apiResult.toString();
//        } else if (apiResultLsp.getCode() == 1208) {
//            apiResult.set(3807, "获取登录页面失败");
//            return apiResult.toString();
//        }
//        //对于已经登录到联通商城的情况：
//        if(apiResultLsp.getCode() == 1214)
//        {
//            apiResult.set(3821, "请获取短信验证码");
//        }
//        else if(apiResultLsp.getCode() == 1006)
//        {
//            apiResult.set(3805, "LSP接收到的参数错误", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1005)
//        {
//            apiResult.set(3825, "LSP：账号验证已过期", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1003)
//        {
//            apiResult.set(3826, "LSP：状态值不对", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1213)
//        {
//            apiResult.set(3818, "返回的数据有误", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1212)
//        {
//            apiResult.set(3819, "积分数据异常，无法转换", postResult);
//        }
//        else if(apiResultLsp.getCode() == 1211)
//        {
//            apiResult.set(3820, "积分不足以兑换该商品", apiResultLsp.getData().toString());
//        }
//        else {   //其他未知状态
//            logger.info("其他未知错误："+postResult);
//            apiResult.set(3811, "其他未知错误", postResult);
//        }
//        return apiResult.toString();
    }

    @RequestMapping(value = "/getvcode/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getVcode(Long userid) {

        ApiResult<String> apiResult = new ApiResult<>();

        //数据检查
        if (userid == null) {
            apiResult.set(2001, "数据不完整");
            return apiResult.toString();
        }
        
        String reslut = this.init(userid, null);
        boolean flag = JsonResult.checkResult(reslut, 3804);
        if(!flag){
        	return reslut;
        }

        //发送请求
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/shop/unicom/getvcode/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            apiResult.set(2611, "请求lsp服务器失败");
            return apiResult.toString();
        }
        ApiResult apiResultLsp = null;
        try {
            apiResultLsp = JSON.parseObject(postResult, ApiResult.class);
        } catch (Exception e) {
            logger.error("解析lsp返回的数据为JSON发生异常,{}", postResult, e);
            apiResult.set(3803, "获取验证码失败，联通系统维护中，请稍后再试", postResult);
            return apiResult.toString();
        }
        if (apiResultLsp == null) {
            apiResult.set(3803, "lsp服务器返回的数据无法解析为json", postResult);
            return apiResult.toString();
        }
        if (apiResultLsp.getCode() == 1001) {
            apiResult.set(3810, "请输入验证码",apiResultLsp.getData().toString());
        } else if (apiResultLsp.getCode() == 1206) {
            apiResult.set(3809, "未接收到uvc cookie", postResult);
        }else {
            logger.info("其他未知错误："+postResult);
            apiResult.set(3811, "获取验证码失败，联通系统维护中，请稍后再试", postResult);
        }

        return apiResult.toString();
    }

    @RequestMapping(value = "/login/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String login(Long userid, String account, String password, String vcode, String self) {

        ApiResult<String> apiResult = new ApiResult<>();

        //数据检查
        if (userid == null ||
                StringUtils.isEmpty(account) ||
                StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(self)
                ) {
            apiResult.set(2001, "数据不完整");
            return apiResult.toString();
        }
        
        if(appConfig.isDevMode){
        	Random r = new Random();
	    	int i = r.nextInt(100) % 2;
	    	if(i == 0){
	        	return apiResult.toJSONString(3821, "");
	    	}else{
	        	return apiResult.toJSONString(3814, "验证码错误");
	    	}
        }
        
        if(StringUtils.isBlank(vcode)){
			logger.info("前端传递图片验证码为空,自动破解验证码");
			//获取图片验证码
			vcode = this.getVcode(userid);
			boolean flagImage = JsonResult.checkResult(vcode,3810);
			if(flagImage){
				vcode = JsonResult.getResult(vcode, "data");
				String imageCodeNew = CommonImgCodeApi.recognizeImgCodeFromStr(vcode);
				if(StringUtils.isBlank(imageCodeNew)){
					return apiResult.toJSONString(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR,"破解图片验证码失败",vcode);
				}
				vcode = imageCodeNew;
			}else{
				return apiResult.toJSONString(-1,"获取图片验证码失败");
			}
		}
        

        //发送请求
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("account", account);
        paramMap.put("password", password);
        paramMap.put("vcode", vcode);
        paramMap.put("self", self);
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/shop/unicom/login/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            apiResult.set(2611, "请求lsp服务器失败");
            return apiResult.toString();
        }
        ApiResult apiResultLsp = null;
        try {
            apiResultLsp = JSON.parseObject(postResult, ApiResult.class);
        } catch (Exception e) {
            logger.error("解析lsp返回的数据为JSON发生异常,{}", postResult, e);
            apiResult.set(3803, "登录失败，联通系统维护中，请稍后再试", postResult);
            return apiResult.toString();
        }
        if (apiResultLsp == null) {
            apiResult.set(3803, "登录失败，联通系统维护中，请稍后再试", postResult);
            return apiResult.toString();
        }
        //保存用户名密码
        if (apiResultLsp.getCode() == 1214) {
            UserPwd userPwd = new UserPwd();
            userPwd.setAccount(account);
            userPwd.setCompany(2);
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

        }
        if(apiResultLsp.getCode() == 1214)
        {
            apiResult.set(3821, "请获取短信验证码");
        }
        else if(apiResultLsp.getCode() == 1006)
        {
            apiResult.set(3805, "LSP接收到的参数错误", postResult);
        }
        else if(apiResultLsp.getCode() == 1005)
        {
            apiResult.set(3825, "LSP：账号验证已过期", postResult);
        }
        else if(apiResultLsp.getCode() == 1003)
        {
            apiResult.set(3826, "LSP：状态值不对", postResult);
        }
        else if(apiResultLsp.getCode() == 1210)
        {
            apiResult.set(3812, "登录返回的数据无法解析", postResult);
        }
        else if(apiResultLsp.getCode() == 1067)
        {
            apiResult.set(3813, "用户名或密码错误", postResult);
        }
        else if(apiResultLsp.getCode() == 1052)
        {
            apiResult.set(3814, "验证码错误", postResult);
        }
        else if(apiResultLsp.getCode() == 1013)
        {
            apiResult.set(3815, "登录失败", postResult);
        }
        else if(apiResultLsp.getCode() == 1209)
        {
            apiResult.set(3816, "无法解析到重定向url", postResult);
        }
        else if(apiResultLsp.getCode() == 1224)
        {
            apiResult.set(3817, "获取新的SESSIONID时无法获取到重定向url", postResult);
        }
        else if(apiResultLsp.getCode() == 1213)
        {
            apiResult.set(3818, "返回的数据有误", postResult);
        }
        else if(apiResultLsp.getCode() == 1212)
        {
            apiResult.set(3819, "积分数据异常，无法转换", postResult);
        }
        else if(apiResultLsp.getCode() == 1211)
        {
            apiResult.set(3820, "积分不足以兑换该商品", apiResultLsp.getData().toString());
        }
        else if(apiResultLsp.getCode() == 1230)
        {
            apiResult.set(3836, "账号登录异常，请稍后再试", postResult);
        }
        else if(apiResultLsp.getCode() == 1231)
        {
            apiResult.set(3837, "登录过于频繁，为保障您的账号安全，请稍后再试", postResult);
        }
        else if(apiResultLsp.getCode() == 1232)
        {
            apiResult.set(3838, "您的账号登录受限，请联系客服", postResult);
        }
        else if(apiResultLsp.getCode() == 1234)
        {
            apiResult.set(3839, "不支持简单密码登录", postResult);
        }
        else {
            logger.info("其他未知错误："+postResult);
            apiResult.set(3811, "登录失败，联通系统维护中，请稍后再试", postResult);
        }
        return apiResult.toString();
    }

    @RequestMapping(value = "/getsmscode/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSmsCode(Long userid,String orderno) {

        ApiResult<String> apiResult = new ApiResult<>();

        //数据检查
        if (userid == null || StringUtils.isEmpty(orderno)) {
        	apiResult.set(2001, "数据不完整", "userid: " + userid + ",orderNo:" + orderno);//todo
        	return apiResult.toString();
        }

        if(appConfig.isDevMode){
        	return apiResult.toJSONString(3822, "");
        }
        
        Map<String, Object> attrs = orderService.getToPayByThird(orderno);
        
        //String price = attrs.get("price").toString();
        //获取商品在联通商城的id
        List<GoodProp> goodPropList = (List<GoodProp>) attrs.get("prop");
        Map<String, Object> goodPropMap = this.getGoodProp(goodPropList);
        String giftid = (String) goodPropMap.get("giftid");
        if (StringUtils.isEmpty(giftid)) {
            logger.error("缺少giftid属性值");
            apiResult.set(3801, "未能获取到属性giftid");//todo
            return apiResult.toString();
        }

        List<OrderItem> orderItems = orderItemService.listByOrderNo(orderno);
        if (orderItems.size() != 1) {
            logger.error("订单号：" + orderno + "，对应的orderItem记录不为1：条数：{}", orderItems.size());
            apiResult.set(3802, "订单号：" + orderno + "，对应的记录不为1", String.valueOf(orderItems.size()));//todo
            return apiResult.toString();
        }
        OrderItem orderItem = orderItems.get(0);
        //数量
        Integer quantity = orderItem.getQuantity();
        //获取商品价格
        Long price = orderItem.getPrice();

        //发送请求
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("price", price.toString());
        paramMap.put("giftid", giftid);
        paramMap.put("nums", quantity.toString());
        paramMap.put("local_orderno", orderno);
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/shop/unicom/getsmscode/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            apiResult.set(2611, "请求lsp服务器失败");
            return apiResult.toString();
        }
        ApiResult apiResultLsp = null;
        try {
            apiResultLsp = JSON.parseObject(postResult, ApiResult.class);
        } catch (Exception e) {
            logger.error("解析lsp返回的数据为JSON发生异常,{}", postResult, e);
            apiResult.set(3803, "获取短信验证码失败，联通系统维护中，请稍后再试", postResult);
            return apiResult.toString();
        }
        if (apiResultLsp == null) {
            apiResult.set(3803, "获取短信验证码失败，联通系统维护中，请稍后再试", postResult);
            return apiResult.toString();
        }
        if(apiResultLsp.getCode() == 1215)
        {
        	String key = String.format(RedisKey.UNICOM_ORDERNO_USERID_KEY, userid);
        	redis.set(key, orderno, time);
            apiResult.set(3822, "请输入短信验证码",postResult);
        }
        else if(apiResultLsp.getCode() == 1006)
        {
            apiResult.set(3805, "LSP接收到的参数错误", postResult);
        }
        else if(apiResultLsp.getCode() == 1005)
        {
            apiResult.set(3825, "LSP：账号验证已过期", postResult);
        }
        else if(apiResultLsp.getCode() == 1003)
        {
            apiResult.set(3826, "LSP：状态值不对", postResult);
        }
        else if(apiResultLsp.getCode() == 1216)
        {
            apiResult.set(3823, "请求发送短信验证码出错", postResult);
        }
        else if(apiResultLsp.getCode() == 1226)
        {
            apiResult.set(3835, "请60秒后再发送请求", postResult);
        }
        else {
            logger.info("其他未知错误："+postResult);
            apiResult.set(3811, "获取短信验证码失败，联通系统维护中，请稍后再试", postResult);
        }

        return apiResult.toString();
    }

    @RequestMapping(value = "/submit/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String submit(Long userid, String smscode,String phone,String password,String orderNo) {
        ApiResult<String> apiResult = new ApiResult<>();
        
        //数据检查
        if (userid == null || StringUtils.isEmpty(smscode) 
        		|| StringUtils.isBlank(phone) || StringUtils.isBlank(password) || StringUtils.isBlank(orderNo)) {
            apiResult.set(2001, "数据不完整");
            logger.error("数据不完整： userid: " + userid + "smscode" + smscode);
            return apiResult.toString();
        }

        ApiResult apiResultLsp = null;
        String postResult = null;
        
        if(appConfig.isDevMode){
        	apiResultLsp = new ApiResult();
        	apiResultLsp.setCode(1222);
        	apiResultLsp.setData("");
        }else{
	        //发送请求
	        Map<String, String> paramMap = new HashMap<>();
	        paramMap.put("userid", String.valueOf(userid));
	        paramMap.put("smscode", smscode);
	        try {
	            postResult = HttpClientUtils
	                    .getInstances()
	                    .doPost(appConfig.spiderUrl + "/api/shop/unicom/submit/1.0", "utf-8", paramMap);
	        } catch (Exception e) {
	            logger.error("请求lsp服务器失败", e);
	            apiResult.set(2611, "请求lsp服务器失败");
	            return apiResult.toString();
	        }
	        try {
	            apiResultLsp = JSON.parseObject(postResult, ApiResult.class);
	        } catch (Exception e) {
	            logger.error("解析lsp返回的数据为JSON发生异常,{}", postResult, e);
	            apiResult.set(3803, "购买失败，联通系统维护中，请稍后再试", postResult);
	            return apiResult.toString();
	        }
	        if (apiResultLsp == null) {
	            apiResult.set(3803, "购买失败，联通系统维护中，请稍后再试", postResult);
	            return apiResult.toString();
	        }
        }

        if (1222 == apiResultLsp.getCode() || 1228 == apiResultLsp.getCode()) {
        	//触发积分查询
			logger.info("商城消费，联通自动更新开始，manualID:{}",6);
			try {
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 6L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("商城消费，联通自动更新失败：{}",e);
			}
			
            logger.info("更改订单状态");
            orderService.processOrderByThird(1, userid, orderNo, apiResultLsp.getData().toString());
            
            try {
				JSONObject info = new JSONObject();
				info.put("phone", phone);
				info.put("password", password);
				
				UserAddTerm userAddTerm = new UserAddTerm();
				userAddTerm.setLoginAccount(phone);
				
				realizeService.ishopBinding(userid, 6L, info.toJSONString(), userAddTerm);
			} catch (Exception e) {
				logger.error("绑定联通第三方积分商城失败:" + e.getMessage(),e);
			}
        }
        if(apiResultLsp.getCode() == 1222)
        {
            apiResult.set(3827, "购买联通商城商品成功", postResult);
        }
        else if(apiResultLsp.getCode() == 1228) {
            apiResult.set(3829, "购买联通商城商品成功，但给自己充值未成功", postResult);
        }
        else if(apiResultLsp.getCode() == 1218) {
            apiResult.set(3828, "联通商城提交订单短信验证失败", postResult);
        }
        else if(apiResultLsp.getCode() == 1219)
        {
            apiResult.set(3824, "您输入的随机码不正确，请重新获取随机码", postResult);
        }
        else if(apiResultLsp.getCode() == 1221)
        {
            apiResult.set(3830, "短信验证后的提交返回有误", postResult);
        }
        else if(apiResultLsp.getCode() == 1225)
        {
            apiResult.set(3831, "您的积分余额不足", postResult);
        }
        else if(apiResultLsp.getCode() == 1227)
        {
            apiResult.set(3832, "无法获取到订单号页面url", postResult);
        }
        else if(apiResultLsp.getCode() == 1229)
        {
            apiResult.set(3833, "未能获取到订单提交结果信息", postResult);
        }
        else if(apiResultLsp.getCode() == 1223)
        {
            apiResult.set(3834, "未能获取到订单号", postResult);
        }
        else {
            logger.info("其他未知错误："+postResult);
            apiResult.set(3811, "购买失败，联通系统维护中，请稍后再试", postResult);
        }
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
