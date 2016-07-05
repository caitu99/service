package com.caitu99.service.message.service.info;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.message.dao.GwInfoMapper;
import com.caitu99.service.message.domain.GwInfo;
import com.caitu99.service.message.service.GwInfoService;
import com.caitu99.service.utils.SpringContext;


@Service
public class GwInfoServiceImpl implements GwInfoService {

	private final static Logger logger = LoggerFactory.getLogger(GwInfoServiceImpl.class);

	@Autowired
	private GwInfoMapper gwInfoMapper;

	@Override
	public void insert(GwInfo gwInfo) {
		Date now = new Date();
		gwInfo.setGmtCreate(now);
		gwInfo.setGmtModify(now);
		gwInfo.setGmtPublish(now);
		gwInfo.setStatus(1);
		
		gwInfoMapper.insertSelective(gwInfo);
	}

	@Override
	public void update(GwInfo gwInfo) {
		Date now = new Date();
		gwInfo.setGmtModify(now);
		gwInfo.setGmtCreate(null);
		gwInfo.setGmtPublish(null);
		
		gwInfoMapper.updateByPrimaryKeySelective(gwInfo);
	}

	@Override
	public GwInfo selectByPrimaryKey(Long id) {
		return gwInfoMapper.selectByPrimaryKey(id);
	}

	@Override
	public Pagination<GwInfo> findPageItem(GwInfo gwInfo,Pagination<GwInfo> pagination) {
		if(null==gwInfo || null==pagination){
			return pagination;
		}
		gwInfo.setStatus(1);
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("gwInfo", gwInfo);
		map.put("start", pagination.getStart());
		map.put("pageSize", pagination.getPageSize());
		
		Integer count = gwInfoMapper.selectPageCount(map);
		List<GwInfo> list = gwInfoMapper.selectPageList(map);
		
		pagination.setDatas(list);
		pagination.setTotalRow(count);
		
		return pagination;
	}
}
