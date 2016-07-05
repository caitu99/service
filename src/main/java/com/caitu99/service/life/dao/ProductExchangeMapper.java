package com.caitu99.service.life.dao;

import java.util.List;

import com.caitu99.service.life.domain.ProductExchange;
import com.caitu99.service.life.domain.ProductExchangeRecord;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface ProductExchangeMapper extends IEntityDAO<ProductExchange, ProductExchange> {
	int deleteByPrimaryKey(Long id);

	int insert(ProductExchange record);

	int insertSelective(ProductExchange record);

	ProductExchange selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ProductExchange record);

	int updateByPrimaryKey(ProductExchange record);

	List<ProductExchange> selectByTime(ProductExchange record);

	/**
	 * 获取用户产品兑换记录
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUser 
	 * @param userId
	 * @return
	 * @date 2015年11月5日 下午2:51:24  
	 * @author ws
	 */
	List<ProductExchangeRecord> selectByUser(Long userId);

	List<ProductExchange> selectByTimeAndId(ProductExchange record);

	List<ProductExchange> selectAllByProductId(ProductExchange record);

	List<ProductExchange> selectAll();

}