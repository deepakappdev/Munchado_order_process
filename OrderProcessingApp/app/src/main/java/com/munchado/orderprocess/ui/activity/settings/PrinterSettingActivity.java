package com.munchado.orderprocess.ui.activity.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.print.ModelCapability;
import com.munchado.orderprocess.utils.IPAddressValidator;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

public class PrinterSettingActivity extends AppCompatActivity {

    private EditText txt_ip;
    private TextView tv_Bluetooth_Model;
    String modelName="";
    int modelCode=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_setting);
        txt_ip = (EditText) findViewById(R.id.ip);
        tv_Bluetooth_Model = (TextView) findViewById(R.id.bluetooth_model);
        setupActionbar();

        if (!StringUtils.isNullOrEmpty(PrefUtil.getIPAddress()))
            txt_ip.setText(PrefUtil.getIPAddress());

        if (!StringUtils.isNullOrEmpty(PrefUtil.getBluetoothModel()))
        {
            tv_Bluetooth_Model.setText("Select Printer Model: "+PrefUtil.getBluetoothModel());
            modelName = PrefUtil.getBluetoothModel();
            modelCode = PrefUtil.getBluetoothModelCode();
        }
        else tv_Bluetooth_Model.setText("No Model selected");
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

        }
        PrefUtil.putBluetoothModel(modelName);
        PrefUtil.putBluetoothModelCode(modelCode);
        finish();
//        else {
//            Toast.makeText(getApplicationContext(), "Please enter valid IP Address.", Toast.LENGTH_SHORT).show();
//        }
    }

    public void showDialog(View view) {
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
                tv_Bluetooth_Model.setText("Select Printer Model: "+strName);
                modelName = strName;
                modelCode = model;
            }
        });
        builderSingle.show();
    }
}
