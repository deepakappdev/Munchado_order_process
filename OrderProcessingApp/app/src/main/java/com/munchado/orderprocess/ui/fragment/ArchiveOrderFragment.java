package com.munchado.orderprocess.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.listener.OnOrderClickListener;
import com.munchado.orderprocess.model.archiveorder.ArchiveOrderResponse;
import com.munchado.orderprocess.model.archiveorder.ArchiveOrderResponseData;
import com.munchado.orderprocess.model.archiveorder.OrderItem;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.activity.HomeActivity;
import com.munchado.orderprocess.ui.adapter.ArchiveOrderAdapter;
import com.munchado.orderprocess.utils.DialogUtil;

/**
 * Created by android on 22/2/17.
 */

public class ArchiveOrderFragment extends BaseFragment implements RequestCallback, View.OnClickListener {

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.frag_archive_order, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        fetchArchiveOrder();
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).setCustomTitle("Archive Orders");
    }
    private void fetchArchiveOrder() {
        DialogUtil.showProgressDialog(getActivity());
        RequestController.getArchiveOrder(this);
    }

    private void initView(View view) {
        view.findViewById(R.id.text_archive_order).setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void error(NetworkError volleyError) {
        DialogUtil.hideProgressDialog();
    }

    @Override
    public void success(Object obj) {
        DialogUtil.hideProgressDialog();
        if (obj instanceof ArchiveOrderResponse) {
            showArchiveList(((ArchiveOrderResponse) obj).data);
        }
    }

    private void showArchiveList(ArchiveOrderResponseData data) {
        ArchiveOrderAdapter adapter = new ArchiveOrderAdapter(onOrderClickListener);
        adapter.setResults(data.archive_order);
        recyclerView.setAdapter(adapter);
    }
    OnOrderClickListener onOrderClickListener = new OnOrderClickListener() {
        @Override
        public void onClickOrderItem(OrderItem orderItem) {
            Bundle bundle = new Bundle();
            bundle.putString("ORDER_ID", orderItem.id);
            ((BaseActivity)getActivity()).addFragment(FRAGMENTS.ORDER_DETAIL, bundle);
        }

        @Override
        public void onClickOrderAction(OrderItem orderItem) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_archive_order:
                getFragmentManager().popBackStack();
                break;
        }
    }
}
