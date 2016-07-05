
package com.caitu99.service.transaction.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.transaction.domain.CusTransactionRecord;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.TransactionRecordService;


/**
 * Created by Lion on 2015/11/25 0025.
 */
@Controller
@RequestMapping("/api/transaction/details")
public class TransactionDetailsController {
    private final static Logger logger = LoggerFactory.getLogger(TransactionDetailsController.class);


    private static final String[] TRANSACTION_RECORD_LIST_FILLTER = {"info","tubistr","rmbstr","typestr","statusstr","totalstr","createTime","iconurl"};

    @Autowired
    private TransactionRecordService transactionRecordService;

    @RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String list(Long user_id, Pagination<CusTransactionRecord> pagination) {
        ApiResult<Pagination<CusTransactionRecord>> result = new ApiResult<>();
        pagination = transactionRecordService.selectTransactionRecoredByUserId(user_id, pagination,null,null);
        return result.toJSONString(0,"success",pagination,TransactionRecord.class,TRANSACTION_RECORD_LIST_FILLTER);
    }
}
