package com.abdalkarimalbiekdev.herafi.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import com.abdalkarimalbiekdev.herafi.InterfaceModel.AcceptRefuseListener;
import com.abdalkarimalbiekdev.herafi.R;

import org.w3c.dom.Text;

public class DialogAcceptRefuse {

    public Dialog dialog;
    private AcceptRefuseListener listener;

    TextView txtAccept;
    TextView txtRefuse;
    TextView txtCancel;
    RadioButton rdbWithCostRepair;
    RadioButton rdbWithoutCostRepair;


    public DialogAcceptRefuse(Context context){

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.show_dialog_accept_refuse , null , false);
        dialog.setContentView(view);

        txtAccept = (TextView)view.findViewById(R.id.btnAccept);
        txtRefuse = (TextView)view.findViewById(R.id.btnRefuse);
        txtCancel = (TextView)view.findViewById(R.id.btnCancel);
        rdbWithoutCostRepair = (RadioButton)view.findViewById(R.id.radioWithoutCostPrepare);
        rdbWithCostRepair = (RadioButton)view.findViewById(R.id.radioWithCostPrepare);

        txtAccept.setOnClickListener(v -> listener.clickAccept(rdbWithCostRepair.isChecked() , rdbWithoutCostRepair.isChecked()));
        txtRefuse.setOnClickListener(v -> listener.clickRefuse());
        txtCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void setListener(AcceptRefuseListener listener) {
        this.listener = listener;
    }
}
