package com.caitu99.service.user.dao;


import java.util.List;
import java.util.Map;

import com.caitu99.platform.dao.base.func.IEntityDAO;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.dto.ManualDto;
import com.caitu99.service.user.dto.UserDto;

public interface UserMapper extends IEntityDAO<User, User> {


    /*********** old **************/

    int deleteByPrimaryKey(Long id);

	int insert(User record);

	int insertSelective(User record);

	User selectByPrimaryKey(Long id);

	List<User> selectLogin(User user);

	User selectLoginByMobile(User user);

	int updateByPrimaryKeySelective(User record);

	int updateByPrimaryKeyWithBLOBs(User record);

	int updateByPrimaryKey(User record);

	int updateIntegral(User record);

	User isExistMobile(User user);

	// 总财币
	Long sum();

	// 用户总数
	int num();

	List<User> selectall();
	
	List<User> all();
	
	List<User> checkUserExit(User user);

	/**
	 * @Description: 根据微信openid查询用户
	 * @Title: selectUserByOpenidBinding 
	 * @param openid
	 * @return
	 * @date 2015年12月11日 下午2:06:34  
	 * @author xiongbin
	*/
	User selectUserByOpenidBinding(String openid);
	
	/**
	 * 查询用户
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectBySelective 
	 * @param map
	 * @return
	 * @date 2015年12月31日 上午11:10:13  
	 * @author xiongbin
	 */
	User selectBySelective(Map<String,Object> map);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectCompanyUser 
	 * @param id
	 * @return
	 * @date 2016年1月7日 下午3:50:46  
	 * @author ws
	*/
	User selectCompanyUser(Long id);

	/**
	 *
	 * */
	List<User> selectByUserId(Map<String,Object> map);

	Integer selectCountByUserId(Map<String,Object> map);
	
	
	UserDto countuser(Map<String,String> map);
	
	Integer countActiveUser(Map<String,String> map);
	
	Integer countPtTotal(Map<String,String> map);
	
	List<ManualDto> queryPtAvg(Map<String,String> map);
}