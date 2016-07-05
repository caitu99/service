package com.caitu99.service.transaction.service;

import java.util.Date;
import java.util.List;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.integral.controller.vo.UnionPayOrder;
import com.caitu99.service.transaction.controller.vo.UnionPaySmartOrder;
import com.caitu99.service.transaction.domain.CusTransactionRecord;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.dto.EnterpriseDetailDto;
import com.caitu99.service.transaction.dto.EnterpriseDto;
import com.caitu99.service.transaction.dto.TransactionRecordDto;

/**
 * Created by Lion on 2015/11/25 0025.
 */

public interface TransactionRecordService {

	Pagination<CusTransactionRecord> selectTransactionRecoredByUserId(
			Long user_id, Pagination<CusTransactionRecord> pagination,Date start_time,Date end_time);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: getTransactionRecord
	 * @param userId
	 * @param orderNo
	 * @return TransactionRecord
	 * @date 2015年12月1日 下午7:40:52
	 * @author ws
	 */
	TransactionRecord getTransactionRecord(Long userId, String orderNo);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: updateByPrimaryKey
	 * @param record
	 * @return
	 * @date 2015年12月1日 下午7:57:27
	 * @author ws
	 */
	int updateByPrimaryKey(TransactionRecord record);
	
	void updateByPrimaryKeySelective(TransactionRecord record);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: selectByOrderNoExludeUserId
	 * @param userId
	 * @param orderNo
	 * @date 2015年12月1日 下午8:01:22
	 * @author ws
	 * @return
	 */
	TransactionRecord selectByOrderNoExludeUserId(Long userId, String orderNo);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: countTotalFreezeByUserId
	 * @param userId
	 * @return
	 * @date 2015年12月1日 下午9:04:20
	 * @author ws
	 */
	Long countTotalFreezeByUserId(Long userId);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: selectByUserId
	 * @param userId
	 * @return
	 * @date 2015年12月1日 下午10:24:18
	 * @author ws
	 */
	List<TransactionRecord> selectByUserIdFreeze(Long userId);

    /**
     * 查询交易记录
     * @param userId
     * @param begin
     * @param end
     * @return
     */
	List<TransactionRecord> selectByUserIdAndTime(Long userId, Date begin,
			Date end);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryRechargeOrders 
	 * @param clientId
	 * @param mobile
	 * @param serialNo
	 * @param orderNo
	 * @return
	 * @date 2016年1月6日 上午11:50:54  
	 * @author ws
	*/
	List<UnionPayOrder> queryRechargeOrders(Long clientId, String mobile,
			String serialNo, String orderNo);


    /**
     * 查询交易总额
     * @param userId
     * @param types
     * @param begin
     * @param end
     * @return
     */
    Integer countByUserIdAndTime(Long userId, List<Integer> types, Date begin, Date end);

    /**
     * 获取最近一条的消费总额
     * @param userId
     * @return
     */
    Integer getLastDayConsumes(Long userId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryRechargeOrdersByPage 
	 * @param clientId
	 * @param mobile
	 * @param serialNo
	 * @param orderNo
	 * @param pagination
	 * @return
	 * @date 2016年1月7日 下午6:28:13  
	 * @author ws
	*/
    Pagination<UnionPayOrder> queryRechargeOrdersByPage(Long clientId, String mobile,
			String serialNo, String orderNo,
			Pagination<UnionPayOrder> pagination);
    
    /**
     * @Description: (3.0银联智慧代充代付查询接口)  
     * @Title: queryUnionPaySmartOrdersByPage 
     * @param clientId
     * @param mobile
     * @param serialNo
     * @param orderNo
     * @param pagination
     * @return
     * @date 2016年5月30日 下午4:02:23  
     * @author Hongbo Peng
     */
    Pagination<UnionPaySmartOrder> queryUnionPaySmartOrdersByPage(Long clientId,
			String mobile, String serialNo, Pagination<UnionPaySmartOrder> pagination);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param transactionRecord
	 * @date 2015年12月30日 下午4:11:18  
	 * @author ws
	*/
	void insert(TransactionRecord transactionRecord);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getTransactionRecordByOrderNo 
	 * @param orderNo
	 * @return
	 * @date 2015年12月31日 上午10:44:10  
	 * @author ws
	*/
	TransactionRecord getTransactionRecordByOrderNo(String orderNo);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveTransactionRecord 
	 * @param userId
	 * @param integral
	 * @param orderId
	 * @param tn
	 * @param txnTime
	 * @date 2016年1月7日 下午2:26:33  
	 * @author ws
	*/
	void saveTransactionRecord(Long userId, Long integral, String orderId,
			String tn, String txnTime);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryByOrderNos 
	 * @param clientId
	 * @param orderList
	 * @date 2016年1月15日 上午11:42:41  
	 * @author ws
	*/
	List<UnionPayOrder> queryByOrderNos(Long clientId, List<String> orderList);
	
	/**
	 * @Description: (3.0银联智慧代充代付查询接口)  
	 * @Title: queryUnionPaySmartOrderByOrderNos 
	 * @param clientId
	 * @param orderList
	 * @return
	 * @date 2016年5月30日 下午4:52:35  
	 * @author Hongbo Peng
	 */
	List<UnionPaySmartOrder> queryUnionPaySmartOrderByOrderNos(Long clientId, List<String> orderList);

	/**
	 * 保存交易明细
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveTransaction 
	 * @param transactionRecordDto
	 * @return
	 * @date 2016年1月21日 下午4:20:10  
	 * @author ws
	 */
	Long saveTransaction(TransactionRecordDto transactionRecordDto);

	/**
	 * 银联支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: payTransactionRecord 
	 * @param userId
	 * @param integral
	 * @param orderId
	 * @param tn
	 * @param txnTime
	 * @date 2016年5月20日 下午12:30:11  
	 * @author xiongbin
	*/
	void payTransactionRecord(Long userId, Long rmb, String orderId, String tn,String txnTime, Long caibi, Long tubi);

	EnterpriseDto queryEnterpriseTotal();
	
	
	Pagination<EnterpriseDetailDto> pageEnterpriseDetail(Date begin,
				Date end,Pagination<EnterpriseDetailDto> pagination);

	
	TransactionRecord selectById(Long id);
	
	TransactionRecord selectByTransactionNumberAndType(String transactionNumber,Integer type);
}
