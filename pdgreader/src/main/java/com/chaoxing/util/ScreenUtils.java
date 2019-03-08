package com.chaoxing.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;

public class ScreenUtils {
    public static final String MI8 = "MI 8";

    public static void setSystemUIVisible(Activity activity, boolean visible, boolean nightMode) {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        if (visible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.TRANSPARENT);

                int newUiOptions = 0;
                if (!nightMode) {
                    newUiOptions = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                newUiOptions |= View.SYSTEM_UI_FLAG_VISIBLE;
                newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;


                decorView.setSystemUiVisibility(newUiOptions);
            } else {
                WindowManager.LayoutParams params = window.getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                window.setAttributes(params);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.TRANSPARENT);

                int newUiOptions = 0;
                if (!nightMode) {
                    newUiOptions = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                newUiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (!SystemUtil.getSystemModel().equalsIgnoreCase(MI8) && !hasNotchInHuawei(activity) && !hasNotchInOppo(activity) && !hasNotchInVivo(activity)) {
                    newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                }
                newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

                decorView.setSystemUiVisibility(newUiOptions);
            } else {
                WindowManager.LayoutParams params = window.getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                window.setAttributes(params);
            }
        }
    }


    public static boolean hasNotchInOppo(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    //Oppo的刘海屏和状态栏高度一致
    public static int getOppoNotchSize(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static boolean hasNotchInHuawei(Context context) {
        boolean hasNotch = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method hasNotchInScreen = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            if (hasNotchInScreen != null) {
                hasNotch = (boolean) hasNotchInScreen.invoke(HwNotchSizeUtil);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNotch;
    }

    public static boolean hasNotchInVivo(Context context) {
        boolean hasNotch = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class ftFeature = cl.loadClass("android.util.FtFeature");
            Method[] methods = ftFeature.getDeclaredMethods();
            if (methods != null) {
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method != null) {
                        if (method.getName().equalsIgnoreCase("isFeatureSupport")) {
                            hasNotch = (boolean) method.invoke(ftFeature, 0x00000020);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasNotch = false;
        }
        return hasNotch;
    }

}
