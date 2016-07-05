/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mail.controller.service;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.mail.controller.api.MailImportController;
import com.caitu99.service.mail.controller.constants.UserMailConstants;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.user.domain.UserMail;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserMailService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.file.CommonImgCodeApi;
import com.caitu99.service.utils.file.ShowImgApi;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailJobService 
 * @author ws
 * @date 2015年12月10日 下午5:44:01 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class MailParseService{

	private static final Logger logger = LoggerFactory
			.getLogger(MailParseService.class);
	
	@Autowired
	MailImportController mailImportController;
	@Autowired
	UserCardService userCardService;
	@Autowired
	UserMailService userMailService;
	@Autowired
	KafkaProducer kafkaProducer;
	@Autowired
	public PushMessageService pushMessageService;
	
	private static final Long TYPE = 1L;//导邮箱类型 
	private String ERR_MSG = "【邮箱自动更新失败】：userId:{},account:{},errMsg:{}";
	private String INFO_MSG = "【邮箱自动更新成功】：userId:{},account:{},infoMsg:{}";
	private String WARN_MSG = "【邮箱自动更新警告】：userId:{},account:{},warnMsg:{}";
	
	/**
	 * 邮箱解析
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: parseSigleMail 
	 * @param userMail
	 * @date 2015年12月10日 下午8:17:21  
	 * @author ws
	 */
	public void parseSigleMail(UserMail userMail) {
		
		String account = userMail.getEmail();
		Long userid = userMail.getUserId();
		String pwdAlone = "";
		String password = ""; 
		try {
			String emailPassword = userMail.getEmailPassword();
			String emailPwdAlone = userMail.getEmailPasswordAlone();
			if(StringUtils.isNotBlank(emailPassword)){
				password = AESCryptoUtil.decrypt(emailPassword);
			}
			if(StringUtils.isNotBlank(emailPwdAlone)){
				pwdAlone = AESCryptoUtil.decrypt(emailPwdAlone);
			}
		} catch (CryptoException e) {
			logger.error(ERR_MSG,userid,account,"用户密码解密失败");
			return;
		}
		
		String loginResult = "";
		loginResult = mailImportController.login(userid, account, password,"","");
		JSONObject loginObject = JSON.parseObject(loginResult);
		String loginCode = loginObject.getString("code");
		
		if("1019".equals(loginCode)){//登录成功，开始获取邮件

			logger.info(INFO_MSG,userid,account,"登录成功，开始获取邮件");
			checkResult(userid, account);
		}else if("1001".equals(loginCode)){//需要验证码

			logger.info(INFO_MSG,userid,account,"需要验证码");
			verify(userid,account,loginObject,pwdAlone);
		}else if("1014".equals(loginCode)){//需要独立密码

			logger.info(INFO_MSG,userid,account,"需要独立密码");
			pwdAlone(userid,account,pwdAlone);
		}else if("1013".equals(loginCode)){//账号密码错误

			logger.warn(WARN_MSG,userid,account,"账号密码错误");
			try {
				String description =  Configuration.getProperty("push.integral.account.password.change.description", null);
				String yellow = Configuration.getProperty("push.integral.account.password.change.yellow", null);
				String title = Configuration.getProperty("push.integral.account.password.change.title", null);
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(true);
				message.setTitle(null);
				message.setTitle(title);
				message.setPushInfo(String.format(description,account+"邮箱"));
				message.setYellowInfo(String.format(yellow, account+"邮箱"));
				logger.info("新增消息通知：userId:{},message:{}",userid,JSON.toJSONString(message));
				pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, userid, message);
			} catch (Exception e) {
				logger.error("积分账户密码变动推送消息异常：{}",e);
			}
			
			//并且更新用户邮箱标识置为不正常
			userMail.setFlag(UserMailConstants.USER_MAIL_FLAG_FAIL);//表示账号密码错误
			userMail.setGmtModify(new Date());
			userMailService.updateByPrimaryKeySelective(userMail);
		}else{//其他未登录成功异常
			
			logger.warn(ERR_MSG,userid,account,"登录失败，其他异常");
		}
	}

	/**
	 * 验证码验证
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: verify 
	 * @param userid
	 * @param account
	 * @param vcode
	 * @date 2015年12月10日 下午8:20:45  
	 * @author ws
	 */
	private void verify(Long userid, String account, JSONObject loginObject, String pwdAlone) {
		String vcodeImge = loginObject.getString("data");
		//验证码识别
		String vcode = CommonImgCodeApi.recognizeImgCodeFromStr(vcodeImge);
		if(null == vcode){//表示识别错误
			vcode = ShowImgApi.recognizeImgCodeFromStr(vcodeImge);//再试一次
			if(null == vcode){
				logger.warn(ERR_MSG,userid,account,"验证码验证失败");
				return;
			}
		}
		
		String verifyResult = "";
		verifyResult = mailImportController.verify(userid, account, vcode);
		JSONObject verifyObject = JSON.parseObject(verifyResult);
		String verifyCode = verifyObject.getString("code");
		if("1019".equals(verifyCode)){//登录成功，开始获取邮件
			checkResult(userid, account);
			return;
		}else if("1001".equals(verifyCode)){//验证码验证失败，再试一次
			verifyResult = mailImportController.verify(userid, account, vcode);
			verifyObject = JSON.parseObject(verifyResult);
			verifyCode = verifyObject.getString("code");
			if("1019".equals(verifyCode)){//登录成功，开始获取邮件
				
				checkResult(userid, account);
				return;
			}else if("1001".equals(verifyCode)){
				
				logger.warn(ERR_MSG,userid,account,"验证码验证失败");
				return;
			}else if("1014".equals(verifyCode)){//需要独立密码
				
				pwdAlone(userid,account,pwdAlone);
				return;
			}
		}else{

			logger.warn(ERR_MSG,userid,account,"登录失败，其他异常");
			return;
		}
	}


	/**
	 * 独立密码验证
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pwdAlone 
	 * @param userid
	 * @param account
	 * @date 2015年12月11日 上午9:13:54  
	 * @author ws
	*/
	private void pwdAlone(Long userid, String account, String pwdAlone) {
		String pwdaloneResult = "";

		pwdaloneResult = mailImportController.pwdalone(userid, account, pwdAlone);
		JSONObject pwdaloneObject = JSON.parseObject(pwdaloneResult);
		String pwdaloneCode = pwdaloneObject.getString("code");
		if("1019".equals(pwdaloneCode)){//登录成功，开始获取邮件
			
			checkResult(userid, account);
			return;
		}else if("1016".equals(pwdaloneCode)){
			
			logger.warn(ERR_MSG,userid,account,"独立密码错误");
			return;
		}else{
			
			logger.warn(ERR_MSG,userid,account,"登录失败，其他异常");
			return;
		}
	}

	
	/**
	 * 获取解析结果
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkResult 
	 * @param userid
	 * @param account
	 * @date 2015年12月10日 下午8:16:45  
	 * @author ws
	 */
	private void checkResult(Long userid, String account) {
		String checkResult = "";
		try {
			Thread.sleep(1000);//等待一秒，用于邮箱解析时间
		} catch (InterruptedException e) {
			logger.error(ERR_MSG,userid,account,e);
		}
		
		//获取结果
		checkResult = mailImportController.checkResult(userid, account, TYPE);
		JSONObject checkObject = JSON.parseObject(checkResult);
		String checkCode = checkObject.getString("code");
		if("1024".equals(checkCode)){//导入成功，并返回结果
			logger.info(INFO_MSG,userid,account,"自动更新成功");
			return;
		}else if("1020".equals(checkCode)){//尚未获得邮件解析结果,继续执行获取
			checkResult(userid, account);
			return;
		}else{
			logger.warn(ERR_MSG,userid,account,"获取结果失败");
			return;
		}
	}

}
