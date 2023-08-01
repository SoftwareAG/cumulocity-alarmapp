package com.cumulocity.alarmapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.cumulocity.alarmapp.databinding.FragmentAddCommentBinding;
import com.cumulocity.alarmapp.fragments.C8yComment;
import com.cumulocity.alarmapp.util.AlarmDetailsFilter;
import com.cumulocity.alarmapp.util.CumulocityAPI;
import com.cumulocity.alarmapp.util.LoginHolder;
import com.cumulocity.client.model.Alarm;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.transition.MaterialContainerTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCommentFragment extends Fragment {

    private FragmentAddCommentBinding fragmentAddCommentBinding;
    private Alarm alarm;

    private final String TAG = AddCommentFragment.class.getCanonicalName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAddCommentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_comment, container, false);
        return fragmentAddCommentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Alarm> list = (ArrayList<Alarm>) getArguments().getSerializable("AlarmSelected");
        alarm = list.get(0);
        fragmentAddCommentBinding.submitButton.setEnabled(false);
        fragmentAddCommentBinding.submitButton.setOnClickListener(v -> navigateToAlarmDetails());
        fragmentAddCommentBinding.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fragmentAddCommentBinding.submitButton.setEnabled(s.toString().isEmpty() ? false : true);
            }
        });
        MaterialContainerTransform enterContainerTransform = new MaterialContainerTransform(requireContext(), true);
        int colorSurface = MaterialColors.getColor(requireView(), com.google.android.material.R.attr.colorSurface);
        enterContainerTransform.setAllContainerColors(colorSurface);
        setSharedElementEnterTransition(enterContainerTransform);
        AlarmDetailsFilter.getInstance().selectComments(true);
    }

    private void navigateToAlarmDetails() {
        updateComment();
        Navigation.findNavController(getView()).popBackStack();
    }

    private void updateComment() {
        List<C8yComment> list = new ArrayList<>();
        C8yComment[] comments = (C8yComment[]) alarm.get(C8yComment.IDENTIFIER);
        C8yComment c8yComment = new C8yComment();
        c8yComment.setText(fragmentAddCommentBinding.inputField.getText().toString());
        c8yComment.setUser(LoginHolder.getInstance(MyApplication.getAppContext()).getCurrentUserName());
        list.add(c8yComment);
        if (comments != null) {
            list.addAll(Arrays.asList(comments));
        }
        alarm.set(C8yComment.IDENTIFIER, Arrays.copyOf(list.toArray(), list.size(), C8yComment[].class));
        CumulocityAPI.Companion.getInstance().updateAlarm(alarm, alarm.getId(), new Callback<Alarm>() {
            @Override
            public void onResponse(Call<Alarm> call, Response<Alarm> response) {
                Log.i(TAG, "Alarm updated: " + alarm.getId());
            }

            @Override
            public void onFailure(Call<Alarm> call, Throwable t) {
                Log.e(TAG, "Failed to update Alarm: " + alarm.getId());
            }
        });
    }
}