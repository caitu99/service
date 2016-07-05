package com.caitu99.service.activities.controller.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.activities.domain.Activities;
import com.caitu99.service.activities.domain.NewbieActivityPay;
import com.caitu99.service.activities.service.ActivitiesService;
import com.caitu99.service.activities.service.NewbieActivityPayService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.api.IntegralRechargeHandler;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.controller.vo.UserCardManualVo;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.unionpay.UnionPay;

@Controller
@RequestMapping("/api/activities/newbie")
public class ActivitiesNewbieContriller extends BaseController {

	private Logger logger = LoggerFactory.getLogger(ActivitiesNewbieContriller.class);
	
	@Autowired
	private ActivitiesService activitiesService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private UserCardManualService userCardManualService;
	@Autowired
	private RealizeService realizeService;
	@Autowired
	private NewbieActivityPayService newbieActivityPayService;
	@Autowired
	private TransactionRecordService transactionRecordService;
	@Autowired
	private KafkaProducer kafkaProducer;
	@Autowired
	private IntegralRechargeHandler integralRechargeHandler;
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserService userService;
	
	/**
	 * 新手活动
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: info 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年5月12日 下午12:21:28  
	 * @author xiongbin
	 */
	@RequestMapping(value="info/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String info(Long userid){
        ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		
        if(null == userid){
        	return result.toJSONString(-1, "参数userid不能为空");
        }
		
		Activities activities = activitiesService.selectByPrimaryKey(2L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日");
		//活动时间
		String activitiesTime = sdf.format(activities.getStartTime()) + "-" + sdf2.format(activities.getEndTime());
		//图片
		String banner1 = appConfig.staticUrl + "/apppage/picture/activity/newbie_activity_1.png";
		String banner2 = appConfig.staticUrl + "/apppage/picture/activity/newbie_activity_2.png";
		
		//查询用户是否查询过积分
		Integer isImport = 0;
        List<UserCardManualVo> list = userCardManualService.queryIntegralRemoveRepetition(userid,1);
        if(null!=list && list.size()>0){
        	isImport = 1;
        }else{
    		list = userCardManualService.queryIntegralRemoveRepetition(userid,-1);
    		if(null!=list && list.size()>0){
    			isImport = 1;
    		}else{
    			isImport = 0;
    		}
        }
        
        //查询用户是否积分变现
        boolean isRealize = realizeService.isRealizeSuccessByUserId(userid);
        //是否允许支付
        boolean isPay = false;
        if(isImport.equals(1) && isRealize){
        	isPay = true;
        }
        
        //查询用户最新的支付状态
        Integer status = 0;
        if(isPay){
	        NewbieActivityPay newbieActivityPay = newbieActivityPayService.selectByUserId(userid);
	        if(null != newbieActivityPay){
	        	status = newbieActivityPay.getStatus();
	        }else{
	        	status = 4;
	        }
        }
        
        JSONObject json = new JSONObject();
        json.put("activitiesTime", activitiesTime);
        json.put("banner1", banner1);
        json.put("banner2", banner2);
        json.put("isImport", isImport);
        json.put("isRealize", isRealize ? 1 : 0);
        json.put("isPay", status);
		
        return result.toJSONString(0, "", json);
	}
	
	/**
	 * 新手任务支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userid	用户ID
	 * @param caibi		财币
	 * @param tubi		途币
	 * @param rmb		人民币(分)
	 * @param paypass	支付密码
	 * @return
	 * @date 2016年5月12日 下午4:40:19  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String pay(Long userid,Long caibi,Long tubi,Long rmb,String paypass){
        ApiResult<String> result = new ApiResult<String>();
		
        if(null == userid){
        	return result.toJSONString(-1, "参数userid不能为空");
        }else if(null == caibi){
        	return result.toJSONString(-1, "参数caibi不能为空");
        }else if(null == tubi){
        	return result.toJSONString(-1, "参数tubi不能为空");
        }else if(null == rmb){
        	return result.toJSONString(-1, "参数rmb不能为空");
        }else if(StringUtils.isBlank(paypass)){
        	return result.toJSONString(-1, "参数paypass不能为空");
        }
        
		User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}
		
		//是否设置支付密码
		boolean isPay = true;
		
		try {
			//已设置支付密码
			if(!StringUtils.isBlank(user.getPaypass())){
				if(!AESCryptoUtil.encrypt(paypass).equals(user.getPaypass())){
					return result.toJSONString(-1, "支付密码错误");
				}
			}else{
				isPay = false;
				user.setPaypass(AESCryptoUtil.encrypt(paypass));
			}
		} catch (CryptoException e) {
			logger.error(e.getMessage(),e);
		}
		
		NewbieActivityPay newbieActivityPay = newbieActivityPayService.selectByUserId(userid);
		if(null != newbieActivityPay){
			if(newbieActivityPay.getStatus().equals(3)){
				return result.toJSONString(-1, "您已经支付成功过");
			}else if(newbieActivityPay.getStatus().equals(1)){
				return result.toJSONString(-1, "您已经支付过了");
			}
		}
		
		//支付
		String unionPayNo = newbieActivityPayService.pay(userid,rmb,caibi,tubi,isPay,user);
        
        return result.toJSONString(0, "SUCCESS",unionPayNo);
	}
	
	/**
	 * 新手任务银联支付查询接口
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryOrder 
	 * @param userId		用户ID
	 * @param orderNo		银联流水号
	 * @param txnTime		时间戳
	 * @param caibi			财币
	 * @param tubi			途币
	 * @return
	 * @date 2016年5月12日 下午5:42:22  
	 * @author xiongbin
	 */
	@RequestMapping(value="pay/order/query/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String queryOrder(Long userId,String orderNo,String txnTime,Long caibi,Long tubi){
        ApiResult<String> result = new ApiResult<String>();
		
        if(null == userId){
        	return result.toJSONString(-1, "参数userId不能为空");
        }else if(StringUtils.isBlank(orderNo)){
        	return result.toJSONString(-1, "参数orderNo不能为空");
        }
        
		UnionPay unionPay = new UnionPay();
		try {
			if(StringUtils.isBlank(txnTime)){
				//订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
				txnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			}
			
			Map<String, String> resmap = unionPay.queryOrder(orderNo, txnTime);

			String queryId = resmap.get("queryId");//银联查询Id
			if(("00").equals(resmap.get("respCode"))){//如果查询交易成功
				String origRespCode = resmap.get("origRespCode");
				//处理被查询交易的应答码逻辑
				if(("00").equals(origRespCode) ||("A6").equals(origRespCode)){
					//A6代付交易返回，参与清算，商户应该算成功交易，根据成功的逻辑处理
					//交易成功，更新商户订单状态
					newbieActivityPayService.paySuccessDo(userId, orderNo, queryId,true);
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
						newbieActivityPayService.paySuccessDo(userId, orderNo, queryId,true);
						return result.toJSONString(3603, "充值财币失败", "");
					}else{
						// 注册查询任务
						Map<String, Object> jobmap = new HashMap<>();
						jobmap.put("userId", userId);
						jobmap.put("orderNo",orderNo); 
						jobmap.put("txnTime",txnTime); 
						jobmap.put("caibi",caibi); 
						jobmap.put("tubi",tubi); 
						jobmap.put("jobType","NEWBIE_ACTIVITY_UNION_PAY_ORDER_QUERY_JOB"); 
						kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
						return result.toJSONString(3605, "银联支付处理中，请稍后查看交易记录结果", "");
					}
				}else{
					//其他应答码为交易失败
					newbieActivityPayService.paySuccessDo(userId, orderNo, queryId,false);
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
					newbieActivityPayService.paySuccessDo(userId, orderNo, queryId,false);
					return result.toJSONString(3604, "交易失败", "");
				}else{
					// 注册查询任务
					Map<String, Object> jobmap = new HashMap<>();
					jobmap.put("userId", userId);
					jobmap.put("orderNo",orderNo); 
					jobmap.put("txnTime",txnTime); 
					jobmap.put("caibi",caibi); 
					jobmap.put("tubi",tubi); 
					jobmap.put("jobType","NEWBIE_ACTIVITY_UNION_PAY_ORDER_QUERY_JOB"); 
					kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
					return result.toJSONString(3605, "交易处理中，请稍后再查", "");
				}
				
			}else{//查询交易本身失败，如应答码10/11检查查询报文是否正确
				// 注册查询任务
				Map<String, Object> jobmap = new HashMap<>();
				jobmap.put("userId", userId);
				jobmap.put("orderNo",orderNo); 
				jobmap.put("txnTime",txnTime); 
				jobmap.put("caibi",caibi); 
				jobmap.put("tubi",tubi); 
				jobmap.put("jobType","NEWBIE_ACTIVITY_UNION_PAY_ORDER_QUERY_JOB"); 
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
			jobmap.put("caibi",caibi); 
			jobmap.put("tubi",tubi); 
			jobmap.put("jobType","NEWBIE_ACTIVITY_UNION_PAY_ORDER_QUERY_JOB"); 
			kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
			
			return result.toJSONString(3602, "查询请求处理异常", "");
		}
	}
}
