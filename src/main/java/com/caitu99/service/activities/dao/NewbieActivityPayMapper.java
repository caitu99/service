package com.caitu99.service.activities.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.activities.domain.NewbieActivityPay;

public interface NewbieActivityPayMapper {
    int deleteByPrimaryKey(Long id);

    int insert(NewbieActivityPay record);

    int insertSelective(NewbieActivityPay record);

    NewbieActivityPay selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(NewbieActivityPay record);

    int updateByPrimaryKey(NewbieActivityPay record);
    
    /**
     * 查询用户最新的支付状态
     * @Description: (方法职责详细描述,可空)  
     * @Title: findPayStatus 
     * @param map
     * @return
     * @date 2016年5月12日 下午3:54:16  
     * @author xiongbin
     */
    List<NewbieActivityPay> findPayStatus(Map<String,Object> map);
    
    NewbieActivityPay selectByUserId(Long userid);
    
    NewbieActivityPay selectByUnionPayNo(String unionPayNo);
}