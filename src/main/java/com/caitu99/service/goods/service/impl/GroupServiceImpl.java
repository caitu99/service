/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.goods.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ItemException;
import com.caitu99.service.goods.dao.GroupMapper;
import com.caitu99.service.goods.dao.TemplateMapper;
import com.caitu99.service.goods.domain.Group;
import com.caitu99.service.goods.dto.TemplateDto;
import com.caitu99.service.goods.service.GroupService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: GroupServiceImpl 
 * @author fangjunxiao
 * @date 2015年12月31日 上午9:51:50 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class GroupServiceImpl implements GroupService{
	
	private final static Logger logger = LoggerFactory
			.getLogger(GroupService.class);
	
    @Autowired
    private GroupMapper groupMapper;
    
    @Autowired
    private TemplateMapper templateDao;
	
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private AppConfig appConfig;
	
	private static final String[] GROUP_DETAIL_FILLTER = {"imgPath","name","title","url","sort","remoteId","remoteName","remoteType","discount","reprice"};

	private static final String[] TEMPLATE_FILE_FILLTER = {"name","goupList","type","url","sort"};
	
	
	

	@Override
	public String findPageGroup(TemplateDto temp,
			Pagination<TemplateDto> pagination) throws ItemException {
		try {
			ApiResult<Pagination<TemplateDto>> result = new ApiResult<Pagination<TemplateDto>>();
			
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("temp", temp); 
			map.put("start", pagination.getStart());
			map.put("pageSize", pagination.getPageSize());
			Integer count = templateDao.selectPageCount(map);
			List<TemplateDto> tLis = templateDao.selectPageList(map);
			
			if(null != tLis && 0 != tLis.size()){
				for (int i = 0; i < tLis.size(); i++) {
					List<Group> goupList = groupMapper.findGroupByTemplateId(tLis.get(i).getId());
					for (Group group : goupList) {
						if(null != group.getImgPath() && !"".equals(group.getImgPath())){
							if(Group.TYPE_ACTIVITY.equals(group.getRemoteType())){
								group.setImgPath(appConfig.staticUrl + group.getImgPath());
							}else{
								group.setImgPath(appConfig.fileUrl + group.getImgPath());
							}
						 }
						 if(null != group.getUrl() && !"".equals(group.getUrl())){
							 if(!group.getUrl().contains("http")){
								 group.setUrl(appConfig.caituUrl + group.getUrl());
							 }
						 }
					}
					tLis.get(i).setGoupList(goupList);
					
					if(null != tLis.get(i).getUrl() && !"".equals(tLis.get(i).getUrl())){
					   if(!tLis.get(i).getUrl().contains("http")){
							tLis.get(i).setUrl(appConfig.caituUrl + tLis.get(i).getUrl());
						}
					}
					
				}
			}
			pagination.setTotalRow(count);
			pagination.setDatas(tLis);
			
			SimplePropertyPreFilter groupFilter = new SimplePropertyPreFilter(Group.class,GROUP_DETAIL_FILLTER);
			SimplePropertyPreFilter templateFileFilter = new SimplePropertyPreFilter(TemplateDto.class,TEMPLATE_FILE_FILLTER);
			
			return result.toJSONString(0,"success",pagination,new SimplePropertyPreFilter[]{templateFileFilter,groupFilter});
			
		
		} catch (Exception e) {
			logger.error("模板分页查询失败:" + e.getMessage());
			throw new ItemException(-1, "模板分页查询失败");
		}
	}
	
	


}

