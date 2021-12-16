package com.abdalkarimalbiekdev.herafi.model;

import com.abdalkarimalbiekdev.herafi.Adapter.ProfileImageAdapter;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;

public class ProjectDetailsModel extends BaseObservable {

    private String userId;
    private String userName;
    private String userEmail;
    private String userImage;
    private String userPhoneNumber;
    private String projectBanner;
    private String projectName;
    private String projectComment;
    private String projectTotal_cost;
    private String projectTotal_rate;



    public ProjectDetailsModel() {
    }

    public ProjectDetailsModel(@Nullable String userId, @Nullable String userName, @Nullable String userEmail, @Nullable String userImage, @Nullable String userPhoneNumber, @Nullable String projectBanner, @Nullable String projectName, @Nullable String projectComment, @Nullable String projectTotal_cost, @Nullable String projectTotal_rate) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userImage = userImage;
        this.userPhoneNumber = userPhoneNumber;
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

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(@Nullable String userImage) {
        this.userImage = userImage;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(@Nullable String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
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

    public float getProjectTotal_rate() {
        return projectTotal_rate !=  null ? Float.parseFloat(String.valueOf(projectTotal_rate)) : 0;
    }

    public void setProjectTotal_rate(@Nullable String projectTotal_rate) {
        this.projectTotal_rate = projectTotal_rate;
    }

}
