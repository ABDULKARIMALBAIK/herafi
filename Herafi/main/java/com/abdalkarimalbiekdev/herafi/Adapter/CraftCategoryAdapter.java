package com.abdalkarimalbiekdev.herafi.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.model.CategoryCraftFragmentModel;
import com.abdalkarimalbiekdev.herafi.networkModel.CategoryModel;
import com.abdalkarimalbiekdev.herafi.model.CraftModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.ViewHolder.CraftCategoryViewHolder;
import com.abdalkarimalbiekdev.herafi.databinding.ItemCategoryCraftBinding;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CraftCategoryAdapter extends RecyclerView.Adapter<CraftCategoryViewHolder> {

    Activity activity;
    MutableLiveData<List<CategoryModel>> categories;
    HerafiAPI api;

    public CraftCategoryAdapter() {
    }

    public CraftCategoryAdapter(Activity activity, List<CategoryModel> data) {
        this.activity = activity;
        categories = new MutableLiveData<>();
        categories.setValue(data);

        api = Common.getAPI();
    }

    @NonNull
    @Override
    public CraftCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemCategoryCraftBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity) , R.layout.item_category_craft , parent , false);
//        binding.setLifecycleOwner((LifecycleOwner) activity);
        return new CraftCategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CraftCategoryViewHolder holder, int position) {

        MutableLiveData<List<CraftModel>> crafts = new MutableLiveData<>();

        try {
            //No need to decrypt because we will send id as encrypted
            categories.observe((LifecycleOwner) activity, models -> {

                String encId = models.get(position).getId();
                Log.d("id_category" , encId);

                //getCraftsOfCategory(encId);
                //Get crafts of category by id then return list of crafts
                new CompositeDisposable().add(
                        api.getCrafts(encId)
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

                                            Log.d("got_1","OK");

                                            //Save the token
                                            Hawk.put("TOKEN" , listResultModel.getResponse().getToken());

                                            //set value to MutableLiveData
                                            //Decryption the data
                                            List<CraftModel> craftModels = listResultModel.getResponse().getResult();
                                            for (int i = 0; i < craftModels.size(); i++) {
                                                try {
                                                    craftModels.get(i).setId(AES.decrypt(craftModels.get(i).getId()));
                                                    craftModels.get(i).setName(AES.decrypt(craftModels.get(i).getName()));
                                                }
                                                catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            try {

                                                CategoryModel categoryModel = new CategoryModel();
                                                categoryModel.setName(AES.decrypt(models.get(position).getName()));
                                                Log.d("dec_name_2" ,categoryModel.getName());

//                                holder.binding.setCategoryItem(categoryModel);
                                                crafts.setValue(craftModels);

                                                holder.binding.recyclerCraft.setAdapter(new CraftAdapter(activity , crafts , position , true));
                                                holder.binding.txtCategory.setText(categoryModel.getName());
                                                startRecyclerViewCategoryAnimation(holder.binding.recyclerCraft);
                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else
                                            Toasty.error(activity, activity.getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                    }
                                    else {

                                        //set value to MutableLiveData
                                        //Decryption the data
                                        List<CraftModel> craftModels = listResultModel.getResponse().getResult();
                                        for (int i = 0; i < craftModels.size(); i++) {
                                            try {
                                                craftModels.get(i).setId(AES.decrypt(craftModels.get(i).getId()));
                                                craftModels.get(i).setName(AES.decrypt(craftModels.get(i).getName()));
                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        try {
                                            CategoryModel categoryModel = models.get(position);
                                            categoryModel.setName(AES.decrypt(models.get(position).getName()));
                                            Log.d("dec_name_2" ,categoryModel.getName());

//                                holder.binding.setCategoryItem(categoryModel);
                                            Log.d("category_id" , String.valueOf(position));

                                            crafts.setValue(craftModels);

                                            holder.binding.recyclerCraft.setAdapter(new CraftAdapter(activity , crafts , position , true));
                                            holder.binding.txtCategory.setText(categoryModel.getName());
                                            startRecyclerViewCategoryAnimation(holder.binding.recyclerCraft);
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }

                            }, throwable -> Log.d("error_request_craftCate" , throwable.getMessage()))

                );

            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return categories.getValue().size();
    }

    private void startRecyclerViewCategoryAnimation(RecyclerView recyclerCraft) {

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
