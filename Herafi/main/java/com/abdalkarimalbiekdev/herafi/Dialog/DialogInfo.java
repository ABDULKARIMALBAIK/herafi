package com.abdalkarimalbiekdev.herafi.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.ClickYesListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInDialogVerifyListener;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogVerifyModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.viewModel.SignInDailogVerifyViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.ShowDialogBinding;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class DialogInfo implements SignInDialogVerifyListener {

    public Dialog dialog;
    private ShowDialogBinding binding;
    public SignInDailogVerifyViewModel verifyViewModel;
    private ClickYesListener listener;

    public DialogInfo(Activity activity , SignInDialogVerifyModel model) {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        binding =  DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout. show_dialog, null, false);
        dialog.setContentView(binding.getRoot());

//        setViews(drawable , message , title , textYES , textNO , hintEdit);
        verifyViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(SignInDailogVerifyViewModel.class);
        verifyViewModel.setActivity(activity , model);
        verifyViewModel.setListener(this);

        Toast.makeText(activity , "pass it here" , Toast.LENGTH_LONG).show();

        binding.setLifecycleOwner((LifecycleOwner) activity);
        binding.setSignInDialogVerify(verifyViewModel);

        dialog.show();
    }

    public void setListener(ClickYesListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClickYes(View view) {
        listener.onClick();
    }

    @Override
    public void onClickNo(View view) {
        dialog.dismiss();
    }
}
