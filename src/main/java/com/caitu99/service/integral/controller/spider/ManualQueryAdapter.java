package com.caitu99.service.integral.controller.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.service.PresentTubiService;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.domain.UserCardManualItem;
import com.caitu99.service.integral.service.ExchangeRuleService;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.ManualService;
import com.caitu99.service.integral.service.UserCardManualItemService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.date.TimeUtil;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;
import com.caitu99.service.utils.string.IdCardValidator;
import com.caitu99.service.utils.string.StrUtil;

@Component
public class ManualQueryAdapter {
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryAdapter.class);
	
	@Autowired
	private ManualQueryCityBank manualQueryCityBank;
	
	@Autowired
	private ManualQueryIHG manualQueryIHG;
	
	@Autowired
	private ManualQueryEsurfing manualQueryEsurfing;
	
	@Autowired
	private ManualQueryAirChina manualQueryAirChina;
	
	@Autowired
	private ManualQueryJingDong manualQueryJingDong;
	
	@Autowired
	private ManualQueryCMB manualQueryCMB;
	
	@Autowired
	private ManualQueryCsAir manualQueryCsAir;
	
	@Autowired
	private ManualQueryTaobao manualQueryTaobao;
	
	@Autowired
	private ManualQueryCMCC manualQueryCMCC;
	
	@Autowired
	private ManualQueryCU manualQueryCU;
	
	@Autowired
	private ManualService manualService;
	
	@Autowired
	private UserCardManualService userCardManualService;
	
	@Autowired
	private ManualLoginService manualLoginService;
	
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private ManualQueryBoTaoHui manualQueryBoTaoHui;
	
	@Autowired
	private ManualQueryPuFaBank manualQueryPuFaBank;
	
	@Autowired
	private ManualQueryWuMei manualQueryWuMei;
	
	@Autowired
	private UserCardManualItemService userCardManualItemService;
	
	@Autowired
	protected PresentTubiService presentTubiService;
	
	/**
	 * 获取图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userid			用户ID
	 * @param manualQuery		手动查询类
	 * @return
	 * @date 2016年1月11日 下午5:24:42  
	 * @author xiongbin
	 */
	public String getImageCode(Long userid,ManualQueryAbstract manualQuery){
		return manualQuery.getImageCode(userid);
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param manualQuery			手动查询类
	 * @param userId				用户ID
	 * @param manualId				积分账户ID
	 * @param loginAccount			登录名
	 * @param password				密码
	 * @param imageCode				图片验证码
	 * @param isExistImageCode		是否有图片验证码
	 * @param isImageCode			是否需要输入验证码
	 * @return
	 * @date 2016年1月12日 上午10:54:39  
	 * @author xiongbin
	 * @param version 
	 */
	public String login(ManualQueryAbstract manualQuery,Long userId,Long manualId,String loginAccount,
																String password,String imageCode,boolean isExistImageCode,boolean isImageCode, String version){
		
		if(isExistImageCode){
			String reslut;
			if(!isImageCode){
				//自动识别图片验证码并登录
				reslut = crackImageLogin(manualQuery,userId,manualId,loginAccount,password,2);
			}else{
				reslut = manualQuery.login(userId, loginAccount, password, imageCode);
			}
			
			return save(manualQuery,userId, manualId,loginAccount, password,reslut,version);
		}else{
			String reslut = manualQuery.login(userId, loginAccount, password);
			return save(manualQuery,userId, manualId,loginAccount, password,reslut,version);
		}
	}

	/**
	 * 保存
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: save 
	 * @param manualQuery		手动查询类
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param loginAccount		登录名
	 * @param password			密码
	 * @param reslut			返回值
	 * @return
	 * @date 2016年1月12日 上午11:18:49  
	 * @author xiongbin
	 * @param version 
	 */
	public String save(ManualQueryAbstract manualQuery,Long userId,Long manualId,String loginAccount,String password,String reslut, String version){
		if(StringUtils.isBlank(reslut)){
			return ApiResult.outSucceed(-1,"登录失败,返回数据为空为空");
		}
		
		reslut = manualQuery.save(userId, manualId, loginAccount, password, reslut,version);
		
		if(StringUtils.isBlank(reslut)){
			return ApiResult.outSucceed(-1,"登录失败,返回数据为空为空");
		}
		
		boolean flag = JsonResult.checkResult(reslut);
		boolean flag2 = JsonResult.checkResult(reslut,ApiResultCode.FOLLOWUP_VERIFY_PHONE);
		if(!flag && !flag2){
			String imageCode = manualQuery.getImageCode(userId);
			imageCode = manualQuery.analysisImageCode(imageCode);
			
			JSONObject reslutJson = JSON.parseObject(reslut);
			reslutJson.put("imagecode", imageCode);
			
			return reslutJson.toJSONString();
		}
		
		return reslut;
	}
	
	/**
	 * 登陆花旗银行并获取积分
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginCityBank
	 * @param userid		用户ID
	 * @return
	 * @date 2015年11月14日 下午2:31:47  
	 * @author chenhl
	 */
	public String loginCityBank(Long userId, Long manualId, String loginAccount, String password, String version){
		try{
			String result = manualQueryCityBank.login(userId, loginAccount, password);
			Boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);

			
			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			
			if(!flag){
				return result;
			}
			/* {"code":0,"data":"{\"cardno\":\"9294\",\"jifen\":\"103460\",\"name\":\"周靖\"}","message":"获取花旗积分成功"}
			 */
			JSONObject json = JSON.parseObject(result);
			String data = json.getString("data");
			JSONObject json2 = JSON.parseObject(data);
			Integer jifen = json2.getInteger("jifen");
			String cardno = json2.getString("cardno");
			String name = json2.getString("name");
			Date now = new Date();
			
			/**
			 * 记录用户积分数据 
			 */
			UserCardManual CityBankManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CITYBANK_INTEGRAL,cardno,name,loginAccount);
			
			if(null == CityBankManual){
				presentTubiService.presentTubiDo(userId, UserCardManual.CITYBANK_INTEGRAL, 0, jifen, resData, version);
				
				CityBankManual = new UserCardManual();
				CityBankManual.setIntegral(jifen);
				CityBankManual.setUserName(name);
				CityBankManual.setGmtModify(now);
				CityBankManual.setGmtCreate(now);
				CityBankManual.setUserId(userId);
				CityBankManual.setLoginAccount(loginAccount);
				CityBankManual.setCardNo(cardno);
				CityBankManual.setCardTypeId(UserCardManual.CITYBANK_INTEGRAL);
			}else{
				presentTubiService.presentTubiDo(userId, UserCardManual.CITYBANK_INTEGRAL, CityBankManual.getIntegral(), jifen, resData, version);
				
				CityBankManual.setIntegral(jifen);
				CityBankManual.setUserName(name);
				CityBankManual.setLoginAccount(loginAccount);
				CityBankManual.setGmtModify(now);
				CityBankManual.setCardNo(cardno);
				CityBankManual.setStatus(1);
			}
			userCardManualService.insertORupdate(CityBankManual);
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			manualLogin.setPassword(password);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			manualLogin.setType(ManualLogin.TYPE_LOGIN_ACCOUNT);
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLoginService.insert(manualLogin);
			}else{
				manualLogin.setPassword(password);
				manualLogin.setStatus(1);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			resultJson.put("data", resData);
			
			return resultJson.toJSONString();
			
		}catch(ApiException e){
			logger.warn(e.getMessage(),e);
			throw e;
		}catch (Exception e) {
			logger.error("登录花旗银行失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,e.getMessage());
		}
	}
	
	
	/**
	 * 登陆洲际并获取积分
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginIHG
	 * @param userid		用户ID
	 * @return
	 * @date 2015年11月14日 下午2:31:47  
	 * @author chenhl
	 */
	public String loginIHG(Long userId, Long manualId, String loginAccount, String password, String version){
		try{
			String result = manualQueryIHG.login(userId, loginAccount, password);
			Boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			
			if(!flag){
				return result;
			}
			/* {
    			"code": 0,
    			"data": "{\"jifen\":\"0\"}",
    			"message": "获取IHG积分成功"
				}
			 */
			JSONObject json = JSON.parseObject(result);
			String data = json.getString("data");
			JSONObject json2 = JSON.parseObject(data);
			Integer jifen = json2.getInteger("jifen");
			String account = json2.getString("account");
			String username = json2.getString("username");
			Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			/**
			 * 记录用户积分数据 
			 */
			UserCardManual IHGManualAccount = userCardManualService.getUserCardManualSelective(userId,UserCardManual.IHG_INTEGRAL,null,null,loginAccount);
			if(IHGManualAccount == null){
				UserCardManual IHGManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.IHG_INTEGRAL,null,username,account);
				if(null == IHGManual){
					presentTubiService.presentTubiDo(userId, UserCardManual.IHG_INTEGRAL, 0, jifen, resData, version);
					
					IHGManual = new UserCardManual();
					IHGManual.setIntegral(jifen);
					IHGManual.setUserName(username);
					IHGManual.setGmtModify(now);
					IHGManual.setGmtCreate(now);
					IHGManual.setUserId(userId);
					IHGManual.setLoginAccount(account);
					IHGManual.setCardTypeId(UserCardManual.IHG_INTEGRAL);
				}else{
					presentTubiService.presentTubiDo(userId, UserCardManual.IHG_INTEGRAL, IHGManual.getIntegral(), jifen, resData, version);
					
					IHGManual.setIntegral(jifen);
					IHGManual.setUserName(username);
					IHGManual.setLoginAccount(account);
					IHGManual.setGmtModify(now);
					IHGManual.setStatus(1);
				}
				userCardManualService.insertORupdate(IHGManual);
			}else{
				presentTubiService.presentTubiDo(userId, UserCardManual.IHG_INTEGRAL, IHGManualAccount.getIntegral(), jifen, resData, version);
				
				IHGManualAccount.setIntegral(jifen);
				IHGManualAccount.setUserName(username);
				IHGManualAccount.setLoginAccount(account);
				IHGManualAccount.setGmtModify(now);
				IHGManualAccount.setStatus(1);
				userCardManualService.updateByPrimaryKeySelective(IHGManualAccount);
			}
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(account);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			manualLogin.setPassword(password);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			manualLogin.setType(ManualLogin.TYPE_LOGIN_ACCOUNT);
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLoginService.insert(manualLogin);
			}else{
				manualLogin.setPassword(password);
				manualLogin.setStatus(1);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			resultJson.put("data", resData);
			
			return resultJson.toJSONString();
			
		}catch(ApiException e){
			logger.error(e.getMessage(),e);
			throw e;
		}catch (Exception e) {
			logger.error("登录洲际失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,e.getMessage());
		}
	}

	/**
	 * 获取天翼图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getEsurfingImageCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2015年11月14日 下午2:31:47  
	 * @author xiongbin
	 */
	public String getEsurfingImageCode(Long userid){
		return manualQueryEsurfing.getImageCode(userid);
	}
	
	/**
	 * 获取国航图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getAirChinaImageCode 
	 * @param userid	用户ID
	 * @return
	 * @date 2015年11月14日 下午2:34:11  
	 * @author xiongbin
	 */
	public String getAirChinaImageCode(Long userid){
		return manualQueryAirChina.getImageCode(userid);
	}
	
	/**
	 * 验证天翼图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkImageCode 
	 * @param userId		用户ID
	 * @param phoneNo		手机号码
	 * @param imgCode		图片验证码
	 * @return
	 * @throws ManualQueryAdaptorException
	 * @date 2015年11月14日 下午7:28:21  
	 * @author xiongbin
	 */
	public String checkEsurfingImageCode(Long userId,Long manualId,String phoneNo,String imgCode) throws ManualQueryAdaptorException {
		try {
			String result = manualQueryEsurfing.checkImageCode(userId,phoneNo, imgCode);
			
			return result;
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("验证天翼图片验证码失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CHECH_TIANYI_IMAGE_CODE_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 登录天翼
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginEsurfing 
	 * @param userId		用户ID
	 * @param manualId		积分账户ID
	 * @param phoneNo		手机号码
	 * @param msCode		短信验证码
	 * @param type			
	 * @return
	 * @date 2015年11月14日 下午8:45:41  
	 * @author xiongbin
	 */
	@Deprecated
	public String loginEsurfing(Long userId,Long manualId,String phoneNo, String msCode){
		try {
			String result = manualQueryEsurfing.login(userId, manualId, phoneNo, msCode);
			
			JSONObject json = JSON.parseObject(result);
			if(!json.getInteger("code").equals(0)){
				return json.toJSONString();
			}
			
			String jsonString = json.getString("data");
			AssertUtil.notNull(jsonString, "从天翼获取用户数据失败");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			AssertUtil.notNull(jsonObject, "从天翼获取用户数据失败");
			
			//电信积分
			Integer integral = jsonObject.getInteger("Integral");
			//天翼积分
			Integer voucher = jsonObject.getInteger("Voucher");	
			//用户名
			String userName = jsonObject.getString("custName");
			
			Date now = new Date();
			
			/**
			 * 记录用户积分数据 
			 */
			//电信积分
//			UserCardManual integralManual = userCardManualService.getByUserIdCardTypeId(userId, UserCardManual.CT_INTEGRAL);
			UserCardManual integralManual = userCardManualService.getUserCardManualSelective(userId, UserCardManual.CT_INTEGRAL,null,null,phoneNo);
			
			if(integralManual != null){
				integralManual.setIntegral(integral);
				integralManual.setGmtModify(now);
				integralManual.setLoginAccount(phoneNo);
				integralManual.setStatus(1);
				userCardManualService.updateByPrimaryKeySelective(integralManual);
			}else{
				integralManual = new UserCardManual(userId,UserCardManual.CT_INTEGRAL,userName,integral);
				integralManual.setLoginAccount(phoneNo);
				integralManual.setGmtCreate(now);
				integralManual.setGmtModify(now);
				userCardManualService.insert(integralManual);
			}
			
			//天翼积分
//			UserCardManual voucherManual = userCardManualService.getByUserIdCardTypeId(userId, UserCardManual.ESURFING_INTEGRAL);
			UserCardManual voucherManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.ESURFING_INTEGRAL,null,null,phoneNo);
			
			if(voucherManual != null){
				voucherManual.setIntegral(voucher);
				voucherManual.setGmtModify(now);
				voucherManual.setLoginAccount(phoneNo);
				voucherManual.setStatus(1);
				userCardManualService.updateByPrimaryKeySelective(voucherManual);
			}else{
				voucherManual = new UserCardManual(userId,UserCardManual.ESURFING_INTEGRAL,userName,voucher);
				voucherManual.setLoginAccount(phoneNo);
				voucherManual.setGmtCreate(now);
				voucherManual.setGmtModify(now);
				userCardManualService.insert(voucherManual);
			}
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin(userId,manualId,phoneNo,ManualLogin.IS_PASSWORD_NO,ManualLogin.TYPE_PHONE);
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			
			if(oldManualLogin == null){
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setStatus(1);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录天翼失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.TIANYI_LOGIN_ERROR,e.getMessage());
		}
	}

	/**
	 * 登录国航
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginAirChina 
	 * @param userId
	 * @param manualId
	 * @param loginAccount
	 * @param password
	 * @param imageCode
	 * @param type
	 * @return
	 * @date 2015年11月14日 下午8:47:40  
	 * @author xiongbin
	*/
	@Deprecated
	public String loginAirChina(Long userId, Long manualId,String loginAccount, String password, String imageCode) {
		try {
			String result = manualQueryAirChina.login(userId, loginAccount, password, imageCode);
			
			JSONObject json = JSON.parseObject(result);
//		{
//		    "data": "{\"thisInvalid\":\"0\",\"nextInvalid\":\"0\",\"name\":\"刘存显\",\"available\":\"54736\",\"account\":\"000577370850\"}",
//		    "message": "0"
//		}
			
			String message = json.getString("message");
			if(message==null || !message.equals("0")){
				return result;
			}
			
			String jsonString  = json.getString("data");
			AssertUtil.hasLength(jsonString, "登录成功,但返回数据出错.data为空");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			Integer expirationIntegral = jsonObject.getInteger("thisInvalid");		//本月过期里程
			Integer nextExpirationIntegral = jsonObject.getInteger("nextInvalid");		//下月过期里程
			String userName =  jsonObject.getString("name");					//用户名
			Integer integral = jsonObject.getInteger("available");			//可用积分
			String cardNo = jsonObject.getString("account");				//卡号
			
			/**
			 * 记录用户积分数据 
			 */
//			UserCardManual airChinaManual = userCardManualService.getByUserIdCardTypeId(userId,UserCardManual.AIRCHINA_INTEGRAL);
			//判断此账号是否有数据
			UserCardManual airChinaManualAccount = userCardManualService.getUserCardManualSelective(userId, UserCardManual.AIRCHINA_INTEGRAL, null, null, loginAccount);
			if(airChinaManualAccount != null){
				airChinaManualAccount.updateIntegral(integral,cardNo,expirationIntegral,nextExpirationIntegral,userName,new Date());
				airChinaManualAccount.setLoginAccount(loginAccount);
				userCardManualService.updateByPrimaryKeySelective(airChinaManualAccount);
			}else{
				UserCardManual airChinaManual = userCardManualService.getUserCardManualSelective(userId, UserCardManual.AIRCHINA_INTEGRAL, cardNo, null, null);
				if(airChinaManual != null){
					airChinaManual.updateIntegral(integral,cardNo,expirationIntegral,nextExpirationIntegral,userName,new Date());
					airChinaManual.setLoginAccount(loginAccount);
					userCardManualService.updateByPrimaryKeySelective(airChinaManual);
				}else{
					airChinaManual = new UserCardManual(integral,cardNo,expirationIntegral,nextExpirationIntegral,userName,userId,UserCardManual.AIRCHINA_INTEGRAL);
					airChinaManual.setLoginAccount(loginAccount);
					userCardManualService.insert(airChinaManual);
				}
			}
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			//manualLogin.setPassword(password);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			
			if(StrUtil.isPhone(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_PHONE);
			}else{
				manualLogin.setType(ManualLogin.TYPE_CARD_NO);
			}

			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setPassword(password);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录国航失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.AIRCHINA_LOGIN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 获取京东图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getJingDongImageCode 
	 * @param userid			用户ID
	 * @return
	 * @date 2015年11月16日 下午6:04:17  
	 * @author xiongbin
	 */
	public String getJingDongImageCode(Long userid){
		return manualQueryJingDong.getImageCode(userid);
	}
	
	/**
	 * 登录京东
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkJingDongImageCode 
	 * @param userid			用户ID
	 * @param loginAccount		用户账号
	 * @param password			密码
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2015年11月16日 下午6:50:22  
	 * @author xiongbin
	 */
	@Deprecated
	public String loginJingDong(Long userId,Long manualId,String loginAccount,String password,String imageCode,boolean isImageCode){
		try {
			String result;
			
			if(!isImageCode){
				//自动识别图片验证码并登录
				result = crackImageLogin(manualQueryJingDong,userId,manualId,loginAccount,password,2);
			}else{
				result = manualQueryJingDong.login(userId, loginAccount, password, imageCode);
			}
			
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				return result;
			}
			
			JSONObject json = JSON.parseObject(result);
			String jsonString = json.getString("data");
			
			AssertUtil.hasLength(jsonString, "获取用户京东数据失败");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
			Integer integral = jsonObject.getInteger("jindou");
			String userName = jsonObject.getString("name");
			
			Date now = new Date();
			
			/**
			 * 记录用户积分数据 
			 */
//			UserCardManual jingDongManual = userCardManualService.getByUserIdCardTypeId(userId,UserCardManual.JINGDONG_INTEGRAL);
			//先判断账号是否登录过,兼容自动发现
			UserCardManual jingDongManualAccount = userCardManualService.getUserCardManualSelective(userId,UserCardManual.JINGDONG_INTEGRAL,null,null,loginAccount);
			if(jingDongManualAccount == null){
				UserCardManual jingDongManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.JINGDONG_INTEGRAL,null,userName,null);
				if(jingDongManual != null){
					jingDongManual.setIntegral(integral);
					jingDongManual.setUserName(userName);
					jingDongManual.setGmtModify(now);
					jingDongManual.setLoginAccount(loginAccount);
					
					userCardManualService.updateByPrimaryKeySelective(jingDongManual);
				}else{
					jingDongManual = new UserCardManual(userId, UserCardManual.JINGDONG_INTEGRAL, userName, integral);
					jingDongManual.setLoginAccount(loginAccount);
					
					userCardManualService.insert(jingDongManual);
				}
			}else{
				jingDongManualAccount.setIntegral(integral);
				jingDongManualAccount.setUserName(userName);
				jingDongManualAccount.setGmtModify(now);
				jingDongManualAccount.setLoginAccount(loginAccount);
				
				userCardManualService.updateByPrimaryKeySelective(jingDongManualAccount);
			}
			
			

			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
//			manualLogin.setPassword(password);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			
			if(loginAccount.matches("^[1][358][0-9]{9}$")){
				manualLogin.setType(ManualLogin.TYPE_PHONE);
			}else{
				manualLogin.setType(ManualLogin.TYPE_LOGIN_ACCOUNT);
			}
			
			if(StrUtil.isPhone(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_PHONE);
			}else if(StrUtil.isEmail(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_EMAIL);
			}else{
				manualLogin.setType(ManualLogin.TYPE_LOGIN_ACCOUNT);
			}

			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setPassword(password);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);

			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录京东账户失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.JINGDONG_LOGIN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 获取招行图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getCMBImageCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2015年11月18日 下午2:44:37  
	 * @author xiongbin
	 */
	public String getCMBImageCode(Long userid){
		return manualQueryCMB.getImageCode(userid);
	}
	
	/**
	 * 登录招行
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginCMB 
	 * @param userId				用户ID
	 * @param manualId				积分账户ID
	 * @param identityCard			身份证
	 * @param password				密码
	 * @param imageCode				图片验证码
	 * @return
	 * @date 2015年11月18日 下午2:54:27  
	 * @author xiongbin
	 */
	@Deprecated
	public String loginCMB(Long userId,Long manualId,String identityCard,String password,String imageCode,boolean isImageCode){
		try {
			String result;
			
			if(!isImageCode){
				//自动识别图片验证码并登录
				result = crackImageLogin(manualQueryCMB,userId,manualId,identityCard,password,2);
			}else{
				result = manualQueryCMB.login(userId,identityCard,password,imageCode);
			}
			
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				return result;
			}
			
			JSONObject json = JSON.parseObject(result);
			String jsonString = json.getString("data");
			
			AssertUtil.hasLength(jsonString, "登录招行失败");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
			Integer integral = jsonObject.getInteger("integral");
			String userName = jsonObject.getString("name");
			String cardNo = jsonObject.getString("account");
			
			Date now = new Date();
			
			/**
			 * 记录用户积分数据 
			 */
//			UserCardManual CMBManual = userCardManualService.getByUserIdCardTypeId(userId,UserCardManual.CMB);
			UserCardManual CMBManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CMB,null,userName,identityCard);
			
			if(null == CMBManual){
				CMBManual = new UserCardManual();
				CMBManual.setIntegral(integral);
				CMBManual.setUserName(userName);
				CMBManual.setCardNo(cardNo);
				CMBManual.setGmtModify(now);
				CMBManual.setGmtCreate(now);
				CMBManual.setUserId(userId);
				CMBManual.setLoginAccount(identityCard);
				CMBManual.setCardTypeId(UserCardManual.CMB);
			}else{
				CMBManual.setIntegral(integral);
				CMBManual.setUserName(userName);
				CMBManual.setCardNo(cardNo);
				CMBManual.setLoginAccount(identityCard);
				CMBManual.setGmtModify(now);
			}
			
			userCardManualService.insertORupdate(CMBManual);
			

			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(identityCard);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
//			manualLogin.setPassword(password);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			manualLogin.setType(ManualLogin.TYPE_IDENTITY_CARD);
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setPassword(password);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);

			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录招行失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CMB_LOGIN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 获取南航验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getCsAirImageCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2015年11月19日 下午4:36:12  
	 * @author xiongbin
	 */
	public String getCsAirImageCode(Long userid){
		return manualQueryCsAir.getImageCode(userid);
	}
	
	/**
	 * 登录南航
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginCsAir 
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param loginAccount		登录名
	 * @param password			密码
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2015年11月19日 下午4:38:21  
	 * @author xiongbin
	 */
	@Deprecated
	public String loginCsAir(Long userId,Long manualId,String loginAccount,String password,String imageCode){
		try {
			String result = manualQueryCsAir.login(userId,loginAccount,password,imageCode);
			
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				return result;
			}
			
			JSONObject json = JSON.parseObject(result);
			String jsonString = json.getString("data");
			
			AssertUtil.hasLength(jsonString, "登录南航失败");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
//			{"code":0,"data":"{\"integral\":\"1,255公里\",\"name\":\"曹君跃\",\"card\":\"卡号 230002631681\"}","message":"0"}
			
			Integer integral = jsonObject.getInteger("integral");
			String userName = jsonObject.getString("name");
			String cardNo = jsonObject.getString("card");
			
			Date now = new Date();
			
			/**
			 * 记录用户积分数据 
			 */
			UserCardManual csAirManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CSAIR_INTEGRAL,cardNo,null,null);
			
			if(null == csAirManual){
				csAirManual = new UserCardManual();
				csAirManual.setIntegral(integral);
				csAirManual.setUserName(userName);
				csAirManual.setCardNo(cardNo);
				csAirManual.setGmtModify(now);
				csAirManual.setGmtCreate(now);
				csAirManual.setUserId(userId);
				csAirManual.setLoginAccount(loginAccount);
				csAirManual.setCardTypeId(UserCardManual.CSAIR_INTEGRAL);
			}else{
				csAirManual.setIntegral(integral);
				csAirManual.setUserName(userName);
				csAirManual.setCardNo(cardNo);
				csAirManual.setLoginAccount(loginAccount);
				csAirManual.setGmtModify(now);
			}
			
			userCardManualService.insertORupdate(csAirManual);
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
//			manualLogin.setPassword(password);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			
			if(StrUtil.isPhone(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_PHONE);
			}else if(StrUtil.isEmail(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_EMAIL);
			}else if(IdCardValidator.valideIdCard(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_IDENTITY_CARD);
			}else if(loginAccount.length()==12 && StringUtils.isNumeric(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_CARD_NO);
			}else{
				manualLogin.setType(ManualLogin.TYPE_LOGIN_ACCOUNT);
			}
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setPassword(password);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);

			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录南航失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CSAIR_LOGIN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 获取淘宝图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getTaobaoImageCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2015年11月19日 下午6:03:17  
	 * @author xiongbin
	 */
	public String getTaobaoImageCode(Long userid){
		return manualQueryTaobao.getImageCode(userid);
	}
	
	/**
	 * 获取物美图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getCMCCImageCode 
	 * @param userid	用户ID
	 * @return
	 * @date 2015年11月20日 下午12:17:42  
	 * @author xiongbin
	 */
	public String getWuMeiImageCode(Long userid){
		return manualQueryWuMei.getImageCode(userid);
	}
	
	/**
	 * 登录淘宝
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginTaobaoImageCode 
	 * @param userid				用户ID
	 * @param loginAccount			登录名
	 * @param password				密码
	 * @param imageCode				图片验证码
	 * @return
	 * @date 2015年11月19日 下午6:23:21  
	 * @author xiongbin
	 */
	@Deprecated
	public String loginTaobao(Long userid,Long manualId,String loginAccount,String password,String imageCode,boolean isImageCode){
		try {
			JSONObject resultJson = new JSONObject();
			
			String result;
			
			if(!isImageCode){
				//自动识别图片验证码并登录
				result = crackImageLogin(manualQueryTaobao,userid,manualId,loginAccount,password,2);
			}else{
				result = manualQueryTaobao.login(userid, loginAccount, password, imageCode);
			}
			
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				boolean flagPhone = JsonResult.checkResult(result,ApiResultCode.FOLLOWUP_VERIFY_PHONE);
				if(!flagPhone){
					return result;
				}

				String loginAccountKey = String.format(RedisKey.TAOBAO_MANUAL_LOGINACCOUNT_USER, userid);
				redis.set(loginAccountKey, loginAccount);

				String passwordKey = String.format(RedisKey.TAOBAO_MANUAL_PASSWORD_USER, userid);
				redis.set(passwordKey, password);
				
				return result;
			}
			
			saveTaobaoDate(userid,manualId,loginAccount,password,result);
			
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录淘宝失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.TAOBAO_LOGIN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 验证淘宝短信验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkTaobaoPhoneCode 
	 * @param userid		用户ID
	 * @param phoneCode		短信验证码
	 * @return
	 * @date 2015年11月19日 下午9:11:08  
	 * @author xiongbin
	 */
	public String checkTaobaoPhoneCode(Long userid,Long manualId,String phoneCode){
		try {
			String result = manualQueryTaobao.checkPhoneCode(userid, phoneCode);
			
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				return result;
			}
			
			String loginAccountKey = String.format(RedisKey.TAOBAO_MANUAL_LOGINACCOUNT_USER, userid);
			String loginAccount = redis.getStringByKey(loginAccountKey);

			String passwordKey = String.format(RedisKey.TAOBAO_MANUAL_PASSWORD_USER, userid);
			String password = redis.getStringByKey(passwordKey);
			
			saveTaobaoDate(userid,manualId,loginAccount,password,result);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("验证淘宝短信验证码失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.TAOBAO_CHECK_PHONECODE_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 保存淘宝数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveTaobaoDate 
	 * @param result
	 * @param userid
	 * @date 2015年11月19日 下午9:13:11  
	 * @author xiongbin
	 */
	private void saveTaobaoDate(Long userid,Long manualId,String loginAccount,String password,String result){
		manualQueryTaobao.saveTaobaoDate(userid, manualId, loginAccount, password, result);
	}
	
	/**
	 * 获取铂涛会图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getCMCCImageCode 
	 * @param userid	用户ID
	 * @return
	 * @date 2015年11月20日 下午12:17:42  
	 * @author xiongbin
	 */
	public String getBoTaoHuiImageCode(Long userid){
		return manualQueryBoTaoHui.getImageCode(userid);
	}
	
	/**
	 * 获取移动图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getCMCCImageCode 
	 * @param userid	用户ID
	 * @return
	 * @date 2015年11月20日 下午12:17:42  
	 * @author xiongbin
	 */
	public String getCMCCImageCode(Long userid){
		return manualQueryCMCC.getImageCode(userid);
	}
	/**
	 * 获取铂涛会短信验证
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getBoTaoHuiPhoneCode 
	 * @param userid		用户ID
	 * @param phone			手机号码
	 * @return
	 * @date 2015年11月20日 下午12:25:19  
	 * @author chenhl
	 */
	@Deprecated
	public String getBoTaoHuiPhoneCode(Long userid,String phone,String imageCode){
		try {
			String reslut = manualQueryBoTaoHui.getPhoneCode(userid, phone,imageCode);
			
			Boolean flag = JsonResult.checkResult(reslut,1051);
			if(!flag){
				return reslut;
			}
			
			//将铂涛会的登录账号码放入redis
			String key = String.format(RedisKey.BOTAOHUI_MANUAL_USER, userid);
			redis.set(key, phone);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.FOLLOWUP_VERIFY_PHONE);
			resultJson.put("message", "验证成功,有短信验证");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("获取铂涛会短信验证码失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.BOTAOHUI_GET_PHONE_CODE_ERROR,e.getMessage());
		}
	}
	/**
	 * 获取移动短信验证
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getCMCCPhoneCode 
	 * @param userid		用户ID
	 * @param phone			手机号码
	 * @return
	 * @date 2015年11月20日 下午12:25:19  
	 * @author xiongbin
	 */
	@Deprecated
	public String getCMCCPhoneCode(Long userid,String phone,String imageCode){
		try {
			String reslut = manualQueryCMCC.getPhoneCode(userid, phone,imageCode);
			
			Boolean flag = JsonResult.checkResult(reslut,ApiResultCode.CMCC_SEND_PHONECODE_SUCCEED);
			if(!flag){
				return reslut;
			}
			
			//将移动的图片验证码放入redis
			String key = String.format(RedisKey.CMCC_MANUAL_USER, userid);
			redis.set(key, imageCode);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.FOLLOWUP_VERIFY_PHONE);
			resultJson.put("message", "验证成功,有短信验证");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("获取移动短信验证码失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CMCC_GET_PHONECODE_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 铂涛会移动
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginBoTaoHui 
	 * @param userid
	 * @param phoneCode
	 * @return
	 * @date 2015年11月20日 下午2:28:33  
	 * @author chenhl
	 */
	public String loginBoTaoHui(Long userId,Long manualId,String phoneCode, String version){
		try {
			String result = manualQueryBoTaoHui.login(userId, phoneCode);
			
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				return result;
			}
			
//			{
//			    "code": 0,
//			    "message": "{\"name\":\"财图\",\"point\":\"0\"}"
//			}
			
			JSONObject json = JSON.parseObject(result);
			String jsonString = json.getString("message");
			
			AssertUtil.hasLength(jsonString, "登录铂涛会失败");
			
			String key = String.format(RedisKey.BOTAOHUI_MANUAL_USER, userId);
			String loginAccount = redis.getStringByKey(key);
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
			Integer integral = jsonObject.getInteger("point");
			String userName = jsonObject.getString("name");
			
			Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			
			/**
			 * 记录用户积分数据 
			 */
			UserCardManual boTaoHuiManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.BOTAOHUI_INTEGRAL,null,userName,loginAccount);
			
			if(null == boTaoHuiManual){
				presentTubiService.presentTubiDo(userId, UserCardManual.BOTAOHUI_INTEGRAL, 0, integral, resData, version);
				
				boTaoHuiManual = new UserCardManual();
				boTaoHuiManual.setIntegral(integral);
				boTaoHuiManual.setUserName(userName);
				boTaoHuiManual.setGmtModify(now);
				boTaoHuiManual.setGmtCreate(now);
				boTaoHuiManual.setUserId(userId);
				boTaoHuiManual.setLoginAccount(loginAccount);
				boTaoHuiManual.setCardTypeId(UserCardManual.BOTAOHUI_INTEGRAL);
			}else{
				presentTubiService.presentTubiDo(userId, UserCardManual.BOTAOHUI_INTEGRAL, boTaoHuiManual.getIntegral(), integral, resData, version);
				
				boTaoHuiManual.setIntegral(integral);
				boTaoHuiManual.setUserName(userName);
				boTaoHuiManual.setLoginAccount(loginAccount);
				boTaoHuiManual.setGmtModify(now);
				boTaoHuiManual.setStatus(1);
			}
			
			userCardManualService.insertORupdate(boTaoHuiManual);
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_NO);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			manualLogin.setType(ManualLogin.TYPE_PHONE);
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setStatus(1);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.SUCCEED);
			resultJson.put("message", "登录成功");
			resultJson.put("data", resData);
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录铂涛会失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.BOTAOHUI_LOGIN_ERROR,e.getMessage());
		}
	}
	/**
	 * 登录移动
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginCMCC 
	 * @param userid
	 * @param phoneCode
	 * @return
	 * @date 2015年11月20日 下午2:28:33  
	 * @author xiongbin
	 */
	@Deprecated
	public String loginCMCC(Long userId,Long manualId,String password,String phone,String imageCode){


		try {
//			String key = String.format(RedisKey.CMCC_MANUAL_USER, userId);
//			String imageCode = redis.getStringByKey(key);

			String result = manualQueryCMCC.login(userId, password, imageCode, "service", phone);
			
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				return result;
			}
			
//			{
//			    "code": 0,
//			    "data": "{\"data\":{\"remark\":null,\"brandCode\":\"02\",\"pointValue\":\"467\",\"pointItems\":[{\"pointName\":\"消费积分\",\"pointValue\":\"59\"},{\"pointName\":\"消费积分\",\"pointValue\":\"59\"},{\"pointName\":\"消费积分\",\"pointValue\":\"59\"},{\"pointName\":\"消费积分\",\"pointValue\":\"59\"},{\"pointName\":\"消费积分\",\"pointValue\":\"99\"},{\"pointName\":\"消费积分\",\"pointValue\":\"97\"},{\"pointName\":\"消费积分\",\"pointValue\":\"35\"}]},\"retCode\":\"000000\",\"retMsg\":\"业务异常\"}",
//			    "message": "抓取成功"
//			}
			
			JSONObject json = JSON.parseObject(result);
			String jsonString = json.getString("data");
			
			AssertUtil.hasLength(jsonString, "登录移动失败");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
			Integer integral = jsonObject.getInteger("integral");
			String userName = jsonObject.getString("account");
			
			Date now = new Date();
			
			/**
			 * 记录用户积分数据 
			 */
			UserCardManual csAirManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CMCC_INTEGRAL,null,null,userName);
			
			if(null == csAirManual){
				csAirManual = new UserCardManual();
				csAirManual.setIntegral(integral);
				csAirManual.setUserName(userName);
				csAirManual.setGmtModify(now);
				csAirManual.setGmtCreate(now);
				csAirManual.setUserId(userId);
				csAirManual.setLoginAccount(userName);
				csAirManual.setCardTypeId(UserCardManual.CMCC_INTEGRAL);
			}else{
				csAirManual.setIntegral(integral);
				csAirManual.setUserName(userName);
				csAirManual.setLoginAccount(userName);
				csAirManual.setGmtModify(now);
				csAirManual.setStatus(1);
			}
			
			userCardManualService.insertORupdate(csAirManual);
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(userName);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			manualLogin.setPassword(password);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			manualLogin.setType(ManualLogin.TYPE_PHONE);
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setStatus(1);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.SUCCEED);
			resultJson.put("message", "登录成功");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录移动失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CMCC_LOGIN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 从redis中删除用户登录记录key
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: delUserRecordRedisKey 
	 * @param userId
	 * @param manualId
	 * @date 2015年11月19日 上午11:40:20  
	 * @author xiongbin
	 */
	private void delUserRecordRedisKey(Long userId,Long manualId){
		//删除用户登录记录key
		String userRecordKey = String.format(RedisKey.INTEGRAL_MANUAL_USER_LOGIN_RECORD_1,userId,manualId);
		String userRecordKey2 = String.format(RedisKey.INTEGRAL_MANUAL_USER_LOGIN_RECORD_2,userId,manualId);
		String isImageKey = String.format(RedisKey.MANUAL_QUERY_ISIMAGE_BY_USERID_MANUALID,userId,manualId);
		redis.del(userRecordKey);
		redis.del(userRecordKey2);
		redis.del(isImageKey);
	}
	
	public static void main(String[] args) throws Exception {
//		String str = "{\"usableTime\":\"2015年12月31日\",\"amountFree\":0}";
//		
//		JSONObject json = JSON.parseObject(str);
//		
//		System.out.println(sdf.format(new Date()));
//		System.out.println(sdf.parseObject("2015年12月31日"));
		
//		System.out.println(json.getTimestamp("usableTime"));
	}


	/**
	 * 物美登录	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: wumeiLogin 
	 * @param userId
	 * @param phone
	 * @param imageCode
	 * @param password
	 * @return
	 * @date 2015年12月12日 上午10:30:58  
	 * @author ws
	 * @param manualId 
	 * @param provinceType 
	*/
	public String wumeiLogin(Long userId, Long manualId, String phone, String imageCode,String password, String provinceType,boolean isImageCode, String version) {
		try {
			String result;
			
			if(!isImageCode){
				//自动识别图片验证码并登录
				imageCode = isCrackImageCode(userId,manualQueryWuMei);
				if(imageCode.startsWith("{")){
					boolean flag = JsonResult.checkResult(imageCode,ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR);
					if(flag){
						return imageCode;
					}
				}
			}
			
			result = manualQueryWuMei.login(userId, phone, password,imageCode,provinceType);
			
			Boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				return result;
			}
			
			Map<String,Object> resData = saveWuMeiData(userId,manualId,phone,password,result, version);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.SUCCEED);
			resultJson.put("message", "登录成功");
			resultJson.put("data", resData);
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录物美失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,e.getMessage());
		}
	}
	/**
	 * 保存物美信息
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveWuMeiData 
	 * @param userId
	 * @param manualId
	 * @param phone
	 * @param password
	 * @param result
	 * @date 2015年12月12日 上午11:49:51  
	 * @author ws
	 */
	private Map<String,Object> saveWuMeiData(Long userId,Long manualId, String phone,String password,String result, String version){
		JSONObject json = JSON.parseObject(result);
		String jsonString = json.getString("data");
		
		AssertUtil.hasLength(jsonString, "登录物美失败");
		
		JSONObject jsonObject = JSON.parseObject(jsonString);
		
		Integer integral = jsonObject.getInteger("interal");
		String userName = phone;
		
		Date now = new Date();
		Date expirationTime = TimeUtil.getYearLast();

		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		resData.put("manualId", String.valueOf(manualId));
		resData.put("loginAccount", phone);
		
		
		/**
		 * 记录用户积分数据 
		 */
		UserCardManual wumeiManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.WUMEI_INTEGRAL,null,null,userName);
		
		if(null == wumeiManual){
			presentTubiService.presentTubiDo(userId, UserCardManual.WUMEI_INTEGRAL, 0, integral, resData, version);
			
			wumeiManual = new UserCardManual();
			wumeiManual.setIntegral(integral);
			wumeiManual.setExpirationIntegral(integral);
			wumeiManual.setUserName(userName.substring(userName.length()-4,userName.length()));
			wumeiManual.setExpirationTime(expirationTime);
			wumeiManual.setGmtModify(now);
			wumeiManual.setGmtCreate(now);
			wumeiManual.setUserId(userId);
			wumeiManual.setLoginAccount(userName);
			wumeiManual.setCardTypeId(UserCardManual.WUMEI_INTEGRAL);
		}else{
			presentTubiService.presentTubiDo(userId, UserCardManual.WUMEI_INTEGRAL, wumeiManual.getIntegral(), integral, resData, version);
			
			wumeiManual.setIntegral(integral);
			wumeiManual.setExpirationIntegral(integral);
			wumeiManual.setUserName(userName.substring(userName.length()-4,userName.length()));
			wumeiManual.setExpirationTime(expirationTime);
			wumeiManual.setLoginAccount(userName);
			wumeiManual.setGmtModify(now);
			wumeiManual.setStatus(1);
		}
		
		userCardManualService.insertORupdate(wumeiManual);
		
		Long userCardManualId = wumeiManual.getId();
		
		//过期积分,保存到子表
		if(null!=userCardManualId && integral>0){
			userCardManualItemService.deleteByUserCardManualId(userCardManualId);
			UserCardManualItem userCardManualItem = new UserCardManualItem();;
			userCardManualItem.setExpirationIntegral(integral);
			userCardManualItem.setExpirationTime(expirationTime);
			userCardManualItem.setUserCardManualId(userCardManualId);
			userCardManualItemService.insert(userCardManualItem);
		}
		
		/**
		 * 记录用户登录数据
		 */
		ManualLogin manualLogin = new ManualLogin();
		manualLogin.setLoginAccount(userName);
		manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
		manualLogin.setUserId(userId);
		manualLogin.setManualId(manualId);
		manualLogin.setType(ManualLogin.TYPE_PHONE);
		
		ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
		if(oldManualLogin == null){
			manualLogin.setPassword(password);
			manualLoginService.insert(manualLogin);
		}else{
			oldManualLogin.setPassword(password);
			oldManualLogin.setStatus(1);
			manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
		}
		
		//删除用户登录记录key
		delUserRecordRedisKey(userId, manualId);
		
		return resData;
	}


	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkWumeiImgCode 
	 * @param userId
	 * @param manualId
	 * @param imageCode
	 * @return
	 * @date 2015年12月12日 下午12:30:43  
	 * @author ws
	*/
	public String checkWumeiImgCode(Long userId, Long manualId, String imageCode) {
		try {
			String result = manualQueryWuMei.checkImgCode(userId, imageCode);
			
			Boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				
				return result;
			}
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.WUMEI_PHONECODE_INPUT);
			resultJson.put("message", "请输入短信验证码");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("物美图片验证码验证失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,e.getMessage());
		}
	}


	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkWumeiPhoneCode 
	 * @param userId
	 * @param manualId
	 * @param phoneCode
	 * @return
	 * @date 2015年12月12日 下午2:07:02  
	 * @author ws
	*/
	public String checkWumeiPhoneCode(Long userId, Long manualId,
			String phoneCode, String version) {
		try {
			String result = manualQueryWuMei.checkPhoneCode(userId, phoneCode);
			
			Boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				
				return result;
			}
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.WUMEI_PWD_MOD_SUCCESS);
			resultJson.put("message", "密码修改成功");
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("物美短信验证失败：" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,e.getMessage());
		}
	}


	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: wumeiLoginSecond 
	 * @param userId
	 * @param manualId
	 * @param imageCode
	 * @return
	 * @date 2015年12月12日 下午5:54:20  
	 * @author ws
	*/
	public String wumeiLoginSecond(Long userId, Long manualId, String imageCode, String version) {
		try {
			String result = manualQueryWuMei.loginSecond(userId, imageCode);
			
			Boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				
				return result;
			}
			JSONObject json = JSON.parseObject(result);
			String data = json.getString("data");
			
			JSONObject dataJson = JSON.parseObject(data);
			String phone = dataJson.getString("account");
			String password = dataJson.getString("password");
			
			Map<String,Object> resData = saveWuMeiData(userId,manualId,phone,password,result, version);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.SUCCEED);
			resultJson.put("message", "登录成功");
			resultJson.put("data", resData);
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("物美登录失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,e.getMessage());
		}
	}
	
	/**
	 * 破解验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: crackImageCode 
	 * @param imageCode
	 * @return
	 * @date 2016年1月8日 下午8:50:37  
	 * @author xiongbin
	 */
	private String crackImageCode(String imageCode) {
		imageCode = analysisImageCode(imageCode);
		if(null == imageCode){
			return null;
		}
		
		Integer count = 1;
		String code = null;

		do {
			code = CommonImgCodeApi.recognizeImgCodeFromStr(imageCode);
			if (code != null) {
				break;
			}
			count++;
		} while (count < 2);

		return code;
	}
	
	/**
	 * 解析图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: analysisImageCode 
	 * @param reslut
	 * @return
	 * @date 2016年1月8日 下午8:49:23  
	 * @author xiongbin
	 */
	private String analysisImageCode(String reslut){
		if(StringUtils.isBlank(reslut)){
			return null;
		}
		
		JSONObject json = JSON.parseObject(reslut);
		Integer code = json.getInteger("code");
		if(code.equals(0)){
			return json.getString("data");
		}else{
			return null;
		}
	}
	
	/**
	 * 自动识别图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: isCrackImageCode 
	 * @param userid
	 * @param manualQuery
	 * @return
	 * @date 2016年1月8日 下午9:27:48  
	 * @author xiongbin
	 */
	private String isCrackImageCode(Long userid,ManualQueryAbstract manualQuery){
		String reslut = manualQuery.getImageCode(userid);
		String imageCode = crackImageCode(reslut);
		
		if(null == imageCode){
			reslut = manualQuery.getImageCode(userid);
			boolean flag = JsonResult.checkResult(reslut);
			if(flag){
				imageCode = JsonResult.getResult(reslut, "data");
				JSONObject reslutJson = new JSONObject();
				reslutJson.put("code", ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR);
				reslutJson.put("imagecode", imageCode);
				reslutJson.put("message", "自动识别图片验证码失败");
				logger.warn("自动识别图片验证码失败");
				return reslutJson.toJSONString();
			}
			return reslut;
		}else{
			return imageCode;
		}
	}
	
	/**
	 * 自动识别图片验证码并登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: crackImageLogin 
	 * @param manualQuery		查询类
	 * @param userid			用户ID
	 * @param loginAccount		登录名
	 * @param password			密码
	 * @param count				重试次数
	 * @return
	 * @date 2016年1月11日 上午11:37:15  
	 * @author xiongbin
	 */
	private String crackImageLogin(ManualQueryAbstract manualQuery,Long userid,Long manualId,String loginAccount,String password,Integer count){
		String reslut = isCrackImageCode(userid,manualQuery);
		
		boolean flag = false;
		
		if(reslut.startsWith("{")){
			flag = JsonResult.checkResult(reslut,ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR);
			if(flag){
				return reslut;
			}else if(JsonResult.checkResult(reslut,ApiResultCode.IMG_GET_ERROR)){
				return reslut;
			}else{
				return reslut;
			}
		}
		
		reslut = manualQuery.login(userid, loginAccount, password, reslut);
		
		flag = JsonResult.checkResult(reslut,ApiResultCode.IMAGECODE_ERROR);
		if(flag){
			count--;
			if(count>0){
				return crackImageLogin(manualQuery,userid,manualId,loginAccount,password,count);
			}
		}
		
		return reslut;
	}
	
	/**
	 * 查询用户是否需要验证码登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: isImageCode 
	 * @param userid		用户ID
	 * @param manualId		积分账户ID
	 * @return
	 * @date 2016年1月11日 下午12:06:09  
	 * @author xiongbin
	 */
	public boolean isImageCode(Long userid,Long manualId){
		boolean isImage = true;;
		String key = String.format(RedisKey.MANUAL_QUERY_ISIMAGE_BY_USERID_MANUALID,userid,manualId);
		String isImageCode = redis.getStringByKey(key);
		if(StringUtils.isBlank(isImageCode)){
			isImage = false;
		}
		
		return isImage;
	}
	
	/**
	 * 设置用户需要验证码登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: setIsImageCode 
	 * @param userid		用户ID
	 * @param manualId		积分账户ID
	 * @date 2016年1月11日 下午12:14:16  
	 * @author xiongbin
	 */
	public void setIsImageCode(Long userid,Long manualId){
		String key = String.format(RedisKey.MANUAL_QUERY_ISIMAGE_BY_USERID_MANUALID,userid,manualId);
		redis.set(key, "true", 1000*60*10);
	}

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginPuFaBank 
	 * @param userId
	 * @param manualId
	 * @param phoneCode
	 * @return
	 * @date 2016年4月6日 下午3:13:36  
	 * @author ws
	*/
	public String loginPuFaBank(Long userId, Long manualId, String phoneCode, String version) {
		try {
			String result = manualQueryPuFaBank.login(userId, phoneCode);
			
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				JSONObject json = JSON.parseObject(result);
				Integer code = json.getInteger("code");
				if(code.intValue() == 3006){
					return result;
				}else{
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, "支付失败，浦发银行系统维护中，请稍后再试");
				}
			}

			JSONObject json = JSON.parseObject(result);
			String jsonString = json.getString("data");
			
			AssertUtil.hasLength(jsonString, "支付失败，浦发银行系统维护中，请稍后再试");
			
			String key = String.format(RedisKey.PUFABANK_MANUAL_USER, userId);
			String loginParam = redis.getStringByKey(key);
			JSONObject jsonObj = JSON.parseObject(loginParam);
			String loginAccount = jsonObj.getString("loginAccount");
			String password = jsonObj.getString("password");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
			Integer integral = jsonObject.getInteger("point");
			String userName = jsonObject.getString("name");
			String cardNo = jsonObject.getString("cardNo");
			if(cardNo.length() > 4){
				cardNo = cardNo.substring(cardNo.length() - 4);
			}
			
			Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			/**
			 * 记录用户积分数据 
			 */
			UserCardManual puFaBankManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.PUFA_INTEGRAL,cardNo,userName,loginAccount);
			
			if(null == puFaBankManual){
				presentTubiService.presentTubiDo(userId, UserCardManual.PUFA_INTEGRAL, 0, integral, resData, version);
				
				puFaBankManual = new UserCardManual();
				puFaBankManual.setIntegral(integral);
				puFaBankManual.setUserName(userName);
				puFaBankManual.setGmtModify(now);
				puFaBankManual.setGmtCreate(now);
				puFaBankManual.setUserId(userId);
				puFaBankManual.setLoginAccount(loginAccount);
				puFaBankManual.setCardNo(cardNo);
				puFaBankManual.setCardTypeId(UserCardManual.PUFA_INTEGRAL);
			}else{
				presentTubiService.presentTubiDo(userId, UserCardManual.PUFA_INTEGRAL, puFaBankManual.getIntegral(), integral, resData, version);
				
				puFaBankManual.setIntegral(integral);
				puFaBankManual.setUserName(userName);
				puFaBankManual.setLoginAccount(loginAccount);
				puFaBankManual.setGmtModify(now);
				puFaBankManual.setStatus(1);
				puFaBankManual.setCardNo(cardNo);
			}
			
			userCardManualService.insertORupdate(puFaBankManual);
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			manualLogin.setType(ManualLogin.TYPE_IDENTITY_CARD);
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLogin.setStatus(1);
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setPassword(password);
				oldManualLogin.setStatus(1);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.SUCCEED);
			resultJson.put("message", "登录成功");
			resultJson.put("data", resData);
			
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.warn(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.warn("登录浦发银行失败:{}",e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,"支付失败，浦发银行系统维护中，请稍后再试");
		}
	}


}