/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life.controller.api;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.life.domain.Product;
import com.caitu99.service.life.service.ProductService;
/**
 * o2o产品控制层
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ProductController 
 * @author lawrence
 * @date 2015年11月2日 下午7:00:07 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/life/product")
public class ProductController extends BaseController {
	

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	
    @Autowired
    private ProductService productService;
    
    @Autowired
    private AppConfig appConfig;

    /**
     * 查询所有O2O产品列表
     * @Description: (方法职责详细描述,可空)  
     * @Title: getProductList 
     * @return
     * @date 2015年11月2日 下午7:00:36  
     * @author lawrence
     */
    @RequestMapping(value = "/list/1.0")
    @ResponseBody
    public String getProductList() {
        ApiResult<List<Product>> apiResult = new ApiResult<List<Product>>();
        //业务逻辑
        //chencheng  20151105  mod 增加异常处理，结果不判空
        try{
	        List<Product> list = productService.selectAll();
	        for (Product product : list) {
                if (StringUtils.isNotEmpty(product.getUrl()) && !product.getUrl().startsWith("http"))
                    product.setUrl(appConfig.staticUrl + product.getUrl());
                if (StringUtils.isNotEmpty(product.getImageurl()) && !product.getImageurl().startsWith("http"))
                    product.setImageurl(appConfig.staticUrl + product.getImageurl());
			}
	        return apiResult.toJSONString(0, "获取O2O产品列表成功", list);
        }catch(Exception ex){
        	logger.error("获取O2O产品列表失败");
        	return apiResult.toJSONString(2204, "获取O2O产品列表失败");
        }
        
    }


}

