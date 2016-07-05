package com.caitu99.service.user.dao;

import java.util.List;

import com.caitu99.service.user.domain.WeixinFenPK;
import com.caitu99.service.user.controller.vo.WeixinFenPKVo;

public interface WeixinFenPKMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WeixinFenPK record);

    int insertSelective(WeixinFenPK record);

    WeixinFenPK selectByPrimaryKey(Integer id);
    
    WeixinFenPKVo selectByUserId(Long userid);
    
    // 查询用户的排名
    int selectOrderSeq(Long totalIntegral);
    int selectOrderSeqMax();
    
    int selectCount(Long userid);
    
    // 获取身价前5名用户
    List<WeixinFenPKVo> selectTop5();
    
    // 获取好友信息列表
    List<WeixinFenPKVo> getFriendInfos(Integer inviterid);

    int updateByPrimaryKeySelective(WeixinFenPK record);

    int updateByPrimaryKey(WeixinFenPK record);
    
    // 修改邀请人的身价信息
    int updateInvitedUser(WeixinFenPK record);
 // 修改邀请人的身价下载app
    int updateInvitedUserLoginApp(WeixinFenPK record);
}