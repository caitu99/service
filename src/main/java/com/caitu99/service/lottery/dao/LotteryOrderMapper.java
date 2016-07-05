package com.caitu99.service.lottery.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.lottery.domain.LotteryOrder;
import com.caitu99.service.lottery.dto.LotteryPageDto;

public interface LotteryOrderMapper {
    int deleteByPrimaryKey(String orderNo);

    int insert(LotteryOrder record);

    int insertSelective(LotteryOrder record);

    LotteryOrder selectByPrimaryKey(String orderNo);

    int updateByPrimaryKeySelective(LotteryOrder record);

    int updateByPrimaryKey(LotteryOrder record);
    
    int checkIsSame(String outOrderId);
    
    LotteryOrder selectByOutOrderId(String outOrderId);
    
    
    List<LotteryPageDto> pageByLottery(Map<String, Object> map);
    
    Integer pageCount(Map<String, Object> map);
}