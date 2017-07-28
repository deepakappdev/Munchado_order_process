package com.munchado.orderprocess.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.munchado.orderprocess.listener.OnVerticalScrollListener;
import com.munchado.orderprocess.model.reservation.ArchiveReservationResponse;
import com.munchado.orderprocess.model.reservation.Archive_reservation;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.activity.HomeActivity;
import com.munchado.orderprocess.ui.adapter.ArchiveReservationAdapter;
import com.munchado.orderprocess.utils.DialogUtil;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArchiveReservationFragment extends BaseFragment implements View.OnClickListener, RequestCallback, OnArchiveReservationClickListener {

    private View rootView;
    RecyclerView recyclerView;
    private TextView textUpcomingBookingCount;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView tv_archive_order_count;
    private List<Archive_reservation> reservationsList;
    private ArchiveReservationAdapter adapter;
    private HomeActivity mHomeActivity;
    long lastApiHitTimeInMillies = 0L;
    long archivefragmentappearTimeInMillies = 0L;
    int page = 1;
    private boolean isMoreLoaded;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        archivefragmentappearTimeInMillies = System.currentTimeMillis();
//        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_reservation_archive_list, container, false);
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
//        if (adapter == null)
            fetchArchiveReservation();
    }

    private void fetchArchiveReservation() {
        DialogUtil.showProgressDialog(getActivity());
        RequestController.getArchiveReservation((BaseActivity) getActivity(), this, page);

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
        RequestController.getArchiveReservation((BaseActivity) getActivity(), this, ++page);

    }

    private void initView(View view) {

        view.findViewById(R.id.text_archive_reservation).setOnClickListener(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_reservation);
        tv_archive_order_count = (TextView) view.findViewById(R.id.text_reservation_count);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }



    @Override
    public FRAGMENTS getFragmentId() {
        return FRAGMENTS.RESERVATION_ARCHIVE;
    }




    @Override
    public void error(NetworkError volleyError) {
        DialogUtil.hideProgressDialog();
        if (volleyError != null && !StringUtils.isNullOrEmpty(volleyError.getLocalizedMessage()))
            if (volleyError.getLocalizedMessage().equalsIgnoreCase("Invalid token") || volleyError.getLocalizedMessage().equalsIgnoreCase("Credential not found")) {
                Utils.showLogin(getActivity());
            }
    }

    @Override
    public void success(Object obj) {
        DialogUtil.hideProgressDialog();
        if (obj instanceof ArchiveReservationResponse) {
            showArchiveList(((ArchiveReservationResponse) obj));
        }
    }

    private void showArchiveList(ArchiveReservationResponse data) {
        LogUtils.d("=========== showArchiveList ");
        if (adapter == null || recyclerView.getAdapter() == null) {
            adapter = new ArchiveReservationAdapter(mHomeActivity,  this);
            recyclerView.setAdapter(adapter);
            if(page ==1){
                adapter.setList(data.data.getArchive_reservation());
            }
//            int adapterSize = adapter.getItemCount();
//            ArrayList<Archive_reservation> archive_order = (ArrayList<Archive_reservation>) adapter.getAllItems();
//            archive_order.addAll(data.archive_order);
//            adapter.appendResult(archive_order);
//            if (adapterSize < adapter.getItemCount())
//                isMoreLoaded = true;
//            adapter.notifyDataSetChanged();
            tv_archive_order_count.setText(adapter.getItemCount() + " ARCHIVE ORDERS");

        }
    }

    @Override
    public void onReservationClick(Archive_reservation incomingReservations) {

    }

    @Override
    public void onReservationActionClick(Archive_reservation incomingReservations) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_archive_reservation:
//                Toast.makeText(mHomeActivity, "hii", Toast.LENGTH_SHORT).show();
                // TRICK TO AVOID NO FRAGMENT APPEAR WHEN USER CONTINUOUS CLICK ON BUTTON VERY FAST SPEED
                if ((System.currentTimeMillis() - archivefragmentappearTimeInMillies) / 1000 >= 1) {
                    archivefragmentappearTimeInMillies = System.currentTimeMillis();
                    ((BaseActivity) getActivity()).addFragment(FRAGMENTS.RESERVATION, null);
                }
                break;
        }
    }
}
