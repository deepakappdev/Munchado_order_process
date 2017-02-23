package com.munchado.orderprocess.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponse;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponseData;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;

/**
 * Created by android on 23/2/17.
 */

public class OrderDetailFragment extends BaseFragment implements RequestCallback {

    private TextView textName;
    private TextView textEmail;
    private TextView textTelephone;
    private TextView textPastActivity;


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
        Bundle bundle = getArguments();
        String orderId = bundle.getString("ORDER_ID");
        RequestController.getOrderDetail(orderId, this);
    }

    private void initView(View view) {
        textName = (TextView) view.findViewById(R.id.text_name);
        textEmail = (TextView) view.findViewById(R.id.text_email);
        textTelephone = (TextView) view.findViewById(R.id.text_telephone);
        textPastActivity = (TextView) view.findViewById(R.id.text_past_activity);
    }

    @Override
    public void error(NetworkError volleyError) {

    }

    @Override
    public void success(Object obj) {
        if (obj instanceof OrderDetailResponse) {
            showDetail(((OrderDetailResponse) obj).data);
        }
    }

    private void showDetail(OrderDetailResponseData data) {
        textName.setText(data.customer_first_name + " " + data.customer_last_name);
//        textEmail.setText(data.);
    }
}
