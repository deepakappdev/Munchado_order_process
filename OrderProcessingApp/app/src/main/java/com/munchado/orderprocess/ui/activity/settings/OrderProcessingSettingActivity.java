package com.munchado.orderprocess.ui.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.utils.PrefUtil;

public class OrderProcessingSettingActivity extends AppCompatActivity {

    CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_processing_setting);

        mCheckBox = (CheckBox) findViewById(R.id.checkBox);

        getSupportActionBar().setTitle("Order Processing Setting");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (PrefUtil.isManualPrint())
            mCheckBox.setChecked(true);
        else
            mCheckBox.setChecked(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    public void saveSetting(View view) {
        if (mCheckBox.isChecked()) {
            PrefUtil.setManualPrint(true);
        } else
            PrefUtil.setManualPrint(false);
        finish();
    }
}
