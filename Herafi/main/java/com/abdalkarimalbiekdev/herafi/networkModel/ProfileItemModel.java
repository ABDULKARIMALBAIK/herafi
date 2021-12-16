package com.abdalkarimalbiekdev.herafi.networkModel;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;

public class ProfileItemModel extends BaseObservable {

    @Nullable
    @SerializedName("photo")
    public String photo;
    @Nullable
    @SerializedName("name")
    public String name;
    @Nullable
    @SerializedName("description")
    public String description;
    @Nullable
    @SerializedName("email")
    public static String email;
    @Nullable
    @SerializedName("phoneNum")
    public String phoneNum;
    @Nullable
    @SerializedName("dateJoin")
    public String dateJoin;
    @Nullable
    @SerializedName("identityNum")
    public String identityNum;
    @Nullable
    @SerializedName("lat")
    public String lat;
    @Nullable
    @SerializedName("lng")
    public String lng;
    @Nullable
    @SerializedName("cityName")
    public String cityName;
    @Nullable
    @SerializedName("levelCraftman")
    public String levelCraftman;
    @Nullable
    @SerializedName("statusCraftman")
    public String statusCraftman;
    @Nullable
    @SerializedName("lowestCost")
    public String lowestCost;
    @Nullable
    @SerializedName("highestCost")
    public String highestCost;
    @Nullable
    @SerializedName("sizeQueue")
    public String sizeQueue;

    public ProfileItemModel() {
    }

    public ProfileItemModel(@Nullable String photo, @Nullable String name, @Nullable String description, @Nullable String email, @Nullable String phoneNum, @Nullable String dateJoin, @Nullable String identityNum, @Nullable String lat, @Nullable String lng, @Nullable String cityName, @Nullable String levelCraftman, @Nullable String statusCraftman, @Nullable String lowestCost, @Nullable String highestCost, @Nullable String sizeQueue) {
        this.photo = photo;
        this.name = name;
        this.description = description;
        this.email = email;
        this.phoneNum = phoneNum;
        this.dateJoin = dateJoin;
        this.identityNum = identityNum;
        this.lat = lat;
        this.lng = lng;
        this.cityName = cityName;
        this.levelCraftman = levelCraftman;
        this.statusCraftman = statusCraftman;
        this.lowestCost = lowestCost;
        this.highestCost = highestCost;
        this.sizeQueue = sizeQueue;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(@Nullable String photo) {
        this.photo = photo;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(@Nullable String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Nullable
    public String getDateJoin() {
        return dateJoin;
    }

    public void setDateJoin(@Nullable String dateJoin) {
        this.dateJoin = dateJoin;
    }

    @Nullable
    public String getIdentityNum() {
        return identityNum;
    }

    public void setIdentityNum(@Nullable String identityNum) {
        this.identityNum = identityNum;
    }

    @Nullable
    public String getLat() {
        return lat;
    }

    public void setLat(@Nullable String lat) {
        this.lat = lat;
    }

    @Nullable
    public String getLng() {
        return lng;
    }

    public void setLng(@Nullable String lng) {
        this.lng = lng;
    }

    @Nullable
    public String getCityName() {
        return cityName;
    }

    public void setCityName(@Nullable String cityName) {
        this.cityName = cityName;
    }

    @Nullable
    public String getLevelCraftman() {
        return levelCraftman;
    }

    public void setLevelCraftman(@Nullable String levelCraftman) {
        this.levelCraftman = levelCraftman;
    }

    @Nullable
    public String getStatusCraftman() {
        return statusCraftman;
    }

    public void setStatusCraftman(@Nullable String statusCraftman) {
        this.statusCraftman = statusCraftman;
    }

    @Nullable
    public String getLowestCost() {
        return lowestCost;
    }

    public void setLowestCost(@Nullable String lowestCost) {
        this.lowestCost = lowestCost;
    }

    @Nullable
    public String getHighestCost() {
        return highestCost;
    }

    public void setHighestCost(@Nullable String highestCost) {
        this.highestCost = highestCost;
    }

    @Nullable
    public String getSizeQueue() {
        return sizeQueue;
    }

    public void setSizeQueue(@Nullable String sizeQueue) {
        this.sizeQueue = sizeQueue;
    }
}
