package com.abdalkarimalbiekdev.herafi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.TextView;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Fragment.About.AboutFragment;
import com.abdalkarimalbiekdev.herafi.Fragment.Authentication.AuthenticationsFragment;
import com.abdalkarimalbiekdev.herafi.Fragment.Chart.ChartFragment;
import com.abdalkarimalbiekdev.herafi.Fragment.Profile.ProfileFragment;
import com.abdalkarimalbiekdev.herafi.Fragment.Project.ProjectFragment;
import com.abdalkarimalbiekdev.herafi.Fragment.Request.RequestFragment;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.service.CheckNewRequests;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NavigatorFragment extends AppCompatActivity {

//implements NavigationView.OnNavigationItemSelectedListener
//    private AppBarConfiguration mAppBarConfiguration;
//    public NavController navController;
//    DrawerLayout drawer;
//    NavigationView navigationView;

//    TextView txtName;
//    TextView txtEmail;
//    CircleImageView img_avatar;
//    MaterialDrawerSliderView sliderView;
    BubbleNavigationConstraintView bubbleNavigationConstraintView;
    FragmentManager manager = getSupportFragmentManager();

    private boolean isDoubleClicked = false;


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

        setContentView(R.layout.activity_navigator_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ResourcesCompat.getDrawable(getResources() , R.drawable.ic_menu_more_vert_24 , getTheme()));

        bubbleNavigationConstraintView =  findViewById(R.id.bubble_nav);

        manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.nav_host_fragment , RequestFragment.class, null)
                .setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out)
                .commit();
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.holo_purple));


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

        startService(new Intent(NavigatorFragment.this , CheckNewRequests.class));

        bubbleNavigationConstraintView.setNavigationChangeListener((view, position) -> {

            switch (position){
                case 0:{
                    manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.nav_host_fragment , RequestFragment.class, null)
                            .setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out)
                            .commit();
                    setTitle(getResources().getString(R.string.menu_request));
                    toolbar.setTitleTextColor(getResources().getColor(android.R.color.holo_purple));
                    break;
                }
                case 1:{
                    manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.nav_host_fragment , ProjectFragment.class, null)
                            .setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out)
                            .commit();
                    setTitle(getResources().getString(R.string.menu_project));
                    toolbar.setTitleTextColor(getResources().getColor(android.R.color.holo_green_light));
                    break;
                }
                case 2:{
                    manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.nav_host_fragment , ProfileFragment.class, null)
                            .setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out)
                            .commit();
                    setTitle(getResources().getString(R.string.menu_Profile));
                    toolbar.setTitleTextColor(getResources().getColor(android.R.color.holo_blue_light));
                    break;
                }
                case 3:{
                    manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.nav_host_fragment , AuthenticationsFragment.class, null)
                            .setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out)
                            .commit();
                    setTitle(getResources().getString(R.string.menu_auth));
                    toolbar.setTitleTextColor(getResources().getColor(android.R.color.holo_red_light));
                    break;
                }
                case 4:{
                    manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.nav_host_fragment , ChartFragment.class, null)
                            .setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out)
                            .commit();
                    setTitle(getResources().getString(R.string.menu_chart));
                    toolbar.setTitleTextColor(getResources().getColor(android.R.color.holo_orange_light));
                    break;
                }
//                case R.id.about_toggle:{
//                    manager.beginTransaction()
//                            .replace(R.id.nav_host_fragment , AboutFragment.class, null)
//                            .setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out)
//                            .commit();
//                    setTitle(getResources().getString(R.string.menu_about));
//                    break;
//                }
            }
        });

    }

    public void setLocale(String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        SharedPreferences languagepref = getSharedPreferences("language",MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("languageToLoad",languageCode);
        editor.apply();

        startActivity(new Intent(this , MainActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.navigator_fragment , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.language_en:{
                setLocale("en");
                break;
            }
            case R.id.language_ar:{
                setLocale("ar");
                break;
            }
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        isDoubleClicked = false;
    }

    @Override
    public void onBackPressed() {

        if (isDoubleClicked)
            super.onBackPressed();
        else {
            Toasty.info(this, getResources().getString(R.string.navigation_toast_click_again), Toast.LENGTH_SHORT , true).show();
            isDoubleClicked = true;
        }
    }

}