package com.abdalkarimalbiekdev.herafi.model;

import android.widget.ImageView;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.bumptech.glide.Glide;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

public class RequestItemModel extends BaseObservable {


    @Nullable
    @SerializedName("requestId")
    private String requestId;
    @Nullable
    @SerializedName("userId")
    private String userId;
    @Nullable
    @SerializedName("userName")
    private String userName;
    @Nullable
    @SerializedName("userEmail")
    private String userEmail;
    @Nullable
    @SerializedName("userPhoneNumber")
    private String userPhoneNumber;
    @Nullable
    @SerializedName("userLat")
    private String userLat;
    @Nullable
    @SerializedName("userLng")
    private String userLng;
    @Nullable
    @SerializedName("userImage")
    private String userImage;
    @Nullable
    @SerializedName("statusId")
    private String statusId;
    @Nullable
    @SerializedName("statusName")
    private String statusName;

    public RequestItemModel() {
    }

    public RequestItemModel(@Nullable String requestId, @Nullable String userId, @Nullable String userName, @Nullable String userEmail, @Nullable String userPhoneNumber, @Nullable String userLat, @Nullable String userLng, @Nullable String userImage, @Nullable String statusId, @Nullable String statusName) {
        this.requestId = requestId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userLat = userLat;
        this.userLng = userLng;
        this.userImage = userImage;
        this.statusId = statusId;
        this.statusName = statusName;
    }

    @Nullable
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(@Nullable String requestId) {
        this.requestId = requestId;
    }

    @Nullable
    public String getUserId() {
        return userId;
    }

    public void setUserId(@Nullable String userId) {
        this.userId = userId;
    }

    @Nullable
    public String getUserName() {
        return userName;
    }

    public void setUserName(@Nullable String userName) {
        this.userName = userName;
    }

    @Nullable
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@Nullable String userEmail) {
        this.userEmail = userEmail;
    }

    @Nullable
    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(@Nullable String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    @Nullable
    public String getUserLat() {
        return userLat;
    }

    public void setUserLat(@Nullable String userLat) {
        this.userLat = userLat;
    }

    @Nullable
    public String getUserLng() {
        return userLng;
    }

    public void setUserLng(@Nullable String userLng) {
        this.userLng = userLng;
    }

    @Nullable
    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(@Nullable String userImage) {
        this.userImage = userImage;
    }

    @Nullable
    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(@Nullable String statusId) {
        this.statusId = statusId;
    }

    @Nullable
    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(@Nullable String statusName) {
        this.statusName = statusName;
    }

    @BindingAdapter("requestPersonImage")
    public static void requestPersonImage(ImageView view , String url){
        if (url != null){
            Glide.with(view.getContext()).load(Common.BASE_PHOTO + "users/" + url).into(view);
        }

    }

    @BindingAdapter("requestImgStatus")
    public static void requestImgStatus(ImageView view , String statusRequest){

        //Here if 1 then red or 2 then blue ....
        String decStatusRequest = null;
        try {
            decStatusRequest = statusRequest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (decStatusRequest != null){

            if (decStatusRequest.equals("1")){
                view.setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            }
            else if (decStatusRequest.equals("2")){
                view.setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_purple));
            }
            else if (decStatusRequest.equals("3")){
                view.setBackgroundColor(view.getContext().getResources().getColor(android.R.color.holo_green_light));
            }
        }
    }

}
