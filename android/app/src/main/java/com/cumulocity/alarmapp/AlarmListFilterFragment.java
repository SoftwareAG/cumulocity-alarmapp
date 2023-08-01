package com.cumulocity.alarmapp;

import com.cumulocity.alarmapp.util.AlarmModel;
import com.cumulocity.alarmapp.util.BottomSheetListener;

public class AlarmListFilterFragment extends BottomSheetBaseFragment {
    public AlarmListFilterFragment(BottomSheetListener listener, AlarmModel alarmModel) {
        super(listener, alarmModel);
    }
}
