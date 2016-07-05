package com.caitu99.service.user.dao;


import com.caitu99.service.user.domain.Sign;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface SignMapper extends IEntityDAO<Sign, Sign> {
	
	void updateRecord(Sign sign);
	
    int insert(Sign sign);
    
    int selectCount(Long id);
    
    Sign selectByUserId(Long userId);
}