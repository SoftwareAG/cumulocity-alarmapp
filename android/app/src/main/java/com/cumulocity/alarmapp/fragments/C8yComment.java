package com.cumulocity.alarmapp.fragments;

import com.cumulocity.alarmapp.datetime.C8yDateFormatter;

import java.util.Date;

public class C8yComment {

    public static final String IDENTIFIER = "c8y_Comments";

    private String text;
    private String user;
    private String time;

    public C8yComment() {
        this.time = C8yDateFormatter.toReadableDate(new Date());
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public String getTime() {
        return time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
