package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.ProfileListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.ProfileGetDataListener;
import com.abdalkarimalbiekdev.herafi.model.ProfileModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.squareup.picasso.Picasso;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileViewModel extends ViewModel {

    private static MutableLiveData<ProfileModel> liveData;

    private static Activity activity;
    private ProfileListener listener;
    private ProfileGetDataListener listenerPass;

    public void setActivity(Activity activity , ProfileModel model) {
        liveData = new MutableLiveData<>();
        liveData.setValue(model);
        this.activity = activity;
    }

    public void setListener(ProfileListener listener) {
        this.listener = listener;
    }

    public void setListenerPass(ProfileGetDataListener listenerPass) {
        this.listenerPass = listenerPass;
    }

    public void onYesClicked(View view){
        listener.onYesClicked(view);
    }

    public void onNOClicked(View view){
        listener.onNoClicked(view);
    }

    public ProfileModel getLiveData() {
        return liveData.getValue();
    }

    @BindingAdapter("setImageProfile")
    public static void setImageProfile(ImageView view , String url){
        Picasso.get().load(Common.BASE_PHOTO + "craftmen/" +url).into(view);
    }


    @BindingAdapter("setProfileImagesAdapter")
    public static void setProfileImagesAdapter(RecyclerView view , String tag){

        //Fix the Code here//

        liveData.observe((LifecycleOwner) activity, model -> {
            Log.d("pass_here" , "OK");
            view.setAdapter(model.getImageAdapter());
            startRecyclerViewAnimation(view);
        });
    }


    @BindingAdapter("setProfileCraftsAdapter")
    public static void setProfileCraftsAdapter(RecyclerView view , String tag){
        liveData.observe((LifecycleOwner) activity, model -> {
            Log.d("pass_here2" , "OK");
            view.setAdapter(model.getCraftAdapter());
            startRecyclerViewAnimation(view);
        });
    }

    @BindingAdapter("setLatLng")
    public static void setLatLng(TextView textView , String tag){
        liveData.observe((LifecycleOwner) activity, profileModel -> {
            profileModel.getProfileItemModelLiveData().observe((LifecycleOwner) activity, profileItemModel -> {
                textView.setText(profileItemModel.getLat() + " ~ " + profileItemModel.getLng());
            });

        });
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
