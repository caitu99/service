package com.caitu99.service.activities.service;

import com.caitu99.service.activities.domain.NewbieActivityPay;
import com.caitu99.service.user.domain.User;


public interface NewbieActivityPayService {

	/**
	 * 新增
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param userid			用户ID
	 * @param unionPayNo		银联流水号
	 * @date 2016年5月12日 下午3:31:01  
	 * @author xiongbin
	 */
	void insert(Long userid,String unionPayNo,Long rmb,Long caibi,Long tubi,Long myRightId,String orderNo);
	
	/**
	 * 新增或更新
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insertORupdate 
	 * @param userid			用户ID
	 * @param unionPayNo		银联流水号
	 * @param isPay				是否设置过支付密码
	 * @param user				
	 * @date 2016年5月13日 上午10:59:30  
	 * @author xiongbin
	 */
	void insertORupdate(Long userid,String unionPayNo,boolean isPay,User user,Long rmb,Long caibi,Long tubi,Long myRightId,String orderNo);
	
	/**
	 * 根据userid查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserId 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年5月12日 下午5:12:42  
	 * @author xiongbin
	 */
	NewbieActivityPay selectByUserId(Long userid);
	
	/**
	 * 查询用户支付状态是否存在
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findPayStatus 
	 * @param userid	用户ID
	 * @param status	支付状态(1:支付中;2:支付失败;3:支付成功)
	 * @return
	 * @date 2016年5月12日 下午5:03:16  
	 * @author xiongbin
	 */
	boolean findPayStatus(Long userid,Integer status);
	
	/**
	 * 银联支付成功
	 * @Title: paySuccessDo 
	 * @param userId		用户ID
	 * @param orderNo		银联流水号
	 * @param queryId		第三方订单号
	 * @param flag			支付状态
	 * @date 2016年5月12日 下午5:35:47  
	 * @author xiongbin
	*/
	void paySuccessDo(Long userId, String orderNo, String queryId,boolean flag);
	
    NewbieActivityPay selectByUnionPayNo(String unionPayNo);

	/**
	 * 支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userid	用户ID
	 * @param rmb		人民币
	 * @param caibi		财币
	 * @param tubi		途币
	 * @param isPay		是否支付
	 * @param user		用户对象
	 * @return			银联支付流水号
	 * @date 2016年5月24日 下午4:32:05  
	 * @author xiongbin
	 */
	String pay(Long userid, Long rmb, Long caibi, Long tubi, boolean isPay, User user);
}
