package com.abdalkarimalbiekdev.herafi.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.Window;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInDialogFingerprintListener;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogFingerprintModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.viewModel.SignInDialogFingerprintViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.ShowDialogFingerprintBinding;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class DialogFingerprint implements SignInDialogFingerprintListener {

    Activity activity;
    ShowDialogFingerprintBinding binding;
    SignInDialogFingerprintViewModel viewModel;
    public Dialog dialog;
    SignInDialogFingerprintListener listener;

    public DialogFingerprint(Activity activity , SignInDialogFingerprintModel model) {

        this.activity = activity;
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        binding = DataBindingUtil.inflate(LayoutInflater.from(activity) , R.layout.show_dialog_fingerprint, null , false);
        dialog.setContentView(binding.getRoot());

        viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(SignInDialogFingerprintViewModel.class);
        viewModel.setActivity(activity , model);
        viewModel.setListener(this);

        binding.setSignInDialogFingerprint(viewModel);
//        binding.setLifecycleOwner((LifecycleOwner) activity);

        binding.imgDialog.setImageResource(model.getImgDialog());

        dialog.show();

    }

    public void setListener(SignInDialogFingerprintListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClickFingerprint() {
        listener.onClickFingerprint();
    }

    @Override
    public void onClickCancel() {
        listener.onClickCancel();
    }
}
