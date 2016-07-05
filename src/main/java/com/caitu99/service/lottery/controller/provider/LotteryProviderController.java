/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lottery.controller.provider;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.lottery.domain.LotteryOrder;
import com.caitu99.service.lottery.service.LotteryMainService;
import com.caitu99.service.lottery.utils.CheckSignMD5;
import com.caitu99.service.lottery.vo.LotteryOrderVo;
import com.caitu99.service.lottery.vo.LotteryResult;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.utils.Configuration;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LotteryProviderController 
 * @author fangjunxiao
 * @date 2016年5月10日 下午8:22:38 
 * @Copyright (c) 2015-2020 by caitu99 
 */

@Controller
@RequestMapping("/public/lottery/")
public class LotteryProviderController extends BaseController {
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private LotteryMainService lotteryMainService;
	
	@Autowired
	private PushMessageService pushMessageService;
	
	private Logger log = LoggerFactory.getLogger(LotteryProviderController.class);
	
	
	@RequestMapping(value="generate/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String generate(LotteryOrderVo lotteryOrderVo){
		 LotteryResult result = new LotteryResult();
		if(null == lotteryOrderVo){
			log.info("参数为空：LotteryOrderVo");
			return result.toJSONString("fail", "参数为空", "", "200");
		}
		
	    if(!appConfig.lotteryAppKey.equals(lotteryOrderVo.getAppKey())){
	        log.info("appKey不匹配");
	        return result.toJSONString("fail", "appKey不匹配", "", "200");
	    }
	    
	    if(null == lotteryOrderVo.getTimestamp()){
	        log.info("请求中没有带时间戳");
	        return result.toJSONString("fail", "请求中没有带时间戳", "", "200");
	    }
	    
	    boolean verify = false;
	    try {
	    	 verify = CheckSignMD5.checkOrder(lotteryOrderVo,appConfig.lotteryAppSecret);
		} catch (Exception e) {
			log.info("签名验证失败");
			e.printStackTrace();
			return result.toJSONString("fail", "签名验证失败", "", "200");
		}
	    
	    if(!verify){
	        log.info("签名验证失败");
	        return result.toJSONString("fail", "签名验证失败", "", "200");
	    }
	    
	    //金额不能超上限
	    if(!"200".equals(lotteryOrderVo.getPoints())){
	    	log.info("金额不匹配:" + lotteryOrderVo.getPoints());
	    	return result.toJSONString("fail", "金额不匹配", "", "200");
	    }
	    
	    //订单重复
	    boolean isSame = lotteryMainService.checkIsSame(lotteryOrderVo.getLotteryOrderId());
	    if(isSame){
	    	log.info("isSame:" + lotteryOrderVo.getLotteryOrderId());
	    	return result.toJSONString("fail", "订单重复", "", "200");
	    }
	    
	    //判断用户财币途币是否足够
	    //判断是否有活动及支付方式
	    Map<String,String> map = lotteryMainService.selectAndCheckPrice(Long.parseLong(lotteryOrderVo.getFuserid()));
	    
	    if("-1".equals(map.get("code"))){
	    	return result.toJSONString("fail", "财币或途币不足", "", "200");
	    }
	    
	    //对彩票业务操作
	    String orderNo = lotteryMainService.createLotteryOrder(lotteryOrderVo,map);
	    if(StringUtils.isBlank(orderNo)){
	    	return result.toJSONString("fail", "生成订单失败", "", "200");
	    }
	    
		return result.toJSONString("ok", "", orderNo, "0");
		
	}
	
	
	@RequestMapping(value="notice/1.0", produces="application/json;charset=utf-8")
	@ResponseBody 
	public String notice(LotteryOrderVo lotteryOrderVo){
		if(null == lotteryOrderVo){
			log.error("第三方彩票通知参数空");
			return "ok";
		}
		
	    if(!appConfig.lotteryAppKey.equals(lotteryOrderVo.getAppKey())){
	        log.error("第三方彩票通知appKey不匹配:{}",lotteryOrderVo.getBizId());
	        return "ok";
	    }	
	    
	    if(null == lotteryOrderVo.getTimestamp()){
	        log.error("第三方彩票通知请求中没有带时间戳:{}",lotteryOrderVo.getBizId());
	        return "ok";
	    }
		
	    boolean verify = false;
	    try {
	    	 verify = CheckSignMD5.checkNotice(lotteryOrderVo,appConfig.lotteryAppSecret);
		} catch (Exception e) {
			log.error("第三方彩票通知签名验证失败:{}",lotteryOrderVo.getBizId());
			e.printStackTrace();
			return "ok";
		}
	    
	    if(!verify){
	        log.error("签名验证失败:{}",lotteryOrderVo.getBizId());
	        return "ok";
	    }
	    
	    
	    //彩票订单业务
	    if("yes".equals(lotteryOrderVo.getResultStatus())){
			//处理重复订单
	    	//查询订单状态是否被处理过
			LotteryOrder lo = lotteryMainService.getLotteryOrder(lotteryOrderVo.getBizId());
			
			if(null == lo || 0 != lo.getStatus().intValue()){
				log.info("彩票通知重复:我方订单号{}" ,lotteryOrderVo.getBizId());
				return "ok";
			}
	    	
			//修改订单状态
		    lotteryMainService.changeLotteryOrderStatus(lotteryOrderVo.getBizId());
		    
			try {
				String title = Configuration.getProperty(
						"pushLotteryTitleSuccess", null);
				String pushMessage = Configuration.getProperty(
						"pushLotteryPaySuccess", null);
				
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setSmsInfo("");
				message.setTitle(title);
				message.setPushInfo(pushMessage);
				log.info("彩票投注成功push消息通知：userId:{},message:{}", lotteryOrderVo.getBizId(),JSON.toJSONString(message));
				pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, lo.getUserId(),message);
			} catch (Exception e) {
				log.error("彩票投注成功提醒推送消息发生异常：{},{}",e.getMessage(),e);
				return "ok";
			}
			
	    	return "ok";
	    }else{
	   	 	log.info("彩票通知出票失败:{}",lotteryOrderVo.getLotteryOrderId());
	   	 	//出票失败，修改  交易记录为  失败 ， 生成入分记录  
	   	 	boolean flag = lotteryMainService.failLotteryOrder(lotteryOrderVo.getLotteryOrderId());
	   	 	if(!flag){
	   	 		log.error("出票失败:我方业务处理异常{}" ,lotteryOrderVo.getLotteryOrderId());
	   	 	} 
	   	 	
			LotteryOrder lo = lotteryMainService.getLotteryOrderByOutOrderId(lotteryOrderVo.getLotteryOrderId());
	   	 	
			try {
				String title = Configuration.getProperty(
						"pushLotteryTitleFail", null);
				String pushMessage = Configuration.getProperty(
						"pushLotteryPayFail", null);
				
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setSmsInfo("");
				message.setTitle(title);
				message.setPushInfo(pushMessage);
				log.info("彩票投注失败push消息通知：userId:{},message:{}", lotteryOrderVo.getBizId(),JSON.toJSONString(message));
				pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, lo.getUserId(),message);
			} catch (Exception e) {
				log.error("彩票投注失败提醒推送消息发生异常：{},{}",e.getMessage(),e);
				return "ok";
			}
	   	 	return "ok";
	    } 
	}

}
