package com.munchado.orderprocess.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.munchado.orderprocess.MyApplication;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.print.BluetoothDeviceModel;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscoveryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Context mContext = null;
    private ArrayList<HashMap<String, Object>> mPrinterList = null;
    private SimpleAdapter mPrinterListAdapter = null;
    private FilterOption mFilterOption = null;
    private static final Integer LOCATION = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        LogUtils.d("=== DiscoveryActivity");
        mContext = this;

        askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION);

        Button button = (Button) findViewById(R.id.btnRestart);
        button.setOnClickListener(this);

        mPrinterList = new ArrayList<HashMap<String, Object>>();
        mPrinterListAdapter = new SimpleAdapter(this, mPrinterList, R.layout.list_at,
                new String[]{"PrinterName", "Target"},
                new int[]{R.id.PrinterName, R.id.Target});
        ListView list = (ListView) findViewById(R.id.lstReceiveData);
        list.setAdapter(mPrinterListAdapter);
        list.setOnItemClickListener(this);

        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NONE);
        try {
            if (mDiscoveryListener != null)
                Discovery.start(this, mFilterOption, mDiscoveryListener);
        } catch (Epos2Exception e) {
            e.printStackTrace();
            if (e.getErrorStatus() != Epos2Exception.ERR_ILLEGAL) {
                // do nothing, it is connected.
            }
        }
    }

    // --------------------------------------------------------------------------------------------

    /**
     * onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        while (true) {
            try {
                Discovery.stop();
                break;
            } catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }

        mFilterOption = null;
    }

    // --------------------------------------------------------------------------------------------

    /**
     * onClick
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRestart:
                restartDiscovery();
                break;

            default:
                // Do nothing
                break;
        }
    }

    // --------------------------------------------------------------------------------------------

    /**
     * On Item click
     *
     * @param parent   AdapterView<?>
     * @param view     clicked view
     * @param position position
     * @param id       id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent();

        HashMap<String, Object> item = mPrinterList.get(position);
        BluetoothDeviceModel mBluetoothDeviceModel = new BluetoothDeviceModel();
        mBluetoothDeviceModel.title_name = String.valueOf(item.get("PrinterName"));
        mBluetoothDeviceModel.title_target = ((DeviceInfo) item.get("DeviceInfo")).getTarget();
        mBluetoothDeviceModel.title_interface = Utility.getInterfaceStringFromEposConnectionType(Utility.convertEpos2DeviceInfoToEposEasySelectDeviceType((DeviceInfo) item.get("DeviceInfo")));
        mBluetoothDeviceModel.title_address = Utility.getAddressFromEpos2DeviceInfo((DeviceInfo) item.get("DeviceInfo"));
        mBluetoothDeviceModel.title_devicetype = String.valueOf(Utility.convertEpos2DeviceInfoToEposEasySelectDeviceType((DeviceInfo) item.get("DeviceInfo")));

        MyApplication.mTarget = mBluetoothDeviceModel.title_target;
        MyApplication.mInterfaceType = mBluetoothDeviceModel.title_interface;
        MyApplication.mAddress = mBluetoothDeviceModel.title_address;
        MyApplication.printerName = mBluetoothDeviceModel.title_name;

//        intent.putExtra(getString(R.string.title_target), ((DeviceInfo) item.get("DeviceInfo")).getTarget());
//        intent.putExtra(getString(R.string.title_interface),
//                Utility.getInterfaceStringFromEposConnectionType(Utility.convertEpos2DeviceInfoToEposEasySelectDeviceType((DeviceInfo) item.get("DeviceInfo"))));
//        intent.putExtra(getString(R.string.title_address),
//                Utility.getAddressFromEpos2DeviceInfo((DeviceInfo) item.get("DeviceInfo")));
//        intent.putExtra(getString(R.string.title_devicetype), Utility.convertEpos2DeviceInfoToEposEasySelectDeviceType((DeviceInfo) item.get("DeviceInfo")));
//
//        setResult(RESULT_OK, intent);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("device", mBluetoothDeviceModel);
//        intent.putExtras(bundle);
//        setResult(RESULT_OK, intent);
        finish();
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Restart discovery
     */
    private void restartDiscovery() {
        while (true) {
            try {
                Discovery.stop();
                break;
            } catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    //ShowMsg.showException(e, "stop", mContext);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mPrinterList.clear();
        mPrinterListAdapter.notifyDataSetChanged();

        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        } catch (Exception e) {
            e.printStackTrace();
            //ShowMsg.showException(e, "start", mContext);
        }
    }

    // --------------------------------------------------------------------------------------------
    /**
     * Listener Registration Method
     *
     * @param deviceInfo DeviceInfo object
     */
    private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("PrinterName", deviceInfo.getDeviceName());
                    item.put("Target", deviceInfo.getTarget());
                    item.put("DeviceInfo", deviceInfo);
                    mPrinterList.add(item);
                    mPrinterListAdapter.notifyDataSetChanged();
                    LogUtils.d("===" + deviceInfo.getDeviceName());
                }
            });
        }
    };

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(DiscoveryActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DiscoveryActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(DiscoveryActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(DiscoveryActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            startDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            startDiscovery();
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startDiscovery() {
        //Disable bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();

        if (mBluetoothAdapter.isEnabled()) {
            mFilterOption = new FilterOption();
            mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
            mFilterOption.setEpsonFilter(Discovery.FILTER_NONE);
            try {

                Discovery.start(this, mFilterOption, mDiscoveryListener);
            } catch (Exception e) {
                e.printStackTrace();
                //ShowMsg.showException(e, "start", mContext);
            }
        }
    }

}
