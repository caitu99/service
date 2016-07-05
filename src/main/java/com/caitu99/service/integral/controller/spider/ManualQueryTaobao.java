package com.caitu99.service.integral.controller.spider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.domain.UserCardManualItem;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;
import com.caitu99.service.utils.string.IdCardValidator;
import com.caitu99.service.utils.string.StrUtil;

@Component
@Deprecated
public class ManualQueryTaobao extends ManualQueryAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryTaobao.class);
	
	/**
	 * 获取图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年1月11日 上午10:28:56  
	 * @author xiongbin
	 */
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/taobao/imgcode/1.0";
		super.setUrl(url);
		super.setName("淘宝");
		super.setSucceedCode(ApiResultCode.SUCCEED);
		super.setFailureCode(ApiResultCode.TAOBAO_GET_IMAGECODE_ERROR);
		
		return super.getImageCode(userid);
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid			用户ID
	 * @param loginAccount		登录名
	 * @param password			密码
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2016年1月11日 上午10:28:56  
	 * @author xiongbin
	 */
	@Override
	public String login(Long userid,String loginAccount,String password,String imageCode){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/taobao/login/1.0";
		super.setName("淘宝");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userid.toString());
		param.put("account", loginAccount);
		param.put("password", password);
		param.put("yzm", imageCode);
		return super.login(userid, url, param, ApiResultCode.SUCCEED);
	}

	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String reslut, String version) {
		try {
			JSONObject resultJson = new JSONObject();
			
			boolean flag = JsonResult.checkResult(reslut,ApiResultCode.SUCCEED);
			if(!flag){
				boolean flagPhone = JsonResult.checkResult(reslut,ApiResultCode.FOLLOWUP_VERIFY_PHONE);
				if(!flagPhone){
					return reslut;
				}

				String loginAccountKey = String.format(RedisKey.TAOBAO_MANUAL_LOGINACCOUNT_USER, userId);
				redis.set(loginAccountKey, loginAccount);

				String passwordKey = String.format(RedisKey.TAOBAO_MANUAL_PASSWORD_USER, userId);
				redis.set(passwordKey, password);
				
				return reslut;
			}
			
			saveTaobaoDate(userId,manualId,loginAccount,password,reslut);
			
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			
			return resultJson.toJSONString();
		} catch (Exception e) {
			logger.error("登录淘宝失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.TAOBAO_LOGIN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 验证手机验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid			用户ID
	 * @param phoneCode			短信验证码
	 * @return
	 * @date 2015年11月19日 下午5:59:02  
	 * @author xiongbin
	 */
	public String checkPhoneCode(Long userid,String phoneCode){
		try {
			AssertUtil.notNull(userid, "用户ID不能为空");
			AssertUtil.hasLength(phoneCode, "短信验证码不能为空");
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/taobao/vcode/1.0";

			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("yzm", phoneCode);
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("验证淘宝短信验证码返回数据:" + jsonString);
			
			boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.SUCCEED);
			if(!flag){
				JSONObject json = JSON.parseObject(jsonString);
				Integer code = json.getInteger("code");
				
				if(code.equals(1044)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(1045)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
				}else if(code.equals(1013)){
					return ApiResult.outSucceed(ApiResultCode.SEND_CODE_ERROR, "短信验证码发送失败.消息通道忙，请15分钟后再试");
				}else if(code.equals(1051)){
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.FOLLOWUP_VERIFY_PHONE);
					resultJson.put("message", "验证成功,有短信验证");
					return resultJson.toJSONString();
				}else if(code.equals(1052)){
					return ApiResult.outSucceed(ApiResultCode.PHONE_CODE_ERROR, "短信验证码不正确 ");
				}
			}
			
			return jsonString;
		} catch (Exception e) {
			logger.error("登录淘宝失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.TAOBAO_CHECK_PHONECODE_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 保存数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveTaobaoDate 
	 * @param userid
	 * @param manualId
	 * @param loginAccount
	 * @param password
	 * @param result
	 * @date 2016年1月12日 上午10:56:18  
	 * @author xiongbin
	 */
	public void saveTaobaoDate(Long userid,Long manualId,String loginAccount,String password,String result){
		try {
			JSONObject reslutData = JSON.parseObject(result);
			String jsonString = reslutData.getString("data");
			JSONObject json = JSON.parseObject(jsonString);
			
			String taojinbiJsonString = json.getString("taojinbi");
			String taolichenJsonString = json.getString("taolichen");
			String tmallJsonString = json.getString("tmallInteger");
			
//		String expiredTaojinbiJsonString = json.getString("expiredTaojinbi");
			
//		AssertUtil.hasLength(taojinbiJsonString, "登录淘宝失败");
//		AssertUtil.hasLength(taolichenJsonString, "登录淘宝失败");
//		AssertUtil.hasLength(tmallJsonString, "登录淘宝失败");
//		AssertUtil.hasLength(expiredTaojinbiJsonString, "登录淘宝失败");
			
//		{
//		    "taojinbi": "{\"expiredCoin\":0,\"expireDate\":\"2015年12月31日\",\"coin\":429,\"discountPrice\":4.29,\"canGetCoin\":5,\"level\":4,\"userId\":313112074,\"nick\":\"aliveu7\",\"isLogin\":true,\"isSuccess\":true}",
//		    "code": 0,
//		    "taolichen": "{\"code\":\"U001\",\"msg\":\"\\u7528\\u6237\\u5df2\\u7ecf\\u6fc0\\u6d3b\",\"validPoint\":222,\"inValidPoint\":214,\"isLogin\":true,\"userId\":313112074,\"snick\":\"aliveu7\",\"userLevel\":1}",
//		    "tmallInteger": {
//		        "toExpiredPoint": 312,
//		        "availablePoint": 7345,
//		        "toExpiredDate": "2015年12月31日"
//		    },
//		    "expiredTaojinbi": "{\"usableTime\":\"2015年12月31日\",\"amountFree\":0}",
//		    "message": "登录成功"
//		}
			
			Date now = new Date();
			
			//淘宝
			Integer taobaoIntegral = 0;
			String taobaoUserName = "";
			Integer taobaoExpirationIntegral = 0;
			String taobaoExpirationTimeStr = "";
			Date taobaoExpirationTime = null;
			
			
			//淘里程
			Integer taolichengIntegral = 0;
			
			//天猫
			Integer tmallIntegral = 0;
			Integer tmallExpirationIntegral = 0;
			String tmallExpirationTimeStr = "";
			Date tmallExpirationTime = null;
			
			
			if(taojinbiJsonString!=null && !"".equals(taojinbiJsonString.trim())){
				JSONObject taojinbiJsonObject = JSON.parseObject(taojinbiJsonString);
				taobaoIntegral = taojinbiJsonObject.getInteger("coin");
				taobaoUserName = taojinbiJsonObject.getString("nick");
				taobaoExpirationIntegral = taojinbiJsonObject.getInteger("expiredCoin");
				taobaoExpirationTimeStr = taojinbiJsonObject.getString("expireDate");
			}
			if(taolichenJsonString!=null && !"".equals(taolichenJsonString.trim())){
				JSONObject taolichenJsonObject = JSON.parseObject(taolichenJsonString);
				taolichengIntegral = taolichenJsonObject.getInteger("validPoint");
				
			}
			if(tmallJsonString!=null && !"".equals(tmallJsonString.trim())){
				JSONObject tmallJsonObject = JSON.parseObject(tmallJsonString);
				tmallIntegral = tmallJsonObject.getInteger("availablePoint");
				tmallExpirationIntegral = tmallJsonObject.getInteger("toExpiredPoint");
				tmallExpirationTimeStr = tmallJsonObject.getString("toExpiredDate");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			try {
				if(!"".equals(taobaoExpirationTimeStr)){
					taobaoExpirationTime = sdf.parse(taobaoExpirationTimeStr);
				}
			} catch (Exception e) {
				logger.error("转换日期出错");
			}
			
			try {
				if(!"".equals(tmallExpirationTimeStr)){
					tmallExpirationTime = sdf.parse(tmallExpirationTimeStr);
				}
			} catch (Exception e) {
				logger.error("转换日期出错");
			}
			
			/**
			 * 记录用户积分数据 
			 */
			//淘宝
			UserCardManual taobaoManual = userCardManualService.getUserCardManualSelective(userid,UserCardManual.TAOBAO_INTEGRAL,null,taobaoUserName,null);
			
			if(null == taobaoManual){
				taobaoManual = new UserCardManual();
				taobaoManual.setIntegral(taobaoIntegral);
				taobaoManual.setUserName(taobaoUserName);
				taobaoManual.setGmtModify(now);
				taobaoManual.setGmtCreate(now);
				taobaoManual.setUserId(userid);
				taobaoManual.setLoginAccount(loginAccount);
				taobaoManual.setExpirationIntegral(taobaoExpirationIntegral);
				taobaoManual.setExpirationTime(taobaoExpirationTime);
				taobaoManual.setCardTypeId(UserCardManual.TAOBAO_INTEGRAL);
			}else{
				taobaoManual.setIntegral(taobaoIntegral);
				taobaoManual.setUserName(taobaoUserName);
				taobaoManual.setLoginAccount(loginAccount);
				taobaoManual.setExpirationIntegral(taobaoExpirationIntegral);
				taobaoManual.setExpirationTime(taobaoExpirationTime);
				taobaoManual.setGmtModify(now);
				taobaoManual.setStatus(1);
			}
			
			//天猫
			UserCardManual tamllManual = userCardManualService.getUserCardManualSelective(userid,UserCardManual.TMALL_INTEGRAL,null,taobaoUserName,null);
			
			if(null == tamllManual){
				tamllManual = new UserCardManual();
				tamllManual.setIntegral(tmallIntegral);
				tamllManual.setUserName(taobaoUserName);
				tamllManual.setGmtModify(now);
				tamllManual.setGmtCreate(now);
				tamllManual.setUserId(userid);
				tamllManual.setLoginAccount(loginAccount);
				tamllManual.setExpirationIntegral(tmallExpirationIntegral);
				tamllManual.setExpirationTime(tmallExpirationTime);
				tamllManual.setCardTypeId(UserCardManual.TMALL_INTEGRAL);
			}else{
				tamllManual.setIntegral(tmallIntegral);
				tamllManual.setUserName(taobaoUserName);
				tamllManual.setLoginAccount(loginAccount);
				tamllManual.setExpirationIntegral(tmallExpirationIntegral);
				tamllManual.setExpirationTime(tmallExpirationTime);
				tamllManual.setGmtModify(now);
				tamllManual.setStatus(1);
			}
			
			//淘里程
			UserCardManual taolichengManual = userCardManualService.getUserCardManualSelective(userid,UserCardManual.TAOLICHENG_INTEGRAL,null,taobaoUserName,null);
			
			if(null == taolichengManual){
				taolichengManual = new UserCardManual();
				taolichengManual.setIntegral(taolichengIntegral);
				taolichengManual.setUserName(taobaoUserName);
				taolichengManual.setGmtModify(now);
				taolichengManual.setGmtCreate(now);
				taolichengManual.setUserId(userid);
				taolichengManual.setLoginAccount(loginAccount);
				taolichengManual.setCardTypeId(UserCardManual.TAOLICHENG_INTEGRAL);
			}else{
				taolichengManual.setIntegral(taolichengIntegral);
				taolichengManual.setUserName(taobaoUserName);
				taolichengManual.setLoginAccount(loginAccount);
				taolichengManual.setGmtModify(now);
				taolichengManual.setStatus(1);
			}
			
			userCardManualService.insertORupdate(taobaoManual);
			userCardManualService.insertORupdate(tamllManual);
			userCardManualService.insertORupdate(taolichengManual);
			
			Long userCardManualIdTaobao = taobaoManual.getId();
			Long userCardManualIdTamll = tamllManual.getId();
			
			//过期积分,保存到子表
			if(null!=userCardManualIdTaobao && taobaoExpirationIntegral>0){
				userCardManualItemService.deleteByUserCardManualId(userCardManualIdTaobao);
				UserCardManualItem userCardManualItem = new UserCardManualItem();;
				userCardManualItem.setExpirationIntegral(taobaoExpirationIntegral);
				userCardManualItem.setExpirationTime(taobaoExpirationTime);
				userCardManualItem.setUserCardManualId(userCardManualIdTaobao);
				userCardManualItemService.insert(userCardManualItem);
			}
			if(null!=userCardManualIdTamll && tmallExpirationIntegral>0){
				userCardManualItemService.deleteByUserCardManualId(userCardManualIdTamll);
				UserCardManualItem userCardManualItem = new UserCardManualItem();;
				userCardManualItem.setExpirationIntegral(tmallExpirationIntegral);
				userCardManualItem.setExpirationTime(tmallExpirationTime);
				userCardManualItem.setUserCardManualId(userCardManualIdTamll);
				userCardManualItemService.insert(userCardManualItem);
			}
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			manualLogin.setPassword(password);
			manualLogin.setUserId(userid);
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
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setStatus(1);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userid, manualId);
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录淘宝失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.TAOBAO_LOGIN_ERROR,e.getMessage());
		}
	}
}