package com.abdalkarimalbiekdev.herafi.networkModel;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class ResponseModel<T> {

    @SerializedName("result")
    @Nullable
    public T result;

    @SerializedName("token")
    @Nullable
    public String token;

    public ResponseModel(@Nullable T result, @Nullable String token) {
        this.result = result;
        this.token = token;
    }

    @Nullable
    public T getResult() {
        return result;
    }

    public void setResult(@Nullable T result) {
        this.result = result;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    public void setToken(@Nullable String token) {
        this.token = token;
    }
}
