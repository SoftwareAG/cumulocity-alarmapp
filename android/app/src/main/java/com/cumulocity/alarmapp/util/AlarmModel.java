package com.cumulocity.alarmapp.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.cumulocity.alarmapp.R;
import com.cumulocity.client.model.Alarm;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;

public class AlarmModel {
    private ArrayList<String> severity;
    private ArrayList<String> status;
    private String[] type;
    private String deviceID, deviceName;

    public ArrayList<String> getSeverity() {
        return severity;
    }

    public ArrayList<String> getStatus() {
        return status;
    }

    public String[] getType() {
        return type;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public synchronized void saveData(ArrayList<String> severity, ArrayList<String> status, String[] type,
                                      String deviceName, String deviceID) {
        this.severity = severity;
        this.status = status;
        this.type = type;
        this.deviceName = deviceName;
        this.deviceID = deviceID;
    }

    public synchronized void deleteData() {
        this.severity = null;
        this.status = null;
        this.type = null;
        this.deviceID = null;
        this.deviceName = null;
    }

    public static BiFunction<Alarm, Context, Drawable> getStatusIcon() {
        return (alarm, context) -> {
            switch (Objects.requireNonNull(alarm.getStatus())) {
                case ACTIVE:
                    return AppCompatResources.getDrawable(context, R.drawable.ic_status_active);
                case ACKNOWLEDGED:
                    return AppCompatResources.getDrawable(context, R.drawable.ic_status_acknowledged);
                default:
                    return AppCompatResources.getDrawable(context, R.drawable.ic_status_cleared);
            }
        };
    }

    public static BiFunction<Alarm, Context, Drawable> getSeverityIcon() {
        return (alarm, context) -> {
            switch (Objects.requireNonNull(alarm.getSeverity())) {
                case CRITICAL:
                    return AppCompatResources.getDrawable(context, R.drawable.ic_severity_critical);
                case MAJOR:
                    return AppCompatResources.getDrawable(context, R.drawable.ic_severity_major);
                case MINOR:
                    return AppCompatResources.getDrawable(context, R.drawable.ic_severity_minor);
                default:
                    return AppCompatResources.getDrawable(context, R.drawable.ic_severity_warning);
            }
        };
    }
}
