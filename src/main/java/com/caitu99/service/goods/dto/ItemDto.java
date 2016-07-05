package com.caitu99.service.goods.dto;

import com.caitu99.service.goods.domain.Item;

import java.util.List;

/**
 * Created by chenhl on 2016/1/20.
 */
public class ItemDto extends Item {

    //商户商品
    private List<Integer> sourceShop;

    //筛选商品时选择的商户商品
    private String[] sourceShops;

    //根据itemNo筛选
    private String itemNoFillter;

    public List<Integer> getSourceShop() {
        return sourceShop;
    }

    public void setSourceShop(List<Integer> sourceShop) {
        this.sourceShop = sourceShop;
    }

    public String[] getSourceShops() {
        return sourceShops;
    }

    public void setSourceShops(String[] sourceShops) {
        this.sourceShops = sourceShops;
    }

    public String getItemNoFillter() {
        return itemNoFillter;
    }

    public void setItemNoFillter(String itemNoFillter) {
        this.itemNoFillter = itemNoFillter;
    }

}