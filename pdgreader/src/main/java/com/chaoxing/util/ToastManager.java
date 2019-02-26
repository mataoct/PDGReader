package com.chaoxing.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastManager {
    private static Toast mToast;

    public static void showBottomText(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return;
        }
        if (mToast == null) {
            synchronized (ToastManager.class) {
                if (mToast == null) {
                    mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
                }
            }
        }
        mToast.setText(str);
        mToast.show();
    }
}
