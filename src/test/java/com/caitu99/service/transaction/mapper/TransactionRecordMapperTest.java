/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.mapper;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.TransactionRecord;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: TransactionRecordMapperTest 
 * @author ws
 * @date 2015年12月2日 上午11:26:25 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class TransactionRecordMapperTest extends AbstractJunit{

	@Autowired
	TransactionRecordMapper mapper;
	
	@Test
	public void testcountTotalFreezeByUserId() {
		try{
			Long userId = 1L;
			Long result = mapper.countTotalFreezeByUserId(userId );
			System.out.println(result);
		}catch(Exception ex){
			fail();
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testselectByOrderNoExludeUserId() {
		try{
			Map<String,Object> queryMap = new HashMap<String, Object>();
			queryMap.put("userId", 1L);
			queryMap.put("orderNo", "dddhhhhh234");
			TransactionRecord result = mapper.selectByOrderNoExludeUserId(queryMap);
			System.out.println(result);
		}catch(Exception ex){
			fail();
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testselectByUserIdAndOrderNo() {
		try{
			Map<String,Object> queryMap = new HashMap<String, Object>();
			queryMap.put("userId", 1L);
			queryMap.put("orderNo", "dddhhhhh234");
			TransactionRecord result = mapper.selectByUserIdAndOrderNo(queryMap);
			System.out.println(result);
		}catch(Exception ex){
			fail();
			ex.printStackTrace();
		}
	}
	

	@Test
	public void testselectByUserId() {
		try{
			Map<String,Object> queryMap = new HashMap<String, Object>();
			queryMap.put("userId", 1L);
			queryMap.put("orderNo", "dddhhhhh234");
			List<TransactionRecord> result = mapper.selectByUserIdFreeze(1L);
			System.out.println(result.size());
		}catch(Exception ex){
			fail();
			ex.printStackTrace();
		}
	}
	
}
