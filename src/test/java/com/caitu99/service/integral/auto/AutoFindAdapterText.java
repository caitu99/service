package com.caitu99.service.integral.auto;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.integral.controller.auto.AutoFindAdapter;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindAdapterText 
 * @author xiongbin
 * @date 2015年12月18日 下午5:05:33 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class AutoFindAdapterText extends AbstractJunit {

	@Autowired
	private AutoFindAdapter autoFindAdapter;
	
	@Test
	public void text(){
		autoFindAdapter.execute(1L, 336L, "15157146726", "382453625");
	}
}
