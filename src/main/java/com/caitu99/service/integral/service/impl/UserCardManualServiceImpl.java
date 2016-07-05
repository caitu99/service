package com.caitu99.service.integral.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.exception.UserCardManualException;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.dao.BankMapper;
import com.caitu99.service.integral.dao.UserCardManualMapper;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ExchangeRuleService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.controller.vo.IntegralVo;
import com.caitu99.service.user.controller.vo.UserCardManualVo;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.card.CardTypes;
import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.string.IdCardValidator;

@Service
public class UserCardManualServiceImpl implements UserCardManualService {
	
	private final static Logger logger = LoggerFactory.getLogger(UserCardManualServiceImpl.class);

	@Autowired
	private UserCardManualMapper userCardManualMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private ExchangeRuleService exchangeRuleService;
	
	@Autowired
	private CardTypeService cardTypeService;
	
	@Autowired
	private BankMapper bankMapper;
	
	@Autowired
	private RealizeService realizeService; 

	@Override
	public void insert(UserCardManual userCardManual) throws UserCardManualException {
		try {
			AssertUtil.notNull(userCardManual, "对象不能为空");
			
			if(null == userCardManual.getGmtCreate()){
				Date now = new Date();
				userCardManual.setGmtCreate(now);
				userCardManual.setGmtModify(now);
			}
			userCardManualMapper.insert(userCardManual);
		} catch (Exception e) {
			logger.error("新增用户手动查询积分数据失败:" + e.getMessage(),e);
			throw new UserCardManualException(ApiResultCode.INSERT_USER_CARD_MANUAL_ERROR,e.getMessage());
		}
	}

	@Override
	public UserCardManual getByUserIdCardTypeId(Long userId,Long manualId,Long cardTypeId) throws UserCardManualException {
		try {
			AssertUtil.notNull(userId, "用户ID不能为空");
			AssertUtil.notNull(cardTypeId, "卡类型ID不能为空");
			
			Map<String,Object> map = new HashMap<String,Object>(3);
			map.put("userId", userId);
			map.put("cardTypeId", cardTypeId);
			map.put("manualId", manualId);
			
			UserCardManual userCardManual = userCardManualMapper.getByUserIdCardTypeId(map);
			
			return userCardManual;
		} catch (Exception e) {
			logger.error("查询用户手动查询积分数据失败:" + e.getMessage(),e);
			throw new UserCardManualException(ApiResultCode.QUERY_USER_CARD_MANUAL_ERROR,e.getMessage());
		}
	}

	@Override
	public void updateByPrimaryKeySelective(UserCardManual userCardManual) throws UserCardManualException {
		try {
			AssertUtil.notNull(userCardManual, "对象不能为空");
			AssertUtil.notNull(userCardManual.getId(), "对象ID不能为空");
			
			if(null == userCardManual.getGmtModify()){
				Date now = new Date();
				userCardManual.setGmtModify(now);
			}
			
			userCardManualMapper.updateByPrimaryKeySelective(userCardManual);
		} catch (Exception e) {
			logger.error("修改用户手动查询积分数据失败:" + e.getMessage(),e);
			throw new UserCardManualException(ApiResultCode.UPDATE_USER_CARD_MANUAL_ERROR,e.getMessage());
		}
	}

	@Override
	public List<UserCardManualVo> queryIntegral(Long userid)throws UserCardManualException {
		try {
			AssertUtil.notNull(userid, "用户ID不能为空");
			List<UserCardManualVo> list = userCardManualMapper.queryIntegral(userid);

			return list;
		} catch (Exception e) {
			logger.error("查询用户积分数据失败:" + e.getMessage(),e);
			throw new UserCardManualException(-1,"查询用户积分数据失败:" + e.getMessage());
		}
	}

	@Override
	public List<UserCardManualVo> queryIntegralRemoveRepetition(Long userid,Integer status) throws UserCardManualException {
		try {
			AssertUtil.notNull(userid, "参数userid不能为空");
			AssertUtil.notNull(status, "参数status不能为空");
			
			Map<String,Object> map = new HashMap<String,Object>(2);
			map.put("userId", userid);
			map.put("status", status);
			
			List<UserCardManualVo> list = userCardManualMapper.queryIntegralRemoveRepetition(map);

			return list;
		} catch (Exception e) {
			logger.error("查询用户积分数据失败:" + e.getMessage(),e);
			throw new UserCardManualException(-1,"查询用户积分数据失败:" + e.getMessage());
		}
	}
	
	@Override
	public void insertORupdate(UserCardManual userCardManual) throws UserCardManualException {
		try {
			AssertUtil.notNull(userCardManual, "对象不能为空");
			
			if(null == userCardManual.getId()){
				userCardManualMapper.insert(userCardManual);
			}else{
				userCardManual.setGmtCreate(null);
				userCardManualMapper.updateByPrimaryKeySelective(userCardManual);
			}
		} catch (Exception e) {
			logger.error("新增或修改用户手动查询积分数据失败:" + e.getMessage(),e);
			throw new UserCardManualException(ApiResultCode.INSERT_UPDATE_USER_CARD_MANUAL_ERROR,e.getMessage());
		}
	}

	@Override
	public UserCardManual getUserCardManualSelective(Long userId,Long cardTypeId,String cardNo,String userName,String loginAccount) {
		try {
			AssertUtil.notNull(userId, "用户ID不能为空");
			AssertUtil.notNull(cardTypeId, "卡类型ID不能为空");

			Map<String,Object> map = new HashMap<String,Object>();
			map.put("userId", userId);
			map.put("cardTypeId", cardTypeId);
			map.put("cardNo", cardNo);
			map.put("userName", userName);
			map.put("loginAccount", loginAccount);
			
			List<UserCardManual> userCardManual = userCardManualMapper.getUserCardManualSelective(map);
			if(null != userCardManual && userCardManual.size() > 0){
				return userCardManual.get(0);
			}else{
				return null;
			}
		} catch (Exception e) {
			logger.error("查询用户手动查询积分数据失败:" + e.getMessage(),e);
			throw new UserCardManualException(ApiResultCode.QUERY_USER_CARD_MANUAL_ERROR,e.getMessage());
		}
	}

	@Override
	public UserCardManual selectByPrimaryKey(Long id) {
		try {
			AssertUtil.notNull(id, "ID不能为空");

			UserCardManual userCardManual = userCardManualMapper.selectByPrimaryKey(id);
			
			return userCardManual;
		} catch (Exception e) {
			logger.error("根据主键查询用户手动查询积分数据失败:" + e.getMessage(),e);
			throw new UserCardManualException(ApiResultCode.QUERY_USER_CARD_MANUAL_ERROR,e.getMessage());
		}
	}
	
	@Override
	public List<IntegralVo> queryIntegral(Long userid,Integer status){
		if(null == status){
			logger.info("查询用户积分失败:参数status不能为空");
			return new ArrayList<IntegralVo>();
		}else if(!status.equals(1) && !status.equals(-1)){
			logger.info("查询用户积分失败:参数status传递错误");
			return new ArrayList<IntegralVo>();
		}
		
		// 业务实现
		User loginUser = userService.getById(userid);
		// validate
		if (null == loginUser) {
			throw new UserNotFoundException(-1, "用户不存在");
		}

		//获取用户手动查询所拥有的卡片
		List<UserCardManualVo> list = this.queryIntegralRemoveRepetition(userid,status);
		
		//卡片总数
		Integer count = 0;
		
		Map<Integer, IntegralVo> map = new HashMap<>();
//		Integer zhaoshanTypeId = null;
//		Map<String,List<UserCardManualVo>> zhaoshangMap = new HashMap<String,List<UserCardManualVo>>();
//		
//		if(list != null){
//			for(UserCardManualVo userCard : list){
//				//招商信用卡多种类型统一只算一种
//				if(userCard.getCardTypeId().equals(2L) || userCard.getCardTypeId().equals(8L) 
//								|| userCard.getCardTypeId().equals(19L) || userCard.getCardTypeId().equals(26L)){
//					String userName = userCard.getUserName();
//					
//					List<UserCardManualVo> userCardManualVoList = null;
//					if(zhaoshangMap.get(userName) == null){
//						userCardManualVoList = new ArrayList<UserCardManualVo>();
//						userCardManualVoList.add(userCard);
//					}else{
//						userCardManualVoList = zhaoshangMap.get(userName);
//						userCardManualVoList.add(userCard);
//					}
//					zhaoshangMap.put(userCard.getUserName(),userCardManualVoList);
//					zhaoshanTypeId = userCard.getTypeId();
//				}else{
//					IntegralVo integralVo = map.get(userCard.getTypeId());
//					map.put(userCard.getTypeId(), getIntegral(integralVo, userCard));
//					count++;
//				}
//			}
//			
//			count = count + 1;
//		}
		
		if (list != null) {
			Map<Long,UserCardManualVo> zhaoshangMap = new HashMap<Long,UserCardManualVo>();
			for (UserCardManualVo userCard : list) {
				IntegralVo integralVo = map.get(userCard.getTypeId());
				map.put(userCard.getTypeId(), getIntegral(integralVo, userCard));
				
				//招商信用卡多种类型统一只算一种
				if(userCard.getCardTypeId().equals(2L) || userCard.getCardTypeId().equals(8L) 
								|| userCard.getCardTypeId().equals(19L) || userCard.getCardTypeId().equals(26L)){
					zhaoshangMap.put(userCard.getCardTypeId(), userCard);
				}
				
				count++;
			}
			
			if(zhaoshangMap.size() > 1){
				count = count - (zhaoshangMap.size() - 1);
			}
		}

		List<IntegralVo> integralList = new ArrayList<>();
//		for(Integer key : map.keySet()){
//			IntegralVo caifen = map.get(key);
//			caifen.setIntegral(0);
//			
//			if(key.equals(zhaoshanTypeId)){
//				for(String userName : zhaoshangMap.keySet()){
//					List<UserCardManualVo> userCardManualVoList = zhaoshangMap.get(userName);
//					UserCardManualVo temp = null;
//					for(UserCardManualVo userCardManualVo : userCardManualVoList){
//						if(null==temp || userCardManualVo.getIntegral()<temp.getIntegral()){
//							temp = userCardManualVo;
//						}
//					}
//					if(temp != null){
//						getIntegral(caifen, temp);
//					}
//				}
//			}
//			
//			integralList.add(caifen);
//		}
		
		//保留两位小数点
		DecimalFormat df = new DecimalFormat("#.00"); 
		
		for (Integer key : map.keySet()) {
			IntegralVo caifen = map.get(key);
			if(null != caifen){
				caifen.setIntegral(0);
				double money = caifen.getMoney();
				caifen.setMoney(Double.parseDouble(df.format(money)));
				integralList.add(caifen);
			}
		}
		
		// 用户的财币
		Account account = accountService.selectByUserId(userid);
		Long availableIntegral = 0L;
		Long tubi = 0L;
		if(account!=null){
			availableIntegral = account.getAvailableIntegral();
			tubi = account.getTubi() == null?0L:account.getTubi();
			if(null == availableIntegral){
				availableIntegral = 0L;
			}
		}
		
		

		//用户冻结财币
		Map<String,Long> mapCash = realizeService.selectFreezeCashByUserId(userid);
		availableIntegral += mapCash.get("total");
		tubi += mapCash.get("tubi");
		
		IntegralVo tubiCaifen = new IntegralVo();
		tubiCaifen.setType(4);//途币
		tubiCaifen.setIntegral(tubi.intValue());
		tubiCaifen.setMoney(tubi.intValue() * appConfig.exchangeScale);
		tubiCaifen.setSize(0);
		integralList.add(tubiCaifen);

		IntegralVo caifen = new IntegralVo();
		caifen.setType(CardTypes.CAIFEN.getValue());
		caifen.setIntegral(availableIntegral.intValue());
		caifen.setMoney(caifen.getIntegral() * appConfig.exchangeScale);
		caifen.setSize(count);
		integralList.add(caifen);
		
		return integralList;
	}
	
	private IntegralVo getIntegral(IntegralVo integralVo, UserCardManualVo userCard) {
		if (null == integralVo) {
			integralVo = new IntegralVo();
			integralVo.setType(userCard.getTypeId());
			if (userCard.getIntegral() != null && userCard.getIntegral() != -1) {
				integralVo.setMoney(appConfig.exchangeScale * userCard.getIntegral() * userCard.getScale());
			}else{
				integralVo.setMoney(0d);
			}
		} else {
			if(integralVo.getMoney() == null){
				integralVo.setMoney(0d);
			}
			integralVo.setMoney(integralVo.getMoney() 
					+ appConfig.exchangeScale
					* ((userCard.getIntegral() == null || userCard.getIntegral() == -1) ? 0
							: userCard.getIntegral())
					* userCard.getScale());
		}
		return integralVo;
	}

	@Override
	public List<UserCardManual> selectByUserIdManualId(Long userId,Long manualId) {
		if(null==userId || null==manualId){
			return null;
		}
		
		Map<String,Object> map = new HashMap<String,Object>(2);
		map.put("userId", userId);
		map.put("manualId", manualId);
		return userCardManualMapper.selectByUserIdManualId(map);
	}

	@Override
	public CardIntegralLastTime selectLastTimeFirst(Long userId) {
		List<UserCardManualVo> list = userCardManualMapper.selectLastTime(userId);
		Date now = new Date();
		Date day30 = DateUtil.nextMonthFirstDate();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if(null!=list && list.size()>0){
			UserCardManualVo temp = null;
			Date tempDate = null;
			boolean tempFlag = false;
			for(UserCardManualVo userCardManual : list){
				Date nowExpirationTimes = null;
				boolean flag = false;
				
				Date expirationTime = userCardManual.getExpirationTime();
				if(null != expirationTime){
					//有过期时间,判断是否有下个月过期积分
					Integer nextExpirationIntegral = userCardManual.getNextExpirationIntegral();
					if(null!=nextExpirationIntegral && nextExpirationIntegral>0){
						//比对过期时间是否超过30天
						if(expirationTime.after(day30)){
//						if(java.sql.Date.valueOf(sdf.format(expirationTime)).after(java.sql.Date.valueOf(sdf.format(day30)))){
							nowExpirationTimes = day30;
							flag = true;
						}else{
							nowExpirationTimes = expirationTime;
							flag = false;
						}
					}else{
						nowExpirationTimes = expirationTime;
						flag = false;
					}
				}else{
					Integer nextExpirationIntegral = userCardManual.getNextExpirationIntegral();
					if(null!=nextExpirationIntegral && nextExpirationIntegral>0){
						nowExpirationTimes = day30;
						flag = true;
					}
				}
				
				if(null == temp){
					temp = userCardManual;
					tempDate = nowExpirationTimes;
					tempFlag = flag;
				}else if(tempDate.after(nowExpirationTimes)){
					temp = userCardManual;
					tempDate = nowExpirationTimes;
					tempFlag = flag;
				}
			}
			
			if(temp != null){
				CardIntegralLastTime cardIntegralLastTime = new CardIntegralLastTime();
				//过期天数
				Long datenum = ((tempDate.getTime() - now.getTime()) / (1000*60*60*24));
				if(tempFlag){
					cardIntegralLastTime.setBalance(temp.getNextExpirationIntegral());
				}else{
					cardIntegralLastTime.setBalance(temp.getExpirationIntegral());
				}
				Long cardTypeId = temp.getCardTypeId();
				CardType cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
				cardIntegralLastTime.setBankname(cardType.getName());
				
				String cardNo = temp.getCardNo();
				if(cardNo != null){
					if(cardNo.indexOf(",")!=-1 && cardNo.length()>9){
						cardNo = cardNo.substring(0, 10);
					}
				}else{
					cardNo = temp.getLoginAccount();
					if(IdCardValidator.valideIdCard(cardNo)){
						cardNo = IdCardValidator.encryption(cardNo);
					}
				}
				
				cardIntegralLastTime.setCardNo(cardNo);
				cardIntegralLastTime.setCardTypeId(cardTypeId);
				cardIntegralLastTime.setDatenum(datenum.intValue());
				cardIntegralLastTime.setGmtEffective(tempDate);
				cardIntegralLastTime.setName(temp.getUserName());
				ExchangeRule exchangeRule = exchangeRuleService.findByCardTypeId(cardTypeId);
				if(exchangeRule!=null){
					cardIntegralLastTime.setScale(exchangeRule.getScale());
				}
				
				cardIntegralLastTime.setPicUrl(temp.getPicUrl());				
				return cardIntegralLastTime;
			}
		}
		return null;
	}
}
