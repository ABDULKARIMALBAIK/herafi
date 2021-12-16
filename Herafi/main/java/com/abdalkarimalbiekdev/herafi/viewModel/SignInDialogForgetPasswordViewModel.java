package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInDialogVerifyListener;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogForgetPasswordModel;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class SignInDialogForgetPasswordViewModel extends ViewModel {

    private static MutableLiveData<SignInDialogForgetPasswordModel> liveData;
    private static Activity  activity;

    //I can't use also to forget_password
    private static SignInDialogVerifyListener listener;

    public SignInDialogForgetPasswordModel getLiveData() {
        return liveData.getValue();
    }

    public void setActivity(Activity activity , SignInDialogForgetPasswordModel model) {
        this.activity = activity;
        liveData = new MutableLiveData<>();
        liveData.setValue(model);
    }

    public void setListener(SignInDialogVerifyListener listener) {
        this.listener = listener;
    }

    public MutableLiveData<SignInDialogForgetPasswordModel> getLiveDataObserve() {
        return liveData;
    }

    public static TextWatcher getEmailTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                liveData.observe((LifecycleOwner) activity, model -> {
                    model.setEdtEmail(s.toString());
                });
            }
        };
    }

    public static TextWatcher getPhoneNumberTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                liveData.observe((LifecycleOwner) activity, model -> {
                    model.setEdtPhoneNumber(s.toString());
                });
            }
        };
    }

    public static TextWatcher getSecureCodeTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                liveData.observe((LifecycleOwner) activity, model -> {
                    model.setEdtScureCode(s.toString());
                });
            }
        };
    }

    @BindingAdapter("loadImageForgetPassword")
    public static void loadImageInfo(ImageView view , int path){
        liveData.observe((LifecycleOwner) activity, model -> {
            model.setImgDialog(path);
            view.setImageResource(model.getImgDialog());  //model.getImgDialog()
        });
    }

    public void onClickYes(View view){
        listener.onClickYes(view);
    }
    public void onClickNo(View view){
        listener.onClickNo(view);
    }
}
