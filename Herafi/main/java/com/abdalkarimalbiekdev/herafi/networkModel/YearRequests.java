package com.abdalkarimalbiekdev.herafi.networkModel;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class YearRequests {

    @Nullable
    @SerializedName("month")
    public String month;
    @Nullable
    @SerializedName("data")
    public String data;

    public YearRequests() {
    }

    public YearRequests(String month, String data) {
        this.month = month;
        this.data = data;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
