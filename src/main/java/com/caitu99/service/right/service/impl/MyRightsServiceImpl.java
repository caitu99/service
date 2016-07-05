/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.right.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.right.dao.MyRightsMapper;
import com.caitu99.service.right.dao.RightCodeMapper;
import com.caitu99.service.right.dao.RightDetailMapper;
import com.caitu99.service.right.domain.MyRights;
import com.caitu99.service.right.domain.RightCode;
import com.caitu99.service.right.domain.RightDetail;
import com.caitu99.service.right.service.MyRightsService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MyRightsServiceImpl 
 * @author ws
 * @date 2016年5月11日 下午2:39:55 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class MyRightsServiceImpl implements MyRightsService {

	private final static Logger logger = LoggerFactory.getLogger(MyRightsServiceImpl.class);
	
	@Autowired
	MyRightsMapper myRightsMapper;
	@Autowired
	RightDetailMapper rightDetailMapper;
	@Autowired
	RightCodeMapper rightCodeMapper;
	@Autowired
	private PushMessageService pushMessageService;
	
	private final static String DATE_FORMAT = "yyyy-MM-dd";
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.right.service.MyRightsService#selectMyRights(java.lang.Long)
	 */
	@Override
	public List<MyRights> selectMyRights(Long userid) {
		List<MyRights> myRightsList = myRightsMapper.selectMyRights(userid);
		String dateNow = DateUtil.DateToString(new Date(),DATE_FORMAT);
		
		List<MyRights> disabledRights = new ArrayList<MyRights>(); 
		
		//处理权益失效
		for (MyRights myRights : myRightsList) {
			String dateDisabled = DateUtil.DateToString(myRights.getGmtDisabled(),DATE_FORMAT);
			if(dateNow.compareTo(dateDisabled) > 0 && myRights.getStatus() > 0){//过期未使用
				disabledRights.add(myRights);
				myRights.setStatus(-1L);//置为失效
				
				//push消息
				push(userid,false);
			}

			myRights.setCreateDateStr(DateUtil.DateToString(myRights.getGmtCreate(),DATE_FORMAT));
			myRights.setDisabledDateStr(DateUtil.DateToString(myRights.getGmtDisabled(),DATE_FORMAT));
			
		}
		
		for (MyRights dr : disabledRights) {
			myRightsMapper.updateByPrimaryKey(dr);
		}
		
		return myRightsList;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.right.service.MyRightsService#selectRightDetail(java.lang.Long)
	 */
	@Override
	public Map<String,String> selectRightDetail(Long id, Long userid) {
		Map<String,String> rightDetailVo = new HashMap<String, String>();
		MyRights myRights = myRightsMapper.selectByPrimaryKey(id);
		
		if(null == myRights || !myRights.getUserId().equals(userid)){
			return null;
		}
		
		RightDetail rightDetail  = rightDetailMapper.selectByPrimaryKey(myRights.getRightId());

		rightDetailVo.put("id", myRights.getId().toString());
		rightDetailVo.put("name", rightDetail.getName());
		rightDetailVo.put("detail", rightDetail.getDetail());
		rightDetailVo.put("createDateStr", DateUtil.DateToString(myRights.getGmtCreate(),DATE_FORMAT));
		rightDetailVo.put("disabledDateStr", DateUtil.DateToString(myRights.getGmtDisabled(),DATE_FORMAT));
		rightDetailVo.put("status", myRights.getStatus().toString());
		rightDetailVo.put("rule", rightDetail.getRule());
		rightDetailVo.put("imgUrl", rightDetail.getImgUrl());
		rightDetailVo.put("scopeUrl", rightDetail.getScopeUrl());
		
		if(myRights.getStatus() == 2){//已使用
			rightDetailVo.put("code", myRights.getCode());
		}
		
		return rightDetailVo;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.right.service.MyRightsService#useMyRights(java.lang.Long)
	 */
	@Override
	public MyRights useMyRights(Long id,Long userid) {

		MyRights myRights = myRightsMapper.selectByPrimaryKey(id);
		
		//判断权益是否存在或已失效
		if(null == myRights || myRights.getStatus() == -1
				|| !myRights.getUserId().equals(userid)){
			return null;
		}
		
		String oldDisabledDateStr = DateUtil.DateToString(myRights.getGmtDisabled(),DATE_FORMAT);
		myRights.setStatus(2L);//已使用
		
		Date nextDate = DateUtil.addDay(new Date(), 1);
		String newDisabledDateStr = DateUtil.DateToString(nextDate,DATE_FORMAT);
		
		if(newDisabledDateStr.compareTo(oldDisabledDateStr) < 0){//判断权益失效日期
			//新的失效日期 小于 原失效日期，则用新的
			myRights.setGmtDisabled(nextDate);
			myRights.setDisabledDateStr(newDisabledDateStr);
		}else{//使用原失效日期
			myRights.setDisabledDateStr(oldDisabledDateStr);
		}
		myRights.setCreateDateStr(DateUtil.DateToString(myRights.getGmtCreate(),DATE_FORMAT));
		
		myRightsMapper.updateByPrimaryKey(myRights);
		
		//push消息
		push(userid,true);
		
		return myRights;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.right.service.MyRightsService#addMyRights(java.lang.Long, java.lang.Long, java.lang.String, java.util.Date)
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void addMyRights(Long userid, Long rightId, String code, String coupon,
			Date disabledDate) {
		
		RightDetail rightDetail  = rightDetailMapper.selectByPrimaryKey(rightId);
		MyRights myRights = new MyRights();
		myRights.setCode(code);
		myRights.setDetail(rightDetail.getDetail());
		myRights.setGmtCreate(new Date());
		myRights.setGmtDisabled(disabledDate);
		myRights.setIdentity(rightDetail.getIdentity());
		myRights.setName(rightDetail.getName());
		myRights.setRightId(rightId);
		myRights.setStatus(1L);//领取
		myRights.setUserId(userid);
		myRights.setCoupon(coupon);
		myRightsMapper.insert(myRights);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.right.service.MyRightsService#getRightCode(java.lang.Long)
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public RightCode getRightCode(Long rightId) {
		
		RightCode rightCode = rightCodeMapper.getNoUsedCode(rightId);
		rightCode.setStatus(2L);//已使用
		rightCodeMapper.updateByPrimaryKey(rightCode);
		return rightCode;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public RightCode freezeRightCode(Long rightId) {
		RightCode rightCode = rightCodeMapper.getNoUsedCode(rightId);
		rightCode.setStatus(3L);//冻结
		rightCodeMapper.updateByPrimaryKey(rightCode);
		return rightCode;
	}

	
	/**
	 * push消息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: push 
	 * @param userId		用户ID
	 * @param isUse			是否是使用还是失效
	 * @date 2016年5月18日 下午5:28:55  
	 * @author xiongbin
	 */
	private void push(Long userId,boolean isUse){
		StringBuffer msg = new StringBuffer("新手任务,");
		if(isUse){
			msg.append("使用权益.");
		}else{
			msg.append("权益过期.");
		}
		
		try {
			String pushDescription = "";
			String title = "";
			if(isUse){
				pushDescription = Configuration.getProperty("push.activities.newbie.use.description", null);
				title = Configuration.getProperty("push.activities.newbie.use.title", null);
			}else{
				pushDescription = Configuration.getProperty("push.activities.newbie.past.description", null);
				title = Configuration.getProperty("push.activities.newbie.past.title", null);
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
	public RightCode selectByPrimaryKey(Long id) {
		return rightCodeMapper.selectByPrimaryKey(id);
	}
}
