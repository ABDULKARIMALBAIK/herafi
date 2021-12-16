package com.abdalkarimalbiekdev.herafi.networkModel;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;

public class CityModel extends BaseObservable {

    @SerializedName("id")
    @Nullable
    public String id;
    @SerializedName("name")
    @Nullable
    public String name;

    public CityModel() {
    }

    public CityModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
