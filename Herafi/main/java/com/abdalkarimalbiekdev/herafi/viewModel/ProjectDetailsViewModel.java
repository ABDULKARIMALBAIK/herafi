package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.ProjectDetailsListener;
import com.abdalkarimalbiekdev.herafi.model.ProjectDetailsModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.sanojpunchihewa.glowbutton.GlowButton;
import com.squareup.picasso.Picasso;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectDetailsViewModel extends ViewModel {

    private MutableLiveData<ProjectDetailsModel> liveData;
    private Activity activity;
    private ProjectDetailsListener listener;

    public void setActivity(Activity activity , ProjectDetailsModel model) {
        liveData = new MutableLiveData<>();
        liveData.setValue(model);
        this.activity = activity;
    }

    public void setListener(ProjectDetailsListener listener) {
        this.listener = listener;
    }

    public ProjectDetailsModel getLiveData() {
        return liveData.getValue();
    }

    @BindingAdapter("setProjectUserPhoto")
    public static void setProjectUserPhoto(CircleImageView view , String url){
        Picasso.get().load(Common.BASE_PHOTO + "users/" + url).into(view);
    }

    @BindingAdapter("setProjectImagesAdapter")
    public static void setProjectImagesAdapter(RecyclerView view , String tag){
//        liveData.observe((LifecycleOwner) activity, model -> {
//            view.setAdapter(model.getAdapter());
//            startRecyclerViewAnimation(view);
//        });
    }

    public void ProjectCallPhone(View view){
        listener.onClickPhoneNumber(view);
    }

    public void onAddPhotoClicked(View view){
        listener.onClickAddPhoto(view);
    }

    private static void startRecyclerViewAnimation(RecyclerView recyclerCraft) {

        Context context = recyclerCraft.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_slide_right);

        recyclerCraft.setHasFixedSize(true);
        recyclerCraft.setLayoutManager(new LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false));

        //Set Animation
        recyclerCraft.setLayoutAnimation(controller);
        recyclerCraft.getAdapter().notifyDataSetChanged();
        recyclerCraft.scheduleLayoutAnimation();

    }

}
