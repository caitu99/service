package com.caitu99.service.user.controller.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.CardIntegralService;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.manage.domain.BankCard;
import com.caitu99.service.manage.service.BankCardService;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.realization.domain.RealizePlatform;
import com.caitu99.service.realization.service.RealizePlatformService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.controller.vo.IntegralVo;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.string.IdCardValidator;
import com.caitu99.service.utils.string.StrUtil;


@Controller
@RequestMapping("/api/user/card")
public class UserCardController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UserCardController.class);

	@Autowired
	private UserCardService userCardService;
	@Autowired
	private CardIntegralService cardIntegralService;
	@Autowired
	private UserService userService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private UserCardManualService userCardManualService;

	@Autowired
	private PushMessageService pushMessageService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private CardTypeService cardTypeService;
	@Autowired
	private RealizePlatformService realizationPlatformService;
	@Autowired
	private BankCardService bankCardService;
	
//	/**
//	 * 获取积分信息
//	 *
//	 * @param userid
//	 * @return
//	 */
//	@RequestMapping(value = "/query/integral/1.0", produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String queryIntegral(Long userid) {
//		ApiResult<List<IntegralVo>> apiResult = new ApiResult<List<IntegralVo>>();
//
//		// 业务实现
//		User loginUser = userService.getById(userid);
//		// validate
//		if (null == loginUser) {
//			throw new UserNotFoundException(-1, "用户不存在");
//		}
//
//		//获取用户手动查询所拥有的卡片
//		List<UserCardManualVo> list = userCardManualService.queryIntegralRemoveRepetition(userid,1);
//		
//		// 获取用户邮箱查询所拥有的卡片
////		List<UserCard> list = userCardService.queryIntegral(userid);
////
////		Map<Integer, IntegralVo> map = new HashMap<>();
////		if (list != null) {
////			for (UserCard userCard : list) {
////				IntegralVo integralVo = map.get(userCard.getTypeId());
////				map.put(userCard.getTypeId(), getIntegral(integralVo, userCard));
////			}
////		}
//		//卡片总数
//		Integer count = 0;
//		
//		Map<Integer, IntegralVo> map = new HashMap<>();
//		if (list != null) {
//			Map<Long,UserCardManualVo> zhaoshangMap = new HashMap<Long,UserCardManualVo>();
//			for (UserCardManualVo userCard : list) {
//				IntegralVo integralVo = map.get(userCard.getTypeId());
//				map.put(userCard.getTypeId(), getIntegral(integralVo, userCard));
//				
//				//招商信用卡多种类型统一只算一种
//				if(userCard.getCardTypeId().equals(2L) || userCard.getCardTypeId().equals(8L) 
//								|| userCard.getCardTypeId().equals(19L) || userCard.getCardTypeId().equals(26L)){
//					zhaoshangMap.put(userCard.getCardTypeId(), userCard);
//				}
//				
//				count++;
//			}
//			
//			if(zhaoshangMap.size() > 1){
//				count = count - (zhaoshangMap.size() - 1);
//			}
//		}
//
//		List<IntegralVo> integralList = new ArrayList<>();
//		for (Integer key : map.keySet()) {
//			IntegralVo caifen = map.get(key);
//			caifen.setIntegral(0);
//			integralList.add(caifen);
//		}
//		// 用户的财币
//		Account account = accountService.selectByUserId(userid);
//		Long availableIntegral = 0L;
//		if(account!=null){
//			availableIntegral = account.getAvailableIntegral();
//		}
//		
//		IntegralVo caifen = new IntegralVo();
//		caifen.setType(CardTypes.CAIFEN.getValue());
////		caifen.setIntegral(loginUser.getIntegral() == null ? 0 : loginUser.getIntegral());
//		caifen.setIntegral(availableIntegral == null ? 0 : availableIntegral.intValue());
//		caifen.setMoney(caifen.getIntegral() * appConfig.exchangeScale);
////		caifen.setSize(list.size());
//		caifen.setSize(count);
//		integralList.add(caifen);
//
//		// 数据返回
//		return apiResult.toJSONString(0, "获取用户积分信息成功", integralList);
//	}

	/**
	 * 把userCard合并到integralVo中
	 *
	 * @param integralVo
	 * @param userCard
	 * @return
	 */
//	private IntegralVo getIntegral(IntegralVo integralVo, UserCard userCard) {
//		if (null == integralVo) {
//			integralVo = new IntegralVo();
//			integralVo.setType(userCard.getTypeId());
//			if (userCard.getIntegralBalance() != null && userCard.getIntegralBalance() != -1) {
//				integralVo.setMoney(appConfig.exchangeScale * userCard.getIntegralBalance() * userCard.getScale());
//			}
//		} else {
//			if(integralVo.getMoney() == null){
//				integralVo.setMoney(0d);
//			}
//			integralVo.setMoney(integralVo.getMoney() 
//					+ appConfig.exchangeScale
//					* ((userCard.getIntegralBalance() == null || userCard.getIntegralBalance() == -1) ? 0
//							: userCard.getIntegralBalance())
//					* userCard.getScale());
//		}
//		return integralVo;
//	}
	
//	private IntegralVo getIntegral(IntegralVo integralVo, UserCardManualVo userCard) {
//		if (null == integralVo) {
//			integralVo = new IntegralVo();
//			integralVo.setType(userCard.getTypeId());
//			if (userCard.getIntegral() != null && userCard.getIntegral() != -1) {
//				integralVo.setMoney(appConfig.exchangeScale * userCard.getIntegral() * userCard.getScale());
//			}
//		} else {
//			if(integralVo.getMoney() == null){
//				integralVo.setMoney(0d);
//			}
//			integralVo.setMoney(integralVo.getMoney() 
//					+ appConfig.exchangeScale
//					* ((userCard.getIntegral() == null || userCard.getIntegral() == -1) ? 0
//							: userCard.getIntegral())
//					* userCard.getScale());
//		}
//		return integralVo;
//	}

	/**
	 * 查询某类卡片的积分信息
	 *
	 * @param cardType
	 * @param userid
	 * @return
	 */
	@RequestMapping(value = "/query/card/integral/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String listIntegral(Integer cardType, Long userid) {
		ApiResult<List<UserCard>> apiResult = new ApiResult<>();

		//数据验证
		if(null == cardType){
			return apiResult.toJSONString(2103, "卡片类型不能为空");
		}

		//业务实现
		List<UserCard> cardList = getUserCardList(userid,cardType,1);
		
		Date now = new Date();
		
		for(UserCard card : cardList){
			Date gmtCreate = card.getGmtCreate();
			Date gmtModify = card.getGmtModify();
			
			long intervalMilli = now.getTime() - gmtCreate.getTime();
			
			if((intervalMilli / (60 * 60 * 1000)) > 24){
				card.setNewest(0);
			}else{
				card.setNewest(1);
			}
			
			//判断创建时间和更新时间是否一致,一致则为刚刚更新数据
			if(!gmtCreate.after(gmtModify) && !gmtCreate.before(gmtModify)){
				userCardService.updateGmtModifyByPrimaryKey(card.getId(),now);
			}
		}

		// 数据返回
		return apiResult.toJSONString(0, "积分查询成功", cardList);
	}
	
	/**
	 * 查询某类卡片的积分信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: listIntegral2 
	 * @param cardType		卡片类型
	 * @param userid		用户ID
	 * @return
	 * @date 2016年2月22日 下午6:32:54  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/query/card/integral/2.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String listIntegral2(Integer cardType, Long userid) {
		ApiResult<List<UserCard>> apiResult = new ApiResult<>();

		//数据验证
		if(null == cardType){
			return apiResult.toJSONString(2103, "卡片类型不能为空");
		}

		//业务实现
		List<UserCard> cardList = getUserCardList(userid,cardType,1);
		
		Date now = new Date();
		
		for(UserCard card : cardList){
			Date gmtCreate = card.getGmtCreate();
			Date gmtModify = card.getGmtModify();
			
			long intervalMilli = now.getTime() - gmtCreate.getTime();
			
			if((intervalMilli / (60 * 60 * 1000)) > 24){
				card.setNewest(0);
			}else{
				card.setNewest(1);
			}
			
			//判断创建时间和更新时间是否一致,一致则为刚刚更新数据
			if(!gmtCreate.after(gmtModify) && !gmtCreate.before(gmtModify)){
				userCardService.updateGmtModifyByPrimaryKey(card.getId(),now);
			}
			
			//设置新返回字段
			setNewFields(card);
		}

		// 数据返回
		return apiResult.toJSONString(0, "积分查询成功", cardList);
	}
	
	/**
	 * 处理招商银行列表数据
	 *
	 * @param userCardResults
	 * @param cardMap
	 * @param zhaoShanResults
	 */
	private void dealZhaoShanCard(List<UserCard> userCardResults, Map<String, List<UserCard>> cardMap,List<UserCard> zhaoShanResults) {
		//处理多张招商银行有多种类型的卡,只取积分最高的
		if (zhaoShanResults.size() > 1) {
			userCardResults.removeAll(zhaoShanResults);
			List<UserCard> zhaoShanResultsTemp = new ArrayList<>();
			for (Entry<String, List<UserCard>> entry : cardMap.entrySet()) {
				List<UserCard> tempList = entry.getValue();
				if (tempList.size() > 1) {
					UserCard cardResult = null;
					for (int i = 0; i < tempList.size(); i++) {
						UserCard tempCard = tempList.get(i);
						if (!StrUtil.isEmpty(tempCard.getCardNo())) {
							UserCard temp = tempList.get(i);
							if(cardResult==null || cardResult.getIntegralBalance() < temp.getIntegralBalance()){
								cardResult = tempList.get(i);
							}
						}
					}
					if (null == cardResult) {
						cardResult = tempList.get(0);
					}
					zhaoShanResultsTemp.add(cardResult);
				} else {
					zhaoShanResultsTemp.addAll(tempList);
				}
			}
			userCardResults.addAll(zhaoShanResultsTemp);
		}
	}

	/**
	 * 获取积分最近到期时间
	 *
	 * @param userid
	 * @return string
	 */
	@RequestMapping(value = "/query/integral/expire/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String queryIntegralExpire(Long userid) {

		ApiResult<CardIntegralLastTime> apiResult = new ApiResult<>();

		// 业务实现
		List<CardIntegralLastTime> cardIntegrals = cardIntegralService.selectLastTime(userid);
		CardIntegralLastTime ans = null;
		if (cardIntegrals.size() > 0) {
			for (int i = 0; i < cardIntegrals.size(); i++) {
				CardIntegralLastTime cardIntegral = cardIntegrals.get(i);
				if (null == cardIntegral.getDatenum() || cardIntegral.getDatenum() < 0) {
					continue; 
				}
				if (ans == null) {
					ans = cardIntegral;
				} else {
					if (cardIntegral.getDatenum() < ans.getDatenum()) {
						ans = cardIntegral;
					}
				}
			}
		}
		return apiResult.toJSONString(0, "获取最近到期时间成功", ans);
	}
	
	/**
	 * @Description: (用户积分到期提醒执行入口，供给定时调度任务每天调度)  
	 * @Title: integralRemindDaysNumber 
	 * @date 2015年12月2日 下午3:25:10  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value = "/job/execute/integral/remind/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public void integralRemindDaysNumber(){
		String daystr = Configuration.getProperty("push.integral.remind.days.number", null);//查询的距离到期天数
		String description = Configuration.getProperty("push.integral.remind.description", null);//消息摘要模版
		String sms = Configuration.getProperty("push.integral.remind.sms", null);//短信
		String title = Configuration.getProperty("push.integral.remind.title", null);
		String[] days = daystr.split(",");
		for (int i = 0; i < days.length; i++) {
			List<UserCard> userCard = userCardService.selectEffectiveIntegralUser(Integer.parseInt(days[i]));
			Map<Long,String> map = new HashMap<Long, String>();
			for (UserCard uc : userCard) {
				if(null == map.get(uc.getUserId())){
					String cardType = null;
					switch (uc.getTypeId()) {
					case 1:
						cardType = "信用卡";
						break;
					case 2:
						cardType = "商旅卡";
						break;
					case 3:
						cardType = "购物卡";
						break;
					}
					map.put(uc.getUserId(), cardType);
				} else {
					map.put(uc.getUserId(), "");
				}
			}
			try {
				for (Long key : map.keySet()) {
					Message message = new Message();
					message.setIsPush(true);
					message.setIsSMS(true);
					message.setIsYellow(false);
					message.setTitle(title);
					message.setPushInfo(String.format(description, map.get(key),days[i]));
					message.setSmsInfo(String.format(sms, map.get(key),days[i]));
					logger.info("新增消息通知：userId:{},description:{}",key,JSON.toJSONString(message));
					pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, key, message);
				}
			} catch (Exception e) {
				logger.error("积分过期提醒推送消息发生异常：{}",e);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 查询用户积分数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryIntegral 
	 * @param userid
	 * @return
	 * @date 2015年12月22日 下午8:13:58  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/query/integral/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String queryIntegral(Long userid) {
		ApiResult<List<IntegralVo>> apiResult = new ApiResult<List<IntegralVo>>();

		if(null == userid){
			apiResult.toJSONString(-1, "参数userid不能为空");
		}
		
		List<IntegralVo> integralList = userCardManualService.queryIntegral(userid,1);

		// 数据返回
		return apiResult.toJSONString(0, "获取用户积分信息成功", integralList);
	}
	
	/**
	 * 待管理积分列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: cardDustbin 
	 * @param userid
	 * @return
	 * @date 2015年12月22日 下午10:10:41  
	 * @author xiongbin
	 */
	@RequestMapping(value="/dustbin/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String cardDustbinList(Long userid) {
		ApiResult<List<UserCard>> apiResult = new ApiResult<List<UserCard>>();
		
		//业务实现
		List<UserCard> cardList = getUserCardList(userid,null,-1);

		//数据返回
		return apiResult.toJSONString(0, "积分查询成功", cardList);
	}
	
	/**
	 * 待管理积分总数
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: cardDustbinCount 
	 * @param userid
	 * @return
	 * @date 2015年12月23日 下午2:30:44  
	 * @author xiongbin
	 */
	@RequestMapping(value="/dustbin/count/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String cardDustbinCount(Long userid) {
		ApiResult<Integer> apiResult = new ApiResult<>();
		
		List<UserCard> cardList = null;
		UserCard userCard = new UserCard();
		userCard.setUserId(userid);
		userCard.setStatus(-1);
		cardList = userCardService.selectByUserCard2(userCard,"1,2,3");

		return apiResult.toJSONString(0, "查询成功", cardList.size());
	}
	
	/**
	 * 返回用户积分数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getUserCardList 
	 * @param userid		用户ID
	 * @param cardType		卡片类型
	 * @param status		查询状态
	 * @return
	 * @date 2015年12月24日 下午2:47:53  
	 * @author xiongbin
	 */
	private List<UserCard> getUserCardList(Long userid,Integer cardType,Integer status){
		List<UserCard> cardList = null;
		UserCard userCard = new UserCard();
		userCard.setUserId(userid);
		userCard.setStatus(status);
		
		if(status.equals(1)){
			userCard.setTypeId(cardType);
			cardList = userCardService.selectByUserCard2(userCard,null);
		}else{
			cardList = userCardService.selectByUserCard2(userCard,"1,2,3");
		}

		List<UserCard> zhaoShangResults = new ArrayList<UserCard>();
		Map<String,List<UserCard>> cardMap = new HashMap<String,List<UserCard>>();
		//数据处理
		for(int i=0;i<cardList.size();i++){
			userCard = cardList.get(i);
			
			if(StringUtils.isNotBlank(userCard.getCardTypePic())){
				//补全图片路径
				userCard.setCardTypePic( appConfig.staticUrl + userCard.getCardTypePic());
			}
			//抹掉邮箱密码
			userCard.setEmailPassword("");
			
			if(StringUtils.isNotBlank(userCard.getTypeName()) && userCard.getTypeName().contains("招商")) {
				userCard.setTypeName(userCard.getTypeName().substring(0, 4));
				String username = userCard.getName();
				zhaoShangResults.add(userCard);
				List<UserCard> tempResult = new ArrayList<>();
				if(null == cardMap.get(username)){
					tempResult.add(userCard);
					cardMap.put(username, tempResult);
				}else{
					tempResult = cardMap.get(username);
					tempResult.add(userCard);
				}
			}
			
			if(UserCardManual.AIRCHINA_INTEGRAL.equals(userCard.getCardTypeId()) && userCard.getCardNo()!=null && !"".equals(userCard.getCardNo())){
				String cardNo = userCard.getCardNo();
				
				if(StringUtils.isNotBlank(cardNo) && !"--".equals(cardNo)){
					Integer length = cardNo.length();
					userCard.setCardNo(cardNo.substring(length-4,length));
				}
			}

			//如果用户名为身份证,加密
			String name = userCard.getName();
			if(StringUtils.isNotBlank(name) && IdCardValidator.valideIdCard(name)){
				StringBuffer temp = new StringBuffer();
				temp.append(name.substring(0,1));
				for(int j=1;j<name.length()-1;j++){
					temp.append("*");
				}
				temp.append(name.subSequence(name.length()-1, name.length()));
				name = temp.toString();
				
				userCard.setName(name);
			}
		}
		//处理招商银行问题
//		dealZhaoShanCard(cardList, cardMap, zhaoShangResults);
		
		return cardList;
	}
	
	/**
	 * 待管理积分列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: cardDustbinList2 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年2月29日 下午2:50:10  
	 * @author xiongbin
	 */
	@RequestMapping(value="/dustbin/list/2.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String cardDustbinList2(Long userid) {
		ApiResult<List<UserCard>> apiResult = new ApiResult<List<UserCard>>();
		
		//业务实现
		List<UserCard> cardList = getUserCardList(userid,null,-1);
		
		for(UserCard card : cardList){
			//设置新返回字段
			setNewFields(card);
		}

		//数据返回
		return apiResult.toJSONString(0, "积分查询成功", cardList);
	}
	
	/**
	 * 设置新返回字段
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: setNewFields 
	 * @param userCard
	 * @date 2016年2月29日 下午2:49:10  
	 * @author xiongbin
	 */
	private void setNewFields(UserCard userCard){
		//是否支持积分变现(0:不支持;1:支持;-1:不显示)
		Integer isRealization = 0;
		//是否支持在线办卡(0:不支持;1:支持;-1:不显示)
		Integer isCard = 0;
		//是否有精选商城(0:不支持;1:支持;-1:不显示)
		Integer isShop = 0;
		
		/** 积分变现,精选商城 */
		Long cardTypeId = userCard.getCardTypeId();
		if(null != cardTypeId){
			CardType cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
			if(null != cardType){
				Long realizationId = cardType.getRealizePlatformId();
				if(null != realizationId && !realizationId.equals(-1L)){
					RealizePlatform realizationPlatform = realizationPlatformService.selectByPrimaryKey(realizationId);
					if(null != realizationPlatform){
						isRealization = 1;
					}
				}
				
				String urlThird = cardType.getUrlThird();
				if(StringUtils.isNotBlank(urlThird)){
					isShop = 1;
				}

				Integer typeId = cardType.getTypeId();
				if(typeId.equals(1)){
					List<BankCard> bankCardDropInList = bankCardService.selectDropInPageList();
					List<BankCard> bankCardOnLineList = bankCardService.selectOnLinePageList();
	
					if(null!=bankCardDropInList && bankCardDropInList.size()>0){
						for(BankCard bankCard : bankCardDropInList){
							if(bankCard.getBankName().equals(userCard.getTypeName())){
								isCard = 1;
								break;
							}
						}
					}
					if(null!=bankCardOnLineList && bankCardOnLineList.size()>0){
						for(BankCard bankCard : bankCardOnLineList){
							if(bankCard.getBankName().equals(userCard.getTypeName())){
								isCard = 1;
								break;
							}
						}
					}
				}else{
					isCard = -1;
				}
			}
		}
		
		userCard.setIsCard(isCard);
		userCard.setIsRealization(isRealization);
		userCard.setIsShop(isShop);
	}
}
