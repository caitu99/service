package com.caitu99.service.backstage.domain;

/**
 * Created by chenhl on 2016/2/17.
 */
public class WeChatInfo {

    private Long ID;

    private String WeChat;

    private Long UID;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getWeChat() {
        return WeChat;
    }

    public void setWeChat(String weChat) {
        WeChat = weChat;
    }

    public Long getUID() {
        return UID;
    }

    public void setUID(Long UID) {
        this.UID = UID;
    }
}
