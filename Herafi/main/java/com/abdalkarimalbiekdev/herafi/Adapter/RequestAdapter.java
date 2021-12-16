package com.abdalkarimalbiekdev.herafi.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.AgoraVideoActivity;
import com.abdalkarimalbiekdev.herafi.Common.Common;
import com.abdalkarimalbiekdev.herafi.Dialog.DialogAcceptRefuse;
import com.abdalkarimalbiekdev.herafi.Dialog.DialogBarcodeReader;
import com.abdalkarimalbiekdev.herafi.Dialog.DialogQRCode;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.AcceptRefuseListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.BarcodeReaderListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.ClickYesListener;
import com.abdalkarimalbiekdev.herafi.MapboxActivity;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.SignIn;
import com.abdalkarimalbiekdev.herafi.databinding.FragmentRequestBinding;
import com.abdalkarimalbiekdev.herafi.model.RequestItemModel;
import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.ViewHolder.RequestViewHolder;
import com.abdalkarimalbiekdev.herafi.model.RequestModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;
import com.abdalkarimalbiekdev.herafi.viewModel.RequestViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.orhanobut.hawk.Hawk;

import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RequestAdapter extends RecyclerView.Adapter<RequestViewHolder> {

    Context context;
    MutableLiveData<List<RequestItemModel>> requestLiveData;
    FragmentRequestBinding binding;
    RequestViewModel viewModel;

    HerafiAPI api;

    public RequestAdapter(Context context, MutableLiveData<List<RequestItemModel>> requestLiveData ,
                          FragmentRequestBinding binding , RequestViewModel viewModel) {
        this.context = context;
        this.requestLiveData = requestLiveData;
        this.binding = binding;
        this.viewModel = viewModel;

        api = Common.getAPI();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {

        requestLiveData.observe((LifecycleOwner) context, model -> {

            RequestItemModel requestItemModel = model.get(position);
            try {
                requestItemModel.setRequestId(AES.decrypt(requestItemModel.getRequestId()));
                requestItemModel.setUserId(AES.decrypt(requestItemModel.getUserId()));
                requestItemModel.setUserName(AES.decrypt(requestItemModel.getUserName()));
                requestItemModel.setUserEmail(AES.decrypt(requestItemModel.getUserEmail()));
                requestItemModel.setUserPhoneNumber(AES.decrypt(requestItemModel.getUserPhoneNumber()));
                requestItemModel.setUserLat(AES.decrypt(requestItemModel.getUserLat()));
                requestItemModel.setUserLng(AES.decrypt(requestItemModel.getUserLng()));
                requestItemModel.setUserImage(AES.decrypt(requestItemModel.getUserImage()));
                requestItemModel.setStatusId(AES.decrypt(requestItemModel.getStatusId()));
                requestItemModel.setStatusName(AES.decrypt(requestItemModel.getStatusName()));
            }
            catch (Exception e){
                e.printStackTrace();
            }

            holder.binding.setRequestItemBinding(requestItemModel);
        });



        holder.binding.getRoot().setOnLongClickListener(v -> {

            //Change the status of request after show dialog and select to change
            requestLiveData.observe((LifecycleOwner) context, requestItemModels -> {

                RequestItemModel model = requestItemModels.get(position);
                Log.d("statusIsDecrypted?" , model.getStatusId() + ".....");

                checkStatus(model.getStatusId() , position);
            });


            return true;
        });


        holder.binding.imgUserPhone.setOnClickListener(v -> {

            Dexter.withActivity((Activity) context)
                    .withPermission(Manifest.permission.CALL_PHONE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            requestLiveData.observe((LifecycleOwner) context, model -> {

                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + model.get(position).getUserPhoneNumber()));
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                    return;
                                }
                                context.startActivity(intent);
                            });

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toasty.error(context , context.getResources().getString(R.string.request_toast_noCallPermission) , Toasty.LENGTH_SHORT , true).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    })
                    .check();

        });

        holder.binding.imgUserMap.setOnClickListener(v -> {
           //Go to map Activity
            String latUser =  requestLiveData.getValue().get(position).getUserLat();
            String lngUser =  requestLiveData.getValue().get(position).getUserLng();
//            Common.latUser = requestLiveData.getValue().get(position).getUserLat();
//            Common.latUser = requestLiveData.getValue().get(position).getUserLat();


            Intent intent = new Intent(context , MapboxActivity.class);
            intent.putExtra("Lat" , latUser);
            intent.putExtra("Lng" , lngUser);
            context.startActivity(intent);
        });



        holder.binding.imgVideo.setOnClickListener(v ->{
            context.startActivity(new Intent(context , AgoraVideoActivity.class));
        });
    }

    private void checkStatus(String statusId , int position) {

        Log.d("positon_value" , position + "");
        switch (statusId){
            case "1":{
                //Accept or Refuse the request
                Log.d("pass1" , position + "");
                showDialogAcceptRefuse(position);
                break;
            }
            case "2":{
                //Show QR Code (user will scan) , Worker incoming => Start
                Log.d("pass2" , position + "");
                showDialogQRCode(position);
                break;
            }
            case "3":{
                //Show Barcode Reader (craftsman will scan) Start => finish
                Log.d("pass3" , position + "");
                showDialogBarcodeReader(position);
                break;
            }
        }
    }

    private void showDialogBarcodeReader(int position) {

        DialogBarcodeReader barcodeReader = new DialogBarcodeReader((Activity) context);
        barcodeReader.setListener(value -> getCraftsmanCodeApi(value , position , barcodeReader));

    }

    private void getCraftsmanCodeApi(String value , int position , DialogBarcodeReader barcodeReader) {

        requestLiveData.observe((LifecycleOwner) context, requestItemModels -> {

            RequestItemModel model = requestItemModels.get(position);

            Log.d("show_request_id" , model.getRequestId() + "...");

            try {
                String token = Hawk.get("TOKEN");
                String encRequestId = AES.encrypt(model.getRequestId());

                new CompositeDisposable().add(
                        api.getCraftsmanCode(
                                "Bearer " + token,
                                encRequestId
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(stringResultModel -> {

                            if (stringResultModel != null){
                                if (!stringResultModel.getErrorMessage().isEmpty()){

                                    try {
                                        String errorMessage = AES.decrypt(stringResultModel.getErrorMessage());
                                        Log.d("error_message_here" , errorMessage);
                                        Toasty.error(context, errorMessage, Toast.LENGTH_SHORT , true).show();

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                    if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                        //Save the token
                                        Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                        //verify qrcode
                                        try {
                                            String craftsmanCode = AES.decrypt(stringResultModel.getResponse().getResult());

                                            //Barcode Reader read qrcode is the sam with qrcode in database
                                            if (craftsmanCode.equalsIgnoreCase(value)){
                                                Toasty.success(context , context.getResources().getString(R.string.barcodeReader_dialog_toast_right_qrcode) , Toasty.LENGTH_SHORT , true).show();
                                                //Update status of request to finish
                                                finishWork(position , barcodeReader);
                                                loadRequests(binding.recyclerRequest);
                                            }
                                            else {
                                                Toasty.success(context , context.getResources().getString(R.string.barcodeReader_dialog_toast_wrong_qrcode) , Toasty.LENGTH_SHORT , true).show();
                                            }
                                        }
                                        catch (Exception e) {
                                            Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                            e.printStackTrace();
                                        }
                                    }
                                    else
                                        Toasty.error(context, context.getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                }
                                else {
                                    //verify qrcode
                                    try {
                                        String craftsmanCode = AES.decrypt(stringResultModel.getResponse().getResult());
                                        Log.d("craftsmanCode",craftsmanCode);

                                        //Barcode Reader read qrcode is the sam with qrcode in database
                                        if (craftsmanCode.equalsIgnoreCase(value)){
                                            Toasty.success(context , context.getResources().getString(R.string.barcodeReader_dialog_toast_right_qrcode) , Toasty.LENGTH_SHORT , true).show();
                                            //Update status of request to finish
                                            finishWork(position , barcodeReader);
                                            loadRequests(binding.recyclerRequest);
                                        }
                                        else {
                                            Toasty.success(context , context.getResources().getString(R.string.barcodeReader_dialog_toast_wrong_qrcode) , Toasty.LENGTH_SHORT , true).show();
                                        }
                                    }
                                    catch (Exception e) {
                                        Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }, throwable -> Log.d("error_request_google" , throwable.getMessage()))
                );

            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("error_here" , e.getMessage());
            }


        });


    }

    private void finishWork(int position , DialogBarcodeReader barcodeReader) {

        requestLiveData.observe((LifecycleOwner) context, requestItemModels -> {

            RequestItemModel model = requestItemModels.get(position);
            Log.d("show_request_id" , model.getRequestId() + "...");
            Log.d("show_user_id" , model.getUserId() + "...");

            try {
                String token = Hawk.get("TOKEN");
                String encRequestId = AES.encrypt(model.getRequestId());
                String encUserId = AES.encrypt(model.getUserId());

                new CompositeDisposable().add(
                        api.finishWork(
                                "Bearer " + token,
                                Common.craftmanData.getId(),
                                encUserId,
                                encRequestId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(stringResultModel -> {

                            if (stringResultModel != null){
                                if (!stringResultModel.getErrorMessage().isEmpty()){

                                    try {
                                        String errorMessage = AES.decrypt(stringResultModel.getErrorMessage());
                                        Log.d("error_message_here" , errorMessage);
                                        Toasty.error(context, errorMessage, Toast.LENGTH_SHORT , true).show();

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                    if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                        //Save the token
                                        Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                        //Show message
                                        try {
                                            barcodeReader.dialog.dismiss();
                                            loadRequests(binding.recyclerRequest);
                                            Toasty.success(context , context.getResources().getString(R.string.barcodeReader_dialog_toast_finish) , Toasty.LENGTH_SHORT , true).show();
                                        }
                                        catch (Exception e) {
                                            Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                            e.printStackTrace();
                                        }
                                    }
                                    else
                                        Toasty.error(context, context.getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                }
                                else {
                                    //Show message
                                    try {
                                        barcodeReader.dialog.dismiss();
                                        loadRequests(binding.recyclerRequest);
                                        Toasty.success(context , context.getResources().getString(R.string.barcodeReader_dialog_toast_finish) , Toasty.LENGTH_SHORT , true).show();
                                    }
                                    catch (Exception e) {
                                        Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }, throwable -> Log.d("error_request_google" , throwable.getMessage()))
                );

            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("error_here" , e.getMessage());
            }

        });
    }

    private void showDialogQRCode(int position) {

        DialogQRCode dialogQRCode = new DialogQRCode(context);

        dialogQRCode.setListener(() -> getQRCodeApi(position , dialogQRCode));

        dialogQRCode.txtCancel.setOnClickListener(v ->{

            dialogQRCode.dialog.dismiss();
            loadRequests(binding.recyclerRequest);
        });

        ////////////////////////////////////Setup websocket (when the qrcode is scanned , receive message from other side then close the dialog)
        dialogQRCode.loadQRCode();


    }


    private void configNoData() {
        binding.imgBackground.setImageResource(R.drawable.no_data);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(context.getResources().getString(R.string.no_requests));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.recyclerRequest.setVisibility(View.GONE);
    }

    private void configGetData() {
        binding.imgBackground.setVisibility(View.GONE);
        binding.textBackground.setVisibility(View.GONE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.recyclerRequest.setVisibility(View.VISIBLE);
    }

    private void configSomeProblem() {
        binding.imgBackground.setImageResource(R.drawable.some_problem);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(context.getResources().getString(R.string.some_problem));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.recyclerRequest.setVisibility(View.GONE);
    }

    private void configNoInternet() {
        binding.imgBackground.setImageResource(R.drawable.no_internet);
        binding.imgBackground.setVisibility(View.VISIBLE);
        binding.textBackground.setText(context.getResources().getString(R.string.you_need_internet));
        binding.textBackground.setVisibility(View.VISIBLE);
        binding.progressBackground.setVisibility(View.GONE);
        binding.recyclerRequest.setVisibility(View.GONE);
    }


    private void loadRequests(RecyclerView view) {

        try {
            String token = Hawk.get("TOKEN");
            Log.d("is_token",token +"");

            new CompositeDisposable().add(
                    api.getRequests(
                            "Bearer " + token,
                            Common.craftmanData.getId()
                    )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(listResultModel -> {

                                if (listResultModel != null){
                                    if (!listResultModel.getErrorMessage().isEmpty()){

                                        try {
                                            String error = AES.decrypt(listResultModel.getErrorMessage());
                                            Log.d("error_message_here" ,error);
                                            Toasty.error(context, error, Toast.LENGTH_SHORT , true).show();

                                            configSomeProblem();

                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                            configSomeProblem();
                                        }

                                    }
                                    else if (listResultModel.getResponse().getResult().size() <= 0){
                                        configNoData();
                                    }
                                    else if (!listResultModel.getResponse().getToken().isEmpty()){

                                        if (Common.checkSecureToken(listResultModel.getResponse().getToken())){

                                            configGetData();

                                            //Save the token
                                            Hawk.put("TOKEN" , listResultModel.getResponse().getToken());
                                            //Add it to adapter
                                            MutableLiveData<List<RequestItemModel>> liveData = new MutableLiveData<>();
                                            liveData.setValue(listResultModel.getResponse().getResult());

                                            RequestModel model = new RequestModel();
                                            model.setAdapter(new RequestAdapter(context , liveData , binding , viewModel));

                                            MutableLiveData<RequestModel> requestModel = new MutableLiveData<>();
                                            requestModel.setValue(model);

                                            view.setAdapter(model.getAdapter());
                                            startRecyclerViewCategoryAnimation(view);

                                            viewModel.setActivity((Activity) context , requestModel);



//                                viewModel.getRequestLiveData().observe(getActivity(), requestModel1 -> {
////                                    if (requestModel1.getAdapter() == null)
////                                        view.setAdapter(new RequestAdapter(getActivity() , new MutableLiveData<>()));
//
//                                    view.setAdapter(requestModel1.getAdapter());
//                                    startRecyclerViewCategoryAnimation(view);
//
//                                });
                                        }
                                        else{
                                            Toasty.error(context, context.getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                            configSomeProblem();
                                        }

                                    }
                                    else {
                                        configGetData();

                                        //Add it to adapter
                                        MutableLiveData<List<RequestItemModel>> liveData = new MutableLiveData<>();
                                        liveData.setValue(listResultModel.getResponse().getResult());

                                        RequestModel model = new RequestModel();
                                        model.setAdapter(new RequestAdapter(context , liveData , binding , viewModel));

                                        MutableLiveData<RequestModel> requestModel = new MutableLiveData<>();
                                        requestModel.setValue(model);

                                        view.setAdapter(model.getAdapter());
                                        startRecyclerViewCategoryAnimation(view);

                                        viewModel.setActivity((Activity) context, requestModel);

//                            viewModel.getRequestLiveData().observe(getActivity(), requestModel1 -> {
////                                if (requestModel1.getAdapter() == null)
////                                    view.setAdapter(new RequestAdapter(getActivity() , new MutableLiveData<>()));
//
//                                view.setAdapter(requestModel1.getAdapter());
//                                startRecyclerViewCategoryAnimation(view);
//
//                            });
                                    }
                                }

                            }, throwable -> {
                                Log.d("error_request_requests" , throwable.getMessage());
                                configSomeProblem();
                            })

            );

        }
        catch (Exception e){
            e.printStackTrace();
            configSomeProblem();
            Toast.makeText(context, "ERROR_HERE" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void getQRCodeApi(int position , DialogQRCode dialogQRCode) {

        requestLiveData.observe((LifecycleOwner) context, requestItemModels -> {

            RequestItemModel model = requestItemModels.get(position);
            Log.d("show_request_id" , model.getRequestId() + "...");


            try {
                String token = Hawk.get("TOKEN");
                String encRequestId = AES.encrypt(model.getRequestId());

                new CompositeDisposable().add(
                        api.getUserCode(
                                "Bearer " + token,
                                encRequestId
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(stringResultModel -> {

                            if (stringResultModel != null){
                                if (!stringResultModel.getErrorMessage().isEmpty()){

                                    try {
                                        String errorMessage = AES.decrypt(stringResultModel.getErrorMessage());
                                        Log.d("error_message_here" , errorMessage);
                                        Toasty.error(context, errorMessage, Toast.LENGTH_SHORT , true).show();

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                    if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                        //Save the token
                                        Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                        //Show QRCode
                                        try {
                                            String qrCode = AES.decrypt(stringResultModel.getResponse().getResult());
                                            Log.d("qr_code_show" , qrCode);

                                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                            BitMatrix bitMatrix = multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE , 200 , 200);
                                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                            dialogQRCode.imgQRCode.setImageBitmap(bitmap);

                                            Toasty.success(context , "Get the QR Code" , Toasty.LENGTH_SHORT , true).show();
                                        }
                                        catch (Exception e) {
                                            Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                            e.printStackTrace();
                                        }
                                    }
                                    else
                                        Toasty.error(context, context.getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                }
                                else {
                                    //Show QRCode
                                    try {
                                        String qrCode = AES.decrypt(stringResultModel.getResponse().getResult());
                                        Log.d("qr_code_show" , qrCode);

                                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                        BitMatrix bitMatrix = multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE , 200 , 200);
                                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                        dialogQRCode.imgQRCode.setImageBitmap(bitmap);

                                        Toasty.success(context , "Get the QR Code" , Toasty.LENGTH_SHORT , true).show();
                                    }
                                    catch (Exception e) {
                                        Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }, throwable -> Log.d("error_request_google" , throwable.getMessage()))
                );

            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("error_here" , e.getMessage());
            }

        });
    }

    private void showDialogAcceptRefuse(int position) {

        DialogAcceptRefuse dialogAcceptRefuse = new DialogAcceptRefuse(context);

        dialogAcceptRefuse.setListener(new AcceptRefuseListener() {
            @Override
            public void clickAccept(boolean isWithPrepare , boolean isWithoutPrepare) {
                acceptApi(position , isWithPrepare , isWithoutPrepare , dialogAcceptRefuse);
            }

            @Override
            public void clickRefuse() {
                refuseApi(position , dialogAcceptRefuse);
            }

        });

    }

    private void refuseApi(int position , DialogAcceptRefuse dialogAcceptRefuse) {

        requestLiveData.observe((LifecycleOwner) context, requestItemModels -> {

            RequestItemModel model = requestItemModels.get(position);

            Log.d("show_user_id" , model.getUserId() + "...");
            Log.d("show_request_id" , model.getRequestId() + "...");


            try {
                String token = Hawk.get("TOKEN");
                String encUserId = AES.encrypt(model.getUserId());
                String encRequestId = AES.encrypt(model.getRequestId());

                new CompositeDisposable().add(
                        api.refuseRequest(
                                "Bearer " + token,
                                Common.craftmanData.getId(),
                                encUserId,
                                encRequestId
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(stringResultModel -> {

                            if (stringResultModel != null){
                                if (!stringResultModel.getErrorMessage().isEmpty()){

                                    try {
                                        String errorMessage = AES.decrypt(stringResultModel.getErrorMessage());
                                        Log.d("error_message_here" , errorMessage);
                                        Toasty.error(context, errorMessage, Toast.LENGTH_SHORT , true).show();

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                    if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                        //Save the token
                                        Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                        //Show message
                                        try {
                                            requestLiveData.observe((LifecycleOwner) context, requestItemModels1 -> {
                                                dialogAcceptRefuse.dialog.dismiss();
                                                requestItemModels.remove(position);
                                                Log.d("size_reduce",requestItemModels.size() + "");
                                                notifyDataSetChanged();
                                                Toasty.success(context , context.getResources().getString(R.string.request_refused) , Toasty.LENGTH_SHORT , true).show();
                                                loadRequests(binding.recyclerRequest);
                                            });
                                        }
                                        catch (Exception e) {
                                            Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                            e.printStackTrace();
                                        }
                                    }
                                    else
                                        Toasty.error(context, context.getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                }
                                else {
                                    //Show message
                                    try {
                                        requestLiveData.observe((LifecycleOwner) context, requestItemModels1 -> {
                                            dialogAcceptRefuse.dialog.dismiss();
                                            requestItemModels.remove(position);
                                            notifyDataSetChanged();
                                            Toasty.success(context , context.getResources().getString(R.string.request_refused) , Toasty.LENGTH_SHORT , true).show();
                                            loadRequests(binding.recyclerRequest);
                                        });
                                    }
                                    catch (Exception e) {
                                        Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }, throwable -> Log.d("error_request_google" , throwable.getMessage()))
                );

            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("error" , e.getMessage());
            }
        });

    }

    private void acceptApi(int position , boolean isWithPrepare , boolean isWithoutPrepare , DialogAcceptRefuse dialogAcceptRefuse) {

        requestLiveData.observe((LifecycleOwner) context, requestItemModels -> {

            RequestItemModel model = requestItemModels.get(position);

            Log.d("show_user_id" , model.getUserId() + "...");
            Log.d("show_request_id" , model.getRequestId() + "...");

            try {
                String token = Hawk.get("TOKEN");
                String encUserId = AES.encrypt(model.getUserId());
                String encRequestId = AES.encrypt(model.getRequestId());

                //Setup process Type
                String withoutCostPrepare = "1";
                if (isWithPrepare)
                    withoutCostPrepare = "2";

                withoutCostPrepare = AES.encrypt(withoutCostPrepare);
                Log.d("encIsPrepare" , withoutCostPrepare);



                //Setup QRCode String
                String qrCode = "12345";
                qrCode = AES.encrypt(qrCode);
                Log.d("qrCode" , qrCode);



                new CompositeDisposable().add(
                        api.acceptRequest(
                                "Bearer " + token,
                                Common.craftmanData.getId(),
                                encUserId,
                                encRequestId,
                                withoutCostPrepare,
                                qrCode
                                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(stringResultModel -> {

                            if (stringResultModel != null){
                                if (!stringResultModel.getErrorMessage().isEmpty()){

                                    try {
                                        String errorMessage = AES.decrypt(stringResultModel.getErrorMessage());
                                        Log.d("error_message_here" , errorMessage);
                                        Toasty.error(context, errorMessage, Toast.LENGTH_SHORT , true).show();

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                else if (!stringResultModel.getResponse().getToken().isEmpty()){
                                    if (Common.checkSecureToken(stringResultModel.getResponse().getToken())){

                                        //Save the token
                                        Hawk.put("TOKEN" , stringResultModel.getResponse().getToken());

                                        //Show message
                                        try {
                                            dialogAcceptRefuse.dialog.dismiss();
                                            notifyDataSetChanged();
                                            Toasty.success(context , context.getResources().getString(R.string.request_converted_workerIncoming) , Toasty.LENGTH_SHORT , true).show();
                                            loadRequests(binding.recyclerRequest);
                                        }
                                        catch (Exception e) {
                                            Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                            e.printStackTrace();
                                        }
                                    }
                                    else
                                        Toasty.error(context, context.getResources().getString(R.string.toast_token_not_secure), Toast.LENGTH_SHORT , true).show();
                                }
                                else {
                                    //Show message
                                    try {
                                        dialogAcceptRefuse.dialog.dismiss();
                                        notifyDataSetChanged();
                                        Toasty.success(context , context.getResources().getString(R.string.request_converted_workerIncoming) , Toasty.LENGTH_SHORT , true).show();
                                        loadRequests(binding.recyclerRequest);
                                    }
                                    catch (Exception e) {
                                        Toasty.error(context , "error_here" + e.getMessage() , Toasty.LENGTH_SHORT , true).show();
                                        e.printStackTrace();
                                    }
                                }
                            }


                        }, throwable -> Log.d("error_request_google" , throwable.getMessage()))
                );

            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("error" , e.getMessage());
            }

        });

    }

    private void startRecyclerViewCategoryAnimation(RecyclerView recyclerView) {

        Context context = recyclerView.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_slide_right);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //Set Animation
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    @Override
    public int getItemCount() {
        return requestLiveData.getValue().size();
    }
}
