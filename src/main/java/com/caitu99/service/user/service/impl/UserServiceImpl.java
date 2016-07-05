package com.caitu99.service.user.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.caitu99.service.utils.crypto.AppUtils;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.encryption.md5.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.RedisKey;
import com.caitu99.service.backstage.domain.UserInfo;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.integral.dao.ManualMapper;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.dao.ContactMapper;
import com.caitu99.service.user.dao.UserAuthMapper;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.dao.UserThirdInfoMapper;
import com.caitu99.service.user.domain.Contact;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.domain.UserThirdInfo;
import com.caitu99.service.user.dto.ManualDto;
import com.caitu99.service.user.dto.UserDto;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.crypto.AESCryptoUtil;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory
            .getLogger(UserServiceImpl.class);

    @Autowired
    private RedisOperate redis;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private UserThirdInfoMapper userThirdInfoMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ManualMapper manualMapper;

    @Autowired
    private AccountService accountService;

    public User getById(Long id) {
       /* String key = String.format(RedisKey.USER_USER_BY_ID_KEY, id);
        String content = redis.getStringByKey(key);

        //get from redis
        if (!StringUtils.isEmpty(content)) {
            return JSON.parseObject(content, User.class);
        }

        //get from db*/
        User user = userMapper.getById(id);
        // if (user != null) {
        // redis.set(key, JSON.toJSONString(user));
        // }
        return user;
    }


    /****************
     * old
     *********************/

    @Transactional(rollbackFor = {RuntimeException.class})
    public int updateUserInfo(User user) {
        try {
            User preUser = userMapper.selectByPrimaryKey(user.getId());
            if (StringUtils.isNotEmpty(user.getMobile())) {
                if (!user.getMobile().equals(preUser.getMobile())) {
                    //如果联系方式变更了，那么将与原联系方式一样的记录更新为最新联系方式
                    //更新号码，修改所有相关联系方式1
                    List<Contact> contactList = contactMapper
                            .selectByMobile1(preUser.getMobile());
                    for (Contact contact : contactList) {
                        contact.setMobile1(user.getMobile());
                        contactMapper.updateByPrimaryKeySelective(contact);
                    }
                    //更新号码，修改所有相关联系方式2
                    contactList = contactMapper.selectByMobile2(preUser
                            .getMobile());
                    for (Contact contact : contactList) {
                        contact.setMobile2(user.getMobile());
                        contactMapper.updateByPrimaryKeySelective(contact);
                    }
                }
            }

            //密码加密
            if (StringUtils.isNotEmpty(user.getPassword())) {
                user.setPassword(AESCryptoUtil.encrypt(user.getPassword()));
            }

            userMapper.updateByPrimaryKeySelective(user);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Transactional(rollbackFor = {RuntimeException.class})
    public int resetPassword(User user) {
        try {
            user.setPassword(AppUtils.MD5(user.getPassword()).toLowerCase());
            userMapper.updateByPrimaryKeySelective(user);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Transactional(rollbackFor = {RuntimeException.class})
    public int saveContacts(JSONArray contacts, String userMobile) {
        try {
            Contact contact = new Contact();
            contact.setMobile1(userMobile);
            for (int i = 0; i < contacts.size(); i++) {
                JSONObject jsonObj = contacts.getJSONObject(i);
                if (null != jsonObj.get("phone")
                        && !"".equals(jsonObj.get("phone"))) {
                    contact.setMobile2(jsonObj.get("phone").toString());
                }
                if (null != jsonObj.get("name")
                        && !"".equals(jsonObj.get("name"))) {
                    contact.setName2(jsonObj.get("name").toString());
                }
                if (null != jsonObj.get("email")
                        && !"".equals(jsonObj.get("email"))) {
                    contact.setEmail2(jsonObj.get("email").toString());
                }
                if (contactMapper.selectCount(contact) == 0) {
                    contactMapper.insert(contact);
                }
            }
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public User login(User user) {
        try {
            return userMapper.selectLoginByMobile(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional(rollbackFor = {RuntimeException.class})
    public User loginThird(User user) {
        List<User> preUsers = userMapper.selectLogin(user);
        if (preUsers.size() == 0) {
            user.setLoginCount(1);
            if (StringUtils.isEmpty(user.getMobile())) {
                user.setStatus(2);
            } else {
                user.setStatus(1);
            }
            userMapper.insert(user);
        } else if (preUsers.size() == 1) {
            User preUser = preUsers.get(0);
            user.setId(preUser.getId());
            if (preUser.getLoginCount() != null) {
                preUser.setLoginCount(preUser.getLoginCount() + 1);
            } else {
                preUser.setLoginCount(1);
            }
            if (preUser.getStatus() != 1) {
                user.setStatus(1);
            }
            userMapper.updateByPrimaryKeySelective(user);
            User users = userMapper.selectByPrimaryKey(user.getId());
            return users;
        } else {
            for (int i = 0; i < preUsers.size(); i++) {
                if (!StringUtils.isEmpty(preUsers.get(i).getMobile())) {
                    return preUsers.get(i);
                }
            }
            return preUsers.get(0);
        }
        return user;
    }

    /*
     * 普通注册
     */
    @Override
    public int regist(User user) {
        return userMapper.insert(user);
    }

    @Override
    public User isExistMobile(User user) {
        // TODO Auto-generated method stub
        return userMapper.isExistMobile(user);
    }

    @Override
    public User selectByPrimaryKey(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public User selectLoginByMobile(User user) {
        return userMapper.selectLoginByMobile(user);
    }

    @Override
    public void updateUserIntegral(Long id, Integer integral) {
        User user = userMapper.selectByPrimaryKey(id);
        if (null == user.getIntegral()) {
            user.setIntegral(0);
        }
        user.setIntegral(integral + user.getIntegral());
        userMapper.updateIntegral(user);
    }

    @Override
    public void updateByPrimaryKeySelective(User user) {
        String key = String.format(RedisKey.USER_USER_BY_ID_KEY, user.getId());
        redis.del(key);
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 绑定第三方信息
     *
     * @param user
     * @param userThirdInfo
     * @return
     */
    @Override
//	@Transactional(rollbackFor = { RuntimeException.class })
    public User bindingthird(User user, UserThirdInfo userThirdInfo) {
        // 更新用户信息
        userMapper.updateByPrimaryKeySelective(user);
        // 如果绑定第三方,则修改没有手机号的user改成2状态
//		if (	null != user.getQq() ||
//				null != user.getOpenid() ||
//				null != user.getWeibo()) {		//判断第三方信息
//			List<User> preUsers = userMapper.selectLogin(user);//用第三方信息查找用户
//			for(User user_it : preUsers)
//			{
//				if(StringUtils.isEmpty(user_it.getMobile()))//判断手机号是否为空
//				{
//					user_it.setStatus(2);
//					userMapper.updateByPrimaryKeySelective(user_it);
//				}
//			}
//		}
        User users = userMapper.selectByPrimaryKey(user.getId());
        if (null != userThirdInfo.getQqUid() ||
                null != userThirdInfo.getWeiboUid() ||
                null != userThirdInfo.getWeixinOpenid()) {
            //根据用户id查找第三方信息
            UserThirdInfo newuserThirdInfo = userThirdInfoMapper.selectByUserId(user.getId());
            userThirdInfo.setUserId(user.getId());//设置第三方信息对应的userid
            if (newuserThirdInfo == null) {//不存在则插入
                userThirdInfoMapper.insertSelective(userThirdInfo);
            } else {//存在则更新
                userThirdInfoMapper.updateByuserid(userThirdInfo);
            }

            setUserImageUrl(users, userThirdInfo);
        }
        return users;
    }

    public void setUserImageUrl(User user, UserThirdInfo userThirdInfo) {
        if (null != userThirdInfo) {
            if (StringUtils.isEmpty(user.getImgurl())) {
                if (!StringUtils.isEmpty(userThirdInfo.getWeixinImgurl())) {
                    user.setImgurl(userThirdInfo.getWeixinImgurl());
                } else if (!StringUtils.isEmpty(userThirdInfo
                        .getQqProfileImageUrl())) {
                    user.setImgurl(userThirdInfo.getQqProfileImageUrl());
                } else if (!StringUtils.isEmpty(userThirdInfo
                        .getWeiboProfileImageUrl())) {
                    user.setImgurl(userThirdInfo.getWeiboProfileImageUrl());
                }
            }
        }
    }

    @Override
    public UserAuth getUserAuth(Long userid) {
        // TODO Auto-generated method stub
        return userAuthMapper.selectByUserId(userid);
    }

    /**
     * 判断用户是否可绑定
     * 当第三方信息对应的用户还不存在
     * 或者 根据第三方信息只能查找到一个用户，并且，待绑定的用户的手机号不为空
     *
     * @param user
     * @return
     */
    @Override
    public Boolean third(User user) {
        List<User> users = userMapper.selectLogin(user);//根据user的第三方信息，qq,weibo,openid查找用户
        if (users.size() == 0) {        //还未绑定
            return true;
        }

        //只有当只查找到一个符合要求的用户并且待绑定的user的手机号不为空
//		if (users.size() == 1 && null != user.getMobile()) {
//			for (User list : users) {		//查找出来的用户手机号要存在
//				if (null != list.getMobile()) {
//					return true;		//手机号存在的用户才可绑定
//				}
//			}
//		}

        return false;
    }

    @Override
    public List<User> selectall() {
        // TODO Auto-generated method stub
        return userMapper.selectall();
    }

    @Override
    public Long sum() {
        return userMapper.sum();
    }

    @Override
    public int num() {
        return userMapper.num();
    }

    @Override
    public User getWechatBindingUser(String openid) {
//		User user = new User();
//		user.setOpenid(openid);
//		List<User> list = userMapper.selectLogin(user);
//		
//		if(list==null || list.size()==0){
//			return null;
//		}
//		
//		return list.get(0);

        return userMapper.selectUserByOpenidBinding(openid);
    }

    @Override
    public boolean checkUserExit(String mobile, String openid) {
        User user = new User();
        user.setType(1);
        user.setMobile(mobile);
        user.setOpenid(openid);
        user.setStatus(1);

        List<User> list = userMapper.checkUserExit(user);
        if (list == null || list.size() == 0) {
            return false;
        }

        return true;
    }


    @Override
    public int insertSelective(User user) {
        return userMapper.insertSelective(user);
    }


    @Override
    public String checkInvitationCodeByUser(Long userId) throws Exception {
        User user = userMapper.selectByPrimaryKey(userId);
        if (null == user) {
            throw new UserNotFoundException(-1, "用户不存在");
        }

        String invitationCode = user.getInvitationCode();
        if (StringUtils.isNotBlank(invitationCode)) {
            return invitationCode;
        }

        //用户无邀请码,自动生成
        invitationCode = this.generateInvitationCode(10);
        user.setInvitationCode(invitationCode);
        user.setGmtModify(new Date());
        userMapper.updateByPrimaryKeySelective(user);

        return invitationCode;
    }

    /**
     * 生成6位验证码
     *
     * @param count 重试次数
     * @return
     * @throws Exception
     * @Description: (方法职责详细描述, 可空)
     * @Title: generateInvitationCode
     * @date 2015年12月31日 上午11:39:29
     * @author xiongbin
     */
    private String generateInvitationCode(Integer count) throws Exception {
        String invitationCode = User.generateInvitationCode();
        User model = new User();
        model.setInvitationCode(invitationCode);
        model = this.selectBySelective(model);

        if (model != null) {
            if (count < 0) {
                count--;
                return generateInvitationCode(count);
            } else {
                logger.error("生成邀请码失败:6位验证码可能已满或生成类有问题");
                throw new ApiException(-1, "生成邀请码失败");
            }
        }
        return invitationCode;
    }

    @Override
    public User selectBySelective(User user) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("user", user);
        return userMapper.selectBySelective(map);
    }


    /* (non-Javadoc)
     * @see com.caitu99.service.user.service.UserService#selectCompanyUser(java.lang.Long)
     */
    @Override
    public User selectCompanyUser(Long id) {
        User user = userMapper.selectCompanyUser(id);
        return user;
    }

    @Override
    public Pagination<UserInfo> selectByUserId(Long startUserId, Long endUserId, Pagination<UserInfo> pagination) {
        if (pagination == null) {
            return pagination;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("startUserId", startUserId);
        map.put("endUserId", endUserId);
        map.put("start", pagination.getStart());
        map.put("pageSize", pagination.getPageSize());

        List<User> user_list = userMapper.selectByUserId(map);
        Integer count = userMapper.selectCountByUserId(map);
        List<UserInfo> userInfo_list = new LinkedList<>();

        for (User user : user_list) {
            Long userId = user.getId();
            UserInfo userInfo = new UserInfo();

            userInfo.setUserId(userId);
            userInfo.setStartDate(user.getGmtCreate());
            userInfo.setIpGpsInfoTable("/backstage.userinfo/1.0/getipgpsinfo?userId=" + userId);
            userInfo.setAccountsTable("/backstage.userinfo/1.0/getaccountsinfo?userId=" + userId);
            userInfo.setPhoneInfoTable("/backstage.userinfo/1.0/getphoneinfo?userId=" + userId);
            userInfo.setAliveInfoTable("/backstage.userinfo/1.0/getaliveinfo?userId=" + userId);
            userInfo.setInIntegralInfoTable("/backstage.userinfo/1.0/getinintegralinfo?userId=" + userId);
            userInfo.setOutIntegralInfoTable("/backstage.userinfo/1.0/getoutintegralinfo?userId=" + userId);
            userInfo.setTotalIntegralChangeInfoTable("/backstage.userinfo/1.0/gettotalintegralchangeinfo?userId=" + userId);
            userInfo.setIntegrlAaccountInfoTable("/backstage.userinfo/1.0/getintegralaccountinfo?userId=" + userId);

            userInfo_list.add(userInfo);
        }

        pagination.setDatas(userInfo_list);
        pagination.setTotalRow(count);

        return pagination;
    }

    @Override
    @Transactional
    public User register(String phone) {
        User user = new User();
        user.setMobile(phone);
        user = this.isExistMobile(user);
        if (null == user) {
            Date now = new Date();
            user = new User();
            user.setMobile(phone);
            user.setGmtCreate(now);
            user.setGmtModify(now);
            user.setType(1);
            user.setLoginCount(0);
            user.setStatus(1);

            this.regist(user);

            // 创建账户
            Account account = new Account();
            account.setAvailableIntegral(0L);
            account.setFreezeIntegral(0L);
            account.setGmtCreate(new Date());
            account.setGmtModify(new Date());
            account.setTotalIntegral(0L);
            account.setTubi(0L);
            account.setUserId(Long.valueOf(user.getId()));
            accountMapper.insertSelective(account);
        }

        return user;
    }


    @Override
    public UserDto countuser(String startTime, String endTime) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("start", startTime);//地推业务员统计时间点
        map.put("end", endTime);
        return userMapper.countuser(map);
    }


    @Override
    public UserDto conutqueryinteger(String startTime, String endTime) {
        UserDto userDto = new UserDto();
        Map<String, String> map = new HashMap<String, String>();
        map.put("start", startTime);//地推业务员统计时间点
        map.put("end", endTime);
        userDto = userMapper.countuser(map);
        Integer activeUser = userMapper.countActiveUser(map);
        Integer totalAccount = userMapper.countPtTotal(map);
        Integer countManual = manualMapper.countManual();

        List<ManualDto> manualDtoList = userMapper.queryPtAvg(map);
        for (ManualDto manualDto : manualDtoList) {
            String ss = "";
            if (null != activeUser && 0 != activeUser.intValue()) {
                ss = CalculateUtils.percent(manualDto.getPtUser(), activeUser);
            }
            String gg = "";
            if (null != countManual && 0 != countManual.intValue()) {
                gg = CalculateUtils.percent(manualDto.getPtAccount(), countManual);
            }
            manualDto.setPtUserPercent(ss);
            manualDto.setPtAccountPercent(gg);
        }
        String queryRate = "";
        if (null != userDto.getUserTotal() && 0 != userDto.getUserTotal().intValue()) {
            queryRate = CalculateUtils.percent(activeUser, userDto.getUserTotal());
        }
        String avgAccount = "";
        if (null != activeUser && 0 != activeUser.intValue()) {
            avgAccount = CalculateUtils.divide(totalAccount, activeUser);
        }
        userDto.setActiveUser(activeUser);
        userDto.setAvgAccount(avgAccount);
        userDto.setQueryRate(queryRate);
        userDto.setManualDtoList(manualDtoList);

        return userDto;
    }

    @Override
    @Transactional
    public void giveNewUserMoney(Long userId) {
        User user = this.selectByPrimaryKey(userId);
        if (null == user) {
            return;
        } else if (user.getLoginCount() > 1) {
            //非新用户
            return;
        } else if (user.getIsGive().equals(1)) {
            //已赠送
            return;
        }

        Long money = Long.parseLong(Configuration.getProperty("give.new.user.money", null));

        TransactionRecordDto transactionRecord = new TransactionRecordDto();
        transactionRecord.setChannel(4);
        transactionRecord.setComment("新用户首次登录送" + money + "财币");
        transactionRecord.setInfo("活动");
        transactionRecord.setOrderNo("");
        transactionRecord.setTotal(money);
        transactionRecord.setTransactionNumber(XStringUtil.createSerialNo("HD", String.valueOf(userId)));
        transactionRecord.setType(5);
        transactionRecord.setUserId(userId);
        transactionRecord.setSource(2);
        transactionRecord.setTubi(0L);
        transactionRecord.setRmb(0L);
        AccountResult result = accountService.add(transactionRecord);
        if (3101 == result.getCode()) {
            user.setIsGive(1);
            this.updateByPrimaryKeySelective(user);
        }
    }

    @Override
    public int checkOriginalPassword(String orginPassword, Long userid) {
        User user = this.selectByPrimaryKey(userid);
        if (user == null) {
            return 1;
        } else if (AppUtils.MD5(orginPassword).equalsIgnoreCase(user.getPassword())) {
            return 0;
        }else {
            return 2;
        }
    }
}
