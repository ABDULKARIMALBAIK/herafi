package com.abdalkarimalbiekdev.herafi.ViewHolder;

import android.view.View;

import com.abdalkarimalbiekdev.herafi.databinding.ItemCategoryCraftBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CraftCategoryViewHolder extends RecyclerView.ViewHolder{

    public ItemCategoryCraftBinding binding;

    public CraftCategoryViewHolder(@NonNull ItemCategoryCraftBinding itemView) {
        super(itemView.getRoot());

        binding = itemView;
    }
}
