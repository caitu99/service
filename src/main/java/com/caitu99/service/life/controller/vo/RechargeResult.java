package com.caitu99.service.life.controller.vo;

/**
 * 充值结果
 * <p>
 * Modified By Lion
 */
public class RechargeResult {
    private boolean success;
    private String result;
    private String orderid;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public RechargeResult() {
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }


}
