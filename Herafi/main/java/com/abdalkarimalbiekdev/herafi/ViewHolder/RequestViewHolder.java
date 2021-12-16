package com.abdalkarimalbiekdev.herafi.ViewHolder;

import android.view.View;

import com.abdalkarimalbiekdev.herafi.databinding.ItemRequestBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RequestViewHolder extends RecyclerView.ViewHolder {

    public ItemRequestBinding binding;

    public RequestViewHolder(@NonNull ItemRequestBinding itemView) {
        super(itemView.getRoot());

        binding = itemView;
    }
}
