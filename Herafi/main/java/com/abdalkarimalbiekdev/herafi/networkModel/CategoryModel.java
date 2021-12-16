package com.abdalkarimalbiekdev.herafi.networkModel;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;

public class CategoryModel extends BaseObservable {

    @Nullable
    @SerializedName("id")
    private String id;
    @Nullable
    @SerializedName("name")
    private String name;

    public CategoryModel() {
    }

    public CategoryModel(String id, String name) {
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
