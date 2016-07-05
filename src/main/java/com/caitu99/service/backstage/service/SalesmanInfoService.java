package com.caitu99.service.backstage.service;

import java.util.List;

import com.caitu99.service.backstage.dto.SalesmanDto;

/**
 * Created by liuzs on 2016/3/15.
 */
public interface SalesmanInfoService {

	SalesmanDto selectByStall(String startTime,String endTime, String stall);
    
    
    List<SalesmanDto> findByStall(String startTime,String endTime, String stall);
}
