package com.cumulocity.alarmapp;

import com.cumulocity.alarmapp.util.AlarmModel;
import com.cumulocity.alarmapp.util.BottomSheetListener;

public class DashboardFilterFragment extends BottomSheetBaseFragment {
    public DashboardFilterFragment(BottomSheetListener listener, AlarmModel alarmModel) {
        super(listener, alarmModel);
    }
}
