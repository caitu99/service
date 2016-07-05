package com.caitu99.service.goods.controller.api;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.file.domain.AttachFile;
import com.caitu99.service.file.service.AttachFileService;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.domain.Sku;
import com.caitu99.service.goods.dto.ItemDto;
import com.caitu99.service.goods.service.GoodPropService;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.goods.service.SkuService;
import com.caitu99.service.goods.service.StockService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.utils.ApiResultCode;

@Controller
@RequestMapping("/api/goods/item/")
public class ItemController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(ItemController.class);
	
	private static final String[] ITEM_LIST_FILLTER = {"itemId","title", "picUrl","salePrice","wapUrl","marketPrice"};
	
	private static final String[] ITEM_DETAIL_FILLTER = {"itemId","title","itemNo","limitNum","saleVolume","content","wapUrl","picUrl","salePrice","source","salesType","marketPrice","isFreeTrade","discount","itemType"};
	
	private static final String[] ITEM_FILE_FILLTER = {"path","sort"};

	private static final String[] ITEM_SKU_FILLTER = {"salePrice","skuId"};

	@Autowired
	private ItemService itemService;
	
	@Autowired
	private SkuService skuService;
	
	@Autowired
	private AttachFileService attachFileService;
	
	@Autowired
	private StockService stockService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private GoodPropService goodPropService;
	
	/**
	 * 分页查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: list 
	 * @param item
	 * @param pagination
	 * @return
	 * @date 2015年11月24日 下午8:07:34  
	 * @author xiongbin
	 */
	@RequestMapping(value="list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String list(ItemDto item, Pagination<Item> pagination,String jsonpCallback) {
		pagination = itemService.findPageItem(item, pagination, null);
		ApiResult<Pagination<Item>> result = new ApiResult<Pagination<Item>>();
		return getJsonpCallback(result.toJSONString(0,"success",pagination,Item.class,ITEM_LIST_FILLTER),jsonpCallback);
	}
	
	/**
	 * 根据商品ID获取商品详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: goodsDetail 
	 * @param itemId
	 * @return
	 * @date 2015年11月25日 下午4:25:43  
	 * @author xiongbin
	 */
	@RequestMapping(value="goodsDetail/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String goodsDetail(Long itemId,String jsonpCallback) {
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		
		if(null == itemId){
			return getJsonpCallback(result.toJSONString(-1,"参数itemId不能为空"),jsonpCallback);
		}
		
		//查询商品
		Item item = itemService.selectByPrimaryKey(itemId);
		
		if(null == item){
			return getJsonpCallback(result.toJSONString(ApiResultCode.GOODS_ITEM_NOT_EXIST,"商品不存在"),jsonpCallback);
		}
				
		//查询商品图片
		List<AttachFile> fileList = attachFileService.finlPageListByItemId(itemId);

		JSONObject json = new JSONObject();
		json.put("item", item);
		json.put("fileList", fileList);
		
		SimplePropertyPreFilter itemFilter = new SimplePropertyPreFilter(Item.class,ITEM_DETAIL_FILLTER);
		SimplePropertyPreFilter attachFileFilter = new SimplePropertyPreFilter(AttachFile.class,ITEM_FILE_FILLTER);

		return getJsonpCallback(result.toJSONString(0,"success",json,new SimplePropertyPreFilter[]{itemFilter,attachFileFilter}),jsonpCallback);
	}
	
	/**
	 * 查询商品价格
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getGoodSalePrice 
	 * @param itemId		商品ID
	 * @return
	 * @date 2015年11月25日 下午3:24:20
	 * @author xiongbin
	 */
	@RequestMapping(value="goodsSalePrice/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String goodsSalePrice(Long itemId,String jsonpCallback) {
		ApiResult<List<Sku>> result = new ApiResult<List<Sku>>();
		
		if(null == itemId){
			return getJsonpCallback(result.toJSONString(-1,"参数itemId不能为空"),jsonpCallback);
		}
		
		List<Sku> list = skuService.findSkuByItemId(itemId);

		return getJsonpCallback(result.toJSONString(0,"success",list,Sku.class,ITEM_SKU_FILLTER),jsonpCallback);
	}
	
	/**
	 * 查询商品库存
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getGoodStock 
	 * @param itemId		商品ID
	 * @param skuId			skuId
	 * @return
	 * @date 2015年11月25日 下午5:00:45  
	 * @author xiongbin
	 */
	@RequestMapping(value="goodsStock/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String goodsStock(Long itemId,Long skuId,String jsonpCallback) {
		ApiResult<Integer> result = new ApiResult<Integer>();
		
		if(null == itemId){
			return getJsonpCallback(result.toJSONString(-1,"参数itemId不能为空"),jsonpCallback);
		}else if(null == skuId){
			return getJsonpCallback(result.toJSONString(-1,"参数skuId不能为空"),jsonpCallback);
		}
		
//		Integer count = stockService.selectCount(itemId, skuId);
		Sku sku = new Sku();
		sku.setItemId(itemId);
		sku.setSkuId(skuId);
		Integer count = orderService.queryInventory(sku);
		
		return getJsonpCallback(result.toJSONString(0,"success",count),jsonpCallback);
	}
	
	/**
	 * 查询商品信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getGoodsDetail 
	 * @param itemId				商品ID
	 * @param jsonpCallback
	 * @return
	 * @date 2015年11月26日 下午6:34:25  
	 * @author xiongbin
	 */
	@RequestMapping(value="goodsDetail/2.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getGoodsDetail(Long itemId,String jsonpCallback) {
		
		
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		
		if(null == itemId){
			return getJsonpCallback(result.toJSONString(-1,"参数itemId不能为空"),jsonpCallback);
		}
		
		JSONObject json = new JSONObject();
		
		//查询商品
		Item item = itemService.selectByPrimaryKey(itemId);
		
		if(null == item){
			return getJsonpCallback(result.toJSONString(-1,"商品不存在"),jsonpCallback);
		}
				
		//查询商品图片
		List<AttachFile> fileList = attachFileService.finlPageListByItemId(itemId);
		
		//查询商品SKU
		List<Sku> list = skuService.findSkuByItemId(itemId);
		
		if(null==list || list.size()<1){
			return getJsonpCallback(result.toJSONString(ApiResultCode.GOODS_ITEM_NOT_EXIST,"商品SKU不存在"),jsonpCallback);
		}
		Sku sku = list.get(0);
		json.put("sku", sku);
		
		if(Item.SOURCE_MOVE.equals(item.getSource())){
	/*		Map<String,List<GoodProp>> propList = goodPropService.findPropByItemId(itemId);
			json.put("prop", propList);*/
			json.put("out",1);
		}else if(Item.SOURCE_UNICON.equals(item.getSource())){
			json.put("out",2);
		}else if(Item.SOURCE_MY.equals(item.getSource()) || Item.SOURCE_OTHER.equals(item.getSource())){
			//查询商品库存
			Integer count = orderService.queryInventory(sku);
			json.put("count", count);
		}
		
		//反转义
		item.setContent(StringEscapeUtils.unescapeHtml(item.getContent()));
	
		json.put("item", item);
		json.put("fileList", fileList);
		
		SimplePropertyPreFilter itemFilter = new SimplePropertyPreFilter(Item.class,ITEM_DETAIL_FILLTER);
		SimplePropertyPreFilter attachFileFilter = new SimplePropertyPreFilter(AttachFile.class,ITEM_FILE_FILLTER);
		SimplePropertyPreFilter skuFilter = new SimplePropertyPreFilter(Sku.class,ITEM_SKU_FILLTER);
		
		return getJsonpCallback(result.toJSONString(0,"success",json,new SimplePropertyPreFilter[]{itemFilter,attachFileFilter,skuFilter}),jsonpCallback);
		
	}
	
	/**
	 * 判断是否返回JSONP
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: isJsonpCallback 
	 * @param result
	 * @param jsonpCallback
	 * @return
	 * @date 2015年11月30日 下午3:35:32  
	 * @author xiongbin
	 */
	private String getJsonpCallback(String result,String jsonpCallback){
		if(StringUtils.isBlank(jsonpCallback)){
			return result;
		}else{
			return jsonpCallback + "(" + result + ")";
		}
	}
	
}
