package com.caitu99.service.integral.controller.api;

import java.util.Date;
import java.util.HashMap;
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
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.constants.SysConstants;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.provider.AddExp;
import com.caitu99.service.expedient.provider.rule.AddExpByIntegral;
import com.caitu99.service.integral.controller.auto.AutoFindAdapter;
import com.caitu99.service.integral.controller.auto.AutoUpdateAdapter;
import com.caitu99.service.integral.controller.spider.ManualQueryAdapter;
import com.caitu99.service.integral.controller.spider.ManualQueryAirChina;
import com.caitu99.service.integral.controller.spider.ManualQueryBoTaoHui;
import com.caitu99.service.integral.controller.spider.ManualQueryCCB;
import com.caitu99.service.integral.controller.spider.ManualQueryCCBI;
import com.caitu99.service.integral.controller.spider.ManualQueryCMB;
import com.caitu99.service.integral.controller.spider.ManualQueryCMCC;
import com.caitu99.service.integral.controller.spider.ManualQueryCOMM;
import com.caitu99.service.integral.controller.spider.ManualQueryCU;
import com.caitu99.service.integral.controller.spider.ManualQueryCsAir;
import com.caitu99.service.integral.controller.spider.ManualQueryEsurfing;
import com.caitu99.service.integral.controller.spider.ManualQueryJingDong;
import com.caitu99.service.integral.controller.spider.ManualQueryPingAnBank;
import com.caitu99.service.integral.controller.spider.ManualQueryPuFaBank;
import com.caitu99.service.integral.controller.spider.ManualQueryTaobao;
import com.caitu99.service.integral.controller.spider.ManualQueryWuMei;
import com.caitu99.service.integral.controller.spider.ManualQueryYjf189;
import com.caitu99.service.integral.controller.spider.ManualQueryYjf189nt;
import com.caitu99.service.integral.domain.Manual;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.ManualService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.life.service.RechargeService;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.VersionUtil;
import com.caitu99.service.utils.exception.AssertApiUtil;
import com.caitu99.service.utils.json.JsonResult;

@Controller
@RequestMapping("/api/integral/manual/")
public class ManualQueryController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryAdapter.class);

	@Autowired
	private ManualService manualService;
	
	@Autowired
	private UserCardManualService userCardManualService;
	
	@Autowired
	private ManualLoginService manualLoginService;
	
	@Autowired
	private ManualQueryAdapter manualQueryAdapter;
	
	@Autowired
	private RedisOperate redis;

	@Autowired
	private RechargeService rechargeService;
	
	@Autowired
	private AutoFindAdapter autoFindAdapter;
	@Autowired
	UserCardService userCardService;
	
	@Autowired
	private AddExp addExp;
	
	@Autowired
	private AddExpByIntegral addExpByIntegral;
	
	/** 各个平台手动查询类 */
	
	@Autowired
	private ManualQueryTaobao manualQueryTaobao;
	@Autowired
	private ManualQueryCMB manualQueryCMB;
	@Autowired
	private ManualQueryJingDong manualQueryJingDong;
	@Autowired
	private ManualQueryEsurfing manualQueryEsurfing;
	@Autowired
	private ManualQueryAirChina manualQueryAirChina;
	@Autowired
	private ManualQueryCsAir manualQueryCsAir;
	@Autowired
	private ManualQueryCMCC manualQueryCMCC;
	@Autowired
	private ManualQueryBoTaoHui manualQueryBoTaoHui;
	@Autowired
	private ManualQueryWuMei manualQueryWuMei;
	
	@Autowired
	private ManualQueryCCB manualQueryCCB;
	@Autowired
	private ManualQueryCCBI manualQueryCCBI;
	
	@Autowired
	private ManualQueryCOMM manualQueryCOMM;
	
	@Autowired
	private ManualQueryYjf189 manualQueryYjf189;
	@Autowired
	private ManualQueryYjf189nt manualQueryYjf189nt;
	@Autowired
	private ManualQueryCU manualQueryCU;
	@Autowired
	private ManualQueryPuFaBank manualQueryPuFaBank;
	@Autowired
	private ManualQueryPingAnBank manualQueryPingAnBank;
	
	@Autowired
	AutoUpdateAdapter autoUpdateAdapter;
	@Autowired
	private AppConfig appConfig;
	
	/** 各个平台手动查询类 */
	
	/**
	 * 查询积分账户列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: list 
	 * @return
	 * @date 2015年11月10日 下午9:24:19  
	 * @author xiongbin
	 */
	@RequestMapping(value="list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String list(String version) {
		ApiResult<Object> result = new ApiResult<Object>();
		if(StringUtils.isBlank(version)){
			version = "1.7.0";
		}
//		Map<String, List<Manual>> map = manualService.finlListToSort();
		List<Manual> list = manualService.listByVersion(version);
		return result.toJSONString(0,"success",list);
	}
	
	/**
	 * 生成登录页面参数 1.0版
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: generatelogin 
	 * @param userid			用户ID
	 * @param manualId			积分账户ID
	 * @param isImageCode		是否需要验证码(0:不需要;1:需要)
	 * @return
	 * @date 2015年11月11日 上午10:59:02
	 * @author xiongbin
	 */
	@RequestMapping(value="generatelogin/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String generatelogin(Long userid,Long manualId) {
		logger.info("手动查询统一生成登录页面参数1.0版");
		
		if(null == userid){
			return ApiResult.outSucceed(-1,"参数userid不能为空");
		}else if(null == manualId){
			return ApiResult.outSucceed(-1,"参数manualId不能为空");
		}
		
		Map<String, Object> map = manualService.generatelogin(userid, manualId);
		//统一生成登录页面接口
		String result = unificationgeneratelogin(userid, manualId, map,1);
		return result;
	}
	
	/**
	 * 生成登录页面参数 2.0版
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: generatelogin2 
	 * @param userid		用户ID
	 * @param manualId		积分账户ID
	 * @return
	 * @date 2015年11月18日 下午8:23:01  
	 * @author xiongbin
	 */
	@RequestMapping(value="generatelogin/2.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String generatelogin2(Long userid,Long manualId) {
		logger.info("手动查询统一生成登录页面参数2.0版");
		
		if(null == userid){
			return ApiResult.outSucceed(-1,"参数userid不能为空");
		}else if(null == manualId){
			return ApiResult.outSucceed(-1,"参数manualId不能为空");
		}
		
		Map<String, Object> map = manualService.generatelogin(userid, manualId);
		//统一生成登录页面接口
		String result = unificationgeneratelogin(userid, manualId, map,0);
		return result;
	}
	
	/**
	 * 实时刷新参数获取
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: generateloginpram 
	 * @param userid
	 * @param manualId
	 * @param account
	 * @return
	 * @date 2016年2月25日 上午11:33:52  
	 * @author ws
	 */
	@RequestMapping(value="generateloginpram/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String generateloginpram(Long userid,Long manualId,String account) {
		
		if(null == userid){
			return ApiResult.outSucceed(-1,"参数userid不能为空");
		}else if(null == manualId){
			return ApiResult.outSucceed(-1,"参数manualId不能为空");
		}
		
		Map<String, Object> map = manualService.generateloginWithAccount(userid, manualId,account);
		//统一生成登录页面接口
		String result = unificationgeneratelogin(userid, manualId, map,0);
		return result;
	}
	

	/**
	 * 实时刷新参数获取
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: generateloginpram 
	 * @param userid
	 * @param manualId
	 * @param account
	 * @return
	 * @date 2016年2月25日 上午11:33:52  
	 * @author ws
	 */
	@RequestMapping(value="loginbackstage/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String loginBackStage(Long userid,Long manualId,String account,HttpServletRequest request) {

		String version = VersionUtil.getAppVersion(request);
		if(StringUtils.isBlank(version)){
			version = "2.2.1";
			logger.info("APP使用默认版本号为:" + version);
		}else{
			logger.info("APP版本号为:" + version);
		}
		
		ApiResult<Map<String, Object>> res = new ApiResult<Map<String, Object>>();
		if(null == userid){
			return ApiResult.outSucceed(-1,"参数userid不能为空");
		}else if(null == manualId){
			return ApiResult.outSucceed(-1,"参数manualId不能为空");
		}else if(null == account){
			return ApiResult.outSucceed(-1,"参数account不能为空");
		}
		
		List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, manualId, account);
		if(manualLoginList.size() > 0){
			for (ManualLogin manualLogin : manualLoginList) {
				if(StringUtils.isNotBlank(manualLogin.getPassword())){
					String result = autoUpdateAdapter.updateAuto(manualLogin.getUserId(),manualLogin.getManualId()
							, manualLogin.getLoginAccount(), manualLogin.getPassword(),version);
					
					if(result.equals(SysConstants.SUCCESS)){

						return res.toJSONString(0, "更新成功");
					}else if(result.equals("账号或密码错误")){

						return res.toJSONString(2451, "账号或密码错误，请手动导入");
					}else if(result.equals("请手动更新")){

						return res.toJSONString(4100, "该账户不支持刷新，请手动导入");
					}else{
						
						return res.toJSONString(2448, "更新失败，请手动导入");
					}
				}
			}
		}
		return res.toJSONString(2001, "账号信息不全，请手动导入");
	}
	
	
	
	/**
	 * 统一生成登录页面参数
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: unificationgeneratelogin 
	 * @param userId		用户ID
	 * @param manualId		积分账户ID
	 * @param map
	 * @return
	 * @date 2015年11月18日 下午8:21:50  
	 * @author xiongbin
	 */
	private String unificationgeneratelogin(Long userId,Long manualId,Map<String, Object> map,Integer isImageCode){
		ApiResult<Map<String, Object>> result = new ApiResult<Map<String, Object>>();
		String resultJsonString = null;
		
//		//判断用户是否需要图片验证码,若不需要则去除
//		boolean isImage = manualQueryAdapter.isImageCode(userId, manualId);
//		if(!isImage){
//			Object[] temps = null;
//			Object object = map.get("name");
//			if(object instanceof JSONArray){
//				JSONArray names = (JSONArray)object;
//				temps = new Object[names.size()-1];
//				Iterator<Object> iterator = names.iterator();
//				int i = 0;
//				while(iterator.hasNext()){
//					Object name = iterator.next();
//					if(!name.equals(6)){
//						temps[i] = name;
//						i++;
//					}
//				}
//			}else{
//				Long[] names = (Long[])map.get("name");
//				if(null != names){
//					temps = new Object[names.length-1];
//					Integer i = 0;
//					for(Long name : names){
//						if(name.equals(6l) || name.equals(6)){
//							
//						}else{
//							temps[i] = name;
//						}
//						i++;
//					}
//				}
//			}
//			map.put("name", temps);
//			
//			return result.toJSONString(0,"success",map);
//		}
		
		if(null!=isImageCode && isImageCode.equals(0)){
			
		}else{
			switch (manualId.intValue()) {
				//淘宝
				case 1:{
					resultJsonString = manualQueryAdapter.getImageCode(userId,manualQueryTaobao);
				}
					break;
				//招行
				case 2:{
					resultJsonString = manualQueryAdapter.getImageCode(userId,manualQueryCMB);
				}
					break;
				//京东
				case 3:{
					resultJsonString = manualQueryAdapter.getImageCode(userId,manualQueryJingDong);
				}
					break;
				//天翼
				case 4:{
					resultJsonString = manualQueryAdapter.getImageCode(userId,manualQueryEsurfing);
				}
					break;
				//国航
				case 5:{
					resultJsonString = manualQueryAdapter.getImageCode(userId,manualQueryAirChina);
				}
					break;
				//联通
				case 6:{
					map.put("imagecode", "");
				}
					break;
				//南航
				case 7:{
					resultJsonString = manualQueryAdapter.getImageCode(userId,manualQueryCsAir);
				}
					break;
				//移动
				case 8:{
					resultJsonString = manualQueryAdapter.getImageCode(userId,manualQueryCMCC);
				}
					break;
				//洲际
				case 9:{
					//洲际无验证码
				}
					break;
				//花旗银行
				case 10:{
					//花旗银行无验证码
				}
					break;
				//铂涛会
				case 11:{
					resultJsonString = manualQueryAdapter.getImageCode(userId,manualQueryBoTaoHui);
				}
					break;
				//物美
				case 12:{
					resultJsonString = manualQueryAdapter.getImageCode(userId,manualQueryWuMei);
				}
					break;
				//交通
				case 13:{
					//交通无验证码
				}
				//建行
				case 14:{
					
				}
				//中信
				case 15:{
					
				}
				
					break;
				default:
					AssertApiUtil.notTrue(true,"暂不支持此积分账户",ApiResultCode.NONSUPPORT_MANUAL);
					break;
			}
		}
		
		if(StringUtils.isNotBlank(resultJsonString)){
			JSONObject resultJson = JSON.parseObject(resultJsonString);
			Integer code = resultJson.getInteger("code");
			if(code.equals(0)){
				map.put("imagecode", resultJson.getString("data"));
			}else{
				return resultJsonString;
			}
		}else{
			map.put("imagecode", "");
		}
		
		return result.toJSONString(0,"success",map);
	}
	
	/**
	 * 登录统一接口,1.0版
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userId		用户ID
	 * @param manualId		积分账户ID
	 * @param many			第几次验证,已1为起始
	 * @param paramJson		{
	 * 							"phone":"1515714655",
	 * 							"imageCode":"1243"
	 * 						}
	 * @return
	 * @date 2015年11月12日 下午9:08:09  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userId, Long manualId,Integer many,String paramJson,Long type) {
		
		logger.info("手动查询统一登录1.0版");
		
		JSONObject json = JSON.parseObject(paramJson);
		String bankCard = json.getString("bankCard");
		String identityCard = json.getString("identityCard");
		String loginAccount = json.getString("loginAccount");
		String phone = json.getString("phone");
		String password = json.getString("password");
        String imageCode= json.getString("imageCode");
		String phoneCode = json.getString("phoneCode");
		String provinceType = json.getString("provinceType");

		//统一登录接口
		String result = "";//= unificationlogin(userId,manualId,many,type,json,true);
	
		//自动发现
//		autoFindAdapter.execute(manualId, userId, loginAccount, password);
		
		return result;
	}
	
	/**
	 * 登录统一接口,2.0版
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param type				网关传参
	 * @param loginAccount		用户名
	 * @param password			密码
	 * @param imageCode			图片验证码
	 * @param phoneCode			短信验证码
	 * @return
	 * @date 2015年11月16日 上午11:39:18  
	 * @author xiongbin
	 */
	@RequestMapping(value="login/2.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login2(Long userId, Long manualId,Integer many,String paramJson,Long type,HttpServletRequest request) {

		logger.info("手动查询统一登录2.0版");
		
		String version = VersionUtil.getAppVersion(request);
		if(StringUtils.isBlank(version)){
			version = "2.2.1";
			logger.info("APP使用默认版本号为:" + version);
		}else{
			logger.info("APP版本号为:" + version);
		}
		
		//统一登录接口
		String result = unificationlogin(userId,manualId,many,type,paramJson,version);
	
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			//自动发现
//			autoFindAdapter.execute(manualId, userId, loginAccount, password);
		}
		
		return result;
	}
	
	/**
	 * 统一登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: unification 
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param many				第几次验证,已1为起始
	 * @param type				网关传参
	 * @param bankCard			银行卡号
	 * @param identityCard		身份证
	 * @param loginAccount		用户名
	 * @param phone				手机
	 * @param password			密码
	 * @param imageCode			图片验证码
	 * @param phoneCode			短信验证码
	 * @param provinceType		
	 * @param isImageCode		是否需要验证码
	 * @return
	 * @date 2015年11月16日 下午6:19:56  
	 * @author xiongbin
	 * @param provinceType 
	 */
	private String unificationlogin(Long userId, Long manualId,Integer many,Long type,String paramJson,String version){
		
		JSONObject json = JSON.parseObject(paramJson);
		String bankCard = json.getString("bankCard");
		String identityCard = json.getString("identityCard");
		String loginAccount = json.getString("loginAccount");
		String phone = json.getString("phone");
		String password = json.getString("password");
        String imageCode= json.getString("imageCode");
		String phoneCode = json.getString("phoneCode");
		String provinceType = json.getString("provinceType");
		String phonenum = json.getString("phonenum");
		
		String yztAccount = json.getString("yztAccount");
		String yztPassword = json.getString("yztPassword");
		
		
		boolean isImageCode = false;
		if(StringUtils.isNotBlank(imageCode)){
			isImageCode = true;
		}
		
		String result =  null;

		if(null == userId){
			return ApiResult.outSucceed(-1,"参数userid不能为空");
		}else if(null == manualId){
			return ApiResult.outSucceed(-1,"参数manualId不能为空");
		}else if(null == many){
			return ApiResult.outSucceed(-1,"参数many不能为空");
		}
		
		switch (manualId.intValue()) {
			//淘宝
			/*case 1:{
				switch (many) {
					case 1:{
						if(StringUtils.isBlank(loginAccount)){
							return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"参数loginAccount不能为空");
						}else if(StringUtils.isBlank(password)){
							return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"参数password不能为空");
						}else if(StringUtils.isEmpty(imageCode) && isImageCode){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数imageCode不能为空");
						}
						
						result = manualQueryAdapter.login(manualQueryTaobao, userId, manualId, loginAccount, password, imageCode, true, isImageCode);
//						result = manualQueryAdapter.loginTaobao(userId, manualId,loginAccount, password, imageCode, isImageCode);
					}
						break;
					case 2:{
						if(StringUtils.isEmpty(phoneCode)){
							return ApiResult.outSucceed(ApiResultCode.PHONE_CODE_NOT_NULL,"参数phoneCode不能为空");
						}

						result = manualQueryAdapter.checkTaobaoPhoneCode(userId, manualId, phoneCode);
					}
						break;
					default:
						break;
				}
			}
				break;*/
			//招行
			case 2:{
				if(StringUtils.isBlank(identityCard)){
					return ApiResult.outSucceed(ApiResultCode.IDENTITYCARD_NOT_NULL,"参数identityCard不能为空");
				}else if(StringUtils.isBlank(password)){
					return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"参数password不能为空");
				}else if(StringUtils.isBlank(imageCode) && isImageCode){
					return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数imageCode不能为空");
				}
				
				result = manualQueryAdapter.login(manualQueryCMB, userId, manualId, identityCard, password, imageCode, true, isImageCode,version);
//				result = manualQueryAdapter.loginCMB(userId, manualId, identityCard, password, imageCode,isImageCode);
			}
				break;
			//京东
			case 3:{
				if(StringUtils.isBlank(loginAccount)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"参数loginAccount不能为空");
				}else if(StringUtils.isBlank(password)){
					return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"参数password不能为空");
				}else if(StringUtils.isBlank(imageCode) && isImageCode){
					return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数imageCode不能为空");
				}
				

				result = manualQueryAdapter.login(manualQueryJingDong, userId, manualId, loginAccount, password, imageCode, true, isImageCode, version);
//				result = manualQueryAdapter.loginJingDong(userId, manualId, loginAccount, password, imageCode,isImageCode);
			}
				break;
//			//天翼
//			case 4:{
//				switch (many) {
//					case 1:{
//						if(StringUtils.isBlank(phone)){
//							return ApiResult.outSucceed(ApiResultCode.PHONE_NOT_NULL,"参数phone不能为空");
//						}else if(StringUtils.isBlank(imageCode) && isImageCode){
//							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数imageCode不能为空");
//						}
//				        
//						result = manualQueryAdapter.login(manualQueryEsurfing,userId,manualId,phone,"",imageCode,true,isImageCode);
////						result = manualQueryAdapter.checkEsurfingImageCode(userId,manualId ,phone, imageCode);
//					}
//						break;
//					case 2:{
//						if(StringUtils.isBlank(phone)){
//							return ApiResult.outSucceed(ApiResultCode.PHONE_NOT_NULL,"参数phone不能为空");
//						}else if(StringUtils.isBlank(phoneCode)){
//							return ApiResult.outSucceed(ApiResultCode.PHONE_CODE_NOT_NULL,"参数phoneCode不能为空");
//						}
//						
//						result = manualQueryAdapter.loginEsurfing(userId, manualId, phone, phoneCode);
//					}
//						break;
//					default:
//						break;
//				}
//			}
			//天翼
			case 4:{
				switch (many) {
					case 1:{
						if(StringUtils.isBlank(phone)){
							return ApiResult.outSucceed(ApiResultCode.IDENTITYCARD_NOT_NULL,"参数phone不能为空");
						}else if(StringUtils.isBlank(password)){
							return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"参数password不能为空");
						}else if(StringUtils.isBlank(imageCode) && isImageCode){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数imageCode不能为空");
						}
						
						String reg2 = "1(33|53|80|89|81|77)[0-9]{8}";
				        if(phone.matches(reg2)){//电信用户
				        	result = manualQueryAdapter.login(manualQueryYjf189, userId, manualId, phone, password, imageCode, true, isImageCode, version);
				        }else{//非电信用户
				        	result = manualQueryAdapter.login(manualQueryYjf189nt, userId, manualId, phone, password, imageCode, true, isImageCode, version);
				        }
					}
						break;
					default:
						break;
				}
			}
				break;
			//国航
			case 5:{
				if(StringUtils.isBlank(loginAccount)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"参数loginAccount不能为空");
				}else if(StringUtils.isBlank(password)){
					return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"参数password不能为空");
				}else if(StringUtils.isBlank(imageCode) && isImageCode){
					return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数imageCode不能为空");
				}
				
				result = manualQueryAdapter.login(manualQueryAirChina, userId, manualId, loginAccount, password, imageCode, true, isImageCode, version);
//		        result = manualQueryAdapter.loginAirChina(userId,manualId,loginAccount, password, imageCode);
			}
				break;
			//联通
			case 6:{
				switch (many) {
					case 1:{
						if(StringUtils.isBlank(phone)){
							return ApiResult.outSucceed(ApiResultCode.IDENTITYCARD_NOT_NULL,"参数phone不能为空");
						}else if(StringUtils.isBlank(password)){
							return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"参数password不能为空");
						}else if(StringUtils.isBlank(imageCode) && isImageCode){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数imageCode不能为空");
						}
						result = manualQueryAdapter.login(manualQueryCU, userId, manualId, phone, password, imageCode, true, isImageCode, version);
					}
						break;
					default:
						break;
				}
			}
				break;
			//南航
			case 7:{
				if(StringUtils.isBlank(loginAccount)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"参数loginAccount不能为空");
				}else if(StringUtils.isBlank(password)){
					return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"参数password不能为空");
				}else if(StringUtils.isBlank(imageCode) && isImageCode){
					return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数imageCode不能为空");
				}
				

				result = manualQueryAdapter.login(manualQueryCsAir, userId, manualId, loginAccount, password, imageCode, true, isImageCode, version);
//		        result = manualQueryAdapter.loginCsAir(userId, manualId, loginAccount, password, imageCode);
			}
				break;   
			//移动
			case 8:{
				if(StringUtils.isBlank(phone)){
					return ApiResult.outSucceed(ApiResultCode.PHONE_NOT_NULL,"手机号不能为空");
				}else if(StringUtils.isBlank(imageCode) && isImageCode){
					return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"图片验证码不能为空");
				}else if(StringUtils.isBlank(password)){
					return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"服务密码不能为空");
				}
				result = manualQueryAdapter.login(manualQueryCMCC, userId, manualId, phone, password, imageCode, true, isImageCode, version);
//				result = manualQueryAdapter.loginCMCC(userId,manualId,password,phone,imageCode);
				/**
				 *用短信验证码登录
				switch (many) {
					case 1:{
						if(StringUtils.isBlank(phone)){
							return ApiResult.outSucceed(ApiResultCode.PHONE_NOT_NULL,"手机号不能为空");
						}else if(StringUtils.isBlank(imageCode) && isImageCode){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"图片验证码不能为空");
						}

						result = manualQueryAdapter.login(manualQueryCMCC, userId, manualId, phone, "", imageCode, true, isImageCode);
//						result = manualQueryAdapter.getCMCCPhoneCode(userId, phone,imageCode);
					}
						break;
					case 2:{
						if(StringUtils.isBlank(phoneCode)){
							return ApiResult.outSucceed(ApiResultCode.PHONE_CODE_NOT_NULL,"短信验证码不能为空");
						}

						result = manualQueryAdapter.loginCMCC(userId,manualId,phon eCode);
					}
						break;
					default:
						break;
				}
				 */
			}
				break;
			//洲际
			case 9:{
				if(StringUtils.isBlank(loginAccount)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"账号不能为空");
				}else if(StringUtils.isBlank(password)){
					return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"密码不能为空");
				}
			    result = manualQueryAdapter.loginIHG(userId, manualId, loginAccount, password, version);
			}
				break;
			//花旗银行
			case 10:{
				if(StringUtils.isBlank(loginAccount)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"账号不能为空");
				}else if(StringUtils.isBlank(password)){
					return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"密码不能为空");
				}
			    result = manualQueryAdapter.loginCityBank(userId, manualId, loginAccount, password, version);
			}
				break;
			//铂涛会
			case 11:{
				switch (many) {
					case 1:{
						if(StringUtils.isBlank(phone)){
							return ApiResult.outSucceed(ApiResultCode.PHONE_NOT_NULL,"账号不能为空");
						}else if(StringUtils.isBlank(imageCode) && isImageCode){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"图片验证码不能为空");
						}
						
						result = manualQueryAdapter.login(manualQueryBoTaoHui, userId, manualId, phone, "", imageCode, true, isImageCode, version);
	//					result = manualQueryAdapter.getBoTaoHuiPhoneCode(userId, phone,imageCode);
					}
						break;
					case 2:{
						if(StringUtils.isBlank(phoneCode)){
							return ApiResult.outSucceed(ApiResultCode.PHONE_CODE_NOT_NULL,"手机验证码不能为空");
						}
						result = manualQueryAdapter.loginBoTaoHui(userId,manualId,phoneCode, version);
					}
						break;
					default:
						break;
				}
			}
				break;
			//物美
			case 12:{
				switch (many) {
					case 1:{//首次登录
						if(StringUtils.isBlank(phone)){
							return ApiResult.outSucceed(ApiResultCode.PHONE_NOT_NULL,"账号不能为空");
						}else if(StringUtils.isBlank(imageCode) && isImageCode){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"图片验证码不能为空");
						}else if(StringUtils.isBlank(password)){
							return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"密码不能为空");
						}else if(StringUtils.isBlank(provinceType)){
							return ApiResult.outSucceed(ApiResultCode.WUMEI_PROVINCE_NOT_NULL,"省份不能为空");
						}
						
						result = manualQueryAdapter.wumeiLogin(userId,manualId,phone,imageCode,password,provinceType,isImageCode, version);
					}
						break;
					case 2:{//重置密码时图片验证
						if(StringUtils.isBlank(imageCode)){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"图片验证码不能为空");
						}
						result = manualQueryAdapter.checkWumeiImgCode(userId,manualId,imageCode);
					}
						break;
					case 3:{//重置密码时短信验证
						if(StringUtils.isBlank(phoneCode)){
							return ApiResult.outSucceed(ApiResultCode.PHONE_CODE_NOT_NULL,"手机验证码不能为空");
						}
						result = manualQueryAdapter.checkWumeiPhoneCode(userId,manualId,phoneCode, version);
					}
						break;
					case 4:{//重置密码后登录
						if(StringUtils.isBlank(imageCode)){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"图片验证码不能为空");
						}
						result = manualQueryAdapter.wumeiLoginSecond(userId,manualId,imageCode, version);
					}
						break;
					default:
						break;
				}
			}
				break;
			//交通
			case 13:{
				if(StringUtils.isBlank(bankCard)){
					return ApiResult.outSucceed(ApiResultCode.BANKCARD_NOT_NULL,"参数bankCard不能为空");
				}else if(StringUtils.isBlank(password)){
					return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"参数password不能为空");
				}

				result = manualQueryAdapter.login(manualQueryCOMM,userId,manualId,bankCard,password,null,false,false, version);
			}
			break;
			
			//建行
			case 14:{
				
				if(StringUtils.isBlank(bankCard)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"卡号不能为空");
				}else if(StringUtils.isBlank(password)){
					return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"密码不能为空");
				}
				result = manualQueryCCB.login(userId,bankCard,password,manualId, version);
			}
			break;
			
			//中信
			case 15:{
				if(StringUtils.isBlank(phoneCode)){
					if(StringUtils.isBlank(identityCard)){
						return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"账号不能为空");
					}else if(StringUtils.isBlank(password)){
						return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"密码不能为空");
					}
					
					result = manualQueryCCBI.login(userId, identityCard, password, imageCode,manualId, version);
				}else {
					result = manualQueryCCBI.checkCode(userId,phoneCode,manualId, version);
				}
			}
			break;
			//浦发
			case 16:{
				switch (many) {
					case 1:{
						if(StringUtils.isBlank(identityCard)){
							return ApiResult.outSucceed(ApiResultCode.IDENTITYCARD_NOT_NULL,"账号不能为空");
						}else if(StringUtils.isBlank(imageCode) && isImageCode){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"图片验证码不能为空");
						}else if(StringUtils.isBlank(password)){
							return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"查询密码不能为空");
						}
						
						result = manualQueryAdapter.login(manualQueryPuFaBank, userId, manualId, identityCard, password, imageCode, true, isImageCode, version);
	//					result = manualQueryAdapter.getBoTaoHuiPhoneCode(userId, phone,imageCode);
					}
						break;
					case 2:{
						if(StringUtils.isBlank(phoneCode)){
							return ApiResult.outSucceed(ApiResultCode.PHONE_CODE_NOT_NULL,"手机验证码不能为空");
						}
						result = manualQueryAdapter.loginPuFaBank(userId,manualId,phoneCode, version);
					}
						break;
					default:
						break;
				}
			}
			break;
			//平安
			case 17:{
					if(StringUtils.isBlank(yztAccount)){
						return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"账号不能为空");
					}else if(StringUtils.isBlank(yztPassword)){
						return ApiResult.outSucceed(ApiResultCode.PASSWORD_NOT_NULL,"查询密码不能为空");
					}
					result = manualQueryAdapter.login(manualQueryPingAnBank, userId, manualId, yztAccount, yztPassword, imageCode, true, isImageCode, version);
				}
			
				break;
			
		default:
				AssertApiUtil.notTrue(true,"暂不支持此积分账户",ApiResultCode.NONSUPPORT_MANUAL);
				break;
		}
		
		
		//查询获得经验
		ExpData expdata = new ExpData();
		expdata.setUserId(userId);
		expdata.setIntegralResult(result);
		addExp.addExp(expdata, addExpByIntegral);
		 	
		
		//手动查询,首次导入送100财币
//		if(JsonResult.checkResult(result)){
//			try {
//				rechargeService.gift(userId, 2, "1", type);
//			} catch (Exception e) {
//				logger.error("手动查询,首次导入送100财币.gift  error: {}",e);
//			}
//		}
		if("3.0.0".compareTo(version) <= 0){//3.0.0版本做如下处理
			JSONObject resultData = JSON.parseObject(result);
			Integer code = resultData.getInteger("code");
			
			if(code == 0){
				JSONObject data = resultData.getJSONObject("data");
				
				//处理返回结果
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", data.getString("userId"));
				map.put("manualId", data.getString("manualId"));
				map.put("loginAccount", data.getString("loginAccount"));
				List<ManualResult> manualInfos = userCardService.getByUserManualInfo(map );
				
				for (ManualResult manualInfo : manualInfos) {
					if(StringUtils.isNotBlank(manualInfo.getPicUrl())){
						//补全图片路径
						manualInfo.setPicUrl(appConfig.staticUrl + manualInfo.getPicUrl());
					}
					//成功
					manualInfo.setResult(SysConstants.SUCCESS);
					manualInfo.setRemark("");
					
					String cardNo = manualInfo.getCardNo();
					if(StringUtils.isNotBlank(cardNo)){
						if(cardNo.length() > 4){
							manualInfo.setCardNo(cardNo.substring(cardNo.length() - 4));
						}
					}
					
					JSONObject addInfo = data.getJSONObject(manualInfo.getCardTypeId().toString());
					if(null == addInfo){
						manualInfo.setAddIntegral("0");
						manualInfo.setAddTubi("0");
					}else{
						manualInfo.setAddIntegral(addInfo.getString("addIntegral"));
						manualInfo.setAddTubi(addInfo.getString("addTubi"));
					}
				}
				resultData.put("data", manualInfos);
	
				return resultData.toJSONString();
			}
		}
		
		return result;
	}
	
	@RequestMapping(value="getimagecode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getimagecode(Long userId,Long manualId) {
		String result = "";
		
		AssertApiUtil.notNull(userId, "用户id不能为空 ",ApiResultCode.USER_ID_NOT_NULL);
		AssertApiUtil.notNull(manualId, "积分账户不能为空",ApiResultCode.MANUAL_ID_NOT_NULL);
		
		switch (manualId.intValue()) {
			//淘宝
			case 1:
				break;
			//招行
			case 2:
				break;
			//京东
			case 3:
				break;
			//天翼
			case 4:
				break;
			//国航
			case 5:
				break;
			default:
				AssertApiUtil.notTrue(true,"暂不支持此积分账户",ApiResultCode.NONSUPPORT_MANUAL);
				break;
		}
		
		return result;
	}
	
	@RequestMapping(value="getphonecode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getphonecode(Long userId,Long manualId) {
		String result = "";
		
		AssertApiUtil.notNull(userId, "用户id不能为空 ",ApiResultCode.USER_ID_NOT_NULL);
		AssertApiUtil.notNull(manualId, "积分账户不能为空",ApiResultCode.MANUAL_ID_NOT_NULL);
		
		switch (manualId.intValue()) {
			//淘宝
			case 1:
				break;
			//招行
			case 2:
				break;
			//京东
			case 3:
				break;
			//天翼
			case 4:
				break;
			//国航
			case 5:
				break;
			default:
				AssertApiUtil.notTrue(true,"暂不支持此积分账户",ApiResultCode.NONSUPPORT_MANUAL);
				break;
		}
		
		return result;
	}
	
	/**
	 * 验证图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkimagecode 
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2015年11月18日 下午8:51:16  
	 * @author xiongbin
	 */
	@RequestMapping(value="checkimagecode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String checkimagecode(Long userId,Long manualId,String imageCode) {
		String result = "";
		
		AssertApiUtil.notNull(userId, "用户id不能为空 ",ApiResultCode.USER_ID_NOT_NULL);
		AssertApiUtil.notNull(manualId, "积分账户不能为空",ApiResultCode.MANUAL_ID_NOT_NULL);
		AssertApiUtil.hasLength(imageCode, "图片验证码不能为空",ApiResultCode.IMAGE_CODE_NOT_NULL);
		
		switch (manualId.intValue()) {
			//淘宝
			case 1:
				break;
			//招行
			case 2:
				break;
			//京东
			case 3:
				break;
			//天翼
			case 4:
				break;
			//国航
			case 5:
				break;
			default:
				AssertApiUtil.notTrue(true,"暂不支持此积分账户",ApiResultCode.NONSUPPORT_MANUAL);
				break;
		}
		
		return result;
	}
	
	/**
	 * 验证短信验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkphonecode 
	 * @param userId		用户ID
	 * @param manualId		积分账户ID
	 * @param phoneCode		短信验证码
	 * @return
	 * @date 2015年11月18日 下午8:59:42  
	 * @author xiongbin
	 */
	@RequestMapping(value="checkphonecode/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String checkphonecode(Long userId,Long manualId,String phoneCode) {
		String result = "";
		
		AssertApiUtil.notNull(userId, "用户id不能为空 ",ApiResultCode.USER_ID_NOT_NULL);
		AssertApiUtil.notNull(manualId, "积分账户不能为空",ApiResultCode.MANUAL_ID_NOT_NULL);
		AssertApiUtil.notNull(phoneCode, "短信验证码不能为空",ApiResultCode.PHONE_CODE_NOT_NULL);
		
		switch (manualId.intValue()) {
			//淘宝
			case 1:
				break;
			//招行
			case 2:
				break;
			//京东
			case 3:
				break;
			//天翼
			case 4:
				//将用户账号从redis取出
				String key = String.format(RedisKey.TIANYI_LOGINACCOUNT_USER, userId);
				String loginAccount = redis.getStringByKey(key);
				
				AssertApiUtil.hasLength(loginAccount,"系统错误",-1);
				
				result = manualQueryAdapter.loginEsurfing(userId, manualId, loginAccount, phoneCode);
				break;
			//国航
			case 5:
				break;
			default:
				AssertApiUtil.notTrue(true,"暂不支持此积分账户",ApiResultCode.NONSUPPORT_MANUAL);
				break;
		}
		
		return result;
	}
	
	
	@RequestMapping(value="modpwd/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String resetPassword(String userId,Long manualId,Integer many,String paramJson) {

		String result = "";
		
		JSONObject json = JSON.parseObject(paramJson);
		String phone = json.getString("phone");
		String password = json.getString("password");
        String imageCode= json.getString("imageCode");
		String phoneCode = json.getString("phoneCode");
		
		boolean isImageCode = false;
		if(StringUtils.isNotBlank(imageCode)){
			isImageCode = true;
		}

		if(null == userId){
			return ApiResult.outSucceed(-1,"参数userid不能为空");
		}else if(null == manualId){
			return ApiResult.outSucceed(-1,"参数manualId不能为空");
		}else if(null == many){
			return ApiResult.outSucceed(-1,"参数many不能为空");
		}
		
		switch (manualId.intValue()) {
			//天翼
			case 4:{
				switch (many) {
					case 1:{
						if(StringUtils.isBlank(phone)){
							return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"参数phone不能为空");
						}else if(StringUtils.isEmpty(imageCode) && isImageCode){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数imageCode不能为空");
						}
						
						result = manualQueryYjf189nt.resetPwdStep2(userId, phone, imageCode);
					}
						break;
					case 2:{
						if(StringUtils.isBlank(password)){
							return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NOT_NULL,"参数password不能为空");
						}else if(StringUtils.isEmpty(phoneCode)){
							return ApiResult.outSucceed(ApiResultCode.IMAGE_CODE_NOT_NULL,"参数phoneCode不能为空");
						}
						
						result = manualQueryYjf189nt.resetPwdStep3(userId, password, phoneCode);
					}
						break;
					default:
						break;
				}
			}
				break;
		default:
				AssertApiUtil.notTrue(true,"暂不支持此积分账户",ApiResultCode.NONSUPPORT_MANUAL);
				break;
		}
		return result;
	}
	
	
}
