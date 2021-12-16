package com.abdalkarimalbiekdev.herafi.Dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.BarcodeReaderListener;
import com.abdalkarimalbiekdev.herafi.InterfaceModel.ClickYesListener;
import com.abdalkarimalbiekdev.herafi.R;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class DialogBarcodeReader implements ZXingScannerView.ResultHandler{

    public Dialog dialog;
    private BarcodeReaderListener listener;
    TextView txtCancel;
    ZXingScannerView scannerView;

    public DialogBarcodeReader(Activity activity) {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        View view = LayoutInflater.from(activity).inflate(R.layout.show_dialog_barcode_reader , null , false);
        dialog.setContentView(view);

        txtCancel = (TextView)view.findViewById(R.id.btnCancel);
        scannerView = (ZXingScannerView) view.findViewById(R.id.barcodeReader);


        //Setup Barcode Reader
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){

                            scannerView.setResultHandler(DialogBarcodeReader.this);
                            scannerView.startCamera();
                        }
                        else{
                            Toasty.error(activity , activity.getResources().getString(R.string.barcodeReader_dialog_toast_noPermission) , Toasty.LENGTH_SHORT , true).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();


        txtCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    public void setListener(BarcodeReaderListener listener) {
        this.listener = listener;
    }

    @Override
    public void handleResult(Result rawResult) {

        String result = rawResult.getText();
        listener.verifyQRCode(result);

    }
}
