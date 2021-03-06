package com.bxtore.dev.bxt.Objects;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Deepak Prasad on 28-09-2018.
 */

public class PostItem implements Serializable {

    String postId, emailId, phoneNumber, title, description, price;
    int category;
    String imgUrlOne, imgUrlTwo, imgUrlThree, imgUrlFour, imgUrlFive;
    String postUserPicUrl, postUserName;
    String status;
    String categoryName;
    String exchangeFor, exchangeType;
    boolean verified;

    float latitude, longitude;

    public PostItem(){ }


    public PostItem(String postId, String emailId, String title, String description,
                    int category, String categoryName, String postUserName, String postUserPicUrl){

        this.postId = postId;
        this.emailId = emailId;
        this.title = title;
        this.description= description;
        this.price = price;
        this.category = category;
        this.categoryName = categoryName;
        this.postUserName = postUserName;
        this.postUserPicUrl = postUserPicUrl;

        this.phoneNumber= "";
        this.imgUrlFive = "";
        this.imgUrlFour="";
        this.imgUrlOne="";
        this.imgUrlTwo= "";
        this.imgUrlThree="";
        this.status ="open";

        this.latitude = 0.0f;
        this.longitude =0.0f;

        this.exchangeFor = "";
        this.exchangeType="";

        this.verified = false;

    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getImgUrlOne() {
        return imgUrlOne;
    }

    public void setImgUrlOne(String imgUrlOne) {
        this.imgUrlOne = imgUrlOne;
    }

    public String getImgUrlTwo() {
        return imgUrlTwo;
    }

    public void setImgUrlTwo(String imgUrlTwo) {
        this.imgUrlTwo = imgUrlTwo;
    }

    public String getImgUrlThree() {
        return imgUrlThree;
    }

    public void setImgUrlThree(String imgUrlThree) {
        this.imgUrlThree = imgUrlThree;
    }

    public String getImgUrlFour() {
        return imgUrlFour;
    }

    public void setImgUrlFour(String imgUrlFour) {
        this.imgUrlFour = imgUrlFour;
    }

    public String getImgUrlFive() {
        return imgUrlFive;
    }

    public void setImgUrlFive(String imgUrlFive) {
        this.imgUrlFive = imgUrlFive;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPostUserPicUrl() {
        return postUserPicUrl;
    }

    public void setPostUserPicUrl(String postUserPicUrl) {
        this.postUserPicUrl = postUserPicUrl;
    }

    public String getPostUserName() {
        return postUserName;
    }

    public void setPostUserName(String postUserName) {
        this.postUserName = postUserName;
    }

    public String getExchangeFor() {
        return exchangeFor;
    }

    public void setExchangeFor(String exchangeFor) {
        this.exchangeFor = exchangeFor;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public static Comparator<PostItem> PostTimeComparator = new Comparator<PostItem>() {
        @Override
        public int compare(PostItem postItem1, PostItem postItem2) {

            long postTime1 = Long.parseLong(postItem1.getPostId());
            long postTime2 = Long.parseLong(postItem2.getPostId());

            // descending order
            return (int)(postTime2 - postTime1);

        }
    };
}
