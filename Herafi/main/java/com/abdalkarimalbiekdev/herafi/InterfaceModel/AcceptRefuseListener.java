package com.abdalkarimalbiekdev.herafi.InterfaceModel;

import android.view.View;

public interface AcceptRefuseListener {

    void clickAccept(boolean isWithPrepare , boolean isWithoutPrepare);
    void clickRefuse();
}
