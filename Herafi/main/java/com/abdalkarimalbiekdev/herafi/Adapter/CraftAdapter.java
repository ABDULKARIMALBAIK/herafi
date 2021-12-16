package com.abdalkarimalbiekdev.herafi.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.model.CraftModel;
import com.abdalkarimalbiekdev.herafi.model.CraftSelectedIModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.ViewHolder.CraftViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import com.abdalkarimalbiekdev.herafi.databinding.ItemCraftBinding;
import com.abdalkarimalbiekdev.herafi.networkModel.CityModel;

public class CraftAdapter extends RecyclerView.Adapter<CraftViewHolder> {

    Activity activity;
    MutableLiveData<List<CraftModel>> craftModels;
    List<CraftModel> craftModelsData;
    int recyclerId = -1;
    boolean isClickable;

    boolean isSelected = false;

    public CraftAdapter(Activity activity, MutableLiveData<List<CraftModel>> craftModels, int recyclerId , boolean isClickable) {
        this.activity = activity;
        this.craftModels = craftModels;
        craftModelsData = craftModels.getValue();
        this.recyclerId = recyclerId;
        this.isClickable = isClickable;
    }

    @NonNull
    @Override
    public CraftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_craft , parent , false);
        return new CraftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CraftViewHolder holder, int position) {

        CraftModel craftModel = craftModelsData.get(holder.getAdapterPosition());
        holder.txtCraftName.setText(craftModel.getName());
        isSelected = false;



        for (int i = 0; i < Common.selectedCraftsData.size(); i++){

            if (Common.selectedCraftsData.get(i).getCraftModel().getId() == craftModel.getId()){   ////Item is selected

                //Log.d("pass_it_here_selected" , position + " , " + recyclerId + " , " + craftModelsData.get(position).getId());
                holder.txtCraftName.setBackgroundResource(R.drawable.design_carft_selected);
                holder.txtCraftName.setTextColor(activity.getResources().getColor(android.R.color.white));
                holder.txtCraftName.setAnimation(AnimationUtils.loadAnimation(activity , R.anim.anime_selected_craft));
                //Add some Animation
                isSelected = true;
            }

        }

        if (!isSelected){
            //Log.d("pass_it_here_unselected" , position + " , " + recyclerId + " , " + craftModelsData.get(position).getId());
            holder.txtCraftName.setBackgroundResource(R.drawable.design_craft);
            holder.txtCraftName.setTextColor(activity.getResources().getColor(R.color.primary_color));
            holder.txtCraftName.setAnimation(AnimationUtils.loadAnimation(activity , R.anim.anime_selected_craft));
            isSelected = false;
        }





//
//            if (Common.selectedCraftsData.get(i).getCraftModel().getRecyclerId() == recyclerId){
//
//                if (Common.selectedCraftsData.get(i).getPosition() == holder.getAdapterPosition()){   ////Item is selected
//
//                    Log.d("pass_it_here_selected" , position + " , " + recyclerId + " , " + craftModelsData.get(position).getId());
//                    holder.txtCraftName.setBackgroundResource(R.drawable.design_carft_selected);
//                    holder.txtCraftName.setTextColor(activity.getResources().getColor(android.R.color.white));
//                    holder.txtCraftName.setAnimation(AnimationUtils.loadAnimation(activity , R.anim.anime_selected_craft));
//                    //Add some Animation
//                    isSelected = true;
//                }
//            }
//
//        }
//
//        if (!isSelected){
//            Log.d("pass_it_here_unselected" , position + " , " + recyclerId + " , " + craftModelsData.get(position).getId());
//            holder.txtCraftName.setBackgroundResource(R.drawable.design_craft);
//            holder.txtCraftName.setTextColor(activity.getResources().getColor(R.color.primary_color));
//            holder.txtCraftName.setAnimation(AnimationUtils.loadAnimation(activity , R.anim.anime_selected_craft));
//            isSelected = false;
//        }


        holder.txtCraftName.setOnClickListener(v -> {

            isSelected = false;


            for (int i = 0; i < Common.selectedCraftsData.size(); i++) {

                if (Common.selectedCraftsData.get(i).getCraftModel().getId() == craftModel.getId()) {  //Item is exists in the list , it is selected before ,so remove it

                    Common.selectedCraftsData.remove(i);
//                    Log.d("my_size", "" + Common.selectedCraftsData.size());
//                    Log.d("no_selected", position + " , " + recyclerId + " , " + craftModelsData.get(position).getId());
//                    Log.d("isSelected", "HERE");
                    isSelected = true;
                    notifyDataSetChanged();


                    MutableLiveData<List<CraftSelectedIModel>> liveData = new MutableLiveData<>();
                    liveData.setValue(Common.selectedCraftsData);
                    if (isClickable){
                        Common.recyclerExp.setAdapter(new CraftSelectedAdapter(activity , liveData));
                        Common.recyclerExp.getAdapter().notifyDataSetChanged();
                    }

                }
            }

            //This mean the item isn't exists in the list
            if (!isSelected) {

                Common.selectedCraftsData.add(new CraftSelectedIModel(holder.getAdapterPosition(), recyclerId, craftModel));
//                Log.d("_selected", position + " , " + recyclerId + " , " + craftModelsData.get(position).getId());
//                Log.d("my_size", "" + Common.selectedCraftsData.size());
//                Log.d("pass2", "HERE");
                isSelected = false;
                notifyDataSetChanged();


                MutableLiveData<List<CraftSelectedIModel>> liveData = new MutableLiveData<>();
                liveData.setValue(Common.selectedCraftsData);
                if (isClickable){
                    Common.recyclerExp.setAdapter(new CraftSelectedAdapter(activity , liveData));
                    Common.recyclerExp.getAdapter().notifyDataSetChanged();
                }

            }



//            for (int i = 0; i < Common.selectedCraftsData.size(); i++) {
//
//                if (Common.selectedCraftsData.get(i).getRecyclerId() == recyclerId) {
//
//                    if (Common.selectedCraftsData.get(i).getPosition() == position) {  //Item is exists in the list , it is selected before ,so remove it
//
//                        Common.selectedCraftsData.remove(i);
//                        Log.d("my_size", "" + Common.selectedCraftsData.size());
//                        Log.d("no_selected", position + " , " + recyclerId + " , " + craftModelsData.get(position).getId());
//                        Log.d("isSelected", "HERE");
//                        isSelected = true;
//                        notifyDataSetChanged();
//
//
//                        MutableLiveData<List<CraftSelectedIModel>> liveData = new MutableLiveData<>();
//                        liveData.setValue(Common.selectedCraftsData);
//                        if (isClickable){
//                            Common.recyclerExp.setAdapter(new CraftSelectedAdapter(activity , liveData));
//                            Common.recyclerExp.getAdapter().notifyDataSetChanged();
//                        }
//
////                            Common.getSelectedCraftsLiveData().observe((LifecycleOwner) activity, craftSelectedItems -> {
////                                craftSelectedItems.remove(finalI);
////                                isSelected = true;
////                                notifyDataSetChanged();
////                            });
//
//                    }
//                }
//            }

            //This mean the item isn't exists in the list
//            if (!isSelected) {
//
//                Common.selectedCraftsData.add(new CraftSelectedIModel(position, recyclerId, craftModelsData.get(position)));
//                Log.d("_selected", position + " , " + recyclerId + " , " + craftModelsData.get(position).getId());
//                Log.d("my_size", "" + Common.selectedCraftsData.size());
//                Log.d("pass2", "HERE");
//                isSelected = false;
//                notifyDataSetChanged();
//
//
//                MutableLiveData<List<CraftSelectedIModel>> liveData = new MutableLiveData<>();
//                liveData.setValue(Common.selectedCraftsData);
//                if (isClickable){
//                    Common.recyclerExp.setAdapter(new CraftSelectedAdapter(activity , liveData));
//                    Common.recyclerExp.getAdapter().notifyDataSetChanged();
//                }
//
//            }
        });

    }

    @Override
    public int getItemCount() {
        return craftModels.getValue().size();
    }

}
