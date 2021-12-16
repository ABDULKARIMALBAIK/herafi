package com.abdalkarimalbiekdev.herafi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.ProjectDetails;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.networkModel.ProjectItemModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.ViewHolder.ProjectViewHolder;
import com.abdalkarimalbiekdev.herafi.databinding.ItemProjectBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;


public class ProjectAdapter extends RecyclerView.Adapter<ProjectViewHolder> {

    Context context;
    List<ProjectItemModel> projects;

    public ProjectAdapter(Context context, List<ProjectItemModel> items) {
        this.context = context;
        this.projects = items;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemProjectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context) , R.layout.item_project , parent , false);

        return new ProjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {

        try {

            //Set image of project
            Glide.with(context)
                    .load(Common.BASE_PHOTO + "craftmen/" + projects.get(position).getProjectPhoto())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.imgProject);

            //set image of user
            Glide.with(context)
                    .load(Common.BASE_PHOTO + "users/" + projects.get(position).getUserPhoto())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.imgProjectPerson);

            //set rating of project
            holder.binding.ratingProjectItem.setRating(Float.parseFloat(projects.get(position).getTotalRate()));

            holder.binding.setProjectItemBinding(projects.get(position));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

//        holder.binding.setProjectItemBinding(model);
//
//        projects.observe((LifecycleOwner) context, projectItemModels -> {
//
//            ProjectItemModel model = projectItemModels.get(position);
//
//
//
//        });

        holder.binding.getRoot().setOnClickListener(v -> {

            Intent intent = new Intent(context , ProjectDetails.class);
            intent.putExtra("projectId" , projects.get(position).getProjectId());
            intent.putExtra("token" , Hawk.get("TOKEN").toString());
            context.startActivity(intent);

//            projects.observe((LifecycleOwner) context, projectItemModels -> {
//
//
//            });

        });

    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public List<ProjectItemModel> getList(){
        return projects;
    }

    public void updateList(List<ProjectItemModel> newProjectData){
        projects = newProjectData;
        notifyDataSetChanged();
    }
}
