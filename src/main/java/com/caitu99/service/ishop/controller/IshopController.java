package com.caitu99.service.ishop.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.ishop.bcm.controller.api.BCMShopController;
import com.caitu99.service.ishop.ccb.controller.api.UserChinaConstructionBankShopController;
import com.caitu99.service.ishop.citic.controller.api.CiticShopController;
import com.caitu99.service.ishop.esurfing.controller.api.UserEsurfingShopController;
import com.caitu99.service.ishop.pab.controller.api.PABShopController;
import com.caitu99.service.ishop.unicom.controller.api.UnicomShopController;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/ishop/")
public class IshopController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(IshopController.class);
	
	@Autowired
	private UserChinaConstructionBankShopController ccbShopController;
	@Autowired
	private UserEsurfingShopController esurfingShopController;
	@Autowired
	private CiticShopController citicShopController;
	@Autowired
	private PABShopController pabShopController;
	@Autowired
	private BCMShopController bcmShopController;
	@Autowired
	private UnicomShopController unicomShopController;

	/**
	 * 自动登录积分变现平台
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginPlatform 
	 * @param userid
	 * @param platformId
	 * @param info
	 * @param orderNo
	 * @return
	 * @date 2016年5月11日 上午11:02:16  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/platform/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String loginPlatform(Long userid,Long platformId,String info,String orderNo){
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}else if(null == platformId){
			return result.toJSONString(-1, "参数platformId不能为空");
		}else if(StringUtils.isBlank(info)){
			return result.toJSONString(-1, "参数info不能为空");
		}else if(StringUtils.isBlank(orderNo)){
			return result.toJSONString(-1, "参数orderNo不能为空");
		}
		
		JSONObject json = JSON.parseObject(info);
		json.put("orderNo", orderNo);
		
		logger.info("积分商城自动登录,用户ID:{},platformId:{},info:{},orderNo:{}",userid,platformId,info,orderNo);
		
		//积分变现平台ID
		switch (platformId.intValue()) {
			case 1:{
				String loginAccount = json.getString("loginAccount");
				String password = json.getString("password");
				String cardNo = json.getString("cardNo");
				String reservedPhone = json.getString("reservedPhone");
				String redirectUrl = "/ccb/pay_code.html?order_no=" + orderNo;
				json.put("redirect", redirectUrl);
				
				String reslut = ccbShopController.login(userid, loginAccount, password, orderNo, null);
				String code = JsonResult.getResult(reslut, "code");
				if(ApiResultCode.SUCCEED.toString().equals(code)){
					reslut = ccbShopController.paySendSMS(userid, cardNo, reservedPhone);
					boolean flag = JsonResult.checkResult(reslut);
					if(flag){
						return result.toJSONString(0, "SUCCESS",json); 
					}
					
					return reslut;
				}else if("2460".equals(code) || "2452".equals(code)){
					//检测是否是图片验证码错误
					String imageCode = JsonResult.getResult(reslut, "data");
					json.put("imageCode", imageCode);
					return result.toJSONString(0, "SUCCESS",json); 
				}
				
				return reslut;
			}
			case 2:
			case 7:{
				String phone = json.getString("phone");
				String redirectUrl = "/esurfing/pay_code.html?order_no=" + orderNo;
				json.put("redirect", redirectUrl);
				
				String reslut = esurfingShopController.login(userid, phone, null);
				String code = JsonResult.getResult(reslut, "code");
				
				if(ApiResultCode.SUCCEED.toString().equals(code)){
					return result.toJSONString(0, "SUCCESS",json); 
				}else if("2460".equals(code) || "2452".equals(code)){
					String imageCode = JsonResult.getResult(reslut, "data");
					json.put("imageCode", imageCode);
					return result.toJSONString(0, "SUCCESS",json); 
				}
				
				return reslut;
			}
			case 3:{
				String loginAccount = json.getString("loginAccount");
				String password = json.getString("password");
				String redirectUrl = "/citic/citic_order.html?order_no=" + orderNo;
				json.put("redirect", redirectUrl);
				
				String reslut = citicShopController.login(userid, loginAccount, password, null, orderNo);
				String code = JsonResult.getResult(reslut, "code");
				if("3901".equals(code)){//登录图形验证码错误
					String imageCode = JsonResult.getResult(reslut, "data");
					json.put("imageCode", imageCode);
					return result.toJSONString(0, "SUCCESS",json); 
				}else if("1001".equals(code)){
					json.put("status", 0);
					return result.toJSONString(0, "SUCCESS",json); 
				}else if("1097".equals(code)){//需要输入短信验证码
					json.put("status", 1);
					return result.toJSONString(0, "SUCCESS",json); 
				}
		
				return reslut;
			}
			//交通
			case 4:{
				String cardNo = json.getString("cardNo");
				String password = json.getString("password");
				String cardyear = json.getString("cardyear");
				String cardmonth = json.getString("cardmonth");
				String redirectUrl = "/bank_jiaohang/code.html?orderno=" + orderNo;
				json.put("redirect", redirectUrl);
				
				String reslut = bcmShopController.login(userid, cardNo, password, cardyear, cardmonth, orderNo);
				boolean flag = JsonResult.checkResult(reslut,2102);
				if(flag){
					return result.toJSONString(0, "SUCCESS",json); 
				}
				
				return reslut;
			}
//			case 5:{
//				//移动，汉字验证码，需要用户手输
//				return result.toJSONString(0, "SUCCESS", info);
//			}
            case 6:{
				//联通
            	String phone = json.getString("phone");
				String password = json.getString("password");
				String redirectUrl = "/unicom/sms.html?orderno=" + orderNo;
				json.put("redirect", redirectUrl);
				json.put("orderNo", orderNo);
				
				String reslut = unicomShopController.login(userid, phone, password, null, "false");
				String code = JsonResult.getResult(reslut, "code");
				
				if("3821".equals(code)){
					reslut = unicomShopController.getSmsCode(userid, orderNo);
					code = JsonResult.getResult(reslut, "code");
					if("3822".equals(code)){
						return result.toJSONString(0, "SUCCESS",json); 
					}
				}else if("3813".equals(code)){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("code", ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR);
					jsonObject.put("message", "账号或密码错误");
					jsonObject.put("data", "/unicom/login.html?orderno="+orderNo);
					return jsonObject.toJSONString(); 
				}else if("2460".equals(code) || "3814".equals(code)){
					reslut = unicomShopController.getVcode(userid);
					boolean flagImage = JsonResult.checkResult(reslut,3810);
					if(flagImage){
						String imageCode = JsonResult.getResult(reslut, "data");
						json.put("imageCode", imageCode);
						return result.toJSONString(0, "SUCCESS",json); 
					}else{
						return result.toJSONString(-1,"获取图片验证码失败");
					}
				}
				
				return reslut;
            }
			//平安
			case 8:{
				String loginAccount = json.getString("loginAccount");
				String password = json.getString("password");
				String redirectUrl = "/pab/pay_skip.html";
				json.put("redirect", redirectUrl);
				
				String reslut = pabShopController.login(userid, loginAccount, password, null, orderNo);
				String code = JsonResult.getResult(reslut, "code");
				if(ApiResultCode.SUCCEED.toString().equals(code)){
					String mode = JsonResult.getResult(reslut, "data");
					json.put("mode", mode);
					return result.toJSONString(0, "SUCCESS",json); 
				}else if(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR.toString().equals(code)
										|| ApiResultCode.IMAGECODE_ERROR.toString().equals(code)){
					
					String imageCode = JsonResult.getResult(reslut, "data");
					json.put("imageCode", imageCode);
					return result.toJSONString(0, "SUCCESS",json); 
				}
				
				return reslut;
			}
			default:
				return result.toJSONString(-1, "暂不支持该平台");
		}
	}
}
