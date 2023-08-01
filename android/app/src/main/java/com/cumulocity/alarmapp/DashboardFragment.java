package com.cumulocity.alarmapp;

import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cumulocity.alarmapp.databinding.AlarmTextTemplateBinding;
import com.cumulocity.alarmapp.databinding.FragmentWelcomeBinding;
import com.cumulocity.alarmapp.util.AlarmDetailsFilter;
import com.cumulocity.alarmapp.util.AlarmFilter;
import com.cumulocity.alarmapp.util.AlarmHolder;
import com.cumulocity.alarmapp.util.BottomSheetListener;
import com.cumulocity.alarmapp.util.CumulocityAPI;
import com.cumulocity.alarmapp.util.DashboardFilter;
import com.cumulocity.alarmapp.util.LoginHolder;
import com.cumulocity.client.model.Alarm;
import com.cumulocity.client.model.AlarmCollection;
import com.cumulocity.client.model.ManagedObject;
import com.cumulocity.client.model.ManagedObjectCollection;
import com.cumulocity.client.supplementary.SeparatedQueryParameter;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements MenuProvider, BottomSheetListener {

    private RecyclerView recyclerView;
    private FragmentWelcomeBinding binding;
    private CumulocityAPI cumulocityAPI = CumulocityAPI.Companion.getInstance();
    private static final String TAG = DashboardFragment.class.getCanonicalName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String alarmId = AlarmHolder.getInstance().getAlarmId();
        if (alarmId != null && !alarmId.isEmpty()) {
            fetchAlarm(alarmId);
        } else {
            configureAlarmsBadge();
            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            final MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
            divider.setLastItemDecorated(false);
            recyclerView.addItemDecoration(divider);

            updateDataFilter();
            fetchAlarms();
            binding.showButton.setOnClickListener(view1 -> {
                navigateToAlarmList(getSeverityList());
            });
            getActivity().addMenuProvider(this);
        }
    }

    private void fetchAlarm(String alarmId) {
        cumulocityAPI.getAlarm(alarmId, new Callback<Alarm>() {
            @Override
            public void onResponse(Call<Alarm> call, Response<Alarm> response) {
                if (response.isSuccessful()) {
                    AlarmHolder.getInstance().setAlarmId(null);
                    navigateToAlarmDetails(response.body());
                }
            }

            @Override
            public void onFailure(Call<Alarm> call, Throwable t) {
                Log.e(TAG, "Failed while fetching Alarm: " + t.getMessage());
            }
        });
    }

    private void navigateToAlarmDetails(Alarm alarm) {
        ArrayList<Alarm> localList = new ArrayList<>();
        localList.add(alarm);
        Bundle bundle = new Bundle();
        bundle.putSerializable("AlarmSelected", localList);
        AlarmDetailsFilter.getInstance().selectComments(false);
        Navigation.findNavController(getView()).navigate(R.id.actionToAlarmDetailsFragment, bundle);
    }

    private void fetchAlarms() {
        cumulocityAPI.filterAlarmList(DashboardFilter.getInstance(getActivity()), new Callback<AlarmCollection>() {
            @Override
            public void onResponse(Call<AlarmCollection> call, Response<AlarmCollection> response) {
                AlarmCollection alarmCollection = response.body();
                AlarmListAdapter alarmListAdapter = new AlarmListAdapter(Arrays.asList(alarmCollection.getAlarms()));
                recyclerView.setAdapter(alarmListAdapter);
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
        boolean enable = recyclerView.getAdapter().getItemCount() > 0;
        recyclerView.setVisibility(enable ? View.VISIBLE : View.GONE);
        binding.filterLabel.setVisibility(enable ? View.VISIBLE : View.GONE);
        binding.emptyView.setVisibility(enable ? View.GONE : View.VISIBLE);
        binding.emptyView.findViewById(R.id.filterButton).setOnClickListener(v -> openBottomSheet());
    }

    private void configureAlarmsBadge() {
        initializeSelection(binding.criticalLayout, Alarm.Severity.CRITICAL);
        initializeSelection(binding.majorLayout, Alarm.Severity.MAJOR);
        initializeSelection(binding.minorLayout, Alarm.Severity.MINOR);
        initializeSelection(binding.warningLayout, Alarm.Severity.WARNING);
    }

    private void initializeSelection(AlarmTextTemplateBinding layoutBinding, Alarm.Severity severity) {
        layoutBinding.getRoot().setOnClickListener(view -> {
            ArrayList<String> list = new ArrayList<String>();
            list.add(severity.name());
            navigateToAlarmList(list);
        });
        cumulocityAPI.getActiveAlarmCount(new SeparatedQueryParameter<String>(new String[]{severity.name()}), new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer value = response.body();
                layoutBinding.countText.setText(String.valueOf(value));
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e(TAG, "Failed while fetching Alarm count: " + t.getMessage());
            }
        });
    }

    private void navigateToAlarmList(ArrayList<String> severityList) {
        AlarmFilter alarmFilter = AlarmFilter.getInstance();
        alarmFilter.saveData(severityList, getStatusList(), null, null, null);
        Navigation.findNavController(getView()).navigate(R.id.actionToAlarmListFragment);
    }

    private ArrayList getStatusList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Alarm.Status.ACTIVE.name());
        return list;
    }

    private ArrayList getSeverityList() {
        ArrayList<String> list = new ArrayList<String>();
        for (Alarm.Severity severity : Alarm.Severity.values()) {
            list.add(severity.name());
        }
        return list;
    }

    private ArrayList getCriticalSeverity() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Alarm.Severity.CRITICAL.name());
        return list;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.logoutButton:
                showLogoutDialog();
                break;
            default:
                openBottomSheet();
                break;
        }
        return true;
    }

    private void openBottomSheet() {
        updateDataFilter();
        DashboardFilterFragment fragment = new DashboardFilterFragment(this, DashboardFilter.getInstance(getActivity()));
        fragment.show(getActivity().getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onBottomSheetClosed() {
        DashboardFilter dashboardFilter = DashboardFilter.getInstance(getActivity());
        String deviceName = dashboardFilter.getDeviceName();
        if (deviceName != null && !deviceName.isEmpty()) {
            filterDevice(cumulocityAPI.appendNameFilter(deviceName));
        } else {
            fetchAlarms();
        }
        ((WelcomeActivity) getActivity()).postToken(LoginHolder.getInstance(getActivity()).getToken());
    }

    @Override
    public void onDestroyView() {
        getActivity().removeMenuProvider(this);
        super.onDestroyView();
    }

    private void updateDataFilter() {
        DashboardFilter dashboardFilter = DashboardFilter.getInstance(getActivity());
        if (dashboardFilter.getSeverity() == null || dashboardFilter.getStatus() == null) {
            dashboardFilter.saveData(getCriticalSeverity(), getStatusList(), null, null, null);
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
                        DashboardFilter dashboardFilter = DashboardFilter.getInstance(getActivity());
                        dashboardFilter.setDeviceID(managedObject.getId());
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

    private void proceedLogout() {
        cumulocityAPI.unSubscribePushNotification(LoginHolder.getInstance(getActivity()).getToken(), new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "Successfully unsubscribed PushNotification: " + response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed while unsubscribing PushNotification: " + t.getMessage());
            }
        });
        LoginHolder.getInstance(MyApplication.getAppContext()).save(false);
        navigateToLogin();
    }

    private void navigateToLogin() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().overridePendingTransition(Intent.FLAG_ACTIVITY_NO_ANIMATION, Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getText(R.string.logout_confirmation_dialog));
        builder.setPositiveButton(getText(R.string.button_confirm), (dialog, which) -> proceedLogout());
        builder.setNegativeButton(getText(R.string.button_cancel), (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.show();
    }
}