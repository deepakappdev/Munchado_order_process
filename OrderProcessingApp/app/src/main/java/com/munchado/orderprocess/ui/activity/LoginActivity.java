package com.munchado.orderprocess.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.login.LoginResponse;
import com.munchado.orderprocess.model.token.TokenResponse;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.fragment.CustomErrorDialogFragment;
import com.munchado.orderprocess.utils.DialogUtil;
import com.munchado.orderprocess.utils.MunchadoUtils;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout mEmailLayout, mPasswordLayout;
    private EditText mEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_login);
        initUI();
    }

    private void initUI() {

        mEmailLayout = (TextInputLayout) findViewById(R.id.email_layout);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.pwd_layout);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        findViewById(R.id.submit_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.e("===", "=== onClick");

        if (MunchadoUtils.isNetworkAvailable(this)) {
            if (checkLoginValidation()) {
                getNewToken();
            } /*else {
                CustomErrorDialogFragment errorDialogFragment = CustomErrorDialogFragment.newInstance(getResources().getString(R.string.credentials_error));
                errorDialogFragment.show(getSupportFragmentManager(), "Error");
            }*/
        } else {
            CustomErrorDialogFragment errorDialogFragment = CustomErrorDialogFragment.newInstance(getResources().getString(R.string.network_error));
            errorDialogFragment.show(getSupportFragmentManager(), "Error");
        }
    }

    public boolean checkLoginValidation() {

        boolean isValid = true;

        Log.e("===", "=== checkLoginValidation");
        if (StringUtils.isNullOrEmpty(mEmail.getText().toString())) {
            mEmailLayout.setErrorEnabled(true);
            mEmailLayout.setError(getString(R.string.error_email_register));
            isValid = false;
        } else {
            if (!StringUtils.isValidEmail(mEmail.getText().toString().trim(), false)) {
                mEmailLayout.setErrorEnabled(true);
                mEmailLayout.setError(getString(R.string.email_error));
                isValid = false;
            } else {
                mEmailLayout.setErrorEnabled(false);
            }
        }
        if (StringUtils.isNullOrEmpty(mPassword.getText().toString())) {
            mPasswordLayout.setErrorEnabled(true);
            mPasswordLayout.setError(getString(R.string.error_password_register));
            isValid = false;
        } else {
            if (mPassword.getText().toString().trim().length() < 6) {
                mPasswordLayout.setErrorEnabled(true);
                mPasswordLayout.setError(getString(R.string.error_password_length));
                isValid = false;
            } else {
                mPasswordLayout.setErrorEnabled(false);
            }
        }

        return isValid;
    }

    private void getNewToken() {
        DialogUtil.showProgressDialog(LoginActivity.this);
        RequestController.createNewAccessToken(new RequestCallback() {
            @Override
            public void error(NetworkError networkError) {
                DialogUtil.hideProgressDialog();
                if (networkError != null && !StringUtils.isNullOrEmpty(networkError.getLocalizedMessage())) {
                    CustomErrorDialogFragment errorDialogFragment = CustomErrorDialogFragment.newInstance(networkError.getLocalizedMessage());
                    errorDialogFragment.show(getSupportFragmentManager(), "Error");
                }
            }

            @Override
            public void success(Object obj) {
                TokenResponse mTokenResponse = (TokenResponse) obj;
                PrefUtil.putToken(mTokenResponse.data.token);
                hitLogin();
            }
        });
    }

    private void hitLogin() {

        RequestController.login(mEmail.getText().toString(), mPassword.getText().toString(), new RequestCallback() {
            @Override
            public void error(NetworkError volleyError) {
                DialogUtil.hideProgressDialog();
                if (volleyError != null && !StringUtils.isNullOrEmpty(volleyError.getLocalizedMessage())) {
                    CustomErrorDialogFragment errorDialogFragment = CustomErrorDialogFragment.newInstance(volleyError.getLocalizedMessage());
                    errorDialogFragment.show(getSupportFragmentManager(), "Error");
                }
            }

            @Override
            public void success(Object response) {
                DialogUtil.hideProgressDialog();
                LoginResponse mLoginResponse = (LoginResponse) response;
                if (mLoginResponse.data.message) {
                    PrefUtil.setLogin(true);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    CustomErrorDialogFragment errorDialogFragment = CustomErrorDialogFragment.newInstance("Unable to login.");
                    errorDialogFragment.show(getSupportFragmentManager(), "Error");
                }
            }
        });
    }
}
