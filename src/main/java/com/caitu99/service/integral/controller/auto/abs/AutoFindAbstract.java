package com.caitu99.service.integral.controller.auto.abs;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.service.PresentTubiService;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.AutoFindRecord;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.domain.Manual;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ExchangeRuleService;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.ManualService;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.json.JsonResult;

public abstract class AutoFindAbstract {

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");

	@Autowired
	public PushMessageService pushMessageService;
	@Autowired
	public ManualLoginService manualLoginService;
	@Autowired
	public ManualService manualService;
	@Autowired
	CardTypeService cardTypeService;

	@Autowired
	public PresentTubiService presentTubiService;
	
	/**
	 * 效验登录
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: login
	 * @param manualQuery
	 *            手动查询类
	 * @param userId
	 *            用户ID
	 * @param loginAccount
	 *            账号
	 * @param password
	 *            密码
	 * @param count
	 *            图片验证码允许错误次数
	 * @param log
	 *            日志
	 * @return
	 * @date 2015年12月16日 下午6:35:36
	 * @author xiongbin
	 */
	public abstract String login(ManualQueryAbstract manualQuery, Long userId,String loginAccount, String password, Integer count, String log);

	/**
	 * 登录，用于自动更新
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: loginForUpdate
	 * @param manualQuery
	 * @param userId
	 * @param loginAccount
	 * @param password
	 * @return
	 * @date 2015年12月18日 下午2:55:37
	 * @author ws
	 */
	public abstract String loginForUpdate(ManualQueryAbstract manualQuery,Long userId, String loginAccount, String password);

	/**
	 * 解析登录返回结果
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: verifyLoginReslut
	 * @param jsonString
	 *            登录返回结果
	 * @param manualQuery
	 *            手动查询类
	 * @param userId
	 *            用户ID
	 * @param loginAccount
	 *            账号
	 * @param password
	 *            密码
	 * @param count
	 *            图片验证码错误次数
	 * @param log
	 *            日志
	 * @return
	 * @date 2015年12月17日 下午4:00:16
	 * @author xiongbin
	 */
	public String verifyLoginReslut(String jsonString,
			ManualQueryAbstract manualQuery, Long userId, String loginAccount,
			String password, Integer count, String log) {
		logger.info("【手动查询自动发现】:" + "userId：" + userId + "," + log
				+ "自动发现,登陆结束,解析返回结果");

		JSONObject reslutJSON = new JSONObject();

		try {
			String reslut = checkResult(jsonString, manualQuery, userId,
					loginAccount, password, count, log);

			if (StringUtils.isBlank(reslut)) {
				return null;
			}

			reslutJSON = JSON.parseObject(reslut);
			Integer code = reslutJSON.getInteger("code");

			if (AutoFindRecord.STATUS_NORMAL.equals(code)) {
				logger.info("【手动查询自动发现成功】:" + "userId：" + userId + "," + log
						+ "自动发现,尝试登陆成功.");
			}

			return reslut;
		} catch (ManualQueryAdaptorException e) {
			logger.error("【手动查询自动发现失败】:" + log + "自动发现失败:" + e.getMessage(), e);

			reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
			reslutJSON.put("message", "自动发现失败:" + e.getMessage());
			reslutJSON.put("error", ApiResultCode.AUTO_FIND_ERROR);
			return reslutJSON.toJSONString();
		}
	}

	/**
	 * 验证返回值
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: checkResult
	 * @param jsonString
	 *            返回值
	 * @param manualQuery
	 *            手动查询类
	 * @param userId
	 *            用户ID
	 * @param loginAccount
	 *            账号
	 * @param password
	 *            密码
	 * @param count
	 *            图片验证码允许错误次数
	 * @param log
	 *            日志
	 * @return
	 * @date 2015年12月16日 下午6:37:34
	 * @author xiongbin
	 */
	public String checkResult(String jsonString,
			ManualQueryAbstract manualQuery, Long userId, String loginAccount,
			String password, Integer count, String log) {

		boolean flag = JsonResult
				.checkResult(jsonString, ApiResultCode.SUCCEED);

		if (!flag) {
			JSONObject json = JSON.parseObject(jsonString);
			Integer code = json.getInteger("code");

			JSONObject reslutJSON = new JSONObject();

			if (code.equals(ApiResultCode.IMAGECODE_ERROR)) {
				// 图片验证码不正确
				if (count < 2) {
					logger.info("【手动查询自动发现失败】:" + "userId：" + userId + ","
							+ log + "自动发现,尝试登陆失败." + "图片验证码不正确,尝试第二次");
					String reslut = login(manualQuery, userId, loginAccount,
							password, count++, log);

					return reslut;
				} else {
					logger.info("【手动查询自动发现失败】:" + "userId：" + userId + ","
							+ log + "自动发现,尝试登陆失败." + "图片验证码不正确,账号密码可能可以登陆");

					reslutJSON.put("code", AutoFindRecord.STATUS_NORMAL);
					reslutJSON.put("messsage", "图片验证码不正确");
					reslutJSON.put("error", ApiResultCode.IMAGECODE_ERROR);
				}
			} else if (code.equals(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR)) {
				// 账号或密码错误
				logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log
						+ "自动发现,尝试登陆失败." + "账号或密码错误 ,账号存在");

//				reslutJSON.put("code", AutoFindRecord.STATUS_LOGINACCUNT_EXIST);
				reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
				reslutJSON.put("messsage", "账号或密码错误");
				reslutJSON.put("error",
						ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR);
			} else if (code.equals(ApiResultCode.SYSTEM_BUSY)) {
				// 系统繁忙
				logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log
						+ "自动发现,尝试登陆失败." + "系统繁忙");

				reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
				reslutJSON.put("messsage", "系统繁忙");
				reslutJSON.put("error", ApiResultCode.SYSTEM_BUSY);
			} else if (code.equals(ApiResultCode.LOGINACCOUNT_NO_EXIST)) {
				// 账户名不存在
				logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log
						+ "自动发现,尝试登陆失败." + "账户名不存在");

				reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
				reslutJSON.put("messsage", "账户名不存在");
				reslutJSON.put("error", ApiResultCode.LOGINACCOUNT_NO_EXIST);
			} else if (code.equals(ApiResultCode.ACCOUNT_LOCK)) {
				// 账户被锁定
				logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log
						+ "自动发现,尝试登陆失败." + "账户被锁定 ,账号密码可登陆");

				reslutJSON.put("code", AutoFindRecord.STATUS_NORMAL);
				reslutJSON.put("messsage", "账户被锁定");
				reslutJSON.put("error", ApiResultCode.ACCOUNT_LOCK);
			} else if (code.equals(ApiResultCode.LOGINACCOUNT_ERROR)) {
				// 账号错误
				logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log
						+ "自动发现,尝试登陆失败." + "账号错误 ");

				reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
				reslutJSON.put("messsage", "账号错误");
				reslutJSON.put("error", ApiResultCode.LOGINACCOUNT_ERROR);
			} else if (code.equals(ApiResultCode.PASSWORD_ERROR)) {
				// 密码错误
				logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log
						+ "自动发现,尝试登陆失败." + "密码错误,但账号存在 ");

				reslutJSON.put("code", AutoFindRecord.STATUS_LOGINACCUNT_EXIST);
				reslutJSON.put("messsage", "密码错误");
				reslutJSON.put("error", ApiResultCode.PASSWORD_ERROR);
			} else if (code.equals(ApiResultCode.LOGINACCOUNT_VERIFY_EXPIRE)) {
				// 账号验证已过期
				logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log
						+ "自动发现,尝试登陆失败." + "账号验证已过期 ,账号密码可登陆 ");

				reslutJSON.put("code", AutoFindRecord.STATUS_NORMAL);
				reslutJSON.put("messsage", "账号验证已过期");
				reslutJSON.put("error",
						ApiResultCode.LOGINACCOUNT_VERIFY_EXPIRE);
			} else {
				return null;
			}

			return reslutJSON.toJSONString();
		}

		return jsonString;
	}

	/**
	 * 破解验证码
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: crackImageCode
	 * @param iamgeCode
	 * @return
	 * @date 2015年12月16日 下午3:18:23
	 * @author xiongbin
	 */
	public String crackImageCode(String iamgeCode) {
		Integer count = 1;
		String code = null;

		do {
			code = CommonImgCodeApi.recognizeImgCodeFromStr(iamgeCode);
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
	 * @date 2015年12月31日 下午4:03:34  
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
	 * 保存结果，用于自动更新
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: saveResult
	 * @param userId
	 * @param manualId
	 * @param loginAccount
	 * @param password
	 * @param result
	 * @return
	 * @date 2015年12月18日 下午2:55:17
	 * @author ws
	 */
	public abstract String saveResult(Long userId, Long manualId,
			String loginAccount, String password, String result,String version);

	/**
	 * 账户密码错误消息推送
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: pwdErrHandler
	 * @param userId
	 * @param manualId
	 * @param loginAccount
	 * @param password
	 * @date 2015年12月25日 下午3:46:13
	 * @author Hongbo Peng
	 */
	public void pwdErrHandler(Long userId, Long manualId, String loginAccount,
			String password) {
		try {
			Manual manual = manualService.selectByPrimaryKey(manualId);
			if (null == manual || null == manual.getName()) {
				return;
			}
			String manualName = manual.getName();
			String description = Configuration.getProperty(
					"push.integral.account.password.change.description", null);
			String yellow = Configuration.getProperty(
					"push.integral.account.password.change.yellow", null);
			String title = Configuration.getProperty(
					"push.integral.account.password.change.title", null);
			Message message = new Message();
			message.setIsPush(true);
			message.setIsSMS(false);
			message.setIsYellow(true);
			message.setTitle(title);
			message.setPushInfo(String.format(description, manualName + "账户"));
			message.setYellowInfo(String.format(yellow, manualName + "账户"));
			logger.info("新增消息通知：userId:{},message:{}", userId,
					JSON.toJSONString(message));
			pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, userId,
					message);
		} catch (Exception e) {
			logger.error("积分账户密码变动推送消息异常：{}", e);
		}

		ManualLogin record = new ManualLogin();
		record.setUserId(userId);
		record.setManualId(manualId);
		record.setLoginAccount(loginAccount);
		record.setPassword(password);
		ManualLogin selectedRecord = manualLoginService.getBySelective(record);
		selectedRecord.setStatus(2);// 标记为账号密码错误
		manualLoginService.updateByPrimaryKeySelective(selectedRecord);
	}

	/**
	 * 积分变动消息推送
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: pushIntegralChangeMessage
	 * @param userId
	 * @param cardTypeId
	 * @param newIntegral
	 * @param changeIntegral
	 * @date 2015年12月25日 下午3:46:26
	 * @author Hongbo Peng
	 */
	public void pushIntegralChangeMessage(Long userId, Long cardTypeId,
			Integer newIntegral, Integer changeIntegral) {
		try {
			if(0 != changeIntegral){
				CardType cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
				String account = cardType.getName();
				String description = Configuration.getProperty(
						"push.auto.update.description", null);
				String title = Configuration.getProperty(
						"push.auto.update.title", null);
				RedSpot redPot = null;
				switch (cardType.getTypeId()) {
				case 1:
					redPot = RedSpot.CREDIT_INTEGRAL;
					break;
				case 2:
					redPot = RedSpot.BUSINESS_INTEGRAL;
					break;
				case 3:
					redPot = RedSpot.SHOPING_INTEGRAL;
					break;
				default:
					break;
				}
				String changeIntStr = "";
				if(changeIntegral > 0){
					changeIntStr = "+" + changeIntegral;
				}else{
					changeIntStr = changeIntegral.toString();
				}
				
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setTitle(title);
				message.setPushInfo(String.format(description, account,
						newIntegral, changeIntStr));
				logger.info("新增消息通知：userId:{},message:{}", userId,
						JSON.toJSONString(message));
				pushMessageService.pushMessage(redPot, userId,
						message);
			}
		} catch (Exception e) {
			logger.error("积分变动推送消息异常：{}", e);
		}
	}
	
}
