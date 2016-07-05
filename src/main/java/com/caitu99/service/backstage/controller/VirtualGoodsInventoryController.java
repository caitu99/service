package com.caitu99.service.backstage.controller;

import com.caitu99.service.backstage.domain.VirtualGoodsInventory;
import com.caitu99.service.backstage.service.VirtualGoodsInventoryService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by liuzs on 2016/2/16.
 */

@Controller
@RequestMapping("/api/backstage/virtualGoodsInventory")
public class VirtualGoodsInventoryController extends BaseController{

    private final static Logger logger = LoggerFactory.getLogger(VirtualGoodsInventoryController.class);

    @Autowired
    private VirtualGoodsInventoryService virtualGoodsInventoryService;

    @RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String virtualGoodsInventorylist(Date date){
        ApiResult<List<VirtualGoodsInventory>> result = new ApiResult<>();
        Date endTime;//查询截止时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        endTime = setEndTime(cal.getTime());
        List<VirtualGoodsInventory> list = virtualGoodsInventoryService.selectByInventory(endTime);
        result.setData(list);
        return result.toString();
    }

    private Date setEndTime(Date endTime){
        endTime.setHours(23);
        endTime.setMinutes(59);
        endTime.setSeconds(59);
        return endTime;
    }
}
