package com.caitu99.service.ishop.bcm.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.domain.OrderItem;
import com.caitu99.service.transaction.service.OrderItemService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.http.HttpClientUtils;

/**
 * 交通银行信用卡商城
 * Created by hy on 16-2-19.
 */
@Controller
@RequestMapping("/api/ishop/bcm/")
public class BCMShopController {
    private final static Logger logger = LoggerFactory.getLogger(BCMShopController.class);

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private RedisOperate redis;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private UserService userService;
	@Autowired
	ManualLoginService manualLoginService;
	@Autowired
	private RealizeService realizeService;

    /**
     * 登录
     *
     * @param userid
     * @param account
     * @param password
     * @param count
     * @param mobile
     * @param orderno
     * @return
     */
    @RequestMapping(value = "/login/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String login(Long userid, String account, String password, String cardyear, String cardmonth, String orderno) {
        ApiResult<String> apiResult = new ApiResult<>();
        //数据检查
        if (userid == null ||
                StringUtils.isEmpty(account) ||
                StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(orderno)) {
            apiResult.set(2001, "数据不完整");
            return apiResult.toString();
        }
        User loginUser = userService.selectByPrimaryKey(userid);
        String mobile = loginUser.getMobile();
        if (StringUtils.isEmpty(mobile)) {
            apiResult.set(4004, "手机号不能为空");
            return apiResult.toString();
        }
        
        if(appConfig.isDevMode){
        	return apiResult.toJSONString(2102, "");
        }
        
        List<OrderItem> orderItemList = orderItemService.listByOrderNo(orderno);
        if (orderItemList == null || orderItemList.size() == 0) {
            apiResult.set(4005, "订单不存在");
            return apiResult.toString();
        }
        if (orderItemList.size() > 1) {
            apiResult.set(4006, "不能多个商品一起购买");
            return apiResult.toString();
        }
        OrderItem orderItem = orderItemList.get(0);
        String count = String.valueOf(orderItem.getQuantity());
        String price = String.valueOf(orderItem.getPrice());

        String key = String.format(RedisKey.COM_ACCOUNT_BY_USERID, userid);
        redis.set(key, cardyear + "_" + cardmonth);
        //获取商品属性
        Map<String, Object> attrs = orderService.getToPayByThird(orderno);
        List<GoodProp> goodPropList = (List<GoodProp>) attrs.get("prop");
        Map<String, Object> goodPropMap = this.getGoodProp(goodPropList);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("account", account);
        paramMap.put("password", password);
        paramMap.put("count", count);
        paramMap.put("mobile", mobile);

        paramMap.put("prodid", goodPropMap.get("prodid").toString());
        paramMap.put("prodname", goodPropMap.get("prodname").toString());
        paramMap.put("originprice", goodPropMap.get("originPrice").toString());
        paramMap.put("cashprice", goodPropMap.get("cashprice").toString());
        paramMap.put("price", price);
        paramMap.put("cardMonth", cardmonth);
        paramMap.put("cardYear", cardyear);

        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/com/order/1.0", "utf-8", paramMap);
//                    .doPost("http://127.0.0.1:8092/api/ishop/com/order/1.0", "utf-8", paramMap);
            JSONObject json = JSON.parseObject(postResult);
            Integer code = json.getInteger("code");
            if( code == 1005 ){ //未登录状态。
                postResult = HttpClientUtils
                        .getInstances()
                        .doPost(appConfig.spiderUrl + "/api/ishop/com/login/1.0", "utf-8", paramMap);
//                        .doPost("http://127.0.0.1:8092/api/ishop/com/login/1.0", "utf-8", paramMap);
                json = JSON.parseObject(postResult);
                code = json.getInteger("code");
                if( code == 0 ){  //登录成功,下单
                    postResult = HttpClientUtils
                            .getInstances()
                            .doPost(appConfig.spiderUrl + "/api/ishop/com/order/1.0", "utf-8", paramMap);
//                            .doPost("http://127.0.0.1:8092/api/ishop/com/order/1.0", "utf-8", paramMap);
                }
            }
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            apiResult.set(2611, "请求lsp服务器失败");
        }
        apiResult = JSON.parseObject(postResult, ApiResult.class);
        
        if("2107".equals(apiResult.getCode())){
			return apiResult.toJSONString(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误","/bank_jiaohang/pay.html?orderno="+orderno);
		}
        
        //数据返回
        return apiResult.toString();
    }

    //短信验证码
    @RequestMapping(value = "/getsmscode/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSmsCode(Long userid) {
        ApiResult<String> apiResult = new ApiResult<>();
        //数据检查
        if (userid == null) {
            apiResult.set(2001, "数据不完整");
            return apiResult.toString();
        }
        
        if(appConfig.isDevMode){
        	return apiResult.toJSONString(2102, "");
        }
        
        String key = String.format(RedisKey.COM_ACCOUNT_BY_USERID, userid);
        String value = redis.getStringByKey(key);
        if (StringUtils.isEmpty(value)) {
            apiResult.set(4007, "信用卡有效期不能为空");
            return apiResult.toString();
        }
        String[] strs = value.split("_");
        String cardYear = strs[0];
        String cardMonth = strs[1];
        //发送请求
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("cardyear", cardYear);
        paramMap.put("cardmonth", cardMonth);
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/com/sms/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            apiResult.set(2611, "请求lsp服务器失败");
            return apiResult.toString();
        }
        apiResult = JSON.parseObject(postResult, ApiResult.class);
        //数据返回
        return apiResult.toString();
    }

    //支付
    @RequestMapping(value = "/pay/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String pay(Long userid, String smscode,String cardNo,String password,String cardyear,String cardmonth,String orderNo) {
        ApiResult<String> apiResult = new ApiResult<>();
//        String key = String.format(RedisKey.UNICOM_ORDERNO_USERID_KEY, userid);
//        String orderno = redis.getStringByKey(key);
        //数据检查
        if (userid == null || StringUtils.isEmpty(smscode)) {
            apiResult.set(2001, "数据不完整");
            logger.error("数据不完整： userid: " + userid + "smscode" + smscode);
            return apiResult.toString();
        }
        
        if(appConfig.isDevMode){
        	apiResult.setCode(2104);
        }else{
	        //发送请求
	        Map<String, String> paramMap = new HashMap<>();
	        paramMap.put("userid", String.valueOf(userid));
	        paramMap.put("smscode", smscode);
	        String postResult = null;
	        try {
	            postResult = HttpClientUtils
	                    .getInstances()
	                    .doPost(appConfig.spiderUrl + "/api/ishop/com/pay/1.0", "utf-8", paramMap);
	        } catch (Exception e) {
	            logger.error("请求lsp服务器失败", e);
	            apiResult.set(2611, "请求lsp服务器失败");
	            return apiResult.toString();
	        }
	        apiResult = JSON.parseObject(postResult, ApiResult.class);
	        
	        if(apiResult.getCode() == 2104 || apiResult.getCode() == 2110){
	        	//支付成功，触发刷新
				logger.info("商城消费交通银行自动更新开始");
				try {
					List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 13L);
					ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
					thread.run();
				} catch (Exception e) {
					logger.warn("商城消费交通银行自动更新失败：{}",e);
				}
	        }
        }

        if(apiResult.getCode() == 2104 || apiResult.getCode() == 2110){
        	try {
				JSONObject info = new JSONObject();
				info.put("cardNo", cardNo);
				info.put("password", password);
				info.put("cardmonth", cardmonth);
				info.put("cardyear", cardyear);
				
				UserAddTerm userAddTerm = new UserAddTerm();
				userAddTerm.setCardNo(cardNo);
				userAddTerm.setLoginAccount(cardNo);
				
				realizeService.ishopBinding(userid, 4L, info.toJSONString(), userAddTerm);
			} catch (Exception e) {
				logger.error("绑定平安第三方积分商城失败:" + e.getMessage(),e);
			}
        	
        	orderService.processTradeOrder(1,userid,orderNo,"");
        }else{
        	orderService.processTradeOrder(0,userid,orderNo,"");
        }
        
        //数据返回
        return apiResult.toString();
    }

    private Map<String, Object> getGoodProp(List<GoodProp> goodPropList) {
        Map<String, Object> goodPropMap = new HashMap<>();
        for (GoodProp goodProp : goodPropList) {
            goodPropMap.put(goodProp.getName(), goodProp.getValue());
        }
        return goodPropMap;
    }
}
