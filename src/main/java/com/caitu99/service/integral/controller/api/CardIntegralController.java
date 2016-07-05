package com.caitu99.service.integral.controller.api;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.free.domain.FreeTradePlatform;
import com.caitu99.service.free.service.FreeTradePlatformService;
import com.caitu99.service.integral.controller.vo.CardIntegralGroup;
import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.domain.Bank;
import com.caitu99.service.integral.domain.CardIntegral;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.domain.IntegralRealizationSubscribe;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.domain.UserCardManualItem;
import com.caitu99.service.integral.service.BankService;
import com.caitu99.service.integral.service.CardIntegralService;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ExchangeRuleService;
import com.caitu99.service.integral.service.IntegralRealizationSubscribeService;
import com.caitu99.service.integral.service.UserCardManualItemService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.realization.domain.RealizePlatform;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.domain.UserTerm;
import com.caitu99.service.realization.service.RealizePlatformService;
import com.caitu99.service.realization.service.UserAddTermService;
import com.caitu99.service.realization.service.UserTermService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.VersionUtil;
import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.string.IdCardValidator;

@Controller
@RequestMapping("/api/integral")
public class CardIntegralController {

	private final static Logger logger = LoggerFactory.getLogger(CardIntegralController.class);

	@Autowired
	private CardIntegralService cardIntegralService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserCardManualService userCardManualService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private CardTypeService cardTypeService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private ExchangeRuleService exchangeRuleService;
	@Autowired
	private UserCardManualItemService userCardManualItemService;
	@Autowired
	private IntegralRealizationSubscribeService integralRealizationSubscribeService;
	@Autowired
	private FreeTradePlatformService freeTradePlatformService;
	@Autowired
	private UserAddTermService userAddTermService;
	@Autowired
	private RealizePlatformService realizePlatformService;
	@Autowired
	private UserTermService userTermService;
	@Autowired
	private BankService bankService;
	
	//根据卡号查询积分到期提醒
	@RequestMapping(value="/card/expire/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String cardIntegral(Long cardId) {
		// 初始化
		ApiResult<List<CardIntegral>> result = new ApiResult<>();
		// 业务实现
		List<CardIntegral> cardIntegralList = cardIntegralService.list(cardId);
		// 数据返回
		return result.toJSONString(0,"success",cardIntegralList);
	}
	
	/**
	 * 根据卡号查询积分到期提醒
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: cardIntegral 
	 * @param cardId			t_user_card或t_user_card_manual ID
	 * @param importType		导入类型(0:邮箱导入;1:手动导入)
	 * @return
	 * @date 2015年11月23日 下午3:14:27  
	 * @author xiongbin
	 */
	@RequestMapping(value="/card/expire/2.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String cardIntegral(Long cardId,Integer importType) {
		ApiResult<List<CardIntegral>> result = new ApiResult<>();
		
		if(null==cardId){
			return result.toJSONString(-1,"参数cardId不能为空");
		}else if(null==importType){
			return result.toJSONString(-1,"参数importType不能为空");
		}
		
		List<CardIntegral> cardIntegralList = new ArrayList<CardIntegral>();
		//积分账户ID
		Long manualId = -1L;
		
		if(UserCard.IMPORT_TYPE_EMAIL.equals(importType)){
			cardIntegralList = cardIntegralService.list(cardId);
			UserCard userCard = userCardService.selectByPrimaryKey(cardId);
			
			if(null != userCard){
				CardType cardType = cardTypeService.selectByPrimaryKey(userCard.getCardTypeId());
				if(null != cardType){
					manualId = cardType.getManualId();
				}
			}
			
		}else if(UserCard.IMPORT_TYPE_MANUAL.equals(importType)){
			UserCardManual userCardManual = userCardManualService.selectByPrimaryKey(cardId);
			
			if(null != userCardManual){
				CardIntegral cardIntegral = new CardIntegral();
				cardIntegral.setBalance(userCardManual.getIntegral());
				cardIntegral.setCardId(userCardManual.getId());
				cardIntegralList.add(cardIntegral);
				
				CardType cardType = cardTypeService.selectByPrimaryKey(userCardManual.getCardTypeId());
				if(null != cardType){
					manualId = cardType.getManualId();
				}
			}
		}
		
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("code", 0);
		resultJSON.put("message","success");
		resultJSON.put("manualId", manualId);
		resultJSON.put("data",cardIntegralList);
		
		return resultJSON.toJSONString();
	}
	
	/**
	 * 根据卡号查询积分到期提醒
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: cardIntegralNew 
	 * @param userid			用户ID
	 * @param cardId			t_user_card或t_user_card_manual ID
	 * @param importType		导入类型(0:邮箱导入;1:手动导入)
	 * @return
	 * @date 2016年1月5日 下午6:31:40  
	 * @author xiongbin
	 */
	@RequestMapping(value="/card/expire/3.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String cardIntegralNew(Long userid,Long cardId,Integer importType) {
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		
		if(null==cardId){
			return result.toJSONString(-1,"参数cardId不能为空");
		}else if(null==importType){
			return result.toJSONString(-1,"参数importType不能为空");
		}else if(null == userid){
			return result.toJSONString(-1,"参数userid不能为空");
		}
		
		//积分账户ID
		Long manualId = -1L;
		//卡号
		String cardNo = "";
		//姓名
		String userName = "";
		//积分兑换比例
		Float scale = 0f;
		//总积分
		Integer integral = 0;
		
		Map<String,String> map = new HashMap<String,String>();
		CardType cardType = null;
		
		if(UserCard.IMPORT_TYPE_EMAIL.equals(importType)){
			UserCard userCard = userCardService.selectByPrimaryKey(cardId);
			if(null != userCard){
				cardType = cardTypeService.selectByPrimaryKey(userCard.getCardTypeId());
				if(null != cardType){
					manualId = cardType.getManualId();
				}
				cardNo = userCard.getCardNo();
				userName = userCard.getName();
				integral = userCard.getIntegralBalance();
				
				ExchangeRule exchangeRule = exchangeRuleService.findByCardTypeId(userCard.getCardTypeId());
				if(null != exchangeRule){
					scale = exchangeRule.getScale();
				}
			}
			
			List<CardIntegralLastTime> cardIntegrals = cardIntegralService.selectLastTimeNew(userid, cardId);
			map = cardIntegralLastTime(cardIntegrals);
			
			
		}else if(UserCard.IMPORT_TYPE_MANUAL.equals(importType)){
			UserCardManual userCardManual = userCardManualService.selectByPrimaryKey(cardId);
			
			if(null != userCardManual){
				integral = userCardManual.getIntegral();
				
				Long cardTypeId = userCardManual.getCardTypeId();
				cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
				if(null != cardType){
					manualId = cardType.getManualId();
				}
				cardNo = userCardManual.getCardNo();
				userName = userCardManual.getUserName();
				
				ExchangeRule exchangeRule = exchangeRuleService.findByCardTypeId(cardTypeId);
				if(null != exchangeRule){
					scale = exchangeRule.getScale();
				}
				
				Integer expirationIntegral = userCardManual.getExpirationIntegral();
				Integer nextExpirationIntegral = userCardManual.getNextExpirationIntegral();
				
				if(null == expirationIntegral){
					map.put("cardIntegrals30", "---");
					map.put("cardIntegrals90", "---");
					map.put("cardIntegrals180", "---");
				}else{
					Date now = new Date();
					Date expirationTime = userCardManual.getExpirationTime();
					//有过期积分,但无过期时间,国航
					if(null == expirationTime){
						map.put("cardIntegrals30", expirationIntegral.equals(0) ? "---" : expirationIntegral.toString());
						map.put("cardIntegrals90", nextExpirationIntegral.equals(0) ? "---" : nextExpirationIntegral.toString());
						map.put("cardIntegrals180", "---");
					}else{
						Long day30 = 30*24*60*60*1000l;
						Long day90 = 90*24*60*60*1000l;
						Long day180 = 180*24*60*60*1000l;
						//时间差
						Long differenceTime = expirationTime.getTime() - now.getTime();
						
						//有过期积分,有过期时间,淘宝
						if(differenceTime >=0 && differenceTime <= day30){
							map.put("cardIntegrals30", expirationIntegral.equals(0) ? "---" : expirationIntegral.toString());
							map.put("cardIntegrals90", "---");
							map.put("cardIntegrals180", "---");
						}else if(differenceTime > day30 && differenceTime <= day90){
							map.put("cardIntegrals30", "---");
							map.put("cardIntegrals90", expirationIntegral==0 ? "---" : expirationIntegral.toString());
							map.put("cardIntegrals180", "---");
						}else if(differenceTime > day90 && differenceTime <= day180){
							map.put("cardIntegrals30", "---");
							map.put("cardIntegrals90", "---");
							map.put("cardIntegrals180", expirationIntegral.equals(0) ? "---" : expirationIntegral.toString());
						}else{
							map.put("cardIntegrals30", "---");
							map.put("cardIntegrals90", "---");
							map.put("cardIntegrals180", "---");
						}
					}
				}

				map.put("cardIntegralsLastMonth", "---");
			}
		}
		
		//总积分价值
		Double balance = 0d;
		balance = scale.doubleValue() * integral * appConfig.exchangeScale;
		
		//是否有第三方商城;(0:无;1:有)
		Integer isThird = 0;
		//第三方商城网址
		String urlThird = "";
		//是否是自己封装的商城;(0:不是;1:是)
		Integer isOneself = 0;
		
		if(cardType != null){
			urlThird = cardType.getUrlThird();
			if(StringUtils.isBlank(urlThird)){
				urlThird = cardType.getUrl();
				if(StringUtils.isNotBlank(urlThird)){
					isThird = 1;
				}
				isOneself = 0;
			}else{
				isThird = 1;
				isOneself = 1;
			}
		}
		
		//保留小数点两位
		DecimalFormat df = new DecimalFormat("#0.00");
		
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("importType", importType);
		resultJSON.put("cardNo", cardNo);
		resultJSON.put("name", userName);
		resultJSON.put("integral", integral);
		resultJSON.put("balance", df.format(balance));
		
		resultJSON.put("isThird", isThird);
		resultJSON.put("isOneself", isOneself);
		resultJSON.put("urlThird", urlThird);
		resultJSON.put("manualId", manualId);
		resultJSON.putAll(map);
		
		return result.toJSONString(0, "success", resultJSON);
	}
	
	/**
	 * 处理到期积分详情,30/90/180到期时间
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: cardIntegralLastTime 
	 * @param cardIntegrals
	 * @return
	 * @date 2016年1月5日 下午4:45:55  
	 * @author xiongbin
	 */
	private Map<String,String> cardIntegralLastTime(List<CardIntegralLastTime> cardIntegrals){
		Integer cardIntegrals30 = 0;
		Integer cardIntegrals90 = 0;
		Integer cardIntegrals180 = 0;
		
		if (null != cardIntegrals && !cardIntegrals.isEmpty()) {
			for (CardIntegralLastTime clt : cardIntegrals) {
				if(null == clt){
					continue;
				}
				
				if(null == clt.getDatenum()){
					//是积分没有有效期的情况
					continue;
				}else if(clt.getDatenum() > 180){
					continue;
				}else if(clt.getDatenum() >= 0 && clt.getDatenum() <= 30){
					cardIntegrals30 += clt.getBalance();
				}else if (clt.getDatenum() > 30 && clt.getDatenum() <= 90){
					cardIntegrals90 += clt.getBalance();
				}else if (clt.getDatenum() > 90 && clt.getDatenum() <= 180){
					cardIntegrals180 += clt.getBalance();
				}
			}
		}
		
		Map<String,String> map = new HashMap<String,String>(4);
		map.put("cardIntegrals30", cardIntegrals30.equals(0) ? "---" : cardIntegrals30.toString());
		map.put("cardIntegrals90", cardIntegrals90.equals(0) ? "---" : cardIntegrals90.toString());
		map.put("cardIntegrals180", cardIntegrals180.equals(0) ? "---" : cardIntegrals180.toString());
		//上月新增积分
		map.put("cardIntegralsLastMonth", "---");
		
		return map;
	}
	
	/**
	 * 根据卡号查询积分到期提醒
	 * @Description: 积分变现只有中信可进
	 * @Title: cardIntegral4 
	 * @param userid			用户ID
	 * @param cardId			t_user_card或t_user_card_manual ID
	 * @param importType		导入类型(0:邮箱导入;1:手动导入)
	 * @return
	 * @date 2016年1月25日 上午12:23:40  
	 * @author xiongbin
	 */
	@RequestMapping(value="/card/expire/4.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String cardIntegral4(Long userid,Long cardId,Integer importType) {
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		
		if(null==cardId){
			return result.toJSONString(-1,"参数cardId不能为空");
		}else if(null==importType){
			return result.toJSONString(-1,"参数importType不能为空");
		}else if(null == userid){
			return result.toJSONString(-1,"参数userid不能为空");
		}
		
		JSONObject resultJSON = getCardIntegral(userid,cardId,importType,"2.0.0");
		
		return result.toJSONString(0, "success", resultJSON);
	}
	
	/**
	 * 根据卡号查询积分到期提醒
	 * @Description: 
	 * @Title: cardIntegral5
	 * @param userid			用户ID
	 * @param cardId			t_user_card或t_user_card_manual ID
	 * @param importType		导入类型(0:邮箱导入;1:手动导入)
	 * @return
	 * @date 2016年1月25日 上午12:23:40  
	 * @author xiongbin
	 */
	@RequestMapping(value="/card/expire/5.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String cardIntegral5(Long userid,Long cardId,Integer importType,HttpServletRequest request) {
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		
		if(null==cardId){
			return result.toJSONString(-1,"参数cardId不能为空");
		}else if(null==importType){
			return result.toJSONString(-1,"参数importType不能为空");
		}else if(null == userid){
			return result.toJSONString(-1,"参数userid不能为空");
		}
		
		String version = VersionUtil.getAppVersion(request);
		if(StringUtils.isBlank(version)){
			logger.error("无法获取APP版本号");
			return result.toJSONString(-1, "请传递版本号");
		}else{
			logger.info("获取APP版本号为:" + version);
		}
		
		JSONObject resultJSON = getCardIntegral2(userid,cardId,importType,version);
		
		return result.toJSONString(0, "success", resultJSON);
	}
	
	/**
	 * 根据卡号查询积分到期提醒
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getCardIntegral 
	 * @param userid			用户ID
	 * @param cardId			t_user_card或t_user_card_manual ID
	 * @param importType		导入类型(0:邮箱导入;1:手动导入)
	 * @return
	 * @date 2016年2月18日 下午3:00:55  
	 * @author xiongbin
	 */
	private JSONObject getCardIntegral(Long userid,Long cardId,Integer importType,String versions){
		//积分账户ID
		Long manualId = -1L;
		//自由交易平台ID
		Long remoteId = -1L;
		//卡号
		String cardNo = "";
		//姓名
		String userName = "";
		//积分兑换比例
		Float scale = 0f;
		//总积分
		Integer integral = 0;
		//过期积分
		Integer expireIntegral = 0;
		//过期时间
		Date expireTime = null;
		//登录账号
		String account = "";
		//积分更新时间
		Date updateTime = null;
		
		CardType cardType = null;
		FreeTradePlatform freeTradePlatform = null;
		
		if(UserCard.IMPORT_TYPE_EMAIL.equals(importType)){
			CardIntegral cardIntegral = cardIntegralService.selectFirstTimeByCardId(cardId);
			if(null != cardIntegral){
				expireIntegral = cardIntegral.getBalance();
				expireTime = cardIntegral.getGmtEffective();
			}
			
			UserCard userCard = userCardService.selectByPrimaryKey(cardId);
			if(null != userCard){
				cardType = cardTypeService.selectByPrimaryKey(userCard.getCardTypeId());
				if(null != cardType){
					manualId = cardType.getManualId();
					remoteId = cardType.getRemoteId();
					freeTradePlatform = freeTradePlatformService.selectByPrimaryKey(remoteId);
				}
				cardNo = userCard.getCardNo();
				userName = userCard.getName();
				integral = userCard.getIntegralBalance();
				account = userCard.getEmail();
				updateTime = userCard.getGmtModify();
				
				ExchangeRule exchangeRule = exchangeRuleService.findByCardTypeId(userCard.getCardTypeId());
				if(null != exchangeRule){
					scale = exchangeRule.getScale();
				}
			}
		}else if(UserCard.IMPORT_TYPE_MANUAL.equals(importType)){
			UserCardManualItem userCardManualItem = userCardManualItemService.selectFirstTimeByCardId(cardId);
			if(null != userCardManualItem){
				expireIntegral = userCardManualItem.getExpirationIntegral();
				expireTime = userCardManualItem.getExpirationTime();
			}
			
			UserCardManual userCardManual = userCardManualService.selectByPrimaryKey(cardId);
			if(null != userCardManual){
				integral = userCardManual.getIntegral();
				
				Long cardTypeId = userCardManual.getCardTypeId();
				cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
				if(null != cardType){
					manualId = cardType.getManualId();
					remoteId = cardType.getRemoteId();
					freeTradePlatform = freeTradePlatformService.selectByPrimaryKey(remoteId);
				}
				cardNo = userCardManual.getCardNo();
				userName = userCardManual.getUserName();
				account = userCardManual.getLoginAccount();
				updateTime = userCardManual.getGmtModify();
				
				ExchangeRule exchangeRule = exchangeRuleService.findByCardTypeId(cardTypeId);
				if(null != exchangeRule){
					scale = exchangeRule.getScale();
				}
			}
		}
		
		//交易比例
		Double exchangeScale = appConfig.exchangeScale;
		
		//总积分价值
		Double balance = 0d;
		balance = scale.doubleValue() * integral * exchangeScale;
		
		//过期积分价值
		Double expireBalance = 0d;
		expireBalance = scale.doubleValue() * expireIntegral * exchangeScale;
		
		
		//是否有第三方商城;(0:无;1:有)
		Integer isThird = 0;
		//第三方商城网址
		String urlThird = "";
		//是否是自己封装的商城;(0:不是;1:是)
		Integer isOneself = 0;
		
		//是否需要显示积分变现按钮;(0:不需要;1:需要;)
		Integer isIntegralRealization = 0;
		//平台预约总人数
		Integer subscribeCount = 0;
		//平台默认预约人数
		Integer defaultSubscribe = 0;
		//查询用户是否预约;(0:无;1:有)
		Integer isSubscribe = 0;
		//积分变现按钮是否可进入(0:不可进入;1:可进入)
		Integer isRealizeRealization = 0;
		
		if(cardType != null){
			urlThird = cardType.getUrlThird();
			if(StringUtils.isBlank(urlThird)){
				urlThird = cardType.getUrl();
				if(StringUtils.isNotBlank(urlThird)){
					isThird = 1;
				}
				isOneself = 0;
			}else{
				isThird = 1;
				isOneself = 1;
				urlThird = appConfig.caituUrl + urlThird;
			}
			
			defaultSubscribe = cardType.getDefaultSubscribe();
			if(null != defaultSubscribe){
				isIntegralRealization = 1;
				subscribeCount += defaultSubscribe;
				if(null != freeTradePlatform){
					Long versionLong = VersionUtil.getVersionLong(versions);
					if(Long.valueOf(freeTradePlatform.getVersion().toString()).compareTo(versionLong) <= 0){
						isRealizeRealization = 1;
					}
				}
			}else{
				defaultSubscribe = 0;
			}
			
			Long cardTypeId = cardType.getId();
			IntegralRealizationSubscribe integralRealizationSubscribe = integralRealizationSubscribeService.selectByUserIdCardTypeId(userid,cardTypeId);
			if(null != integralRealizationSubscribe && isRealizeRealization.equals(0)){
				isSubscribe = 1; 
				subscribeCount += integralRealizationSubscribeService.selectCount(cardTypeId);
			}
		}
		
		//保留小数点两位
		DecimalFormat df = new DecimalFormat("#0.00");
		
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("importType", importType);
		resultJSON.put("cardNo", cardNo);
		resultJSON.put("name", userName);
		resultJSON.put("integral", integral);
		resultJSON.put("balance", df.format(balance));
		resultJSON.put("account", account);
		resultJSON.put("updateTime", updateTime);
		
		resultJSON.put("expireIntegral", expireIntegral);
		resultJSON.put("expireBalance", df.format(expireBalance));
		
		resultJSON.put("expireTime", expireTime!=null ? DateUtil.DateToString(expireTime,"yyyy年MM月dd日") : expireTime);
		
		resultJSON.put("isThird", isThird);
		resultJSON.put("isOneself", isOneself);
		resultJSON.put("urlThird", urlThird);
		resultJSON.put("manualId", manualId);
		resultJSON.put("remoteId", remoteId);

		resultJSON.put("isIntegralRealization", isIntegralRealization);
		resultJSON.put("isSubscribe", isSubscribe);
		resultJSON.put("subscribeCount", subscribeCount);
		resultJSON.put("isRealizeRealization", isRealizeRealization);
		
		return resultJSON;
	}
	
	
	private JSONObject getCardIntegral2(Long userid,Long cardId,Integer importType,String versions){
		//积分账户ID
		Long manualId = -1L;
		//积分变现平台ID
		Long platformId = -1L;
		//卡号
		String cardNo = "";
		//姓名
		String userName = "";
		//积分兑换比例
		Float scale = 0f;
		//总积分
		Integer integral = 0;
		//过期积分
		Integer expireIntegral = 0;
		//过期时间
		Date expireTime = null;
		//登录账号
		String account = "";
		//积分更新时间
		Date updateTime = null;
		//绑定帐号信息JSON
		String info = "";
		////是否绑定(0:未绑定;1:绑定)
		Integer isBinding = 0;
		//绑定类型(1.账单，2.实时，3.添加)
		Integer remoteType = -1;
		//卡片图标
		String icon = "";
		//卡片名称
		String cardName = "";
		//卡片ID
		Long cardTypeId = -1L;
		
		CardType cardType = null;
		RealizePlatform realizePlatform = null;
		
		if(UserCard.IMPORT_TYPE_EMAIL.equals(importType)){
			CardIntegral cardIntegral = cardIntegralService.selectFirstTimeByCardId(cardId);
			if(null != cardIntegral){
				expireIntegral = cardIntegral.getBalance();
				expireTime = cardIntegral.getGmtEffective();
			}
			
			UserCard userCard = userCardService.selectByPrimaryKey(cardId);
			if(null != userCard){
				cardTypeId = userCard.getCardTypeId();
				cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
				if(null != cardType){
					manualId = cardType.getManualId();
					platformId = cardType.getRealizePlatformId();
					realizePlatform = realizePlatformService.selectByPrimaryKey(platformId);
					cardName = cardType.getName();
					
					//查询图标
					String belongTo = cardType.getBelongTo();
					if(StringUtils.isNotBlank(belongTo)){
						Bank bank = bankService.selectByName(belongTo);
						if(null != bank){
							icon = appConfig.staticUrl + bank.getPicUrl();
						}
					}
				}
				if(null != userCard.getCardNo()){
					cardNo = userCard.getCardNo();
				}
				userName = userCard.getName();
				integral = userCard.getIntegralBalance();
				account = userCard.getEmail();
				updateTime = userCard.getGmtModify();
				
				ExchangeRule exchangeRule = exchangeRuleService.findByCardTypeId(userCard.getCardTypeId());
				if(null != exchangeRule){
					scale = exchangeRule.getScale();
				}
			}
			remoteType = 1;
		}else if(UserCard.IMPORT_TYPE_MANUAL.equals(importType)){
			UserCardManualItem userCardManualItem = userCardManualItemService.selectFirstTimeByCardId(cardId);
			if(null != userCardManualItem){
				expireIntegral = userCardManualItem.getExpirationIntegral();
				expireTime = userCardManualItem.getExpirationTime();
			}
			
			UserCardManual userCardManual = userCardManualService.selectByPrimaryKey(cardId);
			if(null != userCardManual){
				integral = userCardManual.getIntegral();
				cardTypeId = userCardManual.getCardTypeId();
				cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
				if(null != cardType){
					manualId = cardType.getManualId();
					platformId = cardType.getRealizePlatformId();
					realizePlatform = realizePlatformService.selectByPrimaryKey(platformId);
					cardName = cardType.getName();
					
					//查询图标
					String belongTo = cardType.getBelongTo();
					if(StringUtils.isNotBlank(belongTo)){
						Bank bank = bankService.selectByName(belongTo);
						if(null != bank){
							icon = appConfig.staticUrl + bank.getPicUrl();
						}
					}
				}
				
				if(null != userCardManual.getCardNo()){
					cardNo = userCardManual.getCardNo();
				}
				userName = userCardManual.getUserName();
				account = userCardManual.getLoginAccount();
				updateTime = userCardManual.getGmtModify();
				
				ExchangeRule exchangeRule = exchangeRuleService.findByCardTypeId(cardTypeId);
				if(null != exchangeRule){
					scale = exchangeRule.getScale();
				}
			}
			remoteType = 2;
		}
		
		//交易比例
		Double exchangeScale = appConfig.exchangeScale;
		
		//总积分价值
		Double balance = 0d;
		if(null!=scale && null!=integral && null!=exchangeScale){
			balance = scale.doubleValue() * integral * exchangeScale;
		}
		
		//过期积分价值
		Double expireBalance = 0d;
		if(null!=scale && null!=expireIntegral && null!=exchangeScale){
			expireBalance = scale.doubleValue() * expireIntegral * exchangeScale;
		}
		
		
		//是否有第三方商城;(0:无;1:有)
		Integer isThird = 0;
		//第三方商城网址
		String urlThird = "";
		//是否是自己封装的商城;(0:不是;1:是)
		Integer isOneself = 0;
		
		//是否需要显示积分变现按钮;(0:不需要;1:需要;)
		Integer isIntegralRealization = 0;
		//平台预约总人数
		Integer subscribeCount = 0;
		//平台默认预约人数
		Integer defaultSubscribe = 0;
		//查询用户是否预约;(0:无;1:有)
		Integer isSubscribe = 0;
		//积分变现按钮是否可进入(0:不可进入;1:可进入)
		Integer isRealizeRealization = 0;
		
		if(cardType != null){
			urlThird = cardType.getUrlThird();
			if(StringUtils.isBlank(urlThird)){
				urlThird = cardType.getUrl();
				if(StringUtils.isNotBlank(urlThird)){
					isThird = 1;
				}
				isOneself = 0;
			}else{
				isThird = 1;
				isOneself = 1;
				urlThird = appConfig.caituUrl + urlThird;
			}
			
			defaultSubscribe = cardType.getDefaultSubscribe();
			if(null != defaultSubscribe){
				isIntegralRealization = 1;
				subscribeCount += defaultSubscribe;
				if(null != realizePlatform){
					Long versionLong = VersionUtil.getVersionLong(versions);
					if(Long.valueOf(realizePlatform.getVersion().toString()).compareTo(versionLong) <= 0){
						isRealizeRealization = 1;
					}
				}
			}else{
				defaultSubscribe = 0;
			}
			
			IntegralRealizationSubscribe integralRealizationSubscribe = integralRealizationSubscribeService.selectByUserIdCardTypeId(userid,cardTypeId);
			if(null != integralRealizationSubscribe && isRealizeRealization.equals(0)){
				isSubscribe = 1; 
				subscribeCount += integralRealizationSubscribeService.selectCount(cardTypeId);
			}
		}
		
		/** 查询用户绑定信息 */
		UserTerm userTerm = userTermService.selectUserTerm(userid, cardId, remoteType);
		if(null != userTerm){
			info = userTerm.getInfo();
			isBinding = 1;
		}
		
		//卡片图标
		if(StringUtils.isNotBlank(cardName)){
			if(cardName.indexOf("招商") != -1){
				cardName = "招商银行";
			}
		}
		
		
		//保留小数点两位
		DecimalFormat df = new DecimalFormat("#0.00");
		
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("importType", importType);
		resultJSON.put("cardNo", cardNo);
		resultJSON.put("name", userName);
		resultJSON.put("integral", integral);
		resultJSON.put("balance", df.format(balance));
		resultJSON.put("account", account);
		resultJSON.put("updateTime", updateTime);
		
		resultJSON.put("expireIntegral", expireIntegral);
		resultJSON.put("expireBalance", df.format(expireBalance));
		
		resultJSON.put("expireTime", expireTime!=null ? DateUtil.DateToString(expireTime,"yyyy年MM月dd日") : expireTime);
		
		resultJSON.put("isThird", isThird);
		resultJSON.put("isOneself", isOneself);
		resultJSON.put("urlThird", urlThird);
		resultJSON.put("manualId", manualId);
		resultJSON.put("platformId", platformId);

		resultJSON.put("isIntegralRealization", isIntegralRealization);
		resultJSON.put("isSubscribe", isSubscribe);
		resultJSON.put("subscribeCount", subscribeCount);
		resultJSON.put("isRealizeRealization", isRealizeRealization);
		
		resultJSON.put("remoteType", remoteType);
		resultJSON.put("info", StringUtils.isBlank(info) ? "" : info);
		resultJSON.put("isBinding", isBinding);
		
		//ios新增返回数据
		resultJSON.put("cardName", cardName);
		resultJSON.put("icon", icon);
		resultJSON.put("integralBalance", integral);
		resultJSON.put("remoteId", cardId);
		resultJSON.put("cardTypeId", cardTypeId);
		
		return resultJSON;
	}
	
	/**
	 * 积分变现预约
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: integralRealizationSubscribe 
	 * @param userid			用户ID
	 * @param cardId			t_user_card或t_user_card_manual ID
	 * @param importType		导入类型(0:邮箱导入;1:手动导入)
	 * @return
	 * @date 2016年1月26日 上午10:39:58  
	 * @author xiongbin
	 */
	@RequestMapping(value="/realization/subscribe/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String integralRealizationSubscribe(Long userid,Long cardId,Integer importType) {
		ApiResult<Integer> result = new ApiResult<Integer>();
		
		if(null==cardId){
			return result.toJSONString(-1,"参数cardId不能为空");
		}else if(null==importType){
			return result.toJSONString(-1,"参数importType不能为空");
		}else if(null == userid){
			return result.toJSONString(-1,"参数userid不能为空");
		}
		
		CardType cardType = null;
		Long cardTypeId = null;
		
		if(UserCard.IMPORT_TYPE_EMAIL.equals(importType)){
			UserCard userCard = userCardService.selectByPrimaryKey(cardId);
			if(null != userCard){
				cardTypeId = userCard.getCardTypeId();
				cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
			}else{
				return result.toJSONString(-1, "该积分信息不存在");
			}
		}else if(UserCard.IMPORT_TYPE_MANUAL.equals(importType)){
			UserCardManual userCardManual = userCardManualService.selectByPrimaryKey(cardId);
			if(null != userCardManual){
				cardTypeId = userCardManual.getCardTypeId();
				cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
			}else{
				return result.toJSONString(-1, "该积分信息不存在");
			}
		}
		
		//平台预约总人数
		Integer subscribeCount = 0;
		if(null != cardType){
			Integer defaultSubscribe = cardType.getDefaultSubscribe();
			if(null == defaultSubscribe){
				return result.toJSONString(-1, "该积分平台无预约功能");
			}else if(defaultSubscribe.equals(-1)){
				return result.toJSONString(-1, "该积分平台已实现积分变现功能");
			}
			
			IntegralRealizationSubscribe integralRealizationSubscribe = integralRealizationSubscribeService.selectByUserIdCardTypeId(userid,cardTypeId);
			if(null != integralRealizationSubscribe){
				return result.toJSONString(-1, "您已经预约过了");
			}
			
			integralRealizationSubscribeService.subscribe(userid, cardTypeId);
			
			Integer count = integralRealizationSubscribeService.selectCount(cardTypeId);
			subscribeCount = defaultSubscribe + count;
		}
		
		return result.toJSONString(0, "预约成功",subscribeCount);
	}

	// 获取最近积分到期时间
	@RequestMapping(value="/card/expireall/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String integralBasic(Long userid) {
		//validate
		User loginUser = userService.getById(userid);
		if (null == loginUser) {
			throw new UserNotFoundException(-1, "用户不存在");
		}
		// 初始化
		ApiResult<List<CardIntegralGroup>> result = new ApiResult<>();
		List<CardIntegralGroup> cardIntegralGroups = new ArrayList<>();
		List<CardIntegralLastTime> cardIntegrals30 = new ArrayList<>();
		List<CardIntegralLastTime> cardIntegrals60 = new ArrayList<>();
		List<CardIntegralLastTime> cardIntegrals180 = new ArrayList<>();
		List<CardIntegralLastTime> cardIntegralsMore = new ArrayList<>();
		// 业务实现
		List<CardIntegralLastTime> cardIntegrals = cardIntegralService.selectLastTime(userid);
		CardIntegralGroup cardIntegralGroup = null;
		if (null != cardIntegrals && !cardIntegrals.isEmpty()) {
			for (CardIntegralLastTime clt : cardIntegrals) {
				if(null == clt){
					continue;
				}
				if(null == clt.getDatenum()){   //是积分没有有效期的情况
					cardIntegralsMore.add(clt);
					continue;
				}
				if(clt.getDatenum() >= 0 && clt.getDatenum() <= 30){
					cardIntegrals30.add(clt);
				}else if (clt.getDatenum() > 30 && clt.getDatenum() <= 90) {
					cardIntegrals60.add(clt);
				}else if (clt.getDatenum() > 90 && clt.getDatenum() <= 180) {
					cardIntegrals180.add(clt);
				}else if (clt.getDatenum() > 180) {
					cardIntegralsMore.add(clt);
				}
			}
			cardIntegralGroup = new CardIntegralGroup();
			cardIntegralGroup.setCardIntegral(cardIntegrals30);
			cardIntegralGroup.setType(1);
			cardIntegralGroups.add(cardIntegralGroup);
			cardIntegralGroup = new CardIntegralGroup();
			cardIntegralGroup.setCardIntegral(cardIntegrals60);
			cardIntegralGroup.setType(2);
			cardIntegralGroups.add(cardIntegralGroup);
			cardIntegralGroup = new CardIntegralGroup();
			cardIntegralGroup.setCardIntegral(cardIntegrals180);
			cardIntegralGroup.setType(3);
			cardIntegralGroups.add(cardIntegralGroup);
		}
		cardIntegrals = cardIntegralService.selectOtherTime(loginUser.getId());
		for (CardIntegralLastTime cardIntegralLastTime : cardIntegrals) {
			cardIntegralsMore.add(cardIntegralLastTime);
		}
		cardIntegralGroup = new CardIntegralGroup();
		cardIntegralGroup.setCardIntegral(cardIntegralsMore);
		cardIntegralGroup.setType(4);
		cardIntegralGroups.add(cardIntegralGroup);

		// 数据返回
		return result.toJSONString(0,"success",cardIntegralGroups);
	}
	
	/**
	 * 修改积分数据状态
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateCardStatus 
	 * @param cardId
	 * @param importType
	 * @param status
	 * @return
	 * @date 2015年12月22日 下午7:35:43  
	 * @author xiongbin
	 */
	@RequestMapping(value="/card/status/update/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String updateCardStatus(Long cardId,Integer importType,Integer status,Long userid) {
		ApiResult<String> result = new ApiResult<String>();
		
		if(null==cardId){
			return result.toJSONString(-1,"参数cardId不能为空");
		}else if(null==importType){
			return result.toJSONString(-1,"参数importType不能为空");
		}else if(null==status){
			return result.toJSONString(-1,"参数status不能为空");
		}else if(!status.equals(-1) && !status.equals(1)){
			return result.toJSONString(-1,"参数status传递错误");
		}else if(null == userid){
			return result.toJSONString(-1,"参数userid不能为空");
		}
		
		if(UserCard.IMPORT_TYPE_EMAIL.equals(importType)){
			UserCard record = userCardService.selectByPrimaryKey(cardId);
			if(null == record){
				return result.toJSONString(-1,"数据不存在");
			}else if(!userid.equals(record.getUserId())){
				return result.toJSONString(-1,"此数据不属于该用户");
			}
			
			record.setStatus(status);
			userCardService.updateByPrimaryKey(record);
		}else if(UserCard.IMPORT_TYPE_MANUAL.equals(importType)){
			UserCardManual record = userCardManualService.selectByPrimaryKey(cardId);
			if(null == record){
				return result.toJSONString(-1,"数据不存在");
			}else if(!userid.equals(record.getUserId())){
				return result.toJSONString(-1,"此数据不属于该用户");
			}
			record.setStatus(status);
			userCardManualService.updateByPrimaryKeySelective(record);
		}else if(UserCard.IMPORT_TYPE_USER_ADD.equals(importType)){
			UserAddTerm record = userAddTermService.selectByPrimaryKey(cardId);
			if(null == record){
				return result.toJSONString(-1,"数据不存在");
			}else if(!userid.equals(record.getUserId())){
				return result.toJSONString(-1,"此数据不属于该用户");
			}
			record.setStatus(status);
			userAddTermService.update(record);
		}else{
			return result.toJSONString(-1,"参数importType传递错误");
		}
		
		return result.toJSONString(0,"操作成功");
	}
	
	/**
	 * 首页第一个到期积分详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: integralExpireAllHome 
	 * @param userid
	 * @return
	 * @date 2016年1月20日 上午9:46:23  
	 * @author xiongbin
	 */
	@RequestMapping(value="/card/expireall/first/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String integralExpireAllFirst(Long userid) {
		ApiResult<Map<String,Object>> result = new ApiResult<Map<String,Object>>();

		CardIntegralLastTime cardIntegral = userCardManualItemService.selectLastTimePageFirst(userid);
		if(null == cardIntegral){
			return result.toJSONString(ApiResultCode.NOT_EXPIRE_INTEGRAL, "无过期积分");
		}
		
		Map<String,Object> map = calculateIntegral(cardIntegral);
		if(null == map){
			return result.toJSONString(ApiResultCode.NOT_EXPIRE_INTEGRAL, "无过期积分");
		}
		
		return result.toJSONString(0, "", map);
	}
	
	/**
	 * 分页查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: integralExpireAllList 
	 * @param userid
	 * @param pagination
	 * @return
	 * @date 2016年1月20日 下午2:23:38  
	 * @author xiongbin
	 */
	@RequestMapping(value="/card/expireall/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String integralExpireAllList(Long userid,Pagination<CardIntegralLastTime> pagination) {
		pagination = userCardManualItemService.selectLastTimePageList(userid, pagination);
		ApiResult<Pagination<Map<String,Object>>> result = new ApiResult<Pagination<Map<String,Object>>>();
		
		Pagination<Map<String,Object>> paginationReslut = new Pagination<Map<String,Object>>();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		List<CardIntegralLastTime> list = pagination.getDatas();
		if(null!=list && list.size()>0){
			for(CardIntegralLastTime cardIntegralLastTime : list){
				Map<String,Object> map = calculateIntegral(cardIntegralLastTime);
				if(null != map){
					resultList.add(map);
				}
			}
			paginationReslut.setDatas(resultList);
			paginationReslut.setCurPage(pagination.getCurPage());
			paginationReslut.setFromPage(pagination.getFromPage());
			paginationReslut.setPageSize(pagination.getPageSize());
			paginationReslut.setShowPageNum(pagination.getShowPageNum());
			paginationReslut.setShowPageSize(pagination.getShowPageSize());
			paginationReslut.setStart(pagination.getStart());
			paginationReslut.setToPage(pagination.getToPage());
			paginationReslut.setTotalRow(pagination.getTotalRow());
		}
		
		return result.toJSONString(0,"success",paginationReslut);
	}
	
	/**
	 * 解析
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: calculateIntegral 
	 * @param cardIntegral
	 * @return
	 * @date 2016年1月20日 下午7:48:30  
	 * @author xiongbin
	 */
	private Map<String,Object> calculateIntegral(CardIntegralLastTime cardIntegral){
		//过期天数
		Integer days;
		//积分价值
		Double balance;
		//积分平台
		String bankname;
		//积分比例
		float scale;
		//卡号
		String cardNo;
		//积分
		Integer integral;
		//图标
		String icon;

		days = cardIntegral.getDatenum();
		
		if(days < 0){
			return null;
		}
		
		Integer Balance = cardIntegral.getBalance();
		bankname = cardIntegral.getBankname();
		scale = cardIntegral.getScale();
		cardNo = cardIntegral.getCardNo();
		integral = Balance;
		balance = scale * integral * appConfig.exchangeScale;
		icon = appConfig.staticUrl + cardIntegral.getPicUrl();
		
		if(cardNo.indexOf(",") != -1){
			if(cardNo.length() > 8){
				cardNo = cardNo.substring(0, 9);
			}
		}else if(IdCardValidator.valideIdCard(cardNo)){
			cardNo = IdCardValidator.encryption(cardNo);
		}
		
		//保留小数点两位
		DecimalFormat df = new DecimalFormat("#0.00");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("days", days+1);
		map.put("balance", df.format(balance));
		map.put("name", bankname.indexOf("招商")!=-1 ? bankname.substring(0, 4) : bankname);
		map.put("cardNo", cardNo);
		map.put("integral", integral);
		map.put("icon", icon);
		
		return map;
	}
	
	/**
	 * 空白首页积分列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: cardList 
	 * @return
	 * @date 2016年3月14日 下午3:58:02  
	 * @author xiongbin
	 */
	@RequestMapping(value="/blank/card/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String cardList() {
		return cardTypeService.selectCardTypeList();
	}
}
