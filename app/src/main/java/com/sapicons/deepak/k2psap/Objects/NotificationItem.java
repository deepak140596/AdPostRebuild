package com.sapicons.deepak.k2psap.Objects;

/**
 * Created by Deepak Prasad on 31-10-2018.
 */

public class NotificationItem {
    String toTokenId, message, fromUserName;
    public NotificationItem(){}
    public  NotificationItem(String toTokenId,String message, String fromUserName){
        this.toTokenId = toTokenId;
        this.message = message;
        this.fromUserName = fromUserName;
    }

    public String getToTokenId() {
        return toTokenId;
    }

    public void setToTokenId(String toTokenId) {
        this.toTokenId = toTokenId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }
}
