package com.caitu99.service.ishop.service.impl;

import com.caitu99.service.ishop.dao.UserPwdMapper;
import com.caitu99.service.ishop.domain.UserPwd;
import com.caitu99.service.ishop.service.UserPwdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by lhj on 2016/1/14.
 */
@Service
public class UserPwdServiceImpl implements UserPwdService {

    @Autowired
    private UserPwdMapper userPwdMapper;

    @Override
    public void saveUserPwd(UserPwd userPwd) {
        UserPwd tempUserPwd = new UserPwd();
        tempUserPwd.setUserId(userPwd.getUserId());
        tempUserPwd.setAccount(userPwd.getAccount());
        tempUserPwd = userPwdMapper.selectByUserAndAccount(tempUserPwd);
        if (tempUserPwd == null || tempUserPwd.getId() == null) {
            userPwdMapper.insert(userPwd);
        } else {
            if (!userPwd.getPassword().equals(tempUserPwd.getPassword())) {
                tempUserPwd.setPassword(userPwd.getPassword());
                tempUserPwd.setGmtModify(new Date());
                userPwdMapper.updateByPrimaryKeySelective(tempUserPwd);
            }
        }
    }
}
