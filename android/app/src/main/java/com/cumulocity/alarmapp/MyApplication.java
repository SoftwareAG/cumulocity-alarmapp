package com.cumulocity.alarmapp;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.cumulocity.alarmapp.util.ApplicationLifecycleObserver;

public class MyApplication extends Application {
    private static Context context;
    private static ApplicationLifecycleObserver lifecycleObserver = new ApplicationLifecycleObserver();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);
    }

    public static Context getAppContext() {
        return context;
    }

    public static boolean isAppIsInForeground() {
        return lifecycleObserver.isAppIsInForeground();
    }
}
