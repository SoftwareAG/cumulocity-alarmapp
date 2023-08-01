package com.cumulocity.alarmapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cumulocity.alarmapp.databinding.MainLayoutBinding;
import com.cumulocity.alarmapp.fragments.C8yComment;
import com.cumulocity.alarmapp.util.AlarmFilter;
import com.cumulocity.alarmapp.util.CumulocityAPI;
import com.cumulocity.alarmapp.util.DashboardFilter;
import com.cumulocity.alarmapp.util.LoginHolder;
import com.cumulocity.client.model.Alarm;
import com.cumulocity.client.model.Registration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarConfiguration appBarConfiguration;
    private MainLayoutBinding mainLayoutBinding;
    private static final String TAG = WelcomeActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Alarm.Serialization.registerAdditionalProperty(C8yComment.IDENTIFIER, C8yComment[].class);
        mainLayoutBinding = DataBindingUtil.setContentView(this, R.layout.main_layout);

        toolbar = mainLayoutBinding.toolbar;
        setSupportActionBar(toolbar);
        final NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(mainLayoutBinding.collapsingToolbarLayout, toolbar, navController, appBarConfiguration);
        askNotificationPermission();
    }

    @Override
    public boolean onSupportNavigateUp() {
        final NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        AlarmFilter.getInstance().deleteData();
        super.onDestroy();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted && !LoginHolder.getInstance(this).isTokenRegistered()) {
                    requestToken();
                }
            });

    private void askNotificationPermission() {
        final LoginHolder loginHolder = LoginHolder.getInstance(this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            if (loginHolder.getToken() == null || loginHolder.getToken().isEmpty()) {
                requestToken();
            } else {
                postToken(loginHolder.getToken());
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void requestToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.i(TAG, "token: " + token);
                        LoginHolder.getInstance(WelcomeActivity.this).setToken(token);
                        postToken(token);
                    }
                });
    }

    public void postToken(String token) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED || LoginHolder.getInstance(this).getToken().isEmpty()) {
            return;
        }
        Registration.Device device = new Registration.Device(Build.DEVICE, token, Registration.Device.Platform.ANDROID);
        String userID = LoginHolder.getInstance(this).getCurrentUserName();
        Registration registration = new Registration(userID, getPackageName(), device);
        registration.setTags(getTag());
        CumulocityAPI.Companion.getInstance().subscribePushNotification(registration, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    LoginHolder.getInstance(WelcomeActivity.this).setTokenRegistered(true);
                }
                Log.i(TAG, "response: " + response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "response: " + t.getMessage());
            }
        });
    }

    private String[] getTag() {
        DashboardFilter filter = DashboardFilter.getInstance(this);
        ArrayList<String> list = new ArrayList<>();

        ArrayList<String> severityList = filter.getSeverity();
        if (severityList.size() == Alarm.Severity.values().length) {
            list.add("severity:all");
        } else {
            for (String str : severityList) {
                list.add("severity:" + str.toLowerCase());
            }
        }

        ArrayList<String> statusList = filter.getStatus();
        if (statusList.size() == Alarm.Status.values().length) {
            list.add("status:all");
        } else {
            for (String str : statusList) {
                list.add("status:" + str.toLowerCase());
            }
        }

        list.add("deviceId:" + (filter.getDeviceID() != null && !filter.getDeviceID().isEmpty() ? filter.getDeviceID() : "all"));
        if (filter.getType() == null || filter.getType().length == 0) {
            list.add("type:all");
        } else {
            for (String str : filter.getType()) {
                list.add("type:" + str);
            }
        }
        return list.toArray(new String[list.size()]);
    }
}