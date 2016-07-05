/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.controller.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.transaction.api.IntegralRechargeHandler;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.IntegralRechargeService;
import com.caitu99.service.transaction.service.IntegralTransferService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.utils.unionpay.UnionPay;

/** 
 * @Description: (积分充值) 
 * @ClassName: IntegralRechargeController 
 * @author Hongbo Peng
 * @date 2015年12月10日 下午6:35:15 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/transaction/recharge/")
public class IntegralRechargeController extends BaseController {

	@Autowired
	private IntegralRechargeService integralRechargeService;
    @Autowired
    IntegralTransferService integralTransferService;
	@Autowired
	private KafkaProducer kafkaProducer;
	@Autowired
	private AppConfig appConfig;
    @Autowired
    private PushMessageService pushMessageService;
	
	/**
	 * @Description: (银行卡充值财币)  
	 * @Title: recharge 
	 * @param userid
	 * @param paypass
	 * @param integral
	 * @return
	 * @date 2015年12月11日 上午9:05:06  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value = "recharge/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String recharge(Long userid,String paypass,Long integral){
		return integralRechargeService.integralReharge(userid, paypass, integral);
	}
	
	/**
	 * @Description: (查询银联支付状态，完成充值财币动作)  
	 * @Title: query 
	 * @param id
	 * @return
	 * @date 2015年12月11日 上午9:05:29  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value = "query/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String query(Long id){
		return integralRechargeService.query(id);
	}
	
	

	private static final Logger logger = LoggerFactory
			.getLogger(IntegralRechargeController.class);

	@Autowired
	TransactionRecordService transactionRecordService;
	@Autowired
	IntegralRechargeHandler integralRechargeHandler;
	
	/**
	 * 银联消费充值财币
	 * 请求银联接口，获取订单编号tn
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: rechargeIntegral 
	 * @param userId
	 * @param integral
	 * @return
	 * @date 2015年12月30日 下午4:41:49  
	 * @author ws
	 */
	@RequestMapping(value="unionpay/recharge/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String rechargeIntegral(Long userId,Long integral){
		ApiResult<String> result = new ApiResult<String>();
		Map<String,String> resmap = null;
		UnionPay unionPay = new UnionPay();
		Random random = new Random();
		String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String orderId = "caitu99" + userId + random.nextInt(10) + dateStr + random.nextInt(10); //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
		String txnTime = dateStr; //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		String backUrl = appConfig.caituUrl+"/public/integral/recharge/unionpay/backresponse/1.0";
		
		try {
			resmap = unionPay.doPay(integral.toString(), orderId, txnTime, backUrl);
		} catch (Exception e) {
			logger.error("银联充值财币，保存交易记录异常：{}",e);
			//其他应答码为失败请排查原因
			result.setCode(3602);
			result.setMessage(resmap.get("银联接口调用异常"));
			return JSON.toJSONString(result);
		}
		
		if(null == resmap || null ==resmap.get("respCode")){
			logger.info("银联接口调用失败，未响应");
			result.setCode(3601);
			result.setMessage("银联接口调用失败，未响应");
			return JSON.toJSONString(result);
		}
		
		String tn = "";
		String respCode = resmap.get("respCode");
		if(("00").equals(respCode)){
			try {
				//成功,获取tn号
				tn = resmap.get("tn");
				result.setCode(0);
				result.setData(tn);
				//保存充值记录
				transactionRecordService.saveTransactionRecord(userId, integral, orderId, tn, txnTime);

				// 注册查询任务
				Map<String, Object> jobmap = new HashMap<>();
				jobmap.put("userId", userId);
				jobmap.put("orderNo",orderId); 
				jobmap.put("txnTime",txnTime); 
				jobmap.put("jobType","UNION_PAY_ORDER_QUERY_JOB"); 
				kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
				
			} catch (Exception e) {
				logger.error("银联充值财币，保存交易记录异常：{}",e);
				//其他应答码为失败请排查原因
				result.setCode(3602);
				result.setMessage(resmap.get("请求处理异常"));
			}
			return JSON.toJSONString(result);
	        
		}else{
			//其他应答码为失败请排查原因
			result.setCode(3601);
			result.setMessage(resmap.get("respMsg"));
			logger.info("银联接口调用失败，失败信息：{}",resmap.get("respMsg"));
			return JSON.toJSONString(result);
		}
	}
	
	/**
	 * 银联支付结果处理
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySuccessHandler 
	 * @param userId
	 * @param orderNo
	 * @return
	 * @date 2015年12月30日 下午4:42:39  
	 * @author ws
	 */
	@RequestMapping(value="unionpay/order/query/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String queryOrder(Long userId,String orderNo,String txnTime){
		ApiResult<String> result = new ApiResult<String>();
		UnionPay unionPay = new UnionPay();
		try {
			if(StringUtils.isBlank(txnTime)){
				txnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//   订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
			}
			
			Map<String, String> resmap = unionPay.queryOrder(orderNo, txnTime);

			String queryId = resmap.get("queryId");//银联查询Id
			if(("00").equals(resmap.get("respCode"))){//如果查询交易成功
				String origRespCode = resmap.get("origRespCode");
				//处理被查询交易的应答码逻辑
				if(("00").equals(origRespCode) ||("A6").equals(origRespCode)){
					//A6代付交易返回，参与清算，商户应该算成功交易，根据成功的逻辑处理
					//交易成功，更新商户订单状态
					integralRechargeHandler.paySuccessDo(userId, orderNo, queryId);
					result.setCode(0);
					return result.toJSONString(0, "充值财币成功");
				}else if(("03").equals(origRespCode)||
						 ("04").equals(origRespCode)||
						 ("05").equals(origRespCode)||
						 ("01").equals(origRespCode)||
						 ("12").equals(origRespCode)||
						 ("34").equals(origRespCode)||
						 ("60").equals(origRespCode)){
					//订单处理中或交易状态未明，需稍后发起交易状态查询交易 【如果最终尚未确定交易是否成功请以对账文件为准】
					//超过1小时，则认为是用户关闭了浏览器，当作失败处理
					TransactionRecord tranRecord = transactionRecordService.getTransactionRecord(userId, orderNo);
					Date nowDate = new Date();
					long diff = nowDate.getTime() - tranRecord.getCreateTime().getTime();
					long minutes = diff / (1000 * 60);
					if(minutes > 60){
						
						integralRechargeHandler.payFailDo(userId, orderNo ,queryId);
						return result.toJSONString(3603, "充值财币失败", "");
					}else{
						// 注册查询任务
						Map<String, Object> jobmap = new HashMap<>();
						jobmap.put("userId", userId);
						jobmap.put("orderNo",orderNo); 
						jobmap.put("txnTime",txnTime); 
						jobmap.put("jobType","UNION_PAY_ORDER_QUERY_JOB"); 
						kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
						
						return result.toJSONString(3605, "银联支付处理中，请稍后查看交易记录结果", "");
					}
					
				}else{
					//其他应答码为交易失败
					integralRechargeHandler.payFailDo(userId, orderNo ,queryId);
					
					return result.toJSONString(3603, "充值财币失败", "");
				}
			}else if(("34").equals(resmap.get("respCode"))){
				//订单不存在，可认为交易状态未明，需要稍后发起交易状态查询，或依据对账结果为准

				//超过1小时，则认为是用户关闭了浏览器，当作失败处理
				TransactionRecord tranRecord = transactionRecordService.getTransactionRecord(userId, orderNo);
				Date nowDate = new Date();
				long diff = nowDate.getTime() - tranRecord.getCreateTime().getTime();
				long minutes = diff / (1000 * 60);
				if(minutes > 60){
				
					integralRechargeHandler.payFailDo(userId, orderNo, queryId);
					return result.toJSONString(3604, "交易失败", "");
				}else{
					// 注册查询任务
					Map<String, Object> jobmap = new HashMap<>();
					jobmap.put("userId", userId);
					jobmap.put("orderNo",orderNo); 
					jobmap.put("txnTime",txnTime); 
					jobmap.put("jobType","UNION_PAY_ORDER_QUERY_JOB"); 
					kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
					return result.toJSONString(3605, "交易处理中，请稍后再查", "");
				}
				
			}else{//查询交易本身失败，如应答码10/11检查查询报文是否正确
				// 注册查询任务
				Map<String, Object> jobmap = new HashMap<>();
				jobmap.put("userId", userId);
				jobmap.put("orderNo",orderNo); 
				jobmap.put("txnTime",txnTime); 
				jobmap.put("jobType","UNION_PAY_ORDER_QUERY_JOB"); 
				kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
				
				return result.toJSONString(3606, "查询请求失败，请重试", "");
			}
		} catch (Exception e) {
			logger.error("充值财币处理失败，userId:{},tn:{},errMsg:{}",userId,orderNo,e);
			// 注册查询任务
			Map<String, Object> jobmap = new HashMap<>();
			jobmap.put("userId", userId);
			jobmap.put("orderNo",orderNo); 
			jobmap.put("txnTime",txnTime); 
			jobmap.put("jobType","UNION_PAY_ORDER_QUERY_JOB"); 
			kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
			
			return result.toJSONString(3602, "查询请求处理异常", "");
		}
	}
	
	/**
	 * 银联结果验证签名
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: validate 
	 * @param resultString
	 * @return
	 * @date 2016年1月11日 上午10:48:57  
	 * @author ws
	 */
	@RequestMapping(value="unionpay/result/validate/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String validate(String resultString){
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		if(StringUtils.isBlank(resultString)){
			return result.toJSONString(2217,"参数不能为空");
		}
		
		UnionPay unionPay = new UnionPay();
		Boolean checkResult = unionPay.validate(resultString);
		
		return result.toJSONString(0,"",checkResult);
	}
	

	/**
	 * 银联支付成功处理
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySuccessHandler 
	 * @return
	 * @date 2015年12月30日 下午4:42:39  
	 * @author ws
	 */
	/*@RequestMapping(value="unionpay/recharge/success/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String paySuccessHandler(Long userId,String tn){
		ApiResult<String> result = new ApiResult<String>();
		try {
			integralRechargeHandler.paySuccessDo(userId, tn);
			result.setCode(0);
			return JSON.toJSONString(result);
		} catch (Exception e) {
			logger.error("充值财币处理失败，userId:{},tn:{},errMsg:{}",userId,tn,e);
			result.setCode(3602);//错误号
			result.setMessage("充值成功请求处理异常");
			return JSON.toJSONString(result);
		}
	}*/
	
//    @RequestMapping(value="user/1.0", produces="application/json;charset=utf-8")
//	@ResponseBody
//	public String transferToUser(Long fUserId, Long tUserId, Long total, String stall){
//		ApiResult<String> result = new ApiResult<String>();
//		try {
//            if (total < 1) {
//                throw new ApiException(3404, "请输入正确的转账金额");
//            }
//
//            if (fUserId.equals(tUserId)) {
//                throw new ApiException(3404, "不能给自己转账");
//            }
//
//            integralTransferService.transferToOtherUser(fUserId, tUserId, total, stall);
//            result.setCode(0);
//            result.setMessage("转账成功");
//
//            Message message = new Message();
//            message.setIsPush(true);
//            message.setIsSMS(false);
//            message.setIsYellow(false);
//            message.setTitle("财途积分转账");
//            message.setPushInfo("您有" + total + "财币到账");
//            pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, tUserId, message);
//
//            logger.info("transfer {} from {} to {} success", total, fUserId, tUserId);
//            return JSON.toJSONString(result);
//        } catch (ApiException e) {
//            logger.error("transfer {} from {} to {} failure", total, fUserId, tUserId, e);
//            result.setCode(e.getCode());//错误号
//            result.setMessage(e.getMessage());
//            return JSON.toJSONString(result);
//		} catch (Exception e) {
//            logger.error("transfer {} from {} to {} failure", total, fUserId, tUserId, e);
//			result.setCode(3404);//错误号
//            result.setMessage("转账失败");
//			return JSON.toJSONString(result);
//		}
//	}
	
    /**
     * 积分转让
     * @Description: (方法职责详细描述,可空)  
     * @Title: transferToUser 
     * @param fUserId		用户ID
     * @param tUserId		转让用户ID
     * @param total			金额
     * @param stall			摊位号
     * @return
     * @date 2016年3月9日 下午4:52:21  
     * @author xiongbin
     */
    @RequestMapping(value="user/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String transferToUser(Long fUserId, Long tUserId, Long total, String stall){
		ApiResult<String> result = new ApiResult<String>();
		try {
			if(null == fUserId){
				return result.toJSONString(-1, "参数fUserId不能为空");
			}else if(null == tUserId){
				return result.toJSONString(-1, "参数tUserId不能为空");
			}else if(null==total || total<1){
            	throw new ApiException(3404, "请输入正确的转账金额");
            }else if(StringUtils.isBlank(stall)){
				return result.toJSONString(-1, "参数stall不能为空");
            }else if(fUserId.equals(tUserId)){
            	throw new ApiException(3404, "不能给自己转账");
            }

            integralTransferService.transferFreezeIntegral(fUserId, tUserId, total, stall);

            logger.info("transfer {} from {} to {} success", total, fUserId, tUserId);
            return result.toJSONString(0, "转账成功");
        } catch (ApiException e) {
            logger.error("transfer {} from {} to {} failure", total, fUserId, tUserId, e);
            result.setCode(e.getCode());//错误号
            result.setMessage(e.getMessage());
            return JSON.toJSONString(result);
		} catch (Exception e) {
            logger.error("transfer {} from {} to {} failure", total, fUserId, tUserId, e);
			result.setCode(3404);//错误号
            result.setMessage("转账失败");
			return JSON.toJSONString(result);
		}
	}
}
