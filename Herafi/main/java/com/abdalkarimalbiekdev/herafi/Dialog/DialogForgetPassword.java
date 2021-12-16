package com.abdalkarimalbiekdev.herafi.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.ClickYesListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInDialogVerifyListener;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.databinding.ShowDialogForgetpasswordBinding;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogForgetPasswordModel;
import com.abdalkarimalbiekdev.herafi.viewModel.SignInDialogForgetPasswordViewModel;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class DialogForgetPassword implements SignInDialogVerifyListener {

    public Dialog dialog;
    private ShowDialogForgetpasswordBinding binding;
    public SignInDialogForgetPasswordViewModel viewModel;
    private ClickYesListener listener;

    public DialogForgetPassword(Activity activity , SignInDialogForgetPasswordModel model) {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        binding = DataBindingUtil.inflate(LayoutInflater.from(activity) , R.layout.show_dialog_forgetpassword , null , false);
        dialog.setContentView(binding.getRoot());

        viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(SignInDialogForgetPasswordViewModel.class);
        viewModel.setActivity(activity , model);
        viewModel.setListener(this);

        binding.setLifecycleOwner((LifecycleOwner) activity);
        binding.setSignInDialogForgetPasswordBinding(viewModel);

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
    public void onClickNo(View view) { dialog.dismiss(); }

}

