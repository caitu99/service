package com.caitu99.service.utils;

/**
 * Created by Lion on 2015/11/6 0006.
 */
public class LoginType2EnumUtils {
    public static LoginType getLoginTypeByStr(String str) {
        if (str == null)
            return null;
        LoginType loginTypes[] = LoginType.values();
        for (LoginType loginType : loginTypes) {
            if (loginType.getValue().equals(str)) {
                return loginType;
            }
        }
        return null;
    }
}
