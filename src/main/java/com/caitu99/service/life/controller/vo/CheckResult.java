package com.caitu99.service.life.controller.vo;

public class CheckResult {
    private boolean success;
    private String result;

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

    public CheckResult() {
        super();
    }

    public CheckResult(boolean success, String result) {
        super();
        this.success = success;
        this.result = result;
    }
}
