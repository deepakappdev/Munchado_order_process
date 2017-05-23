package com.munchado.orderprocess.ui.activity.settings;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.print.ModelCapability;
import com.munchado.orderprocess.ui.widgets.CustomButton;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.IPAddressValidator;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

import java.lang.reflect.Field;

public class PrinterSettingActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private EditText txt_ip;
    private TextInputLayout txtInput;
    private TextView tv_Bluetooth_Model;
    private Toolbar mToolbar;
    private CustomButton btn_select_printer, btn_submit;
    private RadioButton radioWifi, radioBluetooth;
    private RadioGroup radioGroup;
    String modelName = "";
    int modelCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_setting);
        txt_ip = (EditText) findViewById(R.id.ip);
        txtInput = (TextInputLayout) findViewById(R.id.email_layout);
        btn_select_printer = (CustomButton) findViewById(R.id.select_printer);
        btn_submit = (CustomButton) findViewById(R.id.submit_btn);
        tv_Bluetooth_Model = (TextView) findViewById(R.id.bluetooth_model);
        radioWifi = (RadioButton) findViewById(R.id.radioButton_wifi);
        radioBluetooth = (RadioButton) findViewById(R.id.radioButton_bluetooth);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        setupActionbar();

        Typeface type = Typeface.createFromAsset(getAssets(), "HelveticaNeueLight.ttf");
        txtInput.getEditText().setTypeface(type);
        txtInput.setTypeface(type);
        radioWifi.setTypeface(type);
        radioBluetooth.setTypeface(type);

        if (!StringUtils.isNullOrEmpty(PrefUtil.getIPAddress()))
            txt_ip.setText(PrefUtil.getIPAddress());

        Log.e("","========== set printer type : " + PrefUtil.getPrinterType());
        if (PrefUtil.getPrinterType().equalsIgnoreCase(Constants.BLUETOOTH))
            radioBluetooth.setChecked(true);
        else
            radioWifi.setChecked(true);

        if (!StringUtils.isNullOrEmpty(PrefUtil.getBluetoothModel())) {
            tv_Bluetooth_Model.setText("Select Printer Model: " + PrefUtil.getBluetoothModel());
            modelName = PrefUtil.getBluetoothModel();
            modelCode = PrefUtil.getBluetoothModelCode();
        } else tv_Bluetooth_Model.setText("No Model selected");

        btn_select_printer.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    private void setupActionbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_1);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTypeFace();
        getSupportActionBar().setTitle("Printer Setting");
    }

    private void setTypeFace() {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    public void saveIP() {
        if (!StringUtils.isNullOrEmpty(txt_ip.getText().toString()) && new IPAddressValidator().validate(txt_ip.getText().toString())) {
            PrefUtil.putIPAddress(txt_ip.getText().toString());

        }
        if (radioBluetooth.isChecked())
            PrefUtil.putPrinterType(Constants.BLUETOOTH);
        else
            PrefUtil.putPrinterType(Constants.WIFI);
        Log.e("","========== set printer type : " + PrefUtil.getPrinterType());
        PrefUtil.putBluetoothModel(modelName);
        PrefUtil.putBluetoothModelCode(modelCode);
        finish();
//        else {
//            Toast.makeText(getApplicationContext(), "Please enter valid IP Address.", Toast.LENGTH_SHORT).show();
//        }
    }

    public void showDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Select Printer Model:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(new String[]{
                "mPOP",
                "FVP10",
                "TSP100",
                "TSP650II",
                "TSP700II",
                "TSP800II",
                "SP700",
                "SM-S210i",
                "SM-S220i",
                "SM-S230i",
                "SM-T300i/T300",
                "SM-T400i",
                "SM-L200",
                "BSC10",
                "SM-S210i StarPRNT",
                "SM-S220i StarPRNT",
                "SM-S230i StarPRNT",
                "SM-T300i/T300 StarPRNT",
                "SM-T400i StarPRNT"});

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                int model = ModelCapability.NONE;

                switch (which) {
                    case 0:
                        model = ModelCapability.MPOP;
                        break;
                    case 1:
                        model = ModelCapability.FVP10;
                        break;
                    case 2:
                        model = ModelCapability.TSP100;
                        break;
                    case 3:
                        model = ModelCapability.TSP650II;
                        break;
                    case 4:
                        model = ModelCapability.TSP700II;
                        break;
                    case 5:
                        model = ModelCapability.TSP800II;
                        break;
                    case 6:
                        model = ModelCapability.SP700;
                        break;
                    case 7:
                        model = ModelCapability.SM_S210I;
                        break;
                    case 8:
                        model = ModelCapability.SM_S220I;
                        break;
                    case 9:
                        model = ModelCapability.SM_S230I;
                        break;
                    case 10:
                        model = ModelCapability.SM_T300I_T300;
                        break;
                    case 11:
                        model = ModelCapability.SM_T400I;
                        break;
                    case 12:
                        model = ModelCapability.SM_L200;
                        break;
                    case 13:
                        model = ModelCapability.BSC10;
                        break;
                    case 14:
                        model = ModelCapability.SM_S210I_StarPRNT;
                        break;
                    case 15:
                        model = ModelCapability.SM_S220I_StarPRNT;
                        break;
                    case 16:
                        model = ModelCapability.SM_S230I_StarPRNT;
                        break;
                    case 17:
                        model = ModelCapability.SM_T300I_T300_StarPRNT;
                        break;
                    case 18:
                        model = ModelCapability.SM_T400I_StarPRNT;
                        break;

                }
                tv_Bluetooth_Model.setText("Select Printer Model: " + strName);
                modelName = strName;
                modelCode = model;
            }
        });
        builderSingle.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_printer:
                showDialog();
                break;
            case R.id.submit_btn:
                saveIP();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (checkedId == R.id.radioButton_bluetooth)
            radioBluetooth.setChecked(true);
        else
            radioWifi.setChecked(true);
    }
}
