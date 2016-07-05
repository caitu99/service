package com.caitu99.service.utils;

/**
 * Created by Lion on 2015/11/6 0006.
 */

public enum LoginType {
    MOBILE("0"),QQ("1"),WEBO("2"),WEICHAT("3");

    private String value;
    LoginType(String value)
    {
        this.value = value;
    }
    public String getValue(){return value;}
}
