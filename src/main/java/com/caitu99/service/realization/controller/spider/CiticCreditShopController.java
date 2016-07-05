package com.caitu99.service.realization.controller.spider;

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
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.ishop.ccb.controller.api.spider.IntegralShopCCB;
import com.caitu99.service.ishop.service.UserPwdService;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.domain.UserTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.realization.service.UserAddTermService;
import com.caitu99.service.realization.service.UserTermService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

/**
 * Created by chenhl on 2016/1/21.
 */
@Controller
@RequestMapping("/api/realization/citic/")
public class CiticCreditShopController extends BaseController {

    private static final Logger logger = LoggerFactory
            .getLogger(CiticCreditShopController.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private UserPwdService userPwdService;

    
	@Autowired
	private IntegralShopCCB integralShopCCB;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private RealizeService realizeService;
	@Autowired
	private UserTermService userTermService;
	@Autowired
	private UserAddTermService userAddTermService;
	@Autowired
	private CardTypeService cardTypeService;

    
    
    
 
	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid,String loginAccount,String password,Long realizeRecordId,String imageCode) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(loginAccount)){
			return resultJSON.toJSONString(-1, "参数loginAccount不能为空");
		}else if(StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-1, "参数password不能为空");
		}else if(null == realizeRecordId){
			return resultJSON.toJSONString(-1, "参数realizeRecordId不能为空");
		}
		User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}
		
		String phone = user.getMobile();
		if(StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(ApiResultCode.PHONE_NOT_NULL, "用户未绑定手机,请先绑定手机");
		}
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			logger.info("中信变现记录数据异常");
			return resultJSON.toJSONString(-1, "支付失败，中信银行系统维护中，请稍后再试");
		}


        /*if(appConfig.isDevMode){
        	logger.info("积分变现中信银行,登录,用户ID：{},开发模式",userid);
        	return resultJSON.toJSONString(1097," 短信验证码发送成功");
        }*/
		
		if(StringUtils.isBlank(imageCode)){
			logger.info("前端传递图片验证码为空,自动破解验证码");
			//获取图片验证码
			imageCode = this.getImageCode(userid);
			String flag = "";
			
			try {
				flag = JsonResult.getResult(imageCode, "code");
			} catch (Exception e) {
				e.printStackTrace();
				return resultJSON.toJSONString(-1,"获取图片验证码失败");
			}
			
			if("1001".equals(flag)){
				imageCode = JsonResult.getResult(imageCode, "data");
				String imageCodeNew = CommonImgCodeApi.recognizeImgCodeFromStr(imageCode);
				if(StringUtils.isBlank(imageCodeNew)){
					
					JSONObject reslutJson = new JSONObject();
					reslutJson.put("code", ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR);
					reslutJson.put("data", imageCode);
					reslutJson.put("message", "自动识别图片验证码失败");
					return reslutJson.toJSONString();
				}
				imageCode = imageCodeNew;
			}else{
				return resultJSON.toJSONString(1094,"获取图片验证码失败");
			}
		}

		//logger.info("前端传递图片验证码不为空,手动输入验证码");
		
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("userid", String.valueOf(userid));
        paramMap.put("account", loginAccount);  //身份证号
        paramMap.put("password", password);
        paramMap.put("vcode", imageCode);  //图片验证码
		
        //paramMap.put("mobile", phone);
        
		String result = this.citicLogin(loginAccount,user,paramMap); 
		
        logger.info("积分变现中信银行,登录,用户ID：{},返回数据:{}",userid,result);
		
		String code = JsonResult.getResult(result, "code");
		if("1097".equals(code)){
			//1097    短信验证码发送成功	
			return resultJSON.toJSONString(1097," 短信验证码发送成功");
		}else{
			return result;
		}
	}
	
	
	
	
	
	@RequestMapping(value="pay/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String pay(Long userid,String loginAccount,String password,String code,Long realizeRecordId) {
		ApiResult<JSONObject> resultJSON = new ApiResult<JSONObject>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(null==realizeRecordId){
			return resultJSON.toJSONString(-1, "用户支付信息不能为空");
		}
		
		Map<String,String> paramMap = new HashMap<String,String>();
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			logger.info("中信变现记录数据异常");
			return resultJSON.toJSONString(-1, "支付失败，中信银行系统维护中，请稍后再试");
		}		
		
		//获取中信积分商城商品属性
		Long realizeDetailId = realizeRecord.getRealizeDetailId();
		Map<String, Object> propMap = realizeService.getItemPayParams(realizeDetailId);
		if(null == propMap){
			logger.info("中信变现无此商品属性数据:realizeDetailId=" + realizeDetailId);
			return resultJSON.toJSONString(-1, "支付失败，中信银行系统维护中，请稍后再试");
		}
		
		List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
		if(null == goodPropList){
			logger.info("中信变现无此商品属性数据:realizeDetailId=" + realizeDetailId);
			return resultJSON.toJSONString(-1, "支付失败，中信银行系统维护中，请稍后再试");
		}
		
		
		for(GoodProp goodProp : goodPropList){
			paramMap.put(goodProp.getName(), goodProp.getValue());
		}

	    Long price = (Long)propMap.get("price");
	    Integer quantity = (Integer)propMap.get("quantity");
		
	    paramMap.put("price", price.toString());
		paramMap.put("quantity", quantity.toString());
		
	
		Long itemId = (Long)propMap.get("itemId");
		Item item = itemService.selectByPrimaryKey(itemId);
		if(null == item){
			return resultJSON.toJSONString(-1,"该商品不存在");
		}
		
		//备注
		String memo = realizeRecord.getMemo();
		if(StringUtils.isBlank(memo)){
			return resultJSON.toJSONString(-1, "数据出现异常");
		}
		
		//获取必要数据
		JSONObject json = JSON.parseObject(memo);
		
		boolean flag = false;
		String result = "";
		
		/*if(appConfig.isDevMode){
			flag = true;
			logger.info("积分变现中信银行,支付,用户ID：{},开发模式",userid);
		}else{
			if(StringUtils.isBlank(code)){
				logger.info("前端传递图片验证码为空,自动破解验证码");
				//获取图片验证码
				code = this.getPayImageCode(userid,loginAccount,paramMap);
				String flagCode = JsonResult.getResult(code, "code");
				if("1001".equals(flagCode)){
					code = JsonResult.getResult(code, "data");
					String imageCodeNew = CommonImgCodeApi.recognizeImgCodeFromStr(code);
					if(StringUtils.isBlank(imageCodeNew)){
						
						JSONObject reslutJson = new JSONObject();
						reslutJson.put("code", ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR);
						reslutJson.put("data", code);
						reslutJson.put("message", "自动识别图片验证码失败");
						return reslutJson.toJSONString();
					}
					code = imageCodeNew;
				}else if("1109".equals(flagCode)){
					return resultJSON.toJSONString(1109,"您的积分不足");
				}else if("1005".equals(flagCode)){
					return resultJSON.toJSONString(1005,"请重新登录");
				}else{
					return resultJSON.toJSONString(1111,"支付失败，中信银行系统维护中，请稍后再试");
				}
			}
			
			//中信支付提交
			result = this.citicPay(userid, code);
	        logger.info("积分变现中信银行,支付,用户ID：{},返回数据:{}",userid,result);
	        flag = JsonResult.checkResult(result,1115);
		}*/
		
		if(StringUtils.isBlank(code)){
			logger.info("前端传递图片验证码为空,自动破解验证码");
			//获取图片验证码
			code = this.getPayImageCode(userid,loginAccount,paramMap);
			String flagCode = JsonResult.getResult(code, "code");
			if("1001".equals(flagCode)){
				code = JsonResult.getResult(code, "data");
				String imageCodeNew = CommonImgCodeApi.recognizeImgCodeFromStr(code);
				if(StringUtils.isBlank(imageCodeNew)){
					
					JSONObject reslutJson = new JSONObject();
					reslutJson.put("code", ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR);
					reslutJson.put("data", code);
					reslutJson.put("message", "自动识别图片验证码失败");
					return reslutJson.toJSONString();
				}
				code = imageCodeNew;
			}else if("1109".equals(flagCode)){
				return resultJSON.toJSONString(1109,"您的积分不足");
			}else if("1005".equals(flagCode)){
				return resultJSON.toJSONString(1005,"请重新登录");
			}else{
				return resultJSON.toJSONString(1111,"支付失败，中信银行系统维护中，请稍后再试");
			}
		}
		
		//中信支付提交
		result = this.citicPay(userid, code);
        logger.info("积分变现中信银行,支付,用户ID：{},返回数据:{}",userid,result);
        flag = JsonResult.checkResult(result,1115);

		if(appConfig.isDevMode){
			flag = true;
		}
		
		if(flag){
			Long remoteId = json.getLong("remoteId");
			Integer remoteType = json.getInteger("remoteType");
			Integer isAddUserTerm = json.getInteger("isAddUserTerm"); 
			if(null == isAddUserTerm){
				return resultJSON.toJSONString(-1, "支付失败，中信银行系统维护中，请稍后再试");
			}
			
			UserAddTerm userAddTerm = new UserAddTerm();
			userAddTerm.setLoginAccount(loginAccount);
			
			JSONObject info = new JSONObject();
			info.put("loginAccount",loginAccount);
			info.put("password",password);
			
			//支付变现
			String orderNo = realizeService.realizePay(userid, realizeRecord, userAddTerm, json, info, false, remoteId, remoteType, isAddUserTerm,true);
			
			if(StringUtils.isBlank(orderNo)){
				return resultJSON.toJSONString(-1, "系统暂时出现异常,请稍后再试");
			}
			
			logger.info("积分变现中信银行,积分变现支付,用户ID：{},订单号:{},realizeRecordId:{},memo:{}",userid,orderNo,realizeRecordId,memo);
			
			//购买数量
			 quantity = realizeRecord.getQuantity();
			//商品ID
			 itemId = realizeRecord.getItemId();
			 item = itemService.selectByPrimaryKey(itemId);
			String itemName = item.getTitle();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("quantity", quantity);
			jsonObject.put("itemName", itemName);
			jsonObject.put("orderNo", orderNo);
			jsonObject.put("realizeRecordId", realizeRecordId);
			
			return resultJSON.toJSONString(0, "支付成功",jsonObject);
		}
		
		//建行失败返回错误信息
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
    private String citicLogin(String account,User user,Map<String,String> paramMap) {
        try {

			String url = appConfig.spiderUrl + "/api/ishop/ccb/login/1.0";
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("中信积分商城登录返回数据:" + result);
			
        	return result;
        } catch (Exception e) {
        	logger.error("中信积分商城登录失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, "支付失败，中信银行系统维护中，请稍后再试");
        }
    }

    
    /**
     * 下单（验证支付图片验证码）
     * @param userid
     * @return 0:下单成功
     */
    private String citicPay(Long userid, String payvcode) {
    	try {
    	    Map<String, String> paramMap = new HashMap<>();
            paramMap.put("userid", String.valueOf(userid));
            paramMap.put("ordervcode", payvcode);
            
            if(appConfig.isDevMode){//测试环境，让其支付失败
                paramMap.put("ordervcode", "123456");
            }
    		
			String url = appConfig.spiderUrl + "/api/ishop/ccb/order/1.0";
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			//1115    下单成功
			return result;
		} catch (Exception e) {
		  	logger.error("中信积分商城下单失败失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, "支付失败，中信银行系统维护中，请稍后再试");
		}

    }

    
	private String getImageCode(Long userId){
		try {
			String url = appConfig.spiderUrl + "/api/ishop/ccb/vcode/get/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("获取中信积分商城登录图片验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("获取中信积分商城登录图片验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, "支付失败，中信银行系统维护中，请稍后再试");
		}
	}
	
	private String getPayImageCode(Long userId,String loginAccount,Map<String,String> paramMap){
		try {
			String url = appConfig.spiderUrl + "/api/ishop/ccb/revcode/1.0";

			paramMap.put("userid", userId.toString());
			
			User user = userService.selectByPrimaryKey(userId);
			
    		String name ="匿名";
    		if(null != user.getNick()){
    			name=user.getNick();
    		}
    	
    		paramMap.put("mobile", user.getMobile());
            paramMap.put("addrzip","000000");  //收货地址邮编
            paramMap.put("addr","江浙省杭州市天目山路151号丁香公寓907"); //收货地址
            paramMap.put("name",name); //收货人姓名
            paramMap.put("cardId",loginAccount);   //收货人身份证
        	
            paramMap.put("imgType","1");   //收货人身份证
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("获取中信积分商城支付图片验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("获取中信积分商城支付录图片验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, "支付失败，中信银行系统维护中，请稍后再试");
		}
	}

}
