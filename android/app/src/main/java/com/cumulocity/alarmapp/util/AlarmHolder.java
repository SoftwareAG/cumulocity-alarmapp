package com.cumulocity.alarmapp.util;

public class AlarmHolder {

    private static AlarmHolder instance;
    private String alarmId;

    private AlarmHolder() {

    }

    public static AlarmHolder getInstance() {
        if (instance == null) {
            instance = new AlarmHolder();
        }
        return instance;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

}
