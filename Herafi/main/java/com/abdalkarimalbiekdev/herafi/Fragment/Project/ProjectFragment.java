package com.abdalkarimalbiekdev.herafi.Fragment.Project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import com.abdalkarimalbiekdev.herafi.Adapter.ProfileImageAdapter;
import com.abdalkarimalbiekdev.herafi.Adapter.ProjectAdapter;
import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.ProjectListener;
import com.abdalkarimalbiekdev.herafi.NavigatorFragment;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.SignIn;
import com.abdalkarimalbiekdev.herafi.SignUp;
import com.abdalkarimalbiekdev.herafi.model.ProfileModel;
import com.abdalkarimalbiekdev.herafi.model.ProjectModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ProjectItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.abdalkarimalbiekdev.herafi.viewModel.ProjectViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.FragmentProjectBinding;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

public class ProjectFragment extends Fragment implements ProjectListener {

    private ProjectViewModel viewModel;
    private FragmentProjectBinding binding;
    HerafiAPI api;

    ProjectAdapter adapter;

    boolean isFinish , hasData;
    int start = 0 , end = 3;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_project , container , false);

        api = Common.getAPI();
        Hawk.init(getActivity()).build();

        viewModel = new ViewModelProvider(getActivity()).get(ProjectViewModel.class);

        viewModel.setListener(this);

        binding.setProjectBinding(viewModel);


        boolean dark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        binding.swipeProject.setProgressBackgroundColorSchemeResource(dark? R.color.black_item : R.color.white_item);
        binding.swipeProject.setColorSchemeColors(
                getResources().getColor(R.color.primary_color),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark)
        );
        binding.swipeProject.setOnRefreshListener(() -> {

            if (Common.isConnectionToInternet(getActivity())){

                start = 0;
                hasData = false;
                isFinish = false;

                ProjectAdapter adapter = (ProjectAdapter) binding.recyclerProjects.getAdapter();
                if (adapter != null){
                    adapter.getList().clear();
                    adapter.notifyDataSetChanged();
                }

                loadData(binding.recyclerProjects);
                binding.swipeProject.setRefreshing(false);
            }
            else {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
                configNoInternet();
                binding.swipeProject.setRefreshing(false);
                return;
            }

        });



        binding.nestedScrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (nestedScrollView, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                    if (scrollY == nestedScrollView.getChildAt(0).getMeasuredHeight() - nestedScrollView.getMeasuredHeight()){
                        //Load More
                        if (!isFinish){

                            binding.progressMore.setVisibility(View.VISIBLE);

                            if (Common.isConnectionToInternet(getActivity())){

                                loadData(binding.recyclerProjects);
                                binding.swipeProject.setRefreshing(false);
                            }
                            else {
                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
                                configNoInternet();
                                binding.swipeProject.setRefreshing(false);
                                return;
                            }

                        }
                    }
        });


        return binding.getRoot();
    }

    private void configNoData() {
//        binding.imgBackground.setImageResource(R.drawable.no_data);
//        binding.imgBackground.setVisibility(View.VISIBLE);
//        binding.textBackground.setText(getResources().getString(R.string.no_projects));
//        binding.textBackground.setVisibility(View.VISIBLE);
//        binding.progressBackground.setVisibility(View.GONE);
//        binding.recyclerProjects.setVisibility(View.GONE);

        if (!hasData){
            binding.imgBackground.setImageResource(R.drawable.no_data);
            binding.imgBackground.setVisibility(View.VISIBLE);

            binding.textBackground.setText(getActivity().getResources().getString(R.string.no_projects));
            binding.textBackground.setVisibility(View.VISIBLE);

            binding.progressBackground.setVisibility(View.GONE);
            binding.recyclerProjects.setVisibility(View.GONE);

            binding.nestedScrollView.setVisibility(View.GONE);
            binding.progressMore.setVisibility(View.GONE);
        }
        else {
            binding.imgBackground.setVisibility(View.GONE);
            binding.textBackground.setVisibility(View.GONE);

            binding.progressBackground.setVisibility(View.GONE);
            binding.recyclerProjects.setVisibility(View.VISIBLE);

            binding.nestedScrollView.setVisibility(View.VISIBLE);
            binding.progressMore.setVisibility(View.GONE);
        }

    }

    private void configGetData() {
        binding.imgBackground.setVisibility(View.GONE);
        binding.textBackground.setVisibility(View.GONE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.recyclerProjects.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.VISIBLE);
        binding.progressMore.setVisibility(View.GONE);
    }

    private void configSomeProblem() {
//        binding.imgBackground.setImageResource(R.drawable.some_problem);
//        binding.imgBackground.setVisibility(View.VISIBLE);
//        binding.textBackground.setText(getResources().getString(R.string.some_problem));
//        binding.textBackground.setVisibility(View.VISIBLE);
//        binding.progressBackground.setVisibility(View.GONE);
//        binding.recyclerProjects.setVisibility(View.GONE);

        if (!hasData){
            binding.imgBackground.setImageResource(R.drawable.some_problem);
            binding.imgBackground.setVisibility(View.VISIBLE);

            binding.textBackground.setText(getActivity().getResources().getString(R.string.some_problem));
            binding.textBackground.setVisibility(View.VISIBLE);

            binding.progressBackground.setVisibility(View.GONE);
            binding.recyclerProjects.setVisibility(View.GONE);

            binding.nestedScrollView.setVisibility(View.GONE);
            binding.progressMore.setVisibility(View.GONE);
        }
        else {
            binding.imgBackground.setVisibility(View.GONE);
            binding.textBackground.setVisibility(View.GONE);

            binding.progressBackground.setVisibility(View.GONE);
            binding.recyclerProjects.setVisibility(View.VISIBLE);

            binding.nestedScrollView.setVisibility(View.VISIBLE);
            binding.progressMore.setVisibility(View.GONE);
        }

    }

    private void configNoInternet() {
//        binding.imgBackground.setImageResource(R.drawable.no_internet);
//        binding.imgBackground.setVisibility(View.VISIBLE);
//        binding.textBackground.setText(getResources().getString(R.string.you_need_internet));
//        binding.textBackground.setVisibility(View.VISIBLE);
//        binding.progressBackground.setVisibility(View.GONE);
//        binding.recyclerProjects.setVisibility(View.GONE);

        if (!hasData){
            binding.imgBackground.setImageResource(R.drawable.no_internet);
            binding.imgBackground.setVisibility(View.VISIBLE);

            binding.textBackground.setText(getActivity().getResources().getString(R.string.you_need_internet));
            binding.textBackground.setVisibility(View.VISIBLE);

            binding.progressBackground.setVisibility(View.GONE);
            binding.recyclerProjects.setVisibility(View.GONE);

            binding.nestedScrollView.setVisibility(View.GONE);
            binding.progressMore.setVisibility(View.GONE);
        }
        else {
            binding.imgBackground.setVisibility(View.GONE);
            binding.textBackground.setVisibility(View.GONE);

            binding.progressBackground.setVisibility(View.GONE);
            binding.recyclerProjects.setVisibility(View.VISIBLE);

            binding.nestedScrollView.setVisibility(View.VISIBLE);
            binding.progressMore.setVisibility(View.GONE);
        }
    }

    private void loadData(RecyclerView view) {

        if (Common.isConnectionToInternet(getActivity())){

            try {
                String token = Hawk.get("TOKEN");
                Log.d("is_token",token +"");

                String encStart = AES.encrypt(String.valueOf(start));
                String encEnd = AES.encrypt(String.valueOf(end));

                new CompositeDisposable().add(
                        api.getProjects(
                                "Bearer " + token,
                                Common.craftmanData.getId(),
                                encStart,
                                encEnd
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(listResultModel -> {

                            if (listResultModel != null){
                                if (!listResultModel.getErrorMessage().isEmpty()){

                                    try {
                                        String error = AES.decrypt(listResultModel.getErrorMessage());
                                        Log.d("error_message_here" ,error);
                                        Toasty.error(getActivity(), error, Toast.LENGTH_SHORT , true).show();
                                        configSomeProblem();

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                        configSomeProblem();
                                    }

                                }
                                else if (listResultModel.getResponse().getResult().size() <= 0){

                                    if (hasData){
                                        isFinish = true;
                                    }
                                    else {
                                        configNoData();
                                    }

                                    binding.progressMore.setVisibility(View.GONE);

                                }
                                else if (!listResultModel.getResponse().getToken().isEmpty()){
                                    if (Common.checkSecureToken(listResultModel.getResponse().getToken())){

                                        configGetData();

                                        hasData = true;
                                        start += end;
                                        binding.progressMore.setVisibility(View.GONE);

                                        //Save the token
                                        Hawk.put("TOKEN" , listResultModel.getResponse().getToken());
                                        //Add it to adapter

                                        List<ProjectItemModel> periodList = listResultModel.getResponse().getResult();

                                        for (int i = 0; i < periodList.size(); i++) {
                                            periodList.get(i).setProjectId(AES.decrypt(periodList.get(i).getProjectId()));
                                            periodList.get(i).setUserId(AES.decrypt(periodList.get(i).getUserId()));
                                            periodList.get(i).setUserPhoto(AES.decrypt(periodList.get(i).getUserPhoto()));
                                            periodList.get(i).setProjectPhoto(AES.decrypt(periodList.get(i).getProjectPhoto()));
                                            periodList.get(i).setProjectName(AES.decrypt(periodList.get(i).getProjectName()));
                                            periodList.get(i).setTotalCost(AES.decrypt(periodList.get(i).getTotalCost()));
                                            periodList.get(i).setTotalRate(AES.decrypt(periodList.get(i).getTotalRate()));
                                        }



                                        if (adapter == null){
                                            Log.d("first_time" , "OK");
                                            adapter = new ProjectAdapter(getActivity() , periodList);
                                            view.setAdapter(adapter);
                                            startRecyclerViewAnimation(view);
                                        }

                                        else{
                                            Log.d("second_time" , "OK");
                                            List<ProjectItemModel> projectItemModels = new ArrayList<>();
                                            projectItemModels.addAll(adapter.getList());
                                            projectItemModels.addAll(periodList);

                                            adapter.updateList(projectItemModels);

                                            //view.setAdapter(adapter);
                                            view.smoothScrollToPosition(projectItemModels.size() - listResultModel.getResponse().getResult().size() + 1 );
                                        }

//                                        viewModel.setActivity(getActivity() , projectModel);

//                                viewModel.getLiveData().observe(getActivity(), projectModel1 -> {
//                                    if (projectModel1.getAdapter() == null)
//                                        view.setAdapter(new ProjectAdapter(getActivity() , new MutableLiveData<>()));
//
//                                    view.setAdapter(projectModel1.getAdapter());
//                                });

                                    }
                                    else{
                                        Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                        configSomeProblem();
                                    }

                                }
                                else {
                                    configGetData();

                                    hasData = true;
                                    start += end;
                                    binding.progressMore.setVisibility(View.GONE);


                                    List<ProjectItemModel> periodList = listResultModel.getResponse().getResult();
                                    for (int i = 0; i < periodList.size(); i++) {
                                        periodList.get(i).setProjectId(AES.decrypt(periodList.get(i).getProjectId()));
                                        periodList.get(i).setUserId(AES.decrypt(periodList.get(i).getUserId()));
                                        periodList.get(i).setUserPhoto(AES.decrypt(periodList.get(i).getUserPhoto()));
                                        periodList.get(i).setProjectPhoto(AES.decrypt(periodList.get(i).getProjectPhoto()));
                                        periodList.get(i).setProjectName(AES.decrypt(periodList.get(i).getProjectName()));
                                        periodList.get(i).setTotalCost(AES.decrypt(periodList.get(i).getTotalCost()));
                                        periodList.get(i).setTotalRate(AES.decrypt(periodList.get(i).getTotalRate()));
                                    }

                                    //Add it to adapter
                                    MutableLiveData<List<ProjectItemModel>> liveData = new MutableLiveData<>();

                                    liveData.setValue(listResultModel.getResponse().getResult());

                                    ProjectModel model = new ProjectModel();
                                    model.setAdapter(new ProjectAdapter(getActivity() , listResultModel.getResponse().getResult()));

                                    MutableLiveData<ProjectModel> projectModel = new MutableLiveData<>();
                                    projectModel.setValue(model);

//                                    view.setAdapter(model.getAdapter());

                                    if (adapter == null){
                                        Log.d("first_time" , "OK");
                                        adapter = new ProjectAdapter(getActivity() , periodList);
                                        view.setAdapter(adapter);
                                        startRecyclerViewAnimation(view);
                                    }

                                    else{
                                        Log.d("second_time" , "OK");
                                        List<ProjectItemModel> projectItemModels = new ArrayList<>();
                                        projectItemModels.addAll(adapter.getList());
                                        projectItemModels.addAll(periodList);

                                        adapter.updateList(projectItemModels);

                                        //view.setAdapter(adapter);
                                        view.smoothScrollToPosition(projectItemModels.size() - listResultModel.getResponse().getResult().size() + 1 );
                                    }

                                    viewModel.setActivity(getActivity() , projectModel);

                                }
                            }

                        },
                                throwable -> {
                            Log.d("error_request_projects" , throwable.getMessage());
                            configSomeProblem();
                        })

                );

            }
            catch (Exception e) {
                e.printStackTrace();
                configSomeProblem();
            }

        }
        else{
            Toasty.info(getActivity(), getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();
            configNoInternet();
        }

    }

    private void startRecyclerViewAnimation(RecyclerView recyclerCraft) {

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

    @Override
    public void onAdapterHandler(RecyclerView view) {

        if (Common.isConnectionToInternet(getActivity())){

            view.setHasFixedSize(true);
            view.setLayoutManager(new LinearLayoutManager(getActivity()));
            loadData(view);
        }
        else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
            configNoInternet();
            return;
        }

    }
}