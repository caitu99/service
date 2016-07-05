package com.caitu99.service.ishop.esurfing.controller.api;

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
import com.caitu99.service.base.BaseController;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.ishop.domain.UserThirdIntegralShopRecord;
import com.caitu99.service.ishop.esurfing.controller.api.spider.IntegralShopEsurfing;
import com.caitu99.service.ishop.service.UserThirdIntegralShopRecordService;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/ishop/esurfing/")
public class UserEsurfingShopController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(UserEsurfingShopController.class);
	
	private static final String[] AUTO_ACCOUNT_FILLTER = {"loginAccount"};
	
	@Autowired
	private UserThirdIntegralShopRecordService userThirdIntegralShopRecordService;
	@Autowired
	private IntegralShopEsurfing integralShopEsurfing;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	@Autowired
	private ItemService itemService;
	@Autowired
	ManualLoginService manualLoginService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private RealizeService realizeService;
	

	/**
	 * 获取用户最近登录的信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: autoAccount 
	 * @param userid
	 * @return
	 * @date 2016年1月29日 下午3:08:18  
	 * @author xiongbin
	 */
	@RequestMapping(value="auto/account/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String autoAccount(Long userid) {
		ApiResult<UserThirdIntegralShopRecord> resultJSON = new ApiResult<UserThirdIntegralShopRecord>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		UserThirdIntegralShopRecord userThirdIntegralShopRecord =
							userThirdIntegralShopRecordService.selectByUserIdNewest(userid, UserThirdIntegralShopRecord.TYPE_ESURFING);
		if(null != userThirdIntegralShopRecord){
			return resultJSON.toJSONString(0, "succeed",userThirdIntegralShopRecord,UserThirdIntegralShopRecord.class,AUTO_ACCOUNT_FILLTER);
		}
		
		return resultJSON.toJSONString(-1, "没有数据");
	}
	
	/**
	 * 获取登录的图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年2月16日 上午11:43:18  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/imageCode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getImageCode(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		String imageCode = integralShopEsurfing.getImageCode(userid);
		boolean flagImage = JsonResult.checkResult(imageCode);
		if(flagImage){
			imageCode = JsonResult.getResult(imageCode, "data");
			return resultJSON.toJSONString(0,"获取验证码成功",imageCode);
		}else{
			return resultJSON.toJSONString(-1,"获取验证码失败");
		}
	}
	
	/**
	 * 登录,并发送图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid		用户ID
	 * @param phone			手机号
	 * @param imageCode		图片验证码
	 * @return
	 * @date 2016年2月17日 下午3:58:32  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid,String phone,String imageCode) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(-1, "参数phone不能为空");
		}
		
		if(appConfig.isDevMode){
			Random r = new Random();
	    	int i = r.nextInt(100) % 2;
	    	if(i == 0){
	    		return resultJSON.toJSONString(0, "短信发送成功");
	    	}else{
	    		imageCode = integralShopEsurfing.getImageCode(userid);
	    		boolean flagImage = JsonResult.checkResult(imageCode);
				if(flagImage){
					imageCode = JsonResult.getResult(imageCode, "data");
					return resultJSON.toJSONString(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR,"破解图片验证码失败",imageCode);
				}else{
					return resultJSON.toJSONString(-1,"获取图片验证码失败");
				}
	    	}
		}
		
		if(StringUtils.isBlank(imageCode)){
			logger.info("前端传递图片验证码为空,自动破解验证码");
			//获取图片验证码
			imageCode = integralShopEsurfing.getImageCode(userid);
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
		}

		logger.info("前端传递图片验证码不为空,手动输入验证码");
		
		String result = integralShopEsurfing.loginSend(userid, phone, imageCode);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			return resultJSON.toJSONString(0, "短信发送成功");
		}
		
		String code = JsonResult.getResult(result, "code");
		if("1004".equals(code)){
			imageCode = integralShopEsurfing.getImageCode(userid);
			boolean flagImage = JsonResult.checkResult(imageCode);
			if(flagImage){
				imageCode = JsonResult.getResult(imageCode, "data");
			}else{
				return resultJSON.toJSONString(-1,"获取图片验证码失败");
			}
			return resultJSON.toJSONString(ApiResultCode.IMAGECODE_ERROR,"图片验证码不正确",imageCode);
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
//	/**
//	 * 天翼积分商城生成订单,并发送验证码
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: payorder 
//	 * @param userid		用户ID
//	 * @param phone			手机号
//	 * @param msCode		短信验证码
//	 * @param orderNo		订单号
//	 * @return
//	 * @date 2016年2月17日 下午4:04:10  
//	 * @author xiongbin
//	 */
//	@SuppressWarnings("unchecked")
//	@RequestMapping(value="pay/order/1.0", produces="application/json;charset=utf-8")
//	@ResponseBody
//	public String payorder(Long userid,String phone,String msCode,String orderNo) {
//		ApiResult<String> resultJSON = new ApiResult<String>();
//		
//		if(null == userid){
//			return resultJSON.toJSONString(-1, "参数userid不能为空");
//		}else if(StringUtils.isBlank(phone)){
//			return resultJSON.toJSONString(-2, "参数phone不能为空");
//		}else if(StringUtils.isBlank(msCode)){
//			return resultJSON.toJSONString(-1, "参数msCode不能为空");
//		}else if(StringUtils.isBlank(orderNo)){
//			return resultJSON.toJSONString(-2, "参数orderNo不能为空");
//		}
//		
//		//获取天翼积分商城商品属性
//		Map<String, Object> propMap = orderService.getGoodByTrade(orderNo,userid);
//		if(null == propMap){
//			return resultJSON.toJSONString(-3,"无此商品属性数据");
//		}
//		
//		List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
//		if(null == goodPropList){
//			return resultJSON.toJSONString(-3,"无此商品属性数据");
//		}
//		
//		Map<String,String> paramMap = new HashMap<String,String>();
//		for(GoodProp goodProp : goodPropList){
//			paramMap.put(goodProp.getName(), goodProp.getValue());
//		}
//
//	    Long price = (Long)propMap.get("price");
//	    Integer quantity = (Integer)propMap.get("quantity");
//		
//		paramMap.put("buyNum", quantity.toString());
//		paramMap.put("payTotal", String.valueOf(price * quantity));
//		
//		//天翼生成订单,并发送短信
//		String result = integralShopEsurfing.orderGenerate(userid, phone, msCode, paramMap);
//		boolean flag = JsonResult.checkResult(result);
//		if(flag){
//			return resultJSON.toJSONString(0, "短信发送成功");
//		}
//
//		result = disposeCode(result);
//		String message = JsonResult.getResult(result, "message");
//		return resultJSON.toJSONString(-1, message);
//	}
	
	/**
	 * 天翼积分商城支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: payorder 
	 * @param userid		用户ID
	 * @param phone			手机号
	 * @param msCode		短信验证码
	 * @param orderNo		订单号
	 * @return
	 * @date 2016年2月17日 下午4:04:10  
	 * @author xiongbin
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="pay/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String pay(Long userid,String phone,String msCode,String orderNo) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(-2, "参数phone不能为空");
		}else if(StringUtils.isBlank(msCode)){
			return resultJSON.toJSONString(-1, "参数msCode不能为空");
		}else if(StringUtils.isBlank(orderNo)){
			return resultJSON.toJSONString(-2, "参数orderNo不能为空");
		}
		
		//获取天翼积分商城商品属性
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
		
		paramMap.put("buyNum", quantity.toString());
		paramMap.put("payTotal", String.valueOf(price * quantity));
		
		//天翼登录
		String result = "";
		boolean flag = false;
		
		if(appConfig.isDevMode){
			flag = true;
		}else{
			//天翼登录
			result = integralShopEsurfing.login(userid, phone, msCode);
			flag = JsonResult.checkResult(result);
		}
		
		if(flag){
			if(appConfig.isDevMode){
				flag = true;
			}else{
				//天翼生成订单,并发送短信
				result = integralShopEsurfing.orderGenerate(userid, paramMap);
				flag = JsonResult.checkResult(result);
			}
			
			if(flag){
				String thredOrderNo = "";
				if(!appConfig.isDevMode){
					thredOrderNo = JsonResult.getResult(result, "data");
				}
				orderService.processTradeOrder(1,userid,orderNo,thredOrderNo);
				
				try {
	
					logger.info("商城消费，天翼自动更新开始，manualID:{}",4);
					List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 4L);
					ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
					thread.run();
				} catch (Exception e) {
					logger.warn("商城消费，天翼自动更新失败：{}",e);
				}
				
				try {
					JSONObject info = new JSONObject();
					info.put("phone", phone);
					
					UserAddTerm userAddTerm = new UserAddTerm();
					userAddTerm.setLoginAccount(phone);
					
					realizeService.ishopBinding(userid, 2L, info.toJSONString(), userAddTerm);
				} catch (Exception e) {
					logger.error("绑定天翼第三方积分商城失败:" + e.getMessage(),e);
				}
				
				return resultJSON.toJSONString(0, "支付成功");
			}
			
			orderService.processTradeOrder(0,userid,orderNo,"");
		}

		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
//	/**
//	 * 发送支付验证码
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: paySendSMS 
//	 * @param userid	用户ID
//	 * @return
//	 * @date 2016年2月18日 上午10:27:37  
//	 * @author xiongbin
//	 */
//	@RequestMapping(value="pay/send/1.0", produces="application/json;charset=utf-8")
//	@ResponseBody
//	public String paySendSMS(Long userid) {
//		ApiResult<String> resultJSON = new ApiResult<String>();
//		
//		if(null == userid){
//			return resultJSON.toJSONString(-1, "参数userid不能为空");
//		}
//		
//		String reslut = integralShopEsurfing.paySMS(userid);
//		boolean flag = JsonResult.checkResult(reslut);
//		if(flag){
//			return resultJSON.toJSONString(0,"发送验证码成功");
//		}else{
//			return resultJSON.toJSONString(-1,"发送验证码失败");
//		}
//	}
//	
//	/**
//	 * 支付
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: pay 
//	 * @param userid			用户ID
//	 * @param cardNo			银行卡号
//	 * @param reservedPhone		银行预留手机
//	 * @param code				验证码
//	 * @return
//	 * @date 2016年2月1日 上午10:14:04  
//	 * @author xiongbin
//	 */
//	@RequestMapping(value="pay/submit/1.0", produces="application/json;charset=utf-8")
//	@ResponseBody
//	public String pay(Long userid,String code,String orderNo) {
//		ApiResult<String> resultJSON = new ApiResult<String>();
//		
//		if(null == userid){
//			return resultJSON.toJSONString(-1, "参数userid不能为空");
//		}else if(StringUtils.isBlank(orderNo)){
//			return resultJSON.toJSONString(-2, "参数orderNo不能为空");
//		}else if(StringUtils.isBlank(code)){
//			return resultJSON.toJSONString(-1, "参数code不能为空");
//		}
//		
//		//天翼支付提交
//		String result = integralShopEsurfing.pay(userid, code);
//		boolean flag = JsonResult.checkResult(result);
//		if(flag){
//			String thredOrderNo = JsonResult.getResult(result, "data");
//			orderService.processTradeOrder(1,userid,orderNo,thredOrderNo);
//			return resultJSON.toJSONString(0, "支付成功");
//		}
//		
//		orderService.processTradeOrder(0,userid,orderNo,"");
//		
//		//建行失败返回错误信息
//		String message = JsonResult.getResult(result, "message");
//		return resultJSON.toJSONString(-1, message);
//	}
	
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
		if("1117".equals(code) || "1118".equals(code) || "1113".equals(code)|| "1067".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.SYSTEM_BUSY, "支付失败，天翼系统维护中，请稍后再试");
		}
		
		return reslut;
	}
}
