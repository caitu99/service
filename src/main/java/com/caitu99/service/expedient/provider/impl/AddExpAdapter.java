/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.provider.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.exception.ExpedientException;
import com.caitu99.service.expedient.dao.ExpRecordMapper;
import com.caitu99.service.expedient.dao.ExpedientMapper;
import com.caitu99.service.expedient.dao.VipMapper;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.domain.ExpRecord;
import com.caitu99.service.expedient.domain.Expedient;
import com.caitu99.service.expedient.domain.Vip;
import com.caitu99.service.expedient.provider.AddExp;
import com.caitu99.service.expedient.provider.abs.AddExpAbstract;
import com.caitu99.service.expedient.service.ExpedientService;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.sys.domain.Config;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.date.DateUtil;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddExpAdapter 
 * @author fangjunxiao
 * @date 2016年5月26日 下午6:19:16 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class AddExpAdapter implements AddExp{
	
	private final static Logger log = LoggerFactory
			.getLogger(AddExp.class);
	
	@Autowired
	private ExpedientMapper expedientDao;
	
	@Autowired
	private ExpRecordMapper expRecordDao;
	
	@Autowired
	private VipMapper vipDao;
	
	@Autowired
	private UserMapper userDao;
	
	@Autowired
	private PushMessageService pushMessageService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	protected ExpedientService expedientService;

	@Override
	public void addExp(ExpData data, AddExpAbstract addExp) throws ExpedientException{
		try {
			Long exp = addExp.addExp(data);
			
			//升级
			this._levUp(data.getUserId(),exp,addExp.getSource());
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("user up lev fail: userId = {},message:{}",data.getUserId(),e.getMessage());
		}
	}
	
	
	
	private void _levUp(Long userId,Long exp,Integer source){
		
		if(null == exp || 0L == exp){
			return;
		}
		
		User user = userDao.selectByPrimaryKey(userId);
		
		if(null == user){
			log.debug("Expedient  addExp: userId is null or user is null");
			return;
		}
		
		//查询用户等级
		Expedient expedient = expedientDao.getExpedientByuserId(user.getId());
		
		//找出当天已获得经验
		Long myExp = this._getToDayExp(user.getId());
		if(null == myExp)
			myExp = 0L;
		
		if(null == expedient){
			this._insetExp(myExp, exp, user,source);
		}else{
			this._updateExp(myExp, exp, expedient,source);
		}
	}

	
	private Long _getToDayExp(Long userId){
		Date endTime = Calendar.getInstance().getTime();
		Date startTime = DateUtil.getZeroPoint(endTime);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("endTime", endTime);
		map.put("startTime", startTime);
		map.put("userId", userId);
		
		return expRecordDao.findAllExpByToDay(map);
	}
	
	
	private void _insetExp(Long myExp,Long exp,User user,Integer source){
		Date date = new Date();
		//初始等级
		Vip vip = vipDao.getVipByLev(0);
		
		//等级经验限制
		if(exp > vip.getTop()){
			exp = vip.getTop();
		}
		
		String info = ExpRecord.getInfo(source);
		expedientService.addExpRecord(user.getId(), exp, source, info);
		this._pushMessage(info, exp, user.getId());
		
		//经验升级
		Vip upvip = vipDao.upLevByExp(exp);
		
		Expedient newExpt = new Expedient();
		newExpt.setExp(exp);
		newExpt.setLev(upvip.getLev());
		newExpt.setMobile(user.getMobile());
		newExpt.setName(upvip.getName());
		newExpt.setStatus(1);
		newExpt.setUserId(user.getId());
		newExpt.setCreateTime(date);
		newExpt.setUpdateTime(date);
		expedientDao.insertSelective(newExpt);
	}
	
	
	private void _updateExp(Long myExp,Long exp,Expedient expedient,Integer source){
		Date date = new Date();
		//当前等级
		Vip vip = vipDao.getVipByLev(expedient.getLev());
		
		//等级经验限制
		if(myExp > vip.getTop()){
			Long superpass = CalculateUtils.getDifference(myExp, exp);
			if(superpass >= vip.getTop()){
				return;
			}
			exp = CalculateUtils.getDifference(vip.getTop(), superpass);
		}
		
		String info = ExpRecord.getInfo(source);
		expedientService.addExpRecord(expedient.getUserId(), exp, source, info);
		this._pushMessage(info, exp, expedient.getUserId());
		
		//经验累加 
		exp = CalculateUtils.add(exp,expedient.getExp());
		
		//经验升级
		Vip upvip = vipDao.upLevByExp(exp);
		
		Expedient updateExpt = new Expedient();
		updateExpt.setId(expedient.getId());
		updateExpt.setExp(exp);
		updateExpt.setLev(upvip.getLev());
		updateExpt.setName(upvip.getName());
		updateExpt.setUpdateTime(date);
		expedientDao.updateByPrimaryKeySelective(updateExpt);
		
	}
	
	
	
	private void _pushMessage(String info,Long exp,Long userid){
		
		Config config = configService.selectByKey("push_switch");
		
		if(null == config){
			return;
		}
		
		if(config.getValue().equals("0")){
			return;
		}
		
		try {
			//发送消息到消息中心
			String title = Configuration.getProperty("push.expedient.title", null);
			String content = Configuration.getProperty("push.expedient.content", null);
			Message message = new Message();
			message.setTitle(String.format(title, exp));
			message.setPushInfo(String.format(content, info,exp));
			pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, userid, message);
		} catch (Exception e) {
			log.info("获得经验发送消息到消息中心失败",e);
		}
		
	}
	
}
