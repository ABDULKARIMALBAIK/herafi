package com.abdalkarimalbiekdev.herafi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Adapter.ProfileImageAdapter;
import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.ProjectDetailsListener;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.model.ProjectDetailsModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ProfileImageItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ProjectDetailsItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.abdalkarimalbiekdev.herafi.viewModel.ProjectDetailsViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.ActivityProjectDetailsBinding;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProjectDetails extends AppCompatActivity {

    private ActivityProjectDetailsBinding binding;
    private ProjectDetailsViewModel viewModel;
    private ProjectDetailsModel model;

    HerafiAPI api;
    String encProjectId;
    String token;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/teko.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        api = Common.getAPI();
        Hawk.init(this).build();

        //Setup Bottom Buttons
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //Change soft-key Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setNavigationBarColor(getResources().getColor(R.color.black_item));
            }
        }

        try {

            if (getIntent().getExtras() != null){
                encProjectId = AES.encrypt(getIntent().getExtras().getString("projectId"));
                token = getIntent().getExtras().getString("token");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        binding = DataBindingUtil.setContentView(this , R.layout.activity_project_details);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(ProjectDetailsViewModel.class);
        model = new ProjectDetailsModel();

        if (Common.isConnectionToInternet(this)){
            loadData();
        }
        else{
            configNoInternet();
            Toasty.info(this, getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();
        }


        boolean dark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        binding.swipeRefreshProjectDetails.setProgressBackgroundColorSchemeResource(dark? R.color.black_item : R.color.white_item);
        binding.swipeRefreshProjectDetails.setColorSchemeColors(
                getResources().getColor(R.color.primary_color),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark)
        );
        binding.swipeRefreshProjectDetails.setOnRefreshListener(() -> {

            if (Common.isConnectionToInternet(ProjectDetails.this)){

                loadData();
                binding.swipeRefreshProjectDetails.setRefreshing(false);
            }
            else {
                Toast.makeText(ProjectDetails.this, this.getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
                configNoInternet();
                binding.swipeRefreshProjectDetails.setRefreshing(false);
                return;
            }

        });

    }

    private void configNoData() {
        binding.imgBackground.setImageResource(R.drawable.no_data);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getResources().getString(R.string.no_data_to_show));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.scrollView.setVisibility(View.GONE);
    }

    private void configGetData() {
        binding.imgBackground.setVisibility(View.GONE);
        binding.textBackground.setVisibility(View.GONE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.scrollView.setVisibility(View.VISIBLE);
    }

    private void configSomeProblem() {
        binding.imgBackground.setImageResource(R.drawable.some_problem);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getResources().getString(R.string.some_problem));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.scrollView.setVisibility(View.GONE);
    }

    private void configNoInternet() {
        binding.imgBackground.setImageResource(R.drawable.no_internet);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getResources().getString(R.string.you_need_internet));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.scrollView.setVisibility(View.GONE);
    }


    private void loadData() {

        //Create Token
        Log.d("pass0Here" , "OK");
        try {
            createToken();
        }
        catch (Exception e) {
            configSomeProblem();
            e.printStackTrace();
        }

    }

    private String createToken() throws Exception {

        Log.d("craftsman_id" , Common.craftmanData.getId());

        new CompositeDisposable().add(
                api.getAccessToken(Common.craftmanData.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringResultModel -> {

                    if (stringResultModel.getResponse().getToken() != null){

                        Log.d("full_token" , stringResultModel.getResponse().getToken());
                        Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());
                        token = stringResultModel.getResponse().getToken();



                        //Load data of project
                        Log.d("pass1Here" , "OK");
                        fetchProjectData();

                    }
                    else {
                        configSomeProblem();
                        Log.d("null_token" , stringResultModel.getResponse().getToken());
                    }
                },
                        throwable -> {
                    Log.d("error_token" , throwable.getMessage());
                    configSomeProblem();
                })
        );

        return token;

    }

    private void fetchProjectImages() {

        try {
            new CompositeDisposable().add(
                    api.projectDetailsImage(
                            "Bearer " + token,
                            encProjectId,
                            Common.craftmanData.getId()
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listResultModel -> {

                        if (listResultModel != null){
                            if (!listResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String error = AES.decrypt(listResultModel.getErrorMessage());
                                    Log.d("error_message_here" ,error);
                                    Toasty.error(ProjectDetails.this, error, Toast.LENGTH_SHORT , true).show();
                                    configSomeProblem();
                                }
                                catch (Exception e) {
                                    configSomeProblem();
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

                                    //Save data of craftsman
                                    Log.d("project_images" , new Gson().toJson(listResultModel.getResponse().getResult()));

                                    List<ProfileImageItemModel> projectImageList = listResultModel.getResponse().getResult();

                                    for (int i = 0; i < projectImageList.size(); i++) {
                                        try {
                                            projectImageList.get(i).setImage(AES.decrypt(projectImageList.get(i).getImage()));
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                            configSomeProblem();
                                            Toast.makeText(ProjectDetails.this, "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    Log.d("project_images_decrypte" , new Gson().toJson(projectImageList));

                                    binding.recyclerProjectPhotos.setHasFixedSize(true);
                                    binding.recyclerProjectPhotos.setLayoutManager(new LinearLayoutManager(ProjectDetails.this , LinearLayoutManager.HORIZONTAL , false));
                                    binding.recyclerProjectPhotos.setAdapter(new ProfileImageAdapter(ProjectDetails.this , projectImageList));
                                    startRecyclerViewAnimation(binding.recyclerProjectPhotos);


                                    Log.d("final_step" , "OK");
                                    viewModel.setActivity(ProjectDetails.this , model);
                                    viewModel.setListener(new ProjectDetailsListener() {
                                        @Override
                                        public void onClickPhoneNumber(View view) {
                                            Dexter.withActivity(ProjectDetails.this)
                                                    .withPermission(Manifest.permission.CALL_PHONE)
                                                    .withListener(new PermissionListener() {
                                                        @Override
                                                        public void onPermissionGranted(PermissionGrantedResponse response) {

                                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                                            intent.setData(Uri.parse("tel:" + model.getUserPhoneNumber()));
                                                            if (ActivityCompat.checkSelfPermission(ProjectDetails.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                                                return;
                                                            }
                                                            startActivity(intent);

                                                        }

                                                        @Override
                                                        public void onPermissionDenied(PermissionDeniedResponse response) {
                                                            Toasty.error(ProjectDetails.this , getResources().getString(R.string.request_toast_noCallPermission) , Toasty.LENGTH_SHORT , true).show();
                                                        }

                                                        @Override
                                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                                        }
                                                    })
                                                    .check();
                                        }

                                        @Override
                                        public void onClickAddPhoto(View view) {
                                            //No thing now !!!
                                        }
                                    });

                                    binding.setProjectDetailsBinding(viewModel);

                                }
                                else {
                                    Toasty.error(ProjectDetails.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                             }
                                    configSomeProblem();
                            }
                            else {

                                configGetData();

                                //Save data of craftsman
                                Log.d("project_images" , new Gson().toJson(listResultModel.getResponse().getResult()));

                                List<ProfileImageItemModel> projectImageList = listResultModel.getResponse().getResult();

                                for (int i = 0; i < projectImageList.size(); i++) {
                                    try {
                                        projectImageList.get(i).setImage(AES.decrypt(projectImageList.get(i).getImage()));
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                        configSomeProblem();
                                        Toast.makeText(ProjectDetails.this, "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                Log.d("project_images_decrypte" , new Gson().toJson(projectImageList));

                                binding.recyclerProjectPhotos.setHasFixedSize(true);
                                binding.recyclerProjectPhotos.setLayoutManager(new LinearLayoutManager(ProjectDetails.this , LinearLayoutManager.HORIZONTAL , false));
                                binding.recyclerProjectPhotos.setAdapter(new ProfileImageAdapter(ProjectDetails.this , projectImageList));
                                startRecyclerViewAnimation(binding.recyclerProjectPhotos);


                                Log.d("final_step" , "OK");
                                viewModel.setActivity(ProjectDetails.this , model);
                                viewModel.setListener(new ProjectDetailsListener() {
                                    @Override
                                    public void onClickPhoneNumber(View view) {
                                        Dexter.withActivity(ProjectDetails.this)
                                                .withPermission(Manifest.permission.CALL_PHONE)
                                                .withListener(new PermissionListener() {
                                                    @Override
                                                    public void onPermissionGranted(PermissionGrantedResponse response) {

                                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                                        intent.setData(Uri.parse("tel:" + model.getUserPhoneNumber()));
                                                        if (ActivityCompat.checkSelfPermission(ProjectDetails.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                                            return;
                                                        }
                                                        startActivity(intent);

                                                    }

                                                    @Override
                                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                                        Toasty.error(ProjectDetails.this , getResources().getString(R.string.request_toast_noCallPermission) , Toasty.LENGTH_SHORT , true).show();
                                                    }

                                                    @Override
                                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                                    }
                                                })
                                                .check();
                                    }

                                    @Override
                                    public void onClickAddPhoto(View view) {
                                        //No thing now !!!
                                    }
                                });

                                binding.setProjectDetailsBinding(viewModel);


                            }
                        }

                    },
                            throwable -> {
                        Log.d("error_request_proImgs" , throwable.getMessage());
                        configSomeProblem();
                    })
            );

        }
        catch (Exception e){
            e.printStackTrace();
            configSomeProblem();
            Toast.makeText(this, "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchProjectData() {

        try {
            Log.d("test_project_id" , encProjectId);
            Log.d("test_craftsman_id" , Common.craftmanData.getId());
            Log.d("token_id" ,  Hawk.get("TOKEN") != null ? "OK _ " + Hawk.get("TOKEN") : "No");
            Log.d("token_intent" , token != null ? "OK  " + token : "No");

            new CompositeDisposable().add(
                    api.projectDetailsData(
                            "Bearer " + token,
                            encProjectId,
                            Common.craftmanData.getId()
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(projectDetailsItemModelResultModel -> {

                        if (projectDetailsItemModelResultModel != null){
                            if (!projectDetailsItemModelResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String error = AES.decrypt(projectDetailsItemModelResultModel.getErrorMessage());
                                    Log.d("error_message_here" ,error);
                                    Toasty.error(this, error, Toast.LENGTH_SHORT , true).show();
                                    configSomeProblem();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                    configSomeProblem();
                                }

                            }
                            else if (projectDetailsItemModelResultModel.getResponse().getResult().getUserEmail().isEmpty()){
                                configNoData();
                            }
                            else if (!projectDetailsItemModelResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(projectDetailsItemModelResultModel.getResponse().getToken())){

                                    //Save the token
                                    Hawk.put("TOKEN" , projectDetailsItemModelResultModel.getResponse().getToken());

                                    //load data of project
                                    try {
                                        model.setUserId(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserId()));
                                        model.setUserName(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserName()));
                                        model.setUserEmail(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserEmail()));
                                        model.setUserImage(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserImage()));
                                        model.setUserPhoneNumber(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserPhoneNumber()));
                                        model.setProjectBanner(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getProjectBanner()));
                                        model.setProjectName(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getProjectName()));
                                        model.setProjectComment(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getProjectComment()));
                                        model.setProjectTotal_cost(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getProjectTotal_cost()));
                                        model.setProjectTotal_rate(AES.decrypt(String.valueOf(projectDetailsItemModelResultModel.getResponse().getResult().getProjectTotal_rate())));

                                        Log.d("user_image" , Common.BASE_PHOTO + "users/" + model.getUserImage());
                                        Picasso.get().load(Common.BASE_PHOTO + "users/" + model.getUserImage()).into( binding.imgUser);


                                        //load images of project
                                        Log.d("pass2Here" , "OK");
                                        fetchProjectImages();
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                        configSomeProblem();
                                        Toast.makeText(this, "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else{
                                    Toasty.error(this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                             }
                                    configSomeProblem();
                            }
                            else {
                                //load data of project
                                Log.d("data_project" , new Gson().toJson(projectDetailsItemModelResultModel.getResponse().getResult()));

                                try {
                                    model.setUserId(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserId()));
                                    model.setUserName(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserName()));
                                    model.setUserEmail(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserEmail()));
                                    model.setUserImage(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserImage()));
                                    model.setUserPhoneNumber(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getUserPhoneNumber()));
                                    model.setProjectBanner(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getProjectBanner()));
                                    model.setProjectName(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getProjectName()));
                                    model.setProjectComment(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getProjectComment()));
                                    model.setProjectTotal_cost(AES.decrypt(projectDetailsItemModelResultModel.getResponse().getResult().getProjectTotal_cost()));
                                    model.setProjectTotal_rate(AES.decrypt(String.valueOf(projectDetailsItemModelResultModel.getResponse().getResult().getProjectTotal_rate())));

                                    Log.d("user_image" , Common.BASE_PHOTO + "users/" + model.getUserImage());
                                    Picasso.get().load(Common.BASE_PHOTO + "users/" + model.getUserImage()).into(binding.imgUser);

                                    //load images of project
                                    Log.d("pass2Here" , "OK");
                                    fetchProjectImages();
                                }
                                catch (Exception e) {
                                    configSomeProblem();
                                    e.printStackTrace();
                                    Toast.makeText(this, "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    }, throwable -> {
                        configSomeProblem();
                        Log.d("error_request_proData" , throwable.getMessage());
                    })

            );

        }
        catch (Exception e){
            e.printStackTrace();
            configSomeProblem();
            Toast.makeText(this, "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT).show();
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