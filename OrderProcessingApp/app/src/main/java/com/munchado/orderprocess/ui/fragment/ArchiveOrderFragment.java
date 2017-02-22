package com.munchado.orderprocess.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkError;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.archiveorder.ArchiveOrderResponse;
import com.munchado.orderprocess.model.archiveorder.ArchiveOrderResponseData;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.RequestCallback;

/**
 * Created by android on 22/2/17.
 */

public class ArchiveOrderFragment extends BaseFragment implements RequestCallback {
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

    private void fetchArchiveOrder() {
        RequestController.getArchiveOrder(this);
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void error(NetworkError networkError) {

    }

    @Override
    public void success(Object obj) {
        if (obj instanceof ArchiveOrderResponse) {
            showArchiveList(((ArchiveOrderResponse) obj).data);
        }
    }

    private void showArchiveList(ArchiveOrderResponseData data) {

    }
}
