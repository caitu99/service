/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.push.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.mongodb.dao.MongoDao;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.PushMessage;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.sys.domain.Notice;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.sys.service.NoticeService;
import com.caitu99.service.sys.sms.SMSSend;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.dao.UserPushInfoMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserPushInfo;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PushMessageServiceImpl 
 * @author Hongbo Peng
 * @date 2015年12月22日 下午4:23:15 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class PushMessageServiceImpl implements PushMessageService {

	private Logger logger = LoggerFactory.getLogger(PushMessageServiceImpl.class);
	
	@Autowired
	private UserPushInfoMapper userPushInfoMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private MongoDao mongoBaseDao;
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private ConfigService configService;
	
	@Override
	public void pushMessage(RedSpot redSpot, Long userId, Message message) throws Exception{
		if(null == redSpot || null == userId || null == message){
			logger.error("推送参数不完整,redSpot:{},userId:{},message:{}",redSpot,userId,message);
			return;
		}
		if(!message.validate()){
			logger.error("发送消息参数有误,message:{}",JSON.toJSONString(message));
			return;
		}
		try{
			Date date = new Date();
			PushMessage pushMessage = new PushMessage();
			//1.查询该用户是否有regId
			UserPushInfo userPushInfo = userPushInfoMapper.selectByUserId(userId);
			User user = userMapper.selectByPrimaryKey(userId);
			//2.发送短信或者消息推送
			pushMessage.setIsPush(false);
			pushMessage.setIsSMS(false);
			if(message.getIsPush() && null != userPushInfo && StringUtils.isNotBlank(userPushInfo.getRegId())){
				//推送消息
				logger.info("本次给用户userId:{}推送消息",userId);
				pushMessage.setChannel(1);
				pushMessage.setIsPush(true);
				pushMessage.setIsSMS(message.getIsSMS());
			}else if(message.getIsSMS() && null != user && StringUtils.isNotBlank(user.getMobile())){
				//发送短信
				logger.info("本次给用户userId:{}发送短信",userId);
				pushMessage.setChannel(2);
				pushMessage.setIsSMS(true);
				//调用短信接口
			}else{
				logger.info("用户userId:{}既没有下载APP，也没有手机号，不发送消息",userId);
				return ;
			}
			//3.小黄条处理
			if(message.getIsYellow()){
				//插入小黄条数据，展示12小时
				Notice notice = new Notice();
				notice.setContent(message.getYellowInfo());
				notice.setUserid(userId);
				notice.setStatus(1);
				notice.setStartTime(date);
				notice.setEndTime(DateUtil.addHour(date, 12));
				noticeService.insert(notice);
			}
			//4.插入消息到mongodb
			pushMessage.setType(1);
			pushMessage.setRedId(redSpot.getValue());
			pushMessage.setUserId(userId);
			pushMessage.setTitle(message.getTitle());
			pushMessage.setDescription(message.getPushInfo());
			pushMessage.setPayload(message.getPushInfo());
			pushMessage.setSmsInfo(message.getSmsInfo());
			pushMessage.setStatus(0);
			pushMessage.setCreateTime(date);
			pushMessage.setUpdateTime(date);
			mongoBaseDao.add(pushMessage);
			//5.推送到JOB，决定什么时候发送
			Map<String,String> map = new HashMap<String, String>();
			map.put("messageId", pushMessage.getId());
			map.put("jobType", "UNIFY_PUSH_MESSAGE_JOB");//统一消息推送
			kafkaProducer.sendMessage(JSON.toJSONString(map),appConfig.jobTopic);
		}catch(Exception e){
			logger.error("统一发送消息发生异常：{}",e);
			throw e;
		}
	}
	
	@Override
	public void saveMessage(RedSpot redSpot, Long userId, Message message)
			throws Exception {
		if(null == redSpot || null == userId || null == message){
			logger.error("推送参数不完整,redSpot:{},userId:{},message:{}",redSpot,userId,message);
			return;
		}
		try{
			//插入消息到mongodb
			Date date = new Date();
			PushMessage pushMessage = new PushMessage();
			pushMessage.setIsPush(false);
			pushMessage.setIsSMS(false);
			pushMessage.setChannel(1);
			pushMessage.setType(1);
			pushMessage.setRedId(redSpot.getValue());
			pushMessage.setUserId(userId);
			pushMessage.setTitle(message.getTitle());
			pushMessage.setDescription(message.getPushInfo());
			pushMessage.setPayload(message.getPushInfo());
			pushMessage.setSmsInfo(message.getSmsInfo());
			pushMessage.setStatus(1);
			pushMessage.setSendTime(date);
			pushMessage.setCreateTime(date);
			pushMessage.setUpdateTime(date);
			mongoBaseDao.add(pushMessage);
		}catch(Exception e){
			logger.error("统一发送消息发生异常：{}",e);
			throw e;
		}
	}



	@Override
	public void pushMessage(String id) throws Exception {
		PushMessage queryObject = new PushMessage();
		queryObject.setId(id);
		List<PushMessage> list = mongoBaseDao.find(queryObject, PushMessage.class);
		if(null == list){
			logger.info("没有messageId={}的消息记录",id);
			return;
		}
		PushMessage pushMessage = list.get(0);
		if(pushMessage.getIsPush()){
			mipush(pushMessage);
		}else if(pushMessage.getIsSMS()){
			smspush(pushMessage);
		}else{
			logger.info("该消息暂时没有发送方式:{}",JSON.toJSONString(pushMessage));
		}
	}
	
	@Override
	public void smsPush(String id) throws Exception {
		PushMessage queryObject = new PushMessage();
		queryObject.setId(id);
		List<PushMessage> list = mongoBaseDao.find(queryObject, PushMessage.class);
		if(null == list){
			logger.info("没有messageId={}的消息记录",id);
			return;
		}
		PushMessage pushMessage = list.get(0);
		if(pushMessage.getIsSMS()){
			smspush(pushMessage);
		}else{
			logger.info("该消息不需要短信发送:{}",JSON.toJSONString(pushMessage));
		}
	}

	/**
	 * @Description: (使用小米推送)  
	 * @Title: mipush 
	 * @param pushMessage
	 * @throws Exception
	 * @date 2015年12月23日 下午4:37:58  
	 * @author Hongbo Peng
	 */
	private void mipush(PushMessage pushMessage) throws Exception{
		if(!pushMessage.getIsPush()){
			return;
		}

		//修改消息记录的时间
		Date date = new Date();
		PushMessage queryObject = new PushMessage();
		queryObject.setId(pushMessage.getId());
		PushMessage updateObject = new PushMessage();
		updateObject.setSendTime(date);
		updateObject.setUpdateTime(date);
		updateObject.setStatus(1);
		mongoBaseDao.updateMulti(queryObject, updateObject, PushMessage.class);
		
		UserPushInfo userPushInfo = userPushInfoMapper.selectByUserId(pushMessage.getUserId());
		//获取 未读消息总条数 和 小红点编号集合 一并推送
		PushMessage query = new PushMessage();
		query.setUserId(pushMessage.getUserId());
		query.setStatus(1);
		Long count = mongoBaseDao.count(PushMessage.class, query);
		
		//推送
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("pushType", "PUSH_MESSAGE");//推送类型：单推
		jsonObject.put("title", Configuration.getProperty("push.title","财途积分生活"));
		jsonObject.put("description", pushMessage.getDescription());
		jsonObject.put("payload", pushMessage.getPayload());
		jsonObject.put("regId", userPushInfo.getRegId());
		jsonObject.put("type", userPushInfo.getType());
		jsonObject.put("messageCount", count);
		jsonObject.put("redSpots", pushMessage.getRedId());
		jsonObject.put("messageId", pushMessage.getId());
		kafkaProducer.sendMessage(jsonObject.toJSONString(), "app_message_push_topic");
	}
	/**
	 * @Description: (使用短信推送)  
	 * @Title: smspush 
	 * @param pushMessage
	 * @throws Exception
	 * @date 2015年12月23日 下午4:38:23  
	 * @author Hongbo Peng
	 */
	private void smspush(PushMessage pushMessage) throws Exception{
		if(!pushMessage.getIsSMS()){
			return;
		}
		User user = userMapper.selectByPrimaryKey(pushMessage.getUserId());
		String smsConfig = configService.selectByKey("sms_send_channel").getValue();
		SMSSend.sendSMS(new String[]{user.getMobile()}, pushMessage.getSmsInfo(),smsConfig);
		
		//修改消息记录的时间
		Date date = new Date();
		PushMessage queryObject = new PushMessage();
		queryObject.setId(pushMessage.getId());
		PushMessage updateObject = new PushMessage();
		updateObject.setSendTime(date);
		updateObject.setUpdateTime(date);
		updateObject.setStatus(1);
		mongoBaseDao.updateMulti(queryObject, updateObject, PushMessage.class);
	}

	@Override
	public void editMessage(String[] ids,Integer status) throws Exception {
		for (int i = 0; i < ids.length; i++) {
			Date date = new Date();
			PushMessage queryObject = new PushMessage();
			queryObject.setId(ids[i]);
			PushMessage updateObject = new PushMessage();
			updateObject.setStatus(status);
			updateObject.setReadTime(date);
			updateObject.setUpdateTime(date);
			mongoBaseDao.updateMulti(queryObject, updateObject, PushMessage.class);
		}
	}

	@Override
	public Pagination<PushMessage> findPage(Pagination<PushMessage> pagination,
			PushMessage pushMessage) throws Exception{
		int start = pagination.getStart();
		int pageSize = pagination.getPageSize();
		
		PushMessage queryObject = new PushMessage();
		queryObject.setUserId(pushMessage.getUserId());
		
		Criteria criteria = new Criteria();
		criteria.and("userId").is(pushMessage.getUserId())
			.and("status").in(1,2);
		
		Query query = new Query(criteria);
		Long count = mongoBaseDao.count(PushMessage.class, query);

		query.skip(start);
		query.limit(pageSize);
		Sort sort = new Sort(new Order(Direction.DESC,"createTime"));
		query.with(sort);
		
		query.fields().include("description").include("title").include("id").include("sendTime").include("status");
		List<PushMessage> list = mongoBaseDao.find(query, PushMessage.class);
		pagination.setTotalRow(count.intValue());
		pagination.setDatas(list);
		
		//修改用户未读消息为已读
		Date date = new Date();
		queryObject.setStatus(1);
		PushMessage updateObject = new PushMessage();
		updateObject.setStatus(2);
		updateObject.setReadTime(date);
		updateObject.setUpdateTime(date);
		mongoBaseDao.updateMulti(queryObject, updateObject, PushMessage.class);
		return pagination;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.push.service.PushMessageService#getRedSpot(java.lang.Long)
	 */
	@Override
	public List<PushMessage> getRedSpot(Long userId) throws Exception {
		Criteria criteria = new Criteria();
		criteria.and("userId").is(userId)
			.and("status").in(1,2);
		Query query = new Query(criteria);
		query.fields().include("redId").include("_id");
		List<PushMessage> list = mongoBaseDao.find(query, PushMessage.class);
		return list;
	}

	
	@Override
	public void sendBroadcast(String title,String message) throws Exception {
		if(StringUtils.isBlank(title) || StringUtils.isBlank(message)){
			logger.error("推送参数不完整,title:{},message:{}",title,message);
			return;
		}
		try{
			Date date = new Date();
			List<UserPushInfo> list = userPushInfoMapper.selectAll();
			List<PushMessage> pushs = new ArrayList<PushMessage>();
			for (UserPushInfo userPushInfo : list) {
				//构建消息
				PushMessage pushMessage = new PushMessage();
				pushMessage.setIsPush(true);
				pushMessage.setIsSMS(false);
				pushMessage.setChannel(1);
				pushMessage.setType(2);
				pushMessage.setRedId(RedSpot.MESSAGE_CENTER.getValue());
				pushMessage.setUserId(userPushInfo.getUserId());
				pushMessage.setTitle(title);
				pushMessage.setDescription(message);
				pushMessage.setPayload(message);
				pushMessage.setSmsInfo(null);
				pushMessage.setStatus(1);
				pushMessage.setSendTime(date);
				pushMessage.setCreateTime(date);
				pushMessage.setUpdateTime(date);
			}
			//批量插入
			mongoBaseDao.addAll(pushs);
			//推送
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("pushType", "PUSH_TOPIC");//推送类型：单推
			jsonObject.put("title", Configuration.getProperty("push.title","财途积分生活"));
			jsonObject.put("description", message);
			jsonObject.put("payload", message);
			jsonObject.put("messageCount", 1);
			jsonObject.put("redSpots", RedSpot.MESSAGE_CENTER.getValue());
			jsonObject.put("messageId", "");
			kafkaProducer.sendMessage(jsonObject.toJSONString(), "app_message_push_topic");
		}catch(Exception e){
			logger.error("统一发送消息发生异常：{}",e);
			throw e;
		}
	}

}
