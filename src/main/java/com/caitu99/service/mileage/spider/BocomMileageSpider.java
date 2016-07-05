package com.caitu99.service.mileage.spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.ishop.bcm.controller.api.spider.IntegralShopBOCOM;
import com.caitu99.service.mileage.dao.AirlineCompanyMapper;
import com.caitu99.service.mileage.dao.MileageIntegralMapper;
import com.caitu99.service.mileage.domain.AirlineCompany;
import com.caitu99.service.mileage.domain.MileageIntegral;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 交通银行兑换里程
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BocomMileageSpider 
 * @author ws
 * @date 2016年4月28日 下午2:42:13 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/mileage/bocom/")
public class BocomMileageSpider {
	
	private final static Logger logger = LoggerFactory.getLogger(BocomMileageSpider.class);

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
	@Autowired
	AirlineCompanyMapper airlineCompanyMapper;
	@Autowired
	MileageIntegralMapper mileageIntegralMapper;

    /**
     * 登录并兑换
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: login 
     * @param userid
     * @param cardNo
     * @param password
     * @param cardyear
     * @param cardmonth
     * @param realizeRecordId
     * @return
     * @date 2016年4月28日 下午2:42:25  
     * @author ws
     */
	@RequestMapping(value = "/login/1.0", produces = "application/json;charset=utf-8")
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
		
        /*if(appConfig.isDevMode){
            logger.info("积分兑里程交通银行,登录,用户ID：{},开发模式",userid);
        	return apiResult.toJSONString(0, "success");
        }*/
		
		String memo = realizeRecord.getMemo();
		if(StringUtils.isBlank(memo)){
			return apiResult.toJSONString(-2, "数据出现异常");
		}
		//获取必要数据
		JSONObject json = JSON.parseObject(memo);
        Long flightCompanyId = json.getLong("flightCompanyId");
        AirlineCompany airlineCompany = airlineCompanyMapper.selectByPrimaryKey(flightCompanyId);
        Long mileageId = json.getLong("mileageId");
		MileageIntegral mileageIntegral = mileageIntegralMapper.selectByPrimaryKey(mileageId );
        
		//校验输入会员号是否正确
		Boolean checkFlag = checkMemberIdByCode(airlineCompany.getCode(),json.getString("memberId"));
		if(!checkFlag){
			return apiResult.toJSONString(3004, "您输入的航空会员卡号有误，请重新输入");
		}
		
        Map<String, String> paramMap = new HashMap<String, String>();
        Long userBonus = mileageIntegral.getIntegral();
		paramMap.put("memberId", json.getString("memberId"));
		paramMap.put("flightCompanyCode", airlineCompany.getCode());
		paramMap.put("useBonus", String.valueOf(userBonus));
		paramMap.put("validMonth", cardmonth);
		paramMap.put("validYear", "20"+cardyear);
		paramMap.put("flightCompanyName", airlineCompany.getName());
		paramMap.put("ebsNm1", json.getString("ebsNm1"));
		paramMap.put("ebsNm2", json.getString("ebsNm2"));
		//登录交通
        String reslut = this.login(userid, cardNo, password, paramMap );
        
        logger.info("积分变现交通银行,登录,用户ID：{},返回数据:{}",userid,reslut);
        
        boolean flag = JsonResult.checkResult(reslut, 3005);//发送短信验证码
        if(flag){
        	reslut = this.sendSMS(userid);
            if(JsonResult.checkResult(reslut)){
            	Map<String,String> res = new HashMap<String, String>();
            	String mobTel = JsonResult.getResult(reslut, "data");
            	if(null != mobTel && mobTel.trim().length() > 4){//取尾号后4位
            		mobTel = mobTel.substring(mobTel.trim().length()-4);
            	}
            	res.put("userBonus", String.valueOf(userBonus));
            	res.put("mobTel", mobTel);
            	return apiResult.toJSONString(0, "短信发送成功",JSON.toJSONString(res));
            }else{
            	return reslut;
            }
        }
        
        String code = JsonResult.getResult(reslut, "code");
		if("2107".equals(code)){
			return apiResult.toJSONString(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误","/mileage_bocom/login.html?realizeRecordId="+realizeRecordId);
		}
		
		reslut = disposeCode(reslut);
		String message = JsonResult.getResult(reslut, "message");
		return apiResult.toJSONString(-1, message);
    }

    /**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkMemberIdByCode 
	 * @param flightCode
	 * @param long1
	 * @return
	 * @date 2016年5月3日 上午9:50:48  
	 * @author ws
	*/
	private Boolean checkMemberIdByCode(String flightCode, String memberId) {
		Pattern pat = Pattern.compile("\\d{12}");
		Matcher matcher1 = pat.matcher(memberId); 
		if("LP04".equals(flightCode) || "LP06".equals(flightCode)){
			if(!matcher1.matches()){
				//jAlert("error","请输12位数字！");
				return false;
			}
		}
		
		Pattern pat2 = Pattern.compile("\\d{10}");
		Matcher matcher2 = pat2.matcher(memberId); 
		if("LP01".equals(flightCode) || "LP07".equals(flightCode)){
			if(!matcher2.matches()){
				//jAlert("error","请输10位数字！");
				return false;
			}
		}
		
		Pattern pat3 = Pattern.compile("CA(\\d{12}|\\d{9})");
		Matcher matcher3 = pat3.matcher(memberId); 
		if("LP03".equals(flightCode)){
			if(!matcher3.matches()){
				//jAlert("error","请输入CA+9位或12位数字！");
				return false;
			}
		}
		
		return true;
	}
	

	/**
     * 发送兑换短信验证码
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: pagSemdSMS 
     * @param userid
     * @return
     * @date 2016年4月28日 下午2:42:35  
     * @author ws
     */
    @RequestMapping(value = "/send/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String SemdSMS(Long userid) {
        ApiResult<String> apiResult = new ApiResult<String>();
        
        if(null == userid){
        	return apiResult.toJSONString(-1, "参数userid不能为空");
        }
        
        /*if(appConfig.isDevMode){
			logger.info("积分变现交通银行,发送支付验证码,用户ID：{},开发模式",userid);
        	return apiResult.toJSONString(0, "短信发送成功");
        }*/
        
        String reslut = this.sendSMS(userid);
        if(JsonResult.checkResult(reslut)){
        	return apiResult.toJSONString(0, "短信验证码发送成功");
        }

		reslut = disposeCode(reslut);
		String message = JsonResult.getResult(reslut, "message");
		return apiResult.toJSONString(-1, message);
    }

    /**
     * 兑换
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: pay 
     * @param userid
     * @param cardNo
     * @param password
     * @param cardyear
     * @param cardmonth
     * @param smscode
     * @param realizeRecordId
     * @return
     * @date 2016年4月28日 下午2:42:46  
     * @author ws
     */
    @RequestMapping(value = "/convert/1.0", produces = "application/json;charset=utf-8")
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
        
        String reslut = "";
        
		if(appConfig.isDevMode){
			logger.info("积分变现交通银行,支付,用户ID：{},开发模式",userid);
			flag = true;
		}else{
			 //兑换
	        reslut = this.pay(userid, smscode);
	        
	        logger.info("积分变现交通银行,支付,用户ID：{},返回数据:{}",userid,reslut);
	        
	        //兑换成功
	        if(JsonResult.checkResult(reslut)){
	        	flag = true;
	        }
		}
        
        if(flag){
			try {
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 13L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("积分兑里程交通银行，自动更新失败：{}",e);
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
			JSONObject jsonObject = realizeService
					.convertMileage(userid, realizeRecord, userAddTerm, json, 
									info, remoteId, remoteType, isAddUserTerm);

			//logger.info("积分变现交通银行,积分变现支付,用户ID：{},订单号:{},realizeRecordId:{},memo:{}",userid,orderNo,realizeRecordId,memo);
			cardNo = cardNo.substring(cardNo.length() - 4);
			jsonObject.put("cardNo", cardNo);
			jsonObject.put("integral", realizeRecord.getIntegral());
			jsonObject.put("realizeRecordId", realizeRecordId);
			
			return apiResult.toJSONString(0, "支付成功", jsonObject);
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
			|| "2114".equals(code) || "2109".equals(code) 
			|| "2106".equals(code) || "3002".equals(code)){
			return resultJSON.toJSONString(ApiResultCode.SYSTEM_BUSY, "交通积分商城系统维护中,请稍后再试");
		}
		
		return reslut;
	}
    
    
    
    /**
     * 登录和兑换
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: login 
     * @param userid
     * @param cardNo
     * @param password
     * @param paramMap
     * @return
     * @date 2016年4月28日 上午10:34:21  
     * @author ws
     */
	public String login(Long userid,String cardNo,String password,Map<String,String> paramMap){
		try {
			String order_url = appConfig.spiderUrl + "/bocom/submit/1.0";

			paramMap.put("userid", userid.toString());
			paramMap.put("account", cardNo);
			paramMap.put("password", password);
			
			String result = HttpClientUtils.getInstances().doPost(order_url, "UTF-8", paramMap);
			logger.info("交通银行兑里程返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("交通银行兑里程登录失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 发送短信验证码
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: sendSMS 
	 * @param userid
	 * @return
	 * @date 2016年4月28日 上午11:05:37  
	 * @author ws
	 */
	public String sendSMS(Long userid) {
		try {
			String url = appConfig.spiderUrl + "/bocom/sms/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", String.valueOf(userid));
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("交通银行兑里程发送验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("交通银行兑里程发送验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 兑换
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userid
	 * @param phoneCode
	 * @return
	 * @date 2016年4月28日 上午11:05:45  
	 * @author ws
	 */
	public String pay(Long userid, String phoneCode) {
		try {
			String url = appConfig.spiderUrl + "/bocom/convert/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
	        paramMap.put("userid", String.valueOf(userid));
	        paramMap.put("smscode", phoneCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("交通兑里程返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("交通兑里程失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
}
