package com.caitu99.service.mileage.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.caitu99.service.mileage.domain.AirlineCompany;
import com.caitu99.service.mileage.domain.MileageIntegral;
import com.caitu99.service.mileage.domain.UserAirline;
import com.caitu99.service.mileage.service.AirlineCompanyService;
import com.caitu99.service.mileage.service.MileageIntegralService;
import com.caitu99.service.mileage.service.UserAirlineService;
import com.caitu99.service.mileage.spider.BocomMileageSpider;
import com.caitu99.service.realization.controller.spider.CCBShopController;
import com.caitu99.service.realization.controller.spider.CiticCreditShopController;
import com.caitu99.service.realization.controller.spider.EsurfingShopController;
import com.caitu99.service.realization.controller.spider.PABController;
import com.caitu99.service.realization.controller.spider.UnicomController;
import com.caitu99.service.realization.domain.RealizePlatform;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.service.PhoneAmountService;
import com.caitu99.service.realization.service.PhoneRealizeDetailService;
import com.caitu99.service.realization.service.RealizePlatformService;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.realization.service.UserTermService;
import com.caitu99.service.utils.VersionUtil;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 兑航空里程
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MileageConvertController 
 * @author ws
 * @date 2016年4月27日 下午3:16:17 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/mileage/")
public class MileageConvertController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(MileageConvertController.class);

	private final static String[] REALIZATION_PLATFORM_LIST_FILTER = {"realizePlatformId","name","type","icon","sort"};

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
	private BocomMileageSpider bocomMileageSpider;
	
	@Autowired
	private PABController pabController;
	
	@Autowired
	private PhoneAmountService phoneAmountService;
	
	@Autowired
	private PhoneRealizeDetailService phoneRealizeDetailService;
	
	@Autowired
	private MileageIntegralService mileageIntegralService;
	@Autowired
	private AirlineCompanyService airlineCompanyService;
	@Autowired
	private UserAirlineService userAirlineService;
	
	/**
	 * 获取航空公司列表
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getFlightList 
	 * @return
	 * @date 2016年4月27日 下午3:18:27  
	 * @author ws
	 */
	@RequestMapping(value="flight/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getFlightList(Long userid){
		ApiResult<List<AirlineCompany>> result = new ApiResult<List<AirlineCompany>>();
		
		List<AirlineCompany> list = airlineCompanyService.selectList(userid);
		
		return result.toJSONString(0, "获取成功", list);
	}
	
	/**
	 * 获取航空绑定账号列表
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getFlightList 
	 * @return
	 * @date 2016年4月27日 下午3:18:27  
	 * @author ws
	 */
	@RequestMapping(value="flightaccount/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getFlightAccountList(Long userid,Long airlineCompanyId){
		ApiResult<List<UserAirline>> result = new ApiResult<List<UserAirline>>();
		
		List<UserAirline> list = userAirlineService.selectByUser(userid,airlineCompanyId);
		
		return result.toJSONString(0, "获取成功", list);
	}
	
	
	/**
	 * 获取已绑定积分账户
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
	public String userlist(Long userid,Integer platformId,HttpServletRequest request) {
		ApiResult<List<JSONObject>> result = new ApiResult<List<JSONObject>>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}if(null == platformId){
			return result.toJSONString(-1, "参数platformId不能为空");
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
		for (JSONObject jsonObject : list) {
			if(platformId.equals(jsonObject.getInteger("platformId"))){
				filterList.add(jsonObject);
			}
		}
		
		return result.toJSONString(0,"success",filterList);
	}
	
	
	/**
	 * 获取兑换方式列表
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getPhoneRealizeDetail 
	 * @param platformId
	 * @param amountId
	 * @return
	 * @date 2016年4月13日 下午5:58:41  
	 * @author ws
	 */
	@RequestMapping(value="integral/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getMileageIntegralList(Long airCompanyId,Long platformId){
		ApiResult<List<MileageIntegral>> result = new ApiResult<List<MileageIntegral>>();
		if(null == airCompanyId){
			return result.toJSONString(-1, "参数airCompanyId不能为空");
		}if(null == platformId){
			return result.toJSONString(-1, "参数platformId不能为空");
		}
		List<MileageIntegral> list = mileageIntegralService.selectListBy(airCompanyId,platformId);
		
		return result.toJSONString(0, "获取成功", list);
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
		String support = ",2,";//查询支持兑里程的平台
		List<RealizePlatform> list = realizationPlatformService.selectBySupport(versionLong.toString(),support);
		
		return result.toJSONString(0,"success",list,RealizePlatform.class,REALIZATION_PLATFORM_LIST_FILTER);
	}
	
	/**
	 * 进入里程兑换
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
	public String convertMileage(Long userId,String paramJson){
		//{"platformId":"","isAddUserTerm":"","remoteId":"","remoteType":"","info":"","areCompanyId":"","memberId":"","mileageId":"","ebsNm1":"","ebsNm2":""}
		ApiResult<String> result = new ApiResult<String>();
		JSONObject json = JSON.parseObject(paramJson);
		Long platformId = json.getLong("platformId");
		Integer isAddUserTerm = json.getInteger("isAddUserTerm");
		Long remoteId = json.getLong("remoteId");
		Integer remoteType = json.getInteger("remoteType");
		String info = json.getString("info");
		Long flightCompanyId = json.getLong("flightCompanyId");
		String memberId = json.getString("memberId");
		Long mileageId = json.getLong("mileageId");
		String ebsNm1 = json.getString("ebsNm1");//姓  拼音
		String ebsNm2 = json.getString("ebsNm2");//名  拼音
		if(null == platformId || null == isAddUserTerm
				|| null == flightCompanyId || StringUtils.isBlank(memberId)
				|| null == mileageId 
				|| StringUtils.isBlank(ebsNm1) || StringUtils.isBlank(ebsNm2)){
			return result.toJSONString(-1, "参数不能为空");
		}
		
		JSONObject memo = new JSONObject();
		memo.put("remoteId", remoteId);
		memo.put("remoteType", remoteType);
		memo.put("info", info);
		memo.put("isAddUserTerm", isAddUserTerm);
		memo.put("flightCompanyId", flightCompanyId);
		memo.put("memberId", memberId);
		memo.put("mileageId", mileageId);
		memo.put("ebsNm1", ebsNm1);
		memo.put("ebsNm2", ebsNm2);
		
		//生成积分变现记录
		Long realizeRecordId = realizeService.saveMileageConvertRecord(userId, platformId, memo.toJSONString(), mileageId);
		
		StringBuilder redirct = new StringBuilder();
		redirct.append(appConfig.caituUrl);
		
		if(StringUtils.isBlank(info)){
			redirct.append("/transitional/transtional.html?redirect=");
		}else{
			redirct.append("/mileage_transitional/transtional.html?redirect=");
		}
		
		String loginAccount = "";
		
		if (4 == platformId.longValue()){
			//交通
			if(StringUtils.isBlank(info)){
				redirct.append("/mileage_bocom/login.html");
			}else{
				redirct.append("/mileage_bocom/pay_code.html");
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
	 * 自动登录里程兑换平台
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
			//交通
			case 4:{
				String cardNo = info.getString("cardNo");
				String password = info.getString("password");
				String cardyear = info.getString("cardyear");
				String cardmonth = info.getString("cardmonth");
				
				String reslut = bocomMileageSpider.login(userid, cardNo, password, cardyear, cardmonth, realizeRecordId);
				boolean flag = JsonResult.checkResult(reslut);
				if(flag){
					
					String dataStr = JsonResult.getResult(reslut, "data");
					JSONObject data = JSON.parseObject(dataStr);
					info.put("userBonus", data.getString("userBonus"));
					info.put("mobTel", data.getString("mobTel"));
					
					return result.toJSONString(0, "SUCCESS",info); 
				}
				
				return reslut;
			}
			default:
				return result.toJSONString(-1, "暂不支持该平台");
		}
	}
	
}
