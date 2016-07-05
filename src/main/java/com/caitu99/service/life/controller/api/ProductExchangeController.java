package com.caitu99.service.life.controller.api;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.life.domain.Activation;
import com.caitu99.service.life.domain.Product;
import com.caitu99.service.life.domain.ProductExchange;
import com.caitu99.service.life.domain.ProductExchangeRecord;
import com.caitu99.service.life.service.ActivationService;
import com.caitu99.service.life.service.ProductExchangeService;
import com.caitu99.service.life.service.ProductService;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.sys.sms.SMSSend;
import com.caitu99.service.sys.sms.SingletonClient;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.rmi.RemoteException;
import java.util.List;

/**
 * 生活-O2O产品兑换
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ProductExchangeController 
 * @author chencheng
 * @date 2015年11月5日 上午11:40:16 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/life/product/o2o")
public class ProductExchangeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProductExchangeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ProductExchangeService productExchangeService;

    @Autowired
    private ActivationService activationService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ConfigService configService;

    /**
     * 检测用户是否可以兑换该产品
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: check 
     * @param userId
     * @param productId
     * @return json
     * @date 2015年11月5日 上午11:40:46  
     * @author ws
     */
    @RequestMapping(value = "/exchange/check/1.0")
    @ResponseBody
    public String check(Long userId, Long productId) {
        ApiResult<String> apiResult = new ApiResult<String>();

        // 业务逻辑
        User user = userService.selectByPrimaryKey(userId);
        if(null ==  user){
        	throw new UserNotFoundException(2037,"用户不存在");
        }
        // 判断商品是否存在
        Product product = productService.selectByPrimaryKey(productId);
        if (null == product) {
            return apiResult.toJSONString(2205,"该商品不存在");
        }
        
        // 判断用户财币足够
        boolean result = productExchangeService.isIntegralEnough(user, product);
        if (!result) {
            return apiResult.toJSONString(2202,"用户财币不足");
        }

        // 判断用户周期兑换次数
        List<ProductExchange> list = productExchangeService
        		.queryUserExchangeHistoryInPeriod(user.getId(),product);
        if (null != list && list.size() >= product.getNumber().intValue()) {
        	int days = product.getDays();
            switch (days) {
			case 1:
				apiResult.setCode(2206);
                apiResult.setMessage("今日兑换次数已用完");
				break;
			case 2:
				apiResult.setCode(2207);
                apiResult.setMessage("本周兑换次数已用完");
				break;
			case 3:
				apiResult.setCode(2208);
                apiResult.setMessage("本月兑换次数已用完");
				break;
			default:
				break;
			}
            
            return JSON.toJSONString(apiResult);
        }else{
	        // 正常返回
	        apiResult.setCode(0);
	        apiResult.setMessage("用户可以兑换该商品");
	        return JSON.toJSONString(apiResult);
        }
    }

    /**
     * 产品兑换
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: exchange 
     * @param userId
     * @param productId
     * @param payPass
     * @return json
     * @throws RemoteException
     * @throws CryptoException
     * @date 2015年11月5日 上午11:41:09  
     * @author ws
     */
    @RequestMapping(value = "/exchange/1.0")
    @ResponseBody
    public String exchange(Long userId, Long productId, String payPass) 
    		throws RemoteException, CryptoException {
        ApiResult<String> apiResult = new ApiResult<String>();
        
        // 业务逻辑
        User user = userService.selectByPrimaryKey(userId);
        if(null ==  user){
        	logger.error("用户不存在");
        	throw new UserNotFoundException(2037,"用户不存在");
        }
        //判断支付密码是否正确
        if (!AESCryptoUtil.encrypt(payPass).equals(user.getPaypass())) {
            return apiResult.toJSONString(2201,"支付密码错误");
        }

        // 判断商品是否存在
        Product product = productService.selectByPrimaryKey(productId);
        if (null == product) {
            return apiResult.toJSONString(2205,"该商品不存在");
        }
        
        // 判断用户财币足够
        boolean result = productExchangeService.isIntegralEnough(user, product);
        if (!result) {
            return apiResult.toJSONString(2202,"用户财币不足");
        }
        
        List<Activation> activations = activationService.selectByType(productId.intValue());
        if (null== activations || activations.isEmpty()) {
            return apiResult.toJSONString(2210,"激活码已兑完");
        }
        //激活码不足
        if(100 > activations.size()){
        	 String[] mobiles = {"15869025782","18601656668","18857107097","18626866331"};
        	 
        	 StringBuilder smsContentbBuilder = new StringBuilder()
        	 				  .append(product.getProduct())
        	 				  .append("激活码不足100,请尽快补足");
//        	 SingletonClient.getClient().sendSMS(mobiles, 
//        			 smsContentbBuilder.toString(), "", 5);
        	 String smsConfig = configService.selectByKey("sms_send_channel").getValue();
        	 SMSSend.sendSMS(mobiles, smsContentbBuilder.toString(),smsConfig);
        }

        //检测是否能兑换，并进行兑换
        boolean results = productExchangeService.checkAndExchange(user, product,
                activations.get(0).getId());
        if (results) {
            return apiResult.toJSONString(0,"兑换成功");
        }
        	
        return apiResult.toJSONString(2214,"产品兑换失败");
        
    }

    /**
     * 获取用户O2O产品兑换记录
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: history 
     * @param userId
     * @return json
     * @date 2015年11月5日 上午11:41:39  
     * @author ws
     */
    @RequestMapping(value = "/exchange/history/1.0")
    @ResponseBody
    public String history(Long userId) {
        ApiResult<List<ProductExchangeRecord>> apiResult = new ApiResult<List<ProductExchangeRecord>>();
        try{
	        List<ProductExchangeRecord> list = productExchangeService.queryAllExchangeHistoryByUserId(userId);
	        return apiResult.toJSONString(0, "获取O2O兑换记录成功", list);
        }catch(Exception ex){
        	logger.error("获取O2O兑换记录失败");
        	return apiResult.toJSONString(2209,"获取O2O兑换记录失败");
        }
        
    }

    /**
     * 检测用户是否可以兑换E袋洗
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: checkEdaixi 
     * @param userId
     * @param productId
     * @return json
     * @date 2015年11月5日 上午11:41:53  
     * @author ws
     */
    @RequestMapping(value = "/exchange/edaixi/check/1.0")
    @ResponseBody
    public String checkEdaixi(Long userId, Long productId) {
        ApiResult<String> apiResult = new ApiResult<String>();

        User user = userService.selectByPrimaryKey(userId);
        if(null ==  user){
        	logger.error("用户不存在");
        	throw new UserNotFoundException(2037,"用户不存在");
        }

        // 判断商品是否存在
        Product product = productService.selectByPrimaryKey(productId);
        if (null == product) {
            return apiResult.toJSONString(2205,"该商品不存在");
        }
        
        // 判断用户财币足够
        Boolean result = productExchangeService.isIntegralEnough(user, product);
        if (!result) {
            return apiResult.toJSONString(2202,"用户财币不足");
        }

        // 判断用户周期兑换次数
        List<ProductExchange> productchagelist = productExchangeService.queryUserExchangeHistoryByProductId(
                user.getId(), product.getId());
        if (null == productchagelist || productchagelist.isEmpty()) {

        	return apiResult.toJSONString(0,"用户可以兑换E袋洗");
        }
        
        return apiResult.toJSONString(2211,"您已经兑换过了，若还需兑换，请使用与之前不同的手机号");

    }

    /**
     * e袋洗兑换
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: exchangeEdaixi 
     * @param userId
     * @param productId
     * @param payPass
     * @return json
     * @throws RemoteException
     * @throws CryptoException
     * @date 2015年11月5日 上午11:42:07  
     * @author ws
     */
    @RequestMapping(value = "/exchange/edaixi/1.0")
    @ResponseBody
    public String exchangeEdaixi(Long userId, Long productId, String payPass) throws RemoteException, CryptoException {
        ApiResult<Object> apiResult = new ApiResult<Object>();

        // 业务逻辑
        User user = userService.selectByPrimaryKey(userId);
        if(null ==  user){
        	logger.error("用户不存在");
        	throw new UserNotFoundException(2037,"用户不存在");
        }
        //判断支付密码是否正确
        if (!AESCryptoUtil.encrypt(payPass).equals(user.getPaypass())) {
            return apiResult.toJSONString(2201,"支付密码错误");
        }

        // 判断商品是否存在
        Product product = productService.selectByPrimaryKey(productId);
        if (null == product) {
            return apiResult.toJSONString(2205,"该商品不存在");
        }
        
        // 判断用户财币足够
        boolean result = productExchangeService.isIntegralEnough(user, product);
        if (!result) {
            return apiResult.toJSONString(2202,"用户财币不足");
        }

        //兑换商品
        boolean results = productExchangeService.checkAndExchange(user, product, -1L);
        if (results) {
           return apiResult.toJSONString(0, "兑换成功");
        }
        return apiResult.toJSONString(2212,"E袋洗兑换失败");
    }

}

