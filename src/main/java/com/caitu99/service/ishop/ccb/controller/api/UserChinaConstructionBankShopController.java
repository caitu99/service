package com.caitu99.service.ishop.ccb.controller.api;

import java.util.Date;
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

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.ishop.ccb.controller.api.spider.IntegralShopCCB;
import com.caitu99.service.ishop.domain.UserThirdIntegralShopRecord;
import com.caitu99.service.ishop.service.UserChinaConstructionBankCardService;
import com.caitu99.service.ishop.service.UserThirdIntegralShopRecordService;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.domain.OrderAddress;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/ishop/ccb/")
public class UserChinaConstructionBankShopController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(UserChinaConstructionBankShopController.class);
	
	private static final String[] AUTO_ACCOUNT_FILLTER = {"loginAccount","password"};
	private static final String[] AUTO_BANKCARD_FILLTER = {"cardNo","reservedPhone"};
	
	@Autowired
	private UserChinaConstructionBankCardService userChinaConstructionBankCardService;
	@Autowired
	private IntegralShopCCB integralShopCCB;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private UserThirdIntegralShopRecordService userThirdIntegralShopRecordService;
	@Autowired
	private RealizeService realizeService;
	@Autowired
	ManualLoginService manualLoginService;
	@Autowired
	private AppConfig appConfig;
	
	/**
	 * 建行积分商城发送验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: registerSendSMS 
	 * @param userid			用户ID
	 * @param loginAccount		用户名
	 * @param password			密码
	 * @param phone				手机号码
	 * @return
	 * @date 2016年1月29日 上午10:38:34  
	 * @author xiongbin
	 */
	@RequestMapping(value="register/send/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String registerSendSMS(Long userid,String loginAccount,String password,String phone) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(loginAccount)){
			return resultJSON.toJSONString(-1, "参数loginAccount不能为空");
		}else if(StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-1, "参数password不能为空");
		}else if(StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(-1, "参数phone不能为空");
		}
		
		String result = integralShopCCB.registerSendSMS(userid, loginAccount, password, phone);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			return resultJSON.toJSONString(0, "短信发送成功");
		}
		
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 建行积分商城注册
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: register 
	 * @param userid			用户ID
	 * @param code				手机验证码
	 * @return
	 * @date 2016年1月28日 下午5:27:37  
	 * @author xiongbin
	 */
	@RequestMapping(value="register/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String register(Long userid,String loginAccount,String password,String phone,String code) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password) || StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(-2, "用户注册信息不能为空");
		}else if(StringUtils.isBlank(code)){
			return resultJSON.toJSONString(-1, "参数code不能为空");
		}
		
		String result = integralShopCCB.register(userid, code);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			try {
				UserThirdIntegralShopRecord userThirdIntegralShopRecord = 
									userThirdIntegralShopRecordService.selectWhether(userid,loginAccount,UserThirdIntegralShopRecord.TYPE_CCB);
				if(null == userThirdIntegralShopRecord){
					userThirdIntegralShopRecordService.insert(userid, loginAccount, password, phone,UserThirdIntegralShopRecord.TYPE_CCB);
				}else if(!password.equals(userThirdIntegralShopRecord.getPassword()) || !phone.equals(userThirdIntegralShopRecord.getPhone())){
					userThirdIntegralShopRecord.setGmtModify(new Date());
					userThirdIntegralShopRecord.setPassword(password);
					userThirdIntegralShopRecord.setPhone(phone);
					userThirdIntegralShopRecordService.update(userThirdIntegralShopRecord);
				}else if(password.equals(userThirdIntegralShopRecord.getPassword())){
					userThirdIntegralShopRecordService.updateRedis(userThirdIntegralShopRecord);
				}
				return resultJSON.toJSONString(0, "注册成功");
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				return resultJSON.toJSONString(0, "注册成功");
			}
		}
		
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
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
									userThirdIntegralShopRecordService.selectByUserIdNewest(userid,UserThirdIntegralShopRecord.TYPE_CCB);
		if(null != userThirdIntegralShopRecord){
			return resultJSON.toJSONString(0, "succeed",userThirdIntegralShopRecord,UserThirdIntegralShopRecord.class,AUTO_ACCOUNT_FILLTER);
		}
		
		return resultJSON.toJSONString(-1, "没有数据");
	}
	
	/**
	 * 获取用户最近使用的银行卡信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: autoBankCard 
	 * @param userid
	 * @return
	 * @date 2016年1月29日 下午5:36:00  
	 * @author xiongbin
	 */
	@RequestMapping(value="auto/bankCard/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String autoBankCard(Long userid) {
		ApiResult<UserThirdIntegralShopRecord> resultJSON = new ApiResult<UserThirdIntegralShopRecord>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		UserThirdIntegralShopRecord userChinaConstructionBankCard =
									userThirdIntegralShopRecordService.selectByUserIdNewest(userid,UserThirdIntegralShopRecord.TYPE_CCB);
		if(null != userChinaConstructionBankCard){
			return resultJSON.toJSONString(0, "succeed",userChinaConstructionBankCard,UserThirdIntegralShopRecord.class,AUTO_BANKCARD_FILLTER);
		}
		
		return resultJSON.toJSONString(-1, "没有数据");
	}
	
	/**
	 * 获取登录的图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年2月1日 下午3:27:12  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/imageCode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getImageCode(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		String result = integralShopCCB.getImageCode(userid);
		boolean flagImage = JsonResult.checkResult(result);
		if(flagImage){
			String imageCode = JsonResult.getResult(result, "data");
			return resultJSON.toJSONString(0,"获取验证码成功",imageCode);
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid			用户ID
	 * @param loginAccount		用户名
	 * @param password			密码
	 * @return
	 * @date 2016年1月29日 下午3:51:21  
	 * @author xiongbin
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid,String loginAccount,String password,String orderNo,String imageCode) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(loginAccount)){
			return resultJSON.toJSONString(-1, "参数loginAccount不能为空");
		}else if(StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-1, "参数password不能为空");
		}else if(StringUtils.isBlank(orderNo)){
			return resultJSON.toJSONString(-2, "参数orderNo不能为空");
		}
		
		User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}
		
		String phone = user.getMobile();
		if(StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(ApiResultCode.PHONE_NOT_NULL, "用户未绑定手机,请先绑定手机");
		}
		
		if(appConfig.isDevMode){
			logger.info("建设银行第三方商城,登录,用户ID：{},开发模式",userid);
			return resultJSON.toJSONString(0, "登录成功");
		}
		
		if(StringUtils.isBlank(imageCode)){
			logger.info("前端传递图片验证码为空,自动破解验证码");
			//获取图片验证码
			String result = integralShopCCB.getImageCode(userid);
			boolean flagImage = JsonResult.checkResult(result);
			if(flagImage){
				imageCode = JsonResult.getResult(result, "data");
				String imageCodeNew = CommonImgCodeApi.recognizeImgCodeFromStr(imageCode);
				if(StringUtils.isBlank(imageCodeNew)){
					return resultJSON.toJSONString(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR,"破解图片验证码失败",imageCode);
				}
				imageCode = imageCodeNew;
			}else{
				return result;
			}
		}

		logger.info("前端传递图片验证码不为空,手动输入验证码");
		
		//获取建行积分商城商品属性
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
		if(item.getIsFreeTrade().equals(1)){
			OrderAddress orderAddress = (OrderAddress)propMap.get("orderAddress");
			paramMap.put("provinceName", orderAddress.getProvince());
			paramMap.put("cityName", orderAddress.getCity());
			paramMap.put("areaName", orderAddress.getArea());
			paramMap.put("consigneeName", orderAddress.getName());
			paramMap.put("addressDetail", orderAddress.getDetailed());
			paramMap.put("postCode", orderAddress.getZipCode());
			paramMap.put("mobile", orderAddress.getMobile());
		}else{
			paramMap.put("mobile", phone);
		}
		
		String result = integralShopCCB.login(userid,loginAccount,password,imageCode);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			result = integralShopCCB.order(userid, paramMap);
			flag = JsonResult.checkResult(result);
			if(flag){
				return resultJSON.toJSONString(0, "登录成功");
			}
		}
		
		String code = JsonResult.getResult(result, "code");
		if("1302".equals(code) || "1052".equals(code)){
			imageCode = integralShopCCB.getImageCode(userid);
			boolean flagImage = JsonResult.checkResult(imageCode);
			if(flagImage){
				imageCode = JsonResult.getResult(imageCode, "data");
			}else{
				return resultJSON.toJSONString(-1,"获取图片验证码失败");
			}
			return resultJSON.toJSONString(ApiResultCode.IMAGECODE_ERROR,"图片验证码不正确",imageCode);
		}else if("1060".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误,请重新登录","/ccb/login.html?order_no=" + orderNo);
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 建行积分商城支付发送验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySendSMS 
	 * @param userid			用户ID
	 * @param cardNo			银行卡号
	 * @param reservedPhone		银行预留手机号码
	 * @return
	 * @date 2016年1月29日 下午6:01:04  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/send/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String paySendSMS(Long userid,String cardNo,String reservedPhone) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(cardNo)){
			return resultJSON.toJSONString(-1, "参数cardNo不能为空");
		}else if(StringUtils.isBlank(reservedPhone)){
			return resultJSON.toJSONString(-1, "参数reservedPhone不能为空");
		}
		
		//是否是开发环境
		if(appConfig.isDevMode){
			logger.info("积分变现建设银行,发送支付验证码,用户ID：{},开发模式",userid);
			return resultJSON.toJSONString(0, "短信发送成功");
		}
		
		//建行支付发送验证码
		String result = integralShopCCB.paySendSMS(userid,cardNo,reservedPhone);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			return resultJSON.toJSONString(0, "短信发送成功");
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
	 * @param cardNo			银行卡号
	 * @param reservedPhone		银行预留手机
	 * @param code				验证码
	 * @return
	 * @date 2016年2月1日 上午10:14:04  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String pay(Long userid,String cardNo,String reservedPhone,String code,String orderNo,String loginAccount,String password){
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(cardNo) || StringUtils.isBlank(reservedPhone) || StringUtils.isBlank(orderNo)
													|| StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-2, "用户支付信息不能为空");
		}else if(StringUtils.isBlank(code)){
			return resultJSON.toJSONString(-1, "参数code不能为空");
		}
		
		String result = "";
		boolean flag = false;
		
		if(appConfig.isDevMode){
			logger.info("积分变现建设银行,支付,用户ID：{},开发模式",userid);
			flag = true;
		}else{
			//建行支付提交
			result = integralShopCCB.pay(userid, code);
			flag = JsonResult.checkResult(result);
		}
		
		if(flag){
			String thredOrderNo = "";
			if(!StringUtils.isBlank(result)){
				thredOrderNo = JsonResult.getResult(result, "data");
			}
			
			orderService.processTradeOrder(1,userid,orderNo,thredOrderNo);
			
			logger.info("商城消费建设银行自动更新开始");
			try {
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 14L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("商城消费建设银行自动更新失败：{}",e);
			}
			
			try {
				JSONObject info = new JSONObject();
				info.put("loginAccount", loginAccount);
				info.put("password", password);
				info.put("cardNo", cardNo);
				info.put("reservedPhone", reservedPhone);
				
				UserAddTerm userAddTerm = new UserAddTerm();
				userAddTerm.setCardNo(cardNo);
				userAddTerm.setLoginAccount(loginAccount);
				
				realizeService.ishopBinding(userid, 1L, info.toJSONString(), userAddTerm);
				
				return resultJSON.toJSONString(0, "支付成功");
			} catch (Exception e) {
				logger.error("绑定建行第三方积分商城失败:" + e.getMessage(),e);
				return resultJSON.toJSONString(0, "支付成功");
			}
		}
		
		orderService.processTradeOrder(0,userid,orderNo,"");
		
		//建行失败返回错误信息
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 建行第三方积分商城自动登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: autoLogin 
	 * @param userid			用户ID
	 * @param loginAccount		账号
	 * @param password			密码
	 * @param cardNo			卡号
	 * @param reservedPhone		绑定手机
	 * @param orderNo			订单号
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2016年5月11日 下午3:35:37  
	 * @author xiongbin
	 */
	@RequestMapping(value="auto/login/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String autoLogin(Long userid,String loginAccount,String password,String cardNo,String reservedPhone,String orderNo,String imageCode) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(loginAccount)){
			return resultJSON.toJSONString(-1, "参数loginAccount不能为空");
		}else if(StringUtils.isBlank(password)){
			return resultJSON.toJSONString(-1, "参数password不能为空");
		}else if(StringUtils.isBlank(cardNo)){
			return resultJSON.toJSONString(-1, "参数cardNo不能为空");
		}else if(StringUtils.isBlank(reservedPhone)){
			return resultJSON.toJSONString(-1, "参数reservedPhone不能为空");
		}else if(StringUtils.isBlank(orderNo)){
			return resultJSON.toJSONString(-1, "参数orderNo不能为空");
		}
		
		String result = this.login(userid, loginAccount, password, orderNo, imageCode);
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			result = this.paySendSMS(userid, cardNo, reservedPhone);
			return result;
		}
		
		return result;
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
		
		String code = JsonResult.getResult(reslut, "code");
		if("1306".equals(code) || "1068".equals(code) || "1301".equals(code) 
										|| "1006".equals(code) || "1300".equals(code) || "1305".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.SYSTEM_BUSY, "建行积分商城系统维护中,请稍后再试");
		}
		
		return reslut;
	}
}
