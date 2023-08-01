package com.cumulocity.alarmapp.util;

public class AlarmDetailsFilter {
    private static AlarmDetailsFilter instance;
    private boolean tabSelected;

    private AlarmDetailsFilter() {

    }

    public static AlarmDetailsFilter getInstance() {
        if (instance == null) {
            instance = new AlarmDetailsFilter();
        }
        return instance;
    }

    public void selectComments(boolean value) {
        tabSelected = value;
    }

    public boolean isCommentsSelected() {
        return tabSelected;
    }
}
