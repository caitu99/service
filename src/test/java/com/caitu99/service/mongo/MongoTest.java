/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mongo;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AbstractJunit;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.mongodb.dao.MongoBaseDao;
import com.caitu99.service.push.model.PushMessage;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.utils.date.DateStyle;
import com.caitu99.service.utils.date.DateUtil;
import com.mongodb.WriteResult;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MongoTest 
 * @author Hongbo Peng
 * @date 2015年12月22日 上午11:07:36 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class MongoTest extends AbstractJunit{

	@Autowired
	private MongoBaseDao mongoBaseDao;
	
	@SuppressWarnings("rawtypes")
//	@Test
	public void test(){
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("message", "这个还是消息");
//		map.put("red_id", "101");
//		map.put("user_id", "3");
//		map.put("id","123");
//		mongoBaseDao.add(map);
//		List<HashMap> list = mongoBaseDao.findAll(HashMap.class);
//		for (HashMap hashMap : list) {
//			System.out.println(hashMap);
//		}
//		HashMap hashMap = mongoBaseDao.findById(HashMap.class, "ObjectId(\"5678c4e5d6ec9a5d8a5bc3d4\")");
//		System.out.println(hashMap);
		
//		List<HashMap> list = mongoBaseDao.find(new Query(Criteria.where("user_id").is("3").and("id").is("123")), HashMap.class);
//		for (HashMap hashMap : list) {
//			System.out.println(hashMap);
//		}
	}
	
	Date date = new Date();
	@Test
	public void test2(){
		PushMessage push = new PushMessage();
		push.setRedId(101);
		push.setType(1);
		push.setChannel(1);
		push.setUserId(1L);
		push.setTitle("");
		push.setDescription("你有一条新的短消息111");
		push.setPayload("");
		push.setStatus(1);
		push.setSendTime(date);
		push.setCreateTime(date);
		push.setUpdateTime(date);
		mongoBaseDao.add(push);
		
//		push.setRedId(101);
//		push.setType(1);
//		push.setChannel(1);
//		push.setUserId(2L);
//		push.setTitle("");
//		push.setDescription("你有一条新的短消息");
//		push.setPayload("");
//		push.setStatus(1);
//		push.setCrateTime(date);
//		push.setUpdateTime(date);
//		mongoBaseDao.add(push);
		
//		PushMessage queryPush = new PushMessage();
//		queryPush.setUserId(1L);
//		
//		PushMessage updatePush = new PushMessage();
//		updatePush.setStatus(2);
//		updatePush.setReadTime(date);
		
		try {
//			WriteResult result = mongoBaseDao.updateMulti(queryPush, updatePush, PushMessage.class);
//			System.out.println(result);
			
//			List<PushMessage> pm = mongoBaseDao.findAll( PushMessage.class);
//			System.out.println(JSON.toJSONString(pm));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	@Test
	public void test3(){
		try {
			PushMessage queryPush = new PushMessage();
			queryPush.setId("5678ea1fd6ecc0cc5086506b");
			PushMessage updatePush = new PushMessage();
			updatePush.setDescription("修改了消息");
			updatePush.setStatus(2);
			updatePush.setReadTime(date);
			WriteResult result = mongoBaseDao.updateMulti(queryPush, updatePush, PushMessage.class);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Autowired
	private PushMessageService pushMessageService;
	
//	@Test
	public void test4(){
		Pagination<PushMessage> pagination = new Pagination<PushMessage>();
		pagination.setStart(0);
		pagination.setPageSize(5);
		PushMessage pushMessage = new PushMessage();
		pushMessage.setUserId(1L);
		try {
			Pagination<PushMessage>  p = pushMessageService.findPage(pagination, pushMessage);
			System.out.println("总页数"+p.getTotalPage());
			System.out.println("总条数数"+p.getTotalRow());
			System.out.println("当前页"+p.getCurPage());
			for (PushMessage pm : p.getDatas()) {
				System.out.println(JSON.toJSONString(pm));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test5(){
		PushMessage pushMessage = new PushMessage();
		pushMessage.setType(1);
		pushMessage.setRedId(101);
		pushMessage.setUserId(1L);
		pushMessage.setTitle("标题");
		pushMessage.setDescription("描述");
		pushMessage.setPayload("内容");
		pushMessage.setSmsInfo("短信");
		pushMessage.setStatus(0);
		pushMessage.setCreateTime(date);
		pushMessage.setUpdateTime(date);
		mongoBaseDao.add(pushMessage);
		System.out.println(pushMessage.getId());
	}
}
