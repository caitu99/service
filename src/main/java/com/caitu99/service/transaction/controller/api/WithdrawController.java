package com.caitu99.service.transaction.controller.api;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.transaction.service.WithdrawService;

@Controller
@RequestMapping("/api/transaction/withdraw")
public class WithdrawController extends BaseController {

	@Autowired
	private WithdrawService withdrawService;
	/**
	 * @Description: (提现)
	 * @Title: withdraw 
	 * @param userId
	 * @param userBankCardId
	 * @param amount
	 * @param payPass
	 * @return
	 * @date 2016年3月31日 下午4:55:17  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value="/submit/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
	public String withdraw(Long userId, Long userBankCardId, Long amount,
			String payPass){
		
		ApiResult<String> result = new ApiResult<String>();
		if(null == userId || null == userBankCardId || null == amount
				|| StringUtils.isBlank(payPass)){
			return result.toJSONString(4101, "参数不完整");
		}
		return withdrawService.withdraw(userId, userBankCardId, amount, payPass);
	}
	
	/**
	 * @Description: (查询变现结果，供定时任务调用)  
	 * @Title: queryWithdraw 
	 * @param id
	 * @return
	 * @date 2016年3月31日 下午4:55:28  
	 * @author Hongbo Peng
	 */
	@RequestMapping(value="/query/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
	public String queryWithdraw(Long id){
		
		ApiResult<String> result = new ApiResult<String>();
		if(null == id ){
			return result.toJSONString(-1, "参数id不能为空");
		}
		return withdrawService.queryWithdraw(id);
	}

	/**
	 * @Description: (当天是否提现过)
	 * @Title: isWithdraw
	 * @param userId
	 * @return
	 * @date 2016年4月5日
	 * @author liuzs
	 */
	@RequestMapping(value = "/iswithdraw/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String isWithdraw(Long userId){

		ApiResult<String> result = new ApiResult<String>();

		return withdrawService.isWithdraw(userId);
	}

	
}
