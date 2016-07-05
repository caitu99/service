package com.caitu99.service.transaction.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.integral.controller.vo.UnionPayOrder;
import com.caitu99.service.transaction.controller.vo.UnionPaySmartOrder;
import com.caitu99.service.transaction.domain.CusTransactionRecord;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.dto.EnterpriseDetailDto;
import com.caitu99.service.transaction.dto.EnterpriseDto;

public interface TransactionRecordMapper {
	int deleteByPrimaryKey(Long id);

	int insert(TransactionRecord record);

	int insertSelective(TransactionRecord record);

	TransactionRecord selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TransactionRecord record);

	int updateByPrimaryKey(TransactionRecord record);

	List<CusTransactionRecord> selectPageListByUserId(Map<String, Object> map);

	Integer selectCountByUserId(Map<String, Object> map);

	TransactionRecord selectByUserIdAndOrderNo(Map<String, Object> map);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: selectByOrderNoExludeUserId
	 * @param userId
	 * @param orderNo
	 * @date 2015年12月1日 下午8:01:53
	 * @author ws
	 * @return
	 */
	TransactionRecord selectByOrderNoExludeUserId(Map<String, Object> map);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: countTotalFreezeByUserId
	 * @param userId
	 * @return
	 * @date 2015年12月1日 下午9:05:01
	 * @author ws
	 */
	Long countTotalFreezeByUserId(Long userId);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: selectByUserId
	 * @param userId
	 * @return
	 * @date 2015年12月1日 下午10:25:13
	 * @author ws
	 */
	List<TransactionRecord> selectByUserIdFreeze(Long userId);

	/**
	 * 根据时间段查询用户交易记录
	 */
	List<TransactionRecord> selectByUserIdAndTime(Map map);

	/**
	 * 用于银联交易查询
	*/
	List<UnionPayOrder> selectForUnionPay(Map<String, Object> queryParam);

    /**
     * 根据时间段查询用户交易总额
     */
    Integer countByUserIdAndTime(Map<String, Object> map);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryRechargeOrdersCount 
	 * @param queryParam
	 * @return
	 * @date 2016年1月7日 下午6:35:19  
	 * @author ws
	*/
	Integer queryRechargeOrdersCount(Map<String, Object> queryParam);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryRechargeOrdersByPage 
	 * @param queryParam
	 * @return
	 * @date 2016年1月7日 下午6:35:24  
	 * @author ws
	*/
	List<UnionPayOrder> queryRechargeOrdersByPage(Map<String, Object> queryParam);
	
	
	Integer queryUnionPaySmartOrdersCount(Map<String, Object> queryParam);
	/**
	 * @Description: (银联智慧查询接口)  
	 * @Title: queryUnionPaySmartOrdersByPage 
	 * @param queryParam
	 * @return
	 * @date 2016年5月30日 下午4:00:22  
	 * @author Hongbo Peng
	 */
	List<UnionPaySmartOrder> queryUnionPaySmartOrdersByPage(Map<String, Object> queryParam);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByOrderNo 
	 * @param orderNo
	 * @return
	 * @date 2015年12月31日 上午10:45:17  
	 * @author ws
	*/
	TransactionRecord selectByOrderNo(String orderNo);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryByOrderNos 
	 * @param queryParam
	 * @return
	 * @date 2016年1月15日 上午11:49:13  
	 * @author ws
	*/
	List<UnionPayOrder> queryByOrderNos(Map<String, Object> queryParam);
	
	List<UnionPaySmartOrder> queryUnionPaySmartOrderByOrderNos(Map<String, Object> queryParam);

	/**
	 * 根据时间段查询所有用户交易记录
	 */
	List<TransactionRecord> selectByTime(Map map);
	
	List<String> queryTest();

	Integer isWithdraw(Long userId);
	
	Long sameDayWithrawSUM(Long userId);
	
	Long sumPtTotal(Map map);
	
	EnterpriseDto enterpriseTotalInfo(Map<String,Object> map);
	
	List<EnterpriseDetailDto> pageEnterpriseDetail(Map<String,Object> map);
	
	int pageCountEnterpriseDetail(Map<String,Object> map);
	
	TransactionRecord selectByTransactionNumberAndType(Map<String,Object> map);
}
