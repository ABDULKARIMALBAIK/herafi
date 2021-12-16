package com.abdalkarimalbiekdev.herafi.model;

import androidx.databinding.BaseObservable;

public class CraftSelectedIModel extends BaseObservable {

    private int position;
    private int recyclerId;
    private CraftModel craftModel;

    public CraftSelectedIModel() {
    }

    public CraftSelectedIModel(int position, int recyclerId, CraftModel craftModel) {
        this.position = position;
        this.recyclerId = recyclerId;
        this.craftModel = craftModel;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getRecyclerId() {
        return recyclerId;
    }

    public void setRecyclerId(int recyclerId) {
        this.recyclerId = recyclerId;
    }

    public CraftModel getCraftModel() {
        return craftModel;
    }

    public void setCraftModel(CraftModel craftModel) {
        this.craftModel = craftModel;
    }
}
