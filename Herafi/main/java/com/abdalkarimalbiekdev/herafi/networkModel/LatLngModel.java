package com.abdalkarimalbiekdev.herafi.networkModel;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class LatLngModel {

    @Nullable
    @SerializedName("lat")
    private String lat;
    @Nullable
    @SerializedName("lng")
    private String lng;


    public LatLngModel() {
    }

    public LatLngModel(@Nullable String lat, @Nullable String lng) {
        this.lat = lat;
        this.lng = lng;
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


}
