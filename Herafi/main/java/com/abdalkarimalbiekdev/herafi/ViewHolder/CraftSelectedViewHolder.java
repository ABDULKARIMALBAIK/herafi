package com.abdalkarimalbiekdev.herafi.ViewHolder;

import android.view.View;
import android.widget.TextView;


import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.databinding.ItemSelectedCraftBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CraftSelectedViewHolder extends RecyclerView.ViewHolder {

//    public ItemSelectedCraftBinding binding;
    public TextView txtCraftName;

    public CraftSelectedViewHolder(@NonNull View itemView) {
        super(itemView);

//        binding = itemView;
        txtCraftName = itemView.findViewById(R.id.craftName);

    }
}
