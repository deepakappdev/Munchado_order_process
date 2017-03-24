package com.munchado.orderprocess.ui.activity.settings;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.profile.RestaurantProfileResponse;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.fragment.CustomErrorDialogFragment;
import com.munchado.orderprocess.utils.DialogUtil;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

public class ProfileSettingActivity extends AppCompatActivity implements RequestCallback {

    TextInputLayout txt_resname_layout,  txt_phone_layout, txt_email_layout,txt_city_layout,  txt_state_layout, txt_zip_layout;
    com.munchado.orderprocess.ui.widgets.CustomTextInputLayout txt_address_layout;
    TextView txt_resname, txt_address, txt_phone, txt_email, txt_zip, txt_state, txt_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        getSupportActionBar().setTitle("Profile Setting");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_resname_layout = (TextInputLayout) findViewById(R.id.txt_resname_layout);
        txt_address_layout = (com.munchado.orderprocess.ui.widgets.CustomTextInputLayout) findViewById(R.id.txt_address_layout);
        txt_phone_layout = (TextInputLayout) findViewById(R.id.txt_phone_layout);
        txt_email_layout = (TextInputLayout) findViewById(R.id.txt_email_layout);
        txt_city_layout = (TextInputLayout) findViewById(R.id.txt_city_layout);
        txt_state_layout = (TextInputLayout) findViewById(R.id.txt_state_layout);
        txt_zip_layout = (TextInputLayout) findViewById(R.id.txt_zip_layout);

        txt_resname = (TextView) findViewById(R.id.txt_resname);
        txt_address = (TextView) findViewById(R.id.txt_address);
        txt_phone = (TextView) findViewById(R.id.txt_phone);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_city = (TextView) findViewById(R.id.txt_city);
        txt_state = (TextView) findViewById(R.id.txt_state);
        txt_zip = (TextView) findViewById(R.id.txt_zip);


        final Typeface tf = Typeface.createFromAsset(getAssets(), "Avenir-Next-Medium.ttf");
        txt_resname_layout.getEditText().setTypeface(tf);
        txt_resname_layout.setTypeface(tf);
        txt_address_layout.getEditText().setTypeface(tf);
        txt_address_layout.setTypeface(tf);
        txt_phone_layout.getEditText().setTypeface(tf);
        txt_phone_layout.setTypeface(tf);
        txt_email_layout.getEditText().setTypeface(tf);
        txt_email_layout.setTypeface(tf);
        txt_city_layout.getEditText().setTypeface(tf);
        txt_city_layout.setTypeface(tf);
        txt_zip_layout.getEditText().setTypeface(tf);
        txt_zip_layout.setTypeface(tf);
        txt_state_layout.getEditText().setTypeface(tf);
        txt_state_layout.setTypeface(tf);


        DialogUtil.showProgressDialog(this);
        RequestController.getRestaurantProfileDetail(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    public boolean checkLoginValidation() {

        boolean isValid = true;

        if (StringUtils.isNullOrEmpty(txt_resname.getText().toString())) {
            txt_resname_layout.setErrorEnabled(true);
            txt_resname_layout.setError(getString(R.string.error_email_register));
            isValid = false;
        }
        if (StringUtils.isNullOrEmpty(txt_address.getText().toString())) {
            txt_address_layout.setErrorEnabled(true);
            txt_address_layout.setError(getString(R.string.error_email_register));
            isValid = false;
        }
        if (StringUtils.isNullOrEmpty(txt_phone.getText().toString())) {
            txt_phone_layout.setErrorEnabled(true);
            txt_phone_layout.setError(getString(R.string.error_email_register));
            isValid = false;
        }
        if (StringUtils.isNullOrEmpty(txt_email.getText().toString())) {
            txt_email_layout.setErrorEnabled(true);
            txt_email_layout.setError(getString(R.string.error_email_register));
            isValid = false;
        } else {
            if (!StringUtils.isValidEmail(txt_email.getText().toString().trim(), false)) {
                txt_email_layout.setErrorEnabled(true);
                txt_email_layout.setError(getString(R.string.email_error));
                isValid = false;
            } else {
                txt_email_layout.setErrorEnabled(false);
            }
        }

        return isValid;
    }

    public void save(View view) {
        if (checkLoginValidation()) {

        }
    }

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
        DialogUtil.hideProgressDialog();
        RestaurantProfileResponse response = (RestaurantProfileResponse) obj;
        if (response.result) {
            txt_resname.setText(response.data.restaurant_name);
            LogUtils.d("===== address : "+response.data.address);
            txt_address.setText(Utils.decodeHtml(response.data.address));
            txt_phone.setText(response.data.phone);
            txt_email.setText(response.data.email);
            txt_city.setText(response.data.city_name);
            txt_state.setText(response.data.state);
            txt_zip.setText(response.data.zipcode);
        }
    }
}
