package com.sapicons.deepak.k2psap.Objects;

import java.io.Serializable;

/**
 * Created by Deepak Prasad on 26-09-2018.
 */

public class UserItem implements Serializable {
    String name, email, tokenId, phoneNumber, picUrl;

    public UserItem(){}

    public UserItem(String name, String email, String tokenId, String phoneNumber, String picUrl){
        this.name = name;
        this.email = email;
        this.tokenId = tokenId;
        this.phoneNumber = phoneNumber;
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
