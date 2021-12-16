package com.abdalkarimalbiekdev.herafi.viewModel;

import android.app.Activity;
import android.widget.ImageView;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInDialogFingerprintListener;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogFingerprintModel;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignInDialogFingerprintViewModel extends ViewModel {

    private MutableLiveData<SignInDialogFingerprintModel> fingerprintLiveData;
    private Activity activity;
    private SignInDialogFingerprintListener listener;

    public SignInDialogFingerprintViewModel() {
    }

    public void setActivity(Activity activity , SignInDialogFingerprintModel model) {

        this.activity = activity;
        fingerprintLiveData = new MutableLiveData<>();
        fingerprintLiveData.setValue(model);

    }

    public void setListener(SignInDialogFingerprintListener listener) {
        this.listener = listener;
    }

    public MutableLiveData<SignInDialogFingerprintModel> getFingerprintLiveData() {
        return fingerprintLiveData;
    }

    public SignInDialogFingerprintModel getFingerprintModel() {
        return fingerprintLiveData.getValue();
    }

    public void onClickCancel(){
        listener.onClickCancel();
    }

    public void onClickFingerprint(){
        listener.onClickFingerprint();
    }

    @BindingAdapter("loadImage")
    public static void loadImage(ImageView view , int path){
//        fingerprintLiveData.observe((LifecycleOwner) activity, model -> {
//            model.setImgDialog(path);
//            view.setImageResource(model.getImgDialog());
//        });
    }
}
