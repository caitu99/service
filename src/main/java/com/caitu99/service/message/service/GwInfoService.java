package com.caitu99.service.message.service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.message.domain.GwInfo;


public interface GwInfoService {
	
	void insert(GwInfo gwInfo);
	
	void update(GwInfo gwInfo);
	
	GwInfo selectByPrimaryKey(Long id);
	
	Pagination<GwInfo> findPageItem(GwInfo gwInfo,Pagination<GwInfo> pagination);
}
