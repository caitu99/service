package com.caitu99.service.transaction.service;

import java.util.Map;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.OrderException;
import com.caitu99.service.goods.domain.Sku;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.transaction.domain.Order;
import com.caitu99.service.transaction.dto.OrderDto;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: OrderService 
 * @author tsai
 * @date 2015年11月25日 上午10:48:17 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface OrderService {

	
	/**
	 * 	订单分页查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findPageItem 
	 * @param order
	 * @param pagination
	 * @return
	 * @throws OrderException
	 * @date 2015年11月25日 下午9:19:13  
	 * @author fangjunxiao
	 */
	Pagination<OrderDto> findPageOrder(Order order,Pagination<OrderDto> pagination) throws OrderException;
	
	/**
	 * 生成一个状态为未付款的订单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addOrder 
	 * @param userid
	 * @param lm
	 * @return
	 * @throws OrderException
	 * @date 2015年11月25日 下午5:38:00  
	 * @author fangjunxiao
	 */
	String addOrder(Long userid,Long skuId,Integer quantity) throws OrderException;
	
	
	
	/**
	 * 	第三方积分商城
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addOrderByThird 
	 * @param userid
	 * @param skuId
	 * @param quantity
	 * @return
	 * @throws OrderException
	 * @date 2016年1月12日 下午4:36:01  
	 * @author fangjunxiao
	 */
	String addOrderByThird(Long userid,Long skuId,Integer quantity) throws OrderException;
	
	
	/**
	 * 	第三方商城订单处理   flag:1 成功 0:失败    no:订单号    outNo：第三方商城订单号2
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: processOrderByThird 
	 * @param flag
	 * @param userid
	 * @param no
	 * @param outNo
	 * @throws OrderException
	 * @date 2016年1月13日 下午12:12:49  
	 * @author fangjunxiao
	 */
	String processOrderByThird(Integer flag,Long userid,String no,String outNo) throws OrderException;
	
	
	
	/**
	 * 取消支付	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: cancelPay 
	 * @param orderNo
	 * @throws OrderException
	 * @date 2015年11月25日 下午8:18:17  
	 * @author fangjunxiao
	 */
	String cancelPay(String orderNo) throws OrderException;
	
	/**
	 * 	订单超时
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: timeOut 
	 * @param orderNo
	 * @return
	 * @throws OrderException
	 * @date 2015年11月25日 下午7:57:29  
	 * @author fangjunxiao
	 */
	boolean timeOut(Long userid, String orderNo)throws OrderException;
	
	/**
	 * 	删除订单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: delOrder 
	 * @param orderNo
	 * @throws OrderException
	 * @date 2015年11月25日 下午7:37:05  
	 * @author fangjunxiao
	 */
	String delOrder(Long userId,String orderNo)throws OrderException;
	
	
	/**
	 * 	验证用户
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkUserAndOrder 
	 * @param userid
	 * @param orderNo
	 * @return
	 * @throws OrderException
	 * @date 2015年11月26日 下午5:13:40  
	 * @author fangjunxiao
	 */
	boolean checkUserAndOrder(Long userid,String orderNo) throws OrderException;
	
	
	/**
	 * 	查询库存
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryInventory 
	 * @param sku
	 * @return
	 * @throws OrderException
	 * @date 2015年11月27日 下午2:21:23  
	 * @author fangjunxiao
	 */
	Integer queryInventory(Sku sku) throws OrderException;
	
	
	Order queryOrder(String orderNo);
	
	/**
	 * 	支付验证查询订单信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getToPayByThird 
	 * @param orderNo
	 * @return
	 * @throws OrderException
	 * @date 2016年1月14日 下午4:25:12  
	 * @author fangjunxiao
	 */
	Map<String,Object> getToPayByThird(String orderNo) throws OrderException;

	/**
	 * 自由市场新增订单	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addFreeTradeOrder 
	 * @param userId
	 * @param skuId 商品编号
	 * @param quantity 数量
	 * @param type 订单方向，0：财币消费，1：积分消费
	 * @return 自由市场交易编号
	 * @date 2016年1月20日 上午11:45:53  
	 * @author ws
	*/
	String addFreeTradeOrder(Long userId, Long skuId, Integer quantity);
	
	
	/**
	 * 	兑换
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: exchangeTradeOrder 
	 * @param userId
	 * @param quantity
	 * @param freeTradeId
	 * @return
	 * @throws OrderException
	 * @date 2016年1月25日 下午3:48:53  
	 * @author fangjunxiao
	 */
	String exchangeTradeOrder(Long userId,Integer quantity,Long freeTradeId)throws OrderException;
	
	/**
	 * 	处理第三方购买结果      flag:1成功   0失败
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: processTradeOrder 
	 * @param flag
	 * @param userid  用户ID
	 * @param no	订单编号
	 * @param outNo	第三方订单编号
	 * @return
	 * @throws OrderException
	 * @date 2016年1月25日 下午3:52:32  
	 * @author fangjunxiao
	 */
	String processTradeOrder(Integer flag,Long userid,String no,String outNo)throws OrderException;
	
	/**
	 * 获取自由交易爬第三方商城所需数据	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getGoodByTrade 
	 * @param no
	 * @return
	 * @throws OrderException
	 * @date 2016年1月25日 下午3:51:30  
	 * @author fangjunxiao
	 */
	Map<String,Object> getGoodByTrade(String no,Long userid)throws OrderException;
	
	
	/**
	 * 	生成变现订单 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addRealizeOrder 
	 * @param userId
	 * @param integel	积分总数
	 * @param realizationPlatformId   平台ID
	 * @return
	 * @throws OrderException
	 * @date 2016年2月25日 下午5:16:19  
	 * @author fangjunxiao
	 * @param realizeRecord 
	 */
	String addRealizeOrder(Long userId, Long integel,Long realizationPlatformId, RealizeRecord realizeRecord) throws OrderException;

	/**
	 * 生成兑里程订单
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addMileageOrder 
	 * @param userId
	 * @param realizationPlatformId
	 * @param realizeRecord
	 * @return
	 * @throws OrderException
	 * @date 2016年4月28日 下午12:12:22  
	 * @author ws
	 */
	String addMileageOrder(Long userId, Long realizationPlatformId, Long airlineCompanyId, RealizeRecord realizeRecord) throws OrderException;
	
	/**
	 * 	完成变现订单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: finishRealizeOrder 
	 * @param no
	 * @param userId
	 * @return
	 * @throws OrderException
	 * @date 2016年2月25日 下午5:16:55  
	 * @author fangjunxiao
	 */
	String finishRealizeOrder(String no,Long userId) throws OrderException;


	/**
	 * 生成新手任务订单
	 * @Title: addNewbieActivityOrder 
	 * @param userId	用户ID
	 * @param caibi		财币
	 * @param tubi		途币
	 * @param rmb		人民币
	 * @return
	 * @throws OrderException
	 * @date 2016年6月12日 下午3:32:49  
	 * @author xiongbin
	*/
	String addNewbieActivityOrder(Long userId, Long caibi, Long tubi, Long rmb)throws OrderException;

	/**
	 * 完成新手任务订单
	 * @Title: finishNewbieActivityOrder 
	 * @param no			订单号
	 * @param userId		用户ID
	 * @param isSuccess		是否成功
	 * @throws OrderException
	 * @date 2016年6月12日 下午3:52:25  
	 * @author xiongbin
	*/
	void finishNewbieActivityOrder(String no, Long userId, boolean isSuccess)throws OrderException;


	/**
	 * 	话费充值生成订单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: phoneRechargeOrder 
	 * @param userId
	 * @param amountId
	 * @return
	 * @throws OrderException
	 * @date 2016年6月16日 上午11:06:38  
	 * @author fangjunxiao
	 */
	String phoneRechargeOrder(Long userId,Long amountId,String mobile) throws OrderException;
	
	/**
	 * 	话费充值生成订单（积分付）
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: phoneRechargeOrderByJf 
	 * @param userId
	 * @param amountId
	 * @return
	 * @throws OrderException
	 * @date 2016年6月16日 上午11:06:55  
	 * @author fangjunxiao
	 */
	String phoneRechargeOrderByJf(Long userId,Long amountId,String mobile) throws OrderException;

	/**
	 * 实名认证订单生成 
	 * @Title: addAuthenticationOrder 
	 * @param userId	用户ID
	 * @return
	 * @throws OrderException
	 * @date 2016年6月16日 下午3:44:50  
	 * @author xiongbin
	*/
	String addAuthenticationOrder(Long userId) throws OrderException;

	/**
	 * 修改实名认证订单状态 
	 * @Title: updateAuthenticationOrder 
	 * @param no			订单号
	 * @param status		70.待支付, 71.支付中,72.完成,73.失败,74.已退款
	 * @throws OrderException
	 * @date 2016年6月16日 下午4:09:10  
	 * @author xiongbin
	*/
	void updateAuthenticationOrder(String no, Integer status,String oidPaybill)throws OrderException;
	
    Order selectOrderByOutNo(String outNo);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: timeOut2 
	 * @param userid
	 * @param orderNo
	 * @return
	 * @throws OrderException
	 * @date 2016年6月17日 下午5:23:58  
	 * @author ws
	*/
	boolean timeOut2(Long userid, String orderNo) throws OrderException;
}
