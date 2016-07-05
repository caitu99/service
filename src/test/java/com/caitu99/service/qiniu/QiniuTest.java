/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.qiniu;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.file.service.AttachFileService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: QiniuTest 
 * @author Hongbo Peng
 * @date 2015年11月26日 下午12:08:18 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class QiniuTest extends AbstractJunit {

	@Autowired
	private AttachFileService attachFileService;
	
	@Test
	public void test(){
		String backUpPath = attachFileService.backUpImage("GOOD/1448446019179/1100101.JPG");
		Assert.assertTrue(StringUtils.isNotEmpty(backUpPath));
	}
}
