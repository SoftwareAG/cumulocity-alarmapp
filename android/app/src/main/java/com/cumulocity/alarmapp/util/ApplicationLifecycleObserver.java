package com.cumulocity.alarmapp.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class ApplicationLifecycleObserver implements LifecycleEventObserver {

    private boolean appIsInForeground;

    public boolean isAppIsInForeground() {
        return appIsInForeground;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME) {
            appIsInForeground = true;
        } else if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
            appIsInForeground = false;
        }
    }
}
