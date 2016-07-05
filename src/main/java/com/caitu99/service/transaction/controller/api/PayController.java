package com.caitu99.service.transaction.controller.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.caitu99.service.activities.service.ActivitiesGoodService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.lianlianpay.config.PartnerConfig;
import com.caitu99.service.lianlianpay.service.LianlianQueryService;
import com.caitu99.service.lianlianpay.utils.LLPayUtil;
import com.caitu99.service.lianlianpay.vo.PaymentInfo;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.Order;
import com.caitu99.service.transaction.domain.OrderItem;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.OrderItemService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.domain.UserBankCard;
import com.caitu99.service.user.service.UserAuthService;
import com.caitu99.service.user.service.UserBankCardService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.date.DateUtil;

@Controller
@RequestMapping("/api/transaction/pay")
public class PayController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(PayController.class);

	@Autowired
	private AccountService accountService;
	@Autowired
	private UserService userService;
	@Autowired
	private TransactionRecordService transactionRecordService;
	@Autowired
	private AccountDetailService accountDetailService;

	@Autowired
	private PushMessageService pushMessageService;
	
	@Autowired
	private OrderService orderService;
	@Autowired
	AppConfig appConfig;
	@Autowired
	UserAuthService userAuthService;

	@Autowired
	KafkaProducer kafkaProducer;
	
	@Autowired
	LianlianQueryService lianlianQueryService;
	
	@Autowired
	TransactionRecordMapper transactionRecordMapper;
	
	@Autowired
	private ActivitiesGoodService activitiesGoodService;
	
	@Autowired
	private OrderItemService orderItemService;
	/**
	 * 
	 * 
	 * @Description: (财币支付)
	 * @Title: integralPay
	 * @return
	 * @date 2015年12月2日 上午10:37:32
	 * @author lhj
	 * @throws CryptoException
	 */
	@RequestMapping(value = "/shop/1.0")
	@ResponseBody
	public String pay(Long userid, String orderno, String paypass,
			Integer setpass) throws CryptoException {
		// 初始化
		ApiResult apiResult = new ApiResult();
		apiResult.setData(false);
		// 支付密码验证
		if (StringUtils.isEmpty(paypass)) {
			apiResult.setCode(2455);
			apiResult.setMessage("支付密码不能为空");
			return JSON.toJSONString(apiResult);
		}
		//查询是否是活动商品   是否在活动时间
		List<OrderItem> orderItems = orderItemService.listByOrderNo(orderno);
		Integer code = activitiesGoodService.checkIsActivitiesGood(orderItems.get(0).getItemId());
		if(code == 1){
			ApiResult<String> inforesult = new ApiResult<String>();
			return inforesult.toJSONString(ApiResultCode.SC_ORDER_ERROR, "活动时间已过，不能支付");
		}
		
		// 用户验证
		User loginUser = userService.selectByPrimaryKey(userid);
		if (1 == setpass) {// 需要设置支付密码
			loginUser.setPaypass(AESCryptoUtil.encrypt(paypass));
			userService.updateByPrimaryKeySelective(loginUser);
		} else {
			if (!AESCryptoUtil.encrypt(paypass).equals(loginUser.getPaypass())) {
				apiResult.setCode(2032);
				apiResult.setMessage("支付密码错误");
				return JSON.toJSONString(apiResult);
			}
		}
		// 业务实现
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setChannel(3);
		transactionRecordDto.setComment("");
		transactionRecordDto.setInfo("fen生活");
		transactionRecordDto.setOrderNo(orderno);
		transactionRecordDto.setPicUrl("");
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
				"SP", String.valueOf(userid)));
		transactionRecordDto.setType(1);
		transactionRecordDto.setUserId(userid);
		AccountResult accountResult = accountService.pay(transactionRecordDto);
		
		//推送消息
		try {
			Order order = orderService.queryOrder(orderno);
			String description =  Configuration.getProperty("push.my.gift.certificate", null);
			String title =  Configuration.getProperty("push.my.gift.certificate.title", null);
			Message message = new Message();
			message.setIsPush(true);
			message.setIsSMS(false);
			message.setIsYellow(false);
			message.setTitle(title);
			message.setPushInfo(String.format(description,order.getName()));
			logger.info("新增消息通知：userId:{},message:{}",userid,JSON.toJSONString(message));
			pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, userid, message);
			
			//积分商城购买成功赠送途币
			Long tubi = CalculateUtils.multiply(order.getPayPrice(), 0.1);
			accountService.giveTubi(userid, tubi);
		} catch (Exception e) {
			logger.error("订单支付完成获得礼券推送消息失败:{}",e);
		}
		
		
		// 数据返回
		apiResult.setCode(accountResult.getCode());
		apiResult.setMessage(accountResult.getResult());
		apiResult.setData(accountResult.isSuccess() ? true : false);
		return JSON.toJSONString(apiResult);
	}

	@RequestMapping(value = "/add/1.0")
	@ResponseBody
	public String add(Long userid, Long integral, String orderNo) {
		// 初始化
		ApiResult apiResult = new ApiResult();
		// 业务实现
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setChannel(1);
		transactionRecordDto.setComment("");
		transactionRecordDto.setInfo("");
		transactionRecordDto.setOrderNo(orderNo);
		transactionRecordDto.setPicUrl("");
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
				"B", String.valueOf(userid)));
		transactionRecordDto.setType(5);
		transactionRecordDto.setUserId(userid);
		AccountResult accountResult = accountService.add(transactionRecordDto);
		// 数据返回
		apiResult.setCode(accountResult.getCode());
		apiResult.setMessage(accountResult.getResult());
		apiResult.setData(accountResult.isSuccess() ? true : false);
		return JSON.toJSONString(apiResult);
	}

	@RequestMapping(value = "/test/1.0")
	@ResponseBody
	public String test(Long userid) throws ParseException {
		ApiResult apiResult = new ApiResult();
		apiResult.setData(true);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<TransactionRecord> list = transactionRecordService
				.selectByUserIdAndTime(userid, sdf.parse("2015-12-02"),
						sdf.parse("2015-12-04"));

		List<AccountDetail> list1 = accountDetailService.selectByUserIdAndTime(
				userid, sdf.parse("2015-12-02"), sdf.parse("2015-12-04"), null).getDatas();

		apiResult.setData(list1);
		return JSON.toJSONString(apiResult);
	}

	@RequestMapping(value = "/account/init/1.0")
	@ResponseBody
	public String initAccount() {
		// 初始化
		ApiResult apiResult = new ApiResult();
		apiResult.setCode(0);
		accountService.initAccount();
		apiResult.setData(true);
		return JSON.toJSONString(apiResult);
	}
	
	
	
	
	

	@Autowired
	private UserBankCardService userBankCardService;
	
	/**
	 * 使用新卡支付
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: commonPayNewCard 
	 * @param userId	用户id
	 * @param orderId	订单号
	 * @param payType	结果页显示标题（如：1支付     2充值）
	 * @return
	 * @date 2016年6月8日 下午4:06:29  
	 * @author ws
	 */
	@RequestMapping(value = "/commonpay/newcard/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String commonPayNewCard(Long userId, String orderId) {
		ApiResult<String> result = new ApiResult<String>();
		String idName = "";
		String idNo = "";
		
		if(null == userId){
			return result.toJSONString(-1, "参数userId不能为空");
		}else if(StringUtils.isBlank(orderId)){
			return result.toJSONString(-1, "参数orderId不能为空");
		}
		
		Order order = orderService.queryOrder(orderId);
		if(null == order){
			return result.toJSONString(-1, "订单不存在");
		}
		
		//判断用户是否已绑过卡，获取自动填充数据
		List<UserBankCard> userCardList = userBankCardService.selectByUserId(userId, 1);
		if(userCardList.size() > 0){
			UserBankCard userCard = userCardList.get(0);
			idName = userCard.getAccName();
			String idName1 = "";
			if(idNo.length() > 1){
				idName1 = idName.substring(idName.length()-1);
			}else{
				idName1 = idName;
			}
			idName = "*"+idName1;
			idNo = userCard.getAccId();
			String idNo1 = "";
			if(idNo.length() > 4){
				idNo1 = idNo.substring(idNo.length()-4);
			}else{
				idNo1 = idNo;
			}
			idNo = "************"+idNo1;
		}
		//返回跳转至银行卡信息录入页面
		String url = "/quick_pay/authentication.html";
		StringBuilder redirct = new StringBuilder();
		redirct.append(appConfig.caituUrl);
		redirct.append(url)
		.append("?orderId=").append(orderId)
		.append("&idName=").append(idName)
		.append("&idNo=").append(idNo)
		.append("&goodsName=").append(order.getName());
		
		logger.info("使用新卡支付:redirct:{}",redirct.toString());
		
		return result.toJSONString(0, "success", redirct.toString());
	}
	
	
	/**
	 * 
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: commonPay 
	 * @param userId	用户id
	 * @param orderId	订单号
	 * @param payPwd	支付密码
	 * @param bankCardId	绑定银行卡id
	 * @param payType	结果页显示标题（如：1支付   2充值）
	 * @return	跳转url
	 * @throws CryptoException
	 * @date 2016年6月8日 下午3:53:37  
	 * @author ws
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/commonpay/pay/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String commonPay(HttpServletRequest req, Long userId, String orderId, String payPwd, Long bankCardId) 
			throws CryptoException, UnsupportedEncodingException {
		// 初始化
		ApiResult apiResult = new ApiResult();
		//验证支付密码，如果原支付密码为空，则设置支付密码
		User loginUser = userService.selectByPrimaryKey(userId);
		if (StringUtils.isBlank(loginUser.getPaypass())) {// 需要设置支付密码
			loginUser.setPaypass(AESCryptoUtil.encrypt(payPwd));
			userService.updateByPrimaryKeySelective(loginUser);
		} else {
			if (!AESCryptoUtil.encrypt(payPwd).equals(loginUser.getPaypass())) {
				apiResult.setCode(2032);
				apiResult.setMessage("支付密码错误");
				return JSON.toJSONString(apiResult);
			}
		}
		
		//获取订单信息，判断支付现金金额
		Order order = orderService.queryOrder(orderId);
		Long rmb = order.getRmb();
		//1、现金为0，则直接支付，返回支付结果页
		if(null == rmb || rmb == 0){
			// 业务实现
			TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
			transactionRecordDto.setChannel(3);
			transactionRecordDto.setComment("");
			transactionRecordDto.setInfo("fen生活");
			transactionRecordDto.setOrderNo(orderId);
			transactionRecordDto.setPicUrl("");
			transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
					"SP", String.valueOf(userId)));
			transactionRecordDto.setType(1);
			transactionRecordDto.setUserId(userId);
			AccountResult accountResult = accountService.payNew(transactionRecordDto);
			
			//推送消息
			try {
				String description =  Configuration.getProperty("push.my.gift.certificate", null);
				String title =  Configuration.getProperty("push.my.gift.certificate.title", null);
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setTitle(title);
				message.setPushInfo(String.format(description,order.getName()));
				logger.info("新增消息通知：userId:{},message:{}",userId,JSON.toJSONString(message));
				pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, userId, message);
				
				//积分商城购买成功赠送途币
				Long tubiGive = CalculateUtils.multiply(order.getPayPrice(), 0.1);
				accountService.giveTubi(userId, tubiGive);
			} catch (Exception e) {
				logger.error("订单支付完成获得礼券推送消息失败:{}",e);
			}
			
			String redirct = createRedirect(order, accountResult);
			
			return apiResult.toJSONString(0, "success", redirct);
		}else{
			//2、现金不为0，则调用连连支付绑定支付，返回连连支付页
			//获取用户绑定信息
			String idNo = "";
			String idName = "";
			String cardNo = "";
			if(null == bankCardId){
				return apiResult.toJSONString(-1, "用户绑定卡号Id不能为空");
			}
			UserBankCard userBankCard = userBankCardService.selectById(bankCardId);
			if(null != userBankCard){
				//1、如果用户已绑定，则idName,idNo使用已绑定的信息
				//2、如果用户未绑定，则使用新的idName,idNo进行支付
				idNo = userBankCard.getAccId();
				idName = userBankCard.getAccName();
				cardNo = userBankCard.getCardNo();
				
				StringBuffer redirect = new StringBuffer(appConfig.caituUrl)
				.append("/quick_pay/toLianlianPay.html?")
				.append("orderId=").append(orderId)
				.append("&payPwd=").append(payPwd)
				.append("&idName=").append(idName)
				.append("&idNo=").append(idNo)
				.append("&cardNo=").append(cardNo);
				
				return apiResult.toJSONString(0, "success", redirect.toString()); 
			}else{
				return apiResult.toJSONString(-1, "未获取到卡号信息");
			}
		}
	}

	
	@RequestMapping(value = "/commonpay/lianlian/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String lianlianPay(HttpServletRequest req, Long userId, String orderId, String payPwd, String idName, String idNo
			, String cardNo) throws CryptoException, UnsupportedEncodingException {

		ApiResult apiResult = new ApiResult();
		if(null == userId||StringUtils.isBlank(orderId)
				||StringUtils.isBlank(payPwd)||StringUtils.isBlank(idName)
				||StringUtils.isBlank(idNo)||StringUtils.isBlank(cardNo)){
			return apiResult.toJSONString(-1, "必要参数不能为空");
		}
		//验证支付密码，如果原支付密码为空，则设置支付密码
		User loginUser = userService.selectByPrimaryKey(userId);
		if (StringUtils.isBlank(loginUser.getPaypass())) {// 需要设置支付密码
			loginUser.setPaypass(AESCryptoUtil.encrypt(payPwd));
			userService.updateByPrimaryKeySelective(loginUser);
		} else {
			if (!AESCryptoUtil.encrypt(payPwd).equals(loginUser.getPaypass())) {
				apiResult.setCode(2032);
				apiResult.setMessage("支付密码错误");
				return JSON.toJSONString(apiResult);
			}
		}
		
		//获取用户绑定信息
		UserAuth authInfo = userAuthService.findByUserId(userId);
		if(null != authInfo){
			//1、如果用户已绑定，则idName,idNo使用已绑定的信息
			//2、如果用户未绑定，则使用新的idName,idNo进行支付
			idNo = authInfo.getAccId();
			idName = authInfo.getAccName();
		}
		
		Order order = orderService.queryOrder(orderId);
		if(null == order){
			return apiResult.toJSONString(-1, "订单不存在");
		}
		
		String cardBindData = lianlianQueryService.getCardBin(cardNo);
		JSONObject cardBinObject = JSON.parseObject(cardBindData);
		if(!cardBinObject.getString("ret_code").equals("0000")){
			return apiResult.toJSONString(1000, "银行卡号有误!");
		}
		Map<String,String> bankInfo = new HashMap<String, String>();
		bankInfo.put("idName", idName);
		bankInfo.put("idNo", idNo);
		bankInfo.put("cardNo", cardNo);
		bankInfo.put("bankName", cardBinObject.getString("bank_name"));
		bankInfo.put("cardType", cardBinObject.getString("card_type"));//2-储蓄卡 3-信用卡
		
		// 业务实现
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setChannel(3);
		transactionRecordDto.setComment(JSON.toJSONString(bankInfo));
		transactionRecordDto.setInfo("fen生活");
		transactionRecordDto.setOrderNo(orderId);
		transactionRecordDto.setPicUrl("");
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
				"SP", String.valueOf(userId)));
		transactionRecordDto.setType(1);
		transactionRecordDto.setUserId(userId);
		//冻结支付
		AccountResult accountResult = accountService.pay2(transactionRecordDto);
		if(!accountResult.isSuccess()){
			return apiResult.toJSONString(-1, accountResult.getResult());
		}
		String backUrl = "";
		if(appConfig.isDevMode){
			backUrl = "http://hongbo1989.eicp.net:13030" + "/public/lianlian/pay/backresponse/1.0";
		}else{
			backUrl = appConfig.caituUrl + "/public/lianlian/pay/backresponse/1.0";
		}
        String redirect =appConfig.caituUrl + "/public/lianlian/lianlian/result/1.0";
		String reqData = prepositPay(req, userId.toString(),order ,idNo,cardNo,idName,backUrl,redirect);
		
		// 注册查询任务
		Map<String, Object> jobmap = new HashMap<>();
		jobmap.put("userId", userId);
		jobmap.put("orderNo",orderId); 
		jobmap.put("url","http://service.caitu99.com/api/transaction/pay/lianlian/timeout/1.0"); 
		jobmap.put("jobType","LIANLIAN_PAY_TIMEOUT"); 
		kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
		
		return apiResult.toJSONString(1111, "toLianLianPay", reqData);
	}
	
	
	/**
     * 卡前置支付处理
     * @param reqMap
     * @param order
	 * @return 
	 * @return 
	 * @throws UnsupportedEncodingException 
     */
    private String prepositPay(HttpServletRequest req, String userId, Order order, String idNo, String cardNo, String idName, String backUrl, String redirect) throws UnsupportedEncodingException
    {
        // 构造支付请求对象
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setVersion(PartnerConfig.VERSION);
        paymentInfo.setOid_partner(PartnerConfig.OID_PARTNER);
        paymentInfo.setUser_id(userId);
        paymentInfo.setSign_type(PartnerConfig.SIGN_TYPE);
        paymentInfo.setBusi_partner(PartnerConfig.BUSI_PARTNER);
        paymentInfo.setNo_order(order.getOrderNo());
        paymentInfo.setDt_order(LLPayUtil.getCurrentDateTimeStr());
        paymentInfo.setName_goods(order.getName());
        //paymentInfo.setInfo_order("");
        paymentInfo.setMoney_order(new Double(order.getRmb()/100.00).toString());
        paymentInfo.setNotify_url(backUrl);
		paymentInfo.setUrl_return(redirect);
        paymentInfo.setValid_order("10080");// 单位分钟，可以为空，默认7天
        paymentInfo.setRisk_item(createRiskItem(userId,order));
        // 从系统中获取用户身份信息
        paymentInfo.setId_no(idNo);
        paymentInfo.setAcct_name(idName);
        paymentInfo.setFlag_modify("1");
        paymentInfo.setCard_no(cardNo);
        paymentInfo.setApp_request("3");
        //paymentInfo.setBg_color("");
        // 加签名
        String sign = LLPayUtil.addSign(JSON.parseObject(JSON
                .toJSONString(paymentInfo)), PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        paymentInfo.setSign(sign);

        return JSON.toJSONString(paymentInfo);
    }

    /**
     * 根据连连支付风控部门要求的参数进行构造风控参数
     * @param order 
     * @return
     */
    private String createRiskItem(String userId, Order order)
    {
    	User user = userService.getById(Long.valueOf(userId));
        JSONObject riskItemObj = new JSONObject();
        //riskItemObj.put("user_info_full_name", "caitu99");
        riskItemObj.put("frms_ware_category", "1999");
        riskItemObj.put("user_info_mercht_userno", userId);
        if(60 == order.getType()){

            riskItemObj.put("frms_charge_phone", order.getMemo());
        }
        riskItemObj.put("user_info_dt_register", DateUtil.DateToString(user.getGmtCreate(), "yyyyMMddHHmmss"));
        return riskItemObj.toString();
    }

	/**
	 * 支付超时（超时设置5分钟）
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: lianlianTimeout 
	 * @param userId
	 * @param orderId
	 * @date 2016年6月14日 上午9:41:50  
	 * @author ws
	 */
    @RequestMapping(value = "/lianlian/timeout/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public void lianlianTimeout(Long userId, String orderId){
    	
    	accountService.payFailureDo(userId, orderId);
    }
    

	private String createRedirect(Order order, AccountResult accountResult) 
			throws UnsupportedEncodingException {
		// 数据返回
		String title = getTitle(order.getType().toString(), accountResult);
		String payInfo = getPayInfo(order.getRmb(),order.getCaibi(),order.getTubi());
		String remark = getRemark(accountResult);
		String url = "/quick_pay/status_success.html";
		StringBuilder redirct = new StringBuilder();
		//redirct.append(appConfig.caituUrl);
		redirct.append(appConfig.caituUrl);
		redirct.append(url)
		.append("?orderId=").append(URLEncoder.encode(order.getOrderNo(),"UTF-8"))
		.append("&goodsName=").append(URLEncoder.encode(order.getName(),"UTF-8"))
		.append("&payTime=").append(DateUtil.DateToString(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"))
		.append("&title=").append(URLEncoder.encode(title,"UTF-8"))
		.append("&remark=").append(URLEncoder.encode(remark,"UTF-8"))
		.append("&payInfo=").append(URLEncoder.encode(payInfo,"UTF-8"));
		return redirct.toString();
	}

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getRemark 
	 * @param accountResult
	 * @return
	 * @date 2016年6月12日 下午3:58:48  
	 * @author ws
	*/
	private String getRemark(AccountResult accountResult) {

		if(accountResult.isSuccess()){
			return "success";
		}else{
			return accountResult.getResult();
		}
	}

	/**
	 * 获取支付结果展示标题
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getTitle 
	 * @param payType 2充值 or 1支付
	 * @param accountResult
	 * @return
	 * @date 2016年6月8日 下午4:17:09  
	 * @author ws
	 */
	private String getTitle(String payType, AccountResult accountResult) {
		String title = "";
		if(payType.equals("1")){//
			if(accountResult.isSuccess()){
				title = "恭喜，支付成功";
			}else{
				title = "抱歉，支付失败";
			}
		}else{
			if(accountResult.isSuccess()){
				title = "恭喜，充值成功";
			}else{
				title = "很遗憾，充值失败";
			}
		}
		return title;
	}
	/**
	 * 获取支付结果展示支付信息
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getPayInfo 
	 * @param rmb
	 * @param caibi
	 * @param tubi
	 * @return
	 * @date 2016年6月8日 下午4:17:23  
	 * @author ws
	 */
	private String getPayInfo(Long rmb, Long caibi, Long tubi) {
		String payInfo = "";
		if(null != rmb && rmb > 0){
			payInfo = payInfo
					+"现金："+rmb;
		}
		if(null != caibi && caibi > 0){
			payInfo = payInfo
					+"，财币："+caibi;
		}
		if(null != tubi && tubi > 0){
			payInfo = payInfo
					+"，途币："+tubi;
		}
		
		if(null == rmb || rmb == 0){
			payInfo = payInfo.substring(1);//去掉最前面的逗号
		}
		
		return payInfo;
	}

    
	

	@RequestMapping(value = "/commonpay/auth/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String lianlianAuth(HttpServletRequest req, Long userId, String orderId, String idName, String idNo
			, String cardNo) throws CryptoException, UnsupportedEncodingException {

		ApiResult apiResult = new ApiResult();
		if(null == userId||StringUtils.isBlank(orderId)||StringUtils.isBlank(idName)
				||StringUtils.isBlank(idNo)||StringUtils.isBlank(cardNo)){
			return apiResult.toJSONString(-1, "必要参数不能为空");
		}
		
		//获取用户绑定信息
		UserAuth authInfo = userAuthService.findByUserId(userId);
		if(null != authInfo){
			//1、如果用户已绑定，则idName,idNo使用已绑定的信息
			//2、如果用户未绑定，则使用新的idName,idNo进行支付
			idNo = authInfo.getAccId();
			idName = authInfo.getAccName();
		}
		
		Order order = orderService.queryOrder(orderId);
		if(null == order){
			return apiResult.toJSONString(-1, "订单不存在");
		}
		
		String cardBindData = lianlianQueryService.getCardBin(cardNo);
		JSONObject cardBinObject = JSON.parseObject(cardBindData);
		if(!cardBinObject.getString("ret_code").equals("0000")){
			return apiResult.toJSONString(-1, "银行卡号有误!");
		}
		Map<String,String> bankInfo = new HashMap<String, String>();
		bankInfo.put("idName", idName);
		bankInfo.put("idNo", idNo);
		bankInfo.put("cardNo", cardNo);
		bankInfo.put("bankName", cardBinObject.getString("bank_name"));
		bankInfo.put("cardType", cardBinObject.getString("card_type"));//2-储蓄卡 3-信用卡
		
		// 业务实现
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setChannel(3);
		transactionRecordDto.setComment(JSON.toJSONString(bankInfo));
		transactionRecordDto.setInfo("实名");
		transactionRecordDto.setOrderNo(orderId);
		transactionRecordDto.setPicUrl("");
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
				"SP", String.valueOf(userId)));
		transactionRecordDto.setType(11);
		transactionRecordDto.setUserId(userId);
		//冻结支付
		AccountResult accountResult = accountService.pay3(transactionRecordDto);
		if(!accountResult.isSuccess()){
			return apiResult.toJSONString(-1, accountResult.getResult());
		}
		String backUrl = "";
		if(appConfig.isDevMode){
			backUrl = "http://hongbo1989.eicp.net:13030" + "/public/lianlian/pay/backresponse/1.0";
		}else{
			backUrl = appConfig.caituUrl + "/public/lianlian/pay/backresponse/1.0";
		}
		String redirect = appConfig.caituUrl + "/public/lianlian/lianlian/auth/1.0";
		String reqData = prepositPay(req, userId.toString(),order ,idNo,cardNo,idName,backUrl,redirect);
		
		// 注册查询任务
		Map<String, Object> jobmap = new HashMap<>();
		jobmap.put("userId", userId);
		jobmap.put("orderNo",orderId); 
		jobmap.put("url","http://service.caitu99.com/api/transaction/pay/lianlian/timeout/1.0"); 
		jobmap.put("jobType","LIANLIAN_PAY_TIMEOUT"); 
		kafkaProducer.sendMessage(JSON.toJSONString(jobmap),appConfig.jobTopic);
		
		return apiResult.toJSONString(1111, "toLianLianPay", reqData);
	}
	
	
	
}
