package com.caitu99.service.realization.controller.spider;

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
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.ishop.pab.controller.api.spider.IntegralShopPAB;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/realization/pab/")
public class PABController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(PABController.class);
	
	@Autowired
	private IntegralShopPAB integralShopPAB;
	@Autowired
	private RealizeService realizeService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ManualLoginService manualLoginService;
    @Autowired
    private AppConfig appConfig;
	
	/**
	 * 获取图验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年4月6日 上午11:33:23  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/imageCode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getImageCode(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
//		String imageCode = integralShopPAB.getImageCodeRealization(userid);
		String imageCode = integralShopPAB.getImageCodeThird(userid);
		boolean flagImage = JsonResult.checkResult(imageCode,ApiResultCode.NEED_INOUT_IMAGECODE);
		if(flagImage){
			imageCode = JsonResult.getResult(imageCode, "data");
			return resultJSON.toJSONString(0,"获取登录验证码成功",imageCode);
		}else{
			return resultJSON.toJSONString(-1,"支付失败，平安银行系统维护中，请稍后再试");
		}
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid				用户ID
	 * @param loginAccount			登录账号
	 * @param password				密码
	 * @param imageCode				图片验证码
	 * @param realizeRecordId		积分变现记录ID
	 * @return
	 * @date 2016年4月6日 上午10:16:30  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid,String loginAccount,String password,String imageCode,Long realizeRecordId) {
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
		
		String result = "";
		boolean flag = false;
		
		if(appConfig.isDevMode){
			flag = true;
			logger.info("积分变现平安银行,登录,用户ID：{},开发模式",userid);
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
			
//			result = integralShopPAB.loginRealization(userid, loginAccount, password, imageCode);
			result = integralShopPAB.loginThird(userid, loginAccount, password, imageCode);
			logger.info("积分变现平安银行,登录,用户ID：{},返回数据:{}",userid,result);
			flag = JsonResult.checkResult(result);
		}
		
		if(flag){
			result = this.order(userid, realizeRecordId, null);
			String code = JsonResult.getResult(result,"code");
			if(ApiResultCode.PAB_ORDER_PASSWORD.toString().equals(code) 
					|| ApiResultCode.PAB_ORDER_PHONECODE.toString().equals(code)
					|| ApiResultCode.PAB_ORDER_PASSWORD_AND_PHONECODE.toString().equals(code)){
				
				return resultJSON.toJSONString(0,"SUCCEED",code);
			}else if(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR.toString().equals(code)
									|| ApiResultCode.IMAGECODE_ERROR.toString().equals(code)){
				imageCode = JsonResult.getResult(result,"data");
				return resultJSON.toJSONString(-9,"下单验证码错误",imageCode);
			}
		}
		
		String code = JsonResult.getResult(result, "code");
		if("3004".equals(code) || "1010".equals(code) || "1011".equals(code)){
			imageCode = this.getImageCode(userid);
			boolean flagImage = JsonResult.checkResult(imageCode);
			if(flagImage){
				imageCode = JsonResult.getResult(imageCode, "data");
			}else{
				logger.info("平安变现获取图片验证码失败");
				return resultJSON.toJSONString(-1,"支付失败，平安银行系统维护中，请稍后再试");
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
	 * 获取下单图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getOrderImageCode 
	 * @param userid
	 * @return
	 * @date 2016年4月6日 下午5:25:52  
	 * @author xiongbin
	 */
	@RequestMapping(value="order/imageCode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getOrderImageCode(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
		String imageCode = integralShopPAB.getOrderImageCode(userid);
		boolean flagImage = JsonResult.checkResult(imageCode,ApiResultCode.NEED_INOUT_IMAGECODE);
		if(flagImage){
			imageCode = JsonResult.getResult(imageCode, "data");
			return resultJSON.toJSONString(0,"获取下单验证码成功",imageCode);
		}
		
		String code = JsonResult.getResult(imageCode, "code");
		if("3006".equals(code) || "1005".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.NOT_LOGIN_STATUS,"您的登录状态已过期,请重新登录");
		}
		
		return resultJSON.toJSONString(-1,"获取下单验证码失败");
	}
	
	/**
	 * 下单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: order 
	 * @param userid				用户ID
	 * @param realizeRecordId		积分变现记录ID
	 * @param imageCode				图片验证码
	 * @return
	 * @date 2016年4月6日 下午5:56:43  
	 * @author xiongbin
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="order/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String order(Long userid,Long realizeRecordId,String imageCode) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(null == realizeRecordId){
			return resultJSON.toJSONString(-1, "参数realizeRecordId不能为空");
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
			logger.info("积分变现平安银行,下单,用户ID：{},开发模式",userid);
		}else{
			//第三方积分商城无需下单验证码
//			if(StringUtils.isBlank(imageCode)){
//				logger.info("前端传递图片验证码为空,自动破解验证码");
//				//获取图片验证码
//				imageCode = this.getOrderImageCode(userid);
//				boolean flagImage = JsonResult.checkResult(imageCode);
//				if(flagImage){
//					imageCode = JsonResult.getResult(imageCode, "data");
//					String imageCodeNew = CommonImgCodeApi.recognizeImgCodeFromStr(imageCode);
//					if(StringUtils.isBlank(imageCodeNew)){
//						return resultJSON.toJSONString(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR,"破解图片验证码失败",imageCode);
//					}
//					imageCode = imageCodeNew;
//				}else{
//					String imageFlagCode = JsonResult.getResult(imageCode, "code");
//					if(ApiResultCode.NOT_LOGIN_STATUS.toString().equals(imageFlagCode)){
//						return resultJSON.toJSONString(ApiResultCode.NOT_LOGIN_STATUS,"您的登录状态已过期,请重新登录");
//					}
//					
//					return resultJSON.toJSONString(-1,"获取图片验证码失败");
//				}
//			}else{
//				logger.info("前端传递图片验证码不为空,手动输入验证码");
//			}
		
			RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
			if(null == realizeRecord){
				logger.info("平安变现数据出现异常: realizeRecord is null");
				return resultJSON.toJSONString(-2,"支付失败，平安银行系统维护中，请稍后再试");
			}
			
			//获取建行积分商城商品属性
			Long realizeDetailId = realizeRecord.getRealizeDetailId();
			Map<String, Object> propMap = realizeService.getItemPayParams(realizeDetailId);
			if(null == propMap){
				logger.info("平安变现数据出现异常: propMap is null");
				return resultJSON.toJSONString(-3,"支付失败，平安银行系统维护中，请稍后再试");
			}
			
			List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
			if(null == goodPropList){
				logger.info("平安变现数据出现异常:goodPropList is null");
				return resultJSON.toJSONString(-3,"支付失败，平安银行系统维护中，请稍后再试");
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
				logger.info("平安变现商品不存在:itemId is null");
				return resultJSON.toJSONString(-4,"该商品不存在");
			}
			
//			result = integralShopPAB.orderRealization(userid, imageCode, paramMap);
			paramMap.put("cellphone", "15394209984");
			result = integralShopPAB.orderThird(userid, paramMap);
			logger.info("积分变现平安银行,下单,用户ID：{},返回数据:{}",userid,result);
			code = JsonResult.getResult(result, "code");
		}
		
		if("2010".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.PAB_ORDER_PASSWORD, "下单成功,输入支付密码");
		}else if("2001".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.PAB_ORDER_PHONECODE, "下单成功,短信验证码发送成功");
		}else if("2007".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.PAB_ORDER_PASSWORD_AND_PHONECODE, "下单成功,请输入支付密码及短信验证码");
		}else if("3006".equals(code) || "1005".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.NOT_LOGIN_STATUS,"您的登录状态已过期,请重新登录");
		}else if("3004".equals(code)){
			imageCode = this.getOrderImageCode(userid);
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
	
	/**
	 * 发送支付验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySend 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月6日 下午8:13:35  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/send/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String paySend(Long userid) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}
		
//		String result = integralShopPAB.paySendRealization(userid);
		String result = integralShopPAB.paySendThird(userid);
		logger.info("平安银行积分变现,发送支付验证码,用户ID：{},返回数据:{}",userid,result);
		
		boolean flag = JsonResult.checkResult(result);
		if(flag){
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
	 * @param userid				用户ID
	 * @param phoneCode				短信验证码
	 * @param payPassWord			支付密码
	 * @param realizeRecordId		积分变现记录ID
	 * @param loginAccount			登录账号
	 * @param password				密码
	 * @return
	 * @date 2016年4月6日 下午8:24:31  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String pay(Long userid,String phoneCode,String payPassWord,Long realizeRecordId,String loginAccount,String password) {
		ApiResult<JSONObject> resultJSON = new ApiResult<JSONObject>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(null == realizeRecordId){
			return resultJSON.toJSONString(-2, "参数realizeRecordId不能为空");
		}
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			logger.info("平安变现数据出现异常:realizeRecord is null");
			return resultJSON.toJSONString(-3,"支付失败，平安银行系统维护中，请稍后再试");
		}
		
		//备注
		String memo = realizeRecord.getMemo();
		if(StringUtils.isBlank(memo)){
			logger.info("平安变现数据出现异常:memo is null");
			return resultJSON.toJSONString(-3,"支付失败，平安银行系统维护中，请稍后再试");
		}
		
		//获取必要数据
		JSONObject json = JSON.parseObject(memo);
		
		boolean flag = false;
		String result = "";
		
		if(appConfig.isDevMode){
			flag = true;
			logger.info("平安银行积分变现,支付,用户ID：{},开发模式",userid);
		}else{
			logger.info("平安银行积分变现,支付,用户ID：{},phoneCode:{},payPassWord:{},realizeRecordId:{}",userid,phoneCode,payPassWord,realizeRecordId);
//			result = integralShopPAB.payRealization(userid, phoneCode, payPassWord);
			result = integralShopPAB.payThird(userid, phoneCode, payPassWord);
			logger.info("平安银行积分变现,支付,用户ID：{},返回数据:{}",userid,result);
			flag = JsonResult.checkResult(result);
		}
		
		if(flag){
			Long remoteId = json.getLong("remoteId");
			Integer remoteType = json.getInteger("remoteType");
			Integer isAddUserTerm = json.getInteger("isAddUserTerm"); 
			if(null == isAddUserTerm){
				logger.info("ping an isAddUserTerm is null");
				return resultJSON.toJSONString(-2, "支付失败，平安银行系统维护中，请稍后再试");
			}
			
			UserAddTerm userAddTerm = new UserAddTerm();
			userAddTerm.setLoginAccount(loginAccount);
			
			JSONObject info = new JSONObject();
			info.put("loginAccount",loginAccount);
			info.put("password",password);
			
			//支付
			String orderNo = realizeService.realizePay(userid, realizeRecord, userAddTerm, json, 
																		info, false, remoteId, remoteType, isAddUserTerm,false);
			
			if(StringUtils.isBlank(orderNo)){
				return resultJSON.toJSONString(-1, "系统暂时出现异常,请稍后再试");
			}
			
			realizeService.saveRealizeCouponList(null,orderNo,userid,realizeRecordId,false);
			realizeService.realizeJob(realizeRecordId);
			
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
			
			try{
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 17L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("积分变现平安银行，自动更新失败：{}",e);
			}
				
			return resultJSON.toJSONString(0, "支付成功",jsonObject);
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
		if("3002".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.SYSTEM_BUSY, "支付失败，平安银行系统维护中，请稍后再试");
		}
		
		return reslut;
	}
}
