package com.caitu99.service.backstage.controller;

import com.caitu99.service.backstage.domain.IntegralData;
import com.caitu99.service.backstage.service.IntegralDataService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chenhl on 2016/2/15.
 */

@Controller
@RequestMapping("/api/backstage/integraldata")
public class IntegralDataController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(IntegralDataController.class);

    @Autowired
    private IntegralDataService integralDataService;

    @RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String mylist(Date date){
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime;
        Date endTime;
        //初始化
        ApiResult<Map<String,IntegralData>> result = new ApiResult<>();
        Map<String,IntegralData> map = new LinkedHashMap<String, IntegralData>();
        Calendar cal = Calendar.getInstance();
        Date nowTime = cal.getTime();
        cal.setTime(date);

        //业务处理
        //上月数据
        cal.add(Calendar.MONTH,-1);
        startTime = cal.getTime();
        startTime.setDate(1);
        setStartTime(startTime);

        endTime = cal.getTime();
        endTime.setDate(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        setEndTime(endTime);

        IntegralData integralData = integralDataService.selectByTime(startTime, endTime);
        map.put("上月汇总",integralData);

        //本月数据
        cal.add(Calendar.MONTH,1);
        startTime = cal.getTime();
        startTime.setDate(1);
        setStartTime(startTime);

        endTime = cal.getTime();

        endTime.setDate(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        setEndTime(endTime);

        integralData = integralDataService.selectByTime(startTime, endTime);
        map.put("本月汇总",integralData);

        String message;
        int day = isSameMonth(date,nowTime) ? (nowTime.getDate()) : (cal.getActualMaximum(Calendar.DAY_OF_MONTH)) ;
        for(; day > 0 ;day--){
            message = day+"日";
            startTime.setDate(day);
            endTime.setDate(day);
            integralData = integralDataService.selectByTime(startTime, endTime);
            map.put(message,integralData);
        }

        result.setData(map);
        return result.toString();
    }

    private boolean isSameMonth(Date date1,Date date2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR)-cal2.get(Calendar.YEAR);
        if(subYear == 0)
        {
            if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
                return true;
        }
        return false;
    }

    private Date setStartTime(Date startTime){
        startTime.setHours(0);
        startTime.setMinutes(0);
        startTime.setSeconds(0);
        return startTime;
    }

    private Date setEndTime(Date endTime){
        endTime.setHours(23);
        endTime.setMinutes(59);
        endTime.setSeconds(59);
        return endTime;
    }
}
