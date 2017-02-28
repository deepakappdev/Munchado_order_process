package com.munchado.orderprocess.utils;

import android.app.Activity;
import android.view.View;

import com.epson.easyselect.EasySelect;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.munchado.orderprocess.MyApplication;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponse;

import java.util.Arrays;

/**
 * Created by munchado on 28/2/17.
 */
public class PrintReceiptUtils implements ReceiveListener {

    public Printer mPrinter = null;
    public String printData = "";
    public Activity activity;
    public OrderDetailResponse response;
    View progressBar;

    public PrintReceiptUtils(Activity activity,String printdata,OrderDetailResponse response,View progressBar){
        this.printData=printdata;
        this.activity=activity;
        this.response=response;
        this.progressBar=progressBar;
    }


    /**
     * Print data
     *
     * @return boolean result
     */
    private boolean printData() {
        String msg = "";
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;
        if (mPrinter == null) {
            return false;
        }
        try {
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            printData+= ReceiptFormatUtils.seperator;
            mPrinter.addText(printData);
            mPrinter.addTextSize(2, 2);
            int nospace= ReceiptFormatUtils.CONTENT_LENGTH-("$"+response.data.order_amount_calculation.total_order_price).length()*2-10;
            char[] chars = new char[nospace/2];
            Arrays.fill(chars, ' ');
            String string =new String(chars);
            mPrinter.addText("TOTAL"+string+"$"+response.data.order_amount_calculation.total_order_price+"\n");
            mPrinter.addTextSize(1, 1);
            mPrinter.addText(ReceiptFormatUtils.seperator);
            mPrinter.addFeedLine(1);
            mPrinter.addText("See you soon!!");
            mPrinter.addFeedLine(2);


            String qrCode = new String();
            // QR code size
            final int qrcodeWidth = 5;
            final int qrcodeHeight = 5;

            try {
                EasySelect easySelect = new EasySelect();

                // create QR code data from EasySelect library
                qrCode = easySelect.createQR(MyApplication.printerName,
                        MyApplication.mDeviceType,
                        MyApplication.mAddress);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                // QR Code
                mPrinter.addSymbol(qrCode,
                        Printer.SYMBOL_QRCODE_MODEL_2,
                        Printer.LEVEL_L,
                        qrcodeWidth,
                        qrcodeHeight,
                        0);
                mPrinter.addFeedLine(1);

            } catch (Epos2Exception e) {
                ShowMsg.showException(e, "", activity);
                return false;
            }

            mPrinter.addCut(Printer.CUT_FEED);
        } catch (Epos2Exception e) {
            e.printStackTrace();
        }
        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), activity);
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception ex) {
                // Do nothing
            }
            finalizeObject();
            return false;
        }

        try {
            msg = "sendData";
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, msg, activity);
            disconnectPrinter();
            return false;
        }

        return true;
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Initialize printer object
     *
     * @return boolean result
     */
    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Utility.convertPrinterNameToPrinterSeries(MyApplication.printerName),
                    Printer.MODEL_ANK,
                    activity);
            LogUtils.d("====== initialize printer");
        } catch (Exception e) {
            ShowMsg.showException(e, "Printer", activity);
            e.printStackTrace();
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Finalize printer object
     */
    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }
        LogUtils.d("====== finalizeObject");
        progressBar.setVisibility(View.GONE);
//        mPrinter.clearCommandBuffer();
//
//        mPrinter.setReceiveEventListener(null);
//
//        mPrinter = null;
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Connect printer
     *
     * @return boolean result
     */
    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect(MyApplication.mTarget, Printer.PARAM_DEFAULT);
            LogUtils.d("====== mPrinter connected");
        } catch (Exception e) {
            ShowMsg.showException(e, "connect", activity);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
            LogUtils.d("====== mPrinter beginTransaction");
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", activity);
        }

        if (!isBeginTransaction) {
            try {
                mPrinter.disconnect();
                LogUtils.d("====== mPrinter disconnect");
            } catch (Epos2Exception e) {
                return false;
            }
        }

        return true;
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Disconnect printer
     */
    private void disconnectPrinter() {
        String method = "";
        LogUtils.d("====== disconnectPrinter ");
        if (mPrinter == null) {
            return;
        }

        try {
            method = "endTransaction";
            mPrinter.endTransaction();
        } catch (final Exception e) {
            final String errorMethod = method;

//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public synchronized void run() {
//                    ShowMsg.showException(e, errorMethod, activity);
//                }
//            });
        }

        try {
            method = "disconnect";
            mPrinter.disconnect();
        } catch (final Exception e) {
            final String errorMethod = method;

//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public synchronized void run() {
//                    ShowMsg.showException(e, errorMethod, activity);
//                }
//            });
        }

        finalizeObject();
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Disconnect printer
     *
     * @param status PrinterStatusInfo
     * @return boolean result
     */
    private boolean isPrintable(PrinterStatusInfo status) {
        LogUtils.d("====== isPrintable false");
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            //print available
        }
        LogUtils.d("====== isPrintable true");
        return true;
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Make error message
     *
     * @param status PrinterStatusInfo
     * @return String error message
     */
    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += activity.getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += activity.getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += activity.getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += activity.getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += activity.getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += activity.getString(R.string.handlingmsg_err_autocutter);
            msg += activity.getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += activity.getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += activity.getString(R.string.handlingmsg_err_overheat);
                msg += activity.getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += activity.getString(R.string.handlingmsg_err_overheat);
                msg += activity.getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += activity.getString(R.string.handlingmsg_err_overheat);
                msg += activity.getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += activity.getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += activity.getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }


    /**
     * Display warnings
     *
     * @param status PrinterStatusInfo
     */
    private void dispPrinterWarnings(PrinterStatusInfo status) {
//        EditText edtWarnings = (EditText) findViewById(R.id.edtWarnings);
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += activity.getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += activity.getString(R.string.handlingmsg_warn_battery_near_end);
        }

//        edtWarnings.setText(warningsMsg);
    }

    @Override
    public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                LogUtils.d("====== onPtrReceive ");
//                ShowMsg.showResult(code, makeErrorMessage(status), mactivity);
//
//                dispPrinterWarnings(status);
//
//                updateButtonState(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    /**
     * Run Print QRCode Sequence
     */
    public boolean runPrintQRCodeSequence() {

        if (!initializeObject()) {
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }
        if (!StringUtils.isNullOrEmpty(printData)) {
            finalizeObject();
            return false;
        }


        return true;
    }

}
