package com.abdalkarimalbiekdev.herafi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.model.CraftSelectedIModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.ViewHolder.CraftSelectedViewHolder;
import com.abdalkarimalbiekdev.herafi.databinding.ItemSelectedCraftBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

public class CraftSelectedAdapter extends RecyclerView.Adapter<CraftSelectedViewHolder> {

    Context context;
    MutableLiveData<List<CraftSelectedIModel>> items;

    public CraftSelectedAdapter() {
    }

    public CraftSelectedAdapter(Context context, MutableLiveData<List<CraftSelectedIModel>> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CraftSelectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        ItemSelectedCraftBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context) , R.layout.item_selected_craft, parent , false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_craft , parent, false);
//        binding.setLifecycleOwner((LifecycleOwner) context);

        return new CraftSelectedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CraftSelectedViewHolder holder, int position) {

//        holder.binding.setLifecycleOwner((LifecycleOwner) context);

        items.observe((LifecycleOwner) context, model ->{

            CraftSelectedIModel item = model.get(position);
            try {
//                item.getCraftModel().setName(item.getCraftModel().getName());
                holder.txtCraftName.setText(item.getCraftModel().getName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
//            holder.binding.setCraftSelectedItem(item);
        });



    }

    @Override
    public int getItemCount() {
        return items.getValue().size();
    }

}
