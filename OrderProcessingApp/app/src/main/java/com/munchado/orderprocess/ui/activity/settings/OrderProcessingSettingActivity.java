package com.munchado.orderprocess.ui.activity.settings;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.utils.PrefUtil;

import java.lang.reflect.Field;

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

        Typeface face = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Medium.ttf");
        mCheckBox.setTypeface(face);
        radioalwayson.setTypeface(face);
        radiosleep.setTypeface(face);

        setupToolbar();

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

    private void setupToolbar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_1);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView titleTextView = null;

        try {
            Field f = mToolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(mToolbar);
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "HelveticaNeue-Medium.ttf");
            titleTextView.setTypeface(face, Typeface.BOLD);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        getSupportActionBar().setTitle("Order Processing Setting");
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
