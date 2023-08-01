package com.cumulocity.alarmapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.converter.gson.ExtendedGsonConverterFactory;

public class LoginHolder {
    private String currentUserName;
    private String tenant;
    private String userID;
    private String password;

    private String token;

    private boolean tokenRegistered;

    private static LoginHolder holder;
    private static SharedPreferences sharedPreferences;
    private final static ExtendedGsonConverterFactory extendedGsonConverterFactory = new ExtendedGsonConverterFactory();

    private final static String KEY = "LoginHolder";
    private boolean loggedIN = false;

    private LoginHolder(Context context) {
        sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }

    public static LoginHolder getInstance(Context context) {
        if (holder == null) {
            holder = new LoginHolder(context);
        }
        String temp = sharedPreferences.getString(KEY, null);
        return temp != null ? extendedGsonConverterFactory.getGson().fromJson(temp, LoginHolder.class) : holder;
    }

    public void setCurrentUserName(String name) {
        this.currentUserName = name;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void save(boolean value) {
        loggedIN = value;
        if (!value) {
            tokenRegistered = false;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, extendedGsonConverterFactory.getGson().toJson(this));
        editor.commit();
    }

    public boolean isLoggedIN() {
        return loggedIN;
    }

    public void setTokenRegistered(boolean value) {
        tokenRegistered = value;
        if (value) {
            save(true);
        }
    }

    public boolean isTokenRegistered() {
        return tokenRegistered;
    }

    public void setToken(String value) {
        token = value;
        if (!value.isEmpty()) {
            save(true);
        }
    }

    public String getToken() {
        return token;
    }
}
