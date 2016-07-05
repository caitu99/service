package com.caitu99.service.life.service;

import com.caitu99.service.life.domain.Product;
import com.caitu99.service.life.domain.ProductExchange;
import com.caitu99.service.life.domain.ProductExchangeRecord;
import com.caitu99.service.user.domain.User;

import java.util.List;

/**
 * O2O产品兑换服务
 * <p>
 * Modified by Lion
 */
public interface ProductExchangeService {
    int deleteByPrimaryKey(Long id);

    int insert(ProductExchange record);

    int insertSelective(ProductExchange record);

    ProductExchange selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProductExchange record);

    int updateByPrimaryKey(ProductExchange record);

    /**
     * 检测用户财币是否可以兑换该产品
     *
     * @param user
     * @param productid
     * @return
     */
    Boolean check(User user, Long productid);

    /**
     * 用户本周的兑换记录
     *
     * @param userid
     * @return
     */
    List<ProductExchange> queryCurWeekExchangeHistoryByUserId(Long userid);

    /**
     * 检测是否可以兑换并进行兑换
     *
     * @param user
     * @param productid
     * @param activationid
     * @return
     */
    Boolean checkAndExchange(User user, Long product, Long activationid);

    /**
     * 查询用户的所有兑换记录
     *
     * @param userId
     * @return
     */
    List<ProductExchangeRecord> queryAllExchangeHistoryByUserId(Long userId);

    /**
     * 查询用户在某个周期内兑换某商品的所有记录
     *
     * @param userid 用户id
     * @param product 商品
     * @return
     */
    List<ProductExchange> queryUserExchangeHistoryInPeriod(Long userid, Product product);

    /**
     * 查询用户所有的商品兑换记录
     *
     * @param userId
     * @param productId
     * @return
     */
    List<ProductExchange> queryUserExchangeHistoryByProductId(Long userId, Long productId);

    /**
     * 检测用户财币是否可以兑换该产品
     * 	
     * @Description: 
     * @Title: check 
     * @param user
     * @param product
     * @return
     * @date 2015年11月2日 下午3:47:08  
     * @author ws
     */
	Boolean isIntegralEnough(User user, Product product);

	/**
	 * 检测是否可以兑换并进行兑换
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkAndExchange 
	 * @param user
	 * @param product
	 * @param id
	 * @return
	 * @date 2015年11月2日 下午4:31:37  
	 * @author ws
	 */
	Boolean checkAndExchange(User user, Product product, Long activationid);

    List<ProductExchange> selectAll();

}