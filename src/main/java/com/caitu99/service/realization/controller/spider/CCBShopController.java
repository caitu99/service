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
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.ishop.ccb.controller.api.UserChinaConstructionBankShopController;
import com.caitu99.service.ishop.ccb.controller.api.spider.IntegralShopCCB;
import com.caitu99.service.realization.dao.PhoneAmountMapper;
import com.caitu99.service.realization.domain.PhoneAmount;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.realization.service.UserAddTermService;
import com.caitu99.service.realization.service.UserTermService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/realization/ccb/")
public class CCBShopController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(CCBShopController.class);
	
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
	@Autowired
	private UserChinaConstructionBankShopController userChinaConstructionBankShopController;
	@Autowired
	ManualLoginService manualLoginService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private PhoneAmountMapper phoneAmountMapper;
	
//	/**
//	 * 登录
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: login 
//	 * @param userid				用户ID
//	 * @param loginAccount			账号
//	 * @param password				密码
//	 * @param realizeRecordId		积分变现记录ID
//	 * @param imageCode				图片验证码
//	 * @return
//	 * @date 2016年2月25日 下午8:20:24  
//	 * @author xiongbin
//	 */
//	@SuppressWarnings("unchecked")
//	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
//	@ResponseBody
//	public String login(Long userid,String loginAccount,String password,Long realizeRecordId,String imageCode) {
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
//		User user = userService.selectByPrimaryKey(userid);
//		if(null == user){
//			throw new UserNotFoundException(-1, "用户不存在");
//		}
//		
//		String phone = user.getMobile();
//		if(StringUtils.isBlank(phone)){
//			return resultJSON.toJSONString(ApiResultCode.PHONE_NOT_NULL, "用户未绑定手机,请先绑定手机");
//		}
//		
//		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
//		if(null == realizeRecord){
//			return resultJSON.toJSONString(-2, "数据出现异常");
//		}
//		
//		if(StringUtils.isBlank(imageCode)){
//			logger.info("前端传递图片验证码为空,自动破解验证码");
//			//获取图片验证码
//			imageCode = integralShopCCB.getImageCode(userid);
//			boolean flagImage = JsonResult.checkResult(imageCode);
//			if(flagImage){
//				imageCode = JsonResult.getResult(imageCode, "data");
//				String imageCodeNew = CommonImgCodeApi.recognizeImgCodeFromStr(imageCode);
//				if(StringUtils.isBlank(imageCodeNew)){
//					return resultJSON.toJSONString(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR,"破解图片验证码失败",imageCode);
//				}
//				imageCode = imageCodeNew;
//			}else{
//				return resultJSON.toJSONString(-1,"获取图片验证码失败");
//			}
//		}
//
//		logger.info("前端传递图片验证码不为空,手动输入验证码");
//		
//		//获取建行积分商城商品属性
//		Long realizeDetailId = realizeRecord.getRealizeDetailId();
//		Map<String, Object> propMap = realizeService.getItemPayParams(realizeDetailId);
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
//		paramMap.put("quantity", quantity.toString());
//		paramMap.put("productPrice", String.valueOf(price * quantity));
//		
//		//查询商品是否支持自由交易,是否需要物流
//		Long itemId = (Long)propMap.get("itemId");
//		Item item = itemService.selectByPrimaryKey(itemId);
//		if(null == item){
//			return resultJSON.toJSONString(-4,"该商品不存在");
//		}
//		paramMap.put("mobile", phone);
//		
//		String result = integralShopCCB.login(userid,loginAccount,password,imageCode,paramMap);
//		
//        logger.info("积分变现建设银行,登录,用户ID：{},返回数据:{}",userid,result);
//		
//		boolean flag = JsonResult.checkResult(result);
//		if(flag){
//			return resultJSON.toJSONString(0, "登录成功");
//		}
//		
//		String code = JsonResult.getResult(result, "code");
//		if("1302".equals(code) || "1052".equals(code)){
//			imageCode = integralShopCCB.getImageCode(userid);
//			boolean flagImage = JsonResult.checkResult(imageCode);
//			if(flagImage){
//				imageCode = JsonResult.getResult(imageCode, "data");
//			}else{
//				return resultJSON.toJSONString(-1,"获取图片验证码失败");
//			}
//			return resultJSON.toJSONString(ApiResultCode.IMAGECODE_ERROR,"图片验证码不正确",imageCode);
//		}else if("1060".equals(code)){
//			return resultJSON.toJSONString(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误","/realize_ccb/login.html?realizeRecordId="+realizeRecordId);
//		}
//		
//		result = disposeCode(result);
//		String message = JsonResult.getResult(result, "message");
//		return resultJSON.toJSONString(-1, message);
//	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid				用户ID
	 * @param loginAccount			账号
	 * @param password				密码
	 * @param realizeRecordId		积分变现记录ID
	 * @param imageCode				图片验证码
	 * @return
	 * @date 2016年2月25日 下午8:20:24  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid,String loginAccount,String password,Long realizeRecordId,String imageCode,String redirectUrl) {
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
		}else{
			logger.info("前端传递图片验证码不为空,手动输入验证码");
		}
		
		String result = "";
		boolean flag = false;
		
		if(appConfig.isDevMode){
        	logger.info("积分变现建设银行,登录,用户ID：{},开发模式",userid);
			flag = true;
		}else{
			result = integralShopCCB.login(userid,loginAccount,password,imageCode);
        	logger.info("积分变现建设银行,登录,用户ID：{},返回数据:{}",userid,result);
        	flag = JsonResult.checkResult(result);
		}
		
		if(flag){
			result = this.order(userid, realizeRecordId);
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
			return resultJSON.toJSONString(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误",redirectUrl+realizeRecordId);
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 下单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: order 
	 * @param userid				用户Id
	 * @param realizeRecordId		积分变现记录ID
	 * @return
	 * @date 2016年3月23日 下午2:31:18  
	 * @author xiongbin
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="order/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String order(Long userid,Long realizeRecordId) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(null == realizeRecordId){
			return resultJSON.toJSONString(-1, "参数realizeRecordId不能为空");
		}
		
		User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}

		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			return resultJSON.toJSONString(-2, "数据出现异常");
		}

		String phone = user.getMobile();
		if(realizeRecord.getRealizeId().intValue() == -1){
			String memo = realizeRecord.getMemo();
			phone = JSON.parseObject(memo).getString("phoneNo");
		}
		
		if(StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(ApiResultCode.PHONE_NOT_NULL, "用户未绑定手机,请先绑定手机");
		}
		
		//获取建行积分商城商品属性
		/*Long realizeDetailId = realizeRecord.getRealizeDetailId();
		Map<String, Object> propMap = realizeService.getItemPayParams(realizeDetailId);*/
		Map<String, Object> propMap = realizeService.getItemPayParamsByRecord(realizeRecord);
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
		paramMap.put("mobile", phone);
		
		String result = "";
		boolean flag = false;
		
		if(appConfig.isDevMode){
			logger.info("积分变现建设银行,下单,用户ID：{},开发模式",userid);
			flag = true;
		}else{
			result = integralShopCCB.order(userid, paramMap);
			logger.info("积分变现建设银行,下单,用户ID：{},返回数据:{}",userid,result);
			flag = JsonResult.checkResult(result);
		}
		
		if(flag){
			return resultJSON.toJSONString(0, "下单成功");
		}
		
		String code = JsonResult.getResult(result, "code");
		if("1005".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.NOT_LOGIN_STATUS,"您的登录状态已过期,请重新登录");
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
	public String pay(Long userid,String loginAccount,String password,String cardNo,String reservedPhone,String code,Long realizeRecordId) {
		ApiResult<JSONObject> resultJSON = new ApiResult<JSONObject>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(cardNo) || StringUtils.isBlank(reservedPhone) || null==realizeRecordId){
			return resultJSON.toJSONString(-2, "用户支付信息不能为空");
		}else if(StringUtils.isBlank(code)){
			return resultJSON.toJSONString(-1, "参数code不能为空");
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
		
		//是否是开发环境
		if(appConfig.isDevMode){
			logger.info("积分变现建行银行,支付,用户ID：{},开发模式",userid);
			flag = true;
		}else{
			//建行支付提交
			result = integralShopCCB.pay(userid, code);
	        logger.info("积分变现建行银行,支付,用户ID：{},返回数据:{}",userid,result);
			flag = JsonResult.checkResult(result);
		}
		
		if(flag){
//			String thredOrderNo = JsonResult.getResult(result, "data");
			Long remoteId = json.getLong("remoteId");
			Integer remoteType = json.getInteger("remoteType");
			Integer isAddUserTerm = json.getInteger("isAddUserTerm"); 
			if(null == isAddUserTerm){
				return resultJSON.toJSONString(-2, "数据出现异常");
			}
			
			UserAddTerm userAddTerm = new UserAddTerm();
			userAddTerm.setCardNo(cardNo);
			userAddTerm.setLoginAccount(loginAccount);
			
			JSONObject info = new JSONObject();
			info.put("loginAccount",loginAccount);
			info.put("password",password);
			info.put("cardNo",cardNo);
			info.put("reservedPhone",reservedPhone);
			
			//支付
			String orderNo = realizeService.realizePay(userid, realizeRecord, userAddTerm, json, 
																		info, false, remoteId, remoteType, isAddUserTerm,true);
			
			if(StringUtils.isBlank(orderNo)){
				return resultJSON.toJSONString(-1, "系统暂时出现异常,请稍后再试");
			}
			
			logger.info("积分变现建行银行,积分变现支付,用户ID：{},订单号:{},realizeRecordId:{},memo:{}",userid,orderNo,realizeRecordId,memo);
			
			//购买数量
			Integer quantity = realizeRecord.getQuantity();
			//商品ID
			Long itemId = realizeRecord.getItemId();
			Item item = itemService.selectByPrimaryKey(itemId);
			String itemName = item.getTitle();
			
			Long amountId = json.getLong("amountId");
			PhoneAmount phoneAmount = phoneAmountMapper.selectByPrimaryKey(amountId);
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("quantity", quantity);
			jsonObject.put("itemName", itemName);
			jsonObject.put("orderNo", orderNo);
			jsonObject.put("realizeRecordId", realizeRecordId);
			
			//手机充值
			if(realizeRecord.getRealizeId().equals(-1L)){
				jsonObject.put("phoneNo", json.getString("phoneNo"));
				jsonObject.put("amount", phoneAmount.getName());
			}
			
			try {
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 14L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("积分变现建设银行，自动更新失败：{}",e);
			}
			
			return resultJSON.toJSONString(0, "支付成功",jsonObject);
		}
		
		if(JsonResult.checkResult(result,1307)){
			//建行积分不足的情况
			return result;
		}
		
		//建行失败返回错误信息
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 自动登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: autoLogin 
	 * @param userid			用户ID
	 * @param loginAccount		登录账号
	 * @param password			密码
	 * @param realizeRecordId	积分变现记录ID
	 * @param imageCode
	 * @return
	 * @date 2016年2月25日 下午5:32:44  
	 * @author xiongbin
	 */
	@RequestMapping(value="auto/login/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String autoLogin(Long userid,String loginAccount,String password,Long realizeRecordId,String imageCode) {
		ApiResult<JSONObject> resultJSON = new ApiResult<JSONObject>();
		
		String reslut = this.order(userid, realizeRecordId);
		String code = JsonResult.getResult(reslut, "code");
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		
		String memo = realizeRecord.getMemo();
		JSONObject json = JSON.parseObject(memo);
		JSONObject info = JSON.parseObject(json.getString("info"));
		String cardNo = info.getString("cardNo");
		String reservedPhone = info.getString("reservedPhone");
		
		if(ApiResultCode.SUCCEED.toString().equals(code)){
			reslut = userChinaConstructionBankShopController.paySendSMS(userid, cardNo, reservedPhone);
			boolean flag = JsonResult.checkResult(reslut);
			if(flag){
				return resultJSON.toJSONString(0, "success");
			}
		}else{
			reslut = this.login(userid, loginAccount, password, realizeRecordId, imageCode,"");
			boolean flag = JsonResult.checkResult(reslut);
			if(flag){
				reslut = userChinaConstructionBankShopController.paySendSMS(userid, cardNo, reservedPhone);
				flag = JsonResult.checkResult(reslut);
				if(flag){
					return resultJSON.toJSONString(0, "success");
				}
			}
		}
		
		return reslut;
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
		
		if("1068".equals(code) || "1301".equals(code) 
									|| "1006".equals(code) || "1300".equals(code) || "1305".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.SYSTEM_BUSY, "建行积分商城系统维护中,请稍后再试");
		}else if("1306".equals(code)){
			return resultJSON.toJSONString(-1, "请确认您的卡号信息是否正确");
		}
		
		return reslut;
	}
}
