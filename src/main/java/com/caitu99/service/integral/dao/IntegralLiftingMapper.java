package com.caitu99.service.integral.dao;

import java.util.List;

import com.caitu99.service.integral.controller.vo.IntegralLiftingVo;
import com.caitu99.service.integral.domain.IntegralLifting;
import com.caitu99.service.sys.domain.Page;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface IntegralLiftingMapper extends IEntityDAO<IntegralLifting, IntegralLifting> {
	int deleteByPrimaryKey(Long id);

	int insert(IntegralLifting record);

	int insertSelective(IntegralLifting record);

	IntegralLifting selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(IntegralLifting record);

	int updateByPrimaryKey(IntegralLifting record);

	Long countintergral(IntegralLifting record);

	int updateByPrimaryKeySelectivebyorder(IntegralLifting record);

	Long countintergralbyuser(IntegralLifting record);

	List<IntegralLifting> selectBystatus(IntegralLifting record);

	// 获取提现成功的所有信息
	List<IntegralLiftingVo> listSucess(Page page);

	// 获取提现所有信息
	List<IntegralLiftingVo> listAll(Page page);

	// 这是页数
	int countNum();

}