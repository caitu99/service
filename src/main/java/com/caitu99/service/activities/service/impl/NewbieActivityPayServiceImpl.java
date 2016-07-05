package com.caitu99.service.activities.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.activities.dao.NewbieActivityPayMapper;
import com.caitu99.service.activities.domain.NewbieActivityPay;
import com.caitu99.service.activities.service.NewbieActivityPayService;
import com.caitu99.service.exception.ActivitiesException;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.right.dao.RightCodeMapper;
import com.caitu99.service.right.domain.RightCode;
import com.caitu99.service.right.service.MyRightsService;
import com.caitu99.service.transaction.api.IntegralRechargeHandler;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.unionpay.UnionPay;

@Service
public class NewbieActivityPayServiceImpl implements NewbieActivityPayService{

	private final static Logger logger = LoggerFactory.getLogger(NewbieActivityPayServiceImpl.class);

	@Autowired
	private NewbieActivityPayMapper newbieActivityPayMapper;
	@Autowired
	private IntegralRechargeHandler integralRechargeHandler;
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserService userService;
	@Autowired
	private MyRightsService myRightsService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private TransactionRecordService transactionRecordService;
	@Autowired
	private AccountDetailService accountDetailService;
	@Autowired
	private PushMessageService pushMessageService;
	@Autowired
	private KafkaProducer kafkaProducer;
	@Autowired
	private RightCodeMapper rightCodeMapper;
	@Autowired
	private OrderService orderService;
	
	
	@Override
	public void insert(Long userid, String unionPayNo,Long rmb,Long caibi,Long tubi,Long myRightId,String orderNo) {
		Date now = new Date();
		NewbieActivityPay newbieActivityPay = new NewbieActivityPay();
		newbieActivityPay.setUnionPayNo(unionPayNo);
		newbieActivityPay.setGmtCreate(now);
		newbieActivityPay.setGmtModify(now);
		newbieActivityPay.setStatus(1);
		newbieActivityPay.setUserId(userid);
		newbieActivityPay.setCaibi(caibi);
		newbieActivityPay.setTubi(tubi);
		newbieActivityPay.setRmb(rmb);
		newbieActivityPay.setRightId(myRightId);
		newbieActivityPay.setOrderNo(orderNo);
		
		newbieActivityPayMapper.insertSelective(newbieActivityPay);
	}

	@Override
	@Transactional
	public void insertORupdate(Long userid, String unionPayNo,boolean isPay,User user,Long rmb,Long caibi,Long tubi,Long myRightId,String orderNo) {
		NewbieActivityPay newbieActivityPay = this.selectByUserId(userid);
		if(null == newbieActivityPay){
			this.insert(userid, unionPayNo,rmb,rmb,tubi,myRightId,orderNo);
		}else{
			newbieActivityPay.setUnionPayNo(unionPayNo);
			newbieActivityPay.setCaibi(caibi);
			newbieActivityPay.setTubi(tubi);
			newbieActivityPay.setRmb(rmb);
			newbieActivityPay.setRightId(myRightId);
			newbieActivityPay.setOrderNo(orderNo);
			newbieActivityPay.setStatus(1);
			newbieActivityPay.setGmtModify(new Date());
			newbieActivityPayMapper.updateByPrimaryKeySelective(newbieActivityPay);
		}
		
		if(!isPay){
			userService.updateByPrimaryKeySelective(user);
		}
	}

	@Override
	public NewbieActivityPay selectByUserId(Long userid) {
		return newbieActivityPayMapper.selectByUserId(userid);
	}

	@Override
	public boolean findPayStatus(Long userid,Integer status) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userid", userid);
		map.put("status", status);
		
		List<NewbieActivityPay> list = newbieActivityPayMapper.findPayStatus(map);
		if(null != list && list.size() > 0){
			return true;
		}
		
		return false;
	}
	
	@Override
	@Transactional
	public void paySuccessDo(Long userId,String orderNo, String queryId,boolean flag){
		NewbieActivityPay newbieActivityPay = null;
		if(null == userId){
			newbieActivityPay = this.selectByUnionPayNo(orderNo);
		}else{
			newbieActivityPay = this.selectByUserId(userId);
		}
		
		if(null == newbieActivityPay){
			throw new ActivitiesException(-1,"用户:" + userId + ",未查询到银联支付信息");
		}else if(!newbieActivityPay.getUnionPayNo().equals(orderNo)){
			throw new ActivitiesException(-1,"用户:" + userId + ",银联支付信息有误");
		}else if(newbieActivityPay.getStatus().equals(3)){
			return;
//			throw new ActivitiesException(-1,"用户:" + userId + ",银联支付已完成");
		}else if(newbieActivityPay.getStatus().equals(2)){
//			throw new ActivitiesException(-1,"用户:" + userId + ",银联支付已失败");
			return;
		}
		
		Long caibi = newbieActivityPay.getCaibi();
		Long tubi = newbieActivityPay.getTubi();
		Long rmb = newbieActivityPay.getRmb();
		
		Account account = accountService.selectByUserId(userId);
		if(null == account){
			throw new ActivitiesException(-1,"用户:" + userId + ",财币不足");
		}
		
		Long rightId = newbieActivityPay.getRightId();
		RightCode rightCode = myRightsService.selectByPrimaryKey(rightId);
		if(null == rightCode){
			throw new ActivitiesException(-1, "权益券不足");
		}
		
		if(flag){
			newbieActivityPay.setStatus(3);
			integralRechargeHandler.paySuccessDo(userId, orderNo, queryId);
			
			TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
			transactionRecordDto.setOrderNo("");
			transactionRecordDto.setChannel(3);
			transactionRecordDto.setComment("新手任务支付");
			transactionRecordDto.setInfo("fen生活");//
			transactionRecordDto.setSource(2);//活动
			transactionRecordDto.setTotal(caibi);
			transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNoWithRandom("AN", String.valueOf(userId)));
			transactionRecordDto.setTubi(tubi);
			transactionRecordDto.setType(1);
			transactionRecordDto.setUserId(userId);
			transactionRecordDto.setRmb(rmb);
			// 添加交易记录
			Long recordId = transactionRecordService.saveTransaction(transactionRecordDto);
			// 添加交易明细
			//财币
			accountDetailService.saveAccountDetailTubi(recordId, transactionRecordDto,2);
			//途币
			accountDetailService.saveAccountDetailTubi(recordId, transactionRecordDto,4);
			
			// 更新账户
			//accountService.updateAccount(account, transactionRecordDto, -1L);
			//更新订单
			orderService.finishNewbieActivityOrder(newbieActivityPay.getOrderNo(), userId, true);
			
			
			//加权益
			Integer myRightsExpiresTime = appConfig.myRightsExpiresTime;
			Date expiresTime = new Date();
			Calendar calendar = new GregorianCalendar(); 
			calendar.setTime(expiresTime); 
			calendar.add(calendar.DATE,myRightsExpiresTime);
			expiresTime = calendar.getTime(); 
			
			myRightsService.addMyRights(userId,MyRightsService.RIGHTS_ID_AIR,rightCode.getCode(),rightCode.getCoupon(),expiresTime);
			
			rightCode.setStatus(2L);
			rightCodeMapper.updateByPrimaryKeySelective(rightCode);
			
			//push消息
			this.push(userId, true);
		}else{
			newbieActivityPay.setStatus(2);
			integralRechargeHandler.payFailDo(userId, orderNo ,queryId);
			
			//退钱
			account.setTubi(account.getTubi() + tubi);
			account.setTotalIntegral(account.getTotalIntegral() + caibi);
			account.setAvailableIntegral(account.getAvailableIntegral() + caibi);
			account.setGmtModify(new Date());
			accountService.updateAccountByUserId(account);

			//权益解冻
			rightCode.setStatus(1L);
			rightCodeMapper.updateByPrimaryKeySelective(rightCode);
			//更新订单
			orderService.finishNewbieActivityOrder(newbieActivityPay.getOrderNo(), userId, false);
			
			//push消息
			this.push(userId, false);
		}
		newbieActivityPayMapper.updateByPrimaryKeySelective(newbieActivityPay);
	}

	@Override
	public NewbieActivityPay selectByUnionPayNo(String unionPayNo) {
		return newbieActivityPayMapper.selectByUnionPayNo(unionPayNo);
	}
	
	/**
	 * push消息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: push 
	 * @param userId		用户ID
	 * @param isSuccess		是否支付成功
	 * @date 2016年5月18日 下午5:28:55  
	 * @author xiongbin
	 */
	private void push(Long userId,boolean isSuccess){
		StringBuffer msg = new StringBuffer("新手任务,支付");
		if(isSuccess){
			msg.append("成功.");
		}else{
			msg.append("失败.");
		}
		
		try {
			String pushDescription = "";
			String title = "";
			if(isSuccess){
				pushDescription = Configuration.getProperty("push.activities.newbie.pay.success.description", null);
				title = Configuration.getProperty("push.activities.newbie.pay.success.title", null);
			}else{
				pushDescription = Configuration.getProperty("push.activities.newbie.pay.failure.description", null);
				title = Configuration.getProperty("push.activities.newbie.pay.failure.title", null);
			}
			
			Message message = new Message();
			message.setIsPush(true);
			message.setIsSMS(false);
			message.setIsYellow(false);
			message.setTitle(title);
			message.setPushInfo(pushDescription);
			
			StringBuffer msglog = new StringBuffer(msg);
			msglog.append("push消息通知：userId:").append(userId).append(",message:").append(JSON.toJSONString(message));
			logger.info(msglog.toString());
			
			pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, userId,message);
		} catch (Exception e) {
			msg.append("push消息发送失败：userId:").append(userId);
			logger.error(msg.toString(),e);
		}
	}

	@Override
	@Transactional
	public String pay(Long userid, Long rmb, Long caibi, Long tubi, boolean isPay, User user) {
		Date now = new Date();
		//查询用户财币是否够用
		Account account = accountService.selectByUserId(userid);
		if(null == account){
			throw new ActivitiesException(-1, "您的财币不足");
		}else if(null== account.getAvailableIntegral() || account.getAvailableIntegral() < caibi){
			throw new ActivitiesException(-1, "您的财币不足");
		}else if(null==account.getTubi() || account.getTubi() < tubi){
			throw new ActivitiesException(-1, "您的途币不足");
		}
		
		RightCode rightCode = myRightsService.freezeRightCode(MyRightsService.RIGHTS_ID_AIR);
		if(null == rightCode){
			throw new ActivitiesException(-1, "权益券不足");
		}
		
		account.setTubi(account.getTubi() - tubi);
		account.setTotalIntegral(account.getTotalIntegral() - caibi);
		account.setAvailableIntegral(account.getAvailableIntegral() - caibi);
		account.setGmtModify(now);
		accountService.updateAccountByUserId(account);
		
        //调用银联接口支付
		String[] reslut =  this.unionPay(userid, rmb,caibi,tubi);
		if(null == reslut){
			throw new ActivitiesException(-1, "支付失败,请稍后再试");
		}
        String unionPayNo = reslut[0];
        String orderId = reslut[1];
        
        //生成订单
        String orderNo = orderService.addNewbieActivityOrder(userid, caibi, tubi, rmb);
        
        this.insertORupdate(userid,orderId,isPay,user,rmb,caibi,tubi,rightCode.getId(),orderNo);
        
        return unionPayNo;
	}
	
	/**
	 * 银联支付,返回流水
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: unionPay 
	 * @param userId	用户ID
	 * @param rmb		人民币(分)
	 * @param caibi		财币
	 * @param tubi		途币
	 * @return
	 * @date 2016年5月12日 下午3:44:31  
	 * @author xiongbin
	 */
	@Transactional
	public String[] unionPay(Long userId,Long rmb,Long caibi,Long tubi){
		Map<String,String> resmap = null;
		UnionPay unionPay = new UnionPay();
		Random random = new Random();
		String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String orderId = "caitu99" + userId + random.nextInt(10) + dateStr + random.nextInt(10); //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
		String txnTime = dateStr; //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		String backUrl = appConfig.caituUrl+"/public/integral/recharge/activities/newbie/pay/1.0";//TODO 
		try {
			resmap = unionPay.doPay(rmb.toString(), orderId, txnTime, backUrl);
		} catch (Exception e) {
			logger.error("新手任务银联支付,保存交易记录异常：{}",e);
			return null;
		}
		
		if(null == resmap || null ==resmap.get("respCode")){
			logger.info("新手任务银联接口调用失败，未响应");
			return null;
		}
		
		String tn = "";
		String respCode = resmap.get("respCode");
		if(("00").equals(respCode)){
			try {
				//成功,获取tn号
				tn = resmap.get("tn");
				//保存充值记录
				transactionRecordService.payTransactionRecord(userId, rmb, orderId, tn, txnTime,caibi,tubi);

				// 注册查询任务
				Map<String, Object> jobmap = new HashMap<>();
				jobmap.put("userId", userId);
				jobmap.put("orderNo",orderId); 
				jobmap.put("txnTime",txnTime); 
				jobmap.put("caibi",caibi); 
				jobmap.put("tubi",tubi); 
				jobmap.put("jobType","NEWBIE_ACTIVITY_UNION_PAY_ORDER_QUERY_JOB"); 
				kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
			} catch (Exception e) {
				logger.error("新手任务银联充值财币，保存交易记录异常：{}",e);
				return null;
			}
			
			String[] reslut = new String[2];
			reslut[0] = tn;
			reslut[1] = orderId;
			return reslut;
		}else{
			logger.info("新手任务银联接口调用失败，失败信息：{}",resmap.get("respMsg"));
			return null;
		}
	}
}
