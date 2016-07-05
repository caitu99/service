/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.BaseController;
import com.caitu99.service.transaction.service.CouponService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CouponController 
 * @author Hongbo Peng
 * @date 2015年12月7日 下午5:18:46 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/transaction/coupon/")
public class CouponController extends BaseController{

	@Autowired
	private CouponService couponService;
	
	@RequestMapping(value = "receive/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String receive(Long userId,String code){
		return couponService.receiveCoupon(userId, code);
	}
	
	@RequestMapping(value = "overdue/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String overdue(Long id){
		return couponService.overdueCoupon(id);
	}
}
