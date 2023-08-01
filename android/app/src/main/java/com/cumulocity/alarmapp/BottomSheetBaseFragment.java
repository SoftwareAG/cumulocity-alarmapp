package com.cumulocity.alarmapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cumulocity.alarmapp.util.AlarmModel;
import com.cumulocity.alarmapp.util.BottomSheetListener;
import com.cumulocity.alarmapp.util.DashboardFilter;
import com.cumulocity.client.model.Alarm;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class BottomSheetBaseFragment extends BottomSheetDialogFragment {
    View bottomSheetView;

    private BottomSheetListener bottomSheetListener;

    private AlarmModel alarmModel;

    public BottomSheetBaseFragment(BottomSheetListener listener, AlarmModel alarmModel) {
        this.bottomSheetListener = listener;
        this.alarmModel = alarmModel;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetView = View.inflate(getContext(), R.layout.bottomsheet_alarmfilter_layout, null);
        bottomSheet.setContentView(bottomSheetView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) (bottomSheetView.getParent()));
        bottomSheetBehavior.setSkipCollapsed(true);
        return bottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (alarmModel instanceof DashboardFilter) {
            bottomSheetView.<TextView>findViewById(R.id.myfilterLabel).setText(getText(R.string.label_watchlist));
            bottomSheetView.<TextView>findViewById(R.id.myfilterText).setText(getText(R.string.text_watchlist));
        }
        updateUI();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDetach() {
        fetchComponents();
        super.onDetach();
    }

    void updateUI() {
        ArrayList<String> severityList = alarmModel.getSeverity();
        ArrayList<String> statusList = alarmModel.getStatus();

        bottomSheetView.<Chip>findViewById(R.id.criticalChip).setChecked(severityList.contains(Alarm.Severity.CRITICAL.name()));
        bottomSheetView.<Chip>findViewById(R.id.majorChip).setChecked(severityList.contains(Alarm.Severity.MAJOR.name()));
        bottomSheetView.<Chip>findViewById(R.id.minorChip).setChecked(severityList.contains(Alarm.Severity.MINOR.name()));
        bottomSheetView.<Chip>findViewById(R.id.warningChip).setChecked(severityList.contains(Alarm.Severity.WARNING.name()));
        bottomSheetView.<Chip>findViewById(R.id.acknowledgedChip).setChecked(statusList.contains(Alarm.Status.ACKNOWLEDGED.name()));
        bottomSheetView.<Chip>findViewById(R.id.clearedChip).setChecked(statusList.contains(Alarm.Status.CLEARED.name()));
        bottomSheetView.<Chip>findViewById(R.id.activeChip).setChecked(statusList.contains(Alarm.Status.ACTIVE.name()));

        final String[] type = alarmModel.getType();
        if (type != null && type.length > 0) {
            bottomSheetView.<TextView>findViewById(R.id.alarmTypeText).setText(String.join(",", type));
        }

        final String deviceName = alarmModel.getDeviceName();
        if (deviceName != null && !deviceName.isEmpty()) {
            bottomSheetView.<TextView>findViewById(R.id.deviceNameText).setText(deviceName);
        }
    }

    void fetchComponents() {
        ArrayList<String> severityList = new ArrayList<String>();
        ArrayList<String> statusList = new ArrayList<String>();
        if (bottomSheetView.<Chip>findViewById(R.id.criticalChip).isChecked()) {
            severityList.add(Alarm.Severity.CRITICAL.name());
        }
        if (bottomSheetView.<Chip>findViewById(R.id.majorChip).isChecked()) {
            severityList.add(Alarm.Severity.MAJOR.name());
        }
        if (bottomSheetView.<Chip>findViewById(R.id.minorChip).isChecked()) {
            severityList.add(Alarm.Severity.MINOR.name());
        }
        if (bottomSheetView.<Chip>findViewById(R.id.warningChip).isChecked()) {
            severityList.add(Alarm.Severity.WARNING.name());
        }
        if (bottomSheetView.<Chip>findViewById(R.id.activeChip).isChecked()) {
            statusList.add(Alarm.Status.ACTIVE.name());
        }
        if (bottomSheetView.<Chip>findViewById(R.id.acknowledgedChip).isChecked()) {
            statusList.add(Alarm.Status.ACKNOWLEDGED.name());
        }
        if (bottomSheetView.<Chip>findViewById(R.id.clearedChip).isChecked()) {
            statusList.add(Alarm.Status.CLEARED.name());
        }
        String type = bottomSheetView.<TextView>findViewById(R.id.alarmTypeText).getText().toString();
        String[] typeArray = null;
        if (type != null && !type.isEmpty()) {
            typeArray = type.split(",");
        }

        alarmModel.saveData(severityList, statusList, typeArray, bottomSheetView.<TextView>findViewById(R.id.deviceNameText).getText().toString(), null);
        bottomSheetListener.onBottomSheetClosed();
    }
}
