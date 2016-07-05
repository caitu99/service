package com.caitu99.service.transaction.controller.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.transaction.domain.CusTransactionRecord;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.dto.EnterpriseDetailDto;
import com.caitu99.service.transaction.dto.EnterpriseDto;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;


@Controller
@RequestMapping("/api/transaction/enterprise")
public class EnterpriseController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(EnterpriseController.class);
    private static final String[] TRANSACTION_RECORD_LIST_FILLTER = {"info","typestr","statusstr","totalstr","createTime","iconurl"};
    private static final String[] ACCOUNT_DETAIL_LIST_FILLTER = {"integralChange", "type", "gmtCreate"};
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDetailService accountDetailService;

    @Autowired
    private TransactionRecordService transactionRecordService;

    private static final SimplePropertyPreFilter accountFilter = new SimplePropertyPreFilter(EnterpriseDto.class,
            "totalIntegral", "availableIntegral", "freezeIntegral");

    @RequestMapping(value="/account/info/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String account(Long user_id) {
        //账户信息
      //  Account account = accountService.selectByUserId(user_id);
        
    	EnterpriseDto result = transactionRecordService.queryEnterpriseTotal();
        
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/account/detail/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String accountDetail(Long user_id, Date startDate, Date endDate,Pagination<EnterpriseDetailDto> pagination) {
        //账户明细
        ApiResult<Pagination<EnterpriseDetailDto>> result = new ApiResult<>();

        if (startDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
        }
        if (endDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH ));
            endDate = cal.getTime();
        }
        int diffDate = daysBetween(startDate,endDate);
        if (diffDate > 31) {
            return result.toJSONString(7001, "请将查询限制在一个月内");
        }else if( diffDate < 0){
            return result.toJSONString(7001, "结束时间不能早于开始时间");
        }

       // pagination = accountDetailService.selectByUserIdAndTime(user_id, startDate, endDate, pagination);
        
        
        pagination = transactionRecordService.pageEnterpriseDetail(startDate, endDate, pagination);
        
        
        return result.toJSONString(0,"success",pagination);
    }

    @RequestMapping(value = "/tran/detail/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String tranDetail(Long user_id,Date start_time,Date end_time,Pagination<CusTransactionRecord> pagination) {
        //交易明细
        ApiResult<Pagination<CusTransactionRecord>> result = new ApiResult<>();
        if (start_time == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            start_time = cal.getTime();
        }
        if (end_time == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH ));
            end_time = cal.getTime();
        }
        int diffDate = daysBetween(start_time,end_time);
        if (diffDate > 31) {
            return result.toJSONString(7001, "请将查询限制在一个月内");
        }else if( diffDate < 0){
            return result.toJSONString(7001, "结束时间不能早于开始时间");
        }

        pagination = transactionRecordService.selectTransactionRecoredByUserId(user_id, pagination,start_time,end_time);
        return result.toJSONString(0,"success",pagination,TransactionRecord.class,TRANSACTION_RECORD_LIST_FILLTER);
    }

    public static int daysBetween(Date smdate,Date bdate){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate=sdf.parse(sdf.format(smdate));
            bdate=sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }
}
