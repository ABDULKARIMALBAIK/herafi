package com.abdalkarimalbiekdev.herafi.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.ClickYesListener;
import com.abdalkarimalbiekdev.herafi.R;

public class DialogQRCode {

    public Dialog dialog;
    private ClickYesListener listener;
    public TextView txtCancel;
    public ImageView imgQRCode;

    public DialogQRCode(Context context) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.show_dialog_qrcode , null , false);
        dialog.setContentView(view);

        txtCancel = (TextView)view.findViewById(R.id.btnCancel);
        imgQRCode = (ImageView) view.findViewById(R.id.imgQRCode);

        //txtCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void loadQRCode() {

        listener.onClick();
    }

    public void setListener(ClickYesListener listener) {
        this.listener = listener;
    }
}
