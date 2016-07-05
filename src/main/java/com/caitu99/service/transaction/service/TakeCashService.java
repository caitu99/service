package com.caitu99.service.transaction.service;

import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;

/**
 * 
 * 
 * @Description: (提现)
 * @ClassName: TakeCashService
 * @author lhj
 * @date 2015年12月3日 下午7:23:31
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface TakeCashService {

	// 提现延时
	void takeDelay(User user, UserAuth userAuth, Long integral);

	// 提现实时
	void takeNow(User user, UserAuth userAuth, Long integral);

}
