package com.caitu99.service.goods.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.goods.dto.ItemDto;
import com.caitu99.service.transaction.domain.OrderAddress;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.goods.dao.ItemMapper;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.utils.SpringContext;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ItemServiceImpl 
 * @author xiongbin
 * @date 2015年11月24日 下午4:18:49 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class ItemServiceImpl implements ItemService {

	private final static Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
	
	@Autowired
	private ItemMapper itemMapper;

	@Autowired
	private RedisOperate redis;
	
	@Override
	public Pagination<Item> findPageItem(ItemDto item, Pagination<Item> pagination, String[] typeId2s) {
		try {
			String url = SpringContext.getBean(AppConfig.class).fileUrl;
			String imageBig = SpringContext.getBean(AppConfig.class).imagePicBig;
			String caituUrl = SpringContext.getBean(AppConfig.class).caituUrl;
			
			if(null==item || null==pagination){
				return pagination;
			}
			item.setStatus(Item.STATUS_NORMAL);
			Map<String,Object> map = new HashMap<String,Object>(3);
			map.put("item", item);
			map.put("typeId2s", typeId2s);
			map.put("start", pagination.getStart());
			map.put("pageSize", pagination.getPageSize());
			
			Integer count = itemMapper.selectPageCount(map);
			List<Item> list = itemMapper.selectPageList(map);
			
			for(Item i : list){
				String picUrl = i.getPicUrl();
				if(StringUtils.isNotBlank(picUrl)){
					i.setPicUrl(url + picUrl + imageBig);
				}
				String wapUrl = i.getWapUrl();
				if(2 != i.getSource().intValue()){ 
					i.setWapUrl(caituUrl + wapUrl);
				}
				if(Item.SOURCE_MOVE == i.getSource()){
					i.setSourceToString("中国移动");
					i.setMonetaryUnitToString("移动积分");
				}else if(Item.SOURCE_UNICON == i.getSource()){
					i.setSourceToString("中国联通");
					i.setMonetaryUnitToString("联通积分");
				}else if(Item.SOURCE_ZHONGXIN == i.getSource()){
					i.setSourceToString("中信银行");
					i.setMonetaryUnitToString("中信积分");
				}else if(Item.SOURCE_JS == i.getSource()){
					i.setSourceToString("建设银行");
					i.setMonetaryUnitToString("建行积分");
				}else if(Item.SOURCE_JT == i.getSource()){
					i.setSourceToString("交通银行");
					i.setMonetaryUnitToString("交行积分");
				}else if(Item.SOURCE_ZS == i.getSource()){
					i.setSourceToString("招商银行");
					i.setMonetaryUnitToString("招行积分");
				}else if(Item.SOURCE_TY == i.getSource()){
					i.setSourceToString("中国电信");
					i.setMonetaryUnitToString("电信积分");
				}else if(Item.SOURCE_PAB == i.getSource()){
					i.setSourceToString("平安积分");
					i.setMonetaryUnitToString("平安积分");
				}else if(Item.SOURCE_MY == i.getSource()){
					i.setSourceToString("财途超市");
					i.setMonetaryUnitToString("财币");
				}
						
				if(i.getIsFreeTrade().intValue() == 1){//特卖商品
					i.setSourceToString("");
				}
			}
			
			pagination.setDatas(list);
			pagination.setTotalRow(count);
			
			return pagination;
		} catch (Exception e) {
			logger.error("分页查询商品失败:" + e.getMessage(),e);
			return pagination;
		}
	}

	@Override
	public Item selectByPrimaryKey(Long itemId) {
		try {
			if(null == itemId){
				return null;
			}
			
			Item item = itemMapper.selectByPrimaryKey(itemId);
			
			return item;
		} catch (Exception e) {
			logger.error("根据主键查询商品失败:" + e.getMessage(),e);
			return null;
		}
	}

	@Override
	public Item findItemByskuId(Long skuId) {
		try {
			if(null == skuId){
				return null;
			}
			
			Item item = itemMapper.findItemByskuId(skuId);
			
			return item;
		} catch (Exception e) {
			logger.error("根据skuId查询商品失败:" + e.getMessage(),e);
			return null;
		}
	}

}
