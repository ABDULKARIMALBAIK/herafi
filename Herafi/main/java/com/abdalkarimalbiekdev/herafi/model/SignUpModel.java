package com.abdalkarimalbiekdev.herafi.model;

import android.net.Uri;

import com.abdalkarimalbiekdev.herafi.Adapter.CraftSelectedAdapter;
import com.abdalkarimalbiekdev.herafi.Adapter.ImageCioAdpater;
import com.abdalkarimalbiekdev.herafi.networkModel.CityModel;

import androidx.databinding.BaseObservable;

public class SignUpModel extends BaseObservable {

    private Uri imgCraftman;
    private String edtName = "";
    private String edtEmail = "";
    private String edtPassword = "";
    private String edtPhone = "";
    private String edtSecureCode = "";
    private String edtIdentityNumber = "";
    private String edtDescription = "";
    private CityModel spinnerCity;
    private int numSizePeople;
    public int numMinValueHand = 1000;
    public int numMaxValueHand = 100000;
    private String textMinValueHand = "1000";
    private String textMaxValueHand = "100000";
    private boolean checkedPrivacyConditions;
    private double lat;
    private double lng;
    public CraftSelectedAdapter adapter;
    public ImageCioAdpater imageCioAdpater;


    public SignUpModel() {
    }


    public SignUpModel(Uri imgCraftman, String edtName, String edtEmail, String edtPassword, String edtPhone, String edtSecureCode, String edtIdentityNumber, String edtDescription, CityModel spinnerCity, int numSizePeople, int numMinValueHand, int numMaxValueHand, String textMinValueHand, String textMaxValueHand, boolean checkedPrivacyConditions, double lat, double lng) {
        this.imgCraftman = imgCraftman;
        this.edtName = edtName;
        this.edtEmail = edtEmail;
        this.edtPassword = edtPassword;
        this.edtPhone = edtPhone;
        this.edtSecureCode = edtSecureCode;
        this.edtIdentityNumber = edtIdentityNumber;
        this.edtDescription = edtDescription;
        this.spinnerCity = spinnerCity;
        this.numSizePeople = numSizePeople;
        this.numMinValueHand = numMinValueHand;
        this.numMaxValueHand = numMaxValueHand;
        this.textMinValueHand = textMinValueHand;
        this.textMaxValueHand = textMaxValueHand;
        this.checkedPrivacyConditions = checkedPrivacyConditions;
        this.lat = lat;
        this.lng = lng;
        adapter = new CraftSelectedAdapter();
    }

    public ImageCioAdpater getImageCioAdpater() {
        return imageCioAdpater;
    }

    public void setImageCioAdpater(ImageCioAdpater imageCioAdpater) {
        this.imageCioAdpater = imageCioAdpater;
    }

    public CityModel getSpinnerCity() {
        return spinnerCity;
    }

    public void setSpinnerCity(CityModel spinnerCity) {
        this.spinnerCity = spinnerCity;
    }

    public CraftSelectedAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CraftSelectedAdapter adapter) {
        this.adapter = adapter;
    }

    public Uri getImgCraftman() {
        return imgCraftman;
    }

    public void setImgCraftman(Uri imgCraftman) {
        this.imgCraftman = imgCraftman;
    }

    public String getEdtName() {
        return edtName;
    }

    public void setEdtName(String edtName) {
        this.edtName = edtName;
    }

    public String getEdtEmail() {
        return edtEmail;
    }

    public void setEdtEmail(String edtEmail) {
        this.edtEmail = edtEmail;
    }

    public String getEdtPassword() {
        return edtPassword;
    }

    public void setEdtPassword(String edtPassword) {
        this.edtPassword = edtPassword;
    }

    public String getEdtPhone() {
        return edtPhone;
    }

    public void setEdtPhone(String edtPhone) {
        this.edtPhone = edtPhone;
    }

    public String getEdtSecureCode() {
        return edtSecureCode;
    }

    public void setEdtSecureCode(String edtSecureCode) {
        this.edtSecureCode = edtSecureCode;
    }

    public String getEdtIdentityNumber() {
        return edtIdentityNumber;
    }

    public void setEdtIdentityNumber(String edtIdentityNumber) {
        this.edtIdentityNumber = edtIdentityNumber;
    }

    public String getEdtDescription() {
        return edtDescription;
    }

    public void setEdtDescription(String edtDescription) {
        this.edtDescription = edtDescription;
    }

    public int getNumSizePeople() {
        return numSizePeople;
    }

    public void setNumSizePeople(int numSizePeople) {
        this.numSizePeople = numSizePeople;
    }

    public int getNumMinValueHand() {
        return numMinValueHand;
    }

    public void setNumMinValueHand(int numMinValueHand) {
        this.numMinValueHand = numMinValueHand;
    }

    public int getNumMaxValueHand() {
        return numMaxValueHand;
    }

    public void setNumMaxValueHand(int numMaxValueHand) {
        this.numMaxValueHand = numMaxValueHand;
    }

    public String getTextMinValueHand() {
        return textMinValueHand;
    }

    public void setTextMinValueHand(String textMinValueHand) {
        this.textMinValueHand = textMinValueHand;
    }

    public String getTextMaxValueHand() {
        return textMaxValueHand;
    }

    public void setTextMaxValueHand(String textMaxValueHand) {
        this.textMaxValueHand = textMaxValueHand;
    }

    public boolean isCheckedPrivacyConditions() {
        return checkedPrivacyConditions;
    }

    public void setCheckedPrivacyConditions(boolean checkedPrivacyConditions) {
        this.checkedPrivacyConditions = checkedPrivacyConditions;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
