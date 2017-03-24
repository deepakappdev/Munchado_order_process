package com.munchado.orderprocess.ui.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.utils.PrefUtil;

public class OrderProcessingSettingActivity extends AppCompatActivity {

    CheckBox mCheckBox;
    private RadioGroup radioGroup;
    private RadioButton radiosleep, radioalwayson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_processing_setting);

        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_setting);
        radiosleep = (RadioButton) findViewById(R.id.radioButton_sleep);
        radioalwayson = (RadioButton) findViewById(R.id.radioButton_alwayson);

        getSupportActionBar().setTitle("Order Processing Setting");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (PrefUtil.isManualPrint())
            mCheckBox.setChecked(true);
        else
            mCheckBox.setChecked(false);

        if (PrefUtil.isScreenOn())
            radioalwayson.setChecked(true);
        else
            radiosleep.setChecked(true);
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

        if (radioGroup.getCheckedRadioButtonId() == radioalwayson.getId())
            PrefUtil.setScreenON(true);
        else
            PrefUtil.setScreenON(false);

        finish();
    }
}
