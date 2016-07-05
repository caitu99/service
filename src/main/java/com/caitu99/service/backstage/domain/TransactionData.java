package com.caitu99.service.backstage.domain;

/**
 * Created by 信 on 2016/2/17.
 * 商户商品交易数据统计
 */
public class TransactionData {

    private String platform;//平台

    private Long transNumber;//交易人数

    private Long transactions;//交易次数

    private Long transAmount;//交易金额

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Long getTransNumber() {
        return transNumber;
    }

    public void setTransNumber(Long transNumber) {
        this.transNumber = transNumber;
    }

    public Long getTransactions() {
        return transactions;
    }

    public void setTransactions(Long transactions) {
        this.transactions = transactions;
    }

    public Long getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(Long transAmount) {
        this.transAmount = transAmount;
    }
}
