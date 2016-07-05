/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.activities.service.ActivitiesService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesServiceTest 
 * @author ws
 * @date 2015年12月4日 下午2:05:30 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ActivitiesServiceTest extends AbstractJunit{

	@Autowired
	ActivitiesService activitiesService;
	
	/**
	 * Test method for {@link com.caitu99.service.activities.service.impl.ActivitiesServiceImpl#getProof(com.caitu99.service.activities.controller.vo.ActivitiesVo)}.
	 */
	@Test
	public void testGetProof() {

		Long userId = 481L;
		Long activitiesId = 1L;
		Integer source = 1;
		String result = activitiesService.getProof(userId,activitiesId,source );
		System.out.println("===========" + result);
	}

	/**
	 * Test method for {@link com.caitu99.service.activities.service.impl.ActivitiesServiceImpl#getUnautherizedProof(com.caitu99.service.activities.controller.vo.ActivitiesVo)}.
	 */
	@Test
	public void testGetUnautherizedProof() {

		Long activitiesId = 1L;
		Integer source = 4;
		activitiesService.getUnautherizedProof(activitiesId, source);
		
	}

	/**
	 * Test method for {@link com.caitu99.service.activities.service.impl.ActivitiesServiceImpl#saveWinning(com.caitu99.service.activities.controller.vo.ActivitiesVo, java.lang.Long)}.
	 */
	@Test
	public void testSaveWinning() {

		Long itemId = 9L;
		
		Long userId = 481L;
		Long activitiesId = 1L;
		Integer source = 4;
		activitiesService.saveWinning(userId,activitiesId,source,itemId );
		
	}


	/**
	 * Test method for {@link com.caitu99.service.activities.service.impl.ActivitiesServiceImpl#checkUserTimes(java.lang.Long, java.lang.Long)}.
	 */
	@Test
	public void testCheckUserTimes() {


		Long userId = 481L;
		Long activitiesId = 1L;
		String result = activitiesService.checkUserTimes(userId, activitiesId);
		System.out.println(result);
	}

	/**
	 * Test method for {@link com.caitu99.service.activities.service.impl.ActivitiesServiceImpl#awardItm(java.lang.Long, java.lang.Long)}.
	 */
	@Test
	public void testAwardItm() {

		Long inRecordId = 33L;
		Long userId = 481L;
		activitiesService.awardItm(inRecordId, userId);
		
	}

}
