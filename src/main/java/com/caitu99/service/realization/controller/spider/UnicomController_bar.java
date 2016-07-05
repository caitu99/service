///*
// * Copyright (c) 2015-2020 by caitu99
// * All rights reserved.
// */
//package com.caitu99.service.realization.controller.spider;
//
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.caitu99.service.AppConfig;
//import com.caitu99.service.base.ApiResult;
//import com.caitu99.service.exception.UserNotFoundException;
//import com.caitu99.service.goods.domain.GoodProp;
//import com.caitu99.service.goods.domain.Item;
//import com.caitu99.service.goods.service.ItemService;
//import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
//import com.caitu99.service.integral.domain.ManualLogin;
//import com.caitu99.service.integral.service.CardTypeService;
//import com.caitu99.service.integral.service.ManualLoginService;
//import com.caitu99.service.ishop.ccb.controller.api.spider.IntegralShopCCB;
//import com.caitu99.service.realization.domain.RealizeRecord;
//import com.caitu99.service.realization.domain.UserAddTerm;
//import com.caitu99.service.realization.service.RealizeService;
//import com.caitu99.service.realization.service.UserAddTermService;
//import com.caitu99.service.realization.service.UserTermService;
//import com.caitu99.service.transaction.service.OrderService;
//import com.caitu99.service.user.domain.User;
//import com.caitu99.service.user.service.UserService;
//import com.caitu99.service.utils.ApiResultCode;
//import com.caitu99.service.utils.file.CommonImgCodeApi;
//import com.caitu99.service.utils.http.HttpClientUtils;
//import com.caitu99.service.utils.json.JsonResult;
//
///**
// * Created by Administrator on 2016/1/14.
// */
//@Controller
//@RequestMapping("/api/realization/unicom/")
//public class UnicomController_bar {
//    private static final Logger logger = LoggerFactory
//            .getLogger(UnicomController_bar.class);
//
//    
//	@Autowired
//	private IntegralShopCCB integralShopCCB;
//	@Autowired
//	private OrderService orderService;
//	@Autowired
//	private UserService userService;
//	@Autowired
//	private ItemService itemService;
//	@Autowired
//	private RealizeService realizeService;
//	@Autowired
//	private UserTermService userTermService;
//	@Autowired
//	private UserAddTermService userAddTermService;
//	@Autowired
//	private CardTypeService cardTypeService;
//	@Autowired
//	ManualLoginService manualLoginService;
//	
//    @Autowired
//    private AppConfig appConfig;
//
//    
//    
//    
////	@SuppressWarnings("unchecked")
////	@RequestMapping(value="init/submit/1.0", produces="application/json;charset=utf-8")
////	@ResponseBody
////	public String init(Long userid,Long realizeRecordId){
////		
////		ApiResult<String> resultJSON = new ApiResult<String>();
////		
////		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
////		if(null == realizeRecord){
////			return resultJSON.toJSONString(-2, "数据出现异常");
////		}
////		
////		//获取积分商城商品属性
////		Long realizeDetailId = realizeRecord.getRealizeDetailId();
////		Map<String, Object> propMap = realizeService.getItemPayParams(realizeDetailId);
////		if(null == propMap){
////			return resultJSON.toJSONString(-3,"无此商品属性数据");
////		}
////		
////		List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
////		if(null == goodPropList){
////			return resultJSON.toJSONString(-3,"无此商品属性数据");
////		}
////		
////		Map<String,String> paramMap = new HashMap<String,String>();
////		for(GoodProp goodProp : goodPropList){
////			paramMap.put(goodProp.getName(), goodProp.getValue());
////		}
////
////	    Long price = (Long)propMap.get("price");
////	    Integer quantity = (Integer)propMap.get("quantity");
////		
////		paramMap.put("nums", quantity.toString());
////		//paramMap.put("productPrice", String.valueOf(price * quantity));
////		paramMap.put("price", price.toString());
////		
////		paramMap.put("userid", String.valueOf(userid));
////        
////		
////		String result = this.unicomInit(userid, paramMap);
////		
////        logger.info("积分变现联通,初始化登录,用户ID：{},返回数据:{}",userid,result);
////		
////           // apiResult.set(3803, "lsp服务器返回的数据无法解析为json", postResult);
////		
////		String code = JsonResult.getResult(result, "code");
////		if("1207".equals(code)){
////			//1207 联通商城初始化完成
////			return resultJSON.toJSONString(1207,"联通商城初始化完成");
////		}else if("1214".equals(code)){
////			//1214    请获取短信验证码
////			return resultJSON.toJSONString(1214,"请获取短信验证码");
////		}
////		return resultJSON.toJSONString(-1, result);
////	}
//    
//	@RequestMapping(value="init/submit/1.0", produces="application/json;charset=utf-8")
//	@ResponseBody
//	public String init(Long userid,Long realizeRecordId){
//		
//		ApiResult<String> resultJSON = new ApiResult<String>();
//		
//		Map<String,String> paramMap = new HashMap<String,String>();
//		paramMap.put("userid", String.valueOf(userid));
//		
//		String result = this.unicomInit(userid, paramMap);
//		
//        logger.info("积分变现联通,初始化登录,用户ID：{},返回数据:{}",userid,result);
//		
//           // apiResult.set(3803, "lsp服务器返回的数据无法解析为json", postResult);
//		
//		String code = JsonResult.getResult(result, "code");
//		if("1207".equals(code)){
//			//1207 联通商城初始化完成
//			return resultJSON.toJSONString(1207,"联通商城初始化完成");
//		}else if("1214".equals(code)){
//			//1214    请获取短信验证码
//			return resultJSON.toJSONString(1214,"请获取短信验证码");
//		}
//		return resultJSON.toJSONString(-1, result);
//	}
//	
//	
//    
//	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
//	@ResponseBody
//	public String login(Long userid,String loginAccount,String password,Long realizeRecordId,String imageCode) {
//	
//		ApiResult<String> resultJSON = new ApiResult<String>();
//		
//		if(null == userid){
//			return resultJSON.toJSONString(-1, "参数userid不能为空");
//		}else if(StringUtils.isBlank(loginAccount)){
//			return resultJSON.toJSONString(-1, "参数loginAccount不能为空");
//		}else if(StringUtils.isBlank(password)){
//			return resultJSON.toJSONString(-1, "参数password不能为空");
//		}else if(null == realizeRecordId){
//			return resultJSON.toJSONString(-1, "参数realizeRecordId不能为空");
//		}
//		
//		User user = userService.selectByPrimaryKey(userid);
//		if(null == user){
//			throw new UserNotFoundException(-1, "用户不存在");
//		}
//		
//		if(StringUtils.isBlank(imageCode)){
//			logger.info("前端传递图片验证码为空,自动破解验证码");
//			//获取图片验证码
//			imageCode  = this.getImgCode(userid);
//			String flagImage = JsonResult.getResult(imageCode, "code");
//			if("1001".equals(flagImage)){
//				imageCode = JsonResult.getResult(imageCode, "data");
//				String imageCodeNew = CommonImgCodeApi.recognizeImgCodeFromStr(imageCode);
//				if(StringUtils.isBlank(imageCodeNew)){
//					return resultJSON.toJSONString(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR,"破解图片验证码失败",imageCode);
//				}
//				imageCode = imageCodeNew;
//			}
//			else if("1005".equals(flagImage))	//初始化已过期
//			{
//				return resultJSON.toJSONString(1005,"初始化已过期");
//			}
//			else{
//				return resultJSON.toJSONString(-1,"获取图片验证码失败");
//			}
//		}
//
//		Map<String,String> paramMap = new HashMap<String,String>();
//		
//        paramMap.put("userid", String.valueOf(userid));
//        paramMap.put("account", loginAccount);
//        paramMap.put("password", password);
//        paramMap.put("vcode", imageCode);
//        paramMap.put("self", "false");
//		
//        
//		String result = this.unicomLogin(userid, paramMap);
//		
//        logger.info("积分变现联通,登录,用户ID：{},返回数据:{}",userid,result);
//		
//		String resultCode = JsonResult.getResult(result, "code");
//		if("1214".equals(resultCode)){
//			RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
//			if(null == realizeRecord){
//				return resultJSON.toJSONString(-2, "数据出现异常");
//			}
//			
//			//获取积分商城商品属性
//			Long realizeDetailId = realizeRecord.getRealizeDetailId();
//			Map<String, Object> propMap = realizeService.getItemPayParams(realizeDetailId);
//			if(null == propMap){
//				return resultJSON.toJSONString(-3,"无此商品属性数据");
//			}
//			
//			List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
//			if(null == goodPropList){
//				return resultJSON.toJSONString(-3,"无此商品属性数据");
//			}
//			
//			Map<String,String> paramMap2 = new HashMap<String,String>();
//			for(GoodProp goodProp : goodPropList){
//				paramMap2.put(goodProp.getName(), goodProp.getValue());
//			}
//	
//		    Long price = (Long)propMap.get("price");
//		    Integer quantity = (Integer)propMap.get("quantity");
//			
//			paramMap2.put("nums", quantity.toString());
//			//paramMap.put("productPrice", String.valueOf(price * quantity));
//			paramMap2.put("price", price.toString());
//			
//			
//			result = this.unicomSms(userid,paramMap2);
//			resultCode = JsonResult.getResult(result, "code");
//			if("1215".equals(resultCode)){
//				return resultJSON.toJSONString(1215, "短信验证发送成功");
//			}
//			return resultJSON.toJSONString(1214, "登录成功");
//		}else if("1052".equals(resultCode)){
//			return resultJSON.toJSONString(3814, "图片验证码错误");
//		}else if("1067".equals(resultCode)){
//			return resultJSON.toJSONString(3813, "用户名或密码错误");
//		}
//		else if("1211".equals(resultCode))
//        {
//			return resultJSON.toJSONString(3820, "积分不足以兑换该商品",JsonResult.getResult(result, "data"));
//        }
//        else if("1230".equals(resultCode))
//        {
//        	return resultJSON.toJSONString(3836, "账号登录异常，请稍后再试");
//        }
//        else if("1231".equals(resultCode))
//        {
//        	return resultJSON.toJSONString(3837, "登录过于频繁，为保障您的账号安全，请稍后再试");
//        }
//        else if("1232".equals(resultCode))
//        {
//        	return resultJSON.toJSONString(3838, "您的账号登录受限，请联系客服");
//        }
//		else if("1234".equals(resultCode))
//		{
//			return resultJSON.toJSONString(3839, "不支持简单密码登录");
//		}
//		
//		return resultJSON.toJSONString(-1, result);
//	}
//	
//	
//	
//	
//	
//	@RequestMapping(value="pay/submit/1.0", produces="application/json;charset=utf-8")
//	@ResponseBody
//	public String pay(Long userid,String loginAccount,String password,String code,Long realizeRecordId) {
//		ApiResult<JSONObject> resultJSON = new ApiResult<JSONObject>();
//		
//		if(null == userid){
//			return resultJSON.toJSONString(-1, "参数userid不能为空");
//		}else if(null==realizeRecordId){
//			return resultJSON.toJSONString(-2, "用户支付信息不能为空");
//		}else if(StringUtils.isBlank(code)){
//			return resultJSON.toJSONString(-1, "参数code不能为空");
//		}
//		
//		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
//		if(null == realizeRecord){
//			return resultJSON.toJSONString(-2, "数据出现异常");
//		}
//		
//		//备注
//		String memo = realizeRecord.getMemo();
//		if(StringUtils.isBlank(memo)){
//			return resultJSON.toJSONString(-2, "数据出现异常");
//		}
//		
//		//获取必要数据
//		JSONObject json = JSON.parseObject(memo);
//		
//		//支付提交
//		String result = this.unicomPay(userid, code);
//		
//        logger.info("积分变现联通,支付,用户ID：{},返回数据:{}",userid,result);
//		
//		String resultCode = JsonResult.getResult(result, "code");
//		//String resultCode = "1222";
//		if("1222".equals(resultCode)){
//			Long remoteId = json.getLong("remoteId");
//			Integer remoteType = json.getInteger("remoteType");
//			Integer isAddUserTerm = json.getInteger("isAddUserTerm"); 
//			if(null == isAddUserTerm){
//				return resultJSON.toJSONString(-2, "数据出现异常");
//			}
//			
//			UserAddTerm userAddTerm = new UserAddTerm();
//			userAddTerm.setLoginAccount(loginAccount);
//			
//			JSONObject info = new JSONObject();
//			info.put("loginAccount",loginAccount);
//			info.put("password",password);
//			info.put("reservedPhone",loginAccount);
//			
//			//支付
//			String orderNo = realizeService.realizePay(userid, realizeRecord, userAddTerm, json, info, false, remoteId, remoteType, isAddUserTerm,true);
//			
//			logger.info("积分变现联通,积分变现支付,用户ID：{},订单号:{},realizeRecordId:{},memo:{}",userid,orderNo,realizeRecordId,memo);
//			
//			//购买数量
//			Integer quantity = realizeRecord.getQuantity();
//			//商品ID
//			Long itemId = realizeRecord.getItemId();
//			Item item = itemService.selectByPrimaryKey(itemId);
//			String itemName = item.getTitle();
//			
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("quantity", quantity);
//			jsonObject.put("itemName", itemName);
//			jsonObject.put("orderNo", orderNo);
//			
//			try {
//				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 6L);
//				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
//				thread.run();
//			} catch (Exception e) {
//				logger.warn("积分变现联通，自动更新失败：{}",e);
//			}
//			
//			return resultJSON.toJSONString(0, "支付成功",jsonObject);
//		}else if("1218".equals(resultCode)) {
//			return resultJSON.toJSONString(3828, "联通商城提交订单短信验证失败");
//        }else if("1225".equals(resultCode))
//        {
//        	return resultJSON.toJSONString(3831, "您的积分余额不足");
//        }
//		else if("1219".equals(resultCode))
//		{
//			return resultJSON.toJSONString(3824, "您输入的随机码不正确，请重新获取随机码");
//		}
//		
//		//返回错误信息
//		String message = JsonResult.getResult(result, "message");
//		return resultJSON.toJSONString(-1, message);
//	}
//	
//	
//    
//    
//	private String unicomInit(Long userId,Map<String,String> paramMap){
//		try {
//			String url = appConfig.spiderUrl + "/api/shop/unicom/init/1.0";
//			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
//			logger.info("联通登录初始化返回数据:" + result);
//			return result;
//		} catch (Exception e) {
//			logger.error("联通登录初始化失败:" + e.getMessage(),e);
//			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
//			return result.toJSONString(-1, e.getMessage());
//		}
//	}
//	
//	public String getImgCode(Long userid){
//		try {
//			String url = appConfig.spiderUrl + "/api/shop/unicom/getvcode/1.0";
//	        Map<String, String> paramMap = new HashMap<>();
//	        paramMap.put("userid", String.valueOf(userid));
//			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
//			logger.info("联通获取图片验证码返回数据:" + result);
//			return result;
//		} catch (Exception e) {
//			logger.error("联通获取图片验证码失败:" + e.getMessage(),e);
//			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
//			return result.toJSONString(-1, e.getMessage());
//		}
//	}
//    
//    
//	private String unicomLogin(Long userid,Map<String,String> paramMap){
//		try {
//			String url = appConfig.spiderUrl + "/api/shop/unicom/login/1.0";
//			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
//			logger.info("联通登录成功返回数据:" + result);
//			return result;
//		} catch (Exception e) {
//			logger.error("联通登录失败:" + e.getMessage(),e);
//			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
//			return result.toJSONString(-1, e.getMessage());
//		}
//	}
//	
////	private String unicomSms(Long userid){
////		try {
////			String url = appConfig.spiderUrl + "/api/shop/unicom/getsmscode/1.0";
////	        Map<String, String> paramMap = new HashMap<>();
////	        paramMap.put("userid", String.valueOf(userid));
////			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
////			logger.info("联通登录成功返回数据:" + result);
////			return result;
////		} catch (Exception e) {
////			logger.error("联通登录失败:" + e.getMessage(),e);
////			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
////			return result.toJSONString(-1, e.getMessage());
////		}
////	}
//	
//	private String unicomSms(Long userid,Map<String, String> paramMap){
//		try {
//			String url = appConfig.spiderUrl + "/api/shop/unicom/getsmscode/1.0";
//	        paramMap.put("userid", String.valueOf(userid));
//			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
//			logger.info("联通登录成功返回数据:" + result);
//			return result;
//		} catch (Exception e) {
//			logger.error("联通登录失败:" + e.getMessage(),e);
//			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
//			return result.toJSONString(-1, e.getMessage());
//		}
//	}
//	
//	
//	private String unicomPay(Long userid,String smscode){
//		try {
//			String url = appConfig.spiderUrl + "/api/shop/unicom/submit/1.0";
//	        Map<String, String> paramMap = new HashMap<>();
//	        paramMap.put("userid", String.valueOf(userid));
//	        paramMap.put("smscode", smscode);
//			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
//			logger.info("联通登录成功返回数据:" + result);
//			return result;
//		} catch (Exception e) {
//			logger.error("联通登录失败:" + e.getMessage(),e);
//			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
//			return result.toJSONString(-1, e.getMessage());
//		}
//	}
//
//}
