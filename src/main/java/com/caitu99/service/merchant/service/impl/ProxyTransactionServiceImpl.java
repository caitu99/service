/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.merchant.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.caitu99.service.merchant.dao.ProxyRelationMapper;
import com.caitu99.service.merchant.dao.ProxyTransactionItemMapper;
import com.caitu99.service.merchant.dao.ProxyTransactionMapper;
import com.caitu99.service.merchant.dao.SmspayRecordMapper;
import com.caitu99.service.merchant.domain.ProxyRelation;
import com.caitu99.service.merchant.domain.ProxyTransaction;
import com.caitu99.service.merchant.domain.ProxyTransactionItem;
import com.caitu99.service.merchant.domain.SmspayRecord;
import com.caitu99.service.merchant.service.ProxyTransactionService;
import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.date.DateWeekMonthUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ProxyTransactionServiceImpl 
 * @author ws
 * @date 2016年6月20日 下午5:57:12 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class ProxyTransactionServiceImpl implements ProxyTransactionService {

	private static final Logger logger = LoggerFactory.getLogger(ProxyTransactionServiceImpl.class);
	
	@Autowired
	ProxyTransactionMapper proxyTransactionMapper;
	@Autowired
	SmspayRecordMapper smspayRecordMapper;
	@Autowired
	ProxyRelationMapper proxyRelationMapper;
	@Autowired
	ProxyTransactionItemMapper proxyTransactionItemMapper;
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.merchant.service.ProxyTransactionService#querySetteData(java.lang.Long, java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public ProxyTransaction querySetteData(Long userId, Long empUserId, Integer year,
			Integer week, Integer type) {

		ProxyTransaction settleData = null;
		
		//先从结算表中获取
		Map<String,Object> queryParam = new HashMap<String,Object>();
		queryParam.put("empUserId", empUserId);
		queryParam.put("year", year);
		queryParam.put("week", week);
		queryParam.put("type", type);
		settleData = proxyTransactionMapper.getSettleData(queryParam);
		if(null == settleData){//则从数据中获取
			logger.info("结算表中没有数据");
			settleData = createSettleData(userId, empUserId, year, week, type);

		}
		
		return settleData;
	}

	/**
	 * 构造结算数据
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: createSettleData 
	 * @param userId
	 * @param empUserId
	 * @param year
	 * @param week
	 * @param type	下级结算  or 上级结算
	 * @return
	 * @date 2016年6月21日 下午12:00:21  
	 * @author ws
	 */
	private ProxyTransaction createSettleData(Long userId, Long empUserId,
			Integer year, Integer week ,Integer type) {

		Date startDayOfWeek = DateWeekMonthUtil.getStartDateOfWeekNo(year, week);
		Date endDayOfWeek = DateWeekMonthUtil.getEndDateOfWeekNo(year, week);
		Date now = new Date();
		
		ProxyTransaction settleData;
		Long integral = collectSettleData(empUserId, startDayOfWeek, endDayOfWeek);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("empUserId", empUserId);
		param.put("userId", userId);
		ProxyRelation pRelation = proxyRelationMapper.selectRelationBy(param);
		
		if(null == pRelation){
			return null;
		}
		
		//构造结算数据
		settleData = new ProxyTransaction();
		settleData.setCreateTime(now);
		settleData.setEmpUserId(empUserId);
		settleData.setEndTime(endDayOfWeek);
		settleData.setStartTime(startDayOfWeek);
		settleData.setIntegral(integral);
		settleData.setProxyRelationId(pRelation.getId());
		settleData.setRate(pRelation.getRate());
		settleData.setStatus(1);//未结算
		settleData.setUserId(userId);
		settleData.setWeek(week);
		settleData.setYear(year);
		settleData.setType(type);
		return settleData;
	}

	/**
	 * 	汇总用户收分
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: collectSettleData 
	 * @param empUserId
	 * @param startDayOfWeek
	 * @param endDayOfWeek
	 * @date 2016年6月21日 上午11:01:37  
	 * @author ws
	*/
	private Long collectSettleData(Long empUserId, Date startDayOfWeek,
			Date endDayOfWeek) {
		Map<String,Object> queryParam = new HashMap<String,Object>();
		queryParam.put("empUserId", empUserId);
		queryParam.put("startDay", DateUtil.DateToString(startDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		queryParam.put("endDay", DateUtil.DateToString(endDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		Long integral = smspayRecordMapper.collectSettleData(queryParam);
		
		if(null == integral){
			integral = 0L;
		}
		
		return integral;
		
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.merchant.service.ProxyTransactionService#queryMySetteData(java.lang.Long, java.lang.Integer, java.lang.Integer, int)
	 */
	@Override
	public ProxyTransaction queryMySetteData(Long userId, Integer year,
			Integer week, int type) {

		ProxyTransaction settleData = null;
		
		//先从结算表中获取
		Map<String,Object> queryParam = new HashMap<String,Object>();
		queryParam.put("empUserId", userId);
		queryParam.put("year", year);
		queryParam.put("week", week);
		queryParam.put("type", type);
		settleData = proxyTransactionMapper.getSettleData(queryParam);
		if(null == settleData){//则从数据中获取
			ProxyRelation pRelation = proxyRelationMapper.selectMyLoad(userId);
			if(null == pRelation){
				return null;
			}
			logger.info("结算表中没有数据");
			settleData = createSettleData(pRelation.getUserId(), userId, year, week, type);

		}
		
		return settleData;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.merchant.service.ProxyTransactionService#getProxyTranItem(java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<ProxyTransactionItem> queryProxyTranItem(Long userId,
			Integer year, Integer week, Long empUserId, Long settleId) throws Exception {
		
		List<ProxyTransactionItem> settleDetail = null;
		if(null == settleId || settleId == 0L){//从数据中获取
			settleDetail = createSettleDetailDataIterator(userId, empUserId, year, week);
		}else{
			//结算明细表中获取
			settleDetail = proxyTransactionItemMapper.getBySettleId(settleId);
		}
		return settleDetail;
	}


	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: createSettleDetailData 
	 * @param userId
	 * @param empUserId
	 * @param year
	 * @param week
	 * @return
	 * @date 2016年6月21日 下午4:48:32  
	 * @author ws
	 * @throws Exception 
	*/
	private List<ProxyTransactionItem> createSettleDetailData(Long userId,
			Long empUserId, Integer year, Integer week) throws Exception {

		Date startDayOfWeek = DateWeekMonthUtil.getStartDateOfWeekNo(year, week);
		Date endDayOfWeek = DateWeekMonthUtil.getEndDateOfWeekNo(year, week);
		
		Map<String,Object> queryParam = new HashMap<String,Object>();
		queryParam.put("empUserId", empUserId);
		queryParam.put("startDay", DateUtil.DateToString(startDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		queryParam.put("endDay", DateUtil.DateToString(endDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		List<ProxyTransactionItem> settleItem = smspayRecordMapper.collectSettleDetailData(queryParam);
		
		return settleItem;
	}

	
	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: createSettleDetailData 
	 * @param userId
	 * @param empUserId
	 * @param year
	 * @param week
	 * @return
	 * @date 2016年6月21日 下午4:48:32  
	 * @author ws
	 * @throws Exception 
	*/
	private List<ProxyTransactionItem> createSettleDetailDataIterator(Long userId,
			Long empUserId, Integer year, Integer week) throws Exception {

		Date startDayOfWeek = DateWeekMonthUtil.getStartDateOfWeekNo(year, week);
		Date endDayOfWeek = DateWeekMonthUtil.getEndDateOfWeekNo(year, week);
		
		Map<String,Object> queryParam = new HashMap<String,Object>();
		queryParam.put("empUserId", empUserId);
		queryParam.put("startDay", DateUtil.DateToString(startDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		queryParam.put("endDay", DateUtil.DateToString(endDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		List<ProxyTransactionItem> settleItem = smspayRecordMapper.collectSettleDetailData(queryParam);
		
		//下级子项结算
		List<ProxyRelation> myUnderlings = proxyRelationMapper.getMyUnderling(empUserId);
		/*if(null == myUnderlings){//无子项
			return settleItem;
		}*/

		for (ProxyRelation proxyRelation : myUnderlings) {

			addSubSettleItem(startDayOfWeek, endDayOfWeek, settleItem,
					proxyRelation);
			
			//第三级子项结算   最后一级
			List<ProxyRelation> mySubUnderlings = proxyRelationMapper.getMyUnderling(proxyRelation.getEmpUserId());
			for (ProxyRelation proxyRelation2 : mySubUnderlings) {
				addSubSettleItem(startDayOfWeek, endDayOfWeek, settleItem,
						proxyRelation2);
			}
			
		}
		
		return settleItem;
	}

	private void addSubSettleItem(Date startDayOfWeek, Date endDayOfWeek,
			List<ProxyTransactionItem> settleItem, ProxyRelation proxyRelation)
			throws Exception {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("empUserId", proxyRelation.getEmpUserId());
		param.put("startDay", DateUtil.DateToString(startDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		param.put("endDay", DateUtil.DateToString(endDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		List<ProxyTransactionItem> subSettleItem = smspayRecordMapper.collectSettleDetailData(param);
		
		if(null == subSettleItem){
			throw new Exception("结算关系有误");
		}
		
		for (ProxyTransactionItem subItem : subSettleItem) {
			for (ProxyTransactionItem item : settleItem) {
				
				if(subItem.getPlatformId().equals(item.getPlatformId())){
					item.setIntegral(item.getIntegral() + subItem.getIntegral());
				}
				
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.merchant.service.ProxyTransactionService#queryProxyTranRecord(java.lang.Long, java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.Long)
	 */
	@Override
	public List<SmspayRecord> queryProxyTranRecord(Long userId, Long empUserId,
			Integer year, Integer week, Long platformId) {

		Date startDayOfWeek = DateWeekMonthUtil.getStartDateOfWeekNo(year, week);
		Date endDayOfWeek = DateWeekMonthUtil.getEndDateOfWeekNo(year, week);
		
		Map<String,Object> queryParam = new HashMap<String,Object>();
		queryParam.put("empUserId", empUserId);
		queryParam.put("startDay", DateUtil.DateToString(startDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		queryParam.put("endDay", DateUtil.DateToString(endDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		queryParam.put("platformId", platformId);
		List<SmspayRecord> recordList = smspayRecordMapper.queryRecord(queryParam);
		
		//下级子项结算
		List<ProxyRelation> myUnderlings = proxyRelationMapper.getMyUnderling(empUserId);
		for (ProxyRelation proxyRelation : myUnderlings) {
			addSubRecord(platformId, startDayOfWeek, endDayOfWeek,
					recordList, proxyRelation);
			
			//第三级子项结算
			List<ProxyRelation> thirdUnderlings = proxyRelationMapper.getMyUnderling(proxyRelation.getEmpUserId());
			for (ProxyRelation proxyRelation2 : thirdUnderlings) {
				addSubRecord(platformId, startDayOfWeek, endDayOfWeek,
						recordList, proxyRelation2);
			}
		}
		
		return recordList;
	}

	private void addSubRecord(Long platformId, Date startDayOfWeek,
			Date endDayOfWeek, 
			List<SmspayRecord> recordList, ProxyRelation proxyRelation) {
		Map<String,Object> subQueryParam = new HashMap<String,Object>();
		subQueryParam.put("empUserId", proxyRelation.getEmpUserId());
		subQueryParam.put("startDay", DateUtil.DateToString(startDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		subQueryParam.put("endDay", DateUtil.DateToString(endDayOfWeek, "yyyy-MM-dd hh:mm:ss"));
		subQueryParam.put("platformId", platformId);
		List<SmspayRecord> subRecord = smspayRecordMapper.queryRecord(subQueryParam);
		if(null != subRecord){
			recordList.addAll(subRecord);
		}
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.merchant.service.ProxyTransactionService#proposeSettle(java.lang.Long, java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String proposeSettle(Long userId, Long empUserId, Integer year,
			Integer week) throws Exception {
		Date now = new Date();
		//直接下级结算
		ProxyTransaction settleData = null;
		settleData = createSettleData(userId, empUserId, year, week, 1);
		if(null == settleData){
			throw new Exception("结算关系有误");
		}
		settleData.setStatus(2);//待确认状态
		settleData.setProposeTime(now);
		//保存数据
		proxyTransactionMapper.insert(settleData);
		//保存结算明细
		List<ProxyTransactionItem> records = this.createSettleDetailData(userId, empUserId, year, week);
		for (ProxyTransactionItem proxyTransactionItem : records) {
			proxyTransactionItem.setProxyTransactionId(settleData.getId());
			proxyTransactionItem.setCreateTime(now);
			proxyTransactionItemMapper.insert(proxyTransactionItem );
		}
		
		
		//下级子项结算
		List<ProxyRelation> myUnderlings = proxyRelationMapper.getMyUnderling(empUserId);
		if(null == myUnderlings){//无子项
			return "";
		}

		ProxyTransaction subSettleData = null;
		for (ProxyRelation proxyRelation : myUnderlings) {
			subSettleData = createSettleDataIterate(empUserId, proxyRelation.getEmpUserId(), year, week, 2);
			if(null == settleData){
				throw new Exception("结算关系有误");
			}
			subSettleData.setStatus(2);//待确认状态
			subSettleData.setProposeTime(now);
			//保存数据
			proxyTransactionMapper.insert(subSettleData);
			//保存结算明细
			List<ProxyTransactionItem> subRecords = this.createSettleDetailDataIterator(empUserId, proxyRelation.getEmpUserId(), year, week);
			for (ProxyTransactionItem proxyTransactionItem : subRecords) {
				proxyTransactionItem.setProxyTransactionId(subSettleData.getId());
				proxyTransactionItem.setCreateTime(now);
				proxyTransactionItemMapper.insert(proxyTransactionItem );
			}
		}
		return "success";
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.merchant.service.ProxyTransactionService#confirmSettle(java.lang.Long, java.lang.Integer)
	 */
	@Override
	public String confirmSettle(Long userId, Long settleId) {
		Date now =  new Date();
		ProxyTransaction record = proxyTransactionMapper.selectByPrimaryKey(settleId);
		if(null == record){
			logger.error("未找到相关结算记录，结算编号：{}",settleId);
			return "fail";
		}
		
		record.setStatus(3);//已确认
		record.setConfirmTime(now);
		
		proxyTransactionMapper.updateByPrimaryKey(record);
		
		return "success";
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.merchant.service.ProxyTransactionService#checkSettle(java.lang.Long)
	 */
	@Override
	public boolean checkSettle(Long userId) {
		//获取我的结算总分
		Map<String,Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("type", 1);
		Long settle1Integral = proxyTransactionMapper.collectAll(queryMap);
		settle1Integral = null == settle1Integral ? 0 : settle1Integral;
		/*//获取我与上级的结算总分
		queryMap.put("type", 2);
		Long settle2Integral = proxyTransactionMapper.collectAll(queryMap);*/
		//获取总收分
		Long myIntegral = smspayRecordMapper.collectAll(userId);
		myIntegral = null == myIntegral ? 0 : myIntegral;
		
		if(settle1Integral.equals(myIntegral)){
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.merchant.service.ProxyTransactionService#querySetteDataIterate(java.lang.Long, java.lang.Long, java.lang.Integer, java.lang.Integer, int)
	 */
	@Override
	public ProxyTransaction querySetteDataIterate(Long userId, Long empUserId,
			Integer year, Integer week, int type) throws Exception {
		ProxyTransaction settleData = null;
		
		//先从结算表中获取
		Map<String,Object> queryParam = new HashMap<String,Object>();
		queryParam.put("empUserId", empUserId);
		queryParam.put("year", year);
		queryParam.put("week", week);
		queryParam.put("type", type);
		settleData = proxyTransactionMapper.getSettleData(queryParam);
		if(null == settleData){//则从数据中获取
			logger.info("结算表中没有数据");
			settleData = createSettleDataIterate(userId, empUserId, year, week, type);

		}
		
		return settleData;
	}
	
	private ProxyTransaction createSettleDataIterate(Long userId, Long empUserId,
			Integer year, Integer week ,Integer type) throws Exception {

		//直接下级结算
		ProxyTransaction settleData = null;
		settleData = createSettleData(userId, empUserId, year, week, type);
		if(null == settleData){
			throw new Exception("结算关系有误");
		}
		
		//下级子项结算
		List<ProxyRelation> myUnderlings = proxyRelationMapper.getMyUnderling(empUserId);
		/*if(null == myUnderlings){//无子项
			return settleData;
		}
*/
		ProxyTransaction subSettleData = null;
		for (ProxyRelation proxyRelation : myUnderlings) {
			subSettleData = createSettleData(empUserId, proxyRelation.getEmpUserId(), year, week, type);
			if(null == settleData){
				throw new Exception("结算关系有误");
			}
			
			//第三级子项结算   最多三级
			ProxyTransaction thirdSubSettle = null;
			List<ProxyRelation> mySubUnderlings = proxyRelationMapper.getMyUnderling(proxyRelation.getEmpUserId());
			for (ProxyRelation proxyRelation3 : mySubUnderlings) {
				thirdSubSettle = createSettleData(proxyRelation.getEmpUserId(), proxyRelation3.getEmpUserId(), year, week, type);
				
				subSettleData.setIntegral(subSettleData.getIntegral() + thirdSubSettle.getIntegral());
			}
			
			settleData.setIntegral(settleData.getIntegral() + subSettleData.getIntegral());
		}
		return settleData;
	}

}
