package com.abdalkarimalbiekdev.herafi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Adapter.CraftSelectedAdapter;
import com.abdalkarimalbiekdev.herafi.Adapter.ImageCioAdpater;
import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Fragment.Crafts.CraftFragment;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.SignUpListener;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.model.CraftSelectedIModel;
import com.abdalkarimalbiekdev.herafi.model.ImageCioModel;
import com.abdalkarimalbiekdev.herafi.model.SignUpModel;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.networkModel.CityModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.abdalkarimalbiekdev.herafi.viewModel.SignUpViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.ActivitySignUpBinding;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hcaptcha.sdk.HCaptcha;
import com.hcaptcha.sdk.HCaptchaConfig;
import com.hcaptcha.sdk.HCaptchaTheme;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.jem.rubberpicker.RubberRangePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orhanobut.hawk.Hawk;
import com.sanojpunchihewa.glowbutton.GlowButton;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

public class SignUp extends AppCompatActivity implements SignUpListener {

    private static final int REQUEST_GET_IMAGE_GALLERY = 1000;
    private static final int REQUEST_PAYMENT_CODE = 1111;
    private ActivitySignUpBinding binding;
    private SignUpViewModel viewModel;
    private CraftFragment craftFragment;
    private int typeImage = -1;
    private HerafiAPI api;

    public List<CityModel> cityModels = new ArrayList<>();

    //For register process
    SignUpModel customModel;
    String accountId;


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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.setActivity(this, new SignUpModel());
        viewModel.setListener(this);

        //Change the font of title
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

        //Change the title text (SignUP)
        Typeface typeface = Typeface.createFromAsset(getAssets() , "fonts/giddyupstd.otf");
        binding.txtTitle.setTypeface(typeface);

        loadSpinnerCity();

        //Get location
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            getLocationCraftsman();
                        } else {
                            Toasty.error(SignUp.this, getResources().getString(R.string.signUp_toast_noPermissionsAgree), Toasty.LENGTH_LONG, true).show();
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();


        binding.spinnerCity.setOnItemSelectedListener((view, position, id, item) -> {
            viewModel.getSignUpLiveData().observe(this, model -> {

                model.setSpinnerCity(cityModels.get(position));
            });
        });

        binding.sizeQueue.setOnValueChangeListener((view, oldValue, newValue) -> {
            viewModel.getSignUpLiveData().observe(this, model -> {
                model.setNumSizePeople(newValue);
                binding.sizeQueue.setNumber(String.valueOf(model.getNumSizePeople()));
            });
        });

        binding.rangeBarPaid.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(RubberRangePicker rubberRangePicker, int i, int i1, boolean b) {
                viewModel.getSignUpLiveData().observe(SignUp.this, model -> {
                    model.setNumMinValueHand(i);
                    model.setTextMinValueHand(String.valueOf(i));

                    model.setNumMaxValueHand(i1);
                    model.setTextMaxValueHand(String.valueOf(i1));

                    binding.txtMinRange.setText(String.valueOf(i));
                    binding.txtMinRange.setText(String.valueOf(i1));

                });
            }

            @Override
            public void onStartTrackingTouch(RubberRangePicker rubberRangePicker, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(RubberRangePicker rubberRangePicker, boolean b) {

            }
        });

        binding.ckbPrivacyTermsAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.getSignUpLiveData().observe(this, signInModel -> {
                signInModel.setCheckedPrivacyConditions(isChecked);
            });
        });

//        binding.setLifecycleOwner(this);
        binding.setSignUpBinding(viewModel);

    }

    private void getLocationCraftsman() {

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation == null)
            lastLocation = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (lastLocation != null){

//            lat = (float) lastLocation.getLatitude();
//            lng = (float) lastLocation.getLongitude();

            Location finalLastLocation = lastLocation;

            viewModel.getSignUpLiveData().observe(this, model -> {
                model.setLat(finalLastLocation.getLatitude());
                model.setLng(finalLastLocation.getLongitude());
            });
        }
        else {

            viewModel.getSignUpLiveData().observe(this, model -> {
                model.setLat(1.442);
                model.setLng(1.636);
            });

//            lat = 1.442;
//            lng =  1.636;
        }

    }

    private boolean checkInputs() {

        viewModel.getSignUpLiveData().observe(this, signUpModel -> customModel = signUpModel);
//        SignUpModel customModel = viewModel.getSignUpModel();

        if(customModel.getEdtName().isEmpty() ||
                customModel.getEdtEmail().isEmpty() ||
                customModel.getEdtPassword().isEmpty() ||
                customModel.getEdtPhone().isEmpty() ||
                customModel.getEdtIdentityNumber().isEmpty() ||
                customModel.getEdtSecureCode().isEmpty() ||
                customModel.getEdtDescription().isEmpty()){

            Toasty.error(this , getResources().getString(R.string.signIn_toast_fillFields) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(customModel.getEdtEmail()).matches()){

            Toasty.error(this , getResources().getString(R.string.signIn_toast_currentEmail) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if (!TextUtils.isDigitsOnly(customModel.getEdtIdentityNumber())){

            Toasty.error(this , getResources().getString(R.string.signUp_toast_identity_digit) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if (!TextUtils.isDigitsOnly(customModel.getEdtPhone())){

            Toasty.error(this , getResources().getString(R.string.signUp_toast_phone_digit) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if (!TextUtils.isDigitsOnly(customModel.getEdtSecureCode())){

            Toasty.error(this , getResources().getString(R.string.signUp_toast_secure_digit) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if (customModel.getNumSizePeople() == 0){

            Toasty.error(this , getResources().getString(R.string.signUp_toast_size_people) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if (customModel.getSpinnerCity() == null){

            Toasty.error(this , getResources().getString(R.string.signUp_toast_NoCity) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if (customModel.getAdapter().getItemCount() == 0){ /*crafts aren't empty*/
            Toasty.error(this , getResources().getString(R.string.signUp_toast_NoCrafts) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if (customModel.getImageCioAdpater().getItemCount() == 0){  /*photos aren't empty*/
            Toasty.error(this , getResources().getString(R.string.signUp_toast_NoImage) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if(binding.imgPersom.getDrawable() == null){
            Toasty.error(this , getResources().getString(R.string.signUp_toast_NoSelfPhoto) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else if(!customModel.isCheckedPrivacyConditions()){
            Toasty.error(this , getResources().getString(R.string.signUp_toast_NOACCEPT_PRIVACY_CONDITION) , Toasty.LENGTH_SHORT , true).show();
            return false;
        }
        else
            return true;

    }

    private void loadSpinnerCity() {

        if (Common.isConnectionToInternet(SignUp.this)){

            new CompositeDisposable().add(
                    api.getCities()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listResultModel -> {

                        if (listResultModel != null){
                            if (!listResultModel.getErrorMessage().isEmpty()){

                                try {
                                    String error = AES.decrypt(listResultModel.getErrorMessage());
                                    Log.d("error_message_here" ,error);
                                    Toasty.error(SignUp.this, error, Toast.LENGTH_SHORT , true).show();

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else if (!listResultModel.getResponse().getToken().isEmpty()){
                                if (Common.checkSecureToken(listResultModel.getResponse().getToken())){

                                    //Save the token
                                    Hawk.put("TOKEN" , listResultModel.getResponse().getToken());

                                    //load city spinner
                                    List<String> cities = new ArrayList<>();

                                    for (int i = 0; i < listResultModel.getResponse().getResult().size(); i++) {
                                        try {
                                            cities.add(AES.decrypt(listResultModel.getResponse().getResult().get(i).getName()));

                                            CityModel passCity = new CityModel();
                                            passCity.setId(AES.decrypt(listResultModel.getResponse().getResult().get(i).getId()));
                                            passCity.setName(AES.decrypt(listResultModel.getResponse().getResult().get(i).getName()));
                                            cityModels.add(passCity);

                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    binding.spinnerCity.setItems(cities);
                                    Toasty.success(SignUp.this , getResources().getString(R.string.signup_toast_cityLoadedSuccessfully) , Toasty.LENGTH_SHORT , true).show();

                                }
                                else
                                    Toasty.error(SignUp.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                            }
                            else {
                                //load city spinner
                                List<String> cities = new ArrayList<>();

                                for (int i = 0; i < listResultModel.getResponse().getResult().size(); i++) {
                                    try {
                                        cities.add(AES.decrypt(listResultModel.getResponse().getResult().get(i).getName()));

                                        CityModel passCity = new CityModel();
                                        passCity.setId(AES.decrypt(listResultModel.getResponse().getResult().get(i).getId()));
                                        passCity.setName(AES.decrypt(listResultModel.getResponse().getResult().get(i).getName()));
                                        cityModels.add(passCity);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }                        }

                                binding.spinnerCity.setItems(cities);
                                Toasty.success(SignUp.this , getResources().getString(R.string.signup_toast_cityLoadedSuccessfully) , Toasty.LENGTH_SHORT , true).show();
                            }
                        }

                    }, throwable -> Log.d("error_request_city" , throwable.getMessage()))
            );

        }
        else
            Toasty.info(getApplicationContext() , getResources().getString(R.string.signIn_toast_noInternet) , Toast.LENGTH_LONG).show();
    }

    private void startRecyclerViewCategoryAnimation(RecyclerView recyclerCraft) {

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

    private void loadData() {

        viewModel.getSignUpLiveData().observe(this, model -> {
            MutableLiveData<List<CraftSelectedIModel>> liveData = new MutableLiveData<>();
            liveData.setValue(Common.selectedCraftsData);
            model.adapter = new CraftSelectedAdapter(this , liveData);
        });
    }

    private void validate_hCaptcha(String token , String id) {

        Log.d("pass_token" , token + "");

        SharedPreferences languagepref = getSharedPreferences("language",MODE_PRIVATE);

        HCaptchaConfig config = HCaptchaConfig.builder()
                .siteKey(Common.HCAPTCHA_SITE_KEY)
                .locale(languagepref.getString("languageToLoad","en").equalsIgnoreCase("ar")? "ar" : "en")
                .theme(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES? HCaptchaTheme.DARK : HCaptchaTheme.LIGHT)
                .build();

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

                                            accountId = id;
                                            showPaymentUI();
                                        }
                                        else {
                                            Toast.makeText(this, getResources().getString(R.string.robot), Toast.LENGTH_SHORT).show();
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

    private void getImage() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){

//                            fileUri = getOutputMediaFileUri();
                            Intent intent = new Intent(FileUtils.createGetContentIntent());
                            intent.setType("image/*");
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                            startActivityForResult(Intent.createChooser(intent , "Select a Picture") , REQUEST_GET_IMAGE_GALLERY);

                        }
                        else{
                            Toasty.error(SignUp.this , getResources().getString(R.string.signUp_toast_noGoToGallery) , Toasty.LENGTH_SHORT , true).show();
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();
    }

    private void createAccount() {

        try {
            new CompositeDisposable().add(
                    api.createAccount()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(stringResultModel -> {

                            if (stringResultModel != null){
                                if (!stringResultModel.getErrorMessage().isEmpty()){

                                    try {
                                        String error = AES.decrypt(stringResultModel.getErrorMessage());
                                        Log.d("error_message_here" ,error);
                                        Toasty.error(SignUp.this, error, Toast.LENGTH_SHORT , true).show();

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                    if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                        //Save the token
                                        Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                        //Add id
                                        try {
//                            id = AES.decrypt(stringResultModel.getResponse().getResult());
                                            Toasty.success(SignUp.this , "Created account id successfully" , Toasty.LENGTH_SHORT , true).show();

                                            //Update craftsman's data by given id
                                            String token = Hawk.get("TOKEN");
//                                            String accountIdd = AES.decrypt(stringResultModel.getResponse().getResult());
                                            Log.d("ID_new",stringResultModel.getResponse().getResult());
                                            //registerCraftsman(stringResultModel.getResponse().getResult());
                                            validate_hCaptcha(token , stringResultModel.getResponse().getResult());

                                        }
                                        catch (Exception e) {
                                            Log.d("ERROR_HERE" , e.getMessage());
                                            e.printStackTrace();
                                        }

                                    }
                                    else
                                        Toasty.error(SignUp.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                }
                                else {
                                    //Add id
                                    try {
//                        id = AES.decrypt(stringResultModel.getResponse().getResult());
                                        Toasty.success(SignUp.this , "Created account id successfully" , Toasty.LENGTH_SHORT , true).show();

                                        //Update craftsman's data by given id
                                        String token = Hawk.get("TOKEN");
                                        validate_hCaptcha(token , stringResultModel.getResponse().getResult());

                                    }
                                    catch (Exception e) {
                                        Log.d("ERROR_HERE" , e.getMessage());
                                        e.printStackTrace();
                                    }

                                }
                            }

                        }, throwable -> Log.d("error_request_CrAccount" , throwable.getMessage()))

            );

        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("error_create_account" , e.getMessage());
        }
    }

    private void showPaymentUI() {

        String tokenPass = Hawk.get("TOKEN");
        Log.d("pass_it_token", tokenPass + "");

        new CompositeDisposable().add(
                api.getBraintreeToken("Bearer " + tokenPass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringResultModel -> {

                    if (stringResultModel != null){
                        if (!stringResultModel.getErrorMessage().isEmpty()){

                            try {
                                String error = AES.decrypt(stringResultModel.getErrorMessage());
                                Log.d("error_message_here" ,error);
                                Toasty.error(SignUp.this, error, Toast.LENGTH_SHORT , true).show();

                            }
                            catch (Exception e) {
                                Log.d("error_decrypt" , e.getMessage());
                                e.printStackTrace();
                            }

                        }
                        else if (!stringResultModel.getResponse().getToken().isEmpty()){
                            if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                //Save the token
                                Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                //get token then pass it ti drop ui
                                try {
                                    String braintreeToken = stringResultModel.getResponse().getResult();
                                    Log.d("get_token_success",braintreeToken + "");

                                    DropInRequest dropInRequest = new DropInRequest().clientToken(braintreeToken);
                                    startActivityForResult(dropInRequest.getIntent(SignUp.this) , REQUEST_PAYMENT_CODE);

                                }
                                catch (Exception e) {
                                    Toasty.error(this , "ERROR_HERE" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                    e.printStackTrace();
                                }

                            }
                            else
                                Toasty.error(SignUp.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                        }
                        else {
                            //get token then pass it ti drop ui
                            try {
                                String braintreeToken = stringResultModel.getResponse().getResult();
                                Log.d("get_token_success",braintreeToken + "");

                                DropInRequest dropInRequest = new DropInRequest().clientToken(braintreeToken);
                                startActivityForResult(dropInRequest.getIntent(SignUp.this) , REQUEST_PAYMENT_CODE);

                            }
                            catch (Exception e) {
                                Toasty.error(this , "ERROR_HERE" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                e.printStackTrace();
                            }

                        }
                    }

                }, throwable -> Log.d("error_request_register" , throwable.getMessage()))
        );
    }

    private void  registerCraftsman(String id) {

        viewModel.getSignUpLiveData().observe(this, signUpModel -> {

            try {

                Log.d("getId" , String.valueOf(signUpModel.getSpinnerCity().getId()));
                Log.d("getId" , String.valueOf(signUpModel.getEdtDescription()));
                Log.d("getId" , String.valueOf(signUpModel.getEdtName()));
                Log.d("getId" , String.valueOf(signUpModel.getEdtEmail()));
                Log.d("getId" , String.valueOf(signUpModel.getEdtPassword()));
                Log.d("getId" , String.valueOf(signUpModel.getEdtPhone()));
                Log.d("getId" , String.valueOf(signUpModel.getNumSizePeople()));
                Log.d("getId" , String.valueOf(signUpModel.getLat()));
                Log.d("getId" , String.valueOf(signUpModel.getLng()));
                Log.d("getId" , String.valueOf(signUpModel.getEdtSecureCode()));
                Log.d("getId" , String.valueOf(signUpModel.getEdtIdentityNumber()));
                Log.d("getId" , String.valueOf(signUpModel.getNumMinValueHand()));
                Log.d("getId" , String.valueOf(signUpModel.getNumMinValueHand()));
                Log.d("getId" , String.valueOf(signUpModel.getNumMaxValueHand()));

                String token = Hawk.get("TOKEN");
                Log.d("is_token",token +"");

                new CompositeDisposable().add(
                        api.registerCraftsman(
                                "Bearer " + token,
                                AES.encrypt(signUpModel.getSpinnerCity().getId()),
                                AES.encrypt(signUpModel.getEdtDescription()),
                                AES.encrypt(signUpModel.getEdtName()),
                                AES.encrypt(signUpModel.getEdtEmail()),
                                AES.encrypt(signUpModel.getEdtPassword()),
                                AES.encrypt(signUpModel.getEdtPhone()),
                                AES.encrypt(String.valueOf(signUpModel.getNumSizePeople())),
                                AES.encrypt(String.valueOf(signUpModel.getLat())),
                                AES.encrypt(String.valueOf(signUpModel.getLng())),
                                AES.encrypt(signUpModel.getEdtSecureCode()),
                                AES.encrypt(signUpModel.getEdtIdentityNumber()),
                                AES.encrypt(String.valueOf(signUpModel.getNumMinValueHand())),
                                AES.encrypt(String.valueOf(signUpModel.getNumMaxValueHand())),
                                id
                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(stringResultModel -> {

                                    if (stringResultModel != null){
                                        if (!stringResultModel.getErrorMessage().isEmpty()){

                                            try {
                                                String error = AES.decrypt(stringResultModel.getErrorMessage());
                                                Log.d("error_message_here" ,error);
                                                Toasty.error(SignUp.this, error, Toast.LENGTH_SHORT , true).show();

                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                            if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                                //Save the token
                                                Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                                //register is successful
                                                try {
                                                    String result = AES.decrypt(stringResultModel.getResponse().getResult());
                                                    Toasty.success(this , result , Toasty.LENGTH_SHORT , true).show();

                                                    //Upload certificate ot person or Identity images
                                                    uploadProcess(id);

                                                    //Add the crafts
                                                    addCraft(id);

                                                }
                                                catch (Exception e) {
                                                    Toasty.error(this , "ERROR_HERE" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                                    e.printStackTrace();
                                                }

                                            }
                                            else
                                                Toasty.error(SignUp.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                        }
                                        else {
                                            //register is successful
                                            try {
                                                String result = AES.decrypt(stringResultModel.getResponse().getResult());
                                                Toasty.success(this , result , Toasty.LENGTH_SHORT , true).show();

                                                //Upload certificate ot person or Identity images
                                                uploadProcess(id);

                                                //Add the crafts
                                                addCraft(id);
                                            }
                                            catch (Exception e) {
                                                Toasty.error(this , "ERROR_HERE" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                                e.printStackTrace();
                                            }

                                        }
                                    }

                                }, throwable -> Log.d("error_request_register" , throwable.getMessage()))
                );

            }
            catch (Exception e) {
                Toast.makeText(this, "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

    }

    private void register() {

        //Get craftsman's id
        createAccount();

        //all methods are Moved on createAccount method !!!!!!!!!!
        //registerCraftsman();
        //uploadProcess();
        //addCraft();
    }

    private void addCraft(String id) {

        String token = Hawk.get("TOKEN");
        Log.d("is_token",token +"");

        for (int i = 0; i < Common.selectedCraftsData.size(); i++) {

            try {
                new CompositeDisposable().add(
                        api.addCraft(
                                "Bearer " + token,
                                id,
                                AES.encrypt(String.valueOf(Common.selectedCraftsData.get(i).getCraftModel().getId()))
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(stringResultModel -> {

                            if (stringResultModel != null){
                                if (!stringResultModel.getErrorMessage().isEmpty()){

                                    try {
                                        String error = AES.decrypt(stringResultModel.getErrorMessage());
                                        Log.d("error_message_here" ,error);
                                        Toasty.error(SignUp.this, error, Toast.LENGTH_SHORT , true).show();

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                    if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                        //Save the token
                                        Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                        //register is successful
                                        try {
                                            String result = AES.decrypt(stringResultModel.getResponse().getResult());
                                            Toasty.success(this , result , Toasty.LENGTH_SHORT , true).show();
                                        }
                                        catch (Exception e) {
                                            Toasty.error(this , "ERROR_HERE" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                            e.printStackTrace();
                                        }

                                    }
                                    else
                                        Toasty.error(SignUp.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                }
                                else {
                                    //register is successful
                                    try {
                                        String result = AES.decrypt(stringResultModel.getResponse().getResult());
                                        Toasty.success(this , result , Toasty.LENGTH_SHORT , true).show();
                                    }
                                    catch (Exception e) {
                                        Toasty.error(this , "ERROR_HERE" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                        e.printStackTrace();
                                    }

                                }
                            }

                        }, throwable -> Log.d("error_request_addCraft" , throwable.getMessage()))

                );


            }
            catch (Exception e) {
                Toast.makeText(this, "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    }

    private void uploadProcess(String id) {

        viewModel.getSignUpLiveData().observe(this, signUpModel -> {

            //Upload craftsman's photo
            uploadImage(signUpModel.getImgCraftman() , String.valueOf(0) , String.valueOf(0) , String.valueOf(0) , String.valueOf(1) , id);

            signUpModel.getImageCioAdpater().getLiveDataList().observe(this, imageCioModels -> {

                for (int i = 0; i < imageCioModels.size(); i++) {
                    switch (imageCioModels.get(i).getTypeImage()){
                        case 1:{  //Person
                            uploadImage(imageCioModels.get(i).getImgPath() , String.valueOf(0) , String.valueOf(0) , String.valueOf(0) , String.valueOf(1) , id);
                            break;
                        }
                        case 2:{  //Certificate
                            uploadImage(imageCioModels.get(i).getImgPath() , String.valueOf(0) , String.valueOf(1) , String.valueOf(0) , String.valueOf(0) , id);
                            break;
                        }
                        case 3:{  //Other
                            break;
                        }
                        case 4:{  //Identity
                            uploadImage(imageCioModels.get(i).getImgPath() , String.valueOf(0) , String.valueOf(0) , String.valueOf(1) , String.valueOf(0) , id);
                            break;
                        }
                        default:{
                            break;
                        }
                    }
                }
            });
        });
    }

    private void uploadImage(Uri imgPath, String request_id , String cartificate_type , String identity_type , String personal_type , String personId) {

        try {

//            String fileName = new StringBuilder(UUID.randomUUID().toString().substring(0 , 7))
//                    .append("_craftsman_")
//                    .append(AES.decrypt(Common.craftmanData.getId()))
//                    .append(FileUtils.getExtension(file.toString())).toString();


            Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()){

                                try {
                                    File file = FileUtils.getFile(SignUp.this , imgPath);

                                    String token = Hawk.get("TOKEN");
                                    Log.d("is_token",token +"");

                                    Ion.with(SignUp.this)
                                            .load("POST",Common.BASE_URL + "signup/upload")
                                            .setHeader("Authorization" , "Bearer " + token)
                                            .setMultipartParameter("request_id", AES.encrypt(request_id))
                                            .setMultipartParameter("cartificate_type", AES.encrypt(cartificate_type))
                                            .setMultipartParameter("identity_type", AES.encrypt(identity_type))
                                            .setMultipartParameter("personal_type", AES.encrypt(personal_type))
                                            .setMultipartParameter("personId", personId)
                                            .setMultipartFile("photo",  file)
                                            .asJsonObject()
                                            .setCallback((e, result) -> {

                                                if (e != null){
                                                    Toast.makeText(SignUp.this, "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                }
                                                else {

                                                    Type type = new TypeToken<ResultModel<String>>(){}.getType();
                                                    Gson gson = new Gson();
                                                    ResultModel<String> response = gson.fromJson(result , type);

                                                    if (response != null){
                                                        if (!response.getErrorMessage().isEmpty()){

                                                            try {
                                                                String error = AES.decrypt(response.getErrorMessage());
                                                                Log.d("error_message_here" ,error);
                                                            }
                                                            catch (Exception e1) {
                                                                e1.printStackTrace();
                                                            }

                                                        }
                                                        else if (!response.getResponse().getToken().isEmpty()){
                                                            if (Common.checkSecureToken(response.getResponse().getToken())){

                                                                //Save the token
                                                                Hawk.put("TOKEN" , response.getResponse().getToken());

                                                                //register is successful
                                                                try {
                                                                    String resultEnd = AES.decrypt(response.getResponse().getResult());
                                                                    Toasty.success(SignUp.this , resultEnd , Toasty.LENGTH_SHORT , true).show();
                                                                }
                                                                catch (Exception e1) {
                                                                    Toasty.error(SignUp.this , "ERROR_HERE" + e1.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                            else
                                                                Toasty.error(SignUp.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                                        }
                                                        else {
                                                            //register is successful
                                                            try {
                                                                String resultEnd = AES.decrypt(response.getResponse().getResult());
                                                                Toasty.success(SignUp.this , resultEnd , Toasty.LENGTH_SHORT , true).show();
                                                            }
                                                            catch (Exception e1) {
                                                                Toasty.error(SignUp.this , "ERROR_HERE" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                                                e1.printStackTrace();
                                                            }

                                                        }
                                                    }
                                                }
                                            });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else
                                Toast.makeText(SignUp.this, getResources().getString(R.string.signUp_toast_noPermissionsStorage), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        }
                    })
                    .check();

        } catch (Exception e) {
            Toast.makeText(this, "ERROR_HERE : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendPayment(Map<String,String> param) {

        String token = Hawk.get("TOKEN");

        try {
            new CompositeDisposable().add(
                    api.checkout("Bearer " + token,
                            AES.encrypt(param.get("nonce")),
                            AES.encrypt(param.get("amount")))

                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(stringResultModel -> {

                                if (stringResultModel != null){
                                    if (!stringResultModel.getErrorMessage().isEmpty()){

                                        try {
                                            String error = AES.decrypt(stringResultModel.getErrorMessage());
                                            Log.d("error_message_here" ,error);
                                            Toasty.error(SignUp.this, error, Toast.LENGTH_SHORT , true).show();

                                        }
                                        catch (Exception e) {
                                            Log.d("error_decrypt" , e.getMessage());
                                            e.printStackTrace();
                                        }

                                    }
                                    else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                        if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                            //Save the token
                                            Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                            //payment process is sucess => register now
                                            try {
                                                Toasty.success(SignUp.this , getResources().getString(R.string.payment_success) , Toasty.LENGTH_SHORT , true).show();
                                                Log.d("payment_success",AES.decrypt(stringResultModel.getResponse().getResult()) + "");

                                                registerCraftsman(accountId);

                                            }
                                            catch (Exception e) {
                                                Toasty.error(this , "ERROR_HERE" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                                e.printStackTrace();
                                            }

                                        }
                                        else
                                            Toasty.error(SignUp.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                    }
                                    else {
                                        //get token then pass it ti drop ui
                                        try {
                                            Toasty.success(SignUp.this , getResources().getString(R.string.payment_success) , Toasty.LENGTH_SHORT , true).show();
                                            Log.d("payment_success",AES.decrypt(stringResultModel.getResponse().getResult()) + "");

                                            registerCraftsman(accountId);

                                        }
                                        catch (Exception e) {
                                            Toasty.error(this , "ERROR_HERE" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                            e.printStackTrace();
                                        }

                                    }
                                }

                            }, throwable -> {
                                Log.d("show_error" , throwable.getMessage());
                            })
            );

        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("error_payment" , e.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PAYMENT_CODE){
            if (resultCode == RESULT_OK){

                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();

                String amount = "1000";
                Map<String,String>params = new HashMap<>();

                params.put("amount" , amount);
                params.put("nonce" , strNonce);

                sendPayment(params);

            }
            else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, getResources().getString(R.string.payment_canceled), Toast.LENGTH_SHORT).show();
            else {

                Exception error = (Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e("PAYMENT_ERROR" , error.getMessage());
            }
        }
        else if (requestCode == REQUEST_GET_IMAGE_GALLERY){
            if (resultCode == RESULT_OK){

                viewModel.getSignUpLiveData().observe(SignUp.this , model ->{

                    if (model.getImageCioAdpater() == null){
                        MutableLiveData<List<ImageCioModel>> liveData = new MutableLiveData<>();
                        liveData.setValue(new ArrayList<>());
                        model.setImageCioAdpater(new ImageCioAdpater(SignUp.this  , liveData));
                    }

                    model.getImageCioAdpater().getLiveDataList().observe(SignUp.this , model1 ->{

                        model.getImageCioAdpater().notifyDataSetChanged();
                        if (typeImage == 1){
                            binding.imgPersom.setImageURI(data.getData());
                            model.setImgCraftman(data.getData());
                        }
                        else{
                            model1.add(new ImageCioModel(data.getData() , typeImage));
                            binding.recyclerWorkerPhotos.setAdapter(model.getImageCioAdpater());
                            binding.recyclerWorkerPhotos.getAdapter().notifyDataSetChanged();
                        }
                    });


                });
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Maybe change
        binding.sizeQueue.setNumber(String.valueOf(viewModel.getSignUpModel().getNumSizePeople()));
    }

    @Override
    public void onClickBtnExp(View view) {

        Common.recyclerExp = binding.recyclerExperience;

        if (Common.isConnectionToInternet(this)){

            //Init Fragment Experiences
            craftFragment = new CraftFragment(SignUp.this);
            craftFragment.show(getSupportFragmentManager() , "Select your experience");

        }
        else
            Toasty.info(this, getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickBtnPhoto(View view) {

        if (Common.isConnectionToInternet(this)){

            typeImage = 1;
            getImage();
        }
        else
            Toasty.info(this, getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickBanUploadCertificate(View view) {

        if (Common.isConnectionToInternet(this)){

            typeImage = 2;
            getImage();
        }
        else
            Toasty.info(this, getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickBtnUploadOther(View view) {

        if (Common.isConnectionToInternet(this)){

            typeImage = 3;
            getImage();
        }
        else
            Toasty.info(this, getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickBtnUploadIdentity(View view) {

        if (Common.isConnectionToInternet(this)){

            typeImage = 4;
            getImage();
        }
        else
            Toasty.info(this, getResources().getString(R.string.signIn_toast_noInternet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickBtnRegister(View view) {

        if (checkInputs()){
            if (Common.isConnectionToInternet(this)){
                //validate_reCaptcha();
                register();
            }
            else
                Toasty.info(this , getResources().getString(R.string.signIn_toast_noInternet) , Toasty.LENGTH_SHORT , true).show();
        }

    }

    @Override
    public void onClickPrivacyPolicy(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.privacypolicygenerator.info/live.php?token=00QydaXDtfghLAXq2nuV6vvGCZZbB8yy")); ///////Change it
        startActivity(intent);
    }

    @Override
    public void onClickConditionsTerms(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsandconditionsgenerator.com/live.php?token=F07g4TGgbnbTaQCZ7YCIn8IIm5GPvVvW"));   ///////Change it
        startActivity(intent);
    }

    @Override
    public void onChangedNumberPicker(ElegantNumberButton view) {

        view.setOnValueChangeListener((view1, oldValue, newValue) -> {
            viewModel.getSignUpLiveData().observe(this, model ->{
                if (model.getNumSizePeople() == 0)
                    model.setNumSizePeople(1);
                model.setNumSizePeople(newValue);
                view.setNumber(String.valueOf(newValue));
            });
        });
    }

    @Override
    public void onImagePerson(CircleImageView view, Uri image) {
        CircleImageView circleImageView = (CircleImageView)view;
        viewModel.getSignUpLiveData().observe(this , model ->{
            if (image != null){
                circleImageView.setImageURI(image);
                model.setImgCraftman(image);
            }
        });
    }

    @Override
    public void onSetAdapter(RecyclerView view) {

        loadData(); //Get the selected crafts and call mode.setAdapter() to fill it

        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));
        viewModel.getSignUpLiveData().observe(this, model -> {

//            Common.selectedCrafts.setValue(new ArrayList<>());
            MutableLiveData<List<CraftSelectedIModel>> liveData = new MutableLiveData<>();
            liveData.setValue(Common.selectedCraftsData);
            model.setAdapter(new CraftSelectedAdapter(this , liveData));

            view.setAdapter(model.getAdapter());
            startRecyclerViewCategoryAnimation(view);
        });

    }

    @Override
    public void onSetImageCioAdapter(RecyclerView view) {

        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));
        viewModel.getSignUpLiveData().observe(this , model ->{

            MutableLiveData<List<ImageCioModel>> liveData = new MutableLiveData<>();
            liveData.setValue(new ArrayList<>());
            model.setImageCioAdpater(new ImageCioAdpater(SignUp.this , liveData));

            view.setAdapter(model.getImageCioAdpater());
            startRecyclerViewCategoryAnimation(view);
        });
    }


}