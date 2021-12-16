package com.abdalkarimalbiekdev.herafi.networkModel;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;

public class ProjectDetailsItemModel {

    public String userId;
    public String userName;
    public String userEmail;
    public String userPhoneNumber;
    public String userImage;
    public String projectBanner;
    public String projectName;
    public String projectComment;
    public String projectTotal_cost;
    public String projectTotal_rate;

    public ProjectDetailsItemModel() {
    }

    public ProjectDetailsItemModel(@Nullable String userId, @Nullable String userName, @Nullable String userEmail, @Nullable String userPhoneNumber, @Nullable String userImage, @Nullable String projectBanner, @Nullable String projectName, @Nullable String projectComment, @Nullable String projectTotal_cost, @Nullable String projectTotal_rate) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userImage = userImage;
        this.projectBanner = projectBanner;
        this.projectName = projectName;
        this.projectComment = projectComment;
        this.projectTotal_cost = projectTotal_cost;
        this.projectTotal_rate = projectTotal_rate;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(@Nullable String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(@Nullable String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@Nullable String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(@Nullable String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(@Nullable String userImage) {
        this.userImage = userImage;
    }

    public String getProjectBanner() {
        return projectBanner;
    }

    public void setProjectBanner(@Nullable String projectBanner) {
        this.projectBanner = projectBanner;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(@Nullable String projectName) {
        this.projectName = projectName;
    }

    public String getProjectComment() {
        return projectComment;
    }

    public void setProjectComment(@Nullable String projectComment) {
        this.projectComment = projectComment;
    }

    public String getProjectTotal_cost() {
        return projectTotal_cost;
    }

    public void setProjectTotal_cost(@Nullable String projectTotal_cost) {
        this.projectTotal_cost = projectTotal_cost;
    }

    public String getProjectTotal_rate() {
        return projectTotal_rate;
    }

    public void setProjectTotal_rate(@Nullable String projectTotal_rate) {
        this.projectTotal_rate = projectTotal_rate;
    }
}
