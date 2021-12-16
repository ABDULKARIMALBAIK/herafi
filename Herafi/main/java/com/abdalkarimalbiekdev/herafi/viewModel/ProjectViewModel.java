package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.ClickYesListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.ProjectListener;
import com.abdalkarimalbiekdev.herafi.model.ProjectModel;
import com.abdalkarimalbiekdev.herafi.R;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectViewModel extends ViewModel {

    private static MutableLiveData<ProjectModel> liveData;
    private static Activity activity;
    private static ProjectListener listener;

    public ProjectViewModel() {
    }

    public void setActivity(Activity activity , MutableLiveData<ProjectModel> liveData) {
        this.liveData = liveData;
        this.activity = activity;
    }

    public void setListener(ProjectListener listener) {
        this.listener = listener;
    }

    public static MutableLiveData<ProjectModel> getLiveData() {
        return liveData;
    }

    @BindingAdapter("setProjectAdapter")
    public static void setProjectAdapter(RecyclerView view , String tag){
        listener.onAdapterHandler(view);
    }

}
