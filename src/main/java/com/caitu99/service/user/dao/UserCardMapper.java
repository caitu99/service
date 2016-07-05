package com.caitu99.service.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caitu99.platform.dao.base.func.IEntityDAO;
import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.mail.controller.vo.UserCardTypeVo;
import com.caitu99.service.user.domain.UserCard;

public interface UserCardMapper extends IEntityDAO<UserCard, UserCard> {
	Map<String, String> map = new HashMap<>();

	int deleteByPrimaryKey(Long id);

	int insert(UserCard record);

	int insertSelective(UserCard record);

	UserCard selectByPrimaryKey(Long id);

	List<UserCard> selectByUserCard(UserCard userCard);

	List<UserCard> selectByUserCard2(UserCard userCard);

	List<UserCard> selectByUserCard2(Map<String, Object> map);

	List<UserCard> selectByConditions(UserCard userCard);
	
	List<UserCard> selectByAttrs(UserCard userCard);

	List<UserCard> selectByConditionsExt(UserCard userCard);

	Long selectIdByUserCard(UserCard userCard);

	int updateByPrimaryKeySelective(UserCard record);

	int updateByPrimaryKey(UserCard record);

	int updateByUserCard(UserCard record);

	List<UserCard> queryIntegral(Long userid);

	List<UserCard> queryCardByUserIdAndMail(UserCard userCard);

	List<UserCard> selectByUserIdTime(Map map);

	// 获取总分
	Long total(Long userid);

	// 用户积分过期提醒用
	List<UserCard> selectEffectiveIntegralUser(Integer dayNum);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: selectUserCardForJob
	 * @param paramMap
	 * @return
	 * @date 2015年12月11日 下午3:04:21
	 * @author ws
	 */
	List<UserCard> selectUserCardForJob(Map<String, Object> paramMap);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: getAllUserDistinct
	 * @return
	 * @date 2015年12月16日 下午8:21:49
	 * @author ws
	 */
	List<UserCardTypeVo> getAllUserDistinct();

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: queryUserCardForJob
	 * @param day3
	 * @return
	 * @date 2015年12月17日 下午4:52:59
	 * @author ws
	 */
	List<UserCard> queryUserCardForJob(Integer day);

	/**
	 * @Description: (方法职责详细描述,可空)
	 * @Title: selectByUserCard3
	 * @param map2
	 * @return
	 * @date 2015年12月23日 下午3:20:46
	 * @author xiongbin
	 */
	List<UserCard> selectByUserCard3(Map<String, Object> map);

	/**
	 * 修改时间
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: updateGmtModifyByPrimaryKey
	 * @param map2
	 * @date 2015年12月23日 下午4:59:55
	 * @author xiongbin
	 */
	void updateGmtModifyByPrimaryKey(Map<String, Object> map);
	
	List<ManualResult> getByUserManualInfo(Map<String, Object> map);
}