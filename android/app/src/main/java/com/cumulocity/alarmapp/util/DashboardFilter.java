package com.cumulocity.alarmapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import retrofit2.converter.gson.ExtendedGsonConverterFactory;

public final class DashboardFilter extends AlarmModel {

    private static DashboardFilter instance;
    private static SharedPreferences sharedPreferences;

    private final static String FILTER = "DashboardFilter";
    private final static ExtendedGsonConverterFactory extendedGsonConverterFactory = new ExtendedGsonConverterFactory();

    private DashboardFilter(Context context) {
        sharedPreferences = context.getSharedPreferences(FILTER, Context.MODE_PRIVATE);
    }

    public static DashboardFilter getInstance(Context context) {
        if (instance == null) {
            instance = new DashboardFilter(context);
        }
        String temp = sharedPreferences.getString(FILTER, null);
        return temp != null ? extendedGsonConverterFactory.getGson().fromJson(temp, DashboardFilter.class) : instance;
    }

    @Override
    public synchronized void saveData(ArrayList<String> severity, ArrayList<String> status, String[] type, String deviceName, String deviceID) {
        super.saveData(severity, status, type, deviceName, deviceID);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FILTER, extendedGsonConverterFactory.getGson().toJson(this));
        editor.commit();
    }
}
