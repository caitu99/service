/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.sys.sms;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.sys.sms.cl.ClClient;
import com.caitu99.service.utils.SpringContext;

/** 
 * 
 * @Description: (短信发送类) 
 * @ClassName: SMSSend 
 * @author Hongbo Peng
 * @date 2015年12月16日 下午2:15:07 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class SMSSend {

	private static final Logger logger = LoggerFactory.getLogger(SMSSend.class);
	
	/**
	 * @Description: (发送短信，先使用创蓝发送，如果失败，使用YM发送)  
	 * @Title: sendSMS 
	 * @param mobiles
	 * @param msg
	 * @date 2015年12月16日 下午2:23:15  
	 * @author Hongbo Peng
	 */
	public static void sendSMS(String[] mobiles,String msg,String channel){
		String[] mobs = isSendSMS(mobiles);
		if(null == mobs){
			return ;//当前一分钟内发送过短信
		}
		if("1".equals(channel)){
			try {
				String flag = ClClient.getClClient().send(mobs, msg);
				String status = flag.substring(flag.indexOf(",")+1, flag.indexOf("\n"));
				if("0".equals(status)){
					logger.info("创蓝短信发送提交成功,mobile:{},msg:{},flag:{}",mobiles,msg,flag);
					try {
						String msgId = flag.split("\n")[1];
						for (String m : mobs) {
							String key = String.format(RedisKey.SMS_CL_MSGID, msgId,m);
							RedisOperate redis = SpringContext.getBean(RedisOperate.class);
							redis.set(key, msg,5 * 60);//存5分钟
						}
					} catch (Exception e) {
						logger.error("将短信发送ID存入redis发生异常:{}",e);
					}
				} else {
					logger.info("创蓝发送短信失败,尝试使用亿美发送,flag{},mobile:{},msg:{}",flag,mobiles,msg);
					sendYmSMS(mobs,msg);
				}
			} catch (Exception e) {
				logger.error("创蓝发送短信失败,尝试使用亿美发送,mobile:{},msg:{},exception:{}",mobiles,msg,e);
				sendYmSMS(mobs,msg);
			}
		}else if("2".equals(channel)){
			try {
				String flag = ClClient.getClClient().send(mobs, msg);
				String status = flag.substring(flag.indexOf(",")+1, flag.indexOf("\n"));
				if("0".equals(status)){
					logger.info("创蓝短信发送提交成功,mobile:{},msg:{},flag:{}",mobiles,msg,flag);
					try {
						String msgId = flag.split("\n")[1];
						for (String m : mobs) {
							String key = String.format(RedisKey.SMS_CL_MSGID, msgId,m);
							RedisOperate redis = SpringContext.getBean(RedisOperate.class);
							redis.set(key, msg,5 * 60);//存5分钟
						}
					} catch (Exception e) {
						logger.error("将短信发送ID存入redis发生异常:{}",e);
					}
				} else {
					logger.info("使用创蓝单通道发送短信失败,flag{},mobile:{},msg:{}",flag,mobiles,msg);
				}
			} catch (Exception e) {
				logger.error("使用创蓝单通道发送短信失败,mobile:{},msg:{},exception:{}",mobiles,msg,e);
			}
		}else if("3".equals(channel)){
			logger.info("使用亿美发送短信,mobile:{},msg:{}",mobs,msg);
			sendYmSMS(mobs,msg);
		}else{
			logger.error("没有短信通道可以用,mobile:{},msg:{}",mobiles,msg);
		}
	}
	
	public static void sendYmSMS(String[] mobiles,String msg){
		try {
			SingletonClient.getClient().sendSMS(mobiles, msg, "", 5);
		} catch (Exception e) {
			logger.error("YM发送短信失败,mobile:{},msg:{},exception:{}",mobiles,msg,e);
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description: (限制手机号码一分钟内发送一次短信)  
	 * @Title: isSendSMS 
	 * @param mobiles
	 * @return
	 * @date 2015年12月14日 下午2:34:21  
	 * @author Hongbo Peng
	 */
	private static String[] isSendSMS(String[] mobiles){
		StringBuilder builder = new StringBuilder();
		RedisOperate redis = SpringContext.getBean(RedisOperate.class);
		for (String mobile : mobiles) {
			String key = String.format(RedisKey.IS_SEND_TO_MOBILE, mobile);
			String value = redis.getStringByKey(key);
			if(StringUtils.isBlank(value)){
				builder.append(mobile).append(",");
				redis.set(key, mobile, 60);
			}else{
				logger.info("号码:{}在一分钟内发送过短信，暂不发送",mobile);
			}
		}
		return StringUtils.isBlank(builder.toString()) ? null : builder.toString().split(",");
	}
}
