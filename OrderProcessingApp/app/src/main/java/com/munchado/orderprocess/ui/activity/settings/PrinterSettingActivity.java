package com.munchado.orderprocess.ui.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.utils.IPAddressValidator;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

public class PrinterSettingActivity extends AppCompatActivity {

    private EditText txt_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_setting);
        txt_ip = (EditText) findViewById(R.id.ip);
        setupActionbar();

        if (!StringUtils.isNullOrEmpty(PrefUtil.getIPAddress()))
            txt_ip.setText(PrefUtil.getIPAddress());
    }

    private void setupActionbar() {
        getSupportActionBar().setTitle("Printer Setting");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    public void saveIP(View view) {
        if (!StringUtils.isNullOrEmpty(txt_ip.getText().toString()) && new IPAddressValidator().validate(txt_ip.getText().toString())) {
            PrefUtil.putIPAddress(txt_ip.getText().toString());
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter valid IP Address.", Toast.LENGTH_SHORT).show();
        }
    }
}
