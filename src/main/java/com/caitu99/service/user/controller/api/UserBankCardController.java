package com.caitu99.service.user.controller.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.user.domain.UserBankCard;
import com.caitu99.service.user.service.UserBankCardService;
import com.caitu99.service.utils.unionpay.UnionOpens;

@Controller
@RequestMapping("/api/user/bankcard")
public class UserBankCardController extends BaseController {

	private final static String[] USER_BANKCARD_LIST_FILTER = {"id","bankName","cardType","picUrl","cardNo","accName","accId"};

	@Autowired
	private UserBankCardService userBankCardService;

	@Autowired
	private AppConfig appConfig;
	
	@RequestMapping(value = "/list/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String list(Long userId){
		ApiResult<List<UserBankCard>> apiResult = new ApiResult<List<UserBankCard>>();
		List<UserBankCard> list = userBankCardService.selectByUserId(userId,0);
		for(UserBankCard ubc : list){
			if(StringUtils.isNotBlank(ubc.getPicUrl())){
				ubc.setPicUrl(appConfig.staticUrl + ubc.getPicUrl());
			}
		}
		return apiResult.toJSONString(0, "SUCCESS", list);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/add/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String add(Long userId,String cardNo, String accName, String accId
			){
		ApiResult<Long> apiResult = new ApiResult<Long>();
		
		if(null == userId || StringUtils.isBlank(cardNo) || StringUtils.isBlank(accName)
				|| StringUtils.isBlank(accId)){
			return apiResult.toJSONString(4101, "参数不完整");
		}
		UnionOpens unionOpens = new UnionOpens();
		Map map = null;
		try {
			map = unionOpens.getBankInfo(cardNo);
		} catch (Exception e) {
			return apiResult.toJSONString(4115, "获取银行卡信息失败");
		}
		if(!"0000".equals(map.get("retCode"))){
			return apiResult.toJSONString(4115, "获取银行卡信息失败");
		}
		String bankName = map.get("bankName").toString();
		if(StringUtils.isBlank(map.get("bankName").toString())){
			return apiResult.toJSONString(4116, "未获取到银行卡信息");
		}
		String bankId = map.get("bankId").toString();
		//查询表中是否有这个用户的数据
		List<UserBankCard> list = userBankCardService.selectByUserId(userId,0);

		UserBankCard userBankCard = new UserBankCard();
		userBankCard.setUserId(userId);
		userBankCard.setCardNo(cardNo);
		userBankCard.setAccName(accName);
		userBankCard.setAccId(accId);
		userBankCard.setBankName(bankName);
		userBankCard.setBankId(bankId);
		userBankCard.setType(1);
		//当表中有这个用户时，取表中的这个用户的身份证姓名和身份证号码
		if(null != list && list.size() > 0){
			userBankCard.setAccName(list.get(0).getAccName());
			userBankCard.setAccId(list.get(0).getAccId());
		}
		Long id = userBankCardService.saveUserBankCard(userBankCard);
		return apiResult.toJSONString(0, "SUCCESS", id);
	}
	
	@RequestMapping(value = "/del/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String del(Long id){
		ApiResult<String> apiResult = new ApiResult<String>();
		userBankCardService.delUserBankCard(id);
		return apiResult.toJSONString(0, "SUCCESS");
	}
	
	/**
	 * 查询用户银行卡列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: userlist 
	 * @param userId	用户ID
	 * @return
	 * @date 2016年6月7日 下午5:41:31  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/pay/list/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String userlist(Long userid){
		ApiResult<List<UserBankCard>> apiResult = new ApiResult<List<UserBankCard>>();
		if(null == userid){
			return apiResult.toJSONString(-1, "参数userid不能为空");
		}
		
		List<UserBankCard> list = userBankCardService.selectByUserId(userid,1);
		
		for(UserBankCard ubc : list){
			if(StringUtils.isNotBlank(ubc.getPicUrl())){
				ubc.setPicUrl(appConfig.staticUrl + ubc.getPicUrl());
			}else{
				ubc.setPicUrl("");
			}
			
			String cardNo = ubc.getCardNo();
			if(StringUtils.isBlank(cardNo)){
				ubc.setCardNo("");
			}else{
//				StringBuffer cardNoNew = new StringBuffer();
//				for(int i=0;i<cardNo.length()-4;i++){
//					cardNoNew.append("*");
//				}
//				cardNoNew.append(cardNo.substring(cardNo.length()-4, cardNo.length()));
//				
//				Integer index = 0;
//				for(int i=0;i<cardNoNew.length();i++){
//					index++;
//					if(index == 4){
//						cardNoNew.insert(i+1, " ");
//						index = 0;
//					}
//				}
				
				ubc.setCardNo(cardNo.substring(cardNo.length()-4, cardNo.length()));
			}
		}
		
		return apiResult.toJSONString(0, "SUCCESS", list,UserBankCard.class,USER_BANKCARD_LIST_FILTER);
	}
	
	/**
	 * 解绑
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: unbound 
	 * @param userid	用户ID
	 * @param id		银行卡ID
	 * @return
	 * @date 2016年6月8日 下午3:03:58  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/pay/unbound/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String unbound(Long userid,String ids){
		ApiResult<List<UserBankCard>> apiResult = new ApiResult<List<UserBankCard>>();
		if(null == userid){
			return apiResult.toJSONString(-1, "参数userid不能为空");
		}else if(StringUtils.isBlank(ids)){
			return apiResult.toJSONString(-1, "参数ids不能为空");
		}
		
		JSONArray arr = JSON.parseArray(ids);
		String[] jsonIds = new String[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			jsonIds[i] = arr.getString(i);
		}
		
		userBankCardService.unboundUserBankCard(userid,jsonIds);
		
		return apiResult.toJSONString(0, "SUCCESS");
	}
}
