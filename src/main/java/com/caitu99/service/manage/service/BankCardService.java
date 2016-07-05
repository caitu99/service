package com.caitu99.service.manage.service;

import java.util.List;

import com.caitu99.service.manage.domain.BankCard;

public interface BankCardService {
	
	/**
	 * 查询在线办理银行卡信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectOnLinePageList 
	 * @return
	 * @date 2015年12月25日 上午11:50:15  
	 * @author xiongbin
	 */
	List<BankCard> selectOnLinePageList();
	
	/**
	 * 查询上门办理银行卡信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectDropInPageList 
	 * @return
	 * @date 2015年12月25日 上午11:50:15  
	 * @author xiongbin
	 */
	List<BankCard> selectDropInPageList();
	
	/**
	 * 根据id查询银行
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByPrimaryKey 
	 * @param id
	 * @return
	 * @date 2015年12月28日 下午4:39:56  
	 * @author xiongbin
	 */
	BankCard selectByPrimaryKey(Long id);
}
