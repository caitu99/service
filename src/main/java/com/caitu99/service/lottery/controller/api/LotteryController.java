/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lottery.controller.api;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.lottery.dto.LotteryPageDto;
import com.caitu99.service.lottery.service.LotteryMainService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.service.AccountService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LotteryController 
 * @author fangjunxiao
 * @date 2016年5月10日 下午8:22:54 
 * @Copyright (c) 2015-2020 by caitu99 
 */

@Controller
@RequestMapping("/api/lottery/")
public class LotteryController extends BaseController{
	
	@Autowired
	private LotteryMainService lotteryMainService;
	
	@Autowired
	private AccountService accountService;
	
	
	@RequestMapping(value="autologin/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid){
		ApiResult<String> result = new ApiResult<>();
		if(null == userid)
			return result.toJSONString(-1, "userid is null");
		
		String url = lotteryMainService.redirctLotteryUrl(userid);
		if(StringUtils.isBlank(url))
			return result.toJSONString(-2, "the request failed:404");
		
		return result.toJSONString(0, "success", url);
	}
	
	
	@RequestMapping(value="account/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String queryAccount(Long userid){
		ApiResult<Account> result = new ApiResult<>();
		Account account = accountService.selectByUserId(userid);
		Account query = new Account();
		if(null == account){
			query.setTotalIntegral(0L);
			query.setTubi(0L);
			return result.toJSONString(0, "success",  query);
		}
		
		query.setTotalIntegral(account.getTotalIntegral());
		query.setTubi(account.getTubi());
		
		if(null == account.getTotalIntegral())
			query.setTotalIntegral(0L);
		
		if(null == account.getTubi())
			query.setTubi(0L);
		
		query.setUserId(userid);
		return result.toJSONString(0, "success", query);
	}
	
	
	@RequestMapping(value="record/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String record(Long userid,Pagination<LotteryPageDto> pagination){
		ApiResult<Pagination<LotteryPageDto>> result = new ApiResult<>();
        pagination = lotteryMainService.findPageByLottery(userid, pagination);
        return result.toJSONString(0,"success",pagination, LotteryPageDto.class);
	}
	


}
