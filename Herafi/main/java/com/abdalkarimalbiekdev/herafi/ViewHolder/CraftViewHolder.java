package com.abdalkarimalbiekdev.herafi.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.databinding.ItemCraftBinding;

import org.w3c.dom.Text;

public class CraftViewHolder extends RecyclerView.ViewHolder  {

    //public ItemCraftBinding binding;
    public TextView txtCraftName;

    public CraftViewHolder(@NonNull View itemView) {
        super(itemView);

        txtCraftName = itemView.findViewById(R.id.craftName);

    }

}
