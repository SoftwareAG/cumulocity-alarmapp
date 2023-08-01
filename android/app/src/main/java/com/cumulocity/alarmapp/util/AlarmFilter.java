package com.cumulocity.alarmapp.util;

public final class AlarmFilter extends AlarmModel {
    private static AlarmFilter instance;

    private AlarmFilter() {

    }

    public static AlarmFilter getInstance() {
        if (instance == null) {
            instance = new AlarmFilter();
        }
        return instance;
    }
}
