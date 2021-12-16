package com.abdalkarimalbiekdev.herafi.Fragment.Crafts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Adapter.CraftCategoryAdapter;
import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.SignUp;
import com.abdalkarimalbiekdev.herafi.model.CategoryCraftFragmentModel;
import com.abdalkarimalbiekdev.herafi.networkModel.CategoryModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.abdalkarimalbiekdev.herafi.viewModel.CategoryCraftFragmentViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.FragmentCraftBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CraftFragment extends BottomSheetDialogFragment {

    FragmentCraftBinding binding;
    CategoryCraftFragmentViewModel viewModel;
    HerafiAPI api;

    Activity activity;

    public CraftFragment(Activity activity) {
        this.activity = activity;

        api = Common.getAPI();
        Hawk.init(activity).build();

        viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(CategoryCraftFragmentViewModel.class);

        if (viewModel == null)
            Log.d("viewModel_NULL" , "OK");
        else
            Log.d("viewModel_FULL" , "OK");

        //Load categories then call observe and put new adapter inside the model
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setStyle( DialogFragment.STYLE_NORMAL, R.style.DialogStyle );
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_craft , container , false);
        binding.setLifecycleOwner((LifecycleOwner) activity);

        loadAllCategories();

        return binding.getRoot();
    }

    private void loadAllCategories() {

        try {
            new CompositeDisposable().add(
                    api.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(listResultModel -> {

                            if (listResultModel != null){
                                if (!listResultModel.getErrorMessage().isEmpty()){

                                    try {
                                        String error = AES.decrypt(listResultModel.getErrorMessage());
                                        Log.d("error_message_here" ,error);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                else if (!listResultModel.getResponse().getToken().isEmpty()){
                                    if (Common.checkSecureToken(listResultModel.getResponse().getToken())){

                                        Log.d("started_here" , "OK");
                                        //Save the token
                                        Hawk.put("TOKEN" , listResultModel.getResponse().getToken());

                                        //setup adapter to to recyclerview categories
                                        Log.d("size_craftsew" , String.valueOf(listResultModel.getResponse().getResult()));

                                        binding.recyclerCraftCategory.setAdapter(new CraftCategoryAdapter(activity , listResultModel.getResponse().getResult()));
                                        startRecyclerViewCategoryAnimation(binding.recyclerCraftCategory);

                                        Log.d("GET_IT_SUCCESSfuly1" , "OK");

                                    }
                                    else{
                                        Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                        Log.d("DATA_IS_NULL" , "OK");
                                    }
                                }
                                else {
                                    //setup adapter to to recyclerview categories
                                    Log.d("size_craftssss" , String.valueOf(listResultModel.getResponse().getResult()));

                                    binding.recyclerCraftCategory.setAdapter(new CraftCategoryAdapter(activity , listResultModel.getResponse().getResult()));
                                    startRecyclerViewCategoryAnimation(binding.recyclerCraftCategory);

                                    Log.d("GET_IT_SUCCESSfuly1" , "OK");
                                }
                            }

                        }, throwable -> Log.d("error_request_crafCatoy" , throwable.getMessage()))


            );
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("error_crafts_category" , e.getMessage());
        }

    }

    private void startRecyclerViewCategoryAnimation(RecyclerView recyclerCraft) {

        Context context = recyclerCraft.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_slide_right);

        recyclerCraft.setHasFixedSize(true);
        recyclerCraft.setLayoutManager(new LinearLayoutManager(context));

        //Set Animation
//        recyclerCraft.setLayoutAnimation(controller);
//        recyclerCraft.getAdapter().notifyDataSetChanged();
//        recyclerCraft.scheduleLayoutAnimation();

    }
}