package com.caitu99.service.transaction.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.caitu99.service.transaction.domain.AccountDetail;

public interface AccountDetailMapper {
	int deleteByPrimaryKey(Long id);

	int insert(AccountDetail record);

	int insertSelective(AccountDetail record);

	AccountDetail selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(AccountDetail record);

	int updateByPrimaryKey(AccountDetail record);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: countTotalIntegralByUserId
	 * @param userId
	 * @return
	 * @date 2015年12月1日 下午8:37:51
	 * @author ws
	 */
	Long countTotalIntegralByUserId(Long userId);

	List<AccountDetail> selectByUserIdAndTime(Map map);

	List<AccountDetail> selectByTimeAndMemo(Map map);
	
	Integer selectCountByUserId(Map map);

	List<AccountDetail> selectByStall(Map map);//地推业务员数据
	
	List<AccountDetail> sumIntegerByTime(Map map);
}
