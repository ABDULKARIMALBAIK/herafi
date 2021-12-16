package com.abdalkarimalbiekdev.herafi.model;

import androidx.databinding.BaseObservable;

public class SignInDialogVerifyModel extends BaseObservable {

    private String title;
    private String message;
    private String btnYes;
    private String btnNo;
    private int imgDialog;
    private String edtInfo;
    private String edtHintInfo;

    public SignInDialogVerifyModel() {
    }

    public SignInDialogVerifyModel(String title, String message, String btnYes, String btnNo, int imgDialog, String edtInfo, String edtHintInfo) {
        this.title = title;
        this.message = message;
        this.btnYes = btnYes;
        this.btnNo = btnNo;
        this.imgDialog = imgDialog;
        this.edtInfo = edtInfo;
        this.edtHintInfo = edtHintInfo;
    }

    public String getEdtHintInfo() {
        return edtHintInfo;
    }

    public void setEdtHintInfo(String edtHintInfo) {
        this.edtHintInfo = edtHintInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBtnYes() {
        return btnYes;
    }

    public void setBtnYes(String btnYes) {
        this.btnYes = btnYes;
    }

    public String getBtnNo() {
        return btnNo;
    }

    public void setBtnNo(String btnNo) {
        this.btnNo = btnNo;
    }

    public int getImgDialog() {
        return imgDialog;
    }

    public void setImgDialog(int imgDialog) {
        this.imgDialog = imgDialog;
    }

    public String getEdtInfo() {
        return edtInfo;
    }

    public void setEdtInfo(String edtInfo) {
        this.edtInfo = edtInfo;
    }
}
