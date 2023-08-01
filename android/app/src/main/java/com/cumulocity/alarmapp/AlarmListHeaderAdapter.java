package com.cumulocity.alarmapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cumulocity.alarmapp.databinding.AlarmListHeaderBinding;
import com.cumulocity.alarmapp.util.AlarmFilter;
import com.cumulocity.alarmapp.util.StringUtil;
import com.cumulocity.client.model.Alarm;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class AlarmListHeaderAdapter extends AlarmListAdapter {

    private Bundle bundle;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private AlarmListHeaderBinding alarmListHeaderBinding;

    public AlarmListHeaderAdapter(List<Alarm> alarmList) {
        super(alarmList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            alarmListHeaderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.alarm_list_header, parent, false);
            return new HeaderHolder(alarmListHeaderBinding.getRoot());
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
            AlarmFilter alarmFilter = AlarmFilter.getInstance();
            configureChips(alarmFilter.getSeverity(), Alarm.Severity.values().length, R.string.label_severity_all);
            configureChips(alarmFilter.getStatus(), Alarm.Status.values().length, R.string.label_status_all);

            String deviceName = alarmFilter.getDeviceName();
            if (deviceName != null && !deviceName.isEmpty()) {
                addChip(deviceName);
            }
            String[] type = alarmFilter.getType();
            if (type != null && type.length != 0) {
                for (String temp : type) {
                    addChip(temp);
                }
            }
        }
    }

    private void addChip(String text) {
        Chip chip = new Chip(alarmListHeaderBinding.getRoot().getContext());
        chip.setText(text);
        chip.setClickable(false);
        alarmListHeaderBinding.chipGroup.addView(chip);
    }

    private void configureChips(ArrayList<String> list, int size, int id) {
        if (list != null && list.size() != 0) {
            if (list.size() == size) {
                final Context context = alarmListHeaderBinding.getRoot().getContext();
                addChip(String.valueOf(context.getText(id)));
            } else {
                for (String temp : list) {
                    addChip(StringUtil.toCamelCase(temp));
                }
            }
        }
    }
}
