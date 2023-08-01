package com.cumulocity.alarmapp.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cumulocity.alarmapp.MyApplication;
import com.cumulocity.alarmapp.util.AlarmHolder;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class AppPushNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Looper.prepare();
        Toast.makeText(MyApplication.getAppContext(), remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        final Bundle data = intent.getExtras();
        final RemoteMessage remoteMessage = new RemoteMessage(data);
        if (!MyApplication.isAppIsInForeground()) {
            retrieveAlarmId(remoteMessage);
        }
    }

    private void retrieveAlarmId(RemoteMessage remoteMessage) {
        String alarmId = remoteMessage.getData().get("alarmId");
        if (alarmId != null && !alarmId.isEmpty()) {
            AlarmHolder.getInstance().setAlarmId(alarmId);
        }
    }
}
