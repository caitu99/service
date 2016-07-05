package com.caitu99.service.integral.controller.auto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.common.utils.CopyOnWriteMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.integral.domain.AutoFindRecord;
import com.caitu99.service.integral.domain.AutoFindRule;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.Future;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.AutoFindRecordService;
import com.caitu99.service.integral.service.AutoFindRuleService;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.FutureService;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.json.JsonResult;
import com.caitu99.service.utils.string.IdCardValidator;
import com.caitu99.service.utils.string.StrUtil;

@Component
public class AutoFindAdapter {

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	/** 账号类型:手机 */
	private final static Integer LOGIN_TYPE_PHONE = 4;
	/** 账号类型:邮箱 */
	private final static Integer LOGIN_TYPE_EMAIL = 8;
	/** 账号类型:身份证 */
	private final static Integer LOGIN_TYPE_IDCARD = 2;
	
	@Autowired
	private AutoFindRuleService autoFindRuleService;
	
	@Autowired
	private AutoFindRecordService autoFindRecordService;
	
	@Autowired
	private ManualLoginService manualLoginService;
	
	@Autowired
	private UserCardManualService userCardManualService;
	
	@Autowired
	private AutoFindJingDong autoFindJingDong;
	
	@Autowired
	private AutoFindCMB autoFindCMB;
	
	@Autowired
	private AutoFindAirChina autoFindAirChina;
	
	@Autowired
	private AutoFindCU autoFindCU;
	
	@Autowired
	private AutoFindIHG autoFindIHG;
	
	@Autowired
	private AutoFindCsAir autoFindCsAir;
	
	@Autowired
	private PushMessageService pushMessageService;
	
	@Autowired
	private CardTypeService cardTypeService;
	
	@Autowired
	private FutureService futureService;
	
	private static final ExecutorService service = Executors.newFixedThreadPool(10);
	private static final Semaphore semaphore = new Semaphore(10,true);
	
	private static CopyOnWriteArrayList<Long> list = new CopyOnWriteArrayList<Long>();
	private static CopyOnWriteMap<Long, CopyOnWriteArrayList<Condition>> map = new CopyOnWriteMap<Long, CopyOnWriteArrayList<Condition>>();
	private static final ReentrantLock lock = new ReentrantLock();
	
	/**
	 * 用户触发自动发现
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: execute 
	 * @param manualId			积分账户ID
	 * @param userId			用户ID
	 * @param loginAccount		账号
	 * @param password			密码
	 * @date 2015年12月18日 下午4:06:56  
	 * @author xiongbin
	 */
	public void execute(Long manualId,Long userId,String loginAccount,String password) {
		Runnable runnable = new Runnable(){
			public void run() {
				Condition condition = lock.newCondition();
    			if(!list.contains(userId)){
    				list.add(userId);
    				initlock(manualId, userId, loginAccount, password);
    			}else{
    				lock.lock();
        			CopyOnWriteArrayList<Condition> conditionList = map.get(userId);
        			if(null == conditionList){
        				conditionList = new CopyOnWriteArrayList<Condition>();
        			}
        			conditionList.add(condition);
    				map.put(userId, conditionList);
    				try {
						condition.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    				lock.unlock();
    				initlock(manualId, userId, loginAccount, password);
    			}
			}
		};
        service.execute(runnable);
	}
	
	private void initlock(Long manualId,Long userId,String loginAccount,String password){
		try {  
			semaphore.acquire();  
			init(manualId, userId, loginAccount, password);
            list.remove(userId);
    		semaphore.release();  
    	} catch (InterruptedException e) {  
			logger.error(e.getMessage(), e);
			semaphore.release();
    	}

		lock.lock();
    	CopyOnWriteArrayList<Condition> conditionList = map.get(userId);
    	
    	if(null!=conditionList && conditionList.size()>0){
			Condition condition = conditionList.get(0);
			conditionList.remove(condition);
			condition.signal();
    	}
		lock.unlock();
	}
	
	private void init(Long manualId,Long userId,String loginAccount,String password){
		if(null==manualId || null==userId || StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return;
		}
		
		logger.info("【手动查询自动发现】:" + "userId：" + userId + ",开始尝试登陆各个平台.");
		
		//查询用户
		List<ManualLogin> manualLoginList = manualLoginService.selectByUserId(userId);
		
		//push消息
		Map<Integer,Map<Integer,List<String>>> pushMap = new HashMap<Integer,Map<Integer,List<String>>>();
		
		//用户当前登录的账号,尝试登录各个平台
		queryUserSingleAccount(manualId,userId,loginAccount,password,pushMap);
		
		//轮查用户所有的账号
		queryUserAllAccount(manualLoginList,userId,manualId,loginAccount,password,pushMap);
		
		//push消息
		pushMessage(pushMap,userId);
		
		logger.info("【手动查询自动发现】:" + "userId：" + userId + ",尝试登陆各个平台.结束");
	}
	
	/**
	 * push消息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pushMessage 
	 * @param pushMap
	 * @date 2015年12月28日 下午12:28:21  
	 * @author xiongbin
	*/
	private void pushMessage(Map<Integer,Map<Integer,List<String>>> pushMap,Long userId) {
		try {
			if(null == pushMap){
				return;
			}
			
			Map<Integer,List<String>> mapNormal = pushMap.get(AutoFindRecord.STATUS_NORMAL);
			Map<Integer,List<String>> mapExist = pushMap.get(AutoFindRecord.STATUS_LOGINACCUNT_EXIST);
			
			if(mapNormal != null){
				//可登录,信用卡
				List<String> creditPushList = mapNormal.get(1);
				//可登录,商旅卡
				List<String> businessPushList = mapNormal.get(2);
				//可登录,购物卡
				List<String> shopingPushList = mapNormal.get(3);
				
				StringBuffer creditPushMessage = null;
				StringBuffer businessPushMessage = null;
				StringBuffer shopingPushMessage = null;
				
				if(null != creditPushList){
					for(int i=0;i<creditPushList.size();i++){
						String message = creditPushList.get(i);
						if(null == creditPushMessage){
							creditPushMessage = new StringBuffer();
							creditPushMessage.append(message);
						}else{
							creditPushMessage.append("," + message);
						}
					}
				}
				
				if(null != businessPushList){
					for(int i=0;i<businessPushList.size();i++){
						String message = businessPushList.get(i);
						if(null == businessPushMessage){
							businessPushMessage = new StringBuffer();
							businessPushMessage.append(message);
						}else{
							businessPushMessage.append("," + message);
						}
					}
				}
				
				if(null != shopingPushList){
					for(int i=0;i<shopingPushList.size();i++){
						String message = shopingPushList.get(i);
						if(null == shopingPushMessage){
							shopingPushMessage = new StringBuffer();
							shopingPushMessage.append(message);
						}else{
							shopingPushMessage.append("," + message);
						}
					}
				}

//				if(null != creditPushMessage){
//					pushMessage(creditPushMessage.toString(),userId,RedSpot.CREDIT_INTEGRAL);
//				}
//				if(null != businessPushMessage){
//					pushMessage(businessPushMessage.toString(),userId,RedSpot.BUSINESS_INTEGRAL);
//				}
//				if(null != shopingPushMessage){
//					pushMessage(shopingPushMessage.toString(),userId,RedSpot.SHOPING_INTEGRAL);
//				}
				
				StringBuffer pushMessage = null;
				if(null != creditPushMessage){
					if(pushMessage == null){
						pushMessage = new StringBuffer();
						pushMessage.append(creditPushMessage);
					}
				}
				if(null != businessPushMessage){
					if(pushMessage == null){
						pushMessage = new StringBuffer();
						pushMessage.append(businessPushMessage);
					}else{
						pushMessage.append(",").append(businessPushMessage);
					}
				}
				if(null != shopingPushMessage){
					if(pushMessage == null){
						pushMessage = new StringBuffer();
						pushMessage.append(shopingPushMessage);
					}else{
						pushMessage.append(",").append(shopingPushMessage);
					}
				}
				
				if(pushMessage != null){
					pushMessage(pushMessage.toString(),userId,RedSpot.MESSAGE_CENTER);
				}
			}
			
			if(mapExist != null){
				//不登录,信用卡
				List<String> creditSysList = mapExist.get(1);
				//不登录,商旅卡
				List<String> businessSysList = mapExist.get(2);
				//不登录,购物卡
				List<String> shopingSysList = mapExist.get(3);
				
				StringBuffer creditSysMessage = null;
				StringBuffer businessSysMessage = null;
				StringBuffer shopingSysMessage = null;
				
				if(creditSysList != null){
					for(int i=0;i<creditSysList.size();i++){
						String message = creditSysList.get(i);
						if(null == creditSysMessage){
							creditSysMessage = new StringBuffer();
							creditSysMessage.append(message);
						}else{
							creditSysMessage.append("," + message);
						}
					}
				}
				
				if(businessSysList != null){
					for(int i=0;i<businessSysList.size();i++){
						String message = businessSysList.get(i);
						if(null == businessSysMessage){
							businessSysMessage = new StringBuffer();
							businessSysMessage.append(message);
						}else{
							businessSysMessage.append("," + message);
						}
					}
				}
				
				if(shopingSysList != null){
					for(int i=0;i<shopingSysList.size();i++){
						String message = shopingSysList.get(i);
						if(null == shopingSysMessage){
							shopingSysMessage = new StringBuffer();
							shopingSysMessage.append(message);
						}else{
							shopingSysMessage.append("," + message);
						}
					}
				}
				
//				if(null != creditSysMessage){
//					saveMessage(creditSysMessage.toString(),userId,RedSpot.CREDIT_INTEGRAL);
//				}
//				if(null != businessSysMessage){
//					saveMessage(businessSysMessage.toString(),userId,RedSpot.BUSINESS_INTEGRAL);
//				}
//				if(null != shopingSysMessage){
//					saveMessage(shopingSysMessage.toString(),userId,RedSpot.SHOPING_INTEGRAL);
//				}
				
				StringBuffer sysMessage = null;
				if(null != creditSysMessage){
					if(sysMessage == null){
						sysMessage = new StringBuffer();
						sysMessage.append(creditSysMessage);
					}
				}
				if(null != businessSysMessage){
					if(sysMessage == null){
						sysMessage = new StringBuffer();
						sysMessage.append(businessSysMessage);
					}else{
						sysMessage.append(",").append(businessSysMessage);
					}
				}
				if(null != shopingSysMessage){
					if(sysMessage == null){
						sysMessage = new StringBuffer();
						sysMessage.append(shopingSysMessage);
					}else{
						sysMessage.append(",").append(shopingSysMessage);
					}
				}
				
				if(sysMessage != null){
					saveMessage(sysMessage.toString(),userId,RedSpot.MESSAGE_CENTER);
				}
			}
		} catch (Exception e) {
			logger.error("发送自动发现push消息通知失败:" + e.getMessage(),e);
		}
	}
	
	/**
	 * 保存消息到消息中心
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveMessage 
	 * @param pushMessage
	 * @param userId
	 * @throws Exception
	 * @date 2015年12月29日 下午8:07:24  
	 * @author Hongbo Peng
	 */
	private void saveMessage(String pushMessage,Long userId,RedSpot redSpot) throws Exception{
		String pushDescription = Configuration.getProperty("push.auto.find.pwd.error.description", null);
		String title = Configuration.getProperty("push.auto.find.title", null);
		Message message = new Message();
		message.setIsPush(false);
		message.setIsSMS(false);
		message.setIsYellow(false);
		message.setTitle(title);
		message.setPushInfo(String.format(pushDescription,pushMessage));
		logger.info("自动发现push消息通知：userId:{},message:{}", userId,JSON.toJSONString(message));
		pushMessageService.saveMessage(redSpot, userId,message);
	}
	
	/**
	 * push消息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pushMessage 
	 * @param pushMessage
	 * @param userId
	 * @throws Exception
	 * @date 2015年12月29日 下午8:07:49  
	 * @author Hongbo Peng
	 */
	private void pushMessage(String pushMessage,Long userId,RedSpot redSpot) throws Exception{
		String pushDescription = Configuration.getProperty("push.auto.find.description", null);
		String sms = Configuration.getProperty("push.auto.find.sms", null);
		String title = Configuration.getProperty("push.auto.find.title", null);
		Message message = new Message();
		message.setIsPush(true);
		message.setIsSMS(true);
		message.setIsYellow(false);
		message.setSmsInfo(String.format(sms,pushMessage));
		message.setTitle(title);
		message.setPushInfo(String.format(pushDescription,pushMessage));
		logger.info("自动发现push消息通知：userId:{},message:{}", userId,JSON.toJSONString(message));
		pushMessageService.pushMessage(redSpot, userId,message);
	}

	/**
	 * 手动查询自动发现,单个账号
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryUserSingleAccount 
	 * @param manualId
	 * @param userId
	 * @param loginAccount
	 * @param password
	 * @date 2015年12月21日 下午5:23:07  
	 * @author xiongbin
	 */
	private void queryUserSingleAccount(Long manualId,Long userId,String loginAccount,String password,Map<Integer,Map<Integer,List<String>>> pushMap){
		logger.info("【手动查询自动发现】:" + "userId：" + userId + ",loginAccount:" + loginAccount + ",开始尝试登陆各个平台.");
		
		Integer loginType = null;
		
		//判断账号类型
		if(StrUtil.isPhone(loginAccount)){
			loginType = LOGIN_TYPE_PHONE;
		}else if(StrUtil.isEmail(loginAccount)){
			loginType = LOGIN_TYPE_EMAIL;
		}else if(IdCardValidator.valideIdCard(loginAccount)){
			loginType = LOGIN_TYPE_IDCARD;
		}else{
			logger.info("【手动查询自动发现失败】:" + "loginAccount:" + loginAccount + ",当前不支持此类型账号自动发现");
			return;
		}
		
		if(StringUtils.isBlank(password)){
			return;
		}
		
		//查询账号是否已存在
//		AutoFindRecord autoFindRecordQuery = new AutoFindRecord();
//		autoFindRecordQuery.setUserId(userId);
//		autoFindRecordQuery.setLoginAccount(loginAccount);
//		//autoFindRecordQuery.setPassword(password);
//		
//		List<AutoFindRecord> autoFindRecordList = autoFindRecordService.selectPageList(autoFindRecordQuery);
//		
//		if(autoFindRecordList!=null && autoFindRecordList.size()>0){
//			if(password.equals(autoFindRecordList.get(0).getPassword())){
//				logger.info("【手动查询自动发现失败】:" + "此账号已记录过,不进行处理");
//				return;
//			}
//			logger.info("【手动查询自动发现】:" + "此账号已记录过,但密码有做修改,重新验证");
//		}

//		AutoFindRule autoFindRuleImport = autoFindRuleService.selectByManualId(manualId);
//		Integer typeImport = autoFindRuleImport.getType();
//		
//		//积分账户类型若为银行,但账号不是身份证不进行处理
//		if(AutoFindRule.TYPE_BANK.equals(typeImport) && !LOGIN_TYPE_IDCARD.equals(loginType)){
//			logger.info("【手动查询自动发现失败】:" + "当前不支持非身份证的银行自动发现");
//			return;
//		}

		List<AutoFindRule> list = autoFindRuleService.list();
		
		for(AutoFindRule autoFindRule : list){
			Long autoManualId = autoFindRule.getManualId();
			
			//排除自己
			if(autoManualId.equals(manualId)){
				continue;
			}
			
			//判断用户是否有此平台积分数据
			boolean flag = checkManualQuery(userId,autoManualId);
			if(flag){
				logger.info("【手动查询自动发现】:" + "userId:" + userId + ",manualId:" + autoManualId + ",此平台积分数据已存在,不进行自动发现");
				continue;
			}
			
			Integer type = autoFindRule.getType();
			
//			//银行类型的账号必须是身份证
//			if(AutoFindRule.TYPE_BANK.equals(type) && LOGIN_TYPE_IDCARD.equals(loginType)){
//				verify(userId,autoManualId,loginAccount,password,2,loginType);
//			}else{
//				//安全账户类型和普通账号类型,实现方式一样
//				verify(userId,autoManualId,loginAccount,password,2,loginType);
//			}
			
			//银行类型的账号必须是身份证
			if(AutoFindRule.TYPE_BANK.equals(type)){
				if(LOGIN_TYPE_IDCARD.equals(loginType)){
					logger.info("【手动查询自动发现】:" + "userId：" + userId + ",manualId:" + autoManualId + "积分账户是银行类型,并且登录账号为身份证");
					verify(userId,autoManualId,loginAccount,password,2,loginType,pushMap);
				}else{
					logger.info("【手动查询自动发现】:" + "userId：" + userId + ",manualId:" + autoManualId + "积分账户是银行类型,但登录账号非身份证.暂时不支持此登录方式");
				}
			}else{
				//安全账户类型和普通账号类型,实现方式一样
				verify(userId,autoManualId,loginAccount,password,2,loginType,pushMap);
			}
		}
		
		logger.info("【手动查询自动发现】:" + "userId：" + userId + ",loginAccount:" + loginAccount + ",尝试登陆各个平台结束.");
	}
	
	/**
	 * 轮查用户所有的账号
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryUserAllAccount 
	 * @param manualLoginList
	 * @param userId
	 * @param manualId
	 * @param loginAccount
	 * @param password
	 * @date 2015年12月21日 下午4:14:48  
	 * @author xiongbin
	*/
	private void queryUserAllAccount(List<ManualLogin> manualLoginList,Long userId, Long manualId, String loginAccount, String password,Map<Integer,Map<Integer,List<String>>> pushMap) {
		logger.info("【手动查询自动发现】:" + "userId：" + userId + ",开始轮查用户所有的账号");
		
		if(null!=manualLoginList && manualLoginList.size()>0){
			for(ManualLogin manualLogin : manualLoginList){
				String loginAccountManualLogin = manualLogin.getLoginAccount();
				String passwordManualLogin = manualLogin.getPassword();
				Long manualIdManualLogin = manualLogin.getManualId();
				
				//没有密码的账号不进行轮查
				if(StringUtils.isBlank(passwordManualLogin)){
					logger.info("【手动查询自动发现】:" + "userId：" + userId + ",账号:" + loginAccountManualLogin + ",密码为空.不进行轮查");
					continue;
				}else if(loginAccount.equals(loginAccountManualLogin) && password.equals(passwordManualLogin) && manualId.equals(manualIdManualLogin)){
					//用户刚刚输入的账号不进行轮查
					continue;
				}
				
				//轮查账号
				queryUserSingleAccount(manualIdManualLogin,userId,loginAccountManualLogin,passwordManualLogin,pushMap);
			}
		}
		
		logger.info("【手动查询自动发现】:" + "userId：" + userId + "轮查用户所有的账号结束");
	}

	/**
	 * 执行全部自动发现
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: executeAll 
	 * @date 2015年12月18日 下午5:00:50  
	 * @author xiongbin
	 */
	public void executeAll(){
		new Thread(new Runnable() {
			public void run() {
				initAll();
			}
		}).start();
	}
	
	private void initAll(){
		logger.info("【手动查询自动发现】:" + "开始检索所有用户登录数据");
		
		Integer start = 0;
		//每次执行1000条
		Integer pageSize = 5;
		
		executeAll(start,pageSize);
	}
	
	/**
	 * 执行全部自动发现
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: executeAll 
	 * @param start			当前页起始行
	 * @param pageSize		每页多少行
	 * @date 2015年12月18日 下午5:01:18  
	 * @author xiongbin
	 */
	private void executeAll(Integer start,Integer pageSize){
		logger.info("【手动查询自动发现】:" + "开始检索所有用户登录数据.start:" + start + ",pageSize:" + pageSize);

		Integer count = 0;
		
		List<ManualLogin> list = manualLoginService.selectPageList(start, pageSize);
		
		Map<Long,Map<Integer,Map<Integer,List<String>>>> pushMaps = new HashMap<Long,Map<Integer,Map<Integer,List<String>>>>();
		
		for(ManualLogin manualLogin : list){
			Long manualId = manualLogin.getManualId();
			Long userId = manualLogin.getUserId();
			String loginAccount = manualLogin.getLoginAccount();
			String password = manualLogin.getPassword();

			//push消息
			Map<Integer,Map<Integer,List<String>>> pushMap = new HashMap<Integer,Map<Integer,List<String>>>();
			
			//执行自动发现
			queryUserSingleAccount(manualId,userId,loginAccount,password,pushMap);
			
			Map<Integer,Map<Integer,List<String>>> pushMapUser = pushMaps.get(userId);
			if(null == pushMapUser){
				pushMaps.put(userId, pushMap);
			}else{
				pushMapUser.putAll(pushMap);
				pushMaps.put(userId, pushMapUser);
			}
			
			count++;
		}
		//执行数量小于pageSize,表示已全部执行完成
		if(count < pageSize){
			Iterator<Map.Entry<Long,Map<Integer,Map<Integer,List<String>>>>> iterator = pushMaps.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<Long,Map<Integer,Map<Integer,List<String>>>>  i = iterator.next();
				pushMessage(i.getValue(), i.getKey());
			}
			
			return;
		}
		//继续执行
		executeAll(start+pageSize,pageSize);
	}
	
	/**
	 * 自动发现
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: verifyCU 
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param loginAccount		账号
	 * @param password			密码
	 * @param count				图片验证码错误次数
	 * @param type				账户类型
	 * @date 2015年12月15日 下午5:58:16  
	 * @author xiongbin
	 */
	private void verify(Long userId,Long manualId,String loginAccount,String password,Integer count,Integer type,Map<Integer,Map<Integer,List<String>>> pushMap){
		try {
			Date now = new Date();
			
			//查询用户登录记录里账号是否已存在
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(null != oldManualLogin){
				if(password.equals(oldManualLogin.getPassword())){
					logger.info("【手动查询自动发现失败】:" + "此账号已记录过,不进行处理");
					return;
				}else{
					logger.info("【手动查询自动发现】:" + "此账号已记录过,但密码有做修改,重新验证");
				}
			}else{
				logger.info("【手动查询自动发现】:" + "userId:" + userId + ",manualId:" + manualId + ",此账号用户未登录过,进行验证");
			}
			
			
			//查询自动发现记录里账号是否已存在
			AutoFindRecord autoFindRecord = new AutoFindRecord();
			autoFindRecord.setUserId(userId);
			autoFindRecord.setLoginAccount(loginAccount);
			autoFindRecord.setManualId(manualId);
			//autoFindRecordQuery.setPassword(password);
			
			autoFindRecord = autoFindRecordService.getBySelective(autoFindRecord);
			
			boolean flag = false;
			if(autoFindRecord != null){
				if(password.equals(autoFindRecord.getPassword())){
					logger.info("【手动查询自动发现失败】:" + "此账号已记录过,不进行处理");
					return;
				}

				logger.info("【手动查询自动发现】:" + "此账号已记录过,但密码有做修改,重新验证");
				flag = true;
			}
			
			String reslut = polling(userId,manualId,loginAccount,password,count,type);
			Integer status = null;
			
			if(StringUtils.isNotBlank(reslut)){
				status = checkResult(reslut);
			}
			
			if(status != null){
				if(flag){
					autoFindRecord.setGmtModify(now);
					autoFindRecord.setStatus(status);
				}else{
					autoFindRecord = new AutoFindRecord();
					autoFindRecord.setManualId(manualId);
					autoFindRecord.setLoginAccount(loginAccount);
					autoFindRecord.setGmtCreate(now);
					autoFindRecord.setGmtModify(now);
					autoFindRecord.setType(type);
					autoFindRecord.setUserId(userId);
					autoFindRecord.setStatus(status);
				}
				
				if(AutoFindRecord.STATUS_NORMAL.equals(status)){
					autoFindRecord.setPassword(password);
				}else if(!flag){
					autoFindRecord.setPassword("");
				}
				
				autoFindRecordService.insertORupdate(autoFindRecord);
				
				//保存用户手动查询积分数据
				Integer cardTypeId = saveUserCardManual(userId,manualId,loginAccount,password,reslut,status);
				
				//保存用户登录记录
				saveManualLogin(userId,manualId,loginAccount,password,status);
				
				//push消息
				String pushMessage = JsonResult.getResult(reslut, "pushMessage");
				if(AutoFindRecord.STATUS_NORMAL.equals(status)){
					Map<Integer,List<String>> map = pushMap.get(AutoFindRecord.STATUS_NORMAL);
					if(null == map){
						map = new HashMap<Integer,List<String>>();
					}
					List<String> list = map.get(cardTypeId);
					if(list == null){
						list = new ArrayList<String>();
					}
					
					if(!list.contains(pushMessage)){
						list.add(pushMessage);
					}
					map.put(cardTypeId, list);
					pushMap.put(AutoFindRecord.STATUS_NORMAL, map);
				}else if(AutoFindRecord.STATUS_LOGINACCUNT_EXIST.equals(status)){
					Map<Integer,List<String>> map = pushMap.get(AutoFindRecord.STATUS_LOGINACCUNT_EXIST);
					if(null == map){
						map = new HashMap<Integer,List<String>>();
					}
					List<String> list = map.get(cardTypeId);
					if(list == null){
						list = new ArrayList<String>();
					}
					if(!list.contains(pushMessage)){
						list.add(pushMessage);
					}
					map.put(cardTypeId, list);
					pushMap.put(AutoFindRecord.STATUS_LOGINACCUNT_EXIST, map);
				}
			}
		} catch (Exception e) {
			logger.error("【手动查询自动发现失败】:" + "保存自动发现数据失败:" + e.getMessage(),e);
		}
	}
	
	/**
	 * 保存用户手动查询积分数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveUserCardManual 
	 * @param userId
	 * @param manualId
	 * @param loginAccount
	 * @param password
	 * @param reslut
	 * @date 2015年12月22日 上午10:33:01  
	 * @author xiongbin
	*/
	private Integer saveUserCardManual(Long userId, Long manualId,String loginAccount, String password, String reslut,Integer status) {
		logger.info("【手动查询自动发现】:" + "userId:" + userId + ",manualId:" + manualId + ",loginAccount:" + loginAccount + ",开始保存用户积分数据");
		
		boolean flag = JsonResult.checkResult(reslut,ApiResultCode.SUCCEED);
		String error = JsonResult.getResult(reslut,"error");
		Integer cardTypeId = null;
		
		try {
			switch (manualId.intValue()) {
				//淘宝
				case 1:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//招行
				case 2:{
					JSONObject jsonData = new JSONObject();
					jsonData.put("integral", -1);
					jsonData.put("name", loginAccount);
					jsonData.put("account", "");
					
					JSONObject json = new JSONObject();
					json.put("data", jsonData.toJSONString());
					
					if(AutoFindRecord.STATUS_NORMAL.equals(status)){
						if(flag){
							logger.info("【手动查询自动发现】:" + "招行登录成功,可以获取积分数据.");
							cardTypeId = autoFindCMB.saveDataAutoFind(userId, loginAccount, JSON.parseObject(reslut));
						}else{
							logger.info("【手动查询自动发现】:" + "招行登录失败,不可以获取积分数据.");
//							autoFindCMB.saveDataAutoFind(userId, loginAccount, json);
						}
					}else if(AutoFindRecord.STATUS_LOGINACCUNT_EXIST.equals(status)){
						logger.info("【手动查询自动发现】:" + "招行登录失败,不可以获取积分数据.");
//						autoFindCMB.saveDataAutoFind(userId, loginAccount, json);
					}
				}
					break;
				//京东
				case 3:{
					JSONObject jsonData = new JSONObject();
					jsonData.put("jindou", -1);
					jsonData.put("name", loginAccount);
					
					JSONObject json = new JSONObject();
					json.put("data", jsonData.toJSONString());
					
					if(AutoFindRecord.STATUS_NORMAL.equals(status)){
						if(flag){
							logger.info("【手动查询自动发现】:" + "京东登录成功,可以获取积分数据.");
							cardTypeId = autoFindJingDong.saveDataAutoFind(userId, loginAccount, JSON.parseObject(reslut));
						}else{
							logger.info("【手动查询自动发现】:" + "京东登录失败,不可以获取积分数据.");
							cardTypeId = autoFindJingDong.saveDataAutoFind(userId, loginAccount, json);
						}
					}else if(AutoFindRecord.STATUS_LOGINACCUNT_EXIST.equals(status)){
						logger.info("【手动查询自动发现】:" + "京东登录失败,不可以获取积分数据.");
						cardTypeId = autoFindJingDong.saveDataAutoFind(userId, loginAccount, json);
					}
				}
					break;
				//天翼
				case 4:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//国航
				case 5:{
					JSONObject jsonData = new JSONObject();
					jsonData.put("thisInvalid", -1);
					jsonData.put("nextInvalid", -1);
					jsonData.put("name", loginAccount);
					jsonData.put("available", -1);
					jsonData.put("account", "--");
					
					JSONObject json = new JSONObject();
					json.put("data", jsonData.toJSONString());
					
					if(AutoFindRecord.STATUS_NORMAL.equals(status)){
						if(flag){
							logger.info("【手动查询自动发现】:" + "国航登录成功,可以获取积分数据.");
							cardTypeId = autoFindAirChina.saveDataAutoFind(userId, loginAccount, JSON.parseObject(reslut));
						}else{
							logger.info("【手动查询自动发现】:" + "国航登录失败,不可以获取积分数据.");
							cardTypeId = autoFindAirChina.saveDataAutoFind(userId, loginAccount, json);
						}
					}else if(AutoFindRecord.STATUS_LOGINACCUNT_EXIST.equals(status)){
						if(!ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR.equals(error)){
							logger.info("【手动查询自动发现】:" + "国航登录失败,不可以获取积分数据.");
							cardTypeId = autoFindAirChina.saveDataAutoFind(userId, loginAccount, json);
						}
					}
				}
					break;
				//联通
				case 6:{
					JSONObject jsonData = new JSONObject();
					jsonData.put("integral", -1);
					jsonData.put("phone", loginAccount);
					
					JSONObject json = new JSONObject();
					json.put("data", jsonData.toJSONString());
					
					if(AutoFindRecord.STATUS_NORMAL.equals(status)){
						if(flag){
							logger.info("【手动查询自动发现】:" + "联通登录成功,可以获取积分数据.");
							cardTypeId = autoFindCU.saveDataAutoFind(userId,JSON.parseObject(reslut));
						}else{
							logger.info("【手动查询自动发现】:" + "联通登录失败,不可以获取积分数据.");
//							cardTypeId = autoFindCU.saveDataAutoFind(userId,json);
						}
					}else if(AutoFindRecord.STATUS_LOGINACCUNT_EXIST.equals(status)){
						if(!ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR.equals(error)){
							logger.info("【手动查询自动发现】:" + "联通登录失败,不可以获取积分数据.");
//							cardTypeId = autoFindCU.saveDataAutoFind(userId,json);
						}
					}
				}
					break;
				//南航
				case 7:{
					JSONObject jsonData = new JSONObject();
					jsonData.put("integral", -1);
					jsonData.put("name", loginAccount);
					jsonData.put("card", "--");
					
					JSONObject json = new JSONObject();
					json.put("data", jsonData.toJSONString());
					
					if(AutoFindRecord.STATUS_NORMAL.equals(status)){
						if(flag){
							logger.info("【手动查询自动发现】:" + "南航登录成功,可以获取积分数据.");
							cardTypeId = autoFindCsAir.saveDataAutoFind(userId,loginAccount,JSON.parseObject(reslut));
						}else{
							logger.info("【手动查询自动发现】:" + "南航登录失败,不可以获取积分数据.");
//							autoFindCsAir.saveDataAutoFind(userId,loginAccount,json);
						}
					}else if(AutoFindRecord.STATUS_LOGINACCUNT_EXIST.equals(status)){
						logger.info("【手动查询自动发现】:" + "南航登录失败,不可以获取积分数据.");
//						autoFindCsAir.saveDataAutoFind(userId,loginAccount,json);
					}
				}
					break;
				//移动
				case 8:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//洲际
				case 9:{
					JSONObject jsonData = new JSONObject();
					jsonData.put("jifen", -1);
					jsonData.put("account", loginAccount);
					jsonData.put("username", loginAccount);
					
					JSONObject json = new JSONObject();
					json.put("data", jsonData.toJSONString());
					
					if(AutoFindRecord.STATUS_NORMAL.equals(status)){
						if(flag){
							logger.info("【手动查询自动发现】:" + "洲际登录成功,可以获取积分数据.");
							cardTypeId = autoFindIHG.saveDataAutoFind(userId, JSON.parseObject(reslut));
						}else{
							logger.info("【手动查询自动发现】:" + "洲际登录失败,不可以获取积分数据.");
//							autoFindIHG.saveDataAutoFind(userId, json);
						}
					}else if(AutoFindRecord.STATUS_LOGINACCUNT_EXIST.equals(status)){
						logger.info("【手动查询自动发现】:" + "洲际登录失败,不可以获取积分数据.");
//						autoFindIHG.saveDataAutoFind(userId, json);
					}
				}
					break;
				//花旗银行
				case 10:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//铂涛会
				case 11:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//物美
				case 12:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				default:
					logger.info("【手动查询自动发现失败】:" + "暂不支持此积分账户");
					break;
			}
		} catch (Exception e) {
			logger.info("【手动查询自动发现失败】:" + "userId：" + userId + ",尝试登陆失败." + e.getMessage(),e);
		}
		
		logger.info("【手动查询自动发现】:" + "userId:" + userId + ",manualId:" + manualId + ",loginAccount:" + loginAccount + ",保存用户积分数据结束");
		
		try {
			CardType cardType = cardTypeService.selectByManualId(manualId);
			if(cardType != null){
				cardTypeId = cardType.getTypeId();
			}
		} catch (Exception e) {
			logger.warn("查询卡片类型出错:" + e.getMessage(),e);
		}
		
		return cardTypeId;
	}

	/**
	 * 保存用户登录记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveManualLogin 
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param loginAccount		账户
	 * @param password			密码
	 * @param type				账号类型
	 * @date 2015年12月18日 下午2:31:06  
	 * @author xiongbin
	 */
	private void saveManualLogin(Long userId,Long manualId,String loginAccount,String password,Integer status){
		ManualLogin manualLogin = new ManualLogin(userId,manualId,loginAccount,null,null,null);
		ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
		
		Date now = new Date();
		
		Integer type = null;
		if(StrUtil.isPhone(loginAccount)){
			type = ManualLogin.TYPE_PHONE;
		}else if(StrUtil.isEmail(loginAccount)){
			type = ManualLogin.TYPE_EMAIL;
		}else if(IdCardValidator.valideIdCard(loginAccount)){
			type = ManualLogin.TYPE_IDENTITY_CARD;
		}else{
			type = ManualLogin.TYPE_LOGIN_ACCOUNT;
		}
		
		if(null == oldManualLogin){
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			manualLogin.setType(type);
			manualLogin.setGmtCreate(now);
			manualLogin.setGmtModify(now);
			
			if(AutoFindRecord.STATUS_NORMAL.equals(status)){
				manualLogin.setPassword(password);
			}else{
				manualLogin.setPassword("");
			}
			
			manualLoginService.insert(manualLogin);
		}else{
			oldManualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			oldManualLogin.setType(type);
			oldManualLogin.setGmtModify(now);
			
			if(AutoFindRecord.STATUS_NORMAL.equals(status)){
				oldManualLogin.setPassword(password);
			}else{
				oldManualLogin.setPassword("");
			}
			
			manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
		}
	}
	
	/**
	 * 登陆各个平台
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: polling 
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param loginAccount		账号
	 * @param password			密码
	 * @param count				图片验证码错误次数
	 * @date 2015年12月15日 下午4:45:44  
	 * @author xiongbin
	 */
	private String polling(Long userId,Long manualId,String loginAccount,String password,Integer count,Integer type){
		String reslut = null;
		
		boolean flag = verifyLoginAccountSupport(manualId,type);
		if(!flag){
			logger.info("【手动查询自动发现】: manualId : {} , 不支持 loginAccount：{}",manualId,loginAccount);
			return null;
		}
		
		try {
			switch (manualId.intValue()) {
				//淘宝
				case 1:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//招行
				case 2:{
					reslut = autoFindCMB.login(userId, loginAccount, password, count);
					reslut = pushMessage(reslut,"招商银行");
				}
					break;
				//京东
				case 3:{
					reslut = autoFindJingDong.login(userId, loginAccount, password, count);
					reslut = pushMessage(reslut,"京东");
				}
					break;
				//天翼
				case 4:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//国航
				case 5:{
					reslut = autoFindAirChina.login(userId, loginAccount, password, count);
					reslut = pushMessage(reslut,"中国国航");
				}
					break;
				//联通
				case 6:{
					reslut = autoFindCU.login(userId, loginAccount, password, count);
					reslut = pushMessage(reslut,"联通");
				}
					break;
				//南航
				case 7:{
					reslut = autoFindCsAir.login(userId, loginAccount, password, count);
					reslut = pushMessage(reslut,"南方航空");
				}
					break;
				//移动
				case 8:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//洲际
				case 9:{
					reslut = autoFindIHG.login(userId, loginAccount, password,count);
					reslut = pushMessage(reslut,"IHG");
				}
					break;
				//花旗银行
				case 10:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//铂涛会
				case 11:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				//物美
				case 12:{
					logger.info("【手动查询自动发现】:" + "暂不支持此积分账户");
				}
					break;
				default:
					logger.info("【手动查询自动发现失败】:" + "暂不支持此积分账户");
					break;
			}
		} catch (Exception e) {
			logger.info("【手动查询自动发现失败】:" + "userId：" + userId + ",尝试登陆失败." + e.getMessage(),e);
		}
		
		return reslut;
	}
	
	/**
	 * 判断该平台是否支持该账号类型
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: verifyLoginAccountSupport 
	 * @param manualId		积分平台ID
	 * @param type			账号类型
	 * @return
	 * @date 2016年1月14日 下午6:14:26  
	 * @author xiongbin
	 */
	private boolean verifyLoginAccountSupport(Long manualId,Integer type){
		List<Future> futureListAccount = futureService.findListByManualIdType(manualId,Future.TYPE_LOGIN_ACCOUNT);
		if(null!=futureListAccount && futureListAccount.size()>0){
			for(Future future : futureListAccount){
				if(future.getId().intValue() == type.intValue()){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 处理各个平台push消息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pushMessage 
	 * @param reslut
	 * @param string
	 * @return
	 * @date 2015年12月28日 上午11:57:07  
	 * @author xiongbin
	*/
	private String pushMessage(String reslut, String message) {
		if(StringUtils.isNotBlank(reslut)){
			JSONObject json = JSON.parseObject(reslut);
			json.put("pushMessage", message);
			return json.toJSONString();
		}
		return reslut;
	}

	/**
	 * 处理登录返回值
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkResult 
	 * @param reslut		登录返回值
	 * @return
	 * @date 2015年12月18日 上午9:59:57  
	 * @author xiongbin
	 */
	private Integer checkResult(String reslut){
		boolean flag = JsonResult.checkResult(reslut,ApiResultCode.SUCCEED);
		
		if(!flag){
			JSONObject reslutJSON = JSON.parseObject(reslut);
			Integer code = reslutJSON.getInteger("code");
			
			if(AutoFindRecord.STATUS_DETELE.equals(code)){
				return null;
			}
			return code;
		}
		
		return AutoFindRecord.STATUS_NORMAL;
	}
	
	/**
	 * 判断用户是否有此平台积分数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkManualQuery 
	 * @param userId		用户ID
	 * @param manualId		积分账户ID
	 * @return
	 * @date 2015年12月24日 上午11:00:48  
	 * @author xiongbin
	 */
	private boolean checkManualQuery(Long userId,Long manualId){
		List<UserCardManual> list = userCardManualService.selectByUserIdManualId(userId, manualId);
		if(null!=list && list.size()>0){
			return true;
		}
		return false;
	}
}
