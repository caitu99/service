package com.caitu99.service.user.controller.api;

import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.user.domain.Sign;
import com.caitu99.service.user.service.SignService;

import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/api/sign")
public class SignController  extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(SignController.class);
	
	@Autowired
	private SignService signService;

	@Autowired
	private PushMessageService pushMessageService;

	@RequestMapping(value="/signstatus/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String signStatus(Long userId) {
		// 初始化
		ApiResult<Sign> result = new ApiResult<Sign>();
		SimplePropertyPreFilter filter_sign = new SimplePropertyPreFilter(Sign.class,
				"continuous_time", "signGiftIntegral", "signToday");

		//业务
		Sign sign = signService.getSign(userId);
		if( sign != null ){
			result.setCode(0);
			result.setData(sign);
			result.setMessage("查询签到状态成功");
		}else{
			result.setCode(-1);
			result.setMessage("查询签到状态失败");
		}
		return JSON.toJSONString(result,filter_sign);
	}
	@RequestMapping(value="/signevery/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String signEvery(Long userId) {
		// 初始化
		ApiResult<Sign> result = new ApiResult<Sign>();
		SimplePropertyPreFilter filter_sign = new SimplePropertyPreFilter(Sign.class,
				"continuous_time","signGiftIntegral","sign_date");

		Sign sign = signService.signEvery(userId);
		if (null == sign) {
			result.setCode(2307);
			result.setMessage("您今天已经签过到了");
		} else {
			sign = signService.signGiftIntegral(sign);
			result.setData(sign);
			result.setMessage("签到成功");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logger.info("用户userId为{}在{}签到成功,赠送{}财币",userId,dateFormat.format(sign.getSign_date()),sign.getSignGiftIntegral());
			result.setCode(0);
			//推送消息
			try {
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setTitle("签到获得"+sign.getSignGiftIntegral()+"财币");
				message.setPushInfo("您今日签到获得"+sign.getSignGiftIntegral()+"财币奖励，连续签到惊喜不断。");
				pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, userId, message);
			}catch(Exception e){

			}
		}
		return JSON.toJSONString(result,filter_sign);
	}
	
	@RequestMapping(value="/signevery/2.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String signEvery2(Long userId) {
		// 初始化
		ApiResult<Sign> result = new ApiResult<Sign>();
		SimplePropertyPreFilter filter_sign = new SimplePropertyPreFilter(Sign.class,
				"continuous_time","signGiftIntegral","sign_date");

		Sign sign = signService.signEvery(userId);
		if (null == sign) {
			result.setCode(2307);
			result.setMessage("您今天已经签过到了");
		} else {
			sign = signService.signGiftTubi(sign);
			result.setData(sign);
			result.setMessage("签到成功");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logger.info("用户userId为{}在{}签到成功,赠送{}途币",userId,dateFormat.format(sign.getSign_date()),sign.getSignGiftIntegral());
			result.setCode(0);
			//推送消息
			try {
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setTitle("签到获得"+sign.getSignGiftIntegral()+"途币");
				message.setPushInfo("您今日签到获得"+sign.getSignGiftIntegral()+"途币奖励，连续签到惊喜不断。");
				pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, userId, message);
			}catch(Exception e){

			}
		}
		return JSON.toJSONString(result,filter_sign);
	}
}
