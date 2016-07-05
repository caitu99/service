package com.caitu99.service.goods.controller.api;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.domain.ShopList;
import com.caitu99.service.goods.dto.ItemDto;
import com.caitu99.service.goods.domain.Type;
import com.caitu99.service.goods.service.ClassificationGoodService;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.sys.domain.Config;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.utils.VersionUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chenhl on 2015/12/31.
 */
@Controller
@RequestMapping("/api/goods/classification")
public class ClassificationGoodController {

    private final static Logger logger = LoggerFactory.getLogger(ClassificationGoodController.class);

    private static final String[] LIST_FILLTER = {"name","useType","url","imgPath","funOnline","urlParams"};

    private static final String[] ITEM_LIST_FILLTER = {"monetaryUnitToString","itemId","title", "picUrl","salePrice","wapUrl","sourceToString","discount","isFreeTrade","marketPrice"};

    @Autowired
    private ClassificationGoodService classificationGoodService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConfigService configService;

    @RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String list(int useType){
        ApiResult<List<Type>> result = new ApiResult<>();

        List<Type> list = classificationGoodService.selectTypeByUseType(useType);

        return result.toJSONString(0,"success",list,Type.class,LIST_FILLTER);
    }

    @RequestMapping(value="/shop/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String classificationByShop(HttpServletRequest request){
    	
		String version = VersionUtil.getAppVersion(request);
		if(StringUtils.isBlank(version)){
			version = "2.1.2";
			logger.info("APP使用默认版本号为:" + version);
		}else{
			logger.info("APP版本号为:" + version);
		}
		Long versionL = VersionUtil.getVersionLong(version);
		
        //初始化
        ApiResult<List<ShopList>> result = new ApiResult();
        List<ShopList> shopLists;
        //获取所有上线的商城
        Config config = configService.selectByKey("item_source_shop_goods");
        String[] values = config.getValue().split(",");
        shopLists = new ArrayList<ShopList>(values.length + 1);
        for( String value : values ){
            int shopId = Integer.parseInt(value);
            ShopList shop = new ShopList();
            if( shopId == 3 ){
                shop.setShopId(3);
                shop.setShopName("移动积分");
            }else if( shopId == 4 ){
                shop.setShopId(4);
                shop.setShopName("联通积分");
            }else if( shopId == 5 ){
                shop.setShopId(5);
                shop.setShopName("中信积分");
            }else if( shopId == 7 ){
                shop.setShopId(7);
                shop.setShopName("建行积分");
            }else if( shopId == 9 ){
                shop.setShopId(9);
                shop.setShopName("交行积分");
            }else if( shopId == 13 ){
                shop.setShopId(13);
                shop.setShopName("电信积分");
            }else if(shopId == 16 ){
                shop.setShopId(16);
                shop.setShopName("平安积分");
            }else{
                logger.info("有新上线商城未加入到花积分按商城列表，商城代号{}",shopId);
                continue;
            }
            shopLists.add(shop);
        }
//        Long version3 = 3000000L;
//        if(version3.compareTo(versionL) > 0){
//            ShopList shop = new ShopList();
//            shop.setShopId(1);
//            shop.setShopName("财币商城");
//            shopLists.add(3,shop);
//        }
        return result.toJSONString(0,"success",shopLists);
    }


    @RequestMapping(value="type/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String typeList(Pagination<Item> pagination, int from ,Long typeId, String source, String typeId2){
        //typeId2 为筛选id 话费  短信包 流量包  通话时间  爱奇艺
        //source  筛选时选中的 中国移动 中国联通 中信银行
        //from:1商品分类 typeId=1 美食，typeId=2 购物，typeId=3 娱乐，typeId=4 生活服务
        //from:2财途超市，
        //from:3所有商户商品，
        //from:4特定商户的商品 typeId=3 移动商城，typeId=4 联通商城，typeId=5 招商商城， typeId=6 中信银行商城
    	//from:5特卖商品
        ApiResult<Pagination<Item>> result = new ApiResult();
        String[] sources;
        if(StringUtils.isNotBlank(source)){
            sources = source.split(",");
        }else {
            sources=null;
        }
        String[] typeId2s;
        if(StringUtils.isNotBlank(typeId2)){
            typeId2s = typeId2.split(",");
        }else {
            typeId2s=null;
        }
        if( from == 1 ){
            if( typeId == null )
                return result.toJSONString(-1,"typeId不能为空");
            pagination = classificationGoodService.findByTypeId(typeId, pagination, typeId2s, sources );
        }else if( from == 2 ){
            ItemDto item = new ItemDto();
            item.setSource(1);  //自有商品
            item.setIsFreeTrade(0);//20160125 add by chencheng 0为非特卖商品
            pagination = itemService.findPageItem(item, pagination, typeId2s);
        }else if( from == 3){
            ItemDto itemdto = new ItemDto();
            List<Integer> list = new ArrayList<>();

            Config config = configService.selectByKey("item_source_shop_goods");
            String[] values = config.getValue().split(",");
            for( String value : values ){
                try {
                    list.add(Integer.parseInt(value));
                }catch(Exception e){
                    return result.toJSONString(0,"success",pagination,Item.class,ITEM_LIST_FILLTER);
                }
            }
            if( list.size() == 0){
                return result.toJSONString(0,"success",pagination,Item.class,ITEM_LIST_FILLTER);
            }
            itemdto.setSourceShops(sources);
            itemdto.setSourceShop(list);
            itemdto.setIsFreeTrade(0);//20160125 add by chencheng 0为非特卖商品
            pagination = itemService.findPageItem(itemdto, pagination,  typeId2s);
        }else if( from == 4){
        	ItemDto item = new ItemDto();
            item.setSource(new Long(typeId).intValue());
            item.setIsFreeTrade(0);//20160125 add by chencheng 0为非特卖商品
            pagination = itemService.findPageItem(item, pagination, typeId2s);
        }else if(from == 5){//20160125 add by chencheng 增加特卖商品类型
        	ItemDto itemdto = new ItemDto();
            itemdto.setIsFreeTrade(1);
            
            pagination = itemService.findPageItem(itemdto, pagination, null);
        }

        return result.toJSONString(0,"success",pagination,Item.class,ITEM_LIST_FILLTER);
    }
}
