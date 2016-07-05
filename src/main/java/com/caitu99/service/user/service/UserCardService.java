package com.caitu99.service.user.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.mail.controller.vo.UserCardTypeVo;
import com.caitu99.service.user.domain.UserCard;

public interface UserCardService {
	int insert(UserCard record);

	List<UserCard> selectByUserCard(UserCard userCard);

	List<UserCard> selectByUserCard2(UserCard userCard);

	List<UserCard> selectByConditions(UserCard userCard);

	List<UserCard> selectByAttrs(UserCard userCard);
	
	List<UserCard> selectByConditionsExt(UserCard userCard);

	Long selectIdByUserCard(UserCard userCard);

	int updateByUserCard(UserCard record);

	int updateByPrimaryKey(UserCard record);

	// 获取积分
	List<UserCard> queryIntegral(Long userid);

	// 获取某个绑定邮箱的所有卡片
	List<UserCard> queryCardByUserIdAndMail(UserCard userCard);

	// 获取总分
	Long total(Long userid);

	UserCard selectByPrimaryKey(Long id);

	// 用户积分过期提醒用
	List<UserCard> selectEffectiveIntegralUser(Integer dayNum);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: selectUserCardForJob
	 * @param userId
	 * @param carTypeId
	 * @date 2015年12月11日 下午2:59:56
	 * @author ws
	 * @return
	 */
	List<UserCard> selectUserCardForJob(Long userId, Long cardTypeId);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: getAllUserDistinct
	 * @return
	 * @date 2015年12月16日 下午8:20:50
	 * @author ws
	 */
	List<UserCardTypeVo> getAllUserDistinct();

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: queryUserCardForJob
	 * @param day3
	 * @return
	 * @date 2015年12月17日 下午4:51:54
	 * @author ws
	 */
	List<UserCard> queryUserCardForJob(Integer day3);

	/**
	 * @Description: (方法职责详细描述,可空)
	 * @Title: selectByUserCard2
	 * @param userCard
	 * @param typeIds
	 * @return
	 * @date 2015年12月23日 下午1:57:54
	 * @author xiongbin
	 */
	List<UserCard> selectByUserCard2(UserCard userCard, String typeIds);

	/**
	 * 修改时间
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: updateDateById
	 * @param id
	 * @param now
	 * @date 2015年12月23日 下午4:48:39
	 * @author xiongbin
	 */
	void updateGmtModifyByPrimaryKey(Long id, Date now);
	
	List<ManualResult> getByUserManualInfo(Map map);
}
