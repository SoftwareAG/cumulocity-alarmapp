package com.cumulocity.alarmapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cumulocity.alarmapp.databinding.FragmentDeviceDetailsBinding;
import com.cumulocity.alarmapp.util.CumulocityAPI;
import com.cumulocity.client.model.Alarm;
import com.cumulocity.client.model.AlarmCollection;
import com.cumulocity.client.model.C8yHardware;
import com.cumulocity.client.model.ExternalIds;
import com.cumulocity.client.model.ManagedObject;
import com.cumulocity.client.supplementary.SeparatedQueryParameter;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DeviceDetailsFragment extends Fragment {

    private static final String C8Y_HARDWARE = "c8yHardware";
    private static final String DEVICE_TYPE = "type";
    private static final String TAG = DeviceDetailsFragment.class.getCanonicalName();

    private final CumulocityAPI cumulocityAPI = CumulocityAPI.Companion.getInstance();
    private FragmentDeviceDetailsBinding deviceDetailsBinding;

    static {
        ManagedObject.Serialization.registerAdditionalProperty(DEVICE_TYPE, String.class);
        ManagedObject.Serialization.registerAdditionalProperty(C8Y_HARDWARE, C8yHardware.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        deviceDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_details, container, false);
        return deviceDetailsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Alarm> list = (ArrayList<Alarm>) getArguments().getSerializable("AlarmSelected");
        Alarm alarm = list.get(0);
        fetchDeviceDetails(alarm.getSource().getId());
        fetchAlarms(alarm.getSource().getId());
    }

    private void fetchDeviceDetails(String id) {
        cumulocityAPI.getDevice(id, new Callback<ManagedObject>() {

            @Override
            public void onResponse(Call<ManagedObject> call, Response<ManagedObject> response) {
                ManagedObject managedObject = response.body();
                if (managedObject != null) {
                    configureDetailsCard(managedObject);
                }
            }

            @Override
            public void onFailure(Call<ManagedObject> call, Throwable t) {
                Log.e(TAG, "Failed while fetching Device details: " + t.getMessage());
            }
        });
    }

    private void configureDetailsCard(ManagedObject device) {
        deviceDetailsBinding.setVariable(BR.device, device);
        if (device.getCustomFragments() != null) {
            Map map = device.getCustomFragments();
            if (map.containsKey(C8Y_HARDWARE)) {
                C8yHardware hardware = (C8yHardware) map.get(C8Y_HARDWARE);
                deviceDetailsBinding.hardwareRow.getRoot().setVisibility(View.VISIBLE);
                deviceDetailsBinding.setVariable(BR.C8yHardware, hardware);
            }
            if (map.containsKey(DEVICE_TYPE)) {
                deviceDetailsBinding.typeRow.getRoot().setVisibility(View.VISIBLE);
                deviceDetailsBinding.setVariable(BR.deviceType, map.get(DEVICE_TYPE).toString());
            }
        }
        loadExternalID(device.getId());
    }

    private void fetchAlarms(String id) {
        cumulocityAPI.filterAlarms(id, new SeparatedQueryParameter<String>(new String[]{Alarm.Status.ACTIVE.name()}), new Callback<AlarmCollection>() {
            @Override
            public void onResponse(Call<AlarmCollection> call, Response<AlarmCollection> response) {
                AlarmCollection alarmCollection = response.body();
                configureActiveAlarms(Arrays.asList(alarmCollection.getAlarms()));
            }

            @Override
            public void onFailure(Call<AlarmCollection> call, Throwable t) {
                Log.e(TAG, "Failed while fetching Alarms: " + t.getMessage());
            }
        });
    }

    private void configureActiveAlarms(List<Alarm> list) {
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        AlarmListAdapter alarmListAdapter = new AlarmListAdapter(list, false);
        final MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        divider.setLastItemDecorated(false);
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(alarmListAdapter);
        alarmListAdapter.notifyDataSetChanged();
    }

    private void loadExternalID(String id) {
        cumulocityAPI.getExternalID(id, new Callback<ExternalIds>() {
            @Override
            public void onResponse(Call<ExternalIds> call, Response<ExternalIds> response) {
                ExternalIds externalID = response.body();
                if (externalID != null && externalID.getExternalIds().length > 0) {
                    deviceDetailsBinding.externalRow.getRoot().setVisibility(View.VISIBLE);
                    deviceDetailsBinding.setVariable(BR.externalID, externalID.getExternalIds()[0].getExternalId());
                }
            }

            @Override
            public void onFailure(Call<ExternalIds> call, Throwable t) {
                Log.e(TAG, "Failed while fetching External ID: " + t.getMessage());
            }
        });
    }
}