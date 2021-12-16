package com.abdalkarimalbiekdev.herafi.model;

import androidx.databinding.BaseObservable;

public class SignInModel extends BaseObservable {

    private String email = "";
    private String password = "";
    private boolean checked = false;

    public SignInModel() {
    }

    public SignInModel(String email, String password, boolean checked) {
        this.email = email;
        this.password = password;
        this.checked = checked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
