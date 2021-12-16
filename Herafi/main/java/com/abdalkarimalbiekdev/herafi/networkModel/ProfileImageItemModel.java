package com.abdalkarimalbiekdev.herafi.networkModel;

import android.util.Log;
import android.widget.ImageView;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

public class ProfileImageItemModel extends BaseObservable {

    @Nullable
    @SerializedName("photo")
    public String photo;

    public String getImage() {
        return photo;
    }

    public void setImage(String image) {
        this.photo = image;
    }

    @BindingAdapter("setImageProfileItem")
    public static void setImageProfileItem(ImageView view , String url){
//        Picasso.get().load(Common.BASE_PHOTO + "craftmen/" + url).into(view);
//        Log.d("show_photo" , Common.BASE_PHOTO + "craftmen/" + url);
    }
}
