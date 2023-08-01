package com.cumulocity.alarmapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cumulocity.alarmapp.databinding.AlarmListItemBinding;
import com.cumulocity.alarmapp.util.AlarmDetailsFilter;
import com.cumulocity.alarmapp.util.AlarmModel;
import com.cumulocity.client.model.Alarm;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class AlarmListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Alarm> alarmList;
    private final boolean displayDevices;
    private AlarmListItemBinding alarmListItemBinding;

    public AlarmListAdapter(final List<Alarm> alarmList) {
        this(alarmList, true);
    }

    public AlarmListAdapter(final List<Alarm> alarmList, final boolean displayDevices) {
        this.alarmList = alarmList;
        this.displayDevices = displayDevices;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        alarmListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.alarm_list_item, parent, false);
        return new ViewHolder(alarmListItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            final Alarm alarm = alarmList.get(position);
            alarmListItemBinding.setVariable(BR.alarm, alarm);
            holder.deviceText.setVisibility(displayDevices ? View.VISIBLE : View.GONE);

            final Object[] comments = (Object[]) alarm.get("c8y_Comments");
            holder.commentImage.setVisibility(comments != null && comments.length > 0 ? View.VISIBLE : View.GONE);

            shareSeverityDrawable(alarm);
            shareStatusDrawable(alarm);
            holder.itemView.setOnClickListener(view -> {
                ArrayList<Alarm> localList = new ArrayList<>();
                localList.add(alarm);
                Bundle bundle = new Bundle();
                bundle.putSerializable("AlarmSelected", localList);
                AlarmDetailsFilter.getInstance().selectComments(false);
                Navigation.findNavController(view).navigate(R.id.actionToAlarmDetailsFragment, bundle);
            });
        }
    }

    private void shareStatusDrawable(Alarm alarm) {
        final Context context = alarmListItemBinding.getRoot().getContext();
        alarmListItemBinding.setVariable(BR.statusDrawable, AlarmModel.getStatusIcon().apply(alarm, context));
    }

    private void shareSeverityDrawable(Alarm alarm) {
        final Context context = alarmListItemBinding.getRoot().getContext();
        alarmListItemBinding.setVariable(BR.severityDrawable, AlarmModel.getSeverityIcon().apply(alarm, context));
    }

    @Override
    public int getItemCount() {
        return alarmList == null ? 0 : alarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public MaterialTextView deviceText = alarmListItemBinding.deviceName;
        public ImageView commentImage = alarmListItemBinding.commentImage;

        ViewHolder(View v) {
            super(v);
        }
    }
}
