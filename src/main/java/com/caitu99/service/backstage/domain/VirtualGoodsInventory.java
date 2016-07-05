package com.caitu99.service.backstage.domain;

/**
 * Created by liuzs on 2016/2/16.
 */
public class VirtualGoodsInventory {

    private Long goodsID;//商品ID

    private String goodsName;//商品名称

    private int initialInventory;//初始库存

    private int frozenInventory;//冻结库存

    private int actualInventory;//实际库存

    private int exchangeQuantity;//兑换数量

    private int replenishmentQuantity;//补货数量
    
    private int  overdue;//过期商品

    public int getOverdue() {
		return overdue;
	}

	public void setOverdue(int overdue) {
		this.overdue = overdue;
	}

	public Long getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(Long goodsID) {
        this.goodsID = goodsID;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getInitialInventory() {
        return initialInventory;
    }

    public void setInitialInventory(int initialInventory) {
        this.initialInventory = initialInventory;
    }

    public int getFrozenInventory() {
        return frozenInventory;
    }

    public void setFrozenInventory(int frozenInventory) {
        this.frozenInventory = frozenInventory;
    }

    public int getActualInventory() {
        return actualInventory;
    }

    public void setActualInventory(int actualInventory) {
        this.actualInventory = actualInventory;
    }

    public int getExchangeQuantity() {
        return exchangeQuantity;
    }

    public void setExchangeQuantity(int exchangeQuantity) {
        this.exchangeQuantity = exchangeQuantity;
    }

    public int getReplenishmentQuantity() {
        return replenishmentQuantity;
    }

    public void setReplenishmentQuantity(int replenishmentQuantity) {
        this.replenishmentQuantity = replenishmentQuantity;
    }
}
