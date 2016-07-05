package com.caitu99.service.integral.service;

import com.caitu99.service.integral.domain.Bank;

public interface BankService {
	
	/**
	 * 根据名称查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByName 
	 * @param name
	 * @return
	 * @date 2016年1月21日 下午5:28:44  
	 * @author xiongbin
	 */
	Bank selectByName(String name);
}
