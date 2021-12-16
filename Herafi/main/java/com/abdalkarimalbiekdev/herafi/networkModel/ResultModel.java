package com.abdalkarimalbiekdev.herafi.networkModel;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;


public class ResultModel<T> {

    @Nullable
    @SerializedName("response")
    public ResponseModel<T> response;

    @Nullable
    @SerializedName("errorMessage")
    public String errorMessage;

    public ResultModel(@Nullable ResponseModel<T> response, @Nullable String errorMessage) {
        this.response = response;
        this.errorMessage = errorMessage;
    }

    @Nullable
    public ResponseModel<T> getResponse() {
        return response;
    }

    public void setResponse(@Nullable ResponseModel<T> response) {
        this.response = response;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
