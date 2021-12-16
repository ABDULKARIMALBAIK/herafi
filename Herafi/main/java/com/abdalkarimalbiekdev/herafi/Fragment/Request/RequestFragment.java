package com.abdalkarimalbiekdev.herafi.Fragment.Request;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.abdalkarimalbiekdev.herafi.Adapter.ProjectAdapter;
import com.abdalkarimalbiekdev.herafi.Adapter.RequestAdapter;
import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.RecyclerItemClickListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.RequestListener;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.SignUp;
import com.abdalkarimalbiekdev.herafi.model.ProjectModel;
import com.abdalkarimalbiekdev.herafi.model.RequestItemModel;
import com.abdalkarimalbiekdev.herafi.model.RequestModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ProjectItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.abdalkarimalbiekdev.herafi.service.CheckNewRequests;
import com.abdalkarimalbiekdev.herafi.viewModel.RequestViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.FragmentRequestBinding;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.List;

public class RequestFragment extends Fragment implements RequestListener {

    private RequestViewModel viewModel;
    private FragmentRequestBinding binding;
    private RequestAdapter adapter;
    private HerafiAPI api;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        View root = inflater.inflate(R.layout.fragment_request, container, false);
        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_request , container , false);
//        binding.setLifecycleOwner(getActivity());

        api = Common.getAPI();
        Hawk.init(getActivity()).build();

        viewModel = new ViewModelProvider(getActivity()).get(RequestViewModel.class);

        viewModel.setListener(this);

        binding.setRequestBinding(viewModel);


        boolean dark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        binding.swipeRequest.setProgressBackgroundColorSchemeResource(dark? R.color.black_item : R.color.white_item);
        binding.swipeRequest.setColorSchemeColors(
                getResources().getColor(R.color.primary_color),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark)
        );
        binding.swipeRequest.setOnRefreshListener(() -> {

            if (Common.isConnectionToInternet(getActivity())){

                loadRequests(binding.recyclerRequest);
                binding.swipeRequest.setRefreshing(false);
            }
            else {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
                configNoInternet();
                binding.swipeRequest.setRefreshing(false);
                return;
            }

        });

        return binding.getRoot();
    }

    private void loadRequests(RecyclerView view) {

        try {
            String token = Hawk.get("TOKEN");
            Log.d("is_token",token +"");

            new CompositeDisposable().add(
                    api.getRequests(
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
                                        configNoData();
                                    }
                                    else if (!listResultModel.getResponse().getToken().isEmpty()){

                                        if (Common.checkSecureToken(listResultModel.getResponse().getToken())){

                                            configGetData();

                                            Common.lastNewBooking = listResultModel.getResponse().getResult().size();

                                            //Save the token
                                            Hawk.put("TOKEN" , listResultModel.getResponse().getToken());
                                            //Add it to adapter
                                            MutableLiveData<List<RequestItemModel>> liveData = new MutableLiveData<>();
                                            liveData.setValue(listResultModel.getResponse().getResult());

                                            RequestModel model = new RequestModel();
                                            model.setAdapter(new RequestAdapter(getActivity() , liveData , binding , viewModel));

                                            MutableLiveData<RequestModel> requestModel = new MutableLiveData<>();
                                            requestModel.setValue(model);

                                            view.setAdapter(model.getAdapter());
                                            startRecyclerViewCategoryAnimation(view);

                                            viewModel.setActivity(getActivity() , requestModel);



//                                viewModel.getRequestLiveData().observe(getActivity(), requestModel1 -> {
////                                    if (requestModel1.getAdapter() == null)
////                                        view.setAdapter(new RequestAdapter(getActivity() , new MutableLiveData<>()));
//
//                                    view.setAdapter(requestModel1.getAdapter());
//                                    startRecyclerViewCategoryAnimation(view);
//
//                                });
                                        }
                                        else{
                                            Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                            configSomeProblem();
                                        }

                                    }
                                    else {
                                        configGetData();

                                        Common.lastNewBooking = listResultModel.getResponse().getResult().size();

                                        //Add it to adapter
                                        MutableLiveData<List<RequestItemModel>> liveData = new MutableLiveData<>();
                                        liveData.setValue(listResultModel.getResponse().getResult());

                                        RequestModel model = new RequestModel();
                                        model.setAdapter(new RequestAdapter(getActivity() , liveData , binding , viewModel));

                                        MutableLiveData<RequestModel> requestModel = new MutableLiveData<>();
                                        requestModel.setValue(model);

                                        view.setAdapter(model.getAdapter());
                                        startRecyclerViewCategoryAnimation(view);

                                        viewModel.setActivity(getActivity() , requestModel);

//                            viewModel.getRequestLiveData().observe(getActivity(), requestModel1 -> {
////                                if (requestModel1.getAdapter() == null)
////                                    view.setAdapter(new RequestAdapter(getActivity() , new MutableLiveData<>()));
//
//                                view.setAdapter(requestModel1.getAdapter());
//                                startRecyclerViewCategoryAnimation(view);
//
//                            });
                                    }
                                }

                            }, throwable -> {
                                Log.d("error_request_requests" , throwable.getMessage());
                                configSomeProblem();
                            })

            );

        }
        catch (Exception e){
            e.printStackTrace();
            configSomeProblem();
            Toast.makeText(getActivity(), "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void configNoData() {
        binding.imgBackground.setImageResource(R.drawable.no_data);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getActivity().getResources().getString(R.string.no_requests));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.recyclerRequest.setVisibility(View.GONE);
    }

    private void configGetData() {
        binding.imgBackground.setVisibility(View.GONE);
        binding.textBackground.setVisibility(View.GONE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.recyclerRequest.setVisibility(View.VISIBLE);
    }

    private void configSomeProblem() {
        binding.imgBackground.setImageResource(R.drawable.some_problem);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getActivity().getResources().getString(R.string.some_problem));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.recyclerRequest.setVisibility(View.GONE);
    }

    private void configNoInternet() {
        binding.imgBackground.setImageResource(R.drawable.no_internet);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(getActivity().getResources().getString(R.string.you_need_internet));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.recyclerRequest.setVisibility(View.GONE);
    }

    private void startRecyclerViewCategoryAnimation(RecyclerView recyclerView) {

        Context context = recyclerView.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_slide_right);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Set Animation
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    private void checkNewBookings() {

        if (Common.isConnectionToInternet(getActivity())){

            try {
                String token = Hawk.get("TOKEN");

                new CompositeDisposable().add(
                        api.countRequest(token , Common.craftmanData.getId())
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

                                                //check count of requests
                                                try {
                                                    Log.d("new_requets_comming" , "OK");
                                                    int newRequest = Integer.parseInt(AES.decrypt(stringResultModel.getResponse().getResult()));
                                                    Log.d("new_requets_comming" , String.valueOf(newRequest));
                                                    Common.lastNewBooking = newRequest;
//                                                    getActivity().startService(new Intent(getActivity() , CheckNewRequests.class));
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
                                                int newRequest = Integer.parseInt(AES.decrypt(stringResultModel.getResponse().getResult()));
                                                Common.lastNewBooking = newRequest;
                                                getActivity().startService(new Intent(getActivity() , CheckNewRequests.class));
                                            }
                                            catch (Exception e) {
                                                Toasty.error(getActivity() , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }, throwable -> Log.d("request_error_login" , throwable.getMessage())));

            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toasty.info(getActivity(), getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();
        }

    }

    @Override
    public void onAdapterHandler(RecyclerView view){

        if (Common.isConnectionToInternet(getActivity())){

            view.setHasFixedSize(true);
            view.setLayoutManager(new LinearLayoutManager(getActivity()));
            loadRequests(view);
            //Start Service
            checkNewBookings();
        }
        else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
            configNoInternet();
            return;
        }


    }
}