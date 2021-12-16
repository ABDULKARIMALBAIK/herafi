package com.abdalkarimalbiekdev.herafi.model;

import com.abdalkarimalbiekdev.herafi.Adapter.RequestAdapter;

import androidx.databinding.BaseObservable;

public class RequestModel extends BaseObservable {

    public RequestAdapter adapter;

    public RequestAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RequestAdapter adapter) {
        this.adapter = adapter;
    }
}
