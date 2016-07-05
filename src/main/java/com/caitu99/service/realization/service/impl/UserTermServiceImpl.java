package com.caitu99.service.realization.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.AppConfig;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.BankService;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.realization.dao.UserTermMapper;
import com.caitu99.service.realization.domain.RealizePlatform;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.domain.UserTerm;
import com.caitu99.service.realization.service.RealizePlatformService;
import com.caitu99.service.realization.service.UserAddTermService;
import com.caitu99.service.realization.service.UserTermService;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.utils.string.IdCardValidator;
import com.caitu99.service.utils.string.StrUtil;


@Service
public class UserTermServiceImpl implements UserTermService {

	private final static Logger logger = LoggerFactory.getLogger(UserTermServiceImpl.class);

	private final static String[] USER_CARD_USER_LIST_FILTER = {"id","cardNo","name","integralBalance","importType","cardTypeId"};
	
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private UserAddTermService userAddTermService;
	@Autowired
	private CardTypeService cardTypeService;
	@Autowired
	private UserTermMapper userTermMapper;
	@Autowired
	private RealizePlatformService realizationPlatformService;
	@Autowired
	private BankService bankService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private UserCardManualService userCardManualService;
	
	@Override
	public List<JSONObject> selectRealizationList(Long userid,String version,boolean isBindOnly) {
		UserCard userCard = new UserCard();
		userCard.setUserId(userid);
		userCard.setStatus(1);
		
		//手动查询与邮箱
		List<UserCard> cardList = userCardService.selectByUserCard2(userCard,"1,2,3");
		JSONArray jsonArray = new JSONArray();
		
		if(null != cardList && cardList.size()>0){
			SimplePropertyPreFilter filter = new SimplePropertyPreFilter(UserCard.class,USER_CARD_USER_LIST_FILTER);
			String reslut = JSON.toJSONString(cardList, filter);
			jsonArray = JSONArray.parseArray(reslut);
		}
		
		//用户自己添加
		List<UserAddTerm> userAddTermList = userAddTermService.selectListByUserId(userid);
		
		if(null != userAddTermList && userAddTermList.size()>0){
			for(UserAddTerm userAddTerm : userAddTermList){
				String loginAccount = userAddTerm.getLoginAccount();
				if(StrUtil.isPhone(loginAccount)){
					loginAccount = StrUtil.phoneEncrypt(loginAccount);
				}else if(StrUtil.isEmail(loginAccount)){
					loginAccount = StrUtil.emailEncrypt(loginAccount);
				}else if(IdCardValidator.valideIdCard(loginAccount)){
					loginAccount = IdCardValidator.encryption(loginAccount);
				}else{
					loginAccount = loginAccount.substring(0, 3) + "*****" + loginAccount.substring(loginAccount.length()-3, loginAccount.length());
				}
				
				JSONObject json = new JSONObject();
				json.put("remoteId", userAddTerm.getId());
				json.put("cardName", "");
				json.put("name", loginAccount);
				json.put("importType", 2);
				json.put("cardTypeId", userAddTerm.getCardTypeId());
				
				json.put("cardNo", "");
				json.put("integralBalance", 0);
				
				jsonArray.add(json);
			}
		}
		
		//银行图标
		String icon = "";
		
		List<JSONObject> list = new ArrayList<JSONObject>();
		
		for(int i=0;i<jsonArray.size();i++){
			JSONObject json = jsonArray.getJSONObject(i);
			Long cardTypeId = json.getLong("cardTypeId");
			if(null != cardTypeId){
				CardType cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
				if(null != cardType){
					String cardName = cardType.getName();
					if(cardName.indexOf("招商") != -1){
						cardName = "招商银行";
					}
					json.put("cardName", cardName);
					
					Long platformId = cardType.getRealizePlatformId();
					if(null == platformId || platformId.equals(-1L)){
						continue;
					}
					
					RealizePlatform realizationPlatform = realizationPlatformService.selectByPrimaryKey(platformId);
					if(null == realizationPlatform){
						continue;
					}
					
					if(realizationPlatform.getVersion().compareTo(version) > 0){
						continue;
					}
					
					json.put("platformId", platformId);
					icon = realizationPlatform.getIcon();

					
					//新增字母排序
					String sort = realizationPlatform.getSort();
					json.put("sort", sort);
				}
			}
			
			Long cardId = json.getLong("remoteId");
			if(null == cardId){
				cardId = json.getLong("id");
				json.put("remoteId", cardId);
				json.remove("id");
				
				String name = json.getString("name");
				if(null != name){
					if(IdCardValidator.valideIdCard(name)){
						name = IdCardValidator.encryption(name);
					}
					json.put("name", name);
				}
				
				String cardNo = json.getString("cardNo");
				if(null != cardNo){
					if(IdCardValidator.valideIdCard(cardNo)){
						cardNo = IdCardValidator.encryption(cardNo);
					}
					json.put("cardNo", cardNo);
				}else{
					json.put("cardNo","");
				}
			}
			
			Integer importType = json.getInteger("importType");
			
			Integer remoteType = -1;
			if(UserCard.IMPORT_TYPE_EMAIL.equals(importType)){
				remoteType = 1;
			}else if(UserCard.IMPORT_TYPE_MANUAL.equals(importType)){
				remoteType = 2;
			}else if(UserCard.IMPORT_TYPE_USER_ADD.equals(importType)){
				remoteType = 3;
			}
			
			json.put("remoteType", remoteType);
			
			//是否绑定(0:未绑定;1:绑定)
			Integer isBinding = 0;
			String info = "";
			
			UserTerm userTerm = this.selectUserTerm(userid, cardId, remoteType);
			if(null != userTerm){
				info = userTerm.getInfo();
				isBinding = 1;
			}
			
			//isBindOnly为true时只取已绑定了的,未绑定的不取
			if(isBindOnly && isBinding == 0){
				continue;
			}
			
			if(isBindOnly){
				String cardNoPay = "";
				if(null != info){
					JSONObject infoObj = JSON.parseObject(info);
					if(null != infoObj.getString("cardNo")){
						cardNoPay = infoObj.getString("cardNo");
						if(cardNoPay.length() >= 4){
							cardNoPay = cardNoPay.substring(cardNoPay.length() - 4);
						}
					}
				}
				json.put("cardNo", cardNoPay);
			}

			json.put("info", info);
			json.put("isBinding", isBinding);
			json.put("icon", icon);
			
			list.add(json);
		}
		
		//字母排序
		Collections.sort(list,new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				Integer flag = o1.getString("sort").compareTo(o2.getString("sort"));
				if(flag > 0){
					return 1;
				}else if(flag == 0){
					if(o1.getString("platformId").compareTo(o2.getString("platformId")) > 0){
						return 1;
					}else{
						return -1;
					}
				}else{
					return -1;
				}
			}
		});
		
		return list;
	}

	@Override
	public UserTerm selectUserTerm(Long userId, Long remoteId, Integer remoteType) {
		Map<String,String> map = new HashMap<String,String>(3);
		map.put("userId", userId.toString());
		map.put("remoteId", remoteId.toString());
		map.put("remoteType", remoteType.toString());
		return userTermMapper.selectUserTerm(map);
	}

	@Override
	public Long saveOrUpdate(UserTerm userTerm) {
		Date date = new Date();
		UserTerm ut = this.selectUserTerm(userTerm.getUserId(), userTerm.getRemoteId(), userTerm.getRemoteType());
		if(null == ut){
			userTerm.setStatus(1);
			userTerm.setCreateTime(date);
			userTerm.setUpdateTime(date);
			userTermMapper.insert(userTerm);
			return userTerm.getId();
		} else if(!userTerm.getInfo().equals(ut.getInfo())){
			UserTerm edit = new UserTerm();
			edit.setId(ut.getId());
			edit.setInfo(userTerm.getInfo());
			edit.setUpdateTime(date);
			userTermMapper.update(edit);
		}
		return ut.getId();
	}

	@Override
	public String selectLoginAccount(Long remoteId, Integer remoteType) {
		String loginAccount = "";
		if(remoteType.equals(1)){
		}else if(remoteType.equals(2)){
			UserCardManual userCardManual = userCardManualService.selectByPrimaryKey(remoteId);
			if(null != userCardManual){
				loginAccount = userCardManual.getLoginAccount();
			}
		}else{
			UserAddTerm userAddTerm = userAddTermService.selectByPrimaryKey(remoteId);
			loginAccount = userAddTerm.getLoginAccount();
		}
		return loginAccount;
	}
}
