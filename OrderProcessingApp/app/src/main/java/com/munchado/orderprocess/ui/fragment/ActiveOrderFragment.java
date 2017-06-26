package com.munchado.orderprocess.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.munchado.orderprocess.ui.activity.HomeActivity;
import com.munchado.orderprocess.ui.adapter.ActiveOrderAdapter;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by android on 22/2/17.
 */

public class ActiveOrderFragment extends BaseFragment implements RequestCallback, View.OnClickListener {

    RecyclerView recyclerView;
    private TextView textActiveOrderCount;
    private View rootView;
    private ActiveOrderAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;
    private String sent_status = "";
    ArrayList<OrderItem> live_orderList;
    boolean isFirst;
    long lastApiHitTimeInMillies = 0L;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null)
            rootView = inflater.inflate(R.layout.frag_active_order, container, false);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_more, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_menu_more) {
            ((HomeActivity) getActivity()).showDownloadDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_more, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_menu_more) {
//            showDownloadDialog();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        live_orderList = new ArrayList<>();
        fetchActiveOrder();
    }

    public void fetchActiveOrder() {
        if ((System.currentTimeMillis() - lastApiHitTimeInMillies) / 1000 >= 30)
            RequestController.getActiveOrder((BaseActivity) getActivity(), this);
    }


    private void initView(View view) {
        textActiveOrderCount = (TextView) view.findViewById(R.id.text_active_order_count);
        view.findViewById(R.id.text_archive_order).setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        textActiveOrderCount.setText("ACTIVE ORDERS");
    }


    @Override
    public void error(NetworkError volleyError) {
        if (volleyError != null && !StringUtils.isNullOrEmpty(volleyError.getLocalizedMessage()))
            if (volleyError.getLocalizedMessage().equalsIgnoreCase("Invalid token") || volleyError.getLocalizedMessage().equalsIgnoreCase("Credential not found")) {
                Utils.showLogin(getActivity());
            }
    }

    @Override
    public void success(Object obj) {
        if (getActivity() == null) return;
        if (obj instanceof ActiveOrderResponse) {
            lastApiHitTimeInMillies = System.currentTimeMillis();
            live_orderList.clear();
            live_orderList.addAll(((ActiveOrderResponse) obj).data.live_order);

            updateActiveList(((ActiveOrderResponse) obj).data);
            cleanRemainingItem(((ActiveOrderResponse) obj).data);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchActiveOrder();
                }
            }, 30000);
            if (!isFirst) {
                isFirst = true;
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        playRing();
                    }
                }, 0, 5000);
            }
        } else if (obj instanceof OrderProcessResponse) {
            if (((OrderProcessResponse) obj).data.message) {
                if (((OrderProcessResponse) obj).data.status.equalsIgnoreCase("confirmed"))
                    moveToConfirmed(((OrderProcessResponse) obj).data.order_id, "confirmed");
                else if (sent_status.equalsIgnoreCase("ready") && ((OrderProcessResponse) obj).data.status.equalsIgnoreCase("arrived"))

                    moveToConfirmed(((OrderProcessResponse) obj).data.order_id, "arrived");
                else
                    moveToArchive(((OrderProcessResponse) obj).data.order_id);

                if (((OrderProcessResponse) obj).data.status.equalsIgnoreCase("delivered"))
                    showToast("Order Successfully Sent.");
                else if (sent_status.equalsIgnoreCase("ready") && ((OrderProcessResponse) obj).data.status.equalsIgnoreCase("arrived"))//"arrived"
                    showToast("Order is ready for Pick Up.");
//                else if (((OrderProcessResponse) obj).data.status.equalsIgnoreCase("archived"))//"arrived"
//                    showToast("Order Successfully Pickedup.");
                else
                    showToast("Order Successfully " + ((OrderProcessResponse) obj).data.status);

                for (int i = 0; i < live_orderList.size(); i++) {
                    if (live_orderList.get(i).id.equalsIgnoreCase(((OrderProcessResponse) obj).data.order_id)) {
                        OrderItem item = live_orderList.get(i);
                        item.status = ((OrderProcessResponse) obj).data.status;
                        live_orderList.set(i, item);
                    }
                }
            }
        }
    }

    private void playRing() {
        try {
            if (live_orderList != null && live_orderList.size() > 0)
                for (OrderItem item : live_orderList) {
//                LogUtils.d("========= " + item.status);
                    if (item.status.equalsIgnoreCase("placed")) {

                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
                        r.play();
                        break;


                    }
                }
        } catch (Exception e) {
        }
    }

    private void cleanRemainingItem(ActiveOrderResponseData data) {
        List<OrderItem> allItems = adapter.getAllItems();
        for (int index = 0; index < allItems.size(); index++) {
            OrderItem orderItem = allItems.get(index);
            boolean found = false;
            for (OrderItem updatedItem : data.live_order) {
                if (updatedItem.id.equalsIgnoreCase(orderItem.id)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                moveToArchive(orderItem.id);
        }
        textActiveOrderCount.setText(adapter.getItemCount() + " ACTIVE ORDERS");
    }

    private void moveToArchive(String orderId) {
        adapter.removeOrder(orderId);
        textActiveOrderCount.setText(adapter.getItemCount() + "  ACTIVE ORDERS");

    }

    private void moveToConfirmed(String orderId, String status) {
        adapter.confirmOrder(orderId, status);
    }


    private void updateActiveList(ActiveOrderResponseData data) {
//        if (adapter == null || recyclerView.getAdapter() != adapter) {
        adapter = new ActiveOrderAdapter(getActivity(), onOrderClickListener);
        recyclerView.setAdapter(adapter);

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
            String status = "archived";
            if (orderItem.status.equalsIgnoreCase("placed"))
                status = "confirmed";
            else if (orderItem.status.equalsIgnoreCase("confirmed")) {
                if (orderItem.order_type.equalsIgnoreCase("takeout"))
                    status = "ready";//"arrived";
                else
                    status = "delivered";
            } else if (sent_status.equalsIgnoreCase("ready") && orderItem.order_type.equalsIgnoreCase("archived"))
                status = "archived";
            sent_status = status;
            RequestController.orderProcess(orderItem.id, status, "", "", ActiveOrderFragment.this);
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
//            Log.d("receiver", "=============Got message: " + message);
            fetchActiveOrder();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("com.munchado.orderprocess.notification.refresh"));
        try {
            if (adapter != null && adapter.getAllItems() != null && adapter.getAllItems().size() > 0) {
                LogUtils.d("========= Order Id : " + ((BaseActivity) getActivity()).order_ID + "=======" + ((BaseActivity) getActivity()).order_Status + "=======" + adapter.getAllItems().size());
                if (!StringUtils.isNullOrEmpty(((BaseActivity) getActivity()).order_ID) && !StringUtils.isNullOrEmpty(((BaseActivity) getActivity()).order_Status)) {
                    for (int i = 0; i < adapter.getAllItems().size(); i++) {
                        if (adapter.getAllItems().get(i).id.equalsIgnoreCase(((BaseActivity) getActivity()).order_ID)) {
                            LogUtils.d("========= status: " + adapter.getAllItems().get(i).status + "=======" + ((BaseActivity) getActivity()).order_Status);
                            if (!adapter.getAllItems().get(i).status.equalsIgnoreCase(((BaseActivity) getActivity()).order_Status)) {
                                LogUtils.d("========= status: " + adapter.getAllItems().get(i).status + "22222" + ((BaseActivity) getActivity()).order_Status);
                                OrderItem item = adapter.getAllItems().get(i);
                                item.status = ((BaseActivity) getActivity()).order_Status;
                                adapter.getAllItems().set(i, item);

                                if (((BaseActivity) getActivity()).order_Status.equalsIgnoreCase("archived") || ((BaseActivity) getActivity()).order_Status.equalsIgnoreCase("cancelled"))
                                    adapter.updateResult(item);
                                else
                                    adapter.updateStatusResult(item);

                                /*    if (!((BaseActivity) getActivity()).order_Status.equalsIgnoreCase("archived"))
                                    adapter.updateStatusResult(item);
                                else
                                    adapter.updateResult(item);*/
                            }
                            break;
                        }
                    }
                    textActiveOrderCount.setText(adapter.getItemCount() + " ACTIVE ORDERS");
                }

                ((BaseActivity) getActivity()).order_ID = "";
                ((BaseActivity) getActivity()).order_Status = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister since the activity is not visible
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public FRAGMENTS getFragmentId() {
        return FRAGMENTS.ACTIVE;
    }
}
