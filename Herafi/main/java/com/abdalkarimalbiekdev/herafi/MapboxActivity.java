package com.abdalkarimalbiekdev.herafi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.networkModel.LatLngModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.orhanobut.hawk.Hawk;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapboxActivity extends AppCompatActivity {

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private static final String ICON_CRAFTSMAN = "craftsman-icon";
    private static final String SOURCE_ID_Craftsman = "craftsman-source";
    private static final String CRAFTSMAN_LAYOUT = "craftsman-layout";
    private static final String SOURCE_ID_USER = "user-source";
    private static final String ICON_USER = "user-icon";
    private static final String USER_LAYOUT = "user-layout";
    private MapView mapView;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private Point origin;
    private Point destination;
    private LocationComponent locationComponent;

    private HerafiAPI api;

    String craftsmanId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        Mapbox.getInstance(this, Common.MAPBOX_ACCESS_TOKEN);

        setContentView(R.layout.activity_mapbox);

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

        //Get User lat/lng
        ////////////////////////////////////////Use Common don't use getIntent() with mapbox
        String latUser = getIntent().getExtras().getString("Lat");
        String lngUser = getIntent().getExtras().getString("Lng");
        craftsmanId = Common.craftmanData.getId();

        Log.d("lat_user" , latUser);
        Log.d("lng_user" , lngUser);

        //init retrofit & Hawk
        api = Common.getAPI();
        Hawk.init(this).build();



        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

            if (Common.isConnectionToInternet(getApplicationContext())){

                //            //Show craftsman location
                LocationComponentOptions locationComponentOptions =
                        LocationComponentOptions.builder(MapboxActivity.this)
                                .pulseEnabled(true)
                                .pulseColor(Color.WHITE)
                                .pulseAlpha(.4f)
                                .pulseInterpolator(new BounceInterpolator())
                                .build();


                LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                        .builder(MapboxActivity.this, style)
                        .locationComponentOptions(locationComponentOptions)
                        .build();

                locationComponent = mapboxMap.getLocationComponent();
                locationComponent.activateLocationComponent(locationComponentActivationOptions);
                // Enable to make component visible
                if (ActivityCompat.checkSelfPermission(MapboxActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapboxActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationComponent.setLocationComponentEnabled(true);
                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);
                // Set the component's render mode
                locationComponent.setRenderMode(RenderMode.COMPASS);


                getLatLng(style , mapboxMap , latUser , lngUser);

//            Origin   -3.588098, 37.176164   (craftsman)
//            Destination   -3.601845, 37.184080 (user)

            }
            else{
                mapView.onDestroy();
                finish();
                Toasty.info(getApplicationContext() , getResources().getString(R.string.signIn_toast_noInternet) , Toast.LENGTH_LONG , true).show();
            }

        }));

    }

    private void getLatLng(Style style , MapboxMap mapboxMap , String latUser , String lngUser) {

        String token = Hawk.get("TOKEN");


        new CompositeDisposable().add(
                api.getLatLngCraftsman(
                        "Bearer " + token,
                        craftsmanId
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(latLngModelResultModel -> {

                    if (latLngModelResultModel != null){
                        if (!latLngModelResultModel.getErrorMessage().isEmpty()){

                            try {
                                String error = AES.decrypt(latLngModelResultModel.getErrorMessage());
                                Log.d("error_message_here" ,error);
                                Toasty.error(this, "Error Happened :\n" + error, Toast.LENGTH_SHORT , true).show();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        else if (!latLngModelResultModel.getResponse().getToken().isEmpty()) {
                            if (Common.checkSecureToken(latLngModelResultModel.getResponse().getToken())){

                                //Save the token
                                Hawk.put("TOKEN" , latLngModelResultModel.getResponse().getToken());

                                //Start mapbox
                                Log.d("get_it_data" , "OK");
                                String latCraftsman = AES.decrypt(latLngModelResultModel.getResponse().getResult().getLat());
                                String lngCraftsman = AES.decrypt(latLngModelResultModel.getResponse().getResult().getLng());

                                Log.d("lat_craftsman" , latCraftsman);
                                Log.d("lng_craftsman" , lngCraftsman);

                                origin = Point.fromLngLat(Float.parseFloat(lngCraftsman), Float.parseFloat(latCraftsman));
                                destination = Point.fromLngLat(Float.parseFloat(lngUser), Float.parseFloat(latUser));

                                CameraPosition position = new CameraPosition.Builder()
                                        .target(new LatLng(destination.latitude() , destination.longitude()))
                                        .zoom(12)
                                        .tilt(13)
                                        .build();
                                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),2500);



                                initSource(style);
                                //initLayers(style);
                                // Get the directions route from the Mapbox Directions API
                                Log.d("start_route","OK");
                                getRoute(mapboxMap, origin, destination);

                                //startMapbox(latCraftsman,lngCraftsman,latUser,lngUser);


                            }
                            else
                                Toasty.error(MapboxActivity.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                        }
                        else {
                            //Start mapbox
                            Log.d("get_it_data" , "OK");
                            String latCraftsman = AES.decrypt(latLngModelResultModel.getResponse().getResult().getLat());
                            String lngCraftsman = AES.decrypt(latLngModelResultModel.getResponse().getResult().getLng());

                            Log.d("lat_craftsman" , latCraftsman);
                            Log.d("lng_craftsman" , lngCraftsman);

                            origin = Point.fromLngLat(Float.parseFloat(lngCraftsman), Float.parseFloat(latCraftsman));
                            destination = Point.fromLngLat(Float.parseFloat(lngUser), Float.parseFloat(latUser));

                            CameraPosition position = new CameraPosition.Builder()
                                    .target(new LatLng(destination.latitude() , destination.longitude()))
                                    .zoom(12)
                                    .tilt(13)
                                    .build();
                            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),2500);



                            initSource(style);
                            //initLayers(style);
                            // Get the directions route from the Mapbox Directions API
                            Log.d("start_route","OK");
                            getRoute(mapboxMap, origin, destination);

                            //startMapbox(latCraftsman,lngCraftsman,latUser,lngUser);
                        }
                    }

                }, throwable -> Log.d("throwable_error" , throwable.getMessage()))
        );
    }

    private void startMapbox(String latCraftsman, String lngCraftsman, String latUser, String lngUser) {

//        Log.d("start_mapbox","OK");

//        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
//
//            //Origin   -3.588098, 37.176164   (craftsman)
//            //Destination   -3.601845, 37.184080 (user)
//
//            origin = Point.fromLngLat(Float.parseFloat(lngCraftsman), Float.parseFloat(latCraftsman));
//            destination = Point.fromLngLat(Float.parseFloat(lngUser), Float.parseFloat(latUser));
//
//            Log.d("start_origin_destinat","OK");
//
//            //Show craftsman location
//            LocationComponentOptions locationComponentOptions =
//                    LocationComponentOptions.builder(MapboxActivity.this)
//                            .pulseEnabled(true)
//                            .pulseColor(Color.WHITE)
//                            .pulseAlpha(.4f)
//                            .pulseInterpolator(new BounceInterpolator())
//                            .build();
//
//
//            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
//                    .builder(MapboxActivity.this, style)
//                    .locationComponentOptions(locationComponentOptions)
//                    .build();
//
//            locationComponent = mapboxMap.getLocationComponent();
//            locationComponent.activateLocationComponent(locationComponentActivationOptions);
//            // Enable to make component visible
//            if (ActivityCompat.checkSelfPermission(MapboxActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapboxActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationComponent.setLocationComponentEnabled(true);
//            // Set the component's camera mode
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//            // Set the component's render mode
//            locationComponent.setRenderMode(RenderMode.COMPASS);
//
//            Log.d("get_location","OK");
//
//            CameraPosition position = new CameraPosition.Builder()
//                    .target(new LatLng(destination.latitude() , destination.longitude()))
//                    .zoom(12)
//                    .tilt(13)
//                    .build();
//            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),2500);
//
//            Log.d("zoom_camera","OK");
//
//
//
//
//            initSource(style);
////            initLayers(style);
//            // Get the directions route from the Mapbox Directions API
//            Log.d("start_route","OK");
//            getRoute(mapboxMap, origin, destination);
//
//        }));


    }

    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {

        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(Common.MAPBOX_ACCESS_TOKEN)
                .build();

        Log.d("init_client","OK");

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                // You can get the generic HTTP info about the response
                Log.d("Response code: " , String.valueOf(response.code()));
                if (response.body() == null) {
                    Log.d("Response_is_null" , "No routes found, make sure you set the right user and access token.");
                    return;
                }
                else if (response.body().routes().size() < 1) {
                    Log.d("Response_no_routes" , "No routes found");
                    return;
                }

                Log.d("get_route","OK");
                // Get the directions route
                currentRoute = response.body().routes().get(0);

                // Make a toast which displays the route's distance
                Toasty.success(MapboxActivity.this,
                        getResources().getString(R.string.mapbox_toast_distance) + currentRoute.distance() + "m", Toast.LENGTH_SHORT, true).show();

                if (mapboxMap != null) {
                    mapboxMap.getStyle(style -> {

                        // Retrieve and update the source designated for showing the directions route
                        GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                        Log.d("put_route1","OK");
                        // Create a LineString with the directions route's geometry and
                        // reset the GeoJSON source for the route LineLayer source
                        if (source != null) {
                            source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            Log.d("put_route2","OK");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.d("Error_map" , throwable.getMessage());
                Toast.makeText(MapboxActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initLayers(Style style) {

        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(3f),
                lineColor(Color.parseColor("#E30606"))
        );
        style.addLayer(routeLayer);

        // Add the red marker icon image to the map
        style.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.ic_location_prepare_24)));

        // Add the red marker icon SymbolLayer to the map
        style.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));

    }

    private void initSource(Style style) {

        Log.d("init1","OK");

        style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));




        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(3f),
                lineColor(Color.parseColor("#E30606"))
        );
        style.addLayer(routeLayer);


        Log.d("init2","OK");

        //Setup the source (Craftsman's lat/lng on the map)
        GeoJsonSource iconGeoJsonSourceCraftsman = new GeoJsonSource(SOURCE_ID_Craftsman, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude()))}));
        style.addSource(iconGeoJsonSourceCraftsman);

        Log.d("init3","OK");

        //Setup the image (Craftsman's image)
        style.addImage(ICON_CRAFTSMAN, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.ic_focus_worder_24)));

        Log.d("init4","OK");

        //pass source and image to map
        style.addLayer(new SymbolLayer(CRAFTSMAN_LAYOUT, SOURCE_ID_Craftsman).withProperties(
                iconImage(ICON_CRAFTSMAN),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));



        Log.d("init5","OK");

        //Setup the source (User's lat/lng on the map)
        GeoJsonSource iconGeoJsonSourceUser = new GeoJsonSource(SOURCE_ID_USER, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        style.addSource(iconGeoJsonSourceUser);

        Log.d("init6","OK");

        //Setup the image (Craftsman's image)
        style.addImage(ICON_USER, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.ic_focused_profile_30)));

        Log.d("init7","OK");

        //pass source and image to map
        style.addLayer(new SymbolLayer(USER_LAYOUT, SOURCE_ID_USER).withProperties(
                iconImage(ICON_USER),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));

        Log.d("init8","OK");

        Log.d("Complete_data","OK");

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel the Directions API request
        if (client != null) {
            client.cancelCall();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}