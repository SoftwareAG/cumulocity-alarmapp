package com.cumulocity.alarmapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cumulocity.alarmapp.databinding.FragmentAlarmListBinding;
import com.cumulocity.alarmapp.util.AlarmFilter;
import com.cumulocity.alarmapp.util.BottomSheetListener;
import com.cumulocity.alarmapp.util.CumulocityAPI;
import com.cumulocity.client.model.Alarm;
import com.cumulocity.client.model.AlarmCollection;
import com.cumulocity.client.model.ManagedObject;
import com.cumulocity.client.model.ManagedObjectCollection;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AlarmListFragment extends Fragment implements MenuProvider, BottomSheetListener {

    private FragmentAlarmListBinding alarmListBinding;
    private CumulocityAPI cumulocityAPI = CumulocityAPI.Companion.getInstance();
    private static final String TAG = AlarmListFragment.class.getCanonicalName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        alarmListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_list, container, false);
        return alarmListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = alarmListBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        divider.setLastItemDecorated(false);
        recyclerView.addItemDecoration(divider);
        fetchAlarms();
        getActivity().addMenuProvider(this);
    }

    private void fetchAlarms() {
        cumulocityAPI.filterAlarmList(AlarmFilter.getInstance(), new Callback<AlarmCollection>() {
            @Override
            public void onResponse(Call<AlarmCollection> call, Response<AlarmCollection> response) {
                AlarmListHeaderAdapter alarmListAdapter = new AlarmListHeaderAdapter(addHeader(Arrays.asList(response.body().getAlarms())));
                alarmListBinding.recyclerView.setAdapter(alarmListAdapter);
                alarmListAdapter.notifyDataSetChanged();
                validateEmptyList();
            }

            @Override
            public void onFailure(Call<AlarmCollection> call, Throwable t) {
                Log.e(TAG, "Failed while fetching Alarms: " + t.getMessage());
            }
        });
    }

    private void validateEmptyList() {
        boolean enable = alarmListBinding.recyclerView.getAdapter().getItemCount() > 1;
        alarmListBinding.emptyView.setVisibility(enable ? View.GONE : View.VISIBLE);
        alarmListBinding.emptyView.findViewById(R.id.filterButton).setOnClickListener(v -> openBottomSheet());
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_alarmlist, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        openBottomSheet();
        return true;
    }

    private void openBottomSheet() {
        AlarmListFilterFragment fragment = new AlarmListFilterFragment(this, AlarmFilter.getInstance());
        fragment.show(getActivity().getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onDestroyView() {
        getActivity().removeMenuProvider(this);
        super.onDestroyView();
    }

    @Override
    public void onBottomSheetClosed() {
        AlarmFilter alarmFilter = AlarmFilter.getInstance();
        String deviceName = alarmFilter.getDeviceName();
        if (deviceName != null && !deviceName.isEmpty()) {
            filterDevice(cumulocityAPI.appendNameFilter(deviceName));
        } else {
            fetchAlarms();
        }
    }

    private void filterDevice(String name) {
        cumulocityAPI.filterDeviceName(name, new Callback<ManagedObjectCollection>() {
            @Override
            public void onResponse(Call<ManagedObjectCollection> call, Response<ManagedObjectCollection> response) {
                if (response.isSuccessful()) {
                    ManagedObjectCollection collection = response.body();
                    if (collection != null && collection.getManagedObjects().length != 0) {
                        ManagedObject managedObject = collection.getManagedObjects()[0];
                        AlarmFilter alarmFilter = AlarmFilter.getInstance();
                        alarmFilter.setDeviceID(managedObject.getId());
                    }
                }
                fetchAlarms();
            }

            @Override
            public void onFailure(Call<ManagedObjectCollection> call, Throwable t) {
                Log.e(TAG, "Failed while filtering Device Name: " + t.getMessage());
                fetchAlarms();
            }
        });
    }

    private List<Alarm> addHeader(List<Alarm> alarmList) {
        List<Alarm> list = new ArrayList<>();
        //Header element at Position-0
        list.add(0, null);
        list.addAll(alarmList);
        return list;
    }
}