package com.caitu99.service.realization.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.realization.domain.Realize;
import com.caitu99.service.realization.domain.RealizeCoupon;
import com.caitu99.service.realization.domain.RealizeDetail;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.transaction.controller.vo.RechargeResult;

public interface RealizeService {

	/**
	 * @Description: (根据变现平台编号查询可变现数据集合)  
	 * @Title: selectRealizeByPlatform 
	 * @param realizationPlatformId
	 * @return
	 * @date 2016年2月23日 下午2:33:29  
	 * @author Hongbo Peng
	 */
	List<Realize> selectRealizeByPlatform(Long realizationPlatformId);
	
	/**
	 * @Description: (根据变现方案编号)  
	 * @Title: getItemPayParams 
	 * @param realizeId
	 * @return
	 * @date 2016年2月23日 下午2:58:32  
	 * @author Hongbo Peng
	 */
	Map<String,Object> getItemPayParams(Long realizeDetailId);
	
	/**
	 * @Description: (根据变现项查询变现方案)  
	 * @Title: selectByRealizeId 
	 * @param realizeId
	 * @return
	 * @date 2016年2月23日 下午3:26:21  
	 * @author Hongbo Peng
	 */
	List<RealizeDetail> selectRealizeDetailByRealizeId(Long realizeId);
	
	/**
	 * @Description: (查询变现方案)  
	 * @Title: selectByLevel 
	 * @param realizeId
	 * @param realizeDetailId 第一次传null
	 * @return
	 * @date 2016年2月25日 上午11:22:06  
	 * @author Hongbo Peng
	 */
	RealizeDetail selectByLevel(Long realizeId, Long realizeDetailId);
	
	/**
	 * @Description: (保存变现记录)  
	 * @Title: saveRealizeRecord 
	 * @param userId
	 * @param platformId
	 * @param realizeDetailId
	 * @param memo
	 * @return
	 * @date 2016年2月24日 上午10:01:28  
	 * @author Hongbo Peng
	 */
	Long saveRealizeRecord(Long userId, Long platformId, Long realizeDetailId,String memo);
	
	/**
	 * @Description: (当被限额时,修改变现方案)  
	 * @Title: updateRealizeRecord 
	 * @param id
	 * @param realizeDetailId
	 * @date 2016年2月25日 上午11:06:04  
	 * @author Hongbo Peng
	 */
	void updateRealizeRecord(Long id,Long realizeDetailId);
	
	/**
	 * @Description: (修改变现记录的状态)  
	 * @Title: updateRealizeRecordStatus 
	 * @param id
	 * @param status
	 * @date 2016年2月24日 上午10:15:28  
	 * @author Hongbo Peng
	 */
	void updateRealizeRecordStatus(Long id,Integer status);
	
	/**
	 * @Description: (积分变现到账处理)  
	 * @Title: realizeTransfer 
	 * @param realizeRecordId
	 * @date 2016年2月24日 上午11:04:09  
	 * @author Hongbo Peng
	 */
	void realizeTransfer(Long realizeRecordId);
	
	/**
	 * @Description: (启动一个定时任务，给用户返现财币)  
	 * @Title: realizeJob 
	 * @param realizeRecordId
	 * @date 2016年2月24日 上午11:48:59  
	 * @author Hongbo Peng
	 */
	void realizeJob(Long realizeRecordId);

	/**
	 * @Description: (启动一个定时任务，给用户返现财币)
	 * @Title: realizeJob
	 * @param realizeRecordId
	 * @param timeRealizeTransfer
	 * @date 2016年2月24日 上午11:48:59
	 * @author chenhl
	 */
	void realizeJob(Long realizeRecordId,String shopName);
	
	/**
	 * @Description: (根据主键查询变现记录)  
	 * @Title: selectById 
	 * @param id
	 * @return
	 * @date 2016年2月24日 下午5:43:28  
	 * @author Hongbo Peng
	 */
	RealizeRecord selectById(Long id);
	
	/**
	 * @Description: (检查券码6个月内是否存在)  
	 * @Title: checkCodeExists 
	 * @param platformId
	 * @param code
	 * @return
	 * 			true		存在
	 * 			false		不存在
	 * @date 2016年2月24日 下午5:43:21  
	 * @author Hongbo Peng
	 */
	Boolean checkCodeExists(Long platformId, String code);
	
	/**
	 * @Description: (保存变现所得的券信息)  
	 * @Title: saveRealizeCoupon 
	 * @param realizeCoupon
	 * @date 2016年2月24日 下午5:53:25  
	 * @author Hongbo Peng
	 */
	void saveRealizeCoupon(RealizeCoupon realizeCoupon);
	
	/**
	 * @Description: (根据变现记录查询已经保存的券)  
	 * @Title: selectByRealizeRecordId 
	 * @param realizeRecordId
	 * @return
	 * @date 2016年2月25日 下午2:56:20  
	 * @author Hongbo Peng
	 */
	List<RealizeCoupon> selectByRealizeRecordId(Long realizeRecordId);
	
	/**
	 * 批量保存
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveRealizeCouponList 
	 * @param list			券码集合
	 * @param orderNo		订单号
	 * @param userid		用户ID
	 * @param realizeRecordId		积分变现记录ID
	 * @param isSave		是否保存券码
	 * @date 2016年2月25日 下午8:52:12  
	 * @author xiongbin
	 */
	void saveRealizeCouponList(List<RealizeCoupon> list,String orderNo,Long userid,Long realizeRecordId,boolean isSave);
	
	/**
	 * 积分变现支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: realizePay 
	 * @param userid			用户ID
	 * @param realizeRecord		积分变现记录
	 * @param userAddTerm		积分变现用户添加项
	 * @param json				必要数据
	 * @param info				绑定帐号信息JSON
	 * @param isTransfer		是否直接到账
	 * @param remoteId			外部ID
	 * @param remoteType		外部类型(1:账单;2:实时;3:用户添加)
	 * @param isAddUserTerm		是否来自用户添加积分变现账户(0:不是;1:是)
	 * @param isAddCoupon		是否是用户自己输入券码
	 * @return					订单号
	 * @date 2016年2月26日 上午11:59:14  
	 * @author xiongbin
	 */
	String realizePay(Long userid,RealizeRecord realizeRecord,UserAddTerm userAddTerm,JSONObject json,JSONObject info,
										boolean isTransfer,Long remoteId,Integer remoteType,Integer isAddUserTerm,boolean isAddCoupon);
	
	
	/**
	 * 兑里程
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: convertMileage 
	 * @param userid
	 * @param realizeRecord
	 * @param userAddTerm
	 * @param json
	 * @param info
	 * @param isTransfer
	 * @param remoteId
	 * @param remoteType
	 * @param isAddUserTerm
	 * @param isAddCoupon
	 * @return
	 * @date 2016年4月28日 下午12:15:08  
	 * @author ws
	 */
	JSONObject convertMileage(Long userid,RealizeRecord realizeRecord,UserAddTerm userAddTerm,JSONObject json,JSONObject info,
			Long remoteId,Integer remoteType,Integer isAddUserTerm);

	
	/**
	 * @Description: (查询用户移动变现某种方案当月已使用的额度)  
	 * @Title: getUserRealizeSUM 
	 * @param userid
	 * @param account
	 * @param realizeDetailId
	 * @return
	 * @date 2016年2月29日 下午4:59:46  
	 * @author Hongbo Peng
	 */
	Long getUserRealizeSUM(Long userid,String account,Long realizeDetailId);
	
	/**
	 * @Description: (查询某个万里通账户当月已接收的额度)  
	 * @Title: getWLTAccountSUM 
	 * @param wltAccount
	 * @return
	 * @date 2016年2月29日 下午5:01:42  
	 * @author Hongbo Peng
	 */
	Long getWLTAccountSUM(String wltAccount);
	
	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: cmPayBusiness 
	 * @param userid
	 * @param realizeRecordId
	 * @param account
	 * @param password
	 * @return
	 * @date 2016年2月29日 下午6:10:07  
	 * @author Hongbo Peng
	 */
	String cmPayBusiness(Long userid, Long realizeRecordId, String account, String password);
	
	/**
	 * @Description: (修改变现方案)  
	 * @Title: updateRealizeRecord 
	 * @param record
	 * @date 2016年2月29日 下午6:18:55  
	 * @author Hongbo Peng
	 */
	void updateRealizeRecord(RealizeRecord record);
	
	
	/**
	 * 根据订单号查询积分变现记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByOrderNo 
	 * @param orderNo	订单号
	 * @return
	 * @date 2016年3月1日 上午11:23:47  
	 * @author xiongbin
	 */
    RealizeRecord selectByOrderNo(String orderNo);
    
    /**
     * 根据订单号查询模式3所需数据
     * @Description: (方法职责详细描述,可空)  
     * @Title: getMode3Date 
     * @param orderNo		订单号
     * @return
     * @date 2016年3月1日 上午11:33:51  
     * @author xiongbin
     */
    Map<String,Object> getMode3Date(String orderNo);
    
    /**
     * 查询用户最早的未到账的积分变现记录
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectRealizeDetailFirst 
     * @param userId		用户ID
     * @return
     * @date 2016年3月10日 上午9:55:07  
     * @author xiongbin
     */
    List<RealizeRecord> selectRealizeDetailFirst(Long userId);
    
    /**
     * 查询用户冻结财币总数
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectFreezeCashByUserId 
     * @param userId		用户ID
     * @return
     * @date 2016年3月15日 下午4:32:51  
     * @author xiongbin
     */
    Map<String,Long> selectFreezeCashByUserId(Long userId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getItemPayParamsByRecord 
	 * @param realizeRecord
	 * @return
	 * @date 2016年4月12日 下午4:17:39  
	 * @author ws
	*/
	Map<String, Object> getItemPayParamsByRecord(RealizeRecord realizeRecord);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: savePhoneIntegralRecord 
	 * @param userId
	 * @param platformId
	 * @param id
	 * @param jsonString
	 * @return
	 * @date 2016年4月12日 下午6:31:57  
	 * @author ws
	*/
	Long savePhoneIntegralRecord(Long userId, Long platformId, String jsonString,Long amountId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkPhoneLimit 
	 * @param userId
	 * @param amountId
	 * @return
	 * @date 2016年4月14日 下午2:37:31  
	 * @author ws
	*/
	RechargeResult checkPhoneLimit(Long userId, Long amountId);
	
	/**
	 * 查询用户未填券码的数量
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectUserNotCoupon 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月25日 下午3:15:53  
	 * @author xiongbin
	 */
	Integer selectUserNotCoupon(Long userid);

	/**
	 * 生成里程充值记录
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveMileageConvertRecord 
	 * @param userId
	 * @param platformId
	 * @param jsonString
	 * @return
	 * @date 2016年4月28日 上午10:07:05  
	 * @author ws
	*/
	Long saveMileageConvertRecord(Long userId, Long platformId,
			String jsonString, Long mileageId);
	
	/**
	 * 第三方积分商城绑定
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: ishopBinding 
	 * @param userid			用户ID
	 * @param platformId		积分变现平台ID
	 * @param info				账户绑定信息
	 * @param userAddTerm		第三方商城用户添加项
	 * @date 2016年5月11日 下午4:21:49  
	 * @author xiongbin
	 */
	void ishopBinding(Long userid,Long platformId,String info,UserAddTerm userAddTerm);
	
	/**
	 * 查询用户是否变现成功过
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: isRealizeSuccessByUserId 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年5月12日 下午12:07:09  
	 * @author xiongbin
	 */
	boolean isRealizeSuccessByUserId(Long userid);
}
