package com.cumulocity.alarmapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cumulocity.alarmapp.databinding.ActivityLoginBinding;
import com.cumulocity.alarmapp.util.CumulocityAPI;
import com.cumulocity.alarmapp.util.LoginHolder;
import com.cumulocity.client.model.CurrentUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.connectButton.setOnClickListener(v -> {
            if (validateInputFields()) {
                updateLoginInfo();
                fetchUserInfo();
            }
        });
        binding.tenantField.addTextChangedListener(new CustomTextWatcher(binding.tenantField, binding.errorInputTenantField));
        binding.nameField.addTextChangedListener(new CustomTextWatcher(binding.nameField, binding.errorInputNameField));
        binding.passwordField.addTextChangedListener(new CustomTextWatcher(binding.passwordField, binding.errorInputPasswordField));
        addTemp();
    }

    private void navigateToDashboard() {
        startActivity(new Intent(this, WelcomeActivity.class));
        overridePendingTransition(Intent.FLAG_ACTIVITY_NO_ANIMATION, Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
    }

    private boolean validateInputFields() {
        binding.tenantField.setText(binding.tenantField.getText().toString().trim());
        binding.nameField.setText(binding.nameField.getText().toString().trim());
        binding.passwordField.setText(binding.passwordField.getText().toString().trim());
        return !binding.tenantField.getText().toString().isEmpty()
                && !binding.nameField.getText().toString().isEmpty()
                && !binding.passwordField.getText().toString().isEmpty();
    }

    private class CustomTextWatcher implements TextWatcher {

        private TextInputEditText inputField;
        private TextInputLayout errorTextLayout;

        CustomTextWatcher(TextInputEditText inputField, TextInputLayout errorTextLayout) {
            this.inputField = inputField;
            this.errorTextLayout = errorTextLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            errorTextLayout.setError(editable.toString().isEmpty() ? getString(R.string.input_field_required) : null);
        }
    }

    private void fetchUserInfo() {
        showProgress(true);
        CumulocityAPI.Companion.getInstance().getUserInfo(new Callback<CurrentUser>() {
            @Override
            public void onResponse(Call<CurrentUser> call, Response<CurrentUser> response) {
                if (response.isSuccessful()) {
                    final LoginHolder loginHolder = LoginHolder.getInstance(MyApplication.getAppContext());
                    loginHolder.setCurrentUserName(response.body().getUserName());
                    loginHolder.save(true);
                    CumulocityAPI.Companion.getInstance().initializeAPIs();
                    navigateToDashboard();
                } else {
                    showDialog(response.code() != HttpsURLConnection.HTTP_UNAUTHORIZED ? response.message() : (String) getText(R.string.login_authentication_error_message));
                }
            }

            @Override
            public void onFailure(Call<CurrentUser> call, Throwable t) {
                showDialog(t.getMessage());
            }
        });
    }

    private void updateLoginInfo() {
        LoginHolder loginHolder = LoginHolder.getInstance(MyApplication.getAppContext());
        loginHolder.setTenant(binding.tenantField.getText().toString());
        loginHolder.setUserID(binding.nameField.getText().toString());
        loginHolder.setPassword(binding.passwordField.getText().toString());
        loginHolder.save(false);
    }

    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.login_authentication_error_title));
        builder.setMessage(msg);
        builder.setPositiveButton(getText(R.string.login_error_action), (dialog, which) -> {
            dialog.dismiss();
            showProgress(false);
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void showProgress(boolean value) {
        binding.indicator.setVisibility(value ? View.VISIBLE : View.GONE);
        binding.errorInputTenantField.setEnabled(!value);
        binding.errorInputNameField.setEnabled(!value);
        binding.errorInputPasswordField.setEnabled(!value);
        binding.connectButton.setEnabled(!value);
    }

    //Delete Later
    private void addTemp() {
        binding.tenantField.setText("https://mobile-jhartman.eu-latest.cumulocity.com/");
        binding.nameField.setText("test");
        binding.passwordField.setText(",Manage123");
    }
}