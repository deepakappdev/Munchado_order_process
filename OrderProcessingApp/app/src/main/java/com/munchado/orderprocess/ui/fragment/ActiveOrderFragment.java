package com.munchado.orderprocess.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.listener.OnOrderClickListener;
import com.munchado.orderprocess.model.archiveorder.ActiveOrderResponse;
import com.munchado.orderprocess.model.archiveorder.ActiveOrderResponseData;
import com.munchado.orderprocess.model.archiveorder.OrderItem;
import com.munchado.orderprocess.model.orderprocess.OrderProcessResponse;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.adapter.ActiveOrderAdapter;
import com.munchado.orderprocess.utils.DividerItemDecoration;

import java.util.List;

/**
 * Created by android on 22/2/17.
 */

public class ActiveOrderFragment extends BaseFragment implements RequestCallback, View.OnClickListener {

    RecyclerView recyclerView;
    private TextView textActiveOrderCount;
    private View rootView;
    private ActiveOrderAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null)
            rootView = inflater.inflate(R.layout.frag_active_order, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        fetchActiveOrder();
    }

    public void fetchActiveOrder() {
        RequestController.getActiveOrder(this);
    }


    private void initView(View view) {
        textActiveOrderCount = (TextView) view.findViewById(R.id.text_active_order_count);
        view.findViewById(R.id.text_archive_order).setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.horizontal_line));
    }


    @Override
    public void error(NetworkError volleyError) {

    }

    @Override
    public void success(Object obj) {
        if (getActivity() == null) return;
        if (obj instanceof ActiveOrderResponse) {
            updateActiveList(((ActiveOrderResponse) obj).data);
            cleanRemainingItem(((ActiveOrderResponse) obj).data);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchActiveOrder();
                }
            }, 30000);
        } else if (obj instanceof OrderProcessResponse) {
            if (((OrderProcessResponse) obj).data.message) {
                if(((OrderProcessResponse) obj).data.status.equalsIgnoreCase("confirmed"))
                    moveToConfirmed(((OrderProcessResponse) obj).data.order_id);
                else
                    moveToArchive(((OrderProcessResponse) obj).data.order_id);

                if(((OrderProcessResponse) obj).data.status.equalsIgnoreCase("delivered"))
                showToast("Order Successfully Sent.");
                else if(((OrderProcessResponse) obj).data.status.equalsIgnoreCase("arrived"))
                    showToast("Order Successfully Pickedup.");
                else
                showToast("Order Successfully " + ((OrderProcessResponse) obj).data.status);
            }
        }
    }

    private void cleanRemainingItem(ActiveOrderResponseData data) {
        List<OrderItem> allItems = adapter.getAllItems();
        for(int index = 0;index<allItems.size();index++) {
            OrderItem orderItem = allItems.get(index);
            boolean found = false;
            for(OrderItem updatedItem:data.live_order) {
                if(updatedItem.id.equalsIgnoreCase(orderItem.id)) {
                    found = true;
                    break;
                }
            }
            if(!found)
                moveToArchive(orderItem.id);
        }
        textActiveOrderCount.setText(adapter.getItemCount() + " Active Orders");
    }

    private void moveToArchive(String orderId) {
        adapter.removeOrder(orderId);
        textActiveOrderCount.setText(adapter.getItemCount() + " Active Orders");

    }
    private void moveToConfirmed(String orderId) {
        adapter.confirmOrder(orderId);
    }


    private void updateActiveList(ActiveOrderResponseData data) {
        if (adapter == null || recyclerView.getAdapter() != adapter) {
            adapter = new ActiveOrderAdapter(onOrderClickListener);
            recyclerView.setAdapter(adapter);
        }
//        data.live_order.subList(20, data.live_order.size()).clear();
        adapter.updateResult(data.live_order);
        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    OnOrderClickListener onOrderClickListener = new OnOrderClickListener() {
        @Override
        public void onClickOrderItem(OrderItem orderItem) {
            Bundle bundle = new Bundle();
            bundle.putString("ORDER_ID", orderItem.id);
            ((BaseActivity) getActivity()).addFragment(FRAGMENTS.ORDER_DETAIL, bundle);
        }

        @Override
        public void onClickOrderAction(OrderItem orderItem) {
            String status = "";
            if (orderItem.status.equalsIgnoreCase("placed"))
                status = "confirmed";
            else if (orderItem.status.equalsIgnoreCase("confirmed")) {
                if (orderItem.order_type.equalsIgnoreCase("takeout"))
                    status = "arrived";
                else
                    status = "delivered";
            }
            RequestController.orderProcess(orderItem.id, status, "","", ActiveOrderFragment.this);
            orderItem.inProgress = true;
            adapter.updateResult(orderItem);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_archive_order:
                ((BaseActivity) getActivity()).addFragment(FRAGMENTS.ARCHIVE, null);
                break;
        }
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "=============Got message: " + message);
            fetchActiveOrder();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("com.munchado.orderprocess.notification.refresh"));
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister since the activity is not visible
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.ACTIVE;
    }
}
