package com.abdalkarimalbiekdev.herafi.model;

import com.abdalkarimalbiekdev.herafi.Adapter.ProjectAdapter;

import androidx.databinding.BaseObservable;

public class ProjectModel extends BaseObservable {

    private ProjectAdapter adapter;

    public ProjectAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ProjectAdapter adapter) {
        this.adapter = adapter;
    }
}
