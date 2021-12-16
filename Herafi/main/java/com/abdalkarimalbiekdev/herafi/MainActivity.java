package com.abdalkarimalbiekdev.herafi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import es.dmoral.toasty.Toasty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.databinding.ActivityMainBinding;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.orhanobut.hawk.Hawk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    HerafiAPI api = Common.getAPI();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/teko.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        Hawk.init(this).build();

        FacebookSdk.sdkInitialize(MainActivity.this);
        AppEventsLogger.activateApp(this);

        binding = DataBindingUtil.setContentView(this , R.layout.activity_main);

        binding.title.setTypeface(Typeface.createFromAsset(getAssets() , "fonts/giddyupstd.otf"));

        initViews();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        //Change soft-key Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setNavigationBarColor(getResources().getColor(R.color.black_item));
            }
        }


        //Test Get Data
        Toasty.info(this, "Start get data", Toast.LENGTH_SHORT , true).show();
        if (Common.isConnectionToInternet(this)){
            getAccessToken();
        }
        else
            Toasty.info(this , getResources().getString(R.string.signIn_toast_noInternet) , Toasty.LENGTH_SHORT , true).show();

        printKeyHash();

    }

    private void initViews() {
        //binding.title.setTypeface(Typeface.createFromAsset(getAssets() , "fonts/...................."));
        binding.txtslogan.setText(getResources().getString(R.string.main_slogan));
        binding.txtslogan.setLetterDuration(10);
        binding.shimmerLayout.startShimmerAnimation();
    }


    public void getAccessToken(){

        try {

            String encId = AES.encrypt(String.valueOf(104));/////////////////////////////////////////////////////////////////
            Log.d("MY_ID" ,encId);

            new CompositeDisposable().add(

                    api.getAccessToken(encId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(stringResultModel -> {

                                try {
                                    if (stringResultModel != null){

                                        if (!stringResultModel.errorMessage.isEmpty()){
                                            Log.d("error_fill", stringResultModel.errorMessage);
                                            new Handler().postDelayed(this::getAccessToken, 4000);
                                        }
                                        else{
                                            if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                                Toasty.info(this, "Token is right", Toast.LENGTH_SHORT).show();
                                                Log.d("verify_token" , "Token is right");

//                                                Common.token = stringResultModel.getResponse().getToken();
                                                Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                                new Handler().postDelayed(() -> {
                                                    startActivity(new Intent(MainActivity.this , SignIn.class));
                                                    finish();
                                                }, 3000);


                                            }
                                            else {
                                                Toasty.info(this, "Token is hacker", Toast.LENGTH_SHORT).show();
                                                Log.d("verify_not_token" , "Token is hacker");
                                                new Handler().postDelayed(this::getAccessToken, 4000);
                                            }

//
//                                            String craftsmanId = AES.decrypt(stringResultModel.response.result);
//                                            Toasty.info(this, "Get data here", Toast.LENGTH_SHORT , true).show();
//                                            Log.d("Get_data" , craftsmanId);
//                                            Log.d("Get_token" , stringResultModel.response.token);
//
//
//                                            Claims payload = Jwts.parserBuilder()
//                                                    .setSigningKey(Common.getPublicKey())
//                                                    .build()
//                                                    .parseClaimsJws(stringResultModel.response.token)
//                                                    .getBody();
//
//                                            if (AES.decrypt(Common.SECRET_KEY_WORD).equals(AES.decrypt(payload.get("secretKeyword" , String.class)))){
//
//                                                Toasty.info(this, "Token is right", Toast.LENGTH_SHORT).show();
//                                                Log.d("verify_token" , "Token is right");
//
////                                                Common.token = stringResultModel.getResponse().getToken();
//                                                Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());
//
//                                                new Handler().postDelayed(() -> {
//                                                    startActivity(new Intent(MainActivity.this , SignIn.class));
//                                                    finish();
//                                                }, 3000);
//
//                                            }
//                                            else{
//                                                Toasty.info(this, "Token is hacker", Toast.LENGTH_SHORT).show();
//                                                Log.d("verify_not_token" , "Token is hacker");
//                                                new Handler().postDelayed(this::getAccessToken, 4000);
//                                            }

                                        }
                                    }
                                    else{
                                        Log.d("check data","data is null here");
                                        new Handler().postDelayed(this::getAccessToken, 4000);
                                    }

                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                    Log.d("request_error2" , e.getMessage());
                                    System.out.println("error message here : " + e.getMessage());
                                    new Handler().postDelayed(this::getAccessToken, 4000);
                                }


                            }, throwable -> {
                                Log.d("request_error_token" , throwable.getMessage());
                                new Handler().postDelayed(this::getAccessToken, 4000);
                            })

            );

        }
        catch (Exception e) {
            Log.d("first_error" , e.getMessage());
            System.out.println("error here : " + e.getMessage());
        }

    }

    private void printKeyHash() {

        try {

            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo("com.abdalkarimalbiekdev.herafi",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures){

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash" , Base64.encodeToString(md.digest() , Base64.DEFAULT));

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.txtslogan.setText(getResources().getString(R.string.main_slogan));
        binding.txtslogan.setLetterDuration(10);
        binding.shimmerLayout.startShimmerAnimation();

    }

    @Override
    protected void onPause() {
        super.onPause();

        binding.shimmerLayout.stopShimmerAnimation();
    }
}