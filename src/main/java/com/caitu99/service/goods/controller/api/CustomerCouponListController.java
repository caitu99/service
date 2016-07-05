package com.caitu99.service.goods.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.service.CouponReceiveStockService;
import com.caitu99.service.goods.service.StockService;
import com.caitu99.service.realization.domain.RealizeShareRecord;
import com.caitu99.service.realization.service.RealizeShareRecordService;
import com.caitu99.service.goods.domain.CouponReceiveStock;
import com.caitu99.service.transaction.controller.api.TransactionDetailsController;
import com.caitu99.service.transaction.domain.TransactionRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Lion on 2015/11/26 0026.
 */
@Controller
@RequestMapping("/api/customer/coupon")
public class CustomerCouponListController {

    private final static Logger logger = LoggerFactory.getLogger(CustomerCouponListController.class);

    @Autowired
    private CouponReceiveStockService couponReceiveStockService;
    
    @Autowired
    private RealizeShareRecordService realizeShareRecordService;

    private static final String[] CusReceiveStock_FILLTER = {"coupon_code","coupon_password","coupon_effectiveTime","coupon_marketPrice","coupon_name","coupon_receive_time","coupon_wap_url","couponType","status","type"};

    @RequestMapping(value="/my/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String mylist(Long userid,Pagination<CouponReceiveStock> pagination, String jsonpCallback) {
        ApiResult<Pagination<CouponReceiveStock>> result = new ApiResult<>();

        pagination = couponReceiveStockService.selectReceiveByUserIdAndOrderNo(userid, null, pagination);

        if(StringUtils.isBlank(jsonpCallback)){
            return result.toJSONString(0,"success",pagination, CouponReceiveStock.class,CusReceiveStock_FILLTER);
        }else{
            return jsonpCallback + "(" + result.toJSONString(0,"success",pagination,CouponReceiveStock.class,CusReceiveStock_FILLTER) + ")";
        }
    }

    @RequestMapping(value="/order/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String list(Long userid, String order_no,Pagination<CouponReceiveStock> pagination, String jsonpCallback) {
        ApiResult<Pagination<CouponReceiveStock>> result = new ApiResult<>();
        if(order_no == null || userid== null)
        {
            return result.toJSONString(0,"error",pagination, CouponReceiveStock.class,CusReceiveStock_FILLTER);
        }
        pagination = couponReceiveStockService.selectReceiveByUserIdAndOrderNo(userid, order_no, pagination);

        if(StringUtils.isBlank(jsonpCallback)){
            return result.toJSONString(0,"success",pagination, CouponReceiveStock.class,CusReceiveStock_FILLTER);
        }else{
            return jsonpCallback + "(" + result.toJSONString(0,"success",pagination,CouponReceiveStock.class,CusReceiveStock_FILLTER) + ")";
        }
    }
    
    
    //查询用户历史券码
    @RequestMapping(value="/history/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String historyList(Long userid, Pagination<RealizeShareRecord> pagination) {
        ApiResult<Pagination<RealizeShareRecord>> result = new ApiResult<>();
        if(userid== null)
        {
            return result.toJSONString(0,"error",pagination, RealizeShareRecord.class);
        }
        pagination = realizeShareRecordService.selectByUserId(userid, pagination);
        
        return result.toJSONString(0,"success",pagination, RealizeShareRecord.class);
    }
}
