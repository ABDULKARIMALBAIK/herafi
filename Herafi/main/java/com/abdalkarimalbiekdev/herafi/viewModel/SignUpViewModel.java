package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignUpListener;
import com.abdalkarimalbiekdev.herafi.model.SignUpModel;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.jem.rubberpicker.RubberRangePicker;
import com.sanojpunchihewa.glowbutton.GlowButton;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpViewModel extends ViewModel {

    private static MutableLiveData<SignUpModel> signUpLiveData;

    private static Activity activity;
    private static SignUpListener listener;

    public SignUpViewModel() {
    }

    public void setActivity(Activity activity , SignUpModel model) {
        this.activity = activity;

        signUpLiveData = new MutableLiveData<>();
        signUpLiveData.setValue(model);

    }

    public void setListener(SignUpListener listener) {
        this.listener = listener;
    }

    public static MutableLiveData<SignUpModel> getSignUpLiveData() {
        return signUpLiveData;
    }

    public SignUpModel getSignUpModel() {
        return signUpLiveData.getValue();
    }

    public static TextWatcher getEmailChanged(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSignUpLiveData().observe((LifecycleOwner) activity, model -> {
                    model.setEdtEmail(s.toString());
                });
            }
        };
    }
    public static TextWatcher getPasswordChanged(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSignUpLiveData().observe((LifecycleOwner) activity, model -> {
                    model.setEdtPassword(s.toString());
                });
            }
        };
    }
    public static TextWatcher getNameChanged(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSignUpLiveData().observe((LifecycleOwner) activity, model -> {
                    model.setEdtName(s.toString());
                });
            }
        };
    }
    public static TextWatcher getPhoneChanged(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSignUpLiveData().observe((LifecycleOwner) activity, model -> {
                    model.setEdtPhone(s.toString());
                });
            }
        };
    }
    public static TextWatcher getSecureChanged(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSignUpLiveData().observe((LifecycleOwner) activity, model -> {
                    model.setEdtSecureCode(s.toString());
                });
            }
        };
    }
    public static TextWatcher getIdentityChanged(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSignUpLiveData().observe((LifecycleOwner) activity, model -> {
                    model.setEdtIdentityNumber(s.toString());
                });
            }
        };
    }
    public static TextWatcher getDescriptionChanged(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSignUpLiveData().observe((LifecycleOwner) activity, model -> {
                    model.setEdtDescription(s.toString());
                });
            }
        };
    }


    public void onClickBtnPhoto(View view){
        listener.onClickBtnPhoto(view);
    }
    public void onClickBtnExp(View view){
        listener.onClickBtnExp(view);
    }
    public void onClickBtnUploadCerticate(View view){ listener.onClickBanUploadCertificate(view);}
    public void onClickBtnUploadOther(View view){
        listener.onClickBtnUploadOther(view);
    }
    public void onClickBtnUploadIdentity(View view){
        listener.onClickBtnUploadIdentity(view);
    }
    public void onClickBtnRegister(View view){
        listener.onClickBtnRegister(view);
    }
    public void onClickPrivacyPolicy(View view){
        listener.onClickPrivacyPolicy(view);
    }
    public void onClickConditionsTerms(View view){
        listener.onClickConditionsTerms(view);
    }

    @BindingAdapter("ElegantNumberButtonChanged")
    public static void ElegantNumberButtonChanged(ElegantNumberButton view , String tag){ listener.onChangedNumberPicker(view);}

    @BindingAdapter("imgPerson")
    public static void imgPerson(CircleImageView view , Uri image){
        listener.onImagePerson(view , image);
    }
    @BindingAdapter("setAdapter")
    public static void setAdapter(RecyclerView view , String tag){
        listener.onSetAdapter(view);
    }

    @BindingAdapter("onSetImageCioAdapter")
    public static void onSetImageCioAdapter(RecyclerView view , String tag){
        listener.onSetImageCioAdapter(view);
    }

//    @BindingAdapter("setMaxHandValue")
//    public static void setMaxHandValue(RubberRangePicker view , int value){
//
//        signUpLiveData.observe((LifecycleOwner) activity, signUpModel -> {
//            signUpModel.setNumMaxValueHand(value);
//            view.setMax(signUpModel.getNumMaxValueHand());
//        });
//
//    }
//
//    @BindingAdapter("setMinHandValue")
//    public static void setMinHandValue(RubberRangePicker view , int value){
//
//        signUpLiveData.observe((LifecycleOwner) activity, signUpModel -> {
//            signUpModel.setNumMinValueHand(value);
//            view.setMin(1000);
//        });
//
//    }



}
