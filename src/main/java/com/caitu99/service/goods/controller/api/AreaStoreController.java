package com.caitu99.service.goods.controller.api;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.domain.AreaStore;
import com.caitu99.service.goods.service.AreaStoreService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Lion on 2015/12/7 0007.
 */
@Controller
@RequestMapping("/api/goods/areastore")
public class AreaStoreController {

    private final static Logger logger = LoggerFactory.getLogger(CustomerCouponListController.class);

    @Autowired
    private  AreaStoreService areaStoreService;

    private static final String[] AreaStore_FILLTER = {"province","city","shopName","shopAddress","shopPhone"};

    @RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String mylist(Long brandid,Pagination<AreaStore> pagination, String jsonpCallback) {
        ApiResult<Pagination<AreaStore>> result = new ApiResult<>();

        pagination = areaStoreService.selectByBrandIdPageList(brandid, pagination);

        if(StringUtils.isBlank(jsonpCallback)){
            return result.toJSONString(0,"success",pagination, AreaStore.class,AreaStore_FILLTER);
        }else{
            return jsonpCallback + "(" + result.toJSONString(0,"success",pagination,AreaStore.class,AreaStore_FILLTER) + ")";
        }
    }
}
