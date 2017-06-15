package com.munchado.orderprocess.print;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.munchado.orderprocess.listener.OnBluetoothFailListener;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by munchado on 17/3/17.
 */
public class StarPrinterUtils {

    private ProgressDialog mProgressDialog;
    String bluetoothAddress = "";
    String modelName;
    String portName;
    String macAddress, mPortSettings;
    StarIoExt.Emulation emulation;

    int selectedLanguage = PrinterSetting.LANGUAGE_ENGLISH;
    int paperSize = PrinterSetting.PAPER_SIZE_TWO_INCH;
    Activity mActivity;

    OnBluetoothFailListener mOnBluetoothFailListener;
    String printData;

    public StarPrinterUtils(Activity activity, String address, String printdata) {
        mActivity = activity;
        if (activity instanceof OnBluetoothFailListener)
            mOnBluetoothFailListener = (OnBluetoothFailListener) activity;
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Communicating...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();

        bluetoothAddress = address;
        printData = printdata;
        PrinterSetting setting = new PrinterSetting(mActivity);

        if (TextUtils.isEmpty(setting.getPortName()) || TextUtils.isEmpty(setting.getPortSettings()))
            new SearchTask().execute(PrinterSetting.IF_TYPE_BLUETOOTH);
        else
            setForPrint();
        Log.e("===", "=== portname : " + setting.getPortName());
        Log.e("===", "=== getPortSettings : " + setting.getPortSettings());
    }

    public StarPrinterUtils(Activity activity, ProgressDialog progressDialog, String address, String printdata) {
        mActivity = activity;
        if (activity instanceof OnBluetoothFailListener)
            mOnBluetoothFailListener = (OnBluetoothFailListener) activity;
        mProgressDialog = progressDialog;
//        mProgressDialog.show();

        bluetoothAddress = address;
        printData = printdata;
        PrinterSetting setting = new PrinterSetting(mActivity);

        if (TextUtils.isEmpty(setting.getPortName()) || TextUtils.isEmpty(setting.getPortSettings()))
            new SearchTask().execute(PrinterSetting.IF_TYPE_BLUETOOTH);
        else
            setForPrint();
        Log.e("===", "=== portname : " + setting.getPortName());
        Log.e("===", "=== getPortSettings : " + setting.getPortSettings());
    }

    private class SearchTask extends AsyncTask<String, Void, Void> {
        private List<PortInfo> mPortList;

        SearchTask() {
            super();
        }

        @Override
        protected Void doInBackground(String... interfaceType) {
            try {
                Log.e("==", "==== interface Type : " + interfaceType[0]);
                mPortList = StarIOPort.searchPrinter(interfaceType[0], mActivity);
                Log.e("==", "==== mPortList.size() : " + mPortList.size());
            } catch (StarIOPortException e) {
                mPortList = new ArrayList<>();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void doNotUse) {
//            PortInfo info1=new PortInfo("Fa0/20","0000.0c5a.d71e","SM-T300i/T300","XXXXXXXXXXX");
//            mPortList.add(info1);
            for (PortInfo info : mPortList) {
                addItem(info);
            }

            if (modelName.startsWith("Star Micronics") || modelName.contains("Star Micronics"))
                showModelDialog();
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    private void addItem(PortInfo info) {


        // --- Bluetooth ---
        // It can communication used device name(Ex.BT:Star Micronics) at bluetooth.
        // If android device has paired two same name device, can't choose destination target.
        // If used Mac Address(Ex. BT:00:12:3f:XX:XX:XX) at Bluetooth, can choose destination target.
        if (info.getPortName().startsWith(PrinterSetting.IF_TYPE_BLUETOOTH)) {
            modelName = info.getPortName().substring(PrinterSetting.IF_TYPE_BLUETOOTH.length());
            portName = PrinterSetting.IF_TYPE_BLUETOOTH + info.getMacAddress();
            macAddress = info.getMacAddress();
        } else {
            modelName = info.getModelName();
            portName = info.getPortName();
            macAddress = info.getMacAddress();


        }
        Log.e("====", "=== modelName : " + modelName + "==== portName: " + portName + "==== macAddress : " + macAddress);
        PrinterSetting setting = new PrinterSetting(mActivity);
//        setting.write(modelName, portName, macAddress, mPortSettings, emulation, false);
        if (setting.getPortName().equals(portName)) {
//            imgList.add(new ImgInfo(R.drawable.checked_icon, R.id.checkedIconImageView));
        } else {
//            imgList.add(new ImgInfo(R.drawable.unchecked_icon, R.id.checkedIconImageView));
        }
    }

    private void showModelDialog() {
        if (!StringUtils.isNullOrEmpty(PrefUtil.getBluetoothModel()) && PrefUtil.getBluetoothModelCode() != ModelCapability.NONE) {
            int model = PrefUtil.getBluetoothModelCode();
            mPortSettings = ModelCapability.getPortSettings(model);
            emulation = ModelCapability.getEmulation(model);

            Log.e("===", "=== mPortSettings : " + mPortSettings + "===== " + model);
            PrinterSetting setting = new PrinterSetting(mActivity);
            setting.write(modelName, portName, macAddress, mPortSettings, emulation, false);
            setForPrint();
        } else {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(mActivity);
            builderSingle.setTitle("Select Printer Model:-");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_singlechoice);
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
                    mPortSettings = ModelCapability.getPortSettings(model);
                    emulation = ModelCapability.getEmulation(model);
                    PrefUtil.putBluetoothModel(strName);
                    PrefUtil.putBluetoothModelCode(model);
                    Log.e("===", "=== mPortSettings : " + mPortSettings + "===== " + model);
                    PrinterSetting setting = new PrinterSetting(mActivity);
                    setting.write(modelName, portName, macAddress, mPortSettings, emulation, false);
                    dialog.dismiss();
                    setForPrint();

                }
            });
            builderSingle.show();
        }

    }

    private void setForPrint() {
        PrinterSetting setting = new PrinterSetting(mActivity);
        StarIoExt.Emulation emulation = setting.getEmulation();

        ILocalizeReceipts localizeReceipts = ILocalizeReceipts.createLocalizeReceipts(selectedLanguage, paperSize);
        String languageCode = localizeReceipts.getLanguageCode();
        String paperSizeStr = localizeReceipts.getPaperSizeStr();
        String scalePaperSizeStr = localizeReceipts.getScalePaperSizeStr();

        PrintTask printTask = new PrintTask();
        printTask.execute(1);
    }


    private class PrintTask extends AsyncTask<Integer, Void, Communication.Result> {
        @Override
        protected void onPreExecute() {
            if (null != mActivity && !mActivity.isFinishing() && !mProgressDialog.isShowing())
                mProgressDialog.show();
        }

        @Override
        protected Communication.Result doInBackground(Integer... params) {

            if (null != mActivity && !mActivity.isFinishing()) {
                byte[] commands;
                int selectedIndex = params[0];

                PrinterSetting setting = new PrinterSetting(mActivity);
                StarIoExt.Emulation emulation = setting.getEmulation();

                ILocalizeReceipts localizeReceipts = ILocalizeReceipts.createLocalizeReceipts(selectedLanguage, paperSize);

                switch (selectedIndex) {
                    default:
                    case 1:
                        commands = PrinterFunctions.createTextReceiptData(emulation, localizeReceipts, printData, false);
                        break;
                    case 2:
                        commands = PrinterFunctions.createTextReceiptData(emulation, localizeReceipts, printData, true);
                        break;
                    case 3:
                        commands = PrinterFunctions.createRasterReceiptData(emulation, localizeReceipts, mActivity.getResources());
                        break;
                    case 4:
                        commands = PrinterFunctions.createScaleRasterReceiptData(emulation, localizeReceipts, mActivity.getResources(), paperSize, true);
                        break;
                    case 5:
                        commands = PrinterFunctions.createScaleRasterReceiptData(emulation, localizeReceipts, mActivity.getResources(), paperSize, false);
                        break;
                    case 6:
                        commands = PrinterFunctions.createCouponData(emulation, localizeReceipts, mActivity.getResources(), paperSize, ICommandBuilder.BitmapConverterRotation.Normal);
                        break;
                    case 7:
                        commands = PrinterFunctions.createCouponData(emulation, localizeReceipts, mActivity.getResources(), paperSize, ICommandBuilder.BitmapConverterRotation.Right90);
                        break;
                }

                Communication.Result result;

                Log.e("===", "=== portname : " + setting.getPortName());
                Log.e("===", "=== getPortSettings : " + setting.getPortSettings());
                result = Communication.sendCommands(commands, setting.getPortName(), setting.getPortSettings(), 10000, mActivity);     // 10000mS!!!

                return result;
            }
            return null;

        }

        @Override
        protected void onPostExecute(Communication.Result result) {

            if (null != mActivity && !mActivity.isFinishing() && null != result) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                String msg;

                switch (result) {
                    case Success:
                        msg = "Success!";
                        break;
                    case ErrorOpenPort:
                        msg = "Fail to openPort\nTry again.";
                        if (null != mOnBluetoothFailListener)
                            mOnBluetoothFailListener.onBluetoothFail();
                        break;
                    case ErrorBeginCheckedBlock:
                        msg = "Printer is offline (beginCheckedBlock)\n" +
                                "Try again.";
                        break;
                    case ErrorEndCheckedBlock:
                        msg = "Printer is offline (endCheckedBlock)\n" +
                                "Try again.";
                        break;
                    case ErrorReadPort:
                        msg = "Read port error (readPort)\n" +
                                "Try again.";
                        if (null != mOnBluetoothFailListener)
                            mOnBluetoothFailListener.onBluetoothFail();
                        break;
                    case ErrorWritePort:
                        msg = "Write port error (writePort)\n" +
                                "Try again.";
                        if (null != mOnBluetoothFailListener)
                            mOnBluetoothFailListener.onBluetoothFail();
                        break;
                    default:
                        msg = "Unknown error\n" +
                                "Try again.";
                        if (null != mOnBluetoothFailListener)
                            mOnBluetoothFailListener.onBluetoothFail();
                        break;
                }

                Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
                if (!msg.equalsIgnoreCase("Success!")) {
                    PrinterSetting setting = new PrinterSetting(mActivity);
                    setting.write("", "", "", "", emulation, false);
                }
                mActivity.finish();
            }


//            CommonAlertDialogFragment dialog = CommonAlertDialogFragment.newInstance("CommResultDialog");
//            dialog.setTitle("Communication Result");
//            dialog.setMessage(msg);
//            dialog.setPositiveButton("OK");
//            dialog.show(getChildFragmentManager());
        }
    }
}
