package com.caitu99.service.life.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caitu99.service.integral.dao.ConsumeMapper;
import com.caitu99.service.integral.domain.Consume;
import com.caitu99.service.life.dao.ActivationMapper;
import com.caitu99.service.life.dao.ProductExchangeMapper;
import com.caitu99.service.life.dao.ProductMapper;
import com.caitu99.service.life.domain.Activation;
import com.caitu99.service.life.domain.Product;
import com.caitu99.service.life.domain.ProductExchange;
import com.caitu99.service.life.domain.ProductExchangeRecord;
import com.caitu99.service.life.service.ProductExchangeService;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.utils.string.StrUtil;

@Service
public class ProductExchangeServiceImpl implements ProductExchangeService {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductExchangeServiceImpl.class);
	
	@Autowired
	private ProductExchangeMapper productExchangeMapper;
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private ActivationMapper activationMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ConsumeMapper consumeMapper;

	@Override
	public int deleteByPrimaryKey(Long id) {
		return productExchangeMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(ProductExchange record) {
		return productExchangeMapper.insert(record);
	}

	@Override
	public int insertSelective(ProductExchange record) {
		return productExchangeMapper.insertSelective(record);
	}

	@Override
	public ProductExchange selectByPrimaryKey(Long id) {
		return productExchangeMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(ProductExchange record) {
		return productExchangeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ProductExchange record) {
		return productExchangeMapper.updateByPrimaryKey(record);
	}

	@Override
	public Boolean check(User user, Long productId) {
		if(null == user.getIntegral()){
			return false;
		}
		
		Product product = productMapper.selectByPrimaryKey(productId);
		/*if (null != product && null != user.getIntegral()) {
			if (user.getIntegral() >= product.getPrice()) {
				return true;
			}
		}*/
		if(null == product){
			return false;
		}else{
			if (user.getIntegral() >= product.getPrice()) {
				return true;
			}
		}
		
		
		return false;
	}

	@Override
	public List<ProductExchange> queryCurWeekExchangeHistoryByUserId(Long userid) {
		Date date = StrUtil.getTimesWeekmorning();
		ProductExchange record = new ProductExchange();
		record.setUserid(userid);
		record.setTime(date);
		return productExchangeMapper.selectByTime(record);
	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public Boolean checkAndExchange(User user, Long productid, Long activationid) {
		ProductExchange productExchange = new ProductExchange();
		productExchange.setProductid(productid);
		productExchange.setUserid(user.getId());
		productExchange.setTime(new Date());
		productExchange.setStatus(1);
		productExchange.setActivationid(activationid);
		Activation activation = createActivationRecord(activationid);
		Product product = productMapper.selectByPrimaryKey(productid);
		Consume consume = createConsumeRecord(user, product);
		if (null != product) {
			user.setIntegral(user.getIntegral() - product.getPrice());
			consumeMapper.insert(consume);
			productExchangeMapper.insertSelective(productExchange);
			activationMapper.updateByPrimaryKeySelective(activation);
			userMapper.updateIntegral(user);
			return true;
		}
		return false;
	}

	@Override
	public List<ProductExchangeRecord> queryAllExchangeHistoryByUserId(Long userId) {
		return productExchangeMapper.selectByUser(userId);
	}

	@Override
	public List<ProductExchange> queryUserExchangeHistoryInPeriod(Long userid, Product product) {
		Date date = getTime(product);

		ProductExchange record = new ProductExchange();
		record.setUserid(userid);
		record.setTime(date);
		record.setProductid(product.getId());
		List<ProductExchange> list = productExchangeMapper.selectByTimeAndId(record);
		return list;

	}

	/**
	 * 获取日期时间
	 * 	
	 * @Description: 
	 * @Title: getTime 
	 * @param product
	 * @return
	 * @date 2015年11月2日 下午5:14:44  
	 * @author ws
	 */
	private Date getTime(Product product) {
		Date date = new Date();
		switch (product.getDays().intValue()) {
		case 1:
			date = StrUtil.gettiimeforday();
			break;
		case 2:
			date = StrUtil.getTimesWeekmorning();
			break;
		case 3:
			date = StrUtil.getFirstDate(date);
			break;
		default:
			break;
		}
		return date;
	}

	@Override
	public List<ProductExchange> queryUserExchangeHistoryByProductId(Long userId, Long productId) {
		ProductExchange record = new ProductExchange();
		record.setUserid(userId);
		record.setProductid(productId);
		return productExchangeMapper.selectAllByProductId(record);
	}

	@Override
	public Boolean isIntegralEnough(User user, Product product) {
		if(null == user.getIntegral()){
			return false;
		}else{
			int compartor = new BigDecimal(user.getIntegral())
							.compareTo(new BigDecimal(product.getPrice()));
			if (compartor != -1) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public Boolean checkAndExchange(User user, Product product, Long activationid) {
		
		ProductExchange productExchange = createProductExchangeRecord(user,
				product, activationid);
		
		Activation activation = createActivationRecord(activationid);
		
		//Product product = productMapper.selectByPrimaryKey(productid);
		
		Consume consume = createConsumeRecord(user, product);
		try {
			user.setIntegral(user.getIntegral() - product.getPrice());
			consumeMapper.insert(consume);
			productExchangeMapper.insertSelective(productExchange);
			activationMapper.updateByPrimaryKeySelective(activation);
			userMapper.updateIntegral(user);
			return true;
		} catch (Exception e) {
			logger.error("产品兑换失败，用户编号：{}，产品编号：{}",user.getId(),product.getId());
			return false;
		}
		
	}

	@Override
	public List<ProductExchange> selectAll() {
		return productExchangeMapper.selectAll();
	}

	/**
	 * 构建消费记录
	 * 	
	 * @Description:   
	 * @Title: createConsumeRecord 
	 * @param user
	 * @param product
	 * @return
	 * @date 2015年11月2日 下午5:10:04  
	 * @author ws
	 */
	private Consume createConsumeRecord(User user, Product product) {
		Consume consume = new Consume();
		consume.setIntegral(product.getPrice());
		consume.setUserid(user.getId());
		consume.setRegulation(2);//1加积分， 2扣积分
		consume.setUsetype(2L);//消费类型   提现:1  助家生活:2  话费充值：3 信用卡积分兑财币：4
		consume.setStatus(1);//状态 
		return consume;
	}

	/**
	 * 构建激活记录
	 * 	
	 * @Description: 
	 * @Title: createActivationRecord 
	 * @param activationid
	 * @return
	 * @date 2015年11月2日 下午5:10:51  
	 * @author ws
	 */
	private Activation createActivationRecord(Long activationid) {
		Activation activation = new Activation();
		activation.setId(activationid);
		activation.setStatus(2);
		return activation;
	}

	/**
	 * 构建积分兑换记录
	 * 	
	 * @Description:  
	 * @Title: createProductExchangeRecord 
	 * @param user
	 * @param product
	 * @param activationid
	 * @return
	 * @date 2015年11月2日 下午5:11:43  
	 * @author ws
	 */
	private ProductExchange createProductExchangeRecord(User user,
			Product product, Long activationid) {
		ProductExchange productExchange = new ProductExchange();
		productExchange.setProductid(product.getId());
		productExchange.setUserid(user.getId());
		productExchange.setTime(new Date());
		productExchange.setStatus(1);
		productExchange.setActivationid(activationid);
		return productExchange;
	}

}
