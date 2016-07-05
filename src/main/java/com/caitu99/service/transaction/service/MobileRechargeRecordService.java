package com.caitu99.service.transaction.service;

import com.caitu99.service.life.domain.PhoneRechargeRecord;
import com.caitu99.service.sys.domain.Page;

import java.util.List;

/**
 * 手机话费充值记录服务
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PhoneRechargeRecordService 
 * @author lawrence
 * @date 2015年11月2日 下午7:07:27 
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface MobileRechargeRecordService {
	/**
	 * 保存充值记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveRecord 
	 * @param phoneRechargeRecord
	 * @return
	 * @date 2015年11月2日 下午7:06:16  
	 * @author lawrence
	 */
    int saveRecord(PhoneRechargeRecord phoneRechargeRecord);

    /**
     * 获取所有话费充值记录	
     * @Description: (方法职责详细描述,可空)  
     * @Title: listAll 
     * @param page
     * @return
     * @date 2015年11月2日 下午7:06:48  
     * @author lawrence
     */
    List<PhoneRechargeRecord> listAll(Page page);

    /**
     * 获取当前页数	
     * @Description: (方法职责详细描述,可空)  
     * @Title: countNum 
     * @return
     * @date 2015年11月2日 下午7:07:10  
     * @author lawrence
     */
    int countNum();

}
