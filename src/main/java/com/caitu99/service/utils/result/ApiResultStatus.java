package com.caitu99.service.utils.result;

public enum ApiResultStatus {
    UNKNOWN(-1, "unknown"),

    FAILED(0, "failed"),

    SUCCESS(1, "success");

    private int    value;

    private String code;

    private ApiResultStatus(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public int getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static ApiResultStatus valueFrom(String code) {
        for (ApiResultStatus status : ApiResultStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return ApiResultStatus.UNKNOWN;
    }

}
