/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.sys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ActivitiesException;
import com.caitu99.service.goods.service.GroupService;
import com.caitu99.service.sys.dao.BannerMapper;
import com.caitu99.service.sys.dao.FuncModelMapper;
import com.caitu99.service.sys.domain.Banner;
import com.caitu99.service.sys.domain.FuncModel;
import com.caitu99.service.sys.service.BannerService;
import com.caitu99.service.sys.service.FuncModelService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BannerServiceImpl 
 * @author fangjunxiao
 * @date 2015年12月3日 上午10:39:55 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class FuncModelServiceImpl implements FuncModelService{

	private final static Logger logger = LoggerFactory
			.getLogger(FuncModelService.class);
	
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private FuncModelMapper funcModelDao;

	/* (non-Javadoc)
	 * @see com.caitu99.service.sys.service.FuncModelService#selectByModel(java.lang.Integer)
	 */
	@Override
	public List<FuncModel> selectByModel(Integer modelId) {
		List<FuncModel> fms = funcModelDao.selectByModel(modelId);
		for (FuncModel funcModel : fms) {
			if(StringUtils.isNotBlank(funcModel.getUrl()) && !funcModel.getUrl().startsWith("http")){
				funcModel.setUrl(appConfig.caituUrl + funcModel.getUrl());
			}
		}
		return fms;
	}
	
	
	

}
