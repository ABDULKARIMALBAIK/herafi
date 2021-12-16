package com.abdalkarimalbiekdev.herafi.model;

import com.abdalkarimalbiekdev.herafi.Adapter.CraftCategoryAdapter;

import androidx.databinding.BaseObservable;

public class CategoryCraftFragmentModel extends BaseObservable {

    private CraftCategoryAdapter adapter;

    public CategoryCraftFragmentModel() {
    }

    public CategoryCraftFragmentModel(CraftCategoryAdapter adapter) {
        this.adapter = adapter;
    }

    public CraftCategoryAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CraftCategoryAdapter adapter) {
        this.adapter = adapter;
    }
}
