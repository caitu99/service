package com.caitu99.service.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.goods.dao.ClassificationGoodMapper;
import com.caitu99.service.goods.dao.TypeMapper;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.domain.Type;
import com.caitu99.service.goods.service.ClassificationGoodService;
import com.caitu99.service.utils.SpringContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenhl on 2015/12/31.
 */
@Service
public class ClassificationGoodServiceImpl implements ClassificationGoodService{

    private final static Logger logger = LoggerFactory.getLogger(ClassificationGoodServiceImpl.class);

    @Autowired
    private RedisOperate redis;

    @Autowired
    private TypeMapper typeMapper;

    @Autowired
    private ClassificationGoodMapper classificationGoodMapper;

    @Autowired
    private AppConfig appConfig;

    @Override
    public List<Type> selectTypeByUseType(int useType) {

        String content = redis.getStringByKey(RedisKey.GOODS_CLASSIFICATION_GOODS_LIST_KEY);

        // get from redis
        if (!StringUtils.isEmpty(content)) {
            return JSON.parseArray(content,Type.class);
        }

        // get from db
        try{
            List<Type> list = typeMapper.selectByUseType(useType);
            if (list != null && !list.isEmpty()) {
                for(Type i : list){
                    String url = i.getUrl();
                    String img_path = i.getImgPath();
                    if(StringUtils.isNotBlank(url)){
                        i.setUrl(appConfig.caituUrl + url);
                    }
                    if(StringUtils.isNotBlank(img_path)){
                        i.setImgPath(appConfig.staticUrl + img_path);
                    }
                    i.setUrlParams(new HashMap<String,String>());
                    getParamsFromUrl(i.getUrl(), i.getUrlParams());
                }
                redis.set(RedisKey.GOODS_CLASSIFICATION_GOODS_LIST_KEY, JSON.toJSONString(list),86400);
            }
            return list;
        } catch (Exception e) {
            logger.error("根据useType查询首页商品分类失败:" + e.getMessage(),e);
            return null;
        }
    }

    @Override
    public Pagination<Item> findByTypeId(Long typeId, Pagination<Item> pagination, String[] typeId2, String[] source) {
        //初始化
        Map<String,Object> map = new HashMap<String,Object>(5);
        String url = appConfig.fileUrl;
        String imageBig = appConfig.imagePicBig;
        String caituUrl = SpringContext.getBean(AppConfig.class).caituUrl;
        map.put("typeId", typeId);
        map.put("typeId2", typeId2);
        map.put("source", source);
        map.put("start", pagination.getStart());
        map.put("pageSize", pagination.getPageSize());

        List<Item> list = classificationGoodMapper.selectPageList(map);
        Integer count = classificationGoodMapper.selectPageCount(map);

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
            }
            if(Item.SOURCE_UNICON == i.getSource()){
                i.setSourceToString("中国联通");
            }
            if(Item.SOURCE_ZHONGXIN == i.getSource()){
                i.setSourceToString("中信银行");
            }
            if(Item.SOURCE_JS == i.getSource()){
                i.setSourceToString("建设银行");
            }
            if(Item.SOURCE_MY == i.getSource()){
                i.setSourceToString("财途超市");
            }
            if(Item.SOURCE_JT == i.getSource()){
                i.setSourceToString("交通银行");
            }
            if(Item.SOURCE_TY == i.getSource()){
                i.setSourceToString("中国电信");
            }
            if(Item.SOURCE_PAB == i.getSource()){
                i.setSourceToString("平安积分");
            }
        }

        pagination.setDatas(list);
        pagination.setTotalRow(count);

        return pagination;
    }

    public void getParamsFromUrl(String url,Map<String,String> params){
        if(url == null)
            return;

        String[] urlParams = url.split("\\?");
        if(urlParams.length == 1 || urlParams.length == 0)
            return;
        String[] preParam = urlParams[urlParams.length - 1].split("&");
        for(String st : preParam){
            String[] sts = st.split("=");
            if( sts.length != 2 ){
                logger.error("解析url : {} 出错",url);
                continue;
            }else{
                params.put(sts[0],sts[1]);
            }

        }
    }

}
