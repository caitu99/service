package com.caitu99.service.realization.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.caitu99.service.ishop.ccb.controller.api.UserChinaConstructionBankShopController;
import com.caitu99.service.ishop.cm.controller.api.IShopController;
import com.caitu99.service.ishop.esurfing.controller.api.UserEsurfingShopController;
import com.caitu99.service.realization.controller.spider.BOCOMShopController;
import com.caitu99.service.realization.controller.spider.CCBShopController;
import com.caitu99.service.realization.controller.spider.CiticCreditShopController;
import com.caitu99.service.realization.controller.spider.EsurfingShopController;
import com.caitu99.service.realization.controller.spider.PABController;
import com.caitu99.service.realization.controller.spider.UnicomController;
import com.caitu99.service.realization.domain.PhoneAmount;
import com.caitu99.service.realization.domain.PhoneRealizeDetail;
import com.caitu99.service.realization.domain.RealizePlatform;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.dto.PhoneDetailDto;
import com.caitu99.service.realization.service.PhoneAmountService;
import com.caitu99.service.realization.service.PhoneRealizeDetailService;
import com.caitu99.service.realization.service.RealizePlatformService;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.realization.service.UserTermService;
import com.caitu99.service.transaction.controller.vo.RechargeResult;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.VersionUtil;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/phone/recharge/")
public class IntegralPayController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(IntegralPayController.class);

	private final static String[] REALIZATION_PLATFORM_LIST_FILTER = {"realizePlatformId","name","type","icon","sort"};

	private final static String[] REALIZE_LIST_FILTER = {"realizeId","integral","cash","propotion"};

	@Autowired
	private UserTermService userTermService;
	
	@Autowired
	private RealizePlatformService realizationPlatformService;
	
	@Autowired
	private RealizeService realizeService;
	
	@Autowired
	private CCBShopController ccbShopController;
	
	@Autowired
	private UserChinaConstructionBankShopController userChinaConstructionBankShopController;
	
	@Autowired
	private EsurfingShopController esurfingShopController;

	@Autowired
	private CiticCreditShopController citicCreditShopController;

    @Autowired
    private UnicomController unicomController;
	
	@Autowired
	private UserEsurfingShopController userEsurfingShopController;
	
	@Autowired
	private IShopController ishopController;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private BOCOMShopController bocomShopController;
	
	@Autowired
	private PABController pabController;
	
	@Autowired
	private PhoneAmountService phoneAmountService;
	
	@Autowired
	private PhoneRealizeDetailService phoneRealizeDetailService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="amount/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getPhoneAmountList(){
		ApiResult<List<PhoneAmount>> result = new ApiResult<List<PhoneAmount>>();
		List<PhoneAmount> phoneAmountList = phoneAmountService.getPhoneAmountList();
		return result.toJSONString(0, "列表获取成功", phoneAmountList);
	}
	
	/**
	 * 
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: userlist 
	 * @param userid
	 * @param request
	 * @return
	 * @date 2016年4月12日 下午2:55:40  
	 * @author ws
	 */
	@RequestMapping(value="account/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String userlist(Long userid,HttpServletRequest request) {
		ApiResult<List<JSONObject>> result = new ApiResult<List<JSONObject>>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}
		
		String version = VersionUtil.getAppVersion(request);
		if(StringUtils.isBlank(version)){
			logger.warn("未传递版本号");
			return result.toJSONString(-1, "请传递版本号");
		}else{
			logger.info("APP版本号为:" + version);
		}
		Long versionLong = VersionUtil.getVersionLong(version);
		//只需获取所有绑定了变现的账号列表
		List<JSONObject> filterList = new ArrayList<JSONObject>();
		List<JSONObject> list = userTermService.selectRealizationList(userid,versionLong.toString(),true);
		//过滤非该平台的
		for (JSONObject jsonObject : list) {//目前只支持建设银行
			if(1 == jsonObject.getInteger("platformId")){
				filterList.add(jsonObject);
			}
		}
		return result.toJSONString(0,"success",filterList);
	}
	
	/**
	 * 获取兑换方式明细
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getPhoneRealizeDetail 
	 * @param platformId
	 * @param amountId
	 * @return
	 * @date 2016年4月13日 下午5:58:41  
	 * @author ws
	 */
	@RequestMapping(value="phone/detail/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getPhoneRealizeDetail(Long platformId,Long amountId){
		ApiResult<PhoneRealizeDetail> result = new ApiResult<PhoneRealizeDetail>();
		PhoneRealizeDetail detail = phoneRealizeDetailService.selectBy(platformId,amountId);
		return result.toJSONString(0, "获取成功", detail);
	}
	
	
	
	@RequestMapping(value="tel/detail/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getPhoneRealizeDetail2(Long userid,Long amountId){
		ApiResult<Map<String,String>> result = new ApiResult<Map<String,String>>();
		Map<String,String> map = phoneRealizeDetailService.queryAccountByPrice(userid, amountId);
		return result.toJSONString(0, "获取成功", map);
	}
	
	/**
	 * 获取所有支持积分兑话费的平台列表
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: list 
	 * @param request
	 * @return
	 * @date 2016年4月12日 下午2:57:03  
	 * @author ws
	 */
	@RequestMapping(value="platform/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String list(HttpServletRequest request) {
		ApiResult<List<RealizePlatform>> result = new ApiResult<List<RealizePlatform>>();
		String version = VersionUtil.getAppVersion(request);
		if(StringUtils.isBlank(version)){
			logger.warn("未传递版本号");
			return result.toJSONString(-1, "请传递版本号");
		}else{
			logger.info("APP版本号为:" + version);
		}
		Long versionLong = VersionUtil.getVersionLong(version);
		String support = ",1,";//查询支持话费充值的平台
		List<RealizePlatform> list = realizationPlatformService.selectBySupport(versionLong.toString(),support);
		
		return result.toJSONString(0,"success",list,RealizePlatform.class,REALIZATION_PLATFORM_LIST_FILTER);
	}
	
	/**
	 * 
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userId
	 * @param platformId
	 * @param remoteId
	 * @param remoteType
	 * @param info
	 * @param amountId
	 * @param isAddUserTerm
	 * @param phoneNo
	 * @return
	 * @date 2016年4月13日 下午2:22:28  
	 * @author ws
	 */
	@RequestMapping(value="redirect/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String phonePay(Long userId,Long platformId,Long remoteId,Integer remoteType
			,String info,Long amountId,Integer isAddUserTerm,String phoneNo){
		ApiResult<String> result = new ApiResult<String>();
		if(null == platformId || null == isAddUserTerm
				|| null == amountId || StringUtils.isBlank(phoneNo)){
			return result.toJSONString(-1, "参数不能为空");
		}

		
/*		RechargeResult rechargeResult = new RechargeResult();
		rechargeResult = realizeService.checkPhoneLimit(userId,amountId);
		if(!rechargeResult.isSuccess()){//验证不通过
			return result.toJSONString(2219, rechargeResult.getResult());
		}*/
		
		JSONObject memo = new JSONObject();
		memo.put("remoteId", remoteId);
		memo.put("remoteType", remoteType);
		memo.put("info", info);
		memo.put("isAddUserTerm", isAddUserTerm);
		memo.put("phoneNo", phoneNo);
		memo.put("amountId", amountId);
		
		//生成积分变现记录
		Long realizeRecordId = realizeService.savePhoneIntegralRecord(userId, platformId, memo.toJSONString(),amountId);
		
		StringBuilder redirct = new StringBuilder();
		redirct.append(appConfig.caituUrl);
		
		if(StringUtils.isBlank(info)){
			redirct.append("/transitional/transtional.html?redirect=");
		}else{
			redirct.append("/phone_transitional/transtional.html?redirect=");
		}
		
		String loginAccount = "";
		
		//建设
		if(1 == platformId.longValue()){
			if(StringUtils.isBlank(info)){
				redirct.append("/phone_ccb/login.html");
			}else{
				redirct.append("/phone_ccb/pay_code.html");
			}
		} else {
			return result.toJSONString(-1, "暂不支持该平台");
		}
		
		redirct.append("?realizeRecordId=").append(realizeRecordId).append("&loginAccount=").append(loginAccount);
		
		logger.info("积分变现H5跳转入口,用户ID:{},platformId:{},memo:{},realizeRecordId:{},redirct:{}",
												userId,platformId,memo.toJSONString(),realizeRecordId,redirct);
		
		return result.toJSONString(0, "success",redirct.toString());
	}
	
	

	
	
	/**
	 * 自动登录积分变现平台
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginPlatform 
	 * @param userid				用户ID
	 * @param realizeRecordId		积分变现记录ID
	 * @return
	 * @date 2016年2月25日 下午8:22:33  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/platform/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String loginPlatform(Long userid,Long realizeRecordId){
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}else if(null == realizeRecordId){
			return result.toJSONString(-2, "参数realizeRecordId不能为空");
		}
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			return result.toJSONString(-2, "数据错误");
		}
		
		String memo = realizeRecord.getMemo();
		JSONObject json = JSON.parseObject(memo);
		JSONObject info = JSON.parseObject(json.getString("info"));
		
		logger.info("积分变现自动登录,用户ID:{},realizeRecordId:{},memo:{}",userid,realizeRecordId,memo);
		
		//积分变现平台ID
		Long platformId = realizeRecord.getPlatformId();
		switch (platformId.intValue()) {
			case 1:{
				String redirectUrl = "/phone_ccb/login.html?realizeRecordId=";
				
				String loginAccount = info.getString("loginAccount");
				String password = info.getString("password");
				String cardNo = info.getString("cardNo");
				String reservedPhone = info.getString("reservedPhone");
				
				String reslut = ccbShopController.order(userid, realizeRecordId);
				String code = JsonResult.getResult(reslut, "code");
				if(ApiResultCode.SUCCEED.toString().equals(code)){
					logger.info("建行积分变现自动登录,用户ID:{},登录状态.",userid);
					reslut = userChinaConstructionBankShopController.paySendSMS(userid, cardNo, reservedPhone);
					boolean flag = JsonResult.checkResult(reslut);
					if(flag){
						return result.toJSONString(0, "SUCCESS",info); 
					}
					
					return reslut;
				}else if(ApiResultCode.NOT_LOGIN_STATUS.toString().equals(code)){
					logger.info("建行积分变现自动登录,用户ID:{},未登陆状态.",userid);
					reslut = ccbShopController.login(userid, loginAccount, password, realizeRecordId, null, redirectUrl);
					boolean flag = JsonResult.checkResult(reslut);
					if(flag){
						reslut = userChinaConstructionBankShopController.paySendSMS(userid, cardNo, reservedPhone);
						flag = JsonResult.checkResult(reslut);
						if(flag){
							return result.toJSONString(0, "SUCCESS",info); 
						}
						
						return reslut;
					}
					
					//检测是否是图片验证码错误
					code = JsonResult.getResult(reslut, "code");
					if("2460".equals(code) || "2452".equals(code)){
						String imageCode = JsonResult.getResult(reslut, "data");
						JSONObject reslutJSON = new JSONObject();
						reslutJSON.putAll(info);
						reslutJSON.put("imageCode", imageCode);
						return result.toJSONString(0, "SUCCESS",reslutJSON); 
					}
				}
				
				return reslut;
			}
			default:
				return result.toJSONString(-1, "暂不支持该平台");
		}
	}
	
}
