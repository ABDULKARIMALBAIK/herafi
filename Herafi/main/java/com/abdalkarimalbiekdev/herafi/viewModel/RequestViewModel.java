package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.RecyclerItemClickListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.RequestListener;
import com.abdalkarimalbiekdev.herafi.model.RequestModel;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

public class RequestViewModel extends ViewModel {

    private static MutableLiveData<RequestModel> requestLiveData;

    public static Activity  activity;
    public static RequestListener listener;

    public RequestViewModel() {
    }

    public void setActivity(Activity activity , MutableLiveData<RequestModel> requestLiveData) {
        this.requestLiveData = requestLiveData;
        this.activity = activity;
        requestLiveData.setValue(new RequestModel());
    }

    public void setListener(RequestListener listener) {
        this.listener = listener;
    }

    public static MutableLiveData<RequestModel> getRequestLiveData() {
        return requestLiveData;
    }

    public static RequestModel getRequestModel() {
        return requestLiveData.getValue();
    }

    @BindingAdapter("setRequestAdapter")
    public static void setRequestAdapter(RecyclerView view , String tag){
        listener.onAdapterHandler(view);
    }

}