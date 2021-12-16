package com.abdalkarimalbiekdev.herafi.Fragment.Profile;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Adapter.CraftAdapter;
import com.abdalkarimalbiekdev.herafi.Adapter.ProfileImageAdapter;
import com.abdalkarimalbiekdev.herafi.Adapter.ProjectAdapter;
import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.ProfileListener;
import com.abdalkarimalbiekdev.herafi.NavigatorFragment;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.SignIn;
import com.abdalkarimalbiekdev.herafi.model.CraftModel;
import com.abdalkarimalbiekdev.herafi.model.ProfileModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.networkModel.ProfileImageItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ProfileItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.abdalkarimalbiekdev.herafi.service.CheckNewRequests;
import com.abdalkarimalbiekdev.herafi.viewModel.ProfileViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.FragmentProfileBinding;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.List;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    ProfileViewModel viewModel;
    ProfileModel model;

    HerafiAPI api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_profile , container , false);
//        binding.setLifecycleOwner(getActivity());

        viewModel = new ViewModelProvider(getActivity()).get(ProfileViewModel.class);


        api = Common.getAPI();
        Hawk.init(getActivity()).build();
        model = new ProfileModel();

        if (Common.isConnectionToInternet(getActivity())){

            loadData();
//            fetchCraftsmanData();

            viewModel.setListener(new ProfileListener() {
                @Override
                public void onYesClicked(View view) {
                    if (Common.isConnectionToInternet(getActivity())){
                        changeStatus(2);
                    }
                    else{
                        configNoInternet();
                        Toasty.info(getActivity(), getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();
                    }

                }

                @Override
                public void onNoClicked(View view) {
                    if (Common.isConnectionToInternet(getActivity())){
                        changeStatus(1);
                    }
                    else{
                        configNoInternet();
                        Toasty.info(getActivity(), getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();
                    }

                }
            });
            viewModel.setActivity(getActivity() , model);
            binding.setProfileBinding(viewModel);

        }
        else{
            configNoInternet();
            Toasty.info(getActivity(), getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();
        }


        boolean dark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        binding.swipeProfile.setProgressBackgroundColorSchemeResource(dark? R.color.black_item : R.color.white_item);
        binding.swipeProfile.setColorSchemeColors(
                getResources().getColor(R.color.primary_color),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark)
        );
        binding.swipeProfile.setOnRefreshListener(() -> {

            if (Common.isConnectionToInternet(getActivity())){

                loadData();
                binding.swipeProfile.setRefreshing(false);
            }
            else {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
                configNoInternet();
                binding.swipeProfile.setRefreshing(false);
                return;
            }

        });


        return binding.getRoot();
    }

    private void configNoData() {
        binding.imgBackground.setImageResource(R.drawable.no_data);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getActivity().getResources().getString(R.string.no_data_to_show));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.scrollProfile.setVisibility(View.GONE);
    }

    private void configGetData() {
        binding.imgBackground.setVisibility(View.GONE);
        binding.textBackground.setVisibility(View.GONE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.scrollProfile.setVisibility(View.VISIBLE);
    }

    private void configSomeProblem() {
        binding.imgBackground.setImageResource(R.drawable.some_problem);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getActivity().getResources().getString(R.string.some_problem));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.scrollProfile.setVisibility(View.GONE);
    }

    private void configNoInternet() {
        binding.imgBackground.setImageResource(R.drawable.no_internet);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getActivity().getResources().getString(R.string.you_need_internet));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.scrollProfile.setVisibility(View.GONE);
    }

    private void changeStatus(int status) {

        //Status is busy = 2 , free = 1
        String token = Hawk.get("TOKEN");
        try {
            String encStatus = AES.encrypt(String.valueOf(status));

            new CompositeDisposable().add(
                    api.changeStatusCraftsman(token,
                            Common.craftmanData.getId(),
                            encStatus)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(stringResultModel -> {

                        if (stringResultModel != null){
                            if (!stringResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String errorMessage = AES.decrypt(stringResultModel.getErrorMessage());
                                    Log.d("error_message_here" , errorMessage);
                                    Toasty.error(getActivity(), errorMessage, Toast.LENGTH_SHORT , true).show();

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                    //Save the token
                                    Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                    //status is update successfully !
                                    try {
                                        Toasty.success(getActivity() , AES.decrypt(stringResultModel.getResponse().getResult()) , Toasty.LENGTH_SHORT , true).show();
                                    }
                                    catch (Exception e) {
                                        Toasty.error(getActivity() , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                        e.printStackTrace();
                                    }
                                }
                                else
                                    Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                         }
                            else {
                                //check count of requests
                                try {
                                    Toasty.success(getActivity() , AES.decrypt(stringResultModel.getResponse().getResult()) , Toasty.LENGTH_SHORT , true).show();
                                }
                                catch (Exception e) {
                                    Toasty.error(getActivity() , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                    e.printStackTrace();
                                }
                            }
                        }

                    }, throwable ->  Log.d("error_request_google" , throwable.getMessage()))
            );
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("error_aes",e.getMessage());
        }


    }

    private void loadData() {

        //Load craftsman's data
        fetchCraftsmanData();

        //load his images
        fetchCraftsmanImages();

        //load his crafts
        fetchCraftsmanCrafts();

    }

    private void fetchCraftsmanCrafts() {

        try {
            String token = Hawk.get("TOKEN");
            Log.d("is_token",token +"");

            new CompositeDisposable().add(
                    api.craftsmancCrafts(
                            "Bearer " + token,
                            Common.craftmanData.getId()
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listResultModel -> {

                        if (listResultModel != null){
                            if (!listResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String error = AES.decrypt(listResultModel.getErrorMessage());
                                    Log.d("error_message_here" , error);
                                    Toasty.error(getActivity(), error, Toast.LENGTH_SHORT , true).show();

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else if (listResultModel.getResponse().getResult().size() <= 0){
                                configNoData();
                            }
                            else if (!listResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(listResultModel.getResponse().getToken())){

                                    configGetData();

                                    //Save the token
                                    Hawk.put("TOKEN" , listResultModel.getResponse().getToken());

                                    //Save crafts of craftsman
                                    Gson gson = new Gson();
                                    Log.d("crafts_data",gson.toJson(listResultModel.getResponse().getResult()));

                                    List<CraftModel> craftsmanCraftsList = listResultModel.getResponse().getResult();

                                    for (int i = 0; i < craftsmanCraftsList.size(); i++) {
                                        try {
                                            craftsmanCraftsList.get(i).setName(AES.decrypt(craftsmanCraftsList.get(i).getName()));
                                            craftsmanCraftsList.get(i).setId(AES.decrypt(craftsmanCraftsList.get(i).getId()));
                                        }
                                        catch (Exception e) {
                                            configSomeProblem();
                                            Toast.makeText(getActivity(), "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }

                                    MutableLiveData<List<CraftModel>> craftsmanImage = new MutableLiveData<>();
                                    craftsmanImage.setValue(craftsmanCraftsList);

                                    model.setCraftAdapter(new CraftAdapter(getActivity() , craftsmanImage , 0 , false));


                                    binding.recyclerCraftWorker.setHasFixedSize(true);
                                    binding.recyclerCraftWorker.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.HORIZONTAL , false));
                                    binding.recyclerCraftWorker.setAdapter(new CraftAdapter(getActivity() , craftsmanImage , 0 , false));
                                    startRecyclerViewAnimation(binding.recyclerCraftWorker);

                                    Log.d("is_has_images" , model.getCraftAdapter().getItemCount() > 0 ? "FULL" : "NULL");



                                }
                                else{
                                    Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                    configSomeProblem();
                                }
                            }
                            else {
                                configGetData();

                                //Save crafts of craftsman
                                Gson gson = new Gson();
                                Log.d("crafts_data",gson.toJson(listResultModel.getResponse().getResult()));

                                List<CraftModel> craftsmanCraftsList = listResultModel.getResponse().getResult();

                                for (int i = 0; i < craftsmanCraftsList.size(); i++) {
                                    try {
                                        craftsmanCraftsList.get(i).setName(AES.decrypt(craftsmanCraftsList.get(i).getName()));
                                        craftsmanCraftsList.get(i).setId(AES.decrypt(craftsmanCraftsList.get(i).getId()));
                                    }
                                    catch (Exception e) {
                                        Toast.makeText(getActivity(), "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                MutableLiveData<List<CraftModel>> craftsmanImage = new MutableLiveData<>();
                                craftsmanImage.setValue(craftsmanCraftsList);

                                model.setCraftAdapter(new CraftAdapter(getActivity() , craftsmanImage , 0 , false));


                                binding.recyclerCraftWorker.setHasFixedSize(true);
                                binding.recyclerCraftWorker.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.HORIZONTAL , false));
                                binding.recyclerCraftWorker.setAdapter(new CraftAdapter(getActivity() , craftsmanImage , 0 , false));
                                startRecyclerViewAnimation(binding.recyclerCraftWorker);

                                Log.d("is_has_images" , model.getCraftAdapter().getItemCount() > 0 ? "FULL" : "NULL");
                            }
                        }

                    }, throwable -> {
                        configSomeProblem();
                        Log.d("error_request_prf_crfts" , throwable.getMessage());
                    })

            );


        }
        catch (Exception e){
            e.printStackTrace();
            configSomeProblem();
            Toast.makeText(getActivity(), "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCraftsmanImages() {

       try {
           String token = Hawk.get("TOKEN");
           Log.d("is_token",token +"");

           new CompositeDisposable().add(
                   api.craftsmanImages(
                           "Bearer " + token,
                           Common.craftmanData.getId()
                   )
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(listResultModel -> {

                       if (listResultModel != null){
                           if (!listResultModel.getErrorMessage().isEmpty()){

                               try {
                                   String error = AES.decrypt(listResultModel.getErrorMessage());
                                   Log.d("error_message_here" , error);
                                   Toasty.error(getActivity(), error, Toast.LENGTH_SHORT , true).show();

                                   configSomeProblem();

                               }
                               catch (Exception e) {
                                   e.printStackTrace();
                                   configSomeProblem();
                               }

                           }
                           else if (listResultModel.getResponse().getResult().size() <= 0){
                               configNoData();
                           }
                           else if (!listResultModel.getResponse().getToken().isEmpty()){
                               if (Common.checkSecureToken(listResultModel.getResponse().getToken())){

                                   configGetData();

                                   //Save the token
                                   Hawk.put("TOKEN" , listResultModel.getResponse().getToken());

                                   //Save images of craftsman
                                   Log.d("size_images" , String.valueOf(listResultModel.getResponse().getResult().size()));
                                   Log.d("size_images_data" , new Gson().toJson(listResultModel.getResponse().getResult()));

                                   List<ProfileImageItemModel> craftsmanImagesList = listResultModel.getResponse().getResult();

                                   for (int i = 0; i < craftsmanImagesList.size(); i++) {
                                       try {
                                           Log.d("show_image" , craftsmanImagesList.get(i).getImage() != null ? AES.decrypt(craftsmanImagesList.get(i).getImage()) : "....");
                                           craftsmanImagesList.get(i).setImage(AES.decrypt(craftsmanImagesList.get(i).getImage()));
                                       }
                                       catch (Exception e) {
                                           Toast.makeText(getActivity(), "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                           e.printStackTrace();
                                       }
                                   }


//                           MutableLiveData<List<ProfileImageItemModel>> craftsmanImage = new MutableLiveData<>();
//                           craftsmanImage.setValue(craftsmanImagesList);

                                   model.setImageAdapter(new ProfileImageAdapter(getActivity() , craftsmanImagesList));

                                   Log.d("is_has_images" , model.getImageAdapter().getItemCount() > 0 ? "FULL" : "NULL");

                                   binding.recyclerImgWorker.setHasFixedSize(true);
                                   binding.recyclerImgWorker.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.HORIZONTAL , false));
                                   binding.recyclerImgWorker.setAdapter(new ProfileImageAdapter(getActivity() , craftsmanImagesList));
                                   startRecyclerViewAnimation(binding.recyclerImgWorker);

//                           fetchCraftsmanCrafts();

                               }
                               else{
                                   configSomeProblem();
                                   Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                               }
                           }
                           else {
                               configGetData();

                               //Save images of craftsman
                               Log.d("size_images" , String.valueOf(listResultModel.getResponse().getResult().size()));
                               Log.d("size_images_data" , new Gson().toJson(listResultModel.getResponse().getResult()));

                               List<ProfileImageItemModel> craftsmanImagesList = listResultModel.getResponse().getResult();

                               for (int i = 0; i < craftsmanImagesList.size(); i++) {
                                   try {
                                       Log.d("show_image" , craftsmanImagesList.get(i).getImage() != null ? AES.decrypt(craftsmanImagesList.get(i).getImage()) : "....");
                                       craftsmanImagesList.get(i).setImage(AES.decrypt(craftsmanImagesList.get(i).getImage()));
                                   }
                                   catch (Exception e) {
                                       Toast.makeText(getActivity(), "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                       e.printStackTrace();
                                   }
                               }


//                           MutableLiveData<List<ProfileImageItemModel>> craftsmanImage = new MutableLiveData<>();
//                           craftsmanImage.setValue(craftsmanImagesList);

                               model.setImageAdapter(new ProfileImageAdapter(getActivity() , craftsmanImagesList));

                               Log.d("is_has_images" , model.getImageAdapter().getItemCount() > 0 ? "FULL" : "NULL");

                               binding.recyclerImgWorker.setHasFixedSize(true);
                               binding.recyclerImgWorker.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.HORIZONTAL , false));
                               binding.recyclerImgWorker.setAdapter(new ProfileImageAdapter(getActivity() , craftsmanImagesList));
                               startRecyclerViewAnimation(binding.recyclerImgWorker);

//                       fetchCraftsmanCrafts();

                           }
                       }

                   }, throwable -> {
                       configSomeProblem();
                       Log.d("error_request_pof_img" , throwable.getMessage());
                   })

           );

       }
       catch (Exception e){
           configSomeProblem();
           e.printStackTrace();
           Toast.makeText(getActivity(), "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
       }
    }

    private void fetchCraftsmanData() {

        try {
            String token = Hawk.get("TOKEN");
            Log.d("is_token",token +"");

            new CompositeDisposable().add(
                    api.craftsmanProfile(
                            "Bearer " + token,
                            Common.craftmanData.getId()
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(profileItemModelResultModel -> {

                        if (profileItemModelResultModel != null){
                            if (!profileItemModelResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String error = AES.decrypt(profileItemModelResultModel.getErrorMessage());
                                    Log.d("error_message_here" , error);
                                    Toasty.error(getActivity(), error, Toast.LENGTH_SHORT , true).show();
                                    configSomeProblem();

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                    configSomeProblem();
                                }

                            }
                            else if (profileItemModelResultModel.getResponse().getResult().getCityName().isEmpty()){
                                configNoData();
                            }
                            else if (!profileItemModelResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(profileItemModelResultModel.getResponse().getToken())){

                                    configGetData();

                                    //Save the token
                                    Hawk.put("TOKEN" , profileItemModelResultModel.getResponse().getToken());

                                    //Save data of craftsman
                                    ProfileItemModel profileItemModel;
                                    ProfileItemModel currentItemModel = new ProfileItemModel();

                                    profileItemModel = profileItemModelResultModel.getResponse().getResult();

                                    Log.d("email_error", profileItemModelResultModel.getResponse().getResult().getDescription() != null ? "OK" : "NO");

                                    try {
                                        currentItemModel.setDescription(AES.decrypt(profileItemModel.getDescription()));
                                        currentItemModel.setCityName(AES.decrypt(profileItemModel.getCityName()));
                                        currentItemModel.setDateJoin(AES.decrypt(profileItemModel.getDateJoin()));
//                           currentItemModel.setEmail(AES.decrypt(profileItemModel.getEmail()));
                                        currentItemModel.setHighestCost(AES.decrypt(profileItemModel.getHighestCost()));
                                        currentItemModel.setLowestCost(AES.decrypt(profileItemModel.getLowestCost()));
                                        currentItemModel.setName(AES.decrypt(profileItemModel.getName()));
                                        currentItemModel.setIdentityNum(AES.decrypt(profileItemModel.getIdentityNum()));
                                        currentItemModel.setLat(AES.decrypt(profileItemModel.getLat()));
                                        currentItemModel.setLng(AES.decrypt(profileItemModel.getLng()));
                                        currentItemModel.setPhoneNum(AES.decrypt(profileItemModel.getPhoneNum()));
                                        currentItemModel.setLevelCraftman(AES.decrypt(profileItemModel.getLevelCraftman()));
                                        currentItemModel.setPhoto(AES.decrypt(profileItemModel.getPhoto()));
                                        currentItemModel.setSizeQueue(AES.decrypt(profileItemModel.getSizeQueue()));
                                        currentItemModel.setStatusCraftman(AES.decrypt(profileItemModel.getStatusCraftman()));

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                        configSomeProblem();
                                    }

                                    Log.d("craftsman_data" , new Gson().toJson(currentItemModel));
                                    Log.d("craftsman_data_size" , currentItemModel.getName());

                                    MutableLiveData<ProfileItemModel> profileItemModelMutableLiveData = new MutableLiveData<>();
                                    profileItemModelMutableLiveData.setValue(currentItemModel);

                                    model.setProfileItemModelLiveData(profileItemModelMutableLiveData);

//                        Log.d("is_has_data" , model.getProfileItemModelLiveData().getValue() != null ? "FULL" : "NULL");

                                    model.getProfileItemModelLiveData().observe(getActivity(), profileItemModel1 -> {

                                        Log.d("data_photo" , Common.BASE_PHOTO + "craftmen/" + currentItemModel.getPhoto());
                                        Glide.with(getActivity()).load(Common.BASE_PHOTO + "craftmen/" + currentItemModel.getPhoto()).into(binding.imgProfile);
                                        binding.txtCity.setText(profileItemModel1.getCityName());
                                        binding.txtDateJoin.setText(profileItemModel1.getDateJoin());
                                        binding.txtDescription.setText(profileItemModel1.getDescription());
                                        binding.txtEmail.setText(profileItemModel1.getEmail());
                                        binding.txtIdentityNumber.setText(profileItemModel1.getIdentityNum());
                                        binding.txtLatLng.setText(profileItemModel1.getLat() + " ~ " + profileItemModel1.getLng());
                                        binding.txtLevel.setText(profileItemModel1.getLevelCraftman());
                                        binding.txtLowCost.setText(profileItemModel1.getLowestCost());
                                        binding.txtLowHighCost.setText(profileItemModel1.getHighestCost());
                                        binding.txtName.setText(profileItemModel1.getName());
                                        binding.txtPhone.setText(profileItemModel1.getPhoneNum());
                                        binding.txtSizeQueue.setText(profileItemModel1.getSizeQueue());

                                        //profileItemModel1.getStatusCraftman()  for SegementedGroupButton

                                    });


//                            fetchCraftsmanImages();
                                }
                                else{
                                    Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                    configSomeProblem();
                                }
                            }
                            else {

                                configGetData();

                                //Save data of craftsman
                                ProfileItemModel profileItemModel;
                                ProfileItemModel currentItemModel = new ProfileItemModel();

                                profileItemModel = profileItemModelResultModel.getResponse().getResult();

                                Log.d("email_error", profileItemModelResultModel.getResponse().getResult().getDescription() != null ? "OK" : "NO");

                                try {
                                    currentItemModel.setDescription(AES.decrypt(profileItemModel.getDescription()));
                                    currentItemModel.setCityName(AES.decrypt(profileItemModel.getCityName()));
                                    currentItemModel.setDateJoin(AES.decrypt(profileItemModel.getDateJoin()));
//                           currentItemModel.setEmail(AES.decrypt(profileItemModel.getEmail()));
                                    currentItemModel.setHighestCost(AES.decrypt(profileItemModel.getHighestCost()));
                                    currentItemModel.setLowestCost(AES.decrypt(profileItemModel.getLowestCost()));
                                    currentItemModel.setName(AES.decrypt(profileItemModel.getName()));
                                    currentItemModel.setIdentityNum(AES.decrypt(profileItemModel.getIdentityNum()));
                                    currentItemModel.setLat(AES.decrypt(profileItemModel.getLat()));
                                    currentItemModel.setLng(AES.decrypt(profileItemModel.getLng()));
                                    currentItemModel.setPhoneNum(AES.decrypt(profileItemModel.getPhoneNum()));
                                    currentItemModel.setLevelCraftman(AES.decrypt(profileItemModel.getLevelCraftman()));
                                    currentItemModel.setPhoto(AES.decrypt(profileItemModel.getPhoto()));
                                    currentItemModel.setSizeQueue(AES.decrypt(profileItemModel.getSizeQueue()));
                                    currentItemModel.setStatusCraftman(AES.decrypt(profileItemModel.getStatusCraftman()));

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                    configSomeProblem();
                                }

                                Log.d("craftsman_data" , new Gson().toJson(currentItemModel));
                                Log.d("craftsman_data_size" , currentItemModel.getName());

                                MutableLiveData<ProfileItemModel> profileItemModelMutableLiveData = new MutableLiveData<>();
                                profileItemModelMutableLiveData.setValue(currentItemModel);

                                model.setProfileItemModelLiveData(profileItemModelMutableLiveData);

//                        Log.d("is_has_data" , model.getProfileItemModelLiveData().getValue() != null ? "FULL" : "NULL");

                                model.getProfileItemModelLiveData().observe(getActivity(), profileItemModel1 -> {

                                    Log.d("data_photo" , Common.BASE_PHOTO + "craftmen/" + currentItemModel.getPhoto());
                                    Glide.with(getActivity()).load(Common.BASE_PHOTO + "craftmen/" + currentItemModel.getPhoto()).into(binding.imgProfile);
                                    binding.txtCity.setText(profileItemModel1.getCityName());
                                    binding.txtDateJoin.setText(profileItemModel1.getDateJoin());
                                    binding.txtDescription.setText(profileItemModel1.getDescription());
                                    binding.txtEmail.setText(profileItemModel1.getEmail());
                                    binding.txtIdentityNumber.setText(profileItemModel1.getIdentityNum());
                                    binding.txtLatLng.setText(profileItemModel1.getLat() + " ~ " + profileItemModel1.getLng());
                                    binding.txtLevel.setText(profileItemModel1.getLevelCraftman());
                                    binding.txtLowCost.setText(profileItemModel1.getLowestCost());
                                    binding.txtLowHighCost.setText(profileItemModel1.getHighestCost());
                                    binding.txtName.setText(profileItemModel1.getName());
                                    binding.txtPhone.setText(profileItemModel1.getPhoneNum());
                                    binding.txtSizeQueue.setText(profileItemModel1.getSizeQueue());

                                    //profileItemModel1.getStatusCraftman()  for SegementedGroupButton

                                });


//                        fetchCraftsmanImages();

                            }
                        }

                    }, throwable -> {
                        Log.d("error_request_profile" , throwable.getMessage());
                        configSomeProblem();
                    })

            );

        }
        catch (Exception e){
            e.printStackTrace();
            configSomeProblem();
            Toast.makeText(getActivity(), "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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