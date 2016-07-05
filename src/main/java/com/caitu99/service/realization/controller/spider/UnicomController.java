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
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.ishop.unicom.controller.api.spider.IntegralShopUnicom;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/realization/unicom/")
public class UnicomController {
	
    private static final Logger logger = LoggerFactory.getLogger(UnicomController.class);
    
	@Autowired
	private IntegralShopUnicom integralShopUnicom;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private RealizeService realizeService;
	
	@Autowired
	private ManualLoginService manualLoginService;
	
	@Autowired
	private AppConfig appConfig;
	
    /**
     * 登录初始化
     * @Description: (方法职责详细描述,可空)  
     * @Title: init 
     * @param userid	用户ID
     * @return
     * @date 2016年3月22日 下午4:04:12  
     * @author xiongbin
     */
	@RequestMapping(value="init/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String init(Long userid){
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		String result = integralShopUnicom.init(userid);
		
		boolean flag = JsonResult.checkResult(result, 1207);
		if(flag){
			return resultJSON.toJSONString(0, "success");
		}
		
		return resultJSON.toJSONString(-1, result);
	}
	
	/**
	 * 获取图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年3月22日 下午5:16:35  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/imageCode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getImageCode(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		String imageCode = integralShopUnicom.getImageCode(userid);
		boolean flagImage = JsonResult.checkResult(imageCode,1001);
		if(flagImage){
			imageCode = JsonResult.getResult(imageCode, "data");
			return resultJSON.toJSONString(0,"获取验证码成功",imageCode);
		}else{
			return resultJSON.toJSONString(-1,"获取验证码失败，联通系统维护中，请稍后再试");
		}
	}
	
    /**
     * 登录
     * @Description: (方法职责详细描述,可空)  
     * @Title: login 
     * @param userid		用户ID
     * @param loginAccount	手机号码
     * @param password		服务密码
     * @param imageCode		图片验证码
     * @return
     * @date 2016年3月22日 下午4:12:45  
     * @author xiongbin
     */
	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid,String loginAccount,String password,String imageCode,Long realizeRecordId) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(loginAccount)){
			return resultJSON.toJSONString(-1, "参数phone不能为空");
		}else if(StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-1, "参数password不能为空");
		}else if(null == realizeRecordId){
			return resultJSON.toJSONString(-1, "参数realizeRecordId不能为空");
		}
		
		User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}

		boolean flag = false;
		String result = "";
		
		if(appConfig.isDevMode){
			logger.info("积分变现联通,登录,用户ID：{},开发模式",userid);
			flag = true;
		}else{
			if(StringUtils.isBlank(imageCode)){
				logger.info("前端传递图片验证码为空,自动破解验证码");
				//获取图片验证码
				imageCode = integralShopUnicom.getImageCode(userid);
				boolean flagImage = false;
				try{
					flagImage = JsonResult.checkResult(imageCode,1001);
				}catch(com.alibaba.fastjson.JSONException ee){
					ee.printStackTrace();
					return resultJSON.toJSONString(-1, "联通系统维护中，请稍后再试");
				}
	
				if(flagImage){
					imageCode = JsonResult.getResult(imageCode, "data");
					String imageCodeNew = CommonImgCodeApi.recognizeImgCodeFromStr(imageCode);
					if(StringUtils.isBlank(imageCodeNew)){
						return resultJSON.toJSONString(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR,"破解图片验证码失败",imageCode);
					}
					imageCode = imageCodeNew;
				}else{
					return resultJSON.toJSONString(-1,"获取图片验证码失败");
				}
			}else{
				logger.info("前端传递图片验证码不为空,手动输入验证码");
			}
			
			result = integralShopUnicom.login(userid, loginAccount, password, imageCode);
			try{
				flag = JsonResult.checkResult(result,1214);
			}catch(com.alibaba.fastjson.JSONException ee){
				ee.printStackTrace();
				return resultJSON.toJSONString(-1, "联通系统维护中，请稍后再试");
			}
		}

		if(flag){
			result = this.paySendSMS(userid, realizeRecordId);
			flag = JsonResult.checkResult(result);
			if(flag){
				return resultJSON.toJSONString(0, "登录成功");
			}
		}

		String code = JsonResult.getResult(result, "code");
		if("1052".equals(code)){
			imageCode = integralShopUnicom.getImageCode(userid);
			boolean flagImage = JsonResult.checkResult(imageCode,1001);
			if(flagImage){
				imageCode = JsonResult.getResult(imageCode, "data");
			}else{
				return resultJSON.toJSONString(-1,"获取图片验证码失败");
			}
			return resultJSON.toJSONString(ApiResultCode.IMAGECODE_ERROR, "图片验证码不正确",imageCode);
		}else if("1067".equals(code) || "1230".equals(code) || "1013".equals(code) || "1005".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误","/realize_unicom/login.html?realizeRecordId="+realizeRecordId+"&loginAccount="+loginAccount);
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);		
	}
	
	/**
	 * 下单并发送短信验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySendSMS 
	 * @param userid				用户ID
	 * @param realizeRecordId		积分变现记录ID
	 * @return
	 * @date 2016年3月22日 下午4:21:22  
	 * @author xiongbin
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="pay/send/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String paySendSMS(Long userid,Long realizeRecordId) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(null == realizeRecordId){
			return resultJSON.toJSONString(-1, "参数realizeRecordId不能为空");
		}
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			return resultJSON.toJSONString(-2, "数据出现异常");
		}
		
		//获取积分商城商品属性
		Long realizeDetailId = realizeRecord.getRealizeDetailId();
		Map<String, Object> propMap = realizeService.getItemPayParams(realizeDetailId);
		if(null == propMap){
			return resultJSON.toJSONString(-3,"无此商品属性数据");
		}
		
		List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
		if(null == goodPropList){
			return resultJSON.toJSONString(-3,"无此商品属性数据");
		}
		
		Map<String,String> paramMap = new HashMap<String,String>();
		for(GoodProp goodProp : goodPropList){
			paramMap.put(goodProp.getName(), goodProp.getValue());
		}

	    Long price = (Long)propMap.get("price");
	    Integer quantity = (Integer)propMap.get("quantity");
		
		paramMap.put("nums", quantity.toString());
//		paramMap.put("productPrice", String.valueOf(price * quantity));
		paramMap.put("price", price.toString());
		
		if(appConfig.isDevMode){
			logger.info("积分变现联通,发送支付验证码,用户ID：{},开发模式",userid);
			return resultJSON.toJSONString(0, "短信发送成功");
		}
		
		//联通支付发送验证码
		String result = integralShopUnicom.paySendSMS(userid, paramMap);
		boolean flag = false;
		try{
			flag = JsonResult.checkResult(result,1215);
		}catch(com.alibaba.fastjson.JSONException ee){
			ee.printStackTrace();
			return resultJSON.toJSONString(-1, "短信发送失败，联通系统维护中，请稍后再试");
		}

		if(flag){
			return resultJSON.toJSONString(0, "短信发送成功");
		}
		
        String code = JsonResult.getResult(result, "code");
        if("1005".equals(code)){
        	return resultJSON.toJSONString(ApiResultCode.NOT_LOGIN_STATUS, "您的登录状态已过期,请重新登录");
        }

		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userid				用户ID
	 * @param code				            短信验证码
	 * @param realizeRecordId		积分变现记录ID
	 * @param loginAccount			手机号码
	 * @param password				服务密码
	 * @return
	 * @date 2016年3月22日 下午4:37:43  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String pay(Long userid,String code,Long realizeRecordId,String loginAccount,String password) {
		ApiResult<JSONObject> resultJSON = new ApiResult<JSONObject>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(null==realizeRecordId || StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-2, "用户支付信息不能为空");
		}else if(StringUtils.isBlank(code)){
			return resultJSON.toJSONString(-1, "参数phoneCode不能为空");
		}
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			return resultJSON.toJSONString(-2, "数据出现异常");
		}
		
		//备注
		String memo = realizeRecord.getMemo();
		if(StringUtils.isBlank(memo)){
			return resultJSON.toJSONString(-2, "数据出现异常");
		}
		
		//获取必要数据
		JSONObject json = JSON.parseObject(memo);
		

		boolean flag = false;
		String result = "";
		
		if(appConfig.isDevMode){
			flag = true;
	        logger.info("积分变现联通,支付,用户ID：{},开发模式",userid);
		}else{
			//联通支付提交
			result = integralShopUnicom.pay(userid, code);
	        logger.info("积分变现联通,支付,用户ID：{},返回数据:{}",userid,result);
	        try{
				flag = JsonResult.checkResult(result,1222);
			}catch(com.alibaba.fastjson.JSONException ee){
				ee.printStackTrace();
				return resultJSON.toJSONString(-1, "支付失败，联通系统维护中，请稍后再试");
			}
		}

        if(flag){
        	Long remoteId = json.getLong("remoteId");
			Integer remoteType = json.getInteger("remoteType");
			Integer isAddUserTerm = json.getInteger("isAddUserTerm"); 
			if(null == isAddUserTerm){
				return resultJSON.toJSONString(-2, "数据出现异常");
			}
			
			UserAddTerm userAddTerm = new UserAddTerm();
			userAddTerm.setLoginAccount(loginAccount);
			
			JSONObject info = new JSONObject();
			info.put("phone",loginAccount);
			info.put("password",password);
			
			//支付
			String orderNo = realizeService.realizePay(userid, realizeRecord, userAddTerm, 
															json, info, false, remoteId, remoteType, isAddUserTerm,true);
			
			if(StringUtils.isBlank(orderNo)){
				return resultJSON.toJSONString(-1, "系统暂时出现异常,请稍后再试");
			}
			
			logger.info("积分变现联通,积分变现支付,用户ID：{},订单号:{},realizeRecordId:{},memo:{}",userid,orderNo,realizeRecordId,memo);
			
			//购买数量
			Integer quantity = realizeRecord.getQuantity();
			//商品ID
			Long itemId = realizeRecord.getItemId();
			Item item = itemService.selectByPrimaryKey(itemId);
			String itemName = item.getTitle();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("quantity", quantity);
			jsonObject.put("itemName", itemName);
			jsonObject.put("orderNo", orderNo);
			jsonObject.put("realizeRecordId", realizeRecordId);
			
			try {
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 6L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("积分变现联通，自动更新失败：{}",e);
			}
			
			return resultJSON.toJSONString(0, "支付成功",jsonObject);
        }
		
		//返回错误信息
        result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 处理返回值
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: disposeCode 
	 * @param reslut
	 * @return
	 * @date 2016年2月1日 下午4:53:37  
	 * @author xiongbin
	 */
	private String disposeCode(String reslut){
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(JsonResult.checkResult(reslut)){
			return reslut;
		}
		
		String code = JsonResult.getResult(reslut, "code");
		if("1209".equals(code) || "1212".equals(code) || "1213".equals(code) || "1006".equals(code) || "1221".equals(code)
			 					|| "1200".equals(code) || "1202".equals(code) || "1201".equals(code) || "1203".equals(code)
			 					 						|| "1227".equals(code) || "1229".equals(code) || "1223".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.SYSTEM_BUSY, "联通系统维护中，请稍后再试");
		}
		
		return reslut;
	}
}
