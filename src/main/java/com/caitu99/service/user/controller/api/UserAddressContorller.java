/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.controller.api;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.user.domain.Address;
import com.caitu99.service.user.service.AddressService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserAddressContorller 
 * @author ws
 * @date 2016年1月21日 下午4:30:11 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("api/user/address")
public class UserAddressContorller extends BaseController {
	
	@Autowired
	AddressService addressService;
	
	@RequestMapping(value="/update/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String updateUserDefaultAddress(Long userId,String name,String mobile,String zipCode
			,String province,String city,String area,String detailed,String number){
		ApiResult<String> result = new ApiResult<String>();
		
		Address address = addressService.selectByUserId(userId);
		Date date = new Date();
		if(null == address){
			Address addressNew = new Address();
			addressNew.setArea(area);
			addressNew.setCity(city);
			addressNew.setCreateTime(date);
			addressNew.setDetailed(detailed);
			addressNew.setIsDefault(1);//默认收货地址
			addressNew.setMobile(mobile);
			addressNew.setName(name);
			addressNew.setProvince(province);
			addressNew.setSort(1);
			addressNew.setUpdateTime(date);
			addressNew.setUserId(userId);
			addressNew.setZipCode(zipCode);
			addressNew.setIdCard(number);
			addressService.insert(addressNew);
		}else{
			address.setArea(area);
			address.setCity(city);
			//address.setCraeteTime(date);
			address.setDetailed(detailed);
			address.setMobile(mobile);
			address.setName(name);
			address.setProvince(province);
			address.setUpdateTime(date);
			//address.setUserId(userId);
			address.setZipCode(zipCode);
			if(StringUtils.isNotBlank(number)){
				address.setIdCard(number);
			}
			addressService.updateByPrimaryKey(address);
		}
		
		return result.toJSONString(0, "success");
	}
	
	@RequestMapping(value="/get/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getUserDefaultAddress(Long userId){

		ApiResult<Address> result = new ApiResult<Address>();
		if(null == userId){
			return result.toJSONString(2008, "用户id不能为空");
		}
		
		Address address = addressService.selectByUserId(userId);

		return result.toJSONString(0, "success",address);
	}
	
}
