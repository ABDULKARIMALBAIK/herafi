package com.abdalkarimalbiekdev.herafi.model;

import androidx.databinding.BaseObservable;

public class SignInDialogForgetPasswordModel extends BaseObservable {

    private String title = "";
    private String message = "";
    private String btnYes = "";
    private String btnNo = "";
    private int imgDialog;
    private String edtEmail = "";
    private String edtHintEmail = "";
    private String edtPhoneNumber = "";
    private String edtHintPhoneNumber = "";
    private String edtScureCode = "";
    private String edtHintScureCode = "";

    public SignInDialogForgetPasswordModel() {
    }

    public SignInDialogForgetPasswordModel(String title, String message, String btnYes, String btnNo, int imgDialog, String edtEmail, String edtHintEmail, String edtPhoneNumber, String edtHintPhoneNumber, String edtScureCode, String edtHintScureCode) {
        this.title = title;
        this.message = message;
        this.btnYes = btnYes;
        this.btnNo = btnNo;
        this.imgDialog = imgDialog;
        this.edtEmail = edtEmail;
        this.edtHintEmail = edtHintEmail;
        this.edtPhoneNumber = edtPhoneNumber;
        this.edtHintPhoneNumber = edtHintPhoneNumber;
        this.edtScureCode = edtScureCode;
        this.edtHintScureCode = edtHintScureCode;
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

    public String getEdtEmail() {
        return edtEmail;
    }

    public void setEdtEmail(String edtEmail) {
        this.edtEmail = edtEmail;
    }

    public String getEdtHintEmail() {
        return edtHintEmail;
    }

    public void setEdtHintEmail(String edtHintEmail) {
        this.edtHintEmail = edtHintEmail;
    }

    public String getEdtPhoneNumber() {
        return edtPhoneNumber;
    }

    public void setEdtPhoneNumber(String edtPhoneNumber) {
        this.edtPhoneNumber = edtPhoneNumber;
    }

    public String getEdtHintPhoneNumber() {
        return edtHintPhoneNumber;
    }

    public void setEdtHintPhoneNumber(String edtHintPhoneNumber) {
        this.edtHintPhoneNumber = edtHintPhoneNumber;
    }

    public String getEdtScureCode() {
        return edtScureCode;
    }

    public void setEdtScureCode(String edtScureCode) {
        this.edtScureCode = edtScureCode;
    }

    public String getEdtHintScureCode() {
        return edtHintScureCode;
    }

    public void setEdtHintScureCode(String edtHintScureCode) {
        this.edtHintScureCode = edtHintScureCode;
    }
}
