package com.caitu99.service.goods.service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.domain.CouponReceiveStock;
import com.caitu99.service.transaction.domain.TransactionRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by Lion on 2015/11/30 0030.
 */
public interface CouponReceiveStockService {
    Pagination<CouponReceiveStock> selectReceiveByUserIdAndOrderNo(Long userid,String order_no, Pagination<CouponReceiveStock> pagination);


}
