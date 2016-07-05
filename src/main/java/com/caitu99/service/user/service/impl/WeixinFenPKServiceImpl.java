package com.caitu99.service.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.user.controller.vo.WeixinFenPKVo;
import com.caitu99.service.user.dao.WeixinFenPKMapper;
import com.caitu99.service.user.domain.WeixinFenPK;
import com.caitu99.service.user.service.WeixinFenPKService;


@Service
public class WeixinFenPKServiceImpl implements WeixinFenPKService {
	
	@Autowired
	private WeixinFenPKMapper weixinFenPKMapper;

	@Override
	public WeixinFenPKVo selectByUserId(Long userid) {
		return weixinFenPKMapper.selectByUserId(userid);
	}

	@Override
	public int selectOrderSeq(Long totalIntegral) {
		return weixinFenPKMapper.selectOrderSeq(totalIntegral);
	}

	@Override
	public int selectOrderSeqMax() {
		return weixinFenPKMapper.selectOrderSeqMax();
	}

	@Override
	public List<WeixinFenPKVo> selectTop5() {
		return weixinFenPKMapper.selectTop5();
	}

	@Override
	public List<WeixinFenPKVo> getFriendInfos(Integer inviterid) {
		return weixinFenPKMapper.getFriendInfos(inviterid);
	}

	@Override
	public int insert(WeixinFenPK record) {
		return weixinFenPKMapper.insert(record);
	}

	@Override
	public int updateByPrimaryKeySelective(WeixinFenPK record) {
		return weixinFenPKMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateInvitedUser(WeixinFenPK record) {
		return weixinFenPKMapper.updateInvitedUser(record);
	}

	@Override
	public int selectCount(Long userid) {
		return weixinFenPKMapper.selectCount(userid);
	}

	@Override
	public int updateInvitedUserLoginApp(WeixinFenPK record) {
		return weixinFenPKMapper.updateInvitedUserLoginApp(record);
	}
}
