package com.cumulocity.alarmapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.cumulocity.alarmapp.util.CumulocityAPI;
import com.cumulocity.alarmapp.util.LoginHolder;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (LoginHolder.getInstance(MyApplication.getAppContext()).isLoggedIN()) {
            CumulocityAPI.Companion.getInstance().initializeAPIs();
            navigateToScreen(WelcomeActivity.class);
        } else {
            navigateToScreen(LoginActivity.class);
        }
    }

    private void navigateToScreen(Class<?> cls) {
        handler.postDelayed(() -> {
            startActivity(new Intent(this, cls));
            overridePendingTransition(Intent.FLAG_ACTIVITY_NO_ANIMATION, Intent.FLAG_ACTIVITY_NO_ANIMATION);
            SplashScreenActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}