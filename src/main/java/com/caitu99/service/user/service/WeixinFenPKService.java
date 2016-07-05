package com.caitu99.service.user.service;

import com.caitu99.service.user.domain.WeixinFenPK;
import com.caitu99.service.user.controller.vo.WeixinFenPKVo;

import java.util.List;

public interface WeixinFenPKService {
	WeixinFenPKVo selectByUserId(Long userid);
	
	int insert(WeixinFenPK record);
	
	int selectOrderSeq(Long totalIntegral);
	
	int selectOrderSeqMax();
	
	int selectCount(Long userid);
	
	List<WeixinFenPKVo> selectTop5();
	
	List<WeixinFenPKVo> getFriendInfos(Integer inviterid);
	
	int updateByPrimaryKeySelective(WeixinFenPK record);
	
	int updateInvitedUser(WeixinFenPK record);
	int updateInvitedUserLoginApp(WeixinFenPK record);
}