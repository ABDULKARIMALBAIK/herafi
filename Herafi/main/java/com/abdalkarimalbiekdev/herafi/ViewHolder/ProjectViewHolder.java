package com.abdalkarimalbiekdev.herafi.ViewHolder;

import android.view.View;

import com.abdalkarimalbiekdev.herafi.databinding.ItemProjectBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectViewHolder extends RecyclerView.ViewHolder {

    public ItemProjectBinding binding;

    public ProjectViewHolder(@NonNull ItemProjectBinding itemView) {
        super(itemView.getRoot());

        binding = itemView;
    }
}
