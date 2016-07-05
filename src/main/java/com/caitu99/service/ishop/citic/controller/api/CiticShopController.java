package com.caitu99.service.ishop.citic.controller.api;

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
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.ishop.domain.UserPwd;
import com.caitu99.service.ishop.service.UserPwdService;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.domain.OrderAddress;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.http.HttpClientUtils;

/**
 * Created by chenhl on 2016/1/21.
 */
@Controller
@RequestMapping("/api/ishop/citic")
public class CiticShopController extends BaseController {

    private static final Logger logger = LoggerFactory
            .getLogger(CiticShopController.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private UserPwdService userPwdService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RealizeService realizeService;


    /**
     * 获取登陆图片验证码
     *
     * @param userid
     * @return 1001:"成功获取验证码"
     */
    @RequestMapping(value = "/vcode/get/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getVcode(Long userid) {
        logger.debug("获取中信登陆图片验证码");
        ApiResult apiResult = new ApiResult<>();
        //业务实现
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        apiResult = getVCode(userid);
        if(apiResult != null)
            return apiResult.toString();
        else
            return "";
    }

    /**
     * 登录
     * @param userid
     * @param account
     * @param password
     * @param vcode
     * @return
     */
    @RequestMapping(value = "/login/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String login(Long userid, String account, String password, String vcode, String orderno) {
        logger.info("begin login: {}, {}, {}, {}, {}", userid, account, password, vcode, orderno);
        ApiResult<String> apiResult = new ApiResult<>();

        /*if(appConfig.isDevMode){
			Random r = new Random();
	    	int i = r.nextInt(100) % 2;
	    	if(i == 0){
	    		 ApiResult<String> json_vcode = getVCode(userid);
	             if(json_vcode != null) {
	            	 String vCodeString = json_vcode.getData();
	            	 return apiResult.toJSONString(3901,"破解图片验证码失败",vCodeString);
	             } else {
	                 apiResult.set(3902, "获取验证码失败");
	                 return apiResult.toString();
	             }
	    	}else{
	    		i = r.nextInt(100) % 2;
	    		if(i == 0){
		    		return apiResult.toJSONString(1097, "短信发送成功");
	    		}else{
		    		return apiResult.toJSONString(1001, "");
	    		}
	    	}
		}*/
        
        
        //业务实现
        String code = null;
        String vCodeString = null;
        if(StringUtils.isEmpty(vcode)){  //用户未输验证码，后台自动识别
            ApiResult<String> json_vcode = getVCode(userid);
            if(json_vcode != null) {
                vCodeString = json_vcode.getData();
                Integer count = 0;
                do {
                    code = CommonImgCodeApi.recognizeImgCodeFromStr(vCodeString);  //自动识别验证码
                    if (code != null) {
                        break;
                    }
                    count++;
                } while (count < 2);
                vcode = code;
            } else {
                apiResult.set(3902, "获取验证码失败");
                return apiResult.toString();
            }

            if (StringUtils.isEmpty(code)) {
                apiResult.set(3901, "请输入验证码", vCodeString);
                return apiResult.toString();
            }
        }

        Map<String, Object> order_map = orderService.getGoodByTrade(orderno, userid);
        OrderAddress orderAddress = (OrderAddress)order_map.get("orderAddress");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("account", account);  //身份证号
        paramMap.put("password", password);
        paramMap.put("vcode", vcode);  //图片验证码
        paramMap.put("price",order_map.get("price").toString());  //商品单价
        paramMap.put("quantity",order_map.get("quantity").toString());  //商品数量
        paramMap.put("addrzip",orderAddress.getZipCode());  //收货地址邮编
        paramMap.put("addr",orderAddress.getProvince() + orderAddress.getCity() + orderAddress.getArea() + orderAddress.getDetailed()); //收货地址
        paramMap.put("name",orderAddress.getName()); //收货人姓名
        paramMap.put("cardId",account);   //收货人身份证
        paramMap.put("mobile", orderAddress.getMobile()); //收货人号码

        List<GoodProp> propList = (List<GoodProp>) order_map.get("propList");
        String goods_id = "";
        String goods_payway_id = "";
        String vendor_id = "";
        String vendor_nm = "";
        String type_id = "";
        String goods_nm = "";
        for (GoodProp goodProp : propList) {
            switch (goodProp.getName()) {
                case "goods_id":
                    goods_id = goodProp.getValue();
                    break;
                case "goods_payway_id":
                    goods_payway_id = goodProp.getValue();
                    break;
                case "vendor_id":
                    vendor_id = goodProp.getValue();
                    break;
                case "vendor_nm":
                    vendor_nm = goodProp.getValue();
                    break;
                case "type_id":
                    type_id = goodProp.getValue();
                    break;
                case "goods_nm":
                    goods_nm = goodProp.getValue();
                    break;
            }
        }

        paramMap.put("goods_id", goods_id);
        paramMap.put("goods_payway_id", goods_payway_id);
        paramMap.put("vendor_id", vendor_id);
        paramMap.put("vendor_nm", vendor_nm);
        paramMap.put("type_id", type_id);
        paramMap.put("goods_nm", goods_nm);

        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/ccb/login/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
        }

        if (StringUtils.isEmpty(postResult)) {
            apiResult.set(2611, "请求lsp服务器失败");
            return apiResult.toString();
        }

        apiResult = JSON.parseObject(postResult, ApiResult.class);

        if( apiResult.getCode() == 1097
                || apiResult.getCode() == 1109
                || apiResult.getCode() == 1001 ){
            UserPwd userPwd = new UserPwd();
            userPwd.setAccount(account);
            userPwd.setCompany(3);  //1、移动积分商城；2、联通积分商城；3、中信银行积分商城
            userPwd.setGmtCreate(new Date());
            userPwd.setGmtModify(new Date());
            userPwd.setStatus(1);
            userPwd.setUserId(userid);
            try {
                userPwd.setPassword(AESCryptoUtil.encrypt(password));
            } catch (CryptoException e) {
                logger.error("加密失败", e);
            }
            userPwdService.saveUserPwd(userPwd);
        }

        if("1095".equals(apiResult.getCode())){
			return apiResult.toJSONString(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误","/citic/citic_order.html?order_no="+orderno);
		}
        
        return apiResult.toString();
    }



    /**
     * 获取短信验证码
     * @param userid
     * @return   1097:"短信验证码发送成功"   1098:"短信验证码发送失败"
     */
    @RequestMapping(value = "/sms/get/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSms(Long userid) {
        logger.debug("获取中信登陆短信验证码。。。");
        ApiResult<String> apiResult = new ApiResult<>();
        
    	/*if(appConfig.isDevMode){
    		return apiResult.toJSONString(0, "");
    	}*/
        
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/ccb/resms/get/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
        }

        if (StringUtils.isEmpty(postResult)) {
            apiResult.set(2611, "请求lsp服务器失败");
            return apiResult.toString();
        }

        apiResult = JSON.parseObject(postResult, ApiResult.class);

        return apiResult.toString();
    }

    /**
     * 验证短信验证码
     *
     * @param userid
     * @return 1099:"短信验证失败"  1101:"短信验证失败"
     */
    @RequestMapping(value = "/sms/check/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String checkSms(Long userid, String smscode) {
        ApiResult<String> apiResult = new ApiResult<>();
    	
    	/*if(appConfig.isDevMode){
    		logger.info("积分变现中信银行,验证短信验证码,用户ID：{},开发模式",userid);
    		return apiResult.toJSONString(1001, "成功");
    	}*/
    	
        logger.debug("验证中信登陆短信验证码");
        //业务实现
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("smscode", smscode);
        paramMap.put("querytype", "2");
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/ccb/check/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
        }
        logger.info("验证中信登陆短信验证码返回结果：{}",postResult);
        if (StringUtils.isEmpty(postResult)) {
            apiResult.set(2611, "请求lsp服务器失败");
            return apiResult.toString();
        }

        apiResult = JSON.parseObject(postResult, ApiResult.class);

        //数据返回
        return apiResult.toString();
    }


    /**
     * 获取下单图片验证码
     *
     * @param userid
     * @return  0:成功获取图片
     */
    @RequestMapping(value = "/payvcode/get/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getkPayVcode(Long userid) {
        logger.debug("获取支付图片验证码");
        ApiResult<String> apiResult = new ApiResult<>();

        //业务实现
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/ccb/revcode/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
        }

        if (StringUtils.isEmpty(postResult)) {
            apiResult.set(2611, "请求lsp服务器失败");
            return apiResult.toString();
        }

        apiResult = JSON.parseObject(postResult, ApiResult.class);

        return apiResult.toString();
    }

    /**
     * 下单（验证支付图片验证码）
     * @param userid
     * @return 0:下单成功
     */
    @RequestMapping(value = "/order/book/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String checkPayVcode(Long userid, String payvcode, String orderno,String loginAccount,String password) {
        logger.debug("验证支付图片验证码");
        ApiResult<String> apiResult = new ApiResult<>();
        
        if(appConfig.isDevMode){
        	apiResult.setCode(1115);
        	apiResult.setData("123456");
        }else{
	        Map<String, String> paramMap = new HashMap<>();
	        paramMap.put("userid", String.valueOf(userid));
	        paramMap.put("ordervcode", payvcode);
	        String postResult = null;
	        try {
	            postResult = HttpClientUtils
	                    .getInstances()
	                    .doPost(appConfig.spiderUrl + "/api/ishop/ccb/order/1.0", "utf-8", paramMap);
	        } catch (Exception e) {
	            logger.error("请求lsp服务器失败", e);
	        }
	
	        if (StringUtils.isEmpty(postResult)) {
	            apiResult.set(2611, "请求lsp服务器失败");
	            return apiResult.toString();
	        }
	
	        apiResult = JSON.parseObject(postResult, ApiResult.class);
        }

        Integer flag = 0;
        String outNo = "";
        if( apiResult.getCode() == 1115 ){
            flag = 1;
            outNo = apiResult.getData();
        }
        
        String noFalg = orderno.substring(0, 2);
        if("TH".equals(noFalg)){
        	  orderService.processTradeOrder(flag, userid, orderno, outNo);
        }else{
        	  orderService.processOrderByThird(flag, userid, orderno, outNo);
        }
        
        if(StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
        	logger.error("绑定平安第三方积分商城失败:loginAccount:{}或password:{}未空",loginAccount,password);
        }else{
	        try {
				JSONObject info = new JSONObject();
				info.put("loginAccount", loginAccount);
				info.put("password", password);
				
				UserAddTerm userAddTerm = new UserAddTerm();
				userAddTerm.setLoginAccount(loginAccount);
				
				realizeService.ishopBinding(userid, 3L, info.toJSONString(), userAddTerm);
			} catch (Exception e) {
				logger.error("绑定平安第三方积分商城失败:" + e.getMessage(),e);
			}
        }
        
        return apiResult.toString();
    }

    private ApiResult getVCode(Long userid) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/ccb/vcode/get/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
        }

        ApiResult apiResult = null;
        try {
            apiResult = JSON.parseObject(postResult, ApiResult.class);
        } catch (Exception ignored) {

        }

        return apiResult;
    }

}
