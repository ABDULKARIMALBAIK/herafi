package com.abdalkarimalbiekdev.herafi.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Common.NotificationHelper;
import com.abdalkarimalbiekdev.herafi.Fragment.Request.RequestFragment;
import com.abdalkarimalbiekdev.herafi.NavigatorFragment;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.SignIn;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.orhanobut.hawk.Hawk;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CheckNewRequests extends Service {

    Timer timer;

    HerafiAPI api;

    public CheckNewRequests() {

        api = Common.getAPI();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                checkNewRequestsAPI();
            }
        } , 10000 , 15000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void checkNewRequestsAPI() {

        String token = Hawk.get("TOKEN");

        new CompositeDisposable().add(
                api.countRequest("Bearer " + token , Common.craftmanData.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringResultModel -> {

                    if (stringResultModel != null){
                        if (!stringResultModel.getErrorMessage().isEmpty()){

                            try {
                                String errorMessage = AES.decrypt(stringResultModel.getErrorMessage());
                                Log.d("error_message_here" , errorMessage);
                                Toasty.error(CheckNewRequests.this, errorMessage, Toast.LENGTH_SHORT , true).show();

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        else if (!stringResultModel.getResponse().getToken().isEmpty()){
                            if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                //Save the token
                                Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                //check count of requests
                                try {
                                    Log.d("new_Requests" , "OK");
                                    int newRequest = Integer.parseInt(AES.decrypt(stringResultModel.getResponse().getResult()));
                                    Log.d("new_Requests" , String.valueOf(newRequest));
                                    checkValue(newRequest);
                                }
                                catch (Exception e) {
                                    Toasty.error(CheckNewRequests.this , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toasty.error(CheckNewRequests.this, getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();                         }
                        else {
                            //check count of requests
                            try {
                                Log.d("new_Requests" , "OK");
                                int newRequest = Integer.parseInt(AES.decrypt(stringResultModel.getResponse().getResult()));
                                Log.d("new_Requests" , String.valueOf(newRequest));
                                checkValue(newRequest);
                            }
                            catch (Exception e) {
                                Toasty.error(CheckNewRequests.this , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                e.printStackTrace();
                            }
                        }
                    }

                }, throwable -> Log.d("request_error_login" , throwable.getMessage())));

    }

    private void checkValue(int newRequests) {

        if (newRequests > Common.lastNewBooking) {

            try {
                String craftsmanName = AES.decrypt(Common.craftmanData.getName());

                sendNotificationToCraftsman(
                        getResources().getString(R.string.service_toast_1) + craftsmanName + getResources().getString(R.string.service_toast_2) + (newRequests - Common.lastNewBooking),
                        getResources().getString(R.string.service_toast_3));

                Common.lastNewBooking = newRequests;
            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("error: " , e.getMessage());
            }



        }
    }

    private void sendNotificationToCraftsman(String message , String title) {

        //Send Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotificationAPI26(message , title);
        else
            sendNotification(message , title);

    }

    private void sendNotification(String message , String title) {

        Intent intent = new Intent(this , NavigatorFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0 , intent ,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_start)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0  , builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotificationAPI26(String message , String title) {

        PendingIntent pendingIntent;   //Message to open alarm system app
        NotificationHelper helper;
        Notification.Builder builder;  //Object Notification

        //Create message
        Intent intent = new Intent(this , NavigatorFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //if activity is created before => clear alarm system activity and create new activity and make it in top
        pendingIntent = PendingIntent.getActivity(this , 0 , intent ,
                PendingIntent.FLAG_UPDATE_CURRENT); //if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); //set default sound of system
        helper = new NotificationHelper(this);
        builder = helper.getAlarmSystemChannelNotification(title,
                message,
                pendingIntent,
                defaultSoundUri);

        //Show notification
        helper.getManager().notify(new Random().nextInt() , builder.build());

    }


}