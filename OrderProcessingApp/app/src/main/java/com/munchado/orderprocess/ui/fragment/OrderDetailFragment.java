package com.munchado.orderprocess.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.easyselect.EasySelect;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.munchado.orderprocess.MyApplication;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.model.orderdetail.MyItemList;
import com.munchado.orderprocess.model.orderdetail.OrderAmountCalculation;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponse;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponseData;
import com.munchado.orderprocess.model.orderprocess.OrderProcessResponse;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.DiscoveryActivity;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.PrintUtils;
import com.munchado.orderprocess.utils.ShowMsg;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utility;
import com.munchado.orderprocess.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by android on 23/2/17.
 */

public class OrderDetailFragment extends BaseFragment implements RequestCallback, View.OnClickListener, ReceiveListener {

    private TextView textName;
    private TextView textEmail;
    private TextView textTelephone;
    private TextView textPastActivity;
    private TextView textSubtotal;
    private TextView textDealDiscount;
    private TextView textDelivery;
    private TextView textTax;
    private TextView textTip;
    private TextView textTotal;
    private LinearLayout orderLayout;
    private TextView textOrderId;
    private TextView textOrderType;
    private TextView textOrderTime;
    private TextView labelOrderTime;
    private TextView textDeliveryTime;
    private TextView labelDeliveryAddress;
    private TextView textDeliveryAddress;
    private OrderDetailResponse response;
    private ImageView imageView;
    private View progressBar;
    private TextView textAction;
    private TextView textCancel;
    private TextView textPrint;
    private String printData = "";

    public Printer mPrinter = null;

    public static int REQUEST_CODE_DISCOVER_PRINTER = 111;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_order_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        fetchOrderDetail();
    }

    private void fetchOrderDetail() {
        showProgressBar();
        Bundle bundle = getArguments();
        String orderId = bundle.getString("ORDER_ID");
        RequestController.getOrderDetail(orderId, this);
    }

    private void initView(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
        textName = (TextView) view.findViewById(R.id.text_name);
        textEmail = (TextView) view.findViewById(R.id.text_email);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        textTelephone = (TextView) view.findViewById(R.id.text_telephone);
        textPastActivity = (TextView) view.findViewById(R.id.text_past_activity);

        textOrderId = (TextView) view.findViewById(R.id.text_order_id);
        textOrderType = (TextView) view.findViewById(R.id.text_order_type);
        textOrderTime = (TextView) view.findViewById(R.id.text_order_time);
        labelOrderTime = (TextView) view.findViewById(R.id.label_order_time);
        textDeliveryTime = (TextView) view.findViewById(R.id.text_delivery_time);
        labelDeliveryAddress = (TextView) view.findViewById(R.id.label_delivery_address);
        textDeliveryAddress = (TextView) view.findViewById(R.id.text_delivery_address);

        orderLayout = (LinearLayout) view.findViewById(R.id.order_layout);

        textSubtotal = (TextView) view.findViewById(R.id.text_subtotal);
        textDealDiscount = (TextView) view.findViewById(R.id.text_deal_discount);
        textDelivery = (TextView) view.findViewById(R.id.text_delivery);
        textTax = (TextView) view.findViewById(R.id.text_tax);
        textTip = (TextView) view.findViewById(R.id.text_tip);
        textTotal = (TextView) view.findViewById(R.id.text_total);

        (textPrint = (TextView) view.findViewById(R.id.text_print)).setOnClickListener(this);
        textPrint.setPaintFlags(textPrint.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        (textCancel = (TextView) view.findViewById(R.id.text_cancel)).setOnClickListener(this);
        textCancel.setPaintFlags(textCancel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        (textAction = (TextView) view.findViewById(R.id.btn_action)).setOnClickListener(this);

    }

    private void showOrderDetail(OrderDetailResponseData orderDetailData) {
        textOrderId.setText(orderDetailData.id);
        textOrderType.setText(orderDetailData.order_type);
        textOrderTime.setText(orderDetailData.order_date);
        if (orderDetailData.order_type.equalsIgnoreCase("takeout")) {
            labelOrderTime.setText("Time of Takeout");
            labelDeliveryAddress.setVisibility(View.GONE);
            textDeliveryAddress.setVisibility(View.GONE);

        } else {
            labelOrderTime.setText("Time of Delivery");
            labelDeliveryAddress.setVisibility(View.VISIBLE);
            textDeliveryAddress.setVisibility(View.VISIBLE);
            textDeliveryAddress.setText(orderDetailData.my_delivery_detail.apt_suite + ", " + orderDetailData.my_delivery_detail.address +
                    "\n" + orderDetailData.my_delivery_detail.state + "-" + orderDetailData.my_delivery_detail.zipcode);
        }
        textDeliveryTime.setText(orderDetailData.delivery_date);
    }

    @Override
    public void error(NetworkError volleyError) {
        hideProgressBar();
    }

    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.ORDER_DETAIL;
    }

    @Override
    public void success(Object obj) {
        hideProgressBar();
        if (obj instanceof OrderDetailResponse) {
            response = (OrderDetailResponse) obj;
//            responseForPrint = response;

            printData = PrintUtils.setPrintData(response.data);
            LogUtils.d(printData);
            showDetail(response.data);
        } else if (obj instanceof OrderProcessResponse) {
            if (((OrderProcessResponse) obj).data.message) {
                response.data.status = ((OrderProcessResponse) obj).data.status;
                updateActionButton();
            }
        }
    }

    void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressBar() {
        progressBar.setVisibility(View.GONE);

    }

    private void showDetail(OrderDetailResponseData data) {
        textName.setText(data.customer_first_name + " " + data.customer_last_name);
        textTelephone.setText(data.my_delivery_detail.phone);
        textEmail.setText(data.email);

        Picasso.with(getContext()).load(data.user_image)
                .placeholder(R.drawable.profile_img)
                .into(imageView);

        StringBuilder pastActivity = new StringBuilder();
        if (data.user_activity != null) {
            if (data.user_activity.total_user_order > 0)
                pastActivity.append(data.user_activity.total_user_order).append(" Orders");
            if (data.user_activity.total_user_reservation > 0)
                pastActivity.append(", ").append(data.user_activity.total_user_reservation).append(" Reservation");
            if (data.user_activity.total_user_checkin > 0)
                pastActivity.append(", ").append(data.user_activity.total_user_checkin).append(" Checkins");
            if (data.user_activity.total_user_review > 0)
                pastActivity.append(", ").append(data.user_activity.total_user_review).append(" Reviews");
        }
        if (pastActivity.toString().isEmpty())
            textPastActivity.setText("No Past Activity");
        else
            textPastActivity.setText(pastActivity.toString());

        showOrderDetail(data);
        showOrderItem(data.item_list);
        showOrderPaymentDetail(data.order_amount_calculation);
        updateActionButton();
    }

    private void showOrderItem(ArrayList<MyItemList> item_list) {
        orderLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(orderLayout.getContext());
        for (MyItemList itemList : item_list) {
            View view = inflater.inflate(R.layout.row_order_item, null);
            TextView itemName = (TextView) view.findViewById(R.id.text_item_name);
            TextView textInstruction = (TextView) view.findViewById(R.id.text_instruction);
            TextView itemCount = (TextView) view.findViewById(R.id.text_item_count);
            TextView itemPrice = (TextView) view.findViewById(R.id.text_item_price);

            itemName.setText(itemList.item_name);
            if (StringUtils.isNullOrEmpty(itemList.item_special_instruction)) {
                textInstruction.setVisibility(View.GONE);
            } else {
                textInstruction.setText(itemList.item_special_instruction);
            }
            itemCount.setText(itemList.item_qty);
            itemPrice.setText("$" + (Utils.parseDouble(itemList.unit_price) * Utils.parseDouble(itemList.item_qty)));
            orderLayout.addView(view);
        }
    }

    void updateActionButton() {
        textPrint.setVisibility(View.VISIBLE);
        textAction.setVisibility(View.VISIBLE);
        textCancel.setVisibility(View.VISIBLE);
        String currentStatus = response.data.status;
        if (currentStatus.equalsIgnoreCase("placed")) {
            textAction.setText("Confirm");
            textAction.setBackgroundResource(R.drawable.green_button);
        } else if (currentStatus.equalsIgnoreCase("confirmed")) {
            textAction.setBackgroundResource(R.drawable.grey_button);
            if (response.data.order_type.equalsIgnoreCase("takeout"))
                textAction.setText("Picked Up");
            else
                textAction.setText("Sent");
        } else {
            textAction.setVisibility(View.GONE);
            textCancel.setVisibility(View.GONE);
        }
    }

    private void showOrderPaymentDetail(OrderAmountCalculation payment_detail) {
        textSubtotal.setText("$" + payment_detail.subtotal);
        textDealDiscount.setText("$" + payment_detail.promocode_discount);

        if (Utils.parseDouble(payment_detail.delivery_charge) == 0)
            textDelivery.setText("Free");
        else
            textDelivery.setText("$" + payment_detail.delivery_charge);

        textTax.setText("$" + payment_detail.tax_amount);
        textTip.setText("$" + payment_detail.tip_amount);
        textTotal.setText("$" + payment_detail.total_order_price);
    }

    @Override
    public void onClick(View view) {
        String currentStatus = response.data.status;
        switch (view.getId()) {
            case R.id.text_print:
                new PrintUtils().setPrintData(response.data);
            case R.id.btn_print:
//                new PrintUtils().setPrintData(response.data);
                if (StringUtils.isNullOrEmpty(MyApplication.printerName)) {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.enable();
                        Toast.makeText(getActivity(), "Bluetooth is off. Trying to switch ON. Please wait...", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    LogUtils.d("==== "+printData);
                    Intent intent = new Intent(getActivity(), DiscoveryActivity.class);
                    startActivity(intent);
                } else {
//                    LogUtils.d("==== on click else");
//                    Gson gsonObj = new Gson();
//                    String jsonStr = gsonObj.toJson(response);
//                    LogUtils.d("==== "+jsonStr);
//                    LogUtils.d("==== "+printData);
//                    if (mPrinter == null) {
//                        return ;
//                    }
//                    runPrintQRCodeSequence();

                }
                break;
            case R.id.btn_action:

                String status = "";
                if (currentStatus.equalsIgnoreCase("placed"))
                    status = "confirmed";
                else if (currentStatus.equalsIgnoreCase("confirmed")) {
                    if (currentStatus.equalsIgnoreCase("takeout"))
                        status = "arrived";
                    else
                        status = "delivered";
                }
                RequestController.orderProcess(response.data.id, status, "", OrderDetailFragment.this);
                break;
            case R.id.text_cancel:
                askUserForReason();

                break;

        }
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
            mPrinter.addText(printData);
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
//                if (null == qrCode) {
//                    ShowMsg.showException(e, "", getActivity());
//                    return false;
//                }
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
                ShowMsg.showException(e, "", getActivity());
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
            ShowMsg.showMsg(makeErrorMessage(status), getActivity());
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
            ShowMsg.showException(e, msg, getActivity());
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
                    getActivity());
            LogUtils.d("====== initialize printer");
        } catch (Exception e) {
            ShowMsg.showException(e, "Printer", getActivity());
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
            ShowMsg.showException(e, "connect", getActivity());
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
            LogUtils.d("====== mPrinter beginTransaction");
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", getActivity());
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, errorMethod, getActivity());
                }
            });
        }

        try {
            method = "disconnect";
            mPrinter.disconnect();
        } catch (final Exception e) {
            final String errorMethod = method;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, errorMethod, getActivity());
                }
            });
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
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
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
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
        }

//        edtWarnings.setText(warningsMsg);
    }

    @Override
    public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                LogUtils.d("====== onPtrReceive ");
//                ShowMsg.showResult(code, makeErrorMessage(status), mContext);
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
    private boolean runPrintQRCodeSequence() {

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

    private void askUserForReason() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Cancel Order");
        alertDialog.setMessage("Enter Reason");

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Cancel Order",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String message = input.getText().toString().trim();
                        if (message.length() > 0) {
                            showProgressBar();
                            RequestController.orderProcess(response.data.id, "cancelled", input.getText().toString(), OrderDetailFragment.this);
                        } else showToast("Invalid Reason.");
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}