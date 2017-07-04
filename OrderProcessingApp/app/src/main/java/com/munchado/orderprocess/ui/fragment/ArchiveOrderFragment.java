package com.munchado.orderprocess.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.munchado.orderprocess.listener.OnVerticalScrollListener;
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
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

import java.util.ArrayList;

/**
 * Created by android on 22/2/17.
 */

public class ArchiveOrderFragment extends BaseFragment implements RequestCallback, View.OnClickListener {

    RecyclerView recyclerView;
    TextView tv_archive_order_count;
    ArchiveOrderAdapter adapter;
    private View rootView;
    int page = 1;
    private boolean isMoreLoaded;
    long archivefragmentappearTimeInMillies =0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        archivefragmentappearTimeInMillies = System.currentTimeMillis();
        if (rootView == null)
            rootView = inflater.inflate(R.layout.frag_archive_order, container, false);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        LogUtils.d("=========== onViewCreated ==== Archive");
        if (adapter == null)
            fetchArchiveOrder();
    }

    @Override
    public FRAGMENTS getFragmentId() {
        return FRAGMENTS.ARCHIVE;
    }

    private void fetchArchiveOrder() {
//        MyApplication.isApiHitting=true;
        DialogUtil.showProgressDialog(getActivity());
        RequestController.getArchiveOrder((BaseActivity) getActivity(), this, page);

        recyclerView.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledToBottom() {
                super.onScrolledToBottom();
                LogUtils.d("=========== on bottom  ");
                if (isMoreLoaded) {
                    isMoreLoaded = false;
                    loadMoreArchieveOrders();
                }
            }
        });
    }

    private void loadMoreArchieveOrders() {
        DialogUtil.showProgressDialog(getActivity());
        RequestController.getArchiveOrder((BaseActivity) getActivity(), this, ++page);
    }

    private void initView(View view) {
        view.findViewById(R.id.text_archive_order).setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        tv_archive_order_count = (TextView) view.findViewById(R.id.tv_archive_order_count);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.grey_border));
    }


    @Override
    public void error(NetworkError volleyError) {
        DialogUtil.hideProgressDialog();
        if (volleyError != null && !StringUtils.isNullOrEmpty(volleyError.getLocalizedMessage()))
            if (volleyError.getLocalizedMessage().equalsIgnoreCase("Invalid token") || volleyError.getLocalizedMessage().equalsIgnoreCase("Credential not found")) {
                Utils.showLogin(getActivity());
            }

//        MyApplication.isApiHitting=false;
    }

    @Override
    public void success(Object obj) {

        DialogUtil.hideProgressDialog();
        if (obj instanceof ArchiveOrderResponse) {
            showArchiveList(((ArchiveOrderResponse) obj).data);
        }
//        MyApplication.isApiHitting=false;
    }

    private void showArchiveList(ArchiveOrderResponseData data) {
        if (adapter == null || recyclerView.getAdapter() == null) {
            adapter = new ArchiveOrderAdapter(onOrderClickListener);
            recyclerView.setAdapter(adapter);
        }

        int adapterSize = adapter.getItemCount();
        ArrayList<OrderItem> archive_order = (ArrayList<OrderItem>) adapter.getAllItems();
        archive_order.addAll(data.archive_order);
        adapter.appendResult(archive_order);
        if (adapterSize < adapter.getItemCount())
            isMoreLoaded = true;
        adapter.notifyDataSetChanged();
        tv_archive_order_count.setText(data.total_archive_records + " ARCHIVE ORDERS");

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

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_archive_order:
                // TRICK TO AVOID NO FRAGMENT APPEAR WHEN USER CONTINUOUS CLICK ON BUTTON VERY FAST SPEED
                if ((System.currentTimeMillis() - archivefragmentappearTimeInMillies) / 1000 >= 1) {
                    archivefragmentappearTimeInMillies = System.currentTimeMillis();
                    getFragmentManager().popBackStack();
                }
                break;
        }
    }
}
