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
import com.caitu99.service.sys.domain.Banner;
import com.caitu99.service.sys.service.BannerService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BannerServiceImpl 
 * @author fangjunxiao
 * @date 2015年12月3日 上午10:39:55 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class BannerServiceImpl implements BannerService{

	private final static Logger logger = LoggerFactory
			.getLogger(GroupService.class);
	
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private BannerMapper bannerDao;
	
	@Override
	public  List<Banner> getRotaryImg(Integer type) {
		List<Banner> bl = this.getRedisBanner(type);
		 for (Banner banner : bl) {
			 banner.setImgPath(appConfig.staticUrl + banner.getImgPath());
			 if(null != banner.getUrl() && !"".equals(banner.getUrl())){
				 //含有APP:的属于app内部跳转，不需要加http
				 if(!banner.getUrl().contains("http") && !banner.getUrl().contains("APP:")){
					 banner.setUrl(appConfig.caituUrl + banner.getUrl());
				 }
			 }
		}
		 return bl;
	}
	
	private List<Banner> getRedisBanner(Integer type){
		String key = String.format(RedisKey.BANNER_CODE, type);
		String content = redis.getStringByKey(key);
		if(StringUtils.isNotBlank(content)){
			return JSON.parseArray(content, Banner.class);
		}
		Banner querybanner = new Banner();
		querybanner.setType(type);
		List<Banner> bl = bannerDao.findRotaryImg(querybanner);
		if(null != bl){
			redis.set(key,JSON.toJSONString(bl));
			//轮播图缓存7天
			redis.setExpire(key, 1000*60*60*24*7);
		}
		return bl;
	}

	@Override
	public String findPageBanner(Banner banner, Pagination<Banner> pagination) {
		try {
			ApiResult<Pagination<Banner>> result = new ApiResult<Pagination<Banner>>();
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("banner", banner);
			map.put("start", pagination.getStart());
			map.put("pageSize", pagination.getPageSize());
			
			Integer count = bannerDao.selectPageCount(map);
			List<Banner> gLis = bannerDao.selectPageList(map);
			for (Banner b : gLis) {
				 b.setImgPath(appConfig.staticUrl + b.getImgPath());
				 if(null != b.getUrl() && !"".equals(b.getUrl())){
					 if(!b.getUrl().contains("http")){
						 b.setUrl(appConfig.caituUrl + b.getUrl());
					  }
				  }
			}
			
			pagination.setTotalRow(count);
			pagination.setDatas(gLis);
			
			return result.toJSONString(0,"success",pagination);
		} catch (Exception e) {
			logger.error("图片分页查询失败:" + e.getMessage());
			throw new ActivitiesException(-1, "图片分页查询失败");
		}
	}

}
