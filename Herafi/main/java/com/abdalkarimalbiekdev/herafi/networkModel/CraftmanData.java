package com.abdalkarimalbiekdev.herafi.networkModel;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;

public class CraftmanData extends BaseObservable {


    @Nullable
    public String id;
    @Nullable
    public String name;
    @Nullable
    public String email;
    @Nullable
    public String phoneNumber;
    @Nullable
    public String imagePath;

    public CraftmanData() {
    }

    public CraftmanData(@Nullable String id, @Nullable String name, @Nullable String email, @Nullable String phoneNumber, @Nullable String imagePath) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.imagePath = imagePath;
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
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
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(@Nullable String imagePath) {
        this.imagePath = imagePath;
    }
}
