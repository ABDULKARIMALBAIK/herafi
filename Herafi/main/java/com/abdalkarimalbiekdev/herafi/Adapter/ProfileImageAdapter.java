package com.abdalkarimalbiekdev.herafi.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.networkModel.ProfileImageItemModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.ViewHolder.ProfileImageVieHolder;
import com.abdalkarimalbiekdev.herafi.databinding.ItemImageProfileBinding;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileImageAdapter extends RecyclerView.Adapter<ProfileImageVieHolder> {

    Context context;
    List<ProfileImageItemModel> images;

    public ProfileImageAdapter(Context context, List<ProfileImageItemModel> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ProfileImageVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemImageProfileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context) , R.layout.item_image_profile , parent , false);
//        binding.setLifecycleOwner((LifecycleOwner) context);
        return new ProfileImageVieHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileImageVieHolder holder, int position) {

        Log.d("my_image", images.get(position).getImage() != null ? Common.BASE_PHOTO + "craftmen/" +  images.get(position).getImage() : "........");

        Glide.with(context)
                .load(Common.BASE_PHOTO + "craftmen/" +  images.get(position).getImage())
                .into(holder.binding.imgProfileItem);

    }

    @Override
    public int getItemCount() {
        Log.d("size_images", String.valueOf(images.size()));
        return images.size();
    }
}
