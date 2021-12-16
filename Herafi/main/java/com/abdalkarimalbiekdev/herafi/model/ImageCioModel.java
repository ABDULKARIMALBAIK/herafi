package com.abdalkarimalbiekdev.herafi.model;

import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

public class ImageCioModel extends BaseObservable {

    private Uri imgPath;
    private int typeImage;

    public ImageCioModel() {
    }

    public ImageCioModel(Uri imgPath, int typeImage) {
        this.imgPath = imgPath;
        this.typeImage = typeImage;
    }

    public Uri getImgPath() {
        return imgPath;
    }

    public void setImgPath(Uri imgPath) {
        this.imgPath = imgPath;
    }

    public int getTypeImage() {
        return typeImage;
    }

    public void setTypeImage(int typeImage) {
        this.typeImage = typeImage;
    }

    @BindingAdapter("imgCioLoad")
    public static void imgCioLoad(ImageView imageView , Uri path){
        imageView.setImageURI(path);
    }
}
