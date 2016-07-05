package com.caitu99.service.realization.controller;

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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.ishop.ccb.controller.api.UserChinaConstructionBankShopController;
import com.caitu99.service.ishop.cm.controller.api.IShopController;
import com.caitu99.service.ishop.esurfing.controller.api.UserEsurfingShopController;
import com.caitu99.service.realization.controller.spider.BOCOMShopController;
import com.caitu99.service.realization.controller.spider.CCBShopController;
import com.caitu99.service.realization.controller.spider.CiticCreditShopController;
import com.caitu99.service.realization.controller.spider.EsurfingShopController;
import com.caitu99.service.realization.controller.spider.PABController;
import com.caitu99.service.realization.controller.spider.UnicomController;
import com.caitu99.service.realization.domain.Realize;
import com.caitu99.service.realization.domain.RealizeCoupon;
import com.caitu99.service.realization.domain.RealizeDetail;
import com.caitu99.service.realization.domain.RealizePlatform;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.service.RealizePlatformService;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.realization.service.UserTermService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.VersionUtil;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/realization/")
public class RealizationController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(RealizationController.class);

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
	private ItemService itemService;
	
	
	/**
	 * 查询用户积分变现列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: userlist 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年2月23日 下午6:28:21  
	 * @author xiongbin
	 */
	@RequestMapping(value="user/list/1.0", produces="application/json;charset=utf-8")
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
		
		List<JSONObject> list = userTermService.selectRealizationList(userid,versionLong.toString(),false);
		return result.toJSONString(0,"success",list);
	}
	
	/**
	 * 查询积分变现列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: list 
	 * @param request
	 * @return
	 * @date 2016年2月23日 上午11:57:44  
	 * @author xiongbin
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
		List<RealizePlatform> list = realizationPlatformService.selectPageList(versionLong.toString());
		return result.toJSONString(0,"success",list,RealizePlatform.class,REALIZATION_PLATFORM_LIST_FILTER);
	}
	
	/**
	 * @Description: (查询变现数据列表)  
	 * @Title: realizeList 
	 * @param request
	 * @return
	 * @date 2016年2月23日 下午3:39:54  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value="realize/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String realizeList(HttpServletRequest request,Long platformId){
		ApiResult<List<Realize>> result = new ApiResult<List<Realize>>();
		List<Realize> list = realizeService.selectRealizeByPlatform(platformId);
		return result.toJSONString(0, "success", list,Realize.class,REALIZE_LIST_FILTER);
	}
	
	/**
	 * 确认变现跳转h5入口
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: realize 
	 * @param userId			用户ID
	 * @param platformId		积分变现平台ID
	 * @param remoteId			外部记录ID
	 * @param remoteType		外部记录类型(1.账单，2.实时，3.添加)
	 * @param info				绑定帐号信息JSON
	 * @param realizeId	        变现记录ID
	 * @param isAddUserTerm		是否来自用户添加积分变现账户(0:不是;1:是)
	 * @return
	 * @date 2016年2月24日 下午4:34:35  
	 * @author xiongbin
	 */
	@RequestMapping(value="realize/redirect/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String realize(HttpServletRequest request,Long userId,Long platformId,Long remoteId,
													Integer remoteType,String info,Long realizeId,Integer isAddUserTerm){
		ApiResult<String> result = new ApiResult<String>();
		if(null == platformId || null == realizeId || null == isAddUserTerm){
			return result.toJSONString(-1, "参数不能为空");
		}
		
		String version = VersionUtil.getAppVersion(request);
		if(StringUtils.isBlank(version)){
			logger.warn("未传递版本号,默认为老版本:2.1.1");
			version = "2.1.1";
		}else{
			logger.info("APP版本号为:" + version);
		}
		Long versionLong = VersionUtil.getVersionLong(version);
		
		JSONObject memo = new JSONObject();
		memo.put("remoteId", remoteId);
		memo.put("remoteType", remoteType);
		memo.put("info", info);
		memo.put("isAddUserTerm", isAddUserTerm);
		memo.put("versionLong", versionLong);
		
		//查询变现方案
		RealizeDetail realizeDetail = realizeService.selectByLevel(realizeId, null);
		if(null == realizeDetail){
			logger.error("未找到变现方案，id:{}",realizeId);
			return result.toJSONString(-1, "未找到变现方案");
		}
		//生成积分变现记录
		Long realizeRecordId = realizeService.saveRealizeRecord(userId, platformId, realizeDetail.getId(), memo.toJSONString());
		
		StringBuilder redirct = new StringBuilder();
		redirct.append(appConfig.caituUrl);
		
		if(StringUtils.isBlank(info)){
			redirct.append("/transitional/transtional.html?redirect=");
		}else{
			redirct.append("/realize_transitional/transtional.html?redirect=");
		}
		
		String loginAccount = "";
		
		//建设
		if(1 == platformId.longValue()){
			if(StringUtils.isBlank(info)){
				redirct.append("/realize_ccb/login.html");
			}else{
				redirct.append("/realize_ccb/pay_code.html");
			}
		} else if (2 == platformId.longValue() || 7 == platformId.longValue()){
			//天翼
			if(null!=remoteId && null!=remoteType){
				loginAccount = userTermService.selectLoginAccount(remoteId, remoteType);
			}
			if(StringUtils.isBlank(info)){
				redirct.append("/realize_esurfing/login.html");
			}else{
				redirct.append("/realize_esurfing/pay_code.html");
			}
		} else if (3 == platformId.longValue()){
			//中信
	
			if(StringUtils.isBlank(info)){
				redirct.append("/realize_citic/citic_login.html");
			}else{
				redirct.append("/realize_citic/citic_code.html");
			}
		} else if (4 == platformId.longValue()){
			//交通
			if(StringUtils.isBlank(info)){
				redirct.append("/realize_bocom/login.html");
			}else{
				redirct.append("/realize_bocom/pay_code.html");
			}
		} else if (5 == platformId.longValue()){
			//移动
			if(null!=remoteId && null!=remoteType){
				loginAccount = userTermService.selectLoginAccount(remoteId, remoteType);
			}
			if(StringUtils.isBlank(info)){
				redirct.append("/realize_cm/login.html");
			}else{
				redirct.append("/realize_cm/sms.html");
			}
		} else if (6 == platformId.longValue()){
			//联通
			if(null!=remoteId && null!=remoteType){
				loginAccount = userTermService.selectLoginAccount(remoteId, remoteType);
			}
			if(StringUtils.isBlank(info)) {
				redirct.append("/realize_unicom/login.html");
			}else{
				redirct.append("/realize_unicom/sms.html");
			}
		} else if (8 == platformId.longValue()){
			//平安
			if(StringUtils.isBlank(info)) {
				redirct.append("/realize_pab/login.html");
			}else{
				redirct.append("/realize_pab/pay_skip.html");
			}
		} else {
			return result.toJSONString(-1, "暂不支持该平台");
		}
		
		redirct.append("?realizeRecordId=").append(realizeRecordId).append("&loginAccount=").append(loginAccount);
		
		logger.info("积分变现H5跳转入口,用户ID:{},platformId:{},realizeId:{},memo:{},realizeRecordId:{},redirct:{}",
												userId,platformId,realizeId,memo.toJSONString(),realizeRecordId,redirct);
		
		return result.toJSONString(0, "success",redirct.toString());
	}
	
	/**
	 * @Description: (提供给JOB调用，给变现用户返现)  
	 * @Title: transfer 
	 * @param realizeRecordId
	 * @date 2016年2月24日 上午11:54:13  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value="realize/transfer/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public void transfer(Long realizeRecordId){
		realizeService.realizeTransfer(realizeRecordId);
	}
	
	/**
	 * 保存积分变现券码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: couponSave 
	 * @param userid				用户ID
	 * @param realizeRecordId		积分变现记录ID
	 * @param param					券码集合
	 * @return
	 * @date 2016年2月25日 上午11:23:45  
	 * @author xiongbin
	 */
	@RequestMapping(value="coupon/save/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String couponSave(Long userid,Long realizeRecordId,String param,String orderNo){
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}else if(null == realizeRecordId){
			return result.toJSONString(-2, "参数realizeRecordId不能为空");
		}else if(StringUtils.isBlank(param)){
			return result.toJSONString(-1, "参数param不能为空");
		}else if(StringUtils.isBlank(orderNo)){
			return result.toJSONString(-2, "参数orderNo不能为空");
		}
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			return result.toJSONString(-2, "数据错误");
		}
		//积分变现平台ID
		Long platformId = realizeRecord.getPlatformId();
		//券码数量
		Integer quantity = realizeRecord.getQuantity();
		
		JSONArray list = null;
		try {
			list = JSON.parseArray(param);
		} catch (Exception e) {
			logger.error("初始化券码数据出错:" + e.getMessage(),e);
			return result.toJSONString(-1, "系统暂时出现异常,请稍后再试");
		}
		
		if(null == list || list.size() < 1){
			return result.toJSONString(-1, "请输入券码");
		}else if(list.size() != quantity){
			return result.toJSONString(-1, "您输入券码数量不对,请重新输入");
		}
		
		List<RealizeCoupon> realizeCouponListOld = realizeService.selectByRealizeRecordId(realizeRecordId);
		if(realizeCouponListOld.size() > 0){
			return result.toJSONString(-1, "请不要重复提交");
		}
		
		Map<String,Object> map = realizeService.getMode3Date(orderNo);
		if(null == map){
			return result.toJSONString(-1, "未查询到积分变现记录数据");
		}
		
		//获取变现模式
		Integer mode = (Integer)map.get("mode");
		
		List<RealizeCoupon> realizeCouponList = new ArrayList<RealizeCoupon>();
		for(int i=0;i<list.size();i++){
			JSONObject json = JSON.parseObject(list.get(i).toString());

			String veryCode = json.getString("veryCode" + (i+1));
			//String beginTime = json.getString("beginTime" + (i+1));
			String password = json.getString("password" + (i+1));
			
			if(mode.equals(1)){
				if(StringUtils.isBlank(veryCode)){
					return result.toJSONString(-1, "请输入券码");
				}
			}else if(mode.equals(2)){
				if(StringUtils.isBlank(veryCode)){
					return result.toJSONString(-1, "请输入券码");
				}
//				else if(StringUtils.isBlank(beginTime)){
//					return result.toJSONString(-1, "请输入券码过期时间");
//				}
			}else if(mode.equals(3)){
				if(StringUtils.isBlank(veryCode)){
					return result.toJSONString(-1, "请输入券码");
				}
//				else if(StringUtils.isBlank(beginTime)){
//					return result.toJSONString(-1, "请输入券码过期时间");
//				}
				else if(StringUtils.isBlank(password)){
					return result.toJSONString(-1, "请输入券码密码");
				}
			}
			
			RealizeCoupon realizeCoupon = new RealizeCoupon();
			realizeCoupon.setCode(veryCode);
//			realizeCoupon.setExpiryDate(beginTime);
			realizeCoupon.setPwd(password);
			realizeCoupon.setPlatformId(platformId);
			realizeCoupon.setRealizeRecordId(realizeRecordId);
			
			realizeCouponList.add(realizeCoupon);
		}
		
		realizeService.saveRealizeCouponList(realizeCouponList,orderNo,userid,realizeRecordId,true);
		
		logger.info("积分变现保存券码,用户ID:{},realizeRecordId:{},订单号:{}",userid,realizeRecordId,orderNo);

		//启动定时任务返现财币
		if(platformId.equals(4L)){
			//交通银行,定时任务7天
			realizeService.realizeJob(realizeRecordId,"BOCOM_SHOP");
		}else{
			//其他变现,定时任务1天
			realizeService.realizeJob(realizeRecordId);
		}
		
		//变现财币
		Long cash = realizeRecord.getCash();
		
		JSONObject json = new JSONObject();
		json.put("cash", cash);
		json.put("realizeRecordId", realizeRecordId);
		json.put("platformId", realizeRecord.getPlatformId());
		
		return result.toJSONString(0, "SUCCEED",json);
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
				String redirectUrl = "/realize_ccb/login.html?realizeRecordId=";
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
			case 2:
			case 7:{
				String phone = info.getString("phone");
				
				String reslut = esurfingShopController.paySend(userid);
				String code = JsonResult.getResult(reslut, "code");
				
				if(ApiResultCode.SUCCEED.toString().equals(code)){
					logger.info("天翼积分变现自动登录,用户ID:{},登录状态.",userid);
					return result.toJSONString(0, "SUCCESS",info); 
				}else if(ApiResultCode.NOT_LOGIN_STATUS.toString().equals(code)){
					logger.info("天翼积分变现自动登录,用户ID:{},未登录状态.",userid);
					reslut = esurfingShopController.loginSend(userid, phone, null);
					boolean flag = JsonResult.checkResult(reslut);
					if(flag){
						return result.toJSONString(0, "SUCCESS",info); 
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
			case 3:{
				
				String loginAccount = info.getString("loginAccount");
				String password = info.getString("password");
				
				String reslut = citicCreditShopController.pay(userid, loginAccount, password, "", realizeRecordId);
				String code = JsonResult.getResult(reslut, "code");
				if("1115".equals(code)){
					logger.info("中信积分变现自动登录,用户ID:{},登陆状态.",userid);
					return reslut;
				}else if("2460".equals(code) || "1121".equals(code)){//支付图形验证码错误
					String imageCode = JsonResult.getResult(reslut, "data");
					JSONObject reslutJSON = new JSONObject();
					reslutJSON.putAll(info);
					reslutJSON.put("directFlag", "pay");
					reslutJSON.put("data", imageCode);
					return result.toJSONString(0, "SUCCESS",reslutJSON); 
				}else if("1005".equals(code)){
					logger.info("中信积分变现自动登录,用户ID:{},未登陆状态.",userid);
					 reslut = citicCreditShopController.login(userid, loginAccount, password, realizeRecordId, "");
					 code = JsonResult.getResult(reslut, "code");
						if("1001".equals(code)){//自动支付
							reslut = citicCreditShopController.pay(userid, loginAccount, password, "", realizeRecordId);
							if("2460".equals(code) || "1121".equals(code)){//支付图形验证码错误
								String imageCode = JsonResult.getResult(reslut, "data");
								JSONObject reslutJSON = new JSONObject();
								reslutJSON.putAll(info);
								reslutJSON.put("directFlag", "pay");
								reslutJSON.put("data", imageCode);
								return result.toJSONString(0, "SUCCESS",reslutJSON); 
							}
						}else if("2460".equals(code) || "1094".equals(code)){//检测是否是图片验证码错误
							String imageCode = JsonResult.getResult(reslut, "data");
							JSONObject reslutJSON = new JSONObject();
							reslutJSON.putAll(info);
							reslutJSON.put("directFlag", "login");
							reslutJSON.put("data", imageCode);
							return result.toJSONString(0, "SUCCESS",reslutJSON); 
						}else if("1097".equals(code)){//需要输入短信验证码
							JSONObject reslutJSON = new JSONObject();
							reslutJSON.putAll(info);
							reslutJSON.put("directFlag", "msg");
							return result.toJSONString(0, "SUCCESS",reslutJSON); 
						}else{
							JSONObject reslutJSON = new JSONObject();
							reslutJSON.putAll(info);
							reslutJSON.put("directFlag", "login");
							return result.toJSONString(0, "SUCCESS",reslutJSON); 
						}
						
						return reslut;
				}
		
				return reslut;
			}
			//交通
			case 4:{
				String cardNo = info.getString("cardNo");
				String password = info.getString("password");
				String cardyear = info.getString("cardyear");
				String cardmonth = info.getString("cardmonth");
				
				String reslut = bocomShopController.login(userid, cardNo, password, cardyear, cardmonth, realizeRecordId);
				boolean flag = JsonResult.checkResult(reslut);
				if(flag){
					return result.toJSONString(0, "SUCCESS",info); 
				}
				
				return reslut;
			}
			case 5:{
				//移动，汉字验证码，需要用户手输
				return result.toJSONString(0, "SUCCESS", info);
			}
            case 6:{
				//联通
            	String loginAccount = info.getString("phone");
				String password = info.getString("password");
				
				String reslut = unicomController.paySendSMS(userid, realizeRecordId);
				String code = JsonResult.getResult(reslut, "code");
				
				if(ApiResultCode.SUCCEED.toString().equals(code)){
					logger.info("联通积分变现自动登录,用户ID:{},登录状态.",userid);
					return result.toJSONString(0, "SUCCESS",info); 
				}else if(ApiResultCode.NOT_LOGIN_STATUS.toString().equals(code)){
					logger.info("联通积分变现自动登录,用户ID:{},未登录状态.",userid);
					reslut = unicomController.login(userid, loginAccount, password, null, realizeRecordId);
	            	boolean flag = JsonResult.checkResult(reslut);
	            	if(flag){
						return result.toJSONString(0, "SUCCESS",info); 
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
			//平安
			case 8:{
				String loginAccount = info.getString("loginAccount");
				String password = info.getString("password");
				
				info.put("realizeRecordId", realizeRecordId);
				
				String reslut = pabController.order(userid, realizeRecordId, null);
				String code = JsonResult.getResult(reslut, "code");
				if(ApiResultCode.PAB_ORDER_PASSWORD.toString().equals(code)
						|| ApiResultCode.PAB_ORDER_PHONECODE.toString().equals(code)
						|| ApiResultCode.PAB_ORDER_PASSWORD_AND_PHONECODE.toString().equals(code)){
					
					logger.info("平安积分变现自动登录,用户ID:{},登录状态.",userid);
					
					JSONObject reslutJSON = new JSONObject();
					reslutJSON.putAll(info);
					reslutJSON.put("mode", code);
					return result.toJSONString(0, "SUCCESS",reslutJSON); 
				}else if(ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR.toString().equals(code)
										|| ApiResultCode.IMAGECODE_ERROR.toString().equals(code)){
					
					logger.info("平安积分变现自动登录,用户ID:{},登录状态.下单验证码错误",userid);
					String imageCode = JsonResult.getResult(reslut, "data");
					JSONObject reslutJSON = new JSONObject();
					reslutJSON.putAll(info);
					reslutJSON.put("imageCode", imageCode);
					//登录状态
					reslutJSON.put("status", 1);
					return result.toJSONString(0, "SUCCESS",reslutJSON); 
				}else if(ApiResultCode.NOT_LOGIN_STATUS.toString().equals(code)){
					logger.info("平安积分变现自动登录,用户ID:{},未登陆状态.",userid);
					reslut = pabController.login(userid, loginAccount, password, null, realizeRecordId);
					boolean flag = JsonResult.checkResult(reslut);
					if(flag){
						String data = JsonResult.getResult(reslut, "data");
						JSONObject reslutJSON = new JSONObject();
						reslutJSON.putAll(info);
						reslutJSON.put("mode", data);
						return result.toJSONString(0, "SUCCESS",reslutJSON); 
					}
					
					//检测是否是图片验证码错误
					code = JsonResult.getResult(reslut, "code");
					if("2460".equals(code) || "2452".equals(code)){
						String imageCode = JsonResult.getResult(reslut, "data");
						JSONObject reslutJSON = new JSONObject();
						reslutJSON.putAll(info);
						reslutJSON.put("imageCode", imageCode);
						//非登录状态
						reslutJSON.put("status", 0);
						return result.toJSONString(0, "SUCCESS",reslutJSON); 
					}else if("-9".equals(code)){
						//下单验证码错误
						String imageCode = JsonResult.getResult(reslut, "data");
						JSONObject reslutJSON = new JSONObject();
						reslutJSON.putAll(info);
						reslutJSON.put("imageCode", imageCode);
						//非登录状态
						reslutJSON.put("status", 1);
						return result.toJSONString(0, "SUCCESS",reslutJSON); 
					}
				}
				
				return reslut;
			}
			default:
				return result.toJSONString(-1, "暂不支持该平台");
		}
	}
	
	/**
	 * 根据订单号查询积分变现记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getRecord 
	 * @param orderNo		订单号
	 * @return
	 * @date 2016年3月1日 上午11:40:30  
	 * @author xiongbin
	 */
	@RequestMapping(value="get/record/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getRecord(String orderNo){
		ApiResult<Map<String,Object>> result = new ApiResult<Map<String,Object>>();
		if(StringUtils.isBlank(orderNo)){
			return result.toJSONString(-1, "参数orderNO不能为空");
		}
		
		Map<String,Object> map = realizeService.getMode3Date(orderNo);
		if(null == map){
			return result.toJSONString(-1, "未查询到积分变现记录数据");
		}
		
		return result.toJSONString(0, "success", map);
	}
	
	/**
	 * 查询用户未填写券码数量
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: notCoupon 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年4月25日 下午3:21:31  
	 * @author xiongbin
	 */
	@RequestMapping(value="user/coupon/not/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String notCoupon(Long userid){
		ApiResult<Integer> result = new ApiResult<Integer>();
		if(null == userid){
			return result.toJSONString(-1, "参数notCoupon不能为空");
		}
		
		Integer count = realizeService.selectUserNotCoupon(userid);
		
		return result.toJSONString(0, "success", count);
	}
	
	/**
	 * 查询变现详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectRecord 
	 * @param realizeRecordId	积分变现记录ID
	 * @return
	 * @date 2016年4月29日 上午10:48:30  
	 * @author xiongbin
	 */
	@RequestMapping(value="select/record/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String selectRecord(Long realizeRecordId){
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		if(null == realizeRecordId){
			return result.toJSONString(-1, "参数realizeRecordId不能为空");
		}
		
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			return result.toJSONString(-1, "未查询到积分变现记录数据");
		}
		
		Long platformId = realizeRecord.getPlatformId();
		RealizePlatform realizePlatform = realizationPlatformService.selectByPrimaryKey(platformId);
		if(null == realizePlatform){
			return result.toJSONString(-1, "未查询到积分变现平台");
		}
		
		Long itemId = realizeRecord.getItemId();
		Item item = itemService.selectByPrimaryKey(itemId);
		if(null == item){
			return result.toJSONString(-1, "未查询到商品数据");
		}
		
		JSONObject json = new JSONObject();

		//获取版本号
		String memo = realizeRecord.getMemo();
		JSONObject jsonMemo = JSON.parseObject(memo);
		String version = jsonMemo.getString("versionLong");
		if(!StringUtils.isBlank(version) && "3000000".compareTo(version) <= 0){
			json.put("tubi", realizeRecord.getCash());
		}
		
		json.put("platformName", realizePlatform.getName());
		json.put("cash", realizeRecord.getCash());
		json.put("integral", realizeRecord.getIntegral());
		json.put("platformId", platformId);
		json.put("itemName", item.getTitle());
		
		return result.toJSONString(0, "success", json);
	}
}
