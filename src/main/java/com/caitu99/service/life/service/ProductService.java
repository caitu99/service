package com.caitu99.service.life.service;
/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
import com.caitu99.service.life.domain.Product;

import java.util.List;
/**
 * o2o产品服务
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ProductService 
 * @author lawrence
 * @date 2015年11月2日 下午7:04:29 
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface ProductService {
	/**
	 * 主键ID删除产品信息	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: deleteByPrimaryKey 
	 * @param id
	 * @return
	 * @date 2015年11月2日 下午7:04:13  
	 * @author lawrence
	 */
    int deleteByPrimaryKey(Long id);
    /**
     * 新增产品 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: insert 
     * @param record
     * @return
     * @date 2015年11月2日 下午7:04:00  
     * @author lawrence
     */
    int insert(Product record);
    /**
     * 新增产品 
     * @Description: (方法职责详细描述,可空)  
     * @Title: insertSelective 
     * @param record
     * @return
     * @date 2015年11月2日 下午7:03:52  
     * @author lawrence
     */
    int insertSelective(Product record);
    /**
     * 主键查询产品
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectByPrimaryKey 
     * @param id
     * @return
     * @date 2015年11月2日 下午7:03:40  
     * @author lawrence
     */
    Product selectByPrimaryKey(Long id);
    /**
     * 主键更新产品 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: updateByPrimaryKeySelective 
     * @param record
     * @return
     * @date 2015年11月2日 下午7:03:23  
     * @author lawrence
     */
    int updateByPrimaryKeySelective(Product record);
    /**
     * 	主键更新产品
     * @Description: (方法职责详细描述,可空)  
     * @Title: updateByPrimaryKey 
     * @param record
     * @return
     * @date 2015年11月2日 下午7:03:09  
     * @author lawrence
     */
    int updateByPrimaryKey(Product record);
    /**
     * 查询所有产品
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectAll 
     * @return
     * @date 2015年11月2日 下午7:02:34  
     * @author lawrence
     */
	List<Product> selectAll();

}