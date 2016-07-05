package com.caitu99.service.realization.controller.spider;

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
import com.caitu99.service.ishop.bcm.controller.api.spider.IntegralShopBOCOM;
import com.caitu99.service.realization.domain.RealizeCoupon;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 交通银行
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BOCOMShopController 
 * @author xiongbin
 * @date 2016年2月29日 下午3:17:56 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/realization/bocom/")
public class BOCOMShopController {
	
	private final static Logger logger = LoggerFactory.getLogger(BOCOMShopController.class);

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private UserService userService;
	@Autowired
	private RealizeService realizeService;
	@Autowired
	private IntegralShopBOCOM integralShopBOCOM;
	@Autowired
	private ItemService itemService;
	@Autowired
	ManualLoginService manualLoginService;

    /**
     * 登录
     * @Description: (方法职责详细描述,可空)  
     * @Title: login 
     * @param userid				用户ID
     * @param cardNo				卡号
     * @param password				查询密码
     * @param cardyear				信用卡年份
     * @param cardmonth				信用卡月份
     * @param realizeRecordId		积分变现记录ID
     * @return
     * @date 2016年2月29日 下午4:03:59  
     * @author xiongbin
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/login/submit/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String login(Long userid, String cardNo, String password, String cardyear, String cardmonth, Long realizeRecordId) {
        ApiResult<String> apiResult = new ApiResult<>();
        
        if(null == userid){
        	return apiResult.toJSONString(-1, "参数userid不能为空");
        }else if(StringUtils.isBlank(cardNo)){
        	return apiResult.toJSONString(-1, "参数cardNo不能为空");
        }else if(StringUtils.isBlank(password)){
        	return apiResult.toJSONString(-1, "参数password不能为空");
        }else if(StringUtils.isBlank(cardyear)){
        	return apiResult.toJSONString(-1, "参数cardyear不能为空");
        }else if(StringUtils.isBlank(cardmonth)){
        	return apiResult.toJSONString(-1, "参数cardmonth不能为空");
        }else if(null == realizeRecordId){
        	return apiResult.toJSONString(-1, "参数realizeRecordId不能为空");
        }
        
        User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}
		
		String phone = user.getMobile();
		if(StringUtils.isBlank(phone)){
			return apiResult.toJSONString(ApiResultCode.PHONE_NOT_NULL, "用户未绑定手机,请先绑定手机");
		}
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			return apiResult.toJSONString(-2, "数据出现异常");
		}
		
		//获取交通银行积分商城商品属性
		Long realizeDetailId = realizeRecord.getRealizeDetailId();
		Map<String, Object> propMap = realizeService.getItemPayParams(realizeDetailId);
		if(null == propMap){
			return apiResult.toJSONString(-3,"无此商品属性数据");
		}
		
		List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
		if(null == goodPropList){
			return apiResult.toJSONString(-3,"无此商品属性数据");
		}
		
		Map<String,String> paramMap = new HashMap<String,String>();
		for(GoodProp goodProp : goodPropList){
			paramMap.put(goodProp.getName(), goodProp.getValue());
		}

	    Long price = (Long)propMap.get("price");
	    Integer quantity = (Integer)propMap.get("quantity");
		
		paramMap.put("count", quantity.toString());
		paramMap.put("price", price.toString());
        paramMap.put("mobile", phone);
        paramMap.put("cardMonth", cardmonth);
        paramMap.put("cardYear", cardyear);
        paramMap.put("originprice", paramMap.get("originPrice"));
        paramMap.put("prodid", paramMap.get("prodId"));
        
        if(appConfig.isDevMode){
            logger.info("积分变现交通银行,登录,用户ID：{},开发模式",userid);
        	return apiResult.toJSONString(0, "success");
        }
        
        //登录交通
        String reslut = integralShopBOCOM.login(userid, cardNo, password, paramMap);
        
        logger.info("积分变现交通银行,登录,用户ID：{},返回数据:{}",userid,reslut);
        
        boolean flag = JsonResult.checkResult(reslut, 2102);
        if(flag){
        	return apiResult.toJSONString(0, "success");
        }
        
        String code = JsonResult.getResult(reslut, "code");
		if("2107".equals(code)){
			return apiResult.toJSONString(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误","/realize_bocom/login.html?realizeRecordId="+realizeRecordId);
		}
		
		reslut = disposeCode(reslut);
		String message = JsonResult.getResult(reslut, "message");
		return apiResult.toJSONString(-1, message);
    }

    /**
     * 发送支付验证码
     * @Description: (方法职责详细描述,可空)  
     * @Title: pagSemdSMS 
     * @param userid		用户ID
     * @param cardYear		信用卡年份
     * @param cardMonth		信用卡月份
     * @return
     * @date 2016年2月29日 下午5:45:55  
     * @author xiongbin
     */
    @RequestMapping(value = "/pay/send/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String pagSemdSMS(Long userid,String cardyear,String cardmonth) {
        ApiResult<String> apiResult = new ApiResult<String>();
        
        if(null == userid){
        	return apiResult.toJSONString(-1, "参数userid不能为空");
        }else if(StringUtils.isBlank(cardmonth)){
        	return apiResult.toJSONString(-1, "参数cardMonth不能为空");
        }else if(StringUtils.isBlank(cardyear)){
        	return apiResult.toJSONString(-1, "参数cardYear不能为空");
        }
        
        if(appConfig.isDevMode){
			logger.info("积分变现交通银行,发送支付验证码,用户ID：{},开发模式",userid);
        	return apiResult.toJSONString(0, "短信发送成功");
        }
        
        String reslut = integralShopBOCOM.paySendSMS(userid, cardyear, cardmonth);
        boolean flag = JsonResult.checkResult(reslut, 2102);
        if(flag){
        	return apiResult.toJSONString(0, "短信发送成功");
        }

		reslut = disposeCode(reslut);
		String message = JsonResult.getResult(reslut, "message");
		return apiResult.toJSONString(-1, message);
    }

    /**
     * 支付
     * @Description: (方法职责详细描述,可空)  
     * @Title: pay 
     * @param userid				用户ID
     * @param cardNo				银行卡
     * @param password				查询密码
     * @param cardyear				信用卡年份
     * @param cardmonth				信用卡月份
     * @param smscode				短信验证码
     * @param realizeRecordId		积分变现记录ID
     * @return
     * @date 2016年2月29日 下午7:44:28  
     * @author xiongbin
     */
    @RequestMapping(value = "/pay/submit/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String pay(Long userid, String cardNo, String password, String cardyear, String cardmonth,String smscode,Long realizeRecordId) {
        ApiResult<JSONObject> apiResult = new ApiResult<JSONObject>();
        
        if(null == userid){
        	return apiResult.toJSONString(-1, "参数userid不能为空");
        }else if(StringUtils.isBlank(smscode)){
        	return apiResult.toJSONString(-1, "参数smscode不能为空");
        }else if(StringUtils.isBlank(cardNo)){
        	return apiResult.toJSONString(-2, "参数cardNo不能为空");
        }else if(StringUtils.isBlank(password)){
        	return apiResult.toJSONString(-2, "参数password不能为空");
        }else if(StringUtils.isBlank(cardyear)){
        	return apiResult.toJSONString(-2, "参数cardyear不能为空");
        }else if(StringUtils.isBlank(cardmonth)){
        	return apiResult.toJSONString(-2, "参数cardmonth不能为空");
        }else if(null == realizeRecordId){
        	return apiResult.toJSONString(-2, "参数realizeRecordId不能为空");
        }
        
        RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			return apiResult.toJSONString(-2, "数据出现异常");
		}
		
		//备注
		String memo = realizeRecord.getMemo();
		if(StringUtils.isBlank(memo)){
			return apiResult.toJSONString(-2, "数据出现异常");
		}
		
		//获取必要数据
		JSONObject json = JSON.parseObject(memo);

		//支付成功
        boolean flag = false;
        //支付成功 ,爬取到券码
        boolean flagCode = false;
        
        String reslut = "";
        
		if(appConfig.isDevMode){
			logger.info("积分变现交通银行,支付,用户ID：{},开发模式",userid);
			flag = true;
		}else{
			 //支付
	        reslut = integralShopBOCOM.pay(userid, smscode);
	        
	        logger.info("积分变现交通银行,支付,用户ID：{},返回数据:{}",userid,reslut);
	        
	        //支付成功,并爬取到券码(2104),支付成功,但爬取不到券码(2110)
	        String code = JsonResult.getResult(reslut, "code");
	        if("2104".equals(code) || "2110".equals(code)){
	        	flag = true;
	        }
	        if( "2104".equals(code)){
	        	flagCode = true;
	        }
		}
        
        if(flag){
			try {
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 13L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("积分变现交通银行，自动更新失败：{}",e);
			}
			
        	
        	Long remoteId = json.getLong("remoteId");
			Integer remoteType = json.getInteger("remoteType");
			Integer isAddUserTerm = json.getInteger("isAddUserTerm"); 
			if(null == isAddUserTerm){
				return apiResult.toJSONString(-2, "数据出现异常");
			}
			
			UserAddTerm userAddTerm = new UserAddTerm();
			userAddTerm.setCardNo(cardNo);
			userAddTerm.setLoginAccount(cardNo);
			
			JSONObject info = new JSONObject();
			info.put("cardNo",cardNo);
			info.put("password",password);
			info.put("cardyear",cardyear);
			info.put("cardmonth",cardmonth);
			
			//支付
			String orderNo = realizeService.realizePay(userid, realizeRecord, userAddTerm, json, 
																		info, false, remoteId, remoteType, isAddUserTerm,false);
			
			if(StringUtils.isBlank(orderNo)){
				return apiResult.toJSONString(-1, "系统暂时出现异常,请稍后再试");
			}

			logger.info("积分变现交通银行,积分变现支付,用户ID：{},订单号:{},realizeRecordId:{},memo:{}",userid,orderNo,realizeRecordId,memo);
			
			//商品ID
			Long itemId = realizeRecord.getItemId();
			Item item = itemService.selectByPrimaryKey(itemId);
			String itemName = item.getTitle();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("quantity", realizeRecord.getQuantity());
			jsonObject.put("itemName", itemName);
			jsonObject.put("orderNo", orderNo);
			jsonObject.put("cash", realizeRecord.getCash());
			jsonObject.put("realizeRecordId", realizeRecordId);
			
			if(flagCode){
				try {
					//获取交通券码
					String data = JsonResult.getResult(reslut, "data");
					JSONObject jsonObjct = JSON.parseObject(data);
					String[] tickets = jsonObjct.getString("ticket").split(",");
					String effective = jsonObjct.getString("effective");

					List<RealizeCoupon> realizeCouponList = new ArrayList<RealizeCoupon>();
					for(int i=0;i<tickets.length;i++){
						String ticket = tickets[i];
						
						RealizeCoupon realizeCoupon = new RealizeCoupon();
						realizeCoupon.setCode(ticket);
						realizeCoupon.setExpiryDate(effective);
						realizeCoupon.setPlatformId(realizeRecord.getPlatformId());
						realizeCoupon.setRealizeRecordId(realizeRecordId);
						
						realizeCouponList.add(realizeCoupon);
					}
					
					//保存券码
					realizeService.saveRealizeCouponList(realizeCouponList,orderNo,userid,realizeRecordId,true);
					
					logger.info("积分变现交通银行,保存券码,用户ID：{},订单号:{},realizeRecordId:{}",userid,orderNo,realizeRecordId);
	
					//启动定时任务返现财币
					realizeService.realizeJob(realizeRecordId,"BOCOM_SHOP");
				} catch (Exception e) {
					logger.error("保存交通券码出错:" + e.getMessage(),e);
					//修改变现记录的状态
					realizeService.updateRealizeRecordStatus(realizeRecordId, 1);
					return apiResult.toJSONString(-10, "系统暂时出现异常,无法获取您的券码,请手动输入",jsonObject);
				}
			}else{
				logger.info("积分变现交通银行,获取券码失败,用户自己输入,用户ID：{},订单号:{},realizeRecordId:{}",userid,orderNo,realizeRecordId);
				//修改变现记录的状态
				realizeService.updateRealizeRecordStatus(realizeRecordId, 1);
				return apiResult.toJSONString(-10, "系统暂时出现异常,无法获取您的券码,请手动输入",jsonObject);
			}
			
			return apiResult.toJSONString(0, "支付成功",jsonObject);
        }

		reslut = disposeCode(reslut);
		String message = JsonResult.getResult(reslut, "message");
		return apiResult.toJSONString(-1, message);
    }

    /**
     * 处理返回值
     * @Description: (方法职责详细描述,可空)  
     * @Title: disposeCode 
     * @param reslut
     * @return
     * @date 2016年4月18日 下午3:10:13  
     * @author xiongbin
     */
    private String disposeCode(String reslut){
		ApiResult<String> resultJSON = new ApiResult<String>();
		
		String code = JsonResult.getResult(reslut, "code");
		
		if("2107".equals(code) || "2110".equals(code) 
									|| "2114".equals(code) || "2109".equals(code) || "2106".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.SYSTEM_BUSY, "交通积分商城系统维护中,请稍后再试");
		}
		
		return reslut;
	}
}
