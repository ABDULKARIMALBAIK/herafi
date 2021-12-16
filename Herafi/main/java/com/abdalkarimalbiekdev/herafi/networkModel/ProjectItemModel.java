package com.abdalkarimalbiekdev.herafi.networkModel;

import android.widget.ImageView;
import android.widget.RatingBar;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

public class ProjectItemModel extends BaseObservable {

    @Nullable
    @SerializedName("projectId")
    public String projectId;
    @Nullable
    @SerializedName("userId")
    public String userId;
    @Nullable
    @SerializedName("userPhoto")
    public String userPhoto;
    @Nullable
    @SerializedName("projectPhoto")
    public String projectPhoto;
    @Nullable
    @SerializedName("projectName")
    public String projectName;
    @Nullable
    @SerializedName("totalCost")
    public String totalCost;
    @Nullable
    @SerializedName("totalRate")
    public String totalRate;

    public ProjectItemModel() {
    }

    public ProjectItemModel(@Nullable String userId, @Nullable String userPhoto, @Nullable String projectPhoto, @Nullable String projectName, @Nullable String totalCost, @Nullable String totalRate) {
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.projectPhoto = projectPhoto;
        this.projectName = projectName;
        this.totalCost = totalCost;
        this.totalRate = totalRate;
    }

    @Nullable
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(@Nullable String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getProjectPhoto() {
        return projectPhoto;
    }

    public void setProjectPhoto(String projectPhoto) {
        this.projectPhoto = projectPhoto;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(String totalRate) {
        this.totalRate = totalRate;
    }

    @BindingAdapter("setImageProject")
    public static void setImageProject(ImageView view, String url){
//        Picasso.get().load(Common.BASE_PHOTO + "craftmen/" + url).into(view);
    }

    @BindingAdapter("setRating")
    public static void setRating(RatingBar view , String rating){
//        view.setRating(Float.parseFloat(rating));
    }

    @BindingAdapter("setImagePerson")
    public static void setImagePerson(ImageView view , String url){
//        Picasso.get().load(Common.BASE_PHOTO + "users/" + url).into(view);
    }
}
