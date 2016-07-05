package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.integral.controller.vo.IntegralLiftingVo;
import com.caitu99.service.integral.domain.IntegralLifting;
import com.caitu99.service.sys.domain.Page;
import com.caitu99.service.user.domain.User;

public interface IntegralLiftingService {
	// 实时到账
	String payNow(User user, Long integral);

	// 延时到账
	String paydelay(User user, Long intergral);

	// 统计当日提现成功总积分数量
	Long countIntergral(IntegralLifting record);

	// 根据order和userid更改
	int updateByPrimaryKeySelective(IntegralLifting record);

	// 修改回原积分
	Boolean refundintegral(User user);

	// 统计用户当日提现积分数量
	Long countIntergralByUser(IntegralLifting record);

	// 获取提现成功的所有信息
	List<IntegralLiftingVo> listSucess(Page page);

	// 获取提现所有信息
	List<IntegralLiftingVo> listAll(Page page);

	// 这是页数
	int countNum();
}
