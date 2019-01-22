package com.speedata.camerapreviewtest;

import android.app.Application;

/**
 * @author xuyan
 */
public class AppCamera extends Application {

    private static AppCamera sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static AppCamera getInstance() {
        return sInstance;
    }
}

