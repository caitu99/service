package com.caitu99.service.realization.service;

import java.util.List;

import com.caitu99.service.realization.domain.RealizeRecordEsurfing;

public interface RealizeRecordEsurfingService {
	
	void insert(RealizeRecordEsurfing record);

    RealizeRecordEsurfing selectByPrimaryKey(Long id);

    void update(RealizeRecordEsurfing record);

    List<RealizeRecordEsurfing> selectPageList(RealizeRecordEsurfing record);
    
    /**
     * 查询用户使用积分数
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectUserBuyIntegralCount 
     * @param userid			用户ID
     * @param loginAccount		登录账号
     * @param itemId			商品ID
     * @return
     * @date 2016年3月1日 下午5:52:54  
     * @author xiongbin
     */
    Long selectUserBuyIntegralCount(Long userid,String loginAccount,Long itemId);
}
