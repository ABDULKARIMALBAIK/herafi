package com.abdalkarimalbiekdev.herafi.Fragment.Authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveDataReactiveStreams;
import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Dialog.DialogFingerprint;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInDialogFingerprintListener;
import com.abdalkarimalbiekdev.herafi.NavigatorFragment;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.SignIn;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogFingerprintModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.orhanobut.hawk.Hawk;

import java.util.UUID;

public class AuthenticationsFragment extends Fragment {

    private static final int RC_SIGN_IN = 1111;
    LoginButton btnAddFacebook;
    Button btnAddFingerprint;
    SignInButton btnAddGoogle;

    CallbackManager callbackManager;
    CompositeDisposable compositeDisposable;
    HerafiAPI api;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    private Goldfinger goldfinger;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_authentications, container, false);

        initViews(view);

        callbackManager = CallbackManager.Factory.create();
        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();
        Hawk.init(getActivity()).build();


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        btnAddFacebook.setReadPermissions("email");
        // If using in a fragment
        btnAddFacebook.setFragment(this);


        btnAddFacebook.setOnClickListener(v -> {

            AccessToken accessToken = AccessToken.getCurrentAccessToken();
//            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

            if (accessToken == null){ //He doesn't has facebook login

                if (Common.isConnectionToInternet(getActivity())){

                    Log.d("no_facebook_auth" , "OK");

                    btnAddFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {

                            try {
                                String facebookId = loginResult.getAccessToken().getUserId();
                                String encFacebookId = AES.encrypt(facebookId);
                                Log.d("CraftsmanFacebookId" , encFacebookId);

                                String token = Hawk.get("TOKEN");
                                Log.d("is_token",token +"");

                                compositeDisposable.add(
                                        api.addFacebookLogin(
                                                "Bearer " + token,
                                                encFacebookId,
                                                Common.craftmanData.getId()
                                        )
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(stringResultModel -> {

                                                    if (stringResultModel != null){
                                                        if (!stringResultModel.getErrorMessage().isEmpty()){
                                                            if (AES.decrypt(stringResultModel.getErrorMessage()).equals("has_account")){

                                                                String info = getActivity().getResources().getString(R.string.authentication_toast_facebook_added_before);
                                                                Log.d("show_msg" ,info);
                                                            }
                                                            else {
                                                                String error = AES.decrypt(stringResultModel.getErrorMessage());
                                                                Log.d("error_message_here" ,error);
                                                                Toasty.error(getActivity(),  error, Toast.LENGTH_SHORT , true).show();

                                                            }

                                                        }
                                                        else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                                            if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                                                //Save the token
                                                                Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                                                //Facebook login is added successfully
                                                                try {
                                                                    Toasty.success(getActivity() , AES.decrypt(stringResultModel.getResponse().getResult()) , Toasty.LENGTH_SHORT , true).show();
                                                                }
                                                                catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                            else
                                                                Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                             }
                                                        else {
                                                            //Facebook login is added successfully
                                                            try {
                                                                Toasty.success(getActivity() , AES.decrypt(stringResultModel.getResponse().getResult()) , Toasty.LENGTH_SHORT , true).show();
                                                            }
                                                            catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }


                                                }, throwable -> {
                                                    Log.d("error_request" , throwable.getMessage());
                                                })
                                );



                            }
                            catch (Exception e){
                                e.printStackTrace();
                                Log.d("show_error" , e.getMessage());
                            }





                        }

                        @Override
                        public void onCancel() {
                            Toasty.info(getActivity(), getActivity().getResources().getString(R.string.authentication_toast_facebook_canceled), Toast.LENGTH_SHORT , true).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Toasty.error(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();
                        }
                    });

                }
                else
                    Toasty.success(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();



            }
            else
                Toast.makeText(getActivity(), getResources().getString(R.string.authentication_toast_facebook_added_before), Toast.LENGTH_SHORT).show();

        });

        btnAddFingerprint.setOnClickListener(v -> {

            Log.d("Fingerprint_clicked" , "OK");

            if (Common.isConnectionToInternet(getActivity())){

                try {
                    String encFingerprint = Hawk.get("FINGERPRINT");
                    Log.d("CraftsmanFingerprintId" , "the id is " + encFingerprint);

                    if (encFingerprint == null){  //He didn't add fingerprint (First Time)

                        showDialog();
                    }
                    else
                        Toasty.info(getActivity(), getActivity().getResources().getString(R.string.authentication_toast_fingerprint_added_before), Toast.LENGTH_SHORT).show();


                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.d("show_error" , e.getMessage());
                }
            }
            else
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
        });

        btnAddGoogle.setOnClickListener(v -> {

            if (Common.isConnectionToInternet(getActivity())){

                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

                if (account == null){  //First time sign in wit google
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
                else
                    Toasty.info(getActivity(), getActivity().getResources().getString(R.string.authentication_toast_google_added_before), Toast.LENGTH_SHORT , true).show();
            }
            else
                Toasty.info(getActivity(), getActivity().getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();


        });

        return view;
    }

    private void showDialog() {

        SignInDialogFingerprintModel model = new SignInDialogFingerprintModel(
                getResources().getString(R.string.signIn_dialog_fingerprint_title),
                getResources().getString(R.string.signIn_dialog_fingerprint_message),
                "",
                getResources().getString(R.string.signIn_dialog_verify_No),
                R.drawable.ic_focused_key_30);



        DialogFingerprint dialogFingerprint = new DialogFingerprint(getActivity(), model);
        dialogFingerprint.setListener(new SignInDialogFingerprintListener() {
            @Override
            public void onClickFingerprint() {
                Toasty.info(getActivity() , getResources().getString(R.string.signIn_toast_fingerprintClickImage) , Toast.LENGTH_SHORT , true).show();
            }

            @Override
            public void onClickCancel() {
                dialogFingerprint.dialog.dismiss();
                goldfinger.cancel();
            }
        });


        String newFingerprint = UUID.randomUUID().toString().substring(0 , 6);
        Log.d("newFingerprint" , newFingerprint);

        fingerprintLogin(dialogFingerprint , newFingerprint);

    }

    private void fingerprintLogin(DialogFingerprint dialogFingerprint, String newFingerprint) {

        //Init Gold for fingerprint
        Goldfinger.Builder builder = new Goldfinger.Builder(getActivity());
        goldfinger = builder.build();

        if (goldfinger.hasFingerprintHardware()){

            if (goldfinger.hasEnrolledFingerprint()){

                goldfinger.encrypt(Common.FINGERPRINT_KEY, newFingerprint , new Goldfinger.Callback() {
                    @Override
                    public void onSuccess(String value) {

                        try {
                            String encFingerprint = AES.encrypt(value);
                            Log.d("encFingerprint" , encFingerprint);

                            String token = Hawk.get("TOKEN");
                            Log.d("is_token",token +"");

                            compositeDisposable.add(
                                    api.addFingerprintLogin(
                                            "Bearer " + token,
                                            encFingerprint,
                                            Common.craftmanData.getId()
                                    )
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(stringResultModel -> {

                                                if (stringResultModel != null){
                                                    if (!stringResultModel.getErrorMessage().isEmpty()){
                                                        if (AES.decrypt(stringResultModel.getErrorMessage()).equals("has_account")){

                                                            String info = getActivity().getResources().getString(R.string.authentication_toast_fingerprint_added_before);
                                                            Log.d("show_msg" ,info);
                                                            Toasty.info(getActivity(), info, Toast.LENGTH_SHORT , true).show();
                                                            dialogFingerprint.dialog.dismiss();
                                                        }
                                                        else {
                                                            String error = AES.decrypt(stringResultModel.getErrorMessage());
                                                            Log.d("error_message_here" ,error);
                                                            Toasty.error(getActivity(), error, Toast.LENGTH_SHORT , true).show();
                                                            dialogFingerprint.dialog.dismiss();
                                                        }

                                                    }
                                                    else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                                        if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                                            //Save the token
                                                            Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                                            //Fingerprint login is added successfully
                                                            try {
                                                                dialogFingerprint.dialog.dismiss();

                                                                Hawk.put("FINGERPRINT" , encFingerprint);
                                                                Toasty.success(getActivity() , AES.decrypt(stringResultModel.getResponse().getResult()) , Toasty.LENGTH_SHORT , true).show();
                                                            }
                                                            catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                        else
                                                            Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                             }
                                                    else {
                                                        //Fingerprint login is added successfully
                                                        try {
                                                            dialogFingerprint.dialog.dismiss();
                                                            Log.d("fingerprint_ok" , "OK");
                                                            Hawk.put("FINGERPRINT" , encFingerprint);
                                                            Toasty.success(getActivity() , AES.decrypt(stringResultModel.getResponse().getResult()) , Toasty.LENGTH_SHORT , true).show();
                                                        }
                                                        catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }


                                            }, throwable -> {
                                                Log.d("error_request" , throwable.getMessage());
                                            }));

                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Log.d("show_error" , e.getMessage());
                        }

                    }

                    @Override
                    public void onError(Error error) {
                        dialogFingerprint.dialog.dismiss();
                        goldfinger.cancel();
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else{
                goldfinger.cancel();
                dialogFingerprint.dialog.dismiss();
                Toasty.error(getActivity(), getResources().getString(R.string.signIn_toast_fingerprintNoEnroll), Toast.LENGTH_SHORT).show();
            }

        }
        else{
            goldfinger.cancel();
            dialogFingerprint.dialog.dismiss();
            Toasty.error(getActivity(), getResources().getString(R.string.signIN_toast_fingerprintNoSupport), Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        btnAddFacebook = view.findViewById(R.id.btnAddFacebook);
        btnAddFingerprint = view.findViewById(R.id.btnAddFingerprint);
        btnAddGoogle = view.findViewById(R.id.btnAddGoogle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        callbackManager.onActivityResult(requestCode , resultCode , data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.d("craftsman_google_id" , account.getId());
            String googleId = account.getId();
            String encGoogleId = AES.encrypt(googleId);

            String token = Hawk.get("TOKEN");
            Log.d("is_token",token +"");

            try {
                compositeDisposable.add(
                        api.addGoogleLogin(
                                "Bearer " + token,
                                encGoogleId,
                                Common.craftmanData.getId()
                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(stringResultModel -> {

                                    if (stringResultModel != null){
                                        if (!stringResultModel.getErrorMessage().isEmpty()){
                                            if (AES.decrypt(stringResultModel.getErrorMessage()).equals("has_account")){

                                                String info = getActivity().getResources().getString(R.string.authentication_toast_google_added_before);
                                                Log.d("show_msg" ,info);
                                                Toasty.info(getActivity(), info, Toast.LENGTH_SHORT , true).show();
                                            }
                                            else {
                                                String error = AES.decrypt(stringResultModel.getErrorMessage());
                                                Log.d("error_message_here" ,error);
                                                Toasty.error(getActivity(), error, Toast.LENGTH_SHORT , true).show();

                                            }

                                        }
                                        else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                            if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                                //Save the token
                                                Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                                //Facebook login is added successfully
                                                try {
                                                    Toasty.success(getActivity() , AES.decrypt(stringResultModel.getResponse().getResult()) , Toasty.LENGTH_SHORT , true).show();
                                                }
                                                catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                            else
                                                Toasty.error(getActivity(), getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                             }
                                        else {
                                            //Facebook login is added successfully
                                            try {
                                                Toasty.success(getActivity() , AES.decrypt(stringResultModel.getResponse().getResult()) , Toasty.LENGTH_SHORT , true).show();
                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }


                                }, throwable -> {
                                    Log.d("error_request" , throwable.getMessage());
                                }));

            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("show_error" , e.getMessage());
            }

        }
        catch (ApiException e) {

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("google_error", "signInResult:failed code=" + e.getStatusCode());

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}