package com.caitu99.service.realization.controller.spider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.alibaba.fastjson.JSONArray;
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
import com.caitu99.service.ishop.esurfing.controller.api.spider.IntegralShopEsurfing;
import com.caitu99.service.realization.controller.RealizationController;
import com.caitu99.service.realization.domain.RealizeCoupon;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.RealizeRecordEsurfing;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeRecordEsurfingService;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/realization/esurfing/")
public class EsurfingShopController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(EsurfingShopController.class);
	
	@Autowired
	private IntegralShopEsurfing integralShopEsurfing;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private RealizeService realizeService;
	@Autowired
	private RealizationController realizationController;
	@Autowired
	private RealizeRecordEsurfingService realizeRecordEsurfingService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	ManualLoginService manualLoginService;
	
	/**
	 * 发送登录验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid		用户ID
	 * @param phone			手机号
	 * @param imageCode		图片验证码
	 * @return
	 * @date 2016年2月17日 下午3:58:32  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/send/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String loginSend(Long userid,String phone,String imageCode) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(-1, "参数phone不能为空");
		}
		
		if(appConfig.isDevMode){
			logger.info("积分变现天翼,发送登录验证码,用户ID：{},开发模式",userid);
			return resultJSON.toJSONString(0, "短信发送成功");
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
				logger.info("天翼变现获取图片验证码失败");
				return resultJSON.toJSONString(-1,"支付失败，天翼系统维护中，请稍后再试");
			}
		}

		//登录天翼
		String result = integralShopEsurfing.loginSend(userid, phone, imageCode);
		
        logger.info("积分变现天翼,发送登录验证码,用户ID：{},返回数据:{}",userid,result);
		
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
				logger.info("天翼变现获取图片验证码失败");
				return resultJSON.toJSONString(-1,"支付失败，天翼系统维护中，请稍后再试");
			}
			return resultJSON.toJSONString(ApiResultCode.IMAGECODE_ERROR,"图片验证码不正确",imageCode);
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid
	 * @param phone
	 * @param msCode
	 * @param realizeRecordId
	 * @return
	 * @date 2016年3月23日 下午6:11:07  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/submit/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid,String phone,String msCode,Long realizeRecordId) {
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(-1, "参数phone不能为空");
		}else if(StringUtils.isBlank(msCode)){
			return resultJSON.toJSONString(-1, "参数msCode不能为空");
		}else if(null == realizeRecordId){
			return resultJSON.toJSONString(-1, "参数realizeRecordId不能为空");
		}
		
		String result = "";
		boolean flag = false;
		
		if(appConfig.isDevMode){
			logger.info("积分变现天翼,登录,用户ID：{},开发模式",userid);
			flag = true;
		}else{
			//登录
			result = integralShopEsurfing.login(userid, phone, msCode);
			flag = JsonResult.checkResult(result);
	        logger.info("积分变现天翼,登录,用户ID：{},返回数据:{}",userid,result);
		}
		
		if(flag){
			result = this.pay(userid, phone, realizeRecordId);
			return result;
		}
		
		result = disposeCode(result);
		String message = JsonResult.getResult(result, "message");
		return resultJSON.toJSONString(-1, message);
	}
	
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
	public String pay(Long userid,String phone,Long realizeRecordId) {
		ApiResult<JSONObject> resultJSON = new ApiResult<JSONObject>();
		
		if(null == userid){
			return resultJSON.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(phone)){
			return resultJSON.toJSONString(-2, "参数phone不能为空");
		}else if(null == realizeRecordId){
			return resultJSON.toJSONString(-2, "参数realizeRecordId不能为空");
		}
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			logger.info("天翼变现数据异常：realizeRecord is null");
			return resultJSON.toJSONString(-2,"支付失败，天翼系统维护中，请稍后再试");
		}
		
		//获取天翼积分商城商品属性
		Long realizeDetailId = realizeRecord.getRealizeDetailId();
		Map<String, Object> propMap = realizeService.getItemPayParams(realizeDetailId);
		if(null == propMap){
			logger.info("天翼变现数据异常：propMap is null");
			return resultJSON.toJSONString(-3,"支付失败，天翼系统维护中，请稍后再试");
		}
		
		List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
		if(null == goodPropList){
			logger.info("天翼变现数据异常：goodPropList is null");
			return resultJSON.toJSONString(-3,"支付失败，天翼系统维护中，请稍后再试");
		}
		
		Map<String,String> paramMap = new HashMap<String,String>();
		for(GoodProp goodProp : goodPropList){
			paramMap.put(goodProp.getName(), goodProp.getValue());
		}

		Long itemId = (Long)propMap.get("itemId");
	    Long price = (Long)propMap.get("price");
	    Integer quantity = (Integer)propMap.get("quantity");
		
		paramMap.put("buyNum", quantity.toString());
		paramMap.put("payTotal", String.valueOf(price * quantity));
		
		//检测用户购买商品是否超过限制
		Long sum = realizeRecordEsurfingService.selectUserBuyIntegralCount(userid, phone, itemId);
		//爱奇艺限制商品ID
		Long realizeEsurfingLimitIQIYIItemId = Long.parseLong(appConfig.realizeEsurfingLimitIQIYIItemId); 
		//中石油限制商品ID
		Long realizeEsurfingLimitCNPCItemId = Long.parseLong(appConfig.realizeEsurfingLimitCNPCItemId); 
		
		if(itemId.equals(realizeEsurfingLimitIQIYIItemId)){
			//爱奇艺限制积分数
			Long realizeEsurfingLimitIQIYIIntegral = Long.parseLong(appConfig.realizeEsurfingLimitIQIYIIntegral);
			
			if(price+sum > realizeEsurfingLimitIQIYIIntegral){
				return resultJSON.toJSONString(-1, "您已超出今日限额，请选择更小金额或24小时候后再试");
			}
		}else if(itemId.equals(realizeEsurfingLimitCNPCItemId)){
			//中石化限制积分数
			Long realizeEsurfingLimitICNPCIntegral = Long.parseLong(appConfig.realizeEsurfingLimitICNPCIntegral);

			if(price+sum > realizeEsurfingLimitICNPCIntegral){
				return resultJSON.toJSONString(-1, "您已超出今日限额，请选择更小金额或24小时候后再试");
			}
		}
		
		String result = "";
		//是否支付成功
		boolean flag = false;
		//是否爬取券码
		boolean flagCoupon = true;
		
		if(appConfig.isDevMode){
			logger.info("积分变现天翼,支付,用户ID：{},开发模式",userid);
			flag = true;
			flagCoupon = false;
		}else{
			//天翼生成订单,并完成订单
			result = integralShopEsurfing.orderGenerate(userid, paramMap);
        	logger.info("积分变现天翼,支付,用户ID：{},返回数据:{}",userid,result);
			flag = JsonResult.checkResult(result);
		}
		
		if(flag){
			//记录用户购买记录
			RealizeRecordEsurfing realizeRecordEsurfing = new RealizeRecordEsurfing();
			realizeRecordEsurfing.setIntegral(price);
			realizeRecordEsurfing.setItemId(itemId);
			realizeRecordEsurfing.setUserId(userid);
			realizeRecordEsurfing.setLoginAccount(phone);
			realizeRecordEsurfingService.insert(realizeRecordEsurfing);
			
			//备注
			String memo = realizeRecord.getMemo();
			if(StringUtils.isBlank(memo)){
				logger.info("天翼变现数据异常：memo is null");
				return resultJSON.toJSONString(-2,"支付失败，天翼系统维护中，请稍后再试");
			}
			
			//获取必要数据
			JSONObject json = JSON.parseObject(memo);
			Long remoteId = json.getLong("remoteId");
			Integer remoteType = json.getInteger("remoteType");
			Integer isAddUserTerm = json.getInteger("isAddUserTerm"); 
			if(null == isAddUserTerm){
				logger.info("天翼变现数据异常：isAddUserTerm is null");
				return resultJSON.toJSONString(-2,"支付失败，天翼系统维护中，请稍后再试");
			}
			
			UserAddTerm userAddTerm = new UserAddTerm();
			userAddTerm.setLoginAccount(phone);
			
			JSONObject info = new JSONObject();
			info.put("phone",phone);
			
			//支付
			String orderNo = realizeService.realizePay(userid, realizeRecord, userAddTerm, json, 
																info, false, remoteId, remoteType, isAddUserTerm,false);
			
			if(StringUtils.isBlank(orderNo)){
				return resultJSON.toJSONString(-1, "系统暂时出现异常,请稍后再试");
			}
			
			if(StringUtils.isBlank(orderNo)){
				return resultJSON.toJSONString(-1, "系统暂时出现异常,请稍后再试");
			}
			
			logger.info("积分变现天翼,积分变现支付,用户ID：{},订单号:{},realizeRecordId:{},memo:{}",userid,orderNo,realizeRecordId,memo);
			
			Item item = itemService.selectByPrimaryKey(itemId);
			String itemName = item.getTitle();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("quantity", quantity);
			jsonObject.put("itemName", itemName);
			jsonObject.put("orderNo", orderNo);
			jsonObject.put("cash", realizeRecord.getCash());
			jsonObject.put("realizeRecordId", realizeRecordId);
			
			try {
				if(!flagCoupon){
					logger.info("积分变现天翼,不爬取券码,用户ID：{},开发模式",userid);
					//修改变现记录的状态
					realizeService.updateRealizeRecordStatus(realizeRecordId, 1);
					return resultJSON.toJSONString(-10, "系统暂时出现异常,无法获取您的券码,请手动输入",jsonObject);
				}
				
				//获取天翼券码
				result = integralShopEsurfing.getCode(userid);
				flag = JsonResult.checkResult(result);
				if(!flag){
					return resultJSON.toJSONString(-9, JsonResult.getResult(result, "message"),jsonObject);
				}
				
				List<RealizeCoupon> realizeCouponList = new ArrayList<RealizeCoupon>();
				JSONArray jsonArray = JSON.parseArray(JsonResult.getResult(result, "data"));
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObjectCode = jsonArray.getJSONObject(i);
					String cardNo = jsonObjectCode.getString("CardNo");
					String expressDate = jsonObjectCode.getString("ExpressDate");
					String password = jsonObjectCode.getString("CardPWD");
					//过期日期不为空处理	mod by chencheng 20160329
					if(StringUtils.isNotBlank(expressDate)){
						if(expressDate.contains("/")){
							expressDate = DateUtil.getDate(new SimpleDateFormat("yyyy/MM/dd").parse(expressDate));
						}
					}
					
					if(StringUtils.isBlank(password)){
						password = "";
					}
					
					RealizeCoupon realizeCoupon = new RealizeCoupon();
					realizeCoupon.setCode(cardNo);
					realizeCoupon.setExpiryDate(expressDate);
					realizeCoupon.setPlatformId(realizeRecord.getPlatformId());
					realizeCoupon.setRealizeRecordId(realizeRecordId);
					realizeCoupon.setPwd(password);
					
					realizeCouponList.add(realizeCoupon);
				}
				
				//保存券码
				realizeService.saveRealizeCouponList(realizeCouponList,orderNo,userid,realizeRecordId,true);
				
				logger.info("积分变现天翼,保存券码,用户ID：{},订单号:{},realizeRecordId:{}",userid,orderNo,realizeRecordId);

				//启动定时任务返现财币
				realizeService.realizeJob(realizeRecordId);
			} catch (Exception e) {
				logger.error("保存天翼券码出错:" + e.getMessage(),e);
				//修改变现记录的状态
				realizeService.updateRealizeRecordStatus(realizeRecordId, 1);
				return resultJSON.toJSONString(-10, "系统暂时出现异常,无法获取您的券码,请手动输入",jsonObject);
			}
			
			try {
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 4L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("天翼积分变现，自动更新失败：{}",e);
			}
			
			return resultJSON.toJSONString(0, "支付成功",jsonObject);
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
	 * @date 2016年3月23日 下午6:30:18  
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
			logger.info("积分变现天翼,发送支付验证码,用户ID：{},开发模式",userid);
			return resultJSON.toJSONString(0, "发送成功");
		}
		
		//登录
		String result = integralShopEsurfing.paySMS(userid);

        logger.info("积分变现天翼,发送支付验证码,用户ID：{},返回数据:{}",userid,result);
		
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			return resultJSON.toJSONString(0, "发送成功");
		}
		
		String code = JsonResult.getResult(result, "code");
		if("1005".equals(code) || "1098".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.NOT_LOGIN_STATUS, "您的登录状态已过期,请重新登录");
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
		if("1117".equals(code) || "1118".equals(code) || "1113".equals(code)|| "1067".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.SYSTEM_BUSY, "支付失败，天翼系统维护中，请稍后再试");
		}
		
		return reslut;
	}
}
