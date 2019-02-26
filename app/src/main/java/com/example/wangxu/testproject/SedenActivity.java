package com.example.wangxu.testproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class SedenActivity extends FragmentActivity {
    private static final String TAG = "TestLifeCircle";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: seden");
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: seden");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: seden");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: seden");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: seden");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: seden");
    }
}
