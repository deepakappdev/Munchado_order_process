package com.munchado.orderprocess.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.model.orderdetail.MyItemList;
import com.munchado.orderprocess.model.orderdetail.OrderAmountCalculation;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponse;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponseData;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.utils.PrintUtils;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

import java.util.ArrayList;

/**
 * Created by android on 23/2/17.
 */

public class OrderDetailFragment extends BaseFragment implements RequestCallback {

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
    private View progressBar;


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
        progressBar.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();
        String orderId = bundle.getString("ORDER_ID");
        RequestController.getOrderDetail(orderId, this);
    }

    private void initView(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
        textName = (TextView) view.findViewById(R.id.text_name);
        textEmail = (TextView) view.findViewById(R.id.text_email);
        textTelephone = (TextView) view.findViewById(R.id.text_telephone);
        textPastActivity = (TextView) view.findViewById(R.id.text_past_activity);

        textOrderId = (TextView)view.findViewById(R.id.text_order_id);
        textOrderType = (TextView)view.findViewById(R.id.text_order_type);
        textOrderTime = (TextView)view.findViewById(R.id.text_order_time);
        labelOrderTime = (TextView)view.findViewById(R.id.label_order_time);
        textDeliveryTime = (TextView)view.findViewById(R.id.text_delivery_time);
        labelDeliveryAddress = (TextView)view.findViewById(R.id.label_delivery_address);
        textDeliveryAddress = (TextView)view.findViewById(R.id.text_delivery_address);

        orderLayout = (LinearLayout) view.findViewById(R.id.order_layout);

        textSubtotal = (TextView) view.findViewById(R.id.text_subtotal);
        textDealDiscount = (TextView) view.findViewById(R.id.text_deal_discount);
        textDelivery = (TextView) view.findViewById(R.id.text_delivery);
        textTax = (TextView) view.findViewById(R.id.text_tax);
        textTip = (TextView) view.findViewById(R.id.text_tip);
        textTotal = (TextView) view.findViewById(R.id.text_total);
    }

    private void showOrderDetail(OrderDetailResponseData orderDetailData) {
        textOrderId.setText(orderDetailData.id);
        textOrderType.setText(orderDetailData.order_type);
        textOrderTime.setText(orderDetailData.order_date);
        if(orderDetailData.order_type.equalsIgnoreCase("takeout")) {
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
        progressBar.setVisibility(View.GONE);
    }
    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.ORDER_DETAIL;
    }
    @Override
    public void success(Object obj) {
        progressBar.setVisibility(View.GONE);
        if (obj instanceof OrderDetailResponse) {
            showDetail(((OrderDetailResponse) obj).data);
        }

        new PrintUtils().setPrintData(((OrderDetailResponse) obj).data);
    }

    private void showDetail(OrderDetailResponseData data) {
        textName.setText(data.customer_first_name + " " + data.customer_last_name);
        textTelephone.setText(data.my_delivery_detail.phone);
        textEmail.setText(data.email);
        textPastActivity.setText("No Past Activity");


        showOrderDetail(data);
        showOrderItem(data.item_list);
        showOrderPaymentDetail(data.order_amount_calculation);
    }

    private void showOrderItem(ArrayList<MyItemList> item_list) {
        orderLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(orderLayout.getContext());
        for(MyItemList itemList:item_list) {
            View view = inflater.inflate(R.layout.row_order_item, null);
            TextView itemName = (TextView) view.findViewById(R.id.text_item_name);
            TextView textInstruction = (TextView) view.findViewById(R.id.text_instruction);
            TextView itemCount = (TextView) view.findViewById(R.id.text_item_count);
            TextView itemPrice = (TextView) view.findViewById(R.id.text_item_price);

            itemName.setText(itemList.item_name);
            if(StringUtils.isNullOrEmpty(itemList.item_special_instruction)) {
                textInstruction.setVisibility(View.GONE);
            } else {
                textInstruction.setText(itemList.item_special_instruction);
            }
            itemCount.setText(itemList.item_qty);
            itemPrice.setText("$" + (Utils.parseDouble(itemList.unit_price)*Utils.parseDouble(itemList.item_qty)));
            orderLayout.addView(view);
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


}
