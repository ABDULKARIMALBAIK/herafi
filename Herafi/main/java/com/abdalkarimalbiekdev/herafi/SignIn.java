package com.abdalkarimalbiekdev.herafi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Dialog.DialogFingerprint;
import com.abdalkarimalbiekdev.herafi.Dialog.DialogForgetPassword;
import com.abdalkarimalbiekdev.herafi.Dialog.DialogInfo;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInDialogFingerprintListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignInListener;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogFingerprintModel;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogForgetPasswordModel;
import com.abdalkarimalbiekdev.herafi.model.SignInDialogVerifyModel;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.model.SignInModel;
import com.abdalkarimalbiekdev.herafi.networkModel.CraftmanData;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.abdalkarimalbiekdev.herafi.viewModel.SignInViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.ActivitySignInBinding;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.hcaptcha.sdk.HCaptcha;
import com.hcaptcha.sdk.HCaptchaConfig;
import com.hcaptcha.sdk.HCaptchaException;
import com.hcaptcha.sdk.HCaptchaTheme;
import com.hcaptcha.sdk.HCaptchaTokenResponse;
import com.hcaptcha.sdk.tasks.OnFailureListener;
import com.hcaptcha.sdk.tasks.OnSuccessListener;
import com.orhanobut.hawk.Hawk;
import com.sanojpunchihewa.glowbutton.GlowButton;

public class SignIn extends AppCompatActivity implements SignInListener{

    private ActivitySignInBinding binding;
    private SignInViewModel signInViewModel;
    private Goldfinger goldfinger;

    private HerafiAPI api;

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

//        setContentView(R.layout.activity_sign_in);
        binding = DataBindingUtil.setContentView(this , R.layout.activity_sign_in);

        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        signInViewModel.setListener(this);
        signInViewModel.setActivity(this);

//        binding.setLifecycleOwner(this);
        binding.setSignInBinding(signInViewModel);


        binding.ckbRemember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            signInViewModel.getSignInLiveData().observe(this, signInModel -> {
                signInModel.setChecked(isChecked);
            });
        });


        Hawk.init(this).build();
        api = Common.getAPI();

        //Change the font of title
        Typeface typeface = Typeface.createFromAsset(getAssets() , "fonts/giddyupstd.otf");
        binding.txtTitle.setTypeface(typeface);

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

        //Check Remember Check Box
        try {
            String encEmail = Hawk.get("EMAIL");
            String encPwd = Hawk.get("PASSWORD");

            if (encEmail != null && encPwd != null){
                if (!encEmail.isEmpty() && !encPwd.isEmpty()){
                    if (Common.isConnectionToInternet(getApplicationContext())){

                        String token = Hawk.get("TOKEN");
                        startLogin(encEmail, encPwd, token);
                    }
                    else
                        Toasty.info(getApplicationContext() , getResources().getString(R.string.signIn_toast_noInternet) , Toast.LENGTH_LONG , true).show();

                }

            }
        }
        catch (Exception e){
            Toasty.error(this, e.toString(), Toast.LENGTH_SHORT , true).show();
        }


    }

    private void validate_hCaptcha(String email , String password , String token) {

        Log.d("pass_token" , token + "");


        SharedPreferences languagepref = getSharedPreferences("language",MODE_PRIVATE);

        HCaptchaConfig config = HCaptchaConfig.builder()
                .siteKey(Common.HCAPTCHA_SITE_KEY)
                .locale(languagepref.getString("languageToLoad","en").equalsIgnoreCase("ar")? "ar" : "en")
                .theme( HCaptchaTheme.DARK)  //AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES? : HCaptchaTheme.LIGHT
                .build();

        Log.d("type_hcaptcha" , AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES? "dark" : "light");

        HCaptcha.getClient(this).verifyWithHCaptcha(config)
                .addOnSuccessListener(response -> {

                    String responseToken = response.getTokenResult();
                    // Validate the user response token using the hCAPTCHA siteverify API.
                    new CompositeDisposable().add(
                            api.verifyHCaptchaToken(
                                    "Bearer " + token,
                                    responseToken
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(stringResultModel -> {

                                Log.d("result_capthca" , stringResultModel.getResponse().getResult());

                                if (stringResultModel.getResponse().getResult().equalsIgnoreCase("true")){
                                    ////////startLogin
                                    startLogin(email , password , token);
                                }
                                else {
                                    Toasty.warning(this, getResources().getString(R.string.robot), Toast.LENGTH_SHORT , true).show();
                                }

                            }, throwable -> {
                                Log.d("show_error" , throwable.getMessage());
                            })
                    );

                })
                .addOnFailureListener(e -> {
                    Log.d("SignIN_captcha", "Error code: " + e.getStatusCode());
                    Log.d("SignIN_captcha", "Error msg: " + e.getMessage());
                });

    }

    private void fingerprintLogin(DialogFingerprint dialogFingerprint, String fingerprintCode) {

        //Init Gold for fingerprint
        Goldfinger.Builder builder = new Goldfinger.Builder(SignIn.this);
        goldfinger = builder.build();

        if (goldfinger.hasFingerprintHardware()){

            if (goldfinger.hasEnrolledFingerprint()){

                goldfinger.decrypt(Common.FINGERPRINT_KEY, fingerprintCode , new Goldfinger.Callback() {
                    @Override
                    public void onSuccess(String value) {

                        try {

                            String encFingerprintId = AES.encrypt(fingerprintCode);
                            String token = Hawk.get("TOKEN");

                            new CompositeDisposable().add(
                                    api.loginFingerprint(
                                            "Bearer " + token,
                                            encFingerprintId
                                    )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(craftmanDataResultModel -> {

                                        if (craftmanDataResultModel != null){
                                            if (!craftmanDataResultModel.getErrorMessage().isEmpty()){

                                                try {
                                                    String error = AES.decrypt(craftmanDataResultModel.getErrorMessage());
                                                    Log.d("error_message_here" ,error);
                                                }
                                                catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                            else if (!craftmanDataResultModel.getResponse().getToken().isEmpty()){
                                                if (Common.checkSecureToken(craftmanDataResultModel.getResponse().getToken())){

                                                    //Save the token
                                                    Hawk.put("TOKEN" , craftmanDataResultModel.getResponse().getToken());

                                                    //Save data of craftsman
                                                    String name = AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName());
                                                    Toasty.success(SignIn.this,
                                                            getResources().getString(R.string.welcome) +  " " + craftmanDataResultModel.getResponse().getResult().getName() , Toasty.LENGTH_SHORT , true).show();
                                                    Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                                    startActivity(new Intent(SignIn.this , NavigatorFragment.class));

                                                    try {
                                                        Toasty.success(SignIn.this , "Welcome " + AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName()) , Toasty.LENGTH_SHORT , true).show();
                                                    }
                                                    catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                                else
                                                    Toasty.error(SignIn.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                                }
                                            else {
                                                //Save data of craftsman
                                                String name = AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName());
                                                Toasty.success(SignIn.this,
                                                        getResources().getString(R.string.welcome) + " " + name , Toasty.LENGTH_SHORT , true).show();
                                                Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                                startActivity(new Intent(SignIn.this , NavigatorFragment.class));

                                                try {
                                                    Toasty.success(SignIn.this , "Welcome " + AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName()) , Toasty.LENGTH_SHORT , true).show();
                                                }
                                                catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }

                                    }, throwable -> {
                                        goldfinger.cancel();
                                        Log.d("error_fingerprint" , throwable.getMessage());
                                    })
                            );

                        }
                        catch (Exception e) {
                            goldfinger.cancel();
                            Toasty.error(SignIn.this, "error_HERE" + e.getMessage(), Toast.LENGTH_SHORT , true).show();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Error error) {
                        dialogFingerprint.dialog.dismiss();
                        goldfinger.cancel();
                        Toasty.error(SignIn.this, error.toString(), Toast.LENGTH_SHORT , true).show();
                    }
                });

            }
            else{
                goldfinger.cancel();
                dialogFingerprint.dialog.dismiss();
                Toasty.error(this, getResources().getString(R.string.signIn_toast_fingerprintNoEnroll), Toast.LENGTH_SHORT).show();
            }

        }
        else{
            goldfinger.cancel();
            dialogFingerprint.dialog.dismiss();
            Toasty.error(this, getResources().getString(R.string.signIN_toast_fingerprintNoSupport), Toast.LENGTH_SHORT).show();
        }
    }

    private void startLogin(String encEmail, String encPassword, String token) {

        try {
            new CompositeDisposable().add(
                    api.login(
                            "Bearer " + token,
                            encEmail,
                            encPassword)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(craftmanDataResultModel -> {

                        if (craftmanDataResultModel != null){
                            if (!craftmanDataResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String error = AES.decrypt(craftmanDataResultModel.getErrorMessage());
                                    Log.d("error_message_here" ,error);
                                    Toasty.error(this, "Error Happened :\n" + error, Toast.LENGTH_SHORT , true).show();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else if (!craftmanDataResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(craftmanDataResultModel.getResponse().getToken())){

                                    //Save the token
                                    Hawk.put("TOKEN" , craftmanDataResultModel.getResponse().getToken());

                                    //Save data of craftsman
                                    Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                    Log.d("success_login" , "OK");
                                    try {
                                        Toasty.success(this , getResources().getString(R.string.welcome) + AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName()) , Toasty.LENGTH_SHORT , true).show();
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(new Intent(SignIn.this , NavigatorFragment.class));
                                }
                                else
                                    Toasty.error(SignIn.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                             }
                            else {
                                //Save data of craftsman
                                Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                Log.d("success_login" , "OK");
                                try {
                                    Toasty.success(this , getResources().getString(R.string.welcome) + AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName()) , Toasty.LENGTH_SHORT , true).show();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                startActivity(new Intent(SignIn.this , NavigatorFragment.class));
                            }
                        }

                    }, throwable -> Log.d("request_error_login" , throwable.getMessage()))

            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void verifyAccount(String VerifyCode) {

        try {
            String token = Hawk.get("TOKEN");

            new CompositeDisposable().add(
                    api.verify(
                            "Bearer " + token,
                            AES.encrypt(VerifyCode)
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(craftmanDataResultModel -> {

                        if (craftmanDataResultModel != null){
                            if (!craftmanDataResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String error = AES.decrypt(craftmanDataResultModel.getErrorMessage());
                                    Log.d("error_message_here" , error);
                                    Toasty.error(SignIn.this, "Error Happened :\n" + error, Toast.LENGTH_SHORT , true).show();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else if (!craftmanDataResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(craftmanDataResultModel.getResponse().getToken())){

                                    //Save the token
                                    Hawk.put("TOKEN" , craftmanDataResultModel.getResponse().getToken());

                                    //Save data of craftsman
                                    Toasty.success(SignIn.this,getResources().getString(R.string.welcome) + craftmanDataResultModel.getResponse().getResult().getName() , Toasty.LENGTH_SHORT , true).show();
                                    Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                    startActivity(new Intent(SignIn.this , NavigatorFragment.class));
                                }
                                else
                                    Toasty.error(SignIn.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                         }
                            else {
                                //Save data of craftsman
                                Toasty.success(SignIn.this,getResources().getString(R.string.welcome) + craftmanDataResultModel.getResponse().getResult().getName() , Toasty.LENGTH_SHORT , true).show();
                                Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                startActivity(new Intent(SignIn.this , NavigatorFragment.class));
                            }
                        }
                    }, throwable -> Log.d("error_request_verify", throwable.getMessage()))


            );

        } catch (Exception e) {
            Toasty.error(this, "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT , true).show();
            e.printStackTrace();
        }
    }

    private void sendForgetPassword(String email, String phoneNumber, String secureCode) {

        try {
            String token = Hawk.get("TOKEN");

            new CompositeDisposable().add(
                    api.sendForgetPassword(
                            "Bearer " + token,
                            AES.encrypt(email),
                            AES.encrypt(phoneNumber),
                            AES.encrypt(secureCode)
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(stringResultModel -> {

                        if (stringResultModel != null){
                            if (!stringResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String errorMessage = AES.decrypt(stringResultModel.getErrorMessage());
                                    Log.d("error_message_here" , errorMessage);
                                    Toasty.error(SignIn.this, errorMessage, Toast.LENGTH_SHORT , true).show();

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                    //Save the token
                                    Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                    //Show Password
                                    try {
                                        String craftsmanPassword = AES.decrypt(stringResultModel.getResponse().getResult());
                                        Toasty.success(SignIn.this , getResources().getString(R.string.show_password) + craftsmanPassword , Toasty.LENGTH_SHORT , true).show();
                                    }
                                    catch (Exception e) {
                                        Toasty.error(SignIn.this , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                        e.printStackTrace();
                                    }
                                }
                                else
                                    Toasty.error(SignIn.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                         }
                            else {
                                //Show Password
                                try {
                                    String craftsmanPassword = AES.decrypt(stringResultModel.getResponse().getResult());
                                    Toasty.success(SignIn.this , getResources().getString(R.string.show_password) + craftsmanPassword , Toasty.LENGTH_SHORT , true).show();
                                }
                                catch (Exception e) {
                                    Toasty.error(SignIn.this , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                    e.printStackTrace();
                                }
                            }
                        }

                    }, throwable -> Log.d("error_request_forgetpwd" , throwable.getMessage()))

            );
        }
        catch (Exception e) {
            Toasty.error(this, "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT , true).show();
            e.printStackTrace();
        }
    }

    private void loginFacebook(String facebook_id) {

        try {
            String encFacebookId = AES.encrypt(facebook_id);
            String token = Hawk.get("TOKEN");

            new CompositeDisposable().add(
                    api.loginFacebook(
                            "Bearer " + token,
                            encFacebookId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(craftmanDataResultModel -> {

                        if (craftmanDataResultModel != null){
                            if (!craftmanDataResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String error = AES.decrypt(craftmanDataResultModel.getErrorMessage());
                                    Log.d("error_message_here" ,error);
                                    Toasty.error(SignIn.this, error, Toast.LENGTH_SHORT , true).show();

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else if (!craftmanDataResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(craftmanDataResultModel.getResponse().getToken())){

                                    //Save the token
                                    Hawk.put("TOKEN" , craftmanDataResultModel.getResponse().getToken());

                                    //Save data of craftsman
                                    try {
                                        String name = AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName());
                                        Toasty.success(SignIn.this,
                                                getResources().getString(R.string.welcome) + " " +  name, Toasty.LENGTH_SHORT , true).show();
                                        Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                        startActivity(new Intent(SignIn.this , NavigatorFragment.class));
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                else
                                    Toasty.error(SignIn.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                            }
                            else {
                                //Save data of craftsman
                                try {
                                    String name = AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName());
                                    Toasty.success(SignIn.this,
                                            getResources().getString(R.string.welcome) + " " + name, Toasty.LENGTH_SHORT , true).show();
                                    Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                    startActivity(new Intent(SignIn.this , NavigatorFragment.class));
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }

                    }, throwable -> Log.d("error_request_facebook" , throwable.getMessage()))
            );

        }
        catch (Exception e) {
            e.printStackTrace();
            Toasty.error(this, "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT , true).show();
        }
    }

    private void loginGoogle(String google_id) {

        try {
            String encGoogleId = AES.encrypt(google_id);
            String token = Hawk.get("TOKEN");

            new CompositeDisposable().add(
                    api.loginGoogle(
                            "Bearer " + token,
                            encGoogleId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(craftmanDataResultModel -> {

                        if (craftmanDataResultModel != null){
                            if (!craftmanDataResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String error = AES.decrypt(craftmanDataResultModel.getErrorMessage());
                                    Log.d("error_message_here" ,error);
                                    Toasty.error(SignIn.this, error, Toast.LENGTH_SHORT , true).show();

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else if (!craftmanDataResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(craftmanDataResultModel.getResponse().getToken())){

                                    //Save the token
                                    Hawk.put("TOKEN" , craftmanDataResultModel.getResponse().getToken());

                                    //Save data of craftsman
                                    String  name = AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName());
                                    Toasty.success(SignIn.this,
                                            getResources().getString(R.string.welcome) + " " +  name, Toasty.LENGTH_SHORT , true).show();
                                    Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                    startActivity(new Intent(SignIn.this , NavigatorFragment.class));
                                }
                                else
                                    Toasty.error(SignIn.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                             }
                            else {
                                //Save data of craftsman
                                try {
                                    String name = AES.decrypt(craftmanDataResultModel.getResponse().getResult().getName());
                                    Toasty.success(SignIn.this,
                                            getResources().getString(R.string.welcome) + " " +  name, Toasty.LENGTH_SHORT , true).show();
                                    Common.craftmanData = craftmanDataResultModel.getResponse().getResult();
                                    startActivity(new Intent(SignIn.this , NavigatorFragment.class));
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }

                    }, throwable -> {
                        Log.d("error_request_google" , throwable.getMessage());
                    })
            );
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("error_show" , e.getMessage());
        }
    }

    @Override
    public void onVerifyClicked(View view) {

        SignInDialogVerifyModel model = new SignInDialogVerifyModel(
                getResources().getString(R.string.signIn_dialog_verify_title),
                getResources().getString(R.string.signIn_dialog_verify_message),
                getResources().getString(R.string.signIn_dialog_verify_Yes),
                getResources().getString(R.string.signIn_dialog_verify_No),
                R.drawable.ic_focused_secure_30,
                "",
                getResources().getString(R.string.signIn_dialog_verify_hintEdit));

        DialogInfo customDialog;
        DialogInfo dialog = new DialogInfo(SignIn.this, model);
        customDialog = dialog;

        dialog.setListener(() ->{
            
            customDialog.verifyViewModel.getVerifyMutableLiveData().observe(this, model1 -> {
                String edtVerifyCode = model.getEdtInfo();
                
                if (!edtVerifyCode.isEmpty()){
                    if (TextUtils.isDigitsOnly(edtVerifyCode)){
                        if (Common.isConnectionToInternet(this)){

                            verifyAccount(edtVerifyCode);
                        }
                        else
                            Toasty.info(this , getResources().getString(R.string.signIn_toast_noInternet) , Toast.LENGTH_SHORT , true).show();
                    }
                    else
                        Toasty.error(this , getResources().getString(R.string.signIn_toast_editTextIsNotDigit) , Toast.LENGTH_SHORT , true).show();;
                }
                else
                    Toasty.error(this , getResources().getString(R.string.signIn_toast_editTextIsEmpty) , Toast.LENGTH_SHORT , true).show();;
            });
        });
    }

    @Override
    public void onForgetPasswordClicked(View view) {

        SignInDialogForgetPasswordModel model = new SignInDialogForgetPasswordModel(
                getResources().getString(R.string.signIn_dialog_forgetpassword_title),
                getResources().getString(R.string.signIn_dialog_forgetpassword_message),
                getResources().getString(R.string.signIn_dialog_verify_Yes),
                getResources().getString(R.string.signIn_dialog_verify_No),
                R.drawable.ic_focused_password_30,
                "",
                getResources().getString(R.string.signIn_dialog_forgetpassword_email),
                "",
                getResources().getString(R.string.signIn_dialog_forgetpassword_phone),
                "",
                getResources().getString(R.string.signIn_dialog_forgetpassword_SecureCode));

        DialogForgetPassword customDialog;
        DialogForgetPassword dialog = new DialogForgetPassword(SignIn.this, model);
        customDialog = dialog;


        dialog.setListener(() ->{

            customDialog.viewModel.getLiveDataObserve().observe(this, model1 -> {

                String email = model.getEdtEmail();
                String phoneNumber = model.getEdtPhoneNumber();
                String secureCode = model.getEdtScureCode();

                if (!email.isEmpty() && !phoneNumber.isEmpty() && !secureCode.isEmpty()){
                    if (TextUtils.isDigitsOnly(phoneNumber) && TextUtils.isDigitsOnly(secureCode)){
                        if (Common.isConnectionToInternet(this)){

                            sendForgetPassword(email , phoneNumber , secureCode);
                        }
                        else
                            Toasty.info(this , getResources().getString(R.string.signIn_toast_noInternet) , Toast.LENGTH_SHORT , true).show();;
                    }
                    else
                        Toasty.error(this , getResources().getString(R.string.signIn_toast_editTextIsNotDigit) , Toast.LENGTH_SHORT , true).show();;
                }
                else
                    Toasty.error(this , getResources().getString(R.string.signIn_toast_editTextIsEmpty) , Toast.LENGTH_SHORT , true).show();;
            });
        });
    }

    @Override
    public void onLoginClicked(View view) {

        signInViewModel.getSignInLiveData().observe(this , model ->{

            if (model.isChecked()){
                try {
                    Hawk.put("EMAIL", AES.encrypt(model.getEmail()));
                    Hawk.put("PASSWORD", AES.encrypt(model.getPassword()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toasty.error(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT , true).show();
                }
            }

            if (!model.getEmail().isEmpty() && !model.getPassword().isEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(model.getEmail()).matches()) {
                    if (Common.isConnectionToInternet(getApplicationContext())) {

                        try {
//                            startLogin(AES.encrypt(model.getEmail()) , AES.encrypt(model.getPassword()));
                            String token = Hawk.get("TOKEN");
                            validate_hCaptcha(AES.encrypt(model.getEmail()) , AES.encrypt(model.getPassword()) , token);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT  ,true).show();
                        }

                    } else
                        Toasty.info(SignIn.this, getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT , true).show();
                } else
                    Toasty.error(SignIn.this, getResources().getString(R.string.signIn_toast_currentEmail), Toast.LENGTH_SHORT , true).show();
            } else
                Toasty.error(SignIn.this, getResources().getString(R.string.signIn_toast_fillFields), Toast.LENGTH_SHORT , true).show();

        });
    }

    @Override
    public void onFingerprintClicked(View view) {

        String value = Hawk.get("FINGERPRINT");

        if (value != null){
            if (Common.isConnectionToInternet(this)){

                String fingerprintCode = null;
                try {
                    fingerprintCode = AES.decrypt(Hawk.get("FINGERPRINT"));
                }
                catch (Exception e) {
                    Toasty.error(this, "error_here :\n" + e.getMessage(), Toast.LENGTH_SHORT , true).show();
                    e.printStackTrace();
                }


                SignInDialogFingerprintModel model = new SignInDialogFingerprintModel(
                        getResources().getString(R.string.signIn_dialog_fingerprint_title),
                        getResources().getString(R.string.signIn_dialog_fingerprint_message),
                        "",
                        getResources().getString(R.string.signIn_dialog_verify_No),
                        R.drawable.ic_focused_key_30);



                DialogFingerprint dialogFingerprint = new DialogFingerprint(SignIn.this, model);
                dialogFingerprint.setListener(new SignInDialogFingerprintListener() {
                    @Override
                    public void onClickFingerprint() {
                        Toasty.info(SignIn.this , getResources().getString(R.string.signIn_toast_fingerprintClickImage) , Toast.LENGTH_SHORT , true).show();
                    }

                    @Override
                    public void onClickCancel() {
                        dialogFingerprint.dialog.dismiss();
                        goldfinger.cancel();
                    }
                });

                fingerprintLogin(dialogFingerprint , fingerprintCode);

            }
            else
                Toasty.info(this , getResources().getString(R.string.signIn_toast_noInternet) , Toast.LENGTH_SHORT, true);
        }
        else
            Toasty.info(this, getResources().getString(R.string.signIn_toast_noFingerprintBefore), Toast.LENGTH_SHORT , true).show();

    }

    @Override
    public void onFacebookClicked(View view) {

        if (Common.isConnectionToInternet(SignIn.this)){

            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

            if (isLoggedIn){

                String facebook_Id = accessToken.getUserId();
                Log.d("UserFacebookId" , facebook_Id);

                loginFacebook(facebook_Id);
            }
            else
                Toasty.info(SignIn.this, getResources().getString(R.string.signIn_toast_noFacebookAccount), Toast.LENGTH_SHORT , true).show();
        }
        else
            Toasty.info(SignIn.this, getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGoogleClicked(View view) {

        if (Common.isConnectionToInternet(SignIn.this)){

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SignIn.this);

            if (account != null){
                loginGoogle(account.getId());
            }
            else
                Toasty.info(this, getResources().getString(R.string.signIn_toast_noGoogleBefore), Toast.LENGTH_SHORT , true).show();

        }
        else
            Toasty.info(SignIn.this, getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignUpTextClick(View view) {
        startActivity(new Intent(this , SignUp.class));
    }

}