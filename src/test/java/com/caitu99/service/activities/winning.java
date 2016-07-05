/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;











import com.alibaba.druid.support.json.JSONUtils;
import com.caitu99.service.AbstractJunit;
import com.caitu99.service.activities.dao.ActivitiesItemMapper;
import com.caitu99.service.activities.dao.ActivitiesMapper;
import com.caitu99.service.activities.domain.Activities;
import com.caitu99.service.activities.domain.ActivitiesItem;
import com.caitu99.service.activities.dto.ActivitiesItemDto;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.goods.domain.Stock;
import com.caitu99.service.sys.domain.Banner;
import com.caitu99.service.sys.service.BannerService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: winning 
 * @author fangjunxiao
 * @date 2015年12月1日 下午5:33:03 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class winning extends AbstractJunit{

	@Autowired
	private ActivitiesMapper activitiesDao;
	
	@Autowired
	private ActivitiesItemMapper activitiesItemDao;
	
	@Autowired
	private BannerService bannerService;
	
	
	
	private void zjsf(List<ActivitiesItem> aiList){
		Random random = new Random();
		int abc = random.nextInt(1000)+1;
		Integer next = 0;
		Integer last = 0;
		Integer falg = 0;
		for (ActivitiesItem aid : aiList) {
			last = next;
			next += aid.getProbability();
			if(abc>last && abc<=next){
				System.out.println(aid.getName() +"===" +aid.getProbability() +"%==="+last+"-"+ next +"==="+abc);
				falg = 1;
			}
		}
		if(0==falg){
			System.out.println("很可惜，没有中奖！！！");
		}
	}
	
	@Test
	public void newWinning(){
		List<ActivitiesItem> aiList = activitiesItemDao.findAllByActivitiesId(1L);
		for (int i = 0; i < 10; i++) {
			zjsf(aiList);
		}
		
		
	}
	

	public void gettt(){
		/*Long activitiesId = 1L;
		List<ActivitiesItemDto> aiList = activitiesItemDao.findAllByActivitiesId(activitiesId);
		
	
		
		Random random = new Random();
		
		
		for (int i = 0; i < 100; i++) {
			int abc = random.nextInt(100)+1;
			Integer next = 0;
			Integer last = 0;
			for (ActivitiesItemDto aid : aiList) {
				last = next;
				next += aid.getProbability();
				if(abc>last && abc<=next){
					System.out.println(aid.getName() +"===" +aid.getProbability() +"%==="+last+"-"+ next +"==="+abc);
				}
			}
		}*/
			

		
	}
	
	public static void main(String[] args) {
		
		
/*		List<ActivitiesItem> list = new ArrayList<ActivitiesItem>();
		
		ActivitiesItem ai = new ActivitiesItem();
		ai.setProbability(40);
		ai.setName("5财币");
		ai.setQuantity(10000000);
		*/
		
/*		
		1		100
.		3		97-99
		6		91-96
		6		85-90
.		8		77-84
		11		66-76
.		25		41-65
.		40		1-40
		
*/

/*		Random random = new Random();
		for (int i = 0; i < 200; i++) {
			int abc = random.nextInt(101);
			System.out.println(abc);
		}
		*/
		
		List<Integer> stockList = null;
		
		
		stockList.isEmpty();
		
	}
}
