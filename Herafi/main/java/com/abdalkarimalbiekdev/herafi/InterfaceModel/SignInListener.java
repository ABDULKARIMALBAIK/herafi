package com.abdalkarimalbiekdev.herafi.InterfaceModel;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanojpunchihewa.glowbutton.GlowButton;

public interface SignInListener {

    void onVerifyClicked(View view);
    void onForgetPasswordClicked(View view);
    void onLoginClicked(View view);
    void onFingerprintClicked(View view);
    void onFacebookClicked(View view);
    void onGoogleClicked(View view);
    void onSignUpTextClick(View view);
}
