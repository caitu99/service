package com.caitu99.service.user.service;


import com.alibaba.fastjson.JSONArray;
import com.caitu99.service.backstage.domain.UserInfo;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.domain.UserThirdInfo;
import com.caitu99.service.user.dto.UserDto;

import java.util.List;

public interface UserService {

    User getById(Long id);

    /**************** old ****************/

	// 用户登录
	public User login(User user);

	// 第三方登录
	public User loginThird(User user);

	// 密码找回
	public int resetPassword(User user);

	// 用户信息收集并完善
	public int updateUserInfo(User user);

	// 通讯录关系建立
	public int saveContacts(JSONArray contacts, String userMobile);

	// 普通注册
	public int regist(User user);

	// 手机号是否已存在
	public User isExistMobile(User user);

	// 根据用户id找用户
	public User selectByPrimaryKey(Long id);

	User selectLoginByMobile(User user);

	void setUserImageUrl(User user, UserThirdInfo userThirdInfo);
	/**
	 * 更新用户财币
	 * 
	 * @param id
	 * @param integral
	 *            更新后的财币
	 */
	void updateUserIntegral(Long id, Integer integral);

	void updateByPrimaryKeySelective(User user);

	User bindingthird(User user, UserThirdInfo userThirdInfo);

	// 获得用户绑定的银行信息
	UserAuth getUserAuth(Long userid);

	Boolean third(User user);

	// 总财币
	Long sum();

	// 用户总数
	int num();

	List<User> selectall();
	
	/**
	 * 获取微信绑定的用户信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getWechatBindingUser 
	 * @param openid
	 * @return
	 * @date 2015年12月1日 上午10:56:51  
	 * @author xiongbin
	 */
	User getWechatBindingUser(String openid);
	
	/**
	 * 检查用户是否存在
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkUserExit 
	 * @param mobile	手机号码
	 * @param openid	微信openid
	 * @return
	 * @date 2015年12月7日 下午6:03:16  
	 * @author xiongbin
	 */
	boolean checkUserExit(String mobile,String openid);

	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insertSelective 
	 * @param user
	 * @return
	 * @date 2015年12月7日 下午7:53:12  
	 * @author xiongbin
	*/
	int insertSelective(User user);
	
	/**
	 * 检查用户是否有邀请码,没有自动生成
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkInvitationCodeByUser 
	 * @param userId
	 * @return
	 * @date 2015年12月31日 上午10:31:16  
	 * @author xiongbin
	 */
	String checkInvitationCodeByUser(Long userId) throws Exception;
	
	/**
	 * 查询用户
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectBySelective 
	 * @param user
	 * @return
	 * @date 2015年12月31日 上午11:14:19  
	 * @author xiongbin
	 */
	User selectBySelective(User user);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectCompanyUser 
	 * @param userId
	 * @return
	 * @date 2016年1月7日 下午3:33:15  
	 * @author ws
	*/
	User selectCompanyUser(Long userId);

	/**
	 * 根据userId区间查询
	 * @param startUserId
	 * @param endUserId
	 * @return
	 */
	Pagination<UserInfo> selectByUserId(Long startUserId,Long endUserId,Pagination<UserInfo> pagination);
	
	/**
	 * 注册
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: register 
	 * @param phone		手机号码
	 * @return
	 * @date 2016年4月12日 下午2:52:09  
	 * @author xiongbin
	 */
	User register(String phone);
	
	
	/**
	 * 新注册用户数
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: countuser 
	 * @param startTime
	 * @param endTime
	 * @return
	 * @date 2016年4月28日 上午11:11:14  
	 * @author fangjunxiao
	 */
	UserDto countuser(String startTime,String endTime);
	
	/**
	 * 	活跃用户统计
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: conutqueryinteger 
	 * @param startTime
	 * @param endTime
	 * @return
	 * @date 2016年4月28日 上午11:11:27  
	 * @author fangjunxiao
	 */
	UserDto conutqueryinteger(String startTime,String endTime);
	
	/**
	 * 赠送新用户财币
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: giveNewUserMoney 
	 * @param userId
	 * @date 2016年5月19日 下午2:24:43  
	 * @author xiongbin
	 */
	void giveNewUserMoney(Long userId);

	int checkOriginalPassword(String orginPassword, Long userid);
}

