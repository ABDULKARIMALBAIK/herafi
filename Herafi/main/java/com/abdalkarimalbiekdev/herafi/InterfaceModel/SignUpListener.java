package com.abdalkarimalbiekdev.herafi.InterfaceModel;

import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.sanojpunchihewa.glowbutton.GlowButton;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public interface SignUpListener {

    void onClickBtnPhoto(View view);
    void onClickBtnExp(View view);
    void onClickBanUploadCertificate(View view);
    void onClickBtnUploadOther(View view);
    void onClickBtnUploadIdentity(View view);
    void onClickBtnRegister(View view);
    void onClickPrivacyPolicy(View view);
    void onClickConditionsTerms(View view);
    void onChangedNumberPicker(ElegantNumberButton view);
    void onImagePerson(CircleImageView view , Uri image);
    void onSetAdapter(RecyclerView view);
    void onSetImageCioAdapter(RecyclerView view);
}
