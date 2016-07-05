package com.caitu99.service.integral.controller.spider.abs;

import java.util.HashMap;
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
import com.caitu99.service.integral.controller.service.PresentTubiService;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.ManualService;
import com.caitu99.service.integral.service.UserCardManualItemService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

@Component
public abstract class ManualQueryAbstract {

	private final static Logger logger = LoggerFactory.getLogger(ManualQueryAbstract.class);
	
	@Autowired
	protected RedisOperate redis;
	
	@Autowired
	protected ManualService manualService;
	
	@Autowired
	protected UserCardManualService userCardManualService;
	
	@Autowired
	protected ManualLoginService manualLoginService;
	
	@Autowired
	protected UserCardManualItemService userCardManualItemService;

	@Autowired
	protected PresentTubiService presentTubiService;
	
	//获取验证码url地址
	private String url;
	//平台名称
	private String name;
	//成功code
	private Integer succeedCode;
	//失败code
	private Integer failureCode;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSucceedCode() {
		return succeedCode;
	}

	public void setSucceedCode(Integer succeedCode) {
		this.succeedCode = succeedCode;
	}

	public Integer getFailureCode() {
		return failureCode;
	}

	public void setFailureCode(Integer failureCode) {
		this.failureCode = failureCode;
	}

	/**
	 * 获取验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userId			用户ID
	 * @return
	 * @date 2016年1月11日 上午10:19:48  
	 * @author xiongbin
	 */
	public String getImageCode(Long userId){
		ApiResult<String> result = new ApiResult<String>();
		
		if(null == userId){
			return result.toJSONString(-1, "参数userId不能为空");
		}else if(StringUtils.isBlank(url)){
			return result.toJSONString(-1, "参数url不能为空");
		}else if(StringUtils.isBlank(name)){
			return result.toJSONString(-1, "参数name不能为空");
		}
		
		try {
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info(name + "图片验证码返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,succeedCode);
			if(!flag){
				return result.toJSONString(failureCode, "获取" + name + "图片验证码失败");
			}

			String imageCode = JsonResult.getResult(jsonString, "data");
			
			if(StringUtils.isBlank(imageCode)){
				return result.toJSONString(failureCode, "获取" + name + "图片验证码失败");
			}
			
			return result.toJSONString(0, "", imageCode);
		} catch (Exception e) {
			logger.info("获取" + name + "图片验证码失败:" + e.getMessage(),e);
			return result.toJSONString(ApiResultCode.IMG_GET_ERROR, name+"：系统维护中，请稍后再试");
		}
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userId		用户ID
	 * @param url			登录平台url
	 * @param param			登录平台参数
	 * @param succeedCode	登录平台成功code
	 * @return
	 * @date 2016年1月11日 下午5:37:20  
	 * @author xiongbin
	 */
	protected String login(Long userId,String url,Map<String,String> param,Integer succeedCode){
		ApiResult<String> result = new ApiResult<String>();
		
		if(null == userId){
			return result.toJSONString(-1, "参数userId不能为空");
		}else if(StringUtils.isBlank(url)){
			return result.toJSONString(-1, "参数url不能为空");
		}else if(null == param){
			return result.toJSONString(-1, "参数param不能为空");
		}else if(StringUtils.isBlank(name)){
			return result.toJSONString(-1, "参数name不能为空");
		}else if(null == succeedCode){
			return result.toJSONString(-1, "参数succeedCode不能为空");
		}
		
		try {
			String reslut = HttpClientUtils.getInstances().doPost(url, "UTF-8",param);
			logger.info("登录" + name + "返回数据:" + reslut);
			
			return verifyLoginReslut(reslut,userId,succeedCode,name);
			
		} catch (Exception e) {
			logger.error("登录" + name + "平台失败:" + e.getMessage(),e);
			return result.toJSONString(failureCode, "登录" + name + "平台失败:" + e.getMessage());
		}
	}
	
	/**
	 * 解析返回值
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: verifyLoginReslut 
	 * @param jsonString		返回值
	 * @param userid			用户ID
	 * @param succeedCode		成功code
	 * @param name				平台名称
	 * @return
	 * @date 2016年1月11日 下午5:33:52  
	 * @author xiongbin
	 */
	private String verifyLoginReslut(String jsonString,Long userid,Integer succeedCode,String name) {
		boolean flag = JsonResult.checkResult(jsonString,succeedCode);
		if(!flag){
			JSONObject json = JSON.parseObject(jsonString);
			Integer code = json.getInteger("code");

			/** 交通积分商城 */
			if(name.equals("交通银行")){
				if(code.equals(2113)){
					return ApiResult.outSucceed(ApiResultCode.COMM_ACCOUNT_PASSWORD_ERROR, "登录账号密码不匹配");
				}else if(code.equals(2110)){
					return ApiResult.outSucceed(ApiResultCode.COMM_GET_INTEGRAL_ERROR, "解析积分失败");
				}else if(code.equals(2107)){
					return ApiResult.outSucceed(ApiResultCode.COMM_LOGIN_ERROR, "登录失败");
				}else if( code.equals(2115)){
					return ApiResult.outSucceed(ApiResultCode.COMM_ACCOUNT_PASSWORD_LOCKED, "卡片查询密码已被锁定");
				}else if( code.equals(2116)){
					return ApiResult.outSucceed(ApiResultCode.COMM_ACCOUNT_PASSWORD_LIMITTED, "登录密码输入已超过限制，请24小时后再试");
				}
			}

			
			/** 淘宝返回码 */
			/*if(code.equals(1044)){
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
			}*/
			
			/** 招商银行返回码 */
			if(name.equals("招商银行")){
				if(code.equals(2004)){
					String image = this.getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(2005)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
				}else if(code.equals(1048) || code.equals(1049)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, "系统繁忙");
				}
			}
			
			/** 京东返回码 */
			if(name.equals("京东")){
				if(code.equals(1001) || code.equals(1044)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(1045)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
				}else if(code.equals(1040) || code.equals(1041) || code.equals(1031) || code.equals(1032) || code.equals(1033)
												|| code.equals(1013) || code.equals(1034) || code.equals(1035) || code.equals(1036)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, "系统繁忙");
				}else if(code.equals(1046)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NO_EXIST, "账户名不存在");
				}
			}
			
			/** 天翼 */
			/*if(code.equals(2004)){
				String image = getImageCode(userid);
				image = analysisImageCode(image);
				JSONObject resultJson = new JSONObject();
				resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
				resultJson.put("message", "图片验证码不正确");
				resultJson.put("imagecode", image);
				return resultJson.toJSONString();
			}else if(code.equals(2005)){
				return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
			}else if(code.equals(2007) || code.equals(2603) || code.equals(2049)){
				return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, "系统繁忙");
			}*/
			/** 天翼  and 联通*/
			if(name.equals("天翼") || name.equals("联通")){
				if(code.equals(3004)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(3005)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
				}else if(code.equals(3002)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, name+"系统维护中，请稍后再试");
				}else if(code.equals(3003)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, json.getString("message"));
				}
			}
			
			/** 中国国航 */
			if(name.equals("中国国航")){
				if(code.equals(2004)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(2005)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误 ");
				}else if(code.equals(1046)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_NO_EXIST, "会员卡不存在或未激活");
				}else if(code.equals(1068)){
					return ApiResult.outSucceed(ApiResultCode.ACCOUNT_LOCK, "账户被锁定,请于60分钟后再试");
				}
			}
			
			/** 南方航空 */
			if(name.equals("南方航空")){
				if(code.equals(1052)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(1045)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误 ");
				}else if(code.equals(1053)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, "系统繁忙");
				}
			}
			
			/** 移动  */
			if(name.equals("移动")){
				if(code.equals(1052)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(1075)){
					return ApiResult.outSucceed(ApiResultCode.CMCC_SMS_ERROR,"短信验证码错误，请返回并在一分钟后重新获取验证码");
				}else if(code.equals(1055)){
					return ApiResult.outSucceed(ApiResultCode.CMCC_NOT_CMCC_USER,"非移动用户,无法查询");
				}else if(code.equals(1050)){
					return ApiResult.outSucceed(ApiResultCode.CMCC_GET_PHONE_CODE_ERROR,"获取短信验证码失败，请稍后再试");
				}
			}
			
			/** 铂涛会 */
			if(name.equals("铂涛会")){
				if(code.equals(1071)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(1074)){
					String message = json.getString("message");
					if( message == null || "".equals(message)){ message = "请求短信验证码过于频繁，稍后再试";}
					return ApiResult.outSucceed(ApiResultCode.BOTAOHUI_PHONECODE_TWICE_ERROR,message);
				}else if(code.equals(1072)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY,"系统繁忙，稍后再试");
				}
			}
			
			/** 浦发银行*/
			if(name.equals("浦发银行")){
				if(code.equals(3004)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(3005)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
				}else if(code.equals(3002)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, "支付失败，浦发银行系统维护中，请稍后再试");
				}else if(code.equals(3003)){
					return ApiResult.outSucceed(ApiResultCode.LOGIN_ERROR, json.getString("message"));
				}
			}
			
			
			
			/** 平安银行*/
			if(name.equals("平安银行")){
				if(code.equals(1112)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(1111)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
				}else{
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, "支付失败，平安银行系统维护中，请稍后再试");
				}
			}
			
			
		}
		
		return jsonString;
	}
	
	
	/**
	 * 登录,有图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userId
	 * @param loginAccount
	 * @param password
	 * @param imageCode
	 * @return
	 * @date 2016年1月11日 上午10:23:48  
	 * @author xiongbin
	 */
	public abstract String login(Long userId,String loginAccount, String password, String imageCode);
	
	/**
	 * 登录无验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userId
	 * @param loginAccount
	 * @param password
	 * @return
	 * @date 2016年1月11日 上午10:24:10  
	 * @author xiongbin
	 */
	public abstract String login(Long userId,String loginAccount, String password);
	
	/**
	 * 保存
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: save 
	 * @param userId		用户ID
	 * @param manualId		积分账户ID
	 * @param loginAccount	登录名
	 * @param password		密码
	 * @param result		返回值
	 * @return
	 * @date 2016年1月12日 上午10:42:12  
	 * @author xiongbin
	 * @param version 
	 */
	public abstract String save(Long userId,Long manualId,String loginAccount, String password,String result, String version);
	
	/**
	 * 从redis中删除用户登录记录key
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: delUserRecordRedisKey 
	 * @param userId
	 * @param manualId
	 * @date 2015年11月19日 上午11:40:20  
	 * @author xiongbin
	 */
	protected void delUserRecordRedisKey(Long userId,Long manualId){
		//删除用户登录记录key
		String userRecordKey = String.format(RedisKey.INTEGRAL_MANUAL_USER_LOGIN_RECORD_1,userId,manualId);
		String userRecordKey2 = String.format(RedisKey.INTEGRAL_MANUAL_USER_LOGIN_RECORD_2,userId,manualId);
		String isImageKey = String.format(RedisKey.MANUAL_QUERY_ISIMAGE_BY_USERID_MANUALID,userId,manualId);
		redis.del(userRecordKey);
		redis.del(userRecordKey2);
		redis.del(isImageKey);
	}
	
	/**
	 * 解析验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: analysisImageCode 
	 * @param reslut
	 * @return
	 * @date 2016年1月12日 上午11:30:03  
	 * @author xiongbin
	 */
	public String analysisImageCode(String reslut){
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
	 * @Title: crackImageCode 
	 * @param imageCode
	 * @return
	 * @date 2016年1月12日 下午4:55:54  
	 * @author xiongbin
	 */
	protected String crackImageCode(String imageCode) {
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
	
	
}
