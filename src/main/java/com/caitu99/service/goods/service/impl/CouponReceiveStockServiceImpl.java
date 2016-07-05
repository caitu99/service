package com.caitu99.service.goods.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.dao.ReceiveStockMapper;
import com.caitu99.service.goods.domain.CouponReceiveStock;
import com.caitu99.service.goods.service.CouponReceiveStockService;

/**
 * Created by Lion on 2015/11/30 0030.
 */
@Service
public class CouponReceiveStockServiceImpl implements CouponReceiveStockService {
    private final static Logger logger = LoggerFactory.getLogger(CouponReceiveStockServiceImpl.class);

    @Autowired
    private ReceiveStockMapper receiveStockMapper;
    @Autowired
    private AppConfig appConfig;

    @Override
    public Pagination<CouponReceiveStock> selectReceiveByUserIdAndOrderNo(Long userid, String order_no, Pagination<CouponReceiveStock> pagination) {

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("user_id", userid);
            map.put("start", pagination.getStart());
            map.put("pageSize", pagination.getPageSize());
    		map.put("expiresTime", appConfig.realizeShareExpiresTime);
            if(order_no == null) {
                List<CouponReceiveStock> list = receiveStockMapper.selectReceiveByUserId(map);
                Integer cnt = receiveStockMapper.selectCouponReceiveCountByUserId(map);
                pagination.setDatas(list);
                pagination.setTotalRow(cnt);
            }
            else {
                map.put("order_no", order_no);
                List<CouponReceiveStock> list = receiveStockMapper.selectReceiveByUserIdAndOrderNo(map);
                Integer cnt = receiveStockMapper.selectCouponReceiveCountByUserIdAndOrderNo(map);

                pagination.setDatas(list);
                if(cnt==null)
                    pagination.setTotalRow(0);
                else
                    pagination.setTotalRow(cnt);
            }

            return pagination;
        }
        catch (Exception e)
        {
            logger.error("查询我的礼券出错"+e.getMessage(),e);
            return pagination;
        }
    }
}
