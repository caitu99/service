package com.caitu99.service.life.controller.api;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.life.domain.Recharge;
import com.caitu99.service.life.domain.Record;
import com.caitu99.service.life.service.RechargeService;
import com.caitu99.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by zsj on 2015/11/12.
 */
@Controller
@RequestMapping(value = "/api/life/recharge")
public class RechargeController {
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private UserService userService;

    @RequestMapping("/gift/1.0")
    @ResponseBody
    public String Gift(Long userid, Integer giftType, String version, Long type) {
        ApiResult<String> apiResult = new ApiResult<>();

        Map map = rechargeService.gift(userid, giftType, version, type);
        Object code = map.get("code");
        apiResult.setCode((Integer) code);
        Object message = map.get("message");
        apiResult.setMessage((String) message);
        return JSON.toJSONString(apiResult);
    }

    @RequestMapping(value = "/record/1.0")
    @ResponseBody
    public String record(Long userid) {
        ApiResult<List<Record>> apiResult = new ApiResult<>();
        List<Recharge> recharges = rechargeService.findstatus(userid);
        List<Record> list = new ArrayList<>();

        Boolean used1 = false;
        Boolean used2 = false;
        if (recharges.size() > 0) {
            for (Recharge recharge : recharges) {
                if (recharge.getGiftType() == 1) {
                    used1 = true;
                }
                if (recharge.getGiftType() == 2) {
                    used2 = true;
                }
            }
        }
        Record record = new Record();
        record.setUsed(used1);
        record.setType(1);
        list.add(record);
        Record records = new Record();

        records.setType(2);
        records.setUsed(used2);
        list.add(records);
        apiResult.setCode(0);
        apiResult.setData(list);
        return JSON.toJSONString(apiResult);
    }
}
