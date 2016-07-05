/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.weixin;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.utils.weixin.WeixinUtil;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: WeixinUtilTest 
 * @author xiongbin
 * @date 2015年12月7日 上午11:48:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class WeixinUtilTest extends AbstractJunit {

	@Autowired
	private WeixinUtil weinxinUtils;
	
	@Test
	public void test(){
		String accessToken = weinxinUtils.getAccessToken();
		weinxinUtils.setMenu(accessToken);
	}
}
