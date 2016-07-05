/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life.service;

import com.caitu99.service.life.domain.Recharge;

import java.util.List;
import java.util.Map;

/**
 * 财币充值服务
 *
 * @author lawrence
 * @Description: (类职责详细描述, 可空)
 * @ClassName: RechargeService
 * @date 2015年11月10日 下午3:07:47
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface RechargeService {

    /**
     * 营销赠送财币
     *
     * @param userId   用户ID
     * @param giftType 赠送方式 1.邮箱导入 2.手动查询
     * @return
     * @Description: (方法职责详细描述, 可空)
     * @Title: giftWealth
     * @date 2015年11月10日 下午1:42:45
     * @author lawrence
     */
    Map gift(Long userId, Integer giftType,String version,Long type);

    List<Recharge> findstatus(Long userid);
}
