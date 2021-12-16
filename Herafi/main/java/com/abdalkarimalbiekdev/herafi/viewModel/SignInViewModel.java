package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInListener;
import com.abdalkarimalbiekdev.herafi.model.SignInModel;
import com.sanojpunchihewa.glowbutton.GlowButton;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignInViewModel extends ViewModel {

    private MutableLiveData<SignInModel> signInViewModel;
    private Activity activity;
    private SignInListener listener;



    public SignInModel getSignInViewModel(){
        return signInViewModel.getValue();
    }

    public MutableLiveData<SignInModel> getSignInLiveData(){
        return signInViewModel;
    }

    public void setListener(SignInListener listener) {
        this.listener = listener;
    }

    public void setActivity(Activity activity) {

        this.activity = activity;

        SignInModel signInModel = new SignInModel();
        signInViewModel = new MutableLiveData<>();
        signInViewModel.setValue(signInModel);
    }

    public TextWatcher getEmailTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                signInViewModel.observe((LifecycleOwner) activity, signInModel -> {
                    signInModel.setEmail(s.toString());
                });
            }
        };
    }
    public TextWatcher getPasswordWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                signInViewModel.observe((LifecycleOwner) activity, signInModel -> {
                    signInModel.setPassword(s.toString());
                });
            }
        };
    }


    public void onGoogleClicked(View view){ listener.onGoogleClicked(view);}
    public void onVerifyClicked(View view){ listener.onVerifyClicked(view); }
    public  void onForgetPasswordClicked(View view){ listener.onForgetPasswordClicked(view); }
    public  void onLoginClicked(View view){
        listener.onLoginClicked(view);
    }
    public  void onFingerprintClicked(View view){
        listener.onFingerprintClicked(view);
    }
    public  void onFacebookClicked(View view){
        listener.onFacebookClicked(view);
    }
    public  void onSignUpTextClick(View view){
        listener.onSignUpTextClick(view);
    }

}
