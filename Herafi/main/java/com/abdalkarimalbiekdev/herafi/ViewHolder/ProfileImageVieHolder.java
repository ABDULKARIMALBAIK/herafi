package com.abdalkarimalbiekdev.herafi.ViewHolder;

import android.view.View;

import com.abdalkarimalbiekdev.herafi.databinding.ItemImageProfileBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileImageVieHolder extends RecyclerView.ViewHolder {

    public ItemImageProfileBinding binding;

    public ProfileImageVieHolder(@NonNull ItemImageProfileBinding itemView) {
        super(itemView.getRoot());

        binding = itemView;
    }
}
