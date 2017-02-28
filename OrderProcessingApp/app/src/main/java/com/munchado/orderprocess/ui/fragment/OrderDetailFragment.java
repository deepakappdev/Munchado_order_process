package com.munchado.orderprocess.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.munchado.orderprocess.MyApplication;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.model.orderdetail.AddonsList;
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
import com.munchado.orderprocess.utils.PrintReceiptUtils;
import com.munchado.orderprocess.utils.ReceiptFormatUtils;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by android on 23/2/17.
 */

public class OrderDetailFragment extends BaseFragment implements RequestCallback, View.OnClickListener {

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



    public static int REQUEST_CODE_DISCOVER_PRINTER = 111;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return rootView = inflater.inflate(R.layout.frag_order_detail, container, false);
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
        rootView.findViewById(R.id.layout_order_detail).setVisibility(View.VISIBLE);
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
        showToast(volleyError.getLocalizedMessage());
    }

    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.ORDER_DETAIL;
    }

    @Override
    public void success(Object obj) {
        if(getActivity()==null)return;
        hideProgressBar();
        if (obj instanceof OrderDetailResponse) {
            response = (OrderDetailResponse) obj;

                    printData = ReceiptFormatUtils.setPrintData(response.data);

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
        rootView.findViewById(R.id.layout_customer_detail).setVisibility(View.VISIBLE);
        StringBuilder nameBuilder = new StringBuilder(data.customer_first_name);
        if(!StringUtils.isNullOrEmpty(data.customer_last_name))
            nameBuilder.append(" ").append(data.customer_last_name);
        textName.setText(nameBuilder.toString());
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
            LinearLayout layoutAddons = (LinearLayout) view.findViewById(R.id.layout_add_ons);
            if(StringUtils.isNullOrEmpty(itemList.addons_list))
                layoutAddons.setVisibility(View.GONE);
            else {
                layoutAddons.setVisibility(View.VISIBLE);
                for (AddonsList addonsItem:itemList.addons_list) {
                    View addonView = inflater.inflate(R.layout.row_addon_item, null);
                    itemName = (TextView) addonView.findViewById(R.id.text_item_name);
                    itemCount = (TextView) addonView.findViewById(R.id.text_item_count);
                    itemPrice = (TextView) addonView.findViewById(R.id.text_item_price);

                    itemName.setText(addonsItem.addon_name);
                    itemCount.setText(addonsItem.addon_quantity);
                    itemPrice.setText("$" + addonsItem.addons_total_price);
                    layoutAddons.addView(addonView);
                }
            }




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
        rootView.findViewById(R.id.layout_order_payment).setVisibility(View.VISIBLE);
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
                if (StringUtils.isNullOrEmpty(MyApplication.printerName)) {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.enable();
                        Toast.makeText(getActivity(), "Bluetooth is off. Trying to switch ON. Please wait...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(getActivity(), DiscoveryActivity.class);
                    startActivity(intent);
                } else {
                    showProgressBar();
                    new PrintReceiptUtils(getActivity(),printData,response,progressBar).runPrintQRCodeSequence();

                }
                break;
            case R.id.btn_action:
                showProgressBar();
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
                if(progressBar.getVisibility()!=View.VISIBLE)
                    askUserForReason();

                break;

        }
    }


    private void askUserForReason() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

}