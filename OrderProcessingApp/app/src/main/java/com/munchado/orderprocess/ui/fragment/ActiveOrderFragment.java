package com.munchado.orderprocess.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.munchado.orderprocess.model.login.StatusResponse;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.adapter.ActiveOrderAdapter;

/**
 * Created by android on 22/2/17.
 */

public class ActiveOrderFragment extends BaseFragment implements RequestCallback, View.OnClickListener {

    RecyclerView recyclerView;
    private TextView textActiveOrderCount;
    private View rootView;
    private ActiveOrderAdapter adapter;

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

    private void fetchActiveOrder() {
        RequestController.getActiveOrder(this);
    }



    private void initView(View view) {
        textActiveOrderCount = (TextView) view.findViewById(R.id.text_active_order_count);
        view.findViewById(R.id.text_archive_order).setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void error(NetworkError volleyError) {

    }

    @Override
    public void success(Object obj) {
        if(getActivity()==null)return;
        if (obj instanceof ActiveOrderResponse) {
            updateActiveList(((ActiveOrderResponse) obj).data);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchActiveOrder();
                }
            }, 30000);
        } else if (obj instanceof StatusResponse) {
            if (((StatusResponse) obj).data.message) {
                moveToArchive(orderItem);
            }
        }
    }

    private void moveToArchive(OrderItem orderItem) {
        adapter.removeOrder(orderItem);
    }

    private void updateActiveList(ActiveOrderResponseData data) {
        textActiveOrderCount.setText(data.total_live_records + " ACTIVE ORDERS");
        if(adapter==null || recyclerView.getAdapter()!=adapter) {
            adapter = new ActiveOrderAdapter(onOrderClickListener);
            recyclerView.setAdapter(adapter);
        }
        data.live_order.subList(20, data.live_order.size()).clear();
        adapter.updateResult(data.live_order);


    }

    private OrderItem orderItem;
    OnOrderClickListener onOrderClickListener = new OnOrderClickListener() {
        @Override
        public void onClickOrderItem(OrderItem orderItem) {
            Bundle bundle = new Bundle();
            bundle.putString("ORDER_ID", orderItem.id);
            ((BaseActivity) getActivity()).addFragment(FRAGMENTS.ORDER_DETAIL, bundle);


        }

        @Override
        public void onClickOrderAction(OrderItem orderItem) {
            ActiveOrderFragment.this.orderItem = orderItem;
            RequestController.orderProcess(orderItem.id, ActiveOrderFragment.this);
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

    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.ACTIVE;
    }
}
