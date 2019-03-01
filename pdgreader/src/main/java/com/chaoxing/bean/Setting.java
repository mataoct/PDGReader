package com.chaoxing.bean;

import android.content.Context;

public class Setting {
    private int width;
    private int height;
    private static Setting mSetting = new Setting();

    private Setting() {
    }

    public static Setting get() {
        return mSetting;
    }

    public void initSetting(Context application) {
        int heightPixels = application.getResources().getDisplayMetrics().heightPixels;
        int widthPixels = application.getResources().getDisplayMetrics().widthPixels;
        width = widthPixels;
        height = heightPixels;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
