package com.caitu99.service.backstage.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.backstage.dto.SalesmanDto;
import com.caitu99.service.backstage.service.SalesmanInfoService;
import com.caitu99.service.base.ApiResult;

/**
 * Created by liuzs on 2016/3/14.
 */

@Controller
@RequestMapping("/api/backstage/salesmanInfo")
public class SalesmanInfoController {

    private final static Logger logger = LoggerFactory.getLogger(SalesmanLoginController.class);

    private static final String[] GET_SALESMAN_STATISTICS_INFO = {"totalIntegral","stalls"};
    
    private static final String[] FIND_SALESMAN_STATISTICS_INFO = {"totalIntegral","stalls","mobile","salesmanStall"};

    @Autowired
    SalesmanInfoService salesmanInfoService;

    @RequestMapping(value="/querystall/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String GetSalesmanStatistics(String start,String end, String stall,String type){
        //startTime地推业务员统计时间点 stall 摊位号
    	start = start + " 00:00:00";
    	end = end + " 23:59:59";
    	
    	if("manager".equals(type)){
            ApiResult<List<SalesmanDto>> result = new ApiResult<>();
            List<SalesmanDto> salesmanDtoList = salesmanInfoService.findByStall(start,end,stall);
            result.set(0,"succeed",salesmanDtoList);
            return result.toJSONString(0,"succeed",salesmanDtoList,SalesmanDto.class,FIND_SALESMAN_STATISTICS_INFO);
    	}else{
    	    ApiResult<SalesmanDto> result = new ApiResult<>();
    	    SalesmanDto salesmanDto= salesmanInfoService.selectByStall(start,end,stall);
    	    result.set(0,"succeed",salesmanDto);
    	    return result.toJSONString(0,"succeed",salesmanDto,SalesmanDto.class,GET_SALESMAN_STATISTICS_INFO);
    	}
    }
    
    

}
