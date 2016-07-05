package com.caitu99.service.integral.controller.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

@Component
public class ManualQueryCU extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryCU.class);
	

	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/unicomchina/img/1.0";
		super.setUrl(url);
		super.setName("联通");
		super.setSucceedCode(ApiResultCode.NEED_INOUT_IMAGECODE);
		super.setFailureCode(ApiResultCode.GET_TIANYI_IMAGE_CODE_ERROR);
		
		return super.getImageCode(userid);
	}
	

	@Override
	public String login(Long userid,String account,String password,String imageCode){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/unicomchina/login/1.0";
		super.setName("联通");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userid.toString());
		param.put("account", account);
		param.put("password", password);
		param.put("vcode", imageCode);

		return super.login(userid, url, param, ApiResultCode.SUCCEED);
	}
	
	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String result, String version) {
		try {
			JSONObject json = JSON.parseObject(result);
			String data = json.getString("data");
			
			AssertUtil.hasLength(data, "登录联通失败");
			
			Integer integral = json.getInteger("data");
			String userName = loginAccount;
			
			Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			/**
			 * 记录用户积分数据 
			 */
			UserCardManual csAirManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CU_INTEGRAL,null,null,userName);
			
			if(null == csAirManual){
				presentTubiService.presentTubiDo(userId, UserCardManual.CU_INTEGRAL, 0, integral, resData, version);
				
				csAirManual = new UserCardManual();
				csAirManual.setIntegral(integral);
				csAirManual.setUserName(userName);
				csAirManual.setGmtModify(now);
				csAirManual.setGmtCreate(now);
				csAirManual.setUserId(userId);
				csAirManual.setLoginAccount(userName);
				csAirManual.setCardTypeId(UserCardManual.CU_INTEGRAL);
			}else{
				presentTubiService.presentTubiDo(userId, UserCardManual.CU_INTEGRAL, csAirManual.getIntegral(), integral, resData, version);
				
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
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			resultJson.put("data", resData);
			
			return resultJson.toJSONString();
		} catch (Exception e) {
			logger.warn("登录联通失败:{}",e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,"系统繁忙");
		}
	}


	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract#login(java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public String login(Long userId, String loginAccount, String password) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: vcodeLogin 
	 * @param userId
	 * @param phone
	 * @param password
	 * @param imageCode
	 * @return
	 * @date 2016年3月23日 下午3:34:16  
	 * @author ws
	*/
	public String vcodeLogin(Long userId, String phone, String password,
			String imageCode) {
		// TODO Auto-generated method stub
		return null;
	}
}
