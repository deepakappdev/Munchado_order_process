package com.munchado.orderprocess.ui.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.munchado.orderprocess.R;

public class PrinterSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_setting);
        getSupportActionBar().setTitle("Printer Setting");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

}
