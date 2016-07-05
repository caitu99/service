package com.caitu99.service.ishop.pab.controller.api;

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

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.ishop.pab.controller.api.spider.IntegralShopPAB;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 平安积分商城
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PAbShopController 
 * @author xiongbin
 * @date 2016年4月1日 下午3:13:31 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/ishop/pab/")
public class PABShopController {
    private final static Logger logger = LoggerFactory.getLogger(PABShopController.class);

    @Autowired
    private IntegralShopPAB integralShopPAB;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ManualLoginService manualLoginService;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private RealizeService realizeService;

    /**
     * 获取登录图片验证码
     * @Description: (方法职责详细描述,可空)  
     * @Title: getImageCode 
     * @param userid	用户ID
     * @return
     * @date 2016年4月1日 下午3:28:45  
     * @author xiongbin
     */
	@RequestMapping(value="login/imageCode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getImageCode(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		String imageCode = integralShopPAB.getImageCodeThird(userid);
		boolean flagImage = JsonResult.checkResult(imageCode,ApiResultCode.NEED_INOUT_IMAGECODE);
		if(flagImage){
			imageCode = JsonResult.getResult(imageCode, "data");
			return resultJSON.toJSONString(0,"获取验证码成功",imageCode);
		}else{
			return resultJSON.toJSONString(-1,"获取验证码失败");
		}
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid			用户ID
	 * @param loginAccount		登录账号
	 * @param password			密码
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2016年4月6日 上午10:24:48  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid,String loginAccount,String password,String imageCode,String orderNo) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(loginAccount)){
			return resultJSON.toJSONString(-1, "参数loginAccount不能为空");
		}else if(StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-1, "参数password不能为空");
		}else if(StringUtils.isBlank(orderNo)){
			return resultJSON.toJSONString(-1, "参数orderNo不能为空");
		}
		
		boolean flag = false;
		String result = "";
		
		if(appConfig.isDevMode){
			Random r = new Random();
	    	int i = r.nextInt(100) % 2;
	    	if(i == 0){
		    	flag = true;
	    	}else{
	    		imageCode = this.getImageCode(userid);
	    		boolean flagImage = JsonResult.checkResult(imageCode);
				if(flagImage){
					imageCode = JsonResult.getResult(imageCode, "data");
					return resultJSON.toJSONString(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR,"破解图片验证码失败",imageCode);
				}else{
					return resultJSON.toJSONString(-1,"获取图片验证码失败");
				}
	    	}
		}else{
			if(StringUtils.isBlank(imageCode)){
				logger.info("前端传递图片验证码为空,自动破解验证码");
				//获取图片验证码
				imageCode = this.getImageCode(userid);
				boolean flagImage = JsonResult.checkResult(imageCode);
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
			
			result = integralShopPAB.loginThird(userid, loginAccount, password, imageCode);
			logger.info("平安银行第三方商城,登录,用户ID：{},返回数据:{}",userid,result);
			flag = JsonResult.checkResult(result);
		}
		
		if(flag){
			result = this.order(userid, orderNo);
			String code = JsonResult.getResult(result, "code");
			if(ApiResultCode.PAB_ORDER_PASSWORD.toString().equals(code) 
					|| ApiResultCode.PAB_ORDER_PHONECODE.toString().equals(code)
					|| ApiResultCode.PAB_ORDER_PASSWORD_AND_PHONECODE.toString().equals(code)){
				
				return resultJSON.toJSONString(0,"SUCCEED",code);
			}
		}
		
		String code = JsonResult.getResult(result, "code");
		if("1010".equals(code)){
			imageCode = this.getImageCode(userid);
			boolean flagImage = JsonResult.checkResult(imageCode);
			if(flagImage){
				imageCode = JsonResult.getResult(imageCode, "data");
			}else{
				return resultJSON.toJSONString(-1,"获取图片验证码失败");
			}
			return resultJSON.toJSONString(ApiResultCode.IMAGECODE_ERROR,"图片验证码不正确",imageCode);
		}else if("3005".equals(code) || "1012".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 下单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid		用户ID
	 * @param orderNo		订单号
	 * @return
	 * @date 2016年4月6日 下午12:02:22  
	 * @author xiongbin
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="order/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String order(Long userid,String orderNo) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(orderNo)){
			return resultJSON.toJSONString(-1, "参数orderNo不能为空");
		}
		
		String result = "";
		String code = "";
		
		if(appConfig.isDevMode){
			Random r = new Random();
	    	int i = r.nextInt(100) % 2;
	    	if(i == 0){
				code = "2010";
	    	}else{
	    		i = r.nextInt(100) % 2;
	    		if(i == 0){
					code = "2001";
		    	}else{
					code = "2007";
		    		
		    	}
	    	}
		}else{
			//获取商品属性
			Map<String, Object> propMap = orderService.getGoodByTrade(orderNo,userid);
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
			
			paramMap.put("quantity", quantity.toString());
			paramMap.put("productPrice", String.valueOf(price * quantity));
			
			//查询商品是否支持自由交易,是否需要物流
			Long itemId = (Long)propMap.get("itemId");
			Item item = itemService.selectByPrimaryKey(itemId);
			if(null == item){
				return resultJSON.toJSONString(-4,"该商品不存在");
			}
			
//			if(item.getIsFreeTrade().equals(1)){
//				OrderAddress orderAddress = (OrderAddress)propMap.get("orderAddress");
//				paramMap.put("province", orderAddress.getProvince());
//				paramMap.put("city", orderAddress.getCity());
//				paramMap.put("areaName", orderAddress.getArea());
//				paramMap.put("consigneeName", orderAddress.getName());
//				paramMap.put("addressDetail", orderAddress.getDetailed());
//				paramMap.put("postCode", orderAddress.getZipCode());
//				paramMap.put("mobile", orderAddress.getMobile());
//			}else{
//				paramMap.put("mobile", phone);
//			}
			
			paramMap.put("cellphone", "");
			result = integralShopPAB.orderThird(userid, paramMap);
			logger.info("平安银行第三方商城,下单,用户ID：{},返回数据:{}",userid,result);
			code = JsonResult.getResult(result, "code");
		}
		
		if("2010".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.PAB_ORDER_PASSWORD, "下单成功,输入支付密码");
		}else if("2001".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.PAB_ORDER_PHONECODE, "下单成功,短信验证码发送成功");
		}else if("2007".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.PAB_ORDER_PASSWORD_AND_PHONECODE, "下单成功,请输入支付密码及短信验证码");
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 发送支付验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySend 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年4月6日 下午12:18:36  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/send/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String paySend(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		if(appConfig.isDevMode){
			return resultJSON.toJSONString(0, "SUCCEED");
		}
		
		String result = integralShopPAB.paySendThird(userid);
		logger.info("平安银行第三方商城,发送支付验证码,用户ID：{},返回数据:{}",userid,result);
		
		String code = JsonResult.getResult(result, "code");
		if("2001".equals(code) || "2007".equals(code)){
			return resultJSON.toJSONString(0, "SUCCEED");
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userid			用户ID
	 * @param phoneCode			短信验证码
	 * @param payPassWord		支付密码
	 * @param orderNo			订单号
	 * @return
	 * @date 2016年4月6日 下午12:29:26  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String pay(Long userid,String phoneCode,String payPassWord,String orderNo,String loginAccount,String password) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(orderNo)){
			return resultJSON.toJSONString(-2, "参数orderNo不能为空");
		}else if(StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-2, "参数不全");
		}
		
		String result = "";
		boolean flag = false;
		
		if(appConfig.isDevMode){
			flag = true;
		}else{
			logger.info("平安银行第三方商城,支付,用户ID：{},phoneCode:{},payPassWord:{},orderNo:{}",userid,phoneCode,payPassWord,orderNo);
			result = integralShopPAB.payThird(userid, phoneCode, payPassWord);
			logger.info("平安银行第三方商城,支付,用户ID：{},返回数据:{}",userid,result);
			flag = JsonResult.checkResult(result);
		}
		
		if(flag){
			String thredOrderNo = "";
			
			if(!appConfig.isDevMode){
				thredOrderNo = JsonResult.getResult(result, "data");
				
				try{
					List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 17L);
					ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
					thread.run();
				} catch (Exception e) {
					logger.warn("积分变现平安银行，自动更新失败：{}",e);
				}
			}
			
			orderService.processTradeOrder(1,userid,orderNo,thredOrderNo);
			
			try {
				JSONObject info = new JSONObject();
				info.put("loginAccount", loginAccount);
				info.put("password", password);
				
				UserAddTerm userAddTerm = new UserAddTerm();
				userAddTerm.setLoginAccount(loginAccount);
				
				realizeService.ishopBinding(userid, 8L, info.toJSONString(), userAddTerm);
			} catch (Exception e) {
				logger.error("绑定平安第三方积分商城失败:" + e.getMessage(),e);
			}
			
			return resultJSON.toJSONString(0, "SUCCEED");
		}
		
		orderService.processTradeOrder(0,userid,orderNo,"");
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 获取忘记密码图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getForgotImageCode 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月7日 下午2:26:27  
	 * @author xiongbin
	 */
	@RequestMapping(value="forgot/imageCode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getForgotImageCode(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		String imageCode = integralShopPAB.getForgetImageCode(userid);
		boolean flagImage = JsonResult.checkResult(imageCode);
		if(flagImage){
			imageCode = JsonResult.getResult(imageCode, "data");
			return resultJSON.toJSONString(0,"获取验证码成功",imageCode);
		}else{
			return resultJSON.toJSONString(-1,"获取验证码失败");
		}
	}
	
	/**
	 * 获取忘记密码第二张图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getForgotImageCode2 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月7日 下午4:11:56  
	 * @author xiongbin
	 */
	@RequestMapping(value="forgot/imageCode2/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getForgotImageCode2(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		String imageCode = integralShopPAB.getForgetImageCode2(userid);
		boolean flagImage = JsonResult.checkResult(imageCode);
		if(flagImage){
			imageCode = JsonResult.getResult(imageCode, "data");
			return resultJSON.toJSONString(0,"获取验证码成功",imageCode);
		}else{
			return resultJSON.toJSONString(-1,"获取验证码失败");
		}
	}
	
	/**
	 * 忘记密码验证图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: verifyForgetImageCode 
	 * @param userid			用户ID
	 * @param loginAccount		登录账号
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2016年4月7日 下午2:30:59  
	 * @author xiongbin
	 */
	@RequestMapping(value="forgot/verify/imageCode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String verifyForgetImageCode(Long userid,String loginAccount,String imageCode) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(loginAccount)){
			return resultJSON.toJSONString(-1, "参数loginAccount不能为空");
		}else if(StringUtils.isBlank(imageCode)){
			return resultJSON.toJSONString(-1, "参数imageCode不能为空");
		}
		
		String result = integralShopPAB.verifyForgetImageCode(userid, loginAccount, imageCode);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			return resultJSON.toJSONString(0,"验证图片验证码成功");
		}
		
		String code = JsonResult.getResult(result, "code");
		if("3301".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.IMAGECODE_ERROR, "图片验证码不正确 ");
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 忘记密码发送短信验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: sendSmsForget 
	 * @param userid		用户ID
	 * @param imageCode		图片验证码
	 * @return
	 * @date 2016年4月7日 下午4:31:16  
	 * @author xiongbin
	 */
	@RequestMapping(value="forgot/send/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String sendSmsForget(Long userid,String imageCode) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(imageCode)){
			return resultJSON.toJSONString(-1, "参数imageCode不能为空");
		}
		
		String result = integralShopPAB.sendSmsForget(userid, imageCode);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			return resultJSON.toJSONString(0,"短信发送成功");
		}
		
		String code = JsonResult.getResult(result, "code");
		if("3302".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.IMAGECODE_ERROR, "图片验证码不正确 ");
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 忘记密码验证短信验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: verifyForgetSms 
	 * @param userid		用户ID
	 * @param phoneCode		短信验证码
	 * @return
	 * @date 2016年4月7日 下午4:47:28  
	 * @author xiongbin
	 */
	@RequestMapping(value="forgot/verify/sms/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String verifyForgetSms(Long userid,String phoneCode) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(phoneCode)){
			return resultJSON.toJSONString(-1, "参数phoneCode不能为空");
		}
		
		String result = integralShopPAB.verifySmsForget(userid, phoneCode);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			return resultJSON.toJSONString(0,"成功");
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 修改密码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: reset 
	 * @param userid		用户ID
	 * @param password		新密码
	 * @return
	 * @date 2016年4月7日 下午4:51:25  
	 * @author xiongbin
	 */
	@RequestMapping(value="forgot/reset/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String reset(Long userid,String password) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-1, "参数password不能为空");
		}
		
		String result = integralShopPAB.updatePassword(userid, password);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			return resultJSON.toJSONString(0,"成功");
		}
		
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
		if("2002".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.SYSTEM_BUSY, "系统繁忙");
		}
		
		return reslut;
	}
}
