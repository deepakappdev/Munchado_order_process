package com.munchado.orderprocess.ui.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.utils.PrefUtil;

public class OrderProcessingSettingActivity extends AppCompatActivity implements View.OnClickListener {

    CheckBox mCheckBox;
        private RadioGroup radioGroup;
    private RadioButton radiosleep, radioalwayson;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_processing_setting);

        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_setting);
        radiosleep = (RadioButton) findViewById(R.id.radioButton_sleep);
        radioalwayson = (RadioButton) findViewById(R.id.radioButton_alwayson);
        btnSubmit = (Button) findViewById(R.id.submit_btn);

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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioalwayson.getId() == checkedId)
                    radioalwayson.setChecked(true);
                else
                    radiosleep.setChecked(true);
            }
        });

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (mCheckBox.isChecked()) {
            PrefUtil.setManualPrint(true);
        } else
            PrefUtil.setManualPrint(false);

        if (radioalwayson.isChecked())
            PrefUtil.setScreenON(true);
        else
            PrefUtil.setScreenON(false);
        finish();
    }
}
