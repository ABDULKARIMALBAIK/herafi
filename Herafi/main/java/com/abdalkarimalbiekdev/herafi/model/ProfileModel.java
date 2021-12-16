package com.abdalkarimalbiekdev.herafi.model;

import com.abdalkarimalbiekdev.herafi.Adapter.CraftAdapter;
import com.abdalkarimalbiekdev.herafi.Adapter.ProfileImageAdapter;
import com.abdalkarimalbiekdev.herafi.networkModel.ProfileItemModel;

import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

public class ProfileModel extends BaseObservable {

//    private String name;
//    private String email;
//    private String imgPerson;
//    private String phoneNumber;
//    private String dateJoin;
//    private String IdNumber;
//    private String lat;
//    private String lng;
//    private String city;
//    private String level;
//    private String lowCost;
//    private String highCost;
//    private String numberPepole;
//    private String description;
//    private String status;


    private MutableLiveData<ProfileItemModel> profileItemModelLiveData;
    private MutableLiveData<List<String>> images;
    private CraftAdapter craftAdapter;
    private ProfileImageAdapter imageAdapter;

    public MutableLiveData<ProfileItemModel> getProfileItemModelLiveData() {
        return profileItemModelLiveData;
    }

    public void setProfileItemModelLiveData(MutableLiveData<ProfileItemModel> profileItemModelLiveData) {
        this.profileItemModelLiveData = profileItemModelLiveData;
    }

    public MutableLiveData<List<String>> getImages() {
        return images;
    }

    public void setImages(MutableLiveData<List<String>> images) {
        this.images = images;
    }

    public CraftAdapter getCraftAdapter() {
        return craftAdapter;
    }

    public void setCraftAdapter(CraftAdapter craftAdapter) {
        this.craftAdapter = craftAdapter;
    }

    public ProfileImageAdapter getImageAdapter() {
        return imageAdapter;
    }

    public void setImageAdapter(ProfileImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
    }
}
