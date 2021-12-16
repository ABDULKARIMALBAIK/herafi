package com.abdalkarimalbiekdev.herafi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdalkarimalbiekdev.herafi.model.ImageCioModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.ViewHolder.ImageCioViewHolder;
import com.abdalkarimalbiekdev.herafi.databinding.ItemImageCioBinding;

import java.util.List;

import androidx.annotation.NonNull;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

public class ImageCioAdpater extends RecyclerView.Adapter<ImageCioViewHolder> {


    Context context;
    MutableLiveData<List<ImageCioModel>> liveDataList;

    public ImageCioAdpater() {
    }

    public ImageCioAdpater(Context context, MutableLiveData<List<ImageCioModel>> liveDataList) {
        this.context = context;
        this.liveDataList = liveDataList;
    }

    public MutableLiveData<List<ImageCioModel>> getLiveDataList() {
        return liveDataList;
    }

    @NonNull
    @Override
    public ImageCioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_cio , parent , false);
//        ItemImageCioBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context) , R.layout.item_image_cio , parent , false);
//        binding.setLifecycleOwner((LifecycleOwner) context);
        return new ImageCioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageCioViewHolder holder, int position) {

//        ImageCioModel model = liveDataList.getValue().get(position);
//        holder.binding.setImageCioBinding(model);

        liveDataList.observe((LifecycleOwner) context, model ->{
//            holder.binding.setImageCioBinding(modele.get(position));
            holder.imgCIO.setImageURI(model.get(position).getImgPath());
        });


        //If Craftsman use long click on the image , this image will disappear
        holder.imgCIO.setOnLongClickListener(v -> {

            liveDataList.observe((LifecycleOwner) context, model ->{
                model.remove(position);
                notifyDataSetChanged();
            });

            return true;
        });

    }

    @Override
    public int getItemCount() {
        return liveDataList.getValue().size();
    }

//    public void addNewImage(ImageCioModel image){
//        liveDataList.observe((LifecycleOwner) context, model -> model.add(image));
//        notifyDataSetChanged();
//    }
}
