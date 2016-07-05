package com.caitu99.service.backstage.service.impl;

import com.caitu99.service.backstage.dao.SalesmanPushRelationMapper;
import com.caitu99.service.backstage.domain.PushStatistics;
import com.caitu99.service.backstage.dto.SalesmanDto;
import com.caitu99.service.backstage.service.SalesmanInfoService;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.domain.AccountDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuzs on 2016/3/15.
 */

@Service
public class SalesmanInfoServiceImpl implements SalesmanInfoService {

    @Autowired
    AccountDetailMapper accountDetailMapper;
    
    @Autowired
    SalesmanPushRelationMapper salesmanPushRelationMapper;
    

    @Override
    public SalesmanDto selectByStall(String startTime,String endTime,String stall) {
    	  Map<String,String> map = new HashMap<String,String>();
        map.put("start", startTime);//地推业务员统计时间点
        map.put("end", endTime);
        map.put("stall", stall);//摊位号
        return salesmanPushRelationMapper.selectByStall(map);
    }

	@Override
	public List<SalesmanDto> findByStall(String startTime, String endTime,
			String stall) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("start", startTime);//地推业务员统计时间点
        map.put("end", endTime);
        map.put("stall", stall);//摊位号
        return salesmanPushRelationMapper.findByStall(map);
	}

}
