package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInDialogVerifyListener;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogVerifyModel;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignInDailogVerifyViewModel extends ViewModel {

    public static MutableLiveData<SignInDialogVerifyModel> verifyLiveData;

    private static Activity activity;
    private SignInDialogVerifyListener listener;

    public SignInDailogVerifyViewModel() {
    }

    public void setActivity(Activity activity , SignInDialogVerifyModel model) {

        this.activity = activity;
        verifyLiveData = new MutableLiveData<>();
        verifyLiveData.setValue(model);
    }

    public void setListener(SignInDialogVerifyListener listener) {
        this.listener = listener;
    }

    public SignInDialogVerifyModel getVerifyLiveData() {
        return verifyLiveData.getValue();
    }

    public MutableLiveData<SignInDialogVerifyModel> getVerifyMutableLiveData(){
        return verifyLiveData;
    }

    public static TextWatcher getInfoTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                return;
            }
            @Override
            public void afterTextChanged(Editable s) {
                verifyLiveData.observe((LifecycleOwner) activity, SignInDialogVerifyModel -> {
                    SignInDialogVerifyModel.setEdtInfo(s.toString());
                });
            }
        };
    }


    @BindingAdapter("loadImageInfo")
    public static void loadImageInfo(ImageView view , int path){
        verifyLiveData.observe((LifecycleOwner) activity, model -> {
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
