package com.munchado.orderprocess.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.listener.OnReservationClickListener;
import com.munchado.orderprocess.model.reservation.UpcomingReservationModelResponse;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.activity.HomeActivity;
import com.munchado.orderprocess.ui.adapter.ArchiveReservationAdapter;
import com.munchado.orderprocess.utils.DialogUtil;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArchiveReservationFragment extends BaseFragment implements View.OnClickListener, RequestCallback, OnReservationClickListener {

    private View rootView;
    RecyclerView recyclerView;
    private TextView textUpcomingBookingCount;
    private LinearLayoutManager mLinearLayoutManager;
    private List<UpcomingReservationModelResponse.Incoming_reservations> reservationsList;
    private ArchiveReservationAdapter mArchiveReservationAdapter;
    private HomeActivity mHomeActivity;
    long lastApiHitTimeInMillies = 0L;
    long activefragmentappearTimeInMillies = 0L;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHomeActivity = (HomeActivity) context;
    }


    public ArchiveReservationFragment() {
        // Required empty public constructor
      //  Toast.makeText(getActivity(),"HIII",Toast.LENGTH_SHORT).show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activefragmentappearTimeInMillies = System.currentTimeMillis();
        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_reservation_archive_list, container, false);
        return rootView;



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        fetchBookingList(true);
    }

    private void initView() {
        textUpcomingBookingCount = (TextView) rootView.findViewById(R.id.text_reservation_count);
        rootView.findViewById(R.id.text_archive_reservation).setOnClickListener(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_reservation);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        reservationsList = new ArrayList<>();
        textUpcomingBookingCount.setText(reservationsList.size() + " NEW RESERVATIONS");
        mArchiveReservationAdapter = new ArchiveReservationAdapter(mHomeActivity, this);
        recyclerView.setAdapter(mArchiveReservationAdapter);
    }
    private void fetchBookingList(boolean is_dialog_shown) {
        if ((System.currentTimeMillis() - lastApiHitTimeInMillies) / 1000 >= 30) {
//            if (is_dialog_shown)
//                RequestController.getUpcomingArchiveReservation(this);
        }
    }


    @Override
    public FRAGMENTS getFragmentId() {
        return FRAGMENTS.RESERVATION_ARCHIVE;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_archive_reservation:
//                Toast.makeText(mHomeActivity, "hii", Toast.LENGTH_SHORT).show();
                // TRICK TO AVOID NO FRAGMENT APPEAR WHEN USER CONTINUOUS CLICK ON BUTTON VERY FAST SPEED
                if ((System.currentTimeMillis() - activefragmentappearTimeInMillies) / 1000 >= 1) {
                    activefragmentappearTimeInMillies = System.currentTimeMillis();
                    ((BaseActivity) getActivity()).addFragment(FRAGMENTS.RESERVATION, null);
                }
                break;
        }
    }

    @Override
    public void onReservationClick(UpcomingReservationModelResponse.Incoming_reservations incomingReservations) {

    }

    @Override
    public void onReservationActionClick(UpcomingReservationModelResponse.Incoming_reservations incomingReservations) {

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

    }
}
