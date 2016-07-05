/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.merchant.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.merchant.dao.SmspayPlatformMapper;
import com.caitu99.service.merchant.domain.ProxyRelation;
import com.caitu99.service.merchant.domain.ProxyTransaction;
import com.caitu99.service.merchant.domain.ProxyTransactionItem;
import com.caitu99.service.merchant.domain.SmspayPlatform;
import com.caitu99.service.merchant.domain.SmspayRecord;
import com.caitu99.service.merchant.dto.SettleDataDto;
import com.caitu99.service.merchant.dto.SettlePlatformDto;
import com.caitu99.service.merchant.service.ProxyRelationService;
import com.caitu99.service.merchant.service.ProxyTransactionService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ProxyTransactionController 
 * @author ws
 * @date 2016年6月20日 下午5:57:50 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/merchant/transaction/")
public class ProxyTransactionController {

	private static final Logger logger = LoggerFactory.getLogger(ProxyTransactionController.class);
	
	/**
	 * 
	 */
	private static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
	@Autowired
	ProxyTransactionService proxyTransactionService;
	@Autowired
	ProxyRelationService proxyRelationService;
	@Autowired
	UserService userService;
	@Autowired
	SmspayPlatformMapper smspayPlatformMapper;
	/**
	 * 查询我与下级结算数据
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryMyDownIntegralData 
	 * @param userId
	 * @param year
	 * @param week
	 * @return
	 * @date 2016年6月21日 下午5:18:18  
	 * @author ws
	 */
	@RequestMapping(value = "down/query/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String queryMyDownIntegralData(Long userId,Integer year,Integer week){
		ApiResult<List<SettleDataDto>> apiResult = new ApiResult<List<SettleDataDto>>();

		if(null == userId || null == year || null == week){

			return apiResult.toJSONString(-1, "必要参数不能为空");
		}
		
		try {
			int type = 1;//下级结算
			
			//获取我的下级
			List<ProxyRelation> myUnderlings = proxyRelationService.getMyUnderling(userId);
			List<SettleDataDto> SettleDataList = new ArrayList<SettleDataDto>();
			for (ProxyRelation proxyRelation : myUnderlings) {
				
				ProxyTransaction proxyTransaction = proxyTransactionService
						.querySetteDataIterate(userId, proxyRelation.getEmpUserId(), year, week, type);
				
				if(null != proxyTransaction){
					User user = userService.selectByPrimaryKey(proxyRelation.getEmpUserId());
					
					SettleDataDto settleData = new SettleDataDto();
					settleData.setCity(user.getCity());
					settleData.setEmpUserId(user.getId());
					settleData.setEmpUserName(user.getNick());
					settleData.setEndTime(DateUtil.DateToString(proxyTransaction.getEndTime(), DATE_FORMAT_YYYY_MM_DD));
					settleData.setId(proxyTransaction.getId());
					settleData.setIntegral(proxyTransaction.getIntegral());
					settleData.setMobile(user.getContacts());
					settleData.setProvince(user.getProvince());
					settleData.setRate(proxyTransaction.getRate());
					settleData.setStartTime(DateUtil.DateToString(proxyTransaction.getStartTime(), DATE_FORMAT_YYYY_MM_DD));
					settleData.setStatus(proxyTransaction.getStatus());
					settleData.setUserId(userId);
					
					SettleDataList.add(settleData);
				}
			}
			
			
			return apiResult.toJSONString(0, "success", SettleDataList);
		} catch (Exception e) {
			logger.error("查询我与下级结算数据",e);
			return apiResult.toJSONString(-1, "系统繁忙");
		}
	}
	
	
	/**
	 * 查询我与上级结算数据
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryMyLoadIntegralData 
	 * @param userId
	 * @param year
	 * @param week
	 * @return
	 * @date 2016年6月21日 下午5:17:56  
	 * @author ws
	 */
	@RequestMapping(value = "upper/query/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String queryMyLoadIntegralData(Long userId,Integer year,Integer week){
		ApiResult<Map<String,Object>> apiResult = new ApiResult<Map<String,Object>>();

		if(null == userId || null == year || null == week){

			return apiResult.toJSONString(-1, "必要参数不能为空");
		}
		try {
			Map<String,Object> resMap = new HashMap<String,Object>();
			
			int type = 2;//上级结算
			
			//获取我的下级
			List<ProxyRelation> myUnderlings = proxyRelationService.getMyUnderling(userId);
			List<SettleDataDto> SettleDataList = new ArrayList<SettleDataDto>();
			for (ProxyRelation proxyRelation : myUnderlings) {
				ProxyTransaction proxyTransaction = proxyTransactionService
						.querySetteDataIterate(userId, proxyRelation.getEmpUserId(), year, week, type);
				if(null != proxyTransaction){
					User user = userService.selectByPrimaryKey(proxyRelation.getEmpUserId());
					
					SettleDataDto settleData = new SettleDataDto();
					settleData.setCity(user.getCity());
					settleData.setEmpUserId(user.getId());
					settleData.setEmpUserName(user.getNick());
					settleData.setEndTime(DateUtil.DateToString(proxyTransaction.getEndTime(), DATE_FORMAT_YYYY_MM_DD));
					settleData.setId(proxyTransaction.getId());
					settleData.setIntegral(proxyTransaction.getIntegral());
					settleData.setMobile(user.getContacts());
					settleData.setProvince(user.getProvince());
					settleData.setRate(proxyTransaction.getRate());
					settleData.setStartTime(DateUtil.DateToString(proxyTransaction.getStartTime(), DATE_FORMAT_YYYY_MM_DD));
					settleData.setStatus(proxyTransaction.getStatus());
					settleData.setUserId(userId);
					
					SettleDataList.add(settleData);
				}
			}
			resMap.put("upper", SettleDataList);
			
			ProxyTransaction proxyTransaction = proxyTransactionService
					.queryMySetteData(userId, year, week, 1);//作为下级结算发起
			if(null != proxyTransaction){
				User user = userService.selectByPrimaryKey(userId);
				
				SettleDataDto settleData = new SettleDataDto();
				settleData.setCity(user.getCity());
				settleData.setEmpUserId(user.getId());
				settleData.setEmpUserName(user.getNick());
				settleData.setEndTime(DateUtil.DateToString(proxyTransaction.getEndTime(), DATE_FORMAT_YYYY_MM_DD));
				settleData.setId(proxyTransaction.getId());
				settleData.setIntegral(proxyTransaction.getIntegral());
				settleData.setMobile(user.getContacts());
				settleData.setProvince(user.getProvince());
				settleData.setRate(proxyTransaction.getRate());
				settleData.setStartTime(DateUtil.DateToString(proxyTransaction.getStartTime(), DATE_FORMAT_YYYY_MM_DD));
				settleData.setStatus(proxyTransaction.getStatus());
				settleData.setUserId(userId);
				
				resMap.put("my", settleData);
			}
			
			return apiResult.toJSONString(0, "success", resMap);
		} catch (Exception e) {
			logger.error("查询我与上级结算数据,{}",e);
			return apiResult.toJSONString(-1, "系统繁忙");
		}
		
	}
	
	
	
	/**
	 * 查询结算明细
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: querySettleDetail 
	 * @param userId
	 * @param year
	 * @param week
	 * @param empUserId
	 * @param settleId
	 * @return
	 * @date 2016年6月21日 下午5:17:44  
	 * @author ws
	 */
	@RequestMapping(value = "settle/detail/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String querySettleDetail(Long userId,Integer year,Integer week, Long empUserId, Long settleId){
		ApiResult<List<SettlePlatformDto>> apiResult = new ApiResult<List<SettlePlatformDto>>();
		
		if(null == userId || null == year || null == week 
				|| null == empUserId){

			return apiResult.toJSONString(-1, "必要参数不能为空");
		}
		List<SettlePlatformDto> settlePlatforms = new ArrayList<SettlePlatformDto>();
		try {

			List<ProxyTransactionItem> pTItem = proxyTransactionService.queryProxyTranItem(userId, year, week, empUserId, settleId);

			List<SmspayPlatform> smspPlatforms = smspayPlatformMapper.findAll();
			for (ProxyTransactionItem proxyTransactionItem : pTItem) {
				SettlePlatformDto spd = new SettlePlatformDto();
				
				String icon = getIcom(smspPlatforms, proxyTransactionItem);
				
				spd.setIcon(icon);
				spd.setId(proxyTransactionItem.getId());
				spd.setIntegral(proxyTransactionItem.getIntegral());
				spd.setPlatformId(proxyTransactionItem.getPlatformId());
				spd.setPlatformName(proxyTransactionItem.getPlatformName());
				spd.setProxyTransactionId(proxyTransactionItem.getProxyTransactionId());
				settlePlatforms.add(spd);
			}
			
			return apiResult.toJSONString(0, "success", settlePlatforms);
		} catch (Exception e) {
			logger.error("查询结算明细异常：{}",e);
			return apiResult.toJSONString(-1, "系统繁忙");
		}
	}


	private String getIcom(List<SmspayPlatform> smspPlatforms,
			ProxyTransactionItem proxyTransactionItem) {
		String icon = "";
		for (SmspayPlatform smspayPlatform : smspPlatforms) {
			if(smspayPlatform.getId().equals(proxyTransactionItem.getPlatformId())){
				icon = smspayPlatform.getIcon();
			}
		}
		return icon;
	}
	
	
	/**
	 * 查询交易记录
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryRecord 
	 * @param userId
	 * @param year
	 * @param week
	 * @param empUserId
	 * @param platformId
	 * @return
	 * @date 2016年6月21日 下午5:17:34  
	 * @author ws
	 */
	@RequestMapping(value = "record/query/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String queryRecord(Long userId,Integer year,Integer week, Long empUserId, Long platformId){
		ApiResult<List<SmspayRecord>> apiResult = new ApiResult<List<SmspayRecord>>();
		if(null == userId || null == year || null == week 
				|| null == empUserId ||  null == platformId){

			return apiResult.toJSONString(-1, "必要参数不能为空");
		}
		try {
			List<SmspayRecord> record = proxyTransactionService.queryProxyTranRecord(userId, empUserId, year, week, platformId);
			
			return apiResult.toJSONString(0, "success", record);
		} catch (Exception e) {
			logger.error("查询交易记录异常：{}",e);
			return apiResult.toJSONString(-1, "系统繁忙");
		}
		
	}
	

	/**
	 * 发起结算
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addSettle 
	 * @param userId
	 * @param year
	 * @param week
	 * @param empUserId
	 * @return
	 * @date 2016年6月21日 下午5:17:24  
	 * @author ws
	 */
	@RequestMapping(value = "settle/propose/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String addSettle(Long userId,Integer year,Integer week, Long empUserId){
		ApiResult<List<SmspayRecord>> apiResult = new ApiResult<List<SmspayRecord>>();
		if(null == userId || null == year || null == week 
				|| null == empUserId){

			return apiResult.toJSONString(-1, "必要参数不能为空");
		}
		try {

			String result = proxyTransactionService.proposeSettle(userId, empUserId, year, week);
			
			return apiResult.toJSONString(0, "success");
		} catch (Exception e) {
			logger.error("发起结算异常：{}",e);
			return apiResult.toJSONString(-1, "系统繁忙");
		}
	}
	
	

	/**
	 * 确认结算
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: confirmSettle 
	 * @param userId
	 * @param settleId
	 * @return
	 * @date 2016年6月21日 下午5:51:09  
	 * @author ws
	 */
	@RequestMapping(value = "settle/confirm/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String confirmSettle(Long userId,Long settleId){
		ApiResult<List<SmspayRecord>> apiResult = new ApiResult<List<SmspayRecord>>();
		if(null == userId || null == settleId){

			return apiResult.toJSONString(-1, "必要参数不能为空");
		}
		
		try {

			String result = proxyTransactionService.confirmSettle(userId, settleId);
			
			return apiResult.toJSONString(0, "success");
		} catch (Exception e) {
			logger.error("确认结算异常：{}",e);
			return apiResult.toJSONString(-1, "系统繁忙");
		}
	}
	
	


	/**
	 * 检查用户是否存在未结算分
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkSettle 
	 * @param userId
	 * @return
	 * @date 2016年6月22日 下午3:42:52  
	 * @author ws
	 */
	@RequestMapping(value = "settle/check/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String checkSettle(Long userId){
		ApiResult<Boolean> apiResult = new ApiResult<Boolean>();
		
		boolean isExist = proxyTransactionService.checkSettle(userId);
		
		return apiResult.toJSONString(0, "success", isExist);
	}
	
	
	
	
}
