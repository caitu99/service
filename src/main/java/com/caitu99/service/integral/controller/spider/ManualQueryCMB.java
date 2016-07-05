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
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.json.JsonResult;

@Component
public class ManualQueryCMB extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryCMB.class);
	
	/**
	 * 获取图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年1月11日 上午10:28:56  
	 * @author xiongbin
	 */
	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/cmbchina/imgcode/1.0";
		super.setUrl(url);
		super.setName("招商银行");
		super.setSucceedCode(ApiResultCode.SUCCEED);
		super.setFailureCode(ApiResultCode.CMB_GET_IMAGECODE_ERROR);
		
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
	public String login(Long userid,String identityCard,String password,String imageCode){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/cmbchina/login/1.0";
		super.setName("招商银行");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userid.toString());
		param.put("account", identityCard);
		param.put("password", password);
		param.put("yzm", imageCode);
		return super.login(userid, url, param, ApiResultCode.SUCCEED);
	}
	
	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String identityCard,String password, String result,String version) {
		try {
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				return result;
			}
			
			JSONObject json = JSON.parseObject(result);
			String jsonString = json.getString("data");
			
			AssertUtil.hasLength(jsonString, "登录招商银行失败");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
			Integer integral = jsonObject.getInteger("integral");
			String userName = jsonObject.getString("name");
			String cardNo = jsonObject.getString("account");
			
			Date now = new Date();
			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", identityCard);
			
			/**
			 * 记录用户积分数据 
			 */
//			UserCardManual CMBManual = userCardManualService.getByUserIdCardTypeId(userId,UserCardManual.CMB);
			UserCardManual CMBManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CMB,null,userName,identityCard);
			
			if(null == CMBManual){
				//计算并赠送财币
				presentTubiService.presentTubiDo(userId, UserCardManual.CMB, 0, integral, resData, version);
				
				
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
				//计算并赠送财币
				presentTubiService.presentTubiDo(userId, UserCardManual.CMB, CMBManual.getIntegral(), integral, resData, version);
				
				CMBManual.setIntegral(integral);
				CMBManual.setUserName(userName);
				CMBManual.setCardNo(cardNo);
				CMBManual.setLoginAccount(identityCard);
				CMBManual.setGmtModify(now);
				CMBManual.setStatus(1);
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
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录招商银行失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CMB_LOGIN_ERROR,e.getMessage());
		}
	}
}
