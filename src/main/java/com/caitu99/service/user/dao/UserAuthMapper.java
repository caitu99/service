package com.caitu99.service.user.dao;


import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface UserAuthMapper extends IEntityDAO<UserAuth, UserAuth> {
    int insert(UserAuth record);

    int insertSelective(UserAuth record);
    
    UserAuth selectByUserId(Long userId);
    
    UserAuth selectByUserIdForCardPay(Long userId);

    //支付密码根据用户id判断实名认证
    UserAuth findByUserId(Long userId);
    
	List<UserAuth> selectByAccId(String accId);

}