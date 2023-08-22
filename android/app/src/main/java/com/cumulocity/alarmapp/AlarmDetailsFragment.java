package com.cumulocity.alarmapp;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.TransitionManager;

import com.cumulocity.alarmapp.databinding.FragmentAlarmDetailsBinding;
import com.cumulocity.alarmapp.fragments.C8yComment;
import com.cumulocity.alarmapp.util.AlarmDetailsFilter;
import com.cumulocity.alarmapp.util.AlarmModel;
import com.cumulocity.alarmapp.util.CumulocityAPI;
import com.cumulocity.client.model.Alarm;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.transition.MaterialFade;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AlarmDetailsFragment extends Fragment {

    private FragmentAlarmDetailsBinding fragmentAlarmDetailsBinding;
    private final String TAG = AlarmDetailsFragment.class.getCanonicalName();
    private Alarm alarm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentAlarmDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_details, container, false);
        return fragmentAlarmDetailsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Alarm> list = (ArrayList<Alarm>) getArguments().getSerializable("AlarmSelected");
        alarm = list.get(0);
        bind(alarm);
        final boolean selected = AlarmDetailsFilter.getInstance().isCommentsSelected();
        fragmentAlarmDetailsBinding.tabLayout.getTabAt(!selected ? 0 : 1).select();
        updateUI(selected);
        fragmentAlarmDetailsBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateUI(tab.getPosition() != 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void bind(Alarm alarm) {
        configureDetailsCard(alarm);
        configureCommentsCard(alarm);
        configureChip(fragmentAlarmDetailsBinding.statusChip, R.menu.status);
        configureChip(fragmentAlarmDetailsBinding.severityChip, R.menu.severity);
    }

    private void configureDetailsCard(Alarm alarm) {
        fragmentAlarmDetailsBinding.setVariable(BR.alarm, alarm);
        fragmentAlarmDetailsBinding.setVariable(BR.severityDrawable, AlarmModel.getSeverityIcon().apply(alarm, getContext()));
        fragmentAlarmDetailsBinding.setVariable(BR.statusDrawable, AlarmModel.getStatusIcon().apply(alarm, getContext()));
        fragmentAlarmDetailsBinding.openDeviceButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.actionToDeviceDetailsFragment, toBundle(AlarmDetailsFragment.this.alarm), null, null);
        });
    }

    private void configureCommentsCard(Alarm alarm) {
        final C8yComment[] comments = (C8yComment[]) alarm.get("c8y_Comments");
        CommentAdapter commentAdapter = new CommentAdapter(comments);
        fragmentAlarmDetailsBinding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentAlarmDetailsBinding.commentRecyclerView.setAdapter(commentAdapter);
        fragmentAlarmDetailsBinding.commentsButton.setOnClickListener(v -> {
            ViewCompat.setTransitionName(fragmentAlarmDetailsBinding.commentsButton, String.valueOf(fragmentAlarmDetailsBinding.commentsButton.getId()));
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(fragmentAlarmDetailsBinding.commentsButton, "container_transformation")
                    .build();
            Navigation.findNavController(getView()).navigate(R.id.actionToAddCommentFragment, toBundle(AlarmDetailsFragment.this.alarm), null, extras);
        });
        fragmentAlarmDetailsBinding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    fragmentAlarmDetailsBinding.commentsButton.extend();
                } else {
                    fragmentAlarmDetailsBinding.commentsButton.shrink();
                }

            }
        });
    }

    private void validateEmptyList() {
        boolean enable = fragmentAlarmDetailsBinding.commentRecyclerView.getAdapter().getItemCount() > 0;
        fragmentAlarmDetailsBinding.commentRecyclerView.setVisibility(enable ? View.VISIBLE : View.GONE);
        fragmentAlarmDetailsBinding.emptyView.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    private void configureChip(Chip chip, int menu) {
        chip.setOnClickListener(v -> showsPopUpMenu(chip, menu));
    }

    private void showsPopUpMenu(Chip chip, int menus) {
        final PopupMenu popupMenu = new PopupMenu(getContext(), chip, Gravity.NO_GRAVITY, androidx.appcompat.R.attr.listPopupWindowStyle, 0);
        popupMenu.getMenuInflater().inflate(menus, popupMenu.getMenu());
        int itemID = -1;
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            String title = popupMenu.getMenu().getItem(i).getTitle().toString();
            if (title.equalsIgnoreCase(fragmentAlarmDetailsBinding.statusRow.getPassedText()) || title.equalsIgnoreCase(fragmentAlarmDetailsBinding.severityRow.getPassedText())) {
                itemID = popupMenu.getMenu().getItem(i).getItemId();
            }
            final Drawable drawable = popupMenu.getMenu().getItem(i).getIcon();
            drawable.setColorFilter(getResources().getColor(R.color.md_theme_onSurfaceVariant, null), PorterDuff.Mode.SRC_ATOP);
        }
        popupMenu.getMenu().removeItem(itemID);
        popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            updateAlarm(chip, menuItem.getTitle().toString());
            return true;
        });
        popupMenu.show();
    }

    private void updateAlarm(Chip chip, String title) {
        if (chip.equals(fragmentAlarmDetailsBinding.statusChip)) {
            Alarm.Status status = Alarm.Status.valueOf(title.toUpperCase());
            alarm.setStatus(status);
            fragmentAlarmDetailsBinding.statusRow.textID.setText(title);
        } else {
            Alarm.Severity severity = Alarm.Severity.valueOf(title.toUpperCase());
            alarm.setSeverity(severity);
            fragmentAlarmDetailsBinding.severityRow.textID.setText(title);
        }
        CumulocityAPI.Companion.getInstance().updateAlarm(alarm, alarm.getId(), new Callback<Alarm>() {
            @Override
            public void onResponse(retrofit2.Call<Alarm> call, Response<Alarm> response) {
                AlarmDetailsFragment.this.alarm = response.body();
                bind(AlarmDetailsFragment.this.alarm);
            }

            @Override
            public void onFailure(retrofit2.Call<Alarm> call, Throwable t) {

            }
        });
    }

    private Bundle toBundle(Alarm alarm) {
        final ArrayList<Alarm> localList = new ArrayList<>();
        localList.add(alarm);
        final Bundle bundle = getArguments();
        bundle.putSerializable("AlarmSelected", localList);
        return bundle;
    }

    private void updateUI(boolean commentSelected) {
        fragmentAlarmDetailsBinding.commentsView.setVisibility(commentSelected ? View.VISIBLE : View.GONE);
        fragmentAlarmDetailsBinding.detailsCardView.setVisibility(!commentSelected ? View.VISIBLE : View.GONE);
        final MaterialFade animation = new MaterialFade();
        animation.addTarget(fragmentAlarmDetailsBinding.commentsButton);
        TransitionManager.beginDelayedTransition((ViewGroup) fragmentAlarmDetailsBinding.getRoot(), animation);
        fragmentAlarmDetailsBinding.commentsButton.setVisibility(commentSelected ? View.VISIBLE : View.GONE);
        if (commentSelected) {
            validateEmptyList();
        }
    }
}