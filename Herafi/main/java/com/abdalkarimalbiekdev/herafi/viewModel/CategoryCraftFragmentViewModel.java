package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.abdalkarimalbiekdev.herafi.Adapter.CraftCategoryAdapter;
import com.abdalkarimalbiekdev.herafi.model.CategoryCraftFragmentModel;
import com.abdalkarimalbiekdev.herafi.R;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryCraftFragmentViewModel extends ViewModel {

    public static MutableLiveData<CategoryCraftFragmentModel> liveData;
    public static Activity activity;

    public CategoryCraftFragmentViewModel() {
    }

    public void setActivity(Activity activity , MutableLiveData<CategoryCraftFragmentModel> model) {
        this.activity = activity;
        this.liveData = model;
    }

    public static  MutableLiveData<CategoryCraftFragmentModel> getLiveData() {
        return liveData;
    }

    @BindingAdapter("setAdapterCategpryCraftFragment")
    public static void setAdapterCategpryCraftFragment(RecyclerView view , String tag){

        liveData.observe((LifecycleOwner) activity, categoryCraftFragmentModel -> {
            if (categoryCraftFragmentModel.getAdapter() == null)
                view.setAdapter(new CraftCategoryAdapter());
            view.setAdapter(categoryCraftFragmentModel.getAdapter());
            startRecyclerViewCategoryAnimation(view);
        });
    }

    private static void startRecyclerViewCategoryAnimation(RecyclerView recyclerCraft) {

        Context context = recyclerCraft.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_fall_down);

        recyclerCraft.setHasFixedSize(true);
        recyclerCraft.setLayoutManager(new LinearLayoutManager(context));

        //Set Animation
        recyclerCraft.setLayoutAnimation(controller);
        recyclerCraft.getAdapter().notifyDataSetChanged();
        recyclerCraft.scheduleLayoutAnimation();

    }
}
