package com.caitu99.service.user.service;

import com.caitu99.service.user.domain.FenPK;

import java.util.List;

public interface FenPKService {

	// 添加用户数据
	int add(FenPK fenPK);
	
	// 查询排名
	int queryRank(long userid);

	// 获取参加活动人数
	Long queryJoinNum();

	// 查询用户身价
	FenPK queryValue(Long userid);

	// 获取助力伙伴列表
	List<FenPK> queryPartnerList(Long userid);

	// 根据总积分排序
	List<FenPK> queryAll();
	
	// 为用户添加积分
	void addIntegralByUserID(Long userid, Long integral);
	
	// 邀请者为用户添加积分
	void addIntegralByOthers(Long userid, Long inviterid, Long integral);
}
