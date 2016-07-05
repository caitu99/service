/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.druid.filter.AutoLoad;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: KeyConfig 
 * @author ws
 * @date 2015年12月19日 下午5:00:25 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class KeyConfig {
	
	@Value("${showapi.app.id}")
	public String showapiAppId;
	@Value("${showapi.sign}")
	public String showapiSign;
	
	@Value("${juheapi.key}")
	public String juheapiKey;

}
