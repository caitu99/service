package com.caitu99.service.goods.controller.api;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.domain.OrdernoAddr;
import com.caitu99.service.goods.dto.ItemDto;
import com.caitu99.service.goods.service.GetAddressByItemIdService;
import com.caitu99.service.goods.service.OrdernoAddrService;
import com.sun.media.jfxmedia.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by hy on 16-2-22.
 */
@Controller
@RequestMapping("/api/goods/getaddress/")
public class GetAddressByItemIdController {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(GetAddressByItemIdController.class);
    @Autowired
    private GetAddressByItemIdService getAddressByItemIdService;

    @Autowired
    private OrdernoAddrService ordernoAddrService;


    @RequestMapping(value="list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String list(Long userid, Long itemid) {
        ApiResult<String> result = new ApiResult<>();
        if (userid == null || itemid == null) {
            return result.toJSONString(2001, "数据不完整");
        }

        List<String> list = getAddressByItemIdService.getAddressByItemId(itemid);
        if (list == null || list.size() == 0) {
            return result.toJSONString(4001, "根据给定的商品id未查询到地址信息");
        }

        return result.toJSONString(0,"success",list.toString());
    }

    @RequestMapping(value="add/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String addAddr(Long userid, String orderno, String addr) {
        ApiResult<String> result = new ApiResult<>();
        if (userid == null || orderno == null || addr == null) {
            return result.toJSONString(2001, "数据不完整");
        }

        List<OrdernoAddr> list = ordernoAddrService.selectByOrderNo(orderno);
        if (list.size() != 1) {
            return result.toJSONString(4002,"根据orderno查询到记录条数不为1");
        }
        try {
            OrdernoAddr ordernoAddr = list.get(0);
            ordernoAddr.setAddress(addr);
            ordernoAddrService.updateByPrimaryKeySelective(ordernoAddr);
        } catch (Exception e) {
            logger.info("插入地址信息出错",e);
            return result.toJSONString(4003,"插入地址信息出错");
        }
        return result.toJSONString(0,"success",list.toString());
    }

}
