package com.abdalkarimalbiekdev.herafi.ViewHolder;

import android.view.View;
import android.widget.ImageView;

import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.databinding.ItemImageCioBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageCioViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgCIO;
    public ItemImageCioBinding binding;

    public ImageCioViewHolder(@NonNull View itemView) {
        super(itemView);

//        binding = itemView;
        imgCIO = itemView.findViewById(R.id.imgCio);
    }
}
