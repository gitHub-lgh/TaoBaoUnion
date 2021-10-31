package com.utils;

import android.widget.Toast;

import com.base.BaseApplication;

public class ToastUtil {

    private static Toast toast;

    public static void showToast(String tips){
        if(toast == null) {
            toast = Toast.makeText(BaseApplication.getAppContext(), tips, Toast.LENGTH_SHORT);
        }else {
            toast.setText(tips);
        }
        toast.show();
    }
}
