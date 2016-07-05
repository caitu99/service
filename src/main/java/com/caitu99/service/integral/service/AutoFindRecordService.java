package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.integral.domain.AutoFindRecord;

public interface AutoFindRecordService {
    
	/**
	 * 查询发现记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getBySelective 
	 * @param autoFindRecord
	 * @return
	 * @date 2015年12月14日 下午5:38:09  
	 * @author xiongbin
	 */
    AutoFindRecord getBySelective(AutoFindRecord autoFindRecord);
    
    void insert(AutoFindRecord autoFindRecord);
    
    List<AutoFindRecord> selectPageList(AutoFindRecord autoFindRecord);
    
   void insertORupdate(AutoFindRecord autoFindRecord);
}
